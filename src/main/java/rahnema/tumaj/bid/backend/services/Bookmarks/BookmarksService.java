package rahnema.tumaj.bid.backend.services.Bookmarks;

import rahnema.tumaj.bid.backend.models.Auction;

import java.util.List;

public interface BookmarksService {


    List<Auction> getAll(Long id);
}
