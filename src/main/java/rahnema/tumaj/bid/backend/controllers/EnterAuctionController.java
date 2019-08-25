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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.socket.WebSocketHttpHeaders;
import rahnema.tumaj.bid.backend.domains.Messages.EnterAuctionInputMessage;
import rahnema.tumaj.bid.backend.domains.Messages.EnterAuctionMessage;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.models.User;
import rahnema.tumaj.bid.backend.services.auction.AuctionService;
import rahnema.tumaj.bid.backend.services.user.UserService;
import rahnema.tumaj.bid.backend.utils.exceptions.FullAuctionException;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.AuctionNotFoundException;

import java.util.Map;

@Controller
public class EnterAuctionController {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    private final AuctionService service;
    private final UserService userService;

    public EnterAuctionController(AuctionService service, UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    @MessageMapping("/enter")
    public void sendMessage(EnterAuctionInputMessage inputMessage, /*("Authorization")*/ @Headers Map headers) {
        UsernamePasswordAuthenticationToken user = (UsernamePasswordAuthenticationToken) headers.get("simpUser");
        System.out.println("user.getName() = " + user.getName());

        Long longId = Long.valueOf(inputMessage.getAuctionId());
        Auction auction = service.getOne(longId).orElseThrow(() -> new AuctionNotFoundException(longId));
        Integer currentlyActiveBidders = auction.getCurrentlyActiveBidders();
        System.out.println("currentlyActiveBidders = " + currentlyActiveBidders);
        if (auction.getActiveBiddersLimit() > currentlyActiveBidders) {
            auction.setCurrentlyActiveBidders(currentlyActiveBidders + 1);
            service.saveAuction(auction);
            EnterAuctionMessage message = new EnterAuctionMessage();
            message.setCurrentlyActiveBiddersNumber(currentlyActiveBidders + 1);
            this.simpMessagingTemplate.convertAndSend("/auction/" + inputMessage.getAuctionId(), message);
        } else {
            throw new FullAuctionException();
        }
    }
}
