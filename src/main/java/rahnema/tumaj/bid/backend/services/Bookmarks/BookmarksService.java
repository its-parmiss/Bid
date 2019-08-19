package rahnema.tumaj.bid.backend.services.Bookmarks;

import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.models.User;

import java.util.List;

public interface BookmarksService {

    void bookmarkAuction(Long auctionId, User user);
    void unbookmarkAuction(Long auctionId, User user);
    List<Auction> getAll(User user);
}
