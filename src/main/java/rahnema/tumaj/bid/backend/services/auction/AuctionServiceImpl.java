package rahnema.tumaj.bid.backend.services.auction;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.repositories.AuctionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AuctionServiceImpl implements AuctionService {

    private final AuctionRepository repository;

    public AuctionServiceImpl(AuctionRepository repository) {
        this.repository = repository;
    }
    @Override
    public Auction addAuction(Auction auction) {
        return this.repository.save(auction);
    }

    @Override
    public void deleteAuction(Long id) {

    }

    @Override
    public List<Auction> getAll(Integer page, Integer limit) {
//        repository.findAll(
//        repository
        Iterable<Auction> auctionsIterable = repository.findAll(PageRequest.of(page,limit));
        List<Auction> auctions = new ArrayList<>();
        auctionsIterable.forEach(auctions::add);
        return auctions;
    }

    @Override
    public Optional<Auction> getOne(Long id) {
        return this.repository.findById(id);

    }
}
