package rahnema.tumaj.bid.backend.controllers;

import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import rahnema.tumaj.bid.backend.domains.Messages.AuctionInputMessage;
import rahnema.tumaj.bid.backend.domains.Messages.AuctionOutputMessage;
import rahnema.tumaj.bid.backend.domains.bid.BidInputDTO;
import rahnema.tumaj.bid.backend.domains.bid.BidInputMessage;
import rahnema.tumaj.bid.backend.domains.bid.BidOutputDTO;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.models.Bid;
import rahnema.tumaj.bid.backend.services.auction.AuctionService;
import rahnema.tumaj.bid.backend.services.bid.BidService;
import rahnema.tumaj.bid.backend.services.user.UserService;
import rahnema.tumaj.bid.backend.utils.AuctionsBidStorage;
import rahnema.tumaj.bid.backend.utils.exceptions.IllegalInputExceptions.IllegalAuctionInputException;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.AuctionNotFoundException;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

@RestController
public class BidController {

    private final BidService bidService;
    private final UserService userService;
    private final AuctionService auctionService;

    public BidController(BidService bidService, UserService userService, AuctionService auctionService) {
        this.bidService = bidService;
        this.userService = userService;
        this.auctionService = auctionService;
    }

    @PostMapping("/auctions/{auctionId}/bids")
    public BidOutputDTO addBid(@RequestBody BidInputDTO bidInput,
                               @RequestHeader("Authorization") String token,
                               @PathVariable Long auctionId) {
        bidInput.setBidder(this.userService.getUserWithToken(token));
        Auction relatedAuction = this.auctionService
                .getOne(auctionId)
                .orElseThrow(IllegalAuctionInputException::new);
        bidInput.setAuction(relatedAuction);
        return BidOutputDTO.fromModel(bidService.addBid(bidInput));
    }

    @MessageMapping("/bid")
    public synchronized void sendMessage(BidInputMessage inputMessage, /*("Authorization")*/ @Headers Map headers) {
        ConcurrentMap<Long, Auction> auctionsData = AuctionsBidStorage.getInstance().getAuctionsData();
        ConcurrentMap<Long, String> topBidders = AuctionsBidStorage.getInstance().getAuctionsTopBidders();
        String userName = ((UsernamePasswordAuthenticationToken) headers.get("simpUser")).getName();
        Auction auction = getAuction(inputMessage, auctionsData);
        if (this.isBidMessageValid(inputMessage) && !auction.isFinished())
            saveNewAuction(inputMessage, auctionsData, userName, auction);
        this.simpMessagingTemplate.convertAndSend("/auction/" + inputMessage.getAuctionId(), extractOutputMessage( auction));
    }

    private void saveNewAuction(BidInputMessage inputMessage, ConcurrentMap<Long, Auction> auctionsData, String userName, Auction auction) {
        Long bid = auction.getLastBid();
        if (bid != null)
            auction.setLastBid(bid + Long.valueOf(inputMessage.getBidPrice()) );
        else
            auction.setLastBid(Long.valueOf(inputMessage.getBidPrice()));
        auction.setLastBidder(userName);
        auctionsData.put(auction.getId(),auction);
    }

    private Auction getAuction(BidInputMessage inputMessage, ConcurrentMap<Long, Auction> auctionsData) {
        Auction auction;
        Long auctionId = Long.valueOf(inputMessage.getAuctionId());
        if (auctionsData.containsKey(auctionId))
            auction = auctionsData.get(auctionId);
        else
            auction = auctionService.getOne(auctionId).orElseThrow(() -> new AuctionNotFoundException(auctionId));

        return auction;
    }

    private AuctionOutputMessage extractOutputMessage(Auction auction) {
        AuctionOutputMessage message = new AuctionOutputMessage();
        message.setBidPrice(String.valueOf(auction.getLastBid()));
        message.setCurrentlyActiveBiddersNumber(auction.getCurrentlyActiveBidders());
        message.setAuctionId(String.valueOf((auction.getId())));
        return message;
    }

    //TODO
    private boolean isBidMessageValid(BidInputMessage inputMessage){
        return Long.valueOf(inputMessage.getBidPrice()) > 0;
    }

}