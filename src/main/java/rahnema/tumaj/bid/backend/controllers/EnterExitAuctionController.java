package rahnema.tumaj.bid.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.socket.WebSocketHttpHeaders;
import rahnema.tumaj.bid.backend.domains.Messages.AuctionEndedMessage;
import rahnema.tumaj.bid.backend.domains.Messages.AuctionInputMessage;
import rahnema.tumaj.bid.backend.domains.Messages.AuctionOutputMessage;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.models.User;
import rahnema.tumaj.bid.backend.services.auction.AuctionService;
import rahnema.tumaj.bid.backend.services.user.UserService;
import rahnema.tumaj.bid.backend.utils.AuctionsBidStorage;
import rahnema.tumaj.bid.backend.utils.exceptions.FullAuctionException;
import rahnema.tumaj.bid.backend.utils.exceptions.NotAllowedToLeaveAuctionException;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.AuctionNotFoundException;

import java.security.Principal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
public class EnterExitAuctionController {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    private final AuctionService service;
    private final AuctionsBidStorage bidStorage;


    public EnterExitAuctionController(AuctionService service, AuctionsBidStorage bidStorage) {
        this.service = service;
        this.bidStorage = bidStorage;
    }

    @MessageMapping("/enter")
    public synchronized void sendMessage(AuctionInputMessage inputMessage, /*("Authorization")*/ Message<?> message) {

        StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        System.out.println("message = " + headerAccessor);
        System.out.println("headerAccessor.getUser().getName() = " + headerAccessor.getUser().getName());

        ConcurrentMap<Long, Auction> auctionsData = bidStorage.getAuctionsData();
        Long longId = Long.valueOf(inputMessage.getAuctionId());

        Auction currentAuction;
        if (auctionsData.get(longId) != null) {
            currentAuction = auctionsData.get(longId);
        } else {
            currentAuction = service.getOne(longId).orElseThrow(() -> new AuctionNotFoundException(longId));
            auctionsData.put(longId, currentAuction);
        }
        System.out.println("currentlyActiveBidders = " + currentAuction.getCurrentlyActiveBidders());
        if (currentAuction.getActiveBiddersLimit() > auctionsData.get(longId).getCurrentlyActiveBidders()) {
            currentAuction.setCurrentlyActiveBidders(currentAuction.getCurrentlyActiveBidders() + 1);
            auctionsData.put(longId, currentAuction);
            AuctionOutputMessage outMessage = new AuctionOutputMessage();
            outMessage.setCurrentlyActiveBiddersNumber(auctionsData.get(longId).getCurrentlyActiveBidders());
            outMessage.setBidPrice(String.valueOf(auctionsData.get(longId).getLastBid()));
            this.simpMessagingTemplate.convertAndSend("/auction/" + inputMessage.getAuctionId(), outMessage);
        } else {
            throw new FullAuctionException();
        }

    }


    @MessageMapping("/exit")
    public synchronized void exit(AuctionInputMessage auctionInputMessage, @Headers Map headers) {
        ConcurrentMap<Long, Auction> auctionsData = bidStorage.getAuctionsData();

        UsernamePasswordAuthenticationToken user = (UsernamePasswordAuthenticationToken) headers.get("simpUser");
        System.out.println("user.getName() = " + user.getName());
        Long auctionId = Long.valueOf(auctionInputMessage.getAuctionId());
        Auction currentAuction = getAuction(auctionId);
        if (currentAuction.isFinished()) {
            return;
        }
        System.out.println("currentlyActiveBidders = " + currentAuction.getCurrentlyActiveBidders());
        if (!currentAuction.getLastBidder().equals(user.getName())) {
            currentAuction.setCurrentlyActiveBidders(currentAuction.getCurrentlyActiveBidders() - 1);
            auctionsData.put(auctionId, currentAuction);
            AuctionOutputMessage message = new AuctionOutputMessage();
            message.setCurrentlyActiveBiddersNumber(auctionsData.get(auctionId).getCurrentlyActiveBidders());
            this.simpMessagingTemplate.convertAndSend("/auction/" + auctionInputMessage.getAuctionId(), message);
        } else {
            throw new NotAllowedToLeaveAuctionException();
        }
    }

    @MessageMapping("/endOfAuction")
    public synchronized void end(AuctionInputMessage auctionInputMessage) {
        ConcurrentMap<Long, Auction> auctionsData = bidStorage.getAuctionsData();
        Long auctionId = Long.valueOf(auctionInputMessage.getAuctionId());
        Auction currentAuction = getAuction(auctionId);
        currentAuction.setFinished(true);
        auctionsData.put(auctionId, currentAuction);
        service.saveAuction(currentAuction);
        AuctionEndedMessage message = new AuctionEndedMessage();
        message.setWinner(auctionsData.get(auctionId).getLastBidder());
        message.setWinningPrice(auctionsData.get(auctionId).getLastBid());
        this.simpMessagingTemplate.convertAndSend("/auction/" + auctionInputMessage.getAuctionId(), message);
    }

    private synchronized Auction getAuction(Long auctionId) {
        ConcurrentMap<Long, Auction> auctionsData = bidStorage.getAuctionsData();
        Auction currentAuction;
        if (auctionsData.get(auctionId) != null) {
            currentAuction = auctionsData.get(auctionId);
        } else {
            currentAuction = service.getOne(auctionId).orElseThrow(() -> new AuctionNotFoundException(auctionId));
            auctionsData.put(auctionId, currentAuction);
        }
        return currentAuction;
    }
}
