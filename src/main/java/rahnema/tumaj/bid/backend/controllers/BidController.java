package rahnema.tumaj.bid.backend.controllers;

import org.springframework.web.bind.annotation.*;
import rahnema.tumaj.bid.backend.domains.bid.BidInputDTO;
import rahnema.tumaj.bid.backend.domains.bid.BidOutputDTO;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.services.auction.AuctionService;
import rahnema.tumaj.bid.backend.services.bid.BidService;
import rahnema.tumaj.bid.backend.services.user.UserService;
import rahnema.tumaj.bid.backend.utils.exceptions.IllegalInputExceptions.IllegalAuctionInputException;

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

}