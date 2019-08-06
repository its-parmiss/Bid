package rahnema.tumaj.bid.backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import rahnema.tumaj.bid.backend.models.Auction;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuctionRepository extends PagingAndSortingRepository<Auction, Long> {
//    @Query(
////            value = "SELECT auction_id as id, count(auction_id) as c from bookmarks group by auction_id order by c",
//            value = "SELECT * FROM (SELECT auction_id, count(auction_id) as c from bookmarks group by auction_id order by c) as x INNER join auctions ON (x.auction_id = auctions.id);",
//            nativeQuery = true)
//    Page<Auction> findAllAuctionsHottest(Pageable pageable);

//    value = "SELECT * FROM (SELECT auction_id, count(auction_id) as c from bookmarks group by " +
//            "auction_id order by c) as x INNER join auctions ON (x.auction_id = auctions.id) order by c desc;",

    @Query(
            value = "SELECT DISTINCT * FROM (SELECT auction_id, count(auction_id) as c from bookmarks group by " +
                    "auction_id order by c) as x RIGHT OUTER join auctions ON (x.auction_id = auctions.id) order by c desc",
            nativeQuery = true )
    List<Auction> findAllAuctionsHottest(Pageable pageable);

//
    Optional<List<Auction>> findByTitle(String title);
    List<Auction> findByTitleContaining(String title,Pageable pageable);
}
