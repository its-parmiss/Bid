package rahnema.tumaj.bid.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import rahnema.tumaj.bid.backend.domains.Messages.AuctionInputMessage;
import rahnema.tumaj.bid.backend.domains.Messages.AuctionOutputMessage;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.models.User;
import rahnema.tumaj.bid.backend.services.auction.AuctionService;
import rahnema.tumaj.bid.backend.services.user.UserService;
import rahnema.tumaj.bid.backend.utils.exceptions.FullAuctionException;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.AuctionNotFoundException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
public class EnterExitAuctionController {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    private final AuctionService service;
    private final UserService userService;
    ConcurrentMap<Long, Auction> auctionsData = new ConcurrentHashMap<>();


    public EnterExitAuctionController(AuctionService service, UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    @MessageMapping("/enter")

    public synchronized void sendMessage(AuctionInputMessage inputMessage, /*("Authorization")*/ @Headers Map headers) {

            UsernamePasswordAuthenticationToken user = (UsernamePasswordAuthenticationToken) headers.get("simpUser");
            System.out.println("user.getName() = " + user.getName());
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
                AuctionOutputMessage message = new AuctionOutputMessage();
                message.setCurrentlyActiveBiddersNumber(auctionsData.get(longId).getCurrentlyActiveBidders());
                this.simpMessagingTemplate.convertAndSend("/auction/" + inputMessage.getAuctionId(), message);
            } else {
                throw new FullAuctionException();
            }
        }


    @MessageMapping("/exit")
    public synchronized void exit(AuctionInputMessage auctionInputMessage) {
        Long auctionId = Long.valueOf(auctionInputMessage.getAuctionId());
//        Auction auction = service.getOne(auctionId).orElseThrow(() -> new AuctionNotFoundException(auctionId));
//        auction.setCurrentlyActiveBidders(auction.getCurrentlyActiveBidders() - 1);
//        service.saveAuction(auction);
//        AuctionOutputMessage auctionOutputMessage = new AuctionOutputMessage(auction.getCurrentlyActiveBidders());
//        this.simpMessagingTemplate.convertAndSend("/auction/" + auctionInputMessage.getAuctionId(), auctionOutputMessage);
    }
}
