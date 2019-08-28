package rahnema.tumaj.bid.backend.controllers;

import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import rahnema.tumaj.bid.backend.domains.Messages.AuctionInputMessage;
import rahnema.tumaj.bid.backend.domains.Messages.AuctionOutputMessage;
import rahnema.tumaj.bid.backend.domains.Messages.HomeOutputMessage;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.services.auction.AuctionService;
import rahnema.tumaj.bid.backend.utils.AuctionsBidStorage;
import rahnema.tumaj.bid.backend.utils.DisconnectHandler;
import rahnema.tumaj.bid.backend.utils.SubscribeHandler;
import rahnema.tumaj.bid.backend.utils.exceptions.NotAllowedToLeaveAuctionException;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class EnterExitAuctionController {
    private static final Logger logger = LoggerFactory.getLogger(EnterExitAuctionController.class);


    @Autowired
    private Scheduler scheduler;

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final AuctionService service;
    private final AuctionsBidStorage bidStorage;
    private final DisconnectHandler disconnectHandler;
    private final SubscribeHandler subscribeHandler;

    public EnterExitAuctionController(SimpMessagingTemplate simpMessagingTemplate, AuctionService service, AuctionsBidStorage bidStorage, DisconnectHandler disconnectHandler, SubscribeHandler subscribeHandler) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.service = service;
        this.bidStorage = bidStorage;
        this.disconnectHandler = disconnectHandler;
        this.subscribeHandler = subscribeHandler;
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
//    public synchronized void sendMessage(AuctionInputMessage inputMessage, /*("Authorization")*/ Message<?> message) {

//        StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//        System.out.println("message = " + headerAccessor);
//        System.out.println("headerAccessor.getUser().getName() = " + headerAccessor.getUser().getName());


    public synchronized void sendMessage(AuctionInputMessage inputMessage, /*("Authorization")*/ @Headers Map headers) {
        ConcurrentMap<Long, Auction> auctionsData = bidStorage.getAuctionsData();
        ConcurrentMap<String, Long> usersData = bidStorage.getUsersData();
        UsernamePasswordAuthenticationToken user = (UsernamePasswordAuthenticationToken) headers.get("simpUser");
        Long auctionId = Long.valueOf(inputMessage.getAuctionId());
        Auction currentAuction = service.getAuction(auctionId, bidStorage);
        if (currentAuction.isFinished()) {
            AuctionOutputMessage message = new AuctionOutputMessage();
            message.setFinished(currentAuction.isFinished());
            message.setLastBid(currentAuction.getLastBid());
            message.setDescription("you can not enter the auction,auction is closed");
            message.setMessageType("EnterAuctionForbidden");
            this.simpMessagingTemplate.convertAndSendToUser(user.getName(), "/auction/" + auctionId, message);
            return;
        }
        if (usersData.containsKey(user.getName())) {
            if (usersData.get(user.getName()).equals(auctionId)) {
                AuctionOutputMessage message = new AuctionOutputMessage();
                message.setDescription("you can't enter the same auction with two devices");
                message.setMessageType("AlreadyInTheAuction");
                this.simpMessagingTemplate.convertAndSendToUser(user.getName(), "/auction/" + auctionId, message);
                return;
            }
        }
        if (currentAuction.getActiveBiddersLimit() > auctionsData.get(auctionId).getCurrentlyActiveBidders()) {
            currentAuction.setCurrentlyActiveBidders(currentAuction.getCurrentlyActiveBidders() + 1);
            auctionsData.put(auctionId, currentAuction);
            usersData.put(user.getName(), auctionId);
            AuctionOutputMessage message = new AuctionOutputMessage();
            message.setActiveBidders(auctionsData.get(auctionId).getCurrentlyActiveBidders());
            message.setMessageType("UpdateActiveBiddersNumber");
            message.setRemainingTime(calculateRemainingTime(auctionId));
            this.simpMessagingTemplate.convertAndSend("/auction/" + auctionId, message);
            sendMessageToHome(auctionId, currentAuction);
        } else {
            AuctionOutputMessage message = new AuctionOutputMessage();
            message.setDescription("you can't enter the auction,auction is full");
            message.setMessageType("AuctionIsFull");
//                throw new FullAuctionException();
        }
    }

    private long calculateRemainingTime(Long auctionId) {
        if (bidStorage.getTriggers().get(auctionId) != null) {
            return (  bidStorage.getTriggers().get(auctionId).getStartTime().getTime() - new Date().getTime() ) / 1000 ;
        }
        else return -1;
    }

    @MessageMapping("/exit")

    public synchronized void exit(AuctionInputMessage auctionInputMessage, @Headers Map headers) {
        ConcurrentMap<Long, Auction> auctionsData = bidStorage.getAuctionsData();
        ConcurrentMap<String, Long> usersData = bidStorage.getUsersData();
        UsernamePasswordAuthenticationToken user = (UsernamePasswordAuthenticationToken) headers.get("simpUser");
        Long auctionId = Long.valueOf(auctionInputMessage.getAuctionId());
        Auction currentAuction = service.getAuction(auctionId, bidStorage);

//        if (currentAuction.isFinished()) {
//            AuctionOutputMessage message=new AuctionOutputMessage();
//            message.setFinished(currentAuction.isFinished());
//            message.setLastBidder(currentAuction.getLastBidder());
//            message.setLastBid(currentAuction.getLastBid());
//            message.setMessageType("");
//            this.simpMessagingTemplate.convertAndSend("/auction/" + auctionId, message);
//            return;
//        }
        if (!usersData.containsKey(user.getName())) {
            if (usersData.get(user.getName()) != auctionId) {
                AuctionOutputMessage message = new AuctionOutputMessage();
                message.setDescription("you can't exit the auction that you aren't in");
                message.setMessageType("CantEnterAuction");
                this.simpMessagingTemplate.convertAndSendToUser(user.getName(), "/auction/" + auctionId, message);
                return;
            }
        }
        if (!currentAuction.getLastBidder().equals(user.getName()) || currentAuction.isFinished()) {
            currentAuction.setCurrentlyActiveBidders(currentAuction.getCurrentlyActiveBidders() - 1);
            auctionsData.put(auctionId, currentAuction);
            usersData.remove(user.getName());
            AuctionOutputMessage message = new AuctionOutputMessage();
            message.setActiveBidders(auctionsData.get(auctionId).getCurrentlyActiveBidders());
            message.setMessageType("UpdateActiveBiddersNumber");
            this.simpMessagingTemplate.convertAndSend("/auction/" + auctionId, message);
            sendMessageToHome(auctionId, currentAuction);
        } else if (currentAuction.getLastBidder().equals(user.getName())) {
            AuctionOutputMessage message = new AuctionOutputMessage();
            message.setDescription("You can not exit the auction now, you are the last bidder");
            message.setMessageType("ExitAuctionForbidden");
            this.simpMessagingTemplate.convertAndSendToUser(user.getName(), "/auction/" + auctionId, message);
            throw new NotAllowedToLeaveAuctionException();
        }
    }

    private void sendMessageToHome(Long auctionId, Auction currentAuction) {
        HomeOutputMessage homeOutputMessage = new HomeOutputMessage();
        homeOutputMessage.setActiveBidders(currentAuction.getCurrentlyActiveBidders());
        homeOutputMessage.setIsFinished(currentAuction.isFinished());
        this.simpMessagingTemplate.convertAndSend("/home/auctions/" + auctionId, homeOutputMessage);
    }


}
