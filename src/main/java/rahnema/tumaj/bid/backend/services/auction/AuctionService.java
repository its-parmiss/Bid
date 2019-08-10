package rahnema.tumaj.bid.backend.services.auction;

import org.springframework.stereotype.Service;
import rahnema.tumaj.bid.backend.domains.auction.AuctionInputDTO;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.models.Category;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

public interface AuctionService {

    Auction addAuction(AuctionInputDTO auctionInput);

    void deleteAuction(Long id);

    List<Auction> getAll(Integer page, Integer limit);

    Optional<Auction> getOne(Long id);
    List<Auction> findByTitle(String title,Integer page,Integer limit);
    List<Auction> findByTitleAndCategory(String title, Long categoryId,Integer page,Integer limit);
}
