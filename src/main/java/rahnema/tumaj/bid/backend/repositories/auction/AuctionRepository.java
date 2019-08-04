package rahnema.tumaj.bid.backend.repositories.auction;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rahnema.tumaj.bid.backend.models.Auction;

@Repository
public interface AuctionRepository extends CrudRepository<Auction, Long> {
}
