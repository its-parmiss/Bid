package rahnema.tumaj.bid.backend.services;

import org.springframework.stereotype.Service;
import rahnema.tumaj.bid.backend.models.Auction;

import java.util.List;
import java.util.Optional;

public interface AuctionService {

    Auction addAuction(Auction user);

    void deleteAuction(Long id);

    List<Auction> getAll();

    Optional<Auction> getOne(Long id);
}
