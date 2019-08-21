package rahnema.tumaj.bid.backend.services.bid;

import org.springframework.stereotype.Service;
import rahnema.tumaj.bid.backend.domains.auction.AuctionInputDTO;
import rahnema.tumaj.bid.backend.domains.bid.BidInputDTO;
import rahnema.tumaj.bid.backend.domains.user.UserInputDTO;
import rahnema.tumaj.bid.backend.models.Bid;
import rahnema.tumaj.bid.backend.repositories.BidRepository;

import java.util.List;
import java.util.Optional;

@Service
public class BidServiceImpl implements BidService {

    private final BidRepository bidRepository;

    public BidServiceImpl(BidRepository bidRepository) {
        this.bidRepository = bidRepository;
    }

    @Override
    public Bid addBid(BidInputDTO bidInput) {
        Bid bid = BidInputDTO.toModel(bidInput);
        return bidRepository.save(bid);
    }

    @Override
    public Optional<Bid> getBidById(Long id) {
        return bidRepository.findById(id);
    }

    @Override
    public List<Bid> findByUser(UserInputDTO user) {
        return null;
    }

    @Override
    public List<Bid> findByAuction(AuctionInputDTO auction) {
        return null;
    }
}
