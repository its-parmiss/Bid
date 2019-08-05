package rahnema.tumaj.bid.backend.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import rahnema.tumaj.bid.backend.models.Auction;

import java.util.List;

@Repository
public interface AuctionRepository extends PagingAndSortingRepository<Auction, Long> {
//    @Query("SELECT u FROM User u WHERE u.status = 1")
//    List<Auction> findByHottest();
}
