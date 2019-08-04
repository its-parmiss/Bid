package rahnema.tumaj.bid.backend.services;

import org.springframework.stereotype.Service;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.repositories.AuctionRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AuctionServiceimpl implements AuctionService {

    private final AuctionRepository repository;

    public AuctionServiceimpl(AuctionRepository repository) {
        this.repository = repository;
    }
    @Override
    public Auction addAuction(Auction auction) {
        return repository.save(auction);
    }

    @Override
    public void deleteAuction(Long id) {

    }

    @Override
    public List<Auction> getAll() {
        return null;
    }

    @Override
    public Optional<Auction> getOne(Long id) {
        return Optional.empty();
    }
}
