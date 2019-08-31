package rahnema.tumaj.bid.backend.controllers;

import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import rahnema.tumaj.bid.backend.domains.Messages.*;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.services.auction.AuctionService;
import rahnema.tumaj.bid.backend.utils.AuctionsBidStorage;
import rahnema.tumaj.bid.backend.utils.DisconnectHandler;
import rahnema.tumaj.bid.backend.utils.SubscribeHandler;
import rahnema.tumaj.bid.backend.utils.assemblers.MessageAssembler;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class EnterExitAuctionController {
    private static final Logger logger = LoggerFactory.getLogger(EnterExitAuctionController.class);


    private final SimpMessagingTemplate simpMessagingTemplate;
    private final AuctionService service;
    private final AuctionsBidStorage bidStorage;
    private final DisconnectHandler disconnectHandler;
    private final SubscribeHandler subscribeHandler;
    private final MessageAssembler messageAssembler;

    public EnterExitAuctionController(SimpMessagingTemplate simpMessagingTemplate, AuctionService service, AuctionsBidStorage bidStorage, DisconnectHandler disconnectHandler, SubscribeHandler subscribeHandler, MessageAssembler messageAssembler) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.service = service;
        this.bidStorage = bidStorage;
        this.disconnectHandler = disconnectHandler;
        this.subscribeHandler = subscribeHandler;
        this.messageAssembler = messageAssembler;
    }

    @EventListener
    public void onDisconnectEvent(SessionDisconnectEvent event) {
        disconnectHandler.invoke(event);
    }

    @EventListener
    public void onSubscribeEvent(SessionSubscribeEvent event) {
        subscribeHandler.invoke(event);

    }

    @MessageMapping("/enter")
    public synchronized void sendMessage(AuctionInputMessage inputMessage, @Headers Map headers) {
        extractAuctionDetails(inputMessage, headers, "enter");
    }

    @MessageMapping("/exit")
    public synchronized void exit(AuctionInputMessage auctionInputMessage, @Headers Map headers) {
        extractAuctionDetails(auctionInputMessage, headers, "exit");
    }

    private void extractAuctionDetails(AuctionInputMessage inputMessage, @Headers Map headers, String requestType) {
        ConcurrentMap<Long, Auction> auctionsData = bidStorage.getAuctionsData();
        ConcurrentMap<String, Long> usersData = bidStorage.getUsersData();
        UsernamePasswordAuthenticationToken user = (UsernamePasswordAuthenticationToken) headers.get("simpUser");
        Long auctionId = Long.valueOf(inputMessage.getAuctionId());
        Auction currentAuction = service.getAuction(auctionId, bidStorage);
        evaluateRequestType(requestType, auctionsData, usersData, user, auctionId, currentAuction);
    }

    private void evaluateRequestType(String requestType, ConcurrentMap<Long, Auction> auctionsData, ConcurrentMap<String, Long> usersData, UsernamePasswordAuthenticationToken user, Long auctionId, Auction currentAuction) {
        if (requestType.equals("enter"))
            handleEnterRequestMessage(auctionsData, usersData, user, auctionId, currentAuction);
        else if (requestType.equals("exit"))
            handleExitRequestMessage(auctionsData, usersData, user, auctionId, currentAuction);
    }

    private void handleEnterRequestMessage(ConcurrentMap<Long, Auction> auctionsData, ConcurrentMap<String, Long> usersData, UsernamePasswordAuthenticationToken user, Long auctionId, Auction currentAuction) {
        if (currentAuction.isFinished()) {
            AuctionOutputMessage message = messageAssembler.getFinishedMessage(currentAuction);
            this.simpMessagingTemplate.convertAndSendToUser(user.getName(), getAuctionDestination(auctionId), message);
        } else if (usersData.containsKey(user.getName()) && usersData.get(user.getName()).equals(currentAuction.getId())) {
            AuctionOutputMessage message = messageAssembler.getAlreadyInMessage();
            this.simpMessagingTemplate.convertAndSendToUser(user.getName(), getAuctionDestination(auctionId), message);
        } else if (currentAuction.getActiveBiddersLimit() > auctionsData.get(currentAuction.getId()).getCurrentlyActiveBidders()) {
            updateAuctionOnEnter(auctionsData, usersData, user, currentAuction);
            AuctionOutputMessage message = messageAssembler.getUpdateMessage(auctionsData, currentAuction, bidStorage.getTriggers());
            this.simpMessagingTemplate.convertAndSend(getAuctionDestination(auctionId), message);
            sendMessageToHome(currentAuction.getId(), currentAuction);
        } else {
            AuctionOutputMessage message = messageAssembler.getFullMessage();
            this.simpMessagingTemplate.convertAndSendToUser(user.getName(), getAuctionDestination(auctionId), message);
        }
    }


    private void handleExitRequestMessage(ConcurrentMap<Long, Auction> auctionsData, ConcurrentMap<String, Long> usersData, UsernamePasswordAuthenticationToken user, Long auctionId, Auction currentAuction) {
        if (isUserAlreadyIn(usersData, user, auctionId)) {
            AuctionOutputMessage message = messageAssembler.getNotInMessage();
            this.simpMessagingTemplate.convertAndSendToUser(user.getName(), getAuctionDestination(auctionId), message);
        } else if (isExitOk(user, currentAuction)) {
            updateAuctionOnExit(auctionsData, usersData, user, auctionId, currentAuction);
            AuctionOutputMessage message = messageAssembler.getUpdateOnExitMessage(auctionsData, auctionId);
            this.simpMessagingTemplate.convertAndSend("/auction/" + auctionId, message);
            sendMessageToHome(auctionId, currentAuction);
        } else if (isUserLastBidder(user, currentAuction)) {
            AuctionOutputMessage message = messageAssembler.getLastBidderMessage();
            this.simpMessagingTemplate.convertAndSendToUser(user.getName(), getAuctionDestination(auctionId), message);
        }
    }

    private boolean isUserLastBidder(UsernamePasswordAuthenticationToken user, Auction currentAuction) {
        return currentAuction.getLastBidder().equals(user.getName());
    }

    private boolean isExitOk(UsernamePasswordAuthenticationToken user, Auction currentAuction) {
        return !currentAuction.getLastBidder().equals(user.getName()) || currentAuction.isFinished();
    }

    private boolean isUserAlreadyIn(ConcurrentMap<String, Long> usersData, UsernamePasswordAuthenticationToken user, Long auctionId) {
        return !usersData.containsKey(user.getName()) && usersData.get(user.getName()).equals(auctionId);
    }


    private void updateAuctionOnExit(ConcurrentMap<Long, Auction> auctionsData, ConcurrentMap<String, Long> usersData, UsernamePasswordAuthenticationToken user, Long auctionId, Auction currentAuction) {
        currentAuction.setCurrentlyActiveBidders(currentAuction.getCurrentlyActiveBidders() - 1);
        auctionsData.put(auctionId, currentAuction);
        usersData.remove(user.getName());
    }

    private void updateAuctionOnEnter(ConcurrentMap<Long, Auction> auctionsData, ConcurrentMap<String, Long> usersData, UsernamePasswordAuthenticationToken user, Auction currentAuction) {
        currentAuction.setCurrentlyActiveBidders(currentAuction.getCurrentlyActiveBidders() + 1);
        auctionsData.put(currentAuction.getId(), currentAuction);
        usersData.put(user.getName(), currentAuction.getId());
    }

    private void sendMessageToHome(Long auctionId, Auction currentAuction) {
        HomeOutputMessage homeOutputMessage = new HomeOutputMessage();
        homeOutputMessage.setActiveBidders(currentAuction.getCurrentlyActiveBidders());
        homeOutputMessage.setIsFinished(currentAuction.isFinished());
        this.simpMessagingTemplate.convertAndSend("/home/auctions/" + auctionId, homeOutputMessage);
    }

    private String getAuctionDestination(Long Id) {
        return "/auction/" + Id;
    }


}
