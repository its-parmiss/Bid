package rahnema.tumaj.bid.backend.services.auction;

import org.springframework.stereotype.Service;
import rahnema.tumaj.bid.backend.models.Auction;

import java.util.List;
import java.util.Optional;

public interface AuctionService {

    Auction addAuction(Auction user);

    void deleteAuction(Long id);

    List<Auction> getAll(Integer page, Integer limit);

    Optional<Auction> getOne(Long id);
    List<Auction> findByTitle(String title,Integer page,Integer limit);
}
