package rahnema.tumaj.bid.backend.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rahnema.tumaj.bid.backend.models.Bid;

@Repository
public interface BidRepository extends CrudRepository<Bid, Long> {
}
