package rahnema.tumaj.bid.backend.services.bid;

import rahnema.tumaj.bid.backend.domains.auction.AuctionInputDTO;
import rahnema.tumaj.bid.backend.domains.bid.BidInputDTO;
import rahnema.tumaj.bid.backend.domains.user.UserInputDTO;
import rahnema.tumaj.bid.backend.models.Bid;

import java.util.List;
import java.util.Optional;

public interface BidService {
    Bid addBid(BidInputDTO bidInput);
    Optional<Bid> getBidById(Long id);
    List<Bid> findByUser(UserInputDTO userInput);
    List<Bid> findByAuction(AuctionInputDTO auctionInput);
}
