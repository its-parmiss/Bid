package rahnema.tumaj.bid.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import rahnema.tumaj.bid.backend.domains.Messages.AuctionInputMessage;
import rahnema.tumaj.bid.backend.domains.Messages.AuctionOutputMessage;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.services.auction.AuctionService;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.AuctionNotFoundException;

@Controller
public class ExitAuctionController {
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;
    final AuctionService auctionService;

    public ExitAuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @MessageMapping("/exit")
    public void exit(AuctionInputMessage auctionInputMessage){
        Long auctionId=Long.valueOf(auctionInputMessage.getAuctionId());
        Auction auction=auctionService.getOne(auctionId).orElseThrow(()-> new AuctionNotFoundException(auctionId));
        auction.setCurrentlyActiveBidders(auction.getCurrentlyActiveBidders()-1);
        auctionService.saveAuction(auction);
        AuctionOutputMessage auctionOutputMessage =new AuctionOutputMessage(auction.getCurrentlyActiveBidders());
        this.simpMessagingTemplate.convertAndSend("/auction/"+auctionInputMessage.getAuctionId(), auctionOutputMessage);
    }
}
