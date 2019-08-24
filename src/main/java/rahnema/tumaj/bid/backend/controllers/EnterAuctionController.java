package rahnema.tumaj.bid.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import rahnema.tumaj.bid.backend.domains.Messages.EnterAuctionInputMessage;
import rahnema.tumaj.bid.backend.domains.Messages.EnterAuctionMessage;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.services.auction.AuctionService;
import rahnema.tumaj.bid.backend.utils.exceptions.FullAuctionException;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.AuctionNotFoundException;

@Controller
public class EnterAuctionController {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    private final AuctionService service;

    public EnterAuctionController(AuctionService service) {
        this.service = service;
    }

    @MessageMapping("/enter")
    public void sendMessage(EnterAuctionInputMessage inputMessage){
        System.out.println("inputMessage = " + inputMessage.getAuctionId());

        Long longId = Long.valueOf(inputMessage.getAuctionId());
        Auction auction=service.getOne(longId).orElseThrow(()-> new AuctionNotFoundException(longId));
        Integer currentlyActiveBidders=auction.getCurrentlyActiveBidders();
        System.out.println("currentlyActiveBidders = " + currentlyActiveBidders);
        if(auction.getActiveBiddersLimit()>currentlyActiveBidders){
            auction.setCurrentlyActiveBidders(currentlyActiveBidders+1);
            service.saveAuction(auction);
            EnterAuctionMessage message=new EnterAuctionMessage();
            message.setCurrentlyActiveBiddersNumber(currentlyActiveBidders+1);
            this.simpMessagingTemplate.convertAndSend(  "/auction/"+inputMessage.getAuctionId(), message);
        }
        else{
            throw new FullAuctionException();
        }
    }
}
