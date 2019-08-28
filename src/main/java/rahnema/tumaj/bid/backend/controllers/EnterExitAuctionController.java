package rahnema.tumaj.bid.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import rahnema.tumaj.bid.backend.domains.Messages.AuctionInputMessage;
import rahnema.tumaj.bid.backend.domains.Messages.AuctionOutputMessage;
import rahnema.tumaj.bid.backend.domains.Messages.HomeOutputMessage;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.services.auction.AuctionService;
import rahnema.tumaj.bid.backend.utils.AuctionsBidStorage;
import rahnema.tumaj.bid.backend.utils.exceptions.FullAuctionException;
import rahnema.tumaj.bid.backend.utils.exceptions.NotAllowedToLeaveAuctionException;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class EnterExitAuctionController {
    private static final Logger logger = LoggerFactory.getLogger(EnterExitAuctionController.class);

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    private final AuctionService service;
    private final AuctionsBidStorage bidStorage;


    public EnterExitAuctionController(AuctionService service, AuctionsBidStorage bidStorage) {
        this.service = service;
        this.bidStorage = bidStorage;
    }


    @EventListener
    public void onDisconnectEvent(SessionDisconnectEvent event) {
        System.out.println("user " + event.getUser().getName() + " disconnected ");
    }

    @MessageMapping("/enter")
//    public synchronized void sendMessage(AuctionInputMessage inputMessage, /*("Authorization")*/ Message<?> message) {

//        StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//        System.out.println("message = " + headerAccessor);
//        System.out.println("headerAccessor.getUser().getName() = " + headerAccessor.getUser().getName());


    public synchronized void sendMessage(AuctionInputMessage inputMessage, /*("Authorization")*/ @Headers Map headers, @Header("simpSessionId") String sId, SimpMessageHeaderAccessor headerAccessor) {
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
            this.simpMessagingTemplate.convertAndSendToUser(sId, "/auction/" + auctionId, message, headerAccessor.getMessageHeaders());
            return;
        }
        if (usersData.containsKey(user.getName())) {
            if (usersData.get(user.getName()) == auctionId) {
                AuctionOutputMessage message = new AuctionOutputMessage();
                message.setDescription("you can't ente the same auction with two devices");
                message.setMessageType("AlreadyInTheAuction");
                this.simpMessagingTemplate.convertAndSendToUser(sId, "/auction/" + auctionId, message, headerAccessor.getMessageHeaders());
                return;
            }
        }
        if (currentAuction.getActiveBiddersLimit() > auctionsData.get(auctionId).getCurrentlyActiveBidders()) {
            currentAuction.setCurrentlyActiveBidders(currentAuction.getCurrentlyActiveBidders() + 1);
            auctionsData.put(auctionId, currentAuction);
            AuctionOutputMessage message = new AuctionOutputMessage();
            message.setActiveBidders(auctionsData.get(auctionId).getCurrentlyActiveBidders());
            message.setMessageType("UpdateActiveBiddersNumber");
            this.simpMessagingTemplate.convertAndSend("/auction/" + auctionId, message);
            sendMessageToHome(auctionId, currentAuction);
        } else {
            AuctionOutputMessage message = new AuctionOutputMessage();
            message.setDescription("you can't ente the auction,auction is full");
            message.setMessageType("AuctionIsFull");
//                throw new FullAuctionException();
        }
    }

    @MessageMapping("/exit")

    public synchronized void exit(AuctionInputMessage auctionInputMessage, @Headers Map headers, @Header("simpSessionId") String sId, SimpMessageHeaderAccessor headerAccessor) {
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
                this.simpMessagingTemplate.convertAndSendToUser(sId, "/auction/" + auctionId, message, headerAccessor.getMessageHeaders());
                return;
            }
        }
        if (!currentAuction.getLastBidder().equals(user.getName()) || currentAuction.isFinished()) {
            currentAuction.setCurrentlyActiveBidders(currentAuction.getCurrentlyActiveBidders() - 1);
            auctionsData.put(auctionId, currentAuction);
            AuctionOutputMessage message = new AuctionOutputMessage();
            message.setActiveBidders(auctionsData.get(auctionId).getCurrentlyActiveBidders());
            message.setMessageType("UpdateActiveBiddersNumber");
            this.simpMessagingTemplate.convertAndSend("/auction/" + auctionId, message);
            sendMessageToHome(auctionId, currentAuction);
        } else {
            AuctionOutputMessage message = new AuctionOutputMessage();
            message.setDescription("You can not exit the auction now, you are the last bidder");
            message.setMessageType("ExitAuctionForbidden");
            this.simpMessagingTemplate.convertAndSendToUser(sId, "/auction/" + auctionId, message, headerAccessor.getMessageHeaders());
            throw new NotAllowedToLeaveAuctionException();
        }
    }
    private void sendMessageToHome(Long auctionId, Auction currentAuction) {
        HomeOutputMessage homeOutputMessage = new HomeOutputMessage();
        homeOutputMessage.setActiveBidders(currentAuction.getCurrentlyActiveBidders());
        homeOutputMessage.setIsFinished(currentAuction.isFinished());
        this.simpMessagingTemplate.convertAndSend("/home/auction/" + auctionId, homeOutputMessage);
    }

}
