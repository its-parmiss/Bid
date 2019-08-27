package rahnema.tumaj.bid.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import rahnema.tumaj.bid.backend.domains.Messages.AuctionInputMessage;
import rahnema.tumaj.bid.backend.domains.Messages.AuctionOutputMessage;
import rahnema.tumaj.bid.backend.domains.Messages.HomeOutputMessage;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.services.auction.AuctionService;
import rahnema.tumaj.bid.backend.utils.AuctionsBidStorage;
import rahnema.tumaj.bid.backend.utils.exceptions.FullAuctionException;
import rahnema.tumaj.bid.backend.utils.exceptions.NotAllowedToLeaveAuctionException;

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

    @MessageMapping("/enter")
    public synchronized void sendMessage(AuctionInputMessage inputMessage, /*("Authorization")*/ @Headers Map headers, @Header("simpSessionId") String sId, SimpMessageHeaderAccessor headerAccessor) {
            ConcurrentMap <Long, Auction> auctionsData = bidStorage.getAuctionsData();
            UsernamePasswordAuthenticationToken user = (UsernamePasswordAuthenticationToken) headers.get("simpUser");
            Long auctionId = Long.valueOf(inputMessage.getAuctionId());
            Auction currentAuction=service.getAuction(auctionId,bidStorage);
            if(currentAuction.isFinished()){
                AuctionOutputMessage message=new AuctionOutputMessage();
                message.setFinished(currentAuction.isFinished());
                message.setLastBid(currentAuction.getLastBid());
                message.setDescription("you can not enter the auction,auction is closed");
                message.setMessageType("EnterAuctionForbidden");
               this.simpMessagingTemplate.convertAndSendToUser(sId,"/auction/"+inputMessage.getAuctionId(),message,headerAccessor.getMessageHeaders());
                return;
            }
            System.out.println("currentlyActiveBidders = " + currentAuction.getCurrentlyActiveBidders());
            if (currentAuction.getActiveBiddersLimit() > auctionsData.get(auctionId).getCurrentlyActiveBidders()) {
                currentAuction.setCurrentlyActiveBidders(currentAuction.getCurrentlyActiveBidders() + 1);
                auctionsData.put(auctionId, currentAuction);
                AuctionOutputMessage message = new AuctionOutputMessage();
                message.setCurrentlyActiveBiddersNumber(currentAuction.getCurrentlyActiveBidders());
                message.setMessageType("UpdateActiveBiddersNumber");
                this.simpMessagingTemplate.convertAndSend("/auction/" + auctionId, message);
                sendMessageToHome(auctionId, currentAuction);
            } else {
                AuctionOutputMessage message=new AuctionOutputMessage();
                message.setDescription("you can't ente the auction,auction is full");
                message.setMessageType("AuctionIsFull");
                throw new FullAuctionException();
            }
    }

    @MessageMapping("/exit")
    public synchronized void exit(AuctionInputMessage auctionInputMessage, @Headers Map headers,@Header("simpSessionId") String sId, SimpMessageHeaderAccessor headerAccessor) {
        ConcurrentMap <Long, Auction> auctionsData = bidStorage.getAuctionsData();
        UsernamePasswordAuthenticationToken user = (UsernamePasswordAuthenticationToken) headers.get("simpUser");
        Long auctionId = Long.valueOf(auctionInputMessage.getAuctionId());
        Auction currentAuction = service.getAuction(auctionId,bidStorage);
//        if (currentAuction.isFinished()) {
//            AuctionOutputMessage message=new AuctionOutputMessage();
//            message.setFinished(currentAuction.isFinished());
//            message.setLastBidder(currentAuction.getLastBidder());
//            message.setLastBid(currentAuction.getLastBid());
//            message.setMessageType("");
//            this.simpMessagingTemplate.convertAndSend("/auction/" + auctionId, message);
//            return;
//        }
        if (!currentAuction.getLastBidder().equals(user.getName())) {
            currentAuction.setCurrentlyActiveBidders(currentAuction.getCurrentlyActiveBidders() - 1);
            auctionsData.put(auctionId, currentAuction);
            AuctionOutputMessage message = new AuctionOutputMessage();
            message.setCurrentlyActiveBiddersNumber(auctionsData.get(auctionId).getCurrentlyActiveBidders());
            message.setMessageType("UpdateActiveBiddersNumber");
            this.simpMessagingTemplate.convertAndSend("/auction/" + auctionId, message);
            sendMessageToHome(auctionId, currentAuction);
        } else {
            AuctionOutputMessage message = new AuctionOutputMessage();
            message.setDescription("You can not exit the auction now, you are the last bidder");
            message.setMessageType("ExitAuctionForbidden");
            this.simpMessagingTemplate.convertAndSendToUser(sId,"/auction/"+auctionId,message,headerAccessor.getMessageHeaders());
            throw new NotAllowedToLeaveAuctionException();
        }
    }

    private void sendMessageToHome(Long auctionId, Auction currentAuction) {
        HomeOutputMessage homeOutputMessage = new HomeOutputMessage();
        homeOutputMessage.setActiveBidders(currentAuction.getCurrentlyActiveBidders());
        homeOutputMessage.setIsFinished(currentAuction.isFinished());
        this.simpMessagingTemplate.convertAndSend("/home/auction" + auctionId, homeOutputMessage);
    }


}
