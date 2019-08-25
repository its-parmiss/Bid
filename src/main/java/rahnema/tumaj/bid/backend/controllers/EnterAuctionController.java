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
    public void sendMessage(AuctionInputMessage inputMessage){
        System.out.println("inputMessage = " + inputMessage.getAuctionId());

        Long longId = Long.valueOf(inputMessage.getAuctionId());
        Auction auction=service.getOne(longId).orElseThrow(()-> new AuctionNotFoundException(longId));
        Integer currentlyActiveBidders=auction.getCurrentlyActiveBidders();
        if(auction.getActiveBiddersLimit()>currentlyActiveBidders){
            auction.setCurrentlyActiveBidders(currentlyActiveBidders+1);
            service.saveAuction(auction);
            AuctionOutputMessage message=new AuctionOutputMessage();
            message.setCurrentlyActiveBiddersNumber(currentlyActiveBidders+1);
            this.simpMessagingTemplate.convertAndSend(  "/auction/"+inputMessage.getAuctionId(), message);
        }
        else{
            throw new FullAuctionException();
        }
    }
}
