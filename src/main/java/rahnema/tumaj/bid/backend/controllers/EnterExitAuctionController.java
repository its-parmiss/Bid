package rahnema.tumaj.bid.backend.controllers;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import rahnema.tumaj.bid.backend.domains.Messages.*;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.services.Message.MessageService;
import rahnema.tumaj.bid.backend.services.auction.AuctionService;
import rahnema.tumaj.bid.backend.storage.AuctionsBidStorage;
import rahnema.tumaj.bid.backend.utils.handlers.DisconnectHandler;
import rahnema.tumaj.bid.backend.utils.handlers.SubscribeHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class EnterExitAuctionController {
    private static final Logger logger = LoggerFactory.getLogger(EnterExitAuctionController.class);


    private final AuctionService service;
    private final AuctionsBidStorage bidStorage;
    private final DisconnectHandler disconnectHandler;
    private final SubscribeHandler subscribeHandler;
    private final MessageService messageService;

    public EnterExitAuctionController(AuctionService service, AuctionsBidStorage bidStorage, DisconnectHandler disconnectHandler, SubscribeHandler subscribeHandler, MessageService messageService) {
        this.service = service;
        this.bidStorage = bidStorage;
        this.disconnectHandler = disconnectHandler;
        this.subscribeHandler = subscribeHandler;
        this.messageService = messageService;
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
        this.messageService.enterAuction(auctionsData,usersData,user,auctionId,currentAuction);
    }

    private void handleExitRequestMessage(ConcurrentMap<Long, Auction> auctionsData, ConcurrentMap<String, Long> usersData, UsernamePasswordAuthenticationToken user, Long auctionId, Auction currentAuction) {
        this.messageService.exitAuction(auctionsData,usersData,user,auctionId,currentAuction);
    }
}
