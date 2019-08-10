package rahnema.tumaj.bid.backend.services.Bookmarks;

import org.springframework.stereotype.Service;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.models.User;
import rahnema.tumaj.bid.backend.repositories.AuctionRepository;
import rahnema.tumaj.bid.backend.repositories.UserRepository;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.AuctionNotFoundException;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.UserNotFoundException;

import java.util.ArrayList;

import java.util.List;
import java.util.Set;

@Service
public class BookmarksServiceImpl implements BookmarksService {

    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;
    BookmarksServiceImpl(UserRepository userRepository, AuctionRepository auctionRepository){
        this.userRepository = userRepository;
        this.auctionRepository = auctionRepository;
    }
    @Override
    public List<Auction> getAll(Long id) {
        User user = userRepository.findById(id).orElseThrow(()-> new UserNotFoundException(id));
        Set<Auction> auctions = user.getAuctions();
        return new ArrayList<>(auctions);
    }

    @Override
    public void bookmarkAuction(Long auctionId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException(userId));
        Auction auction = auctionRepository.findById(auctionId).orElseThrow(()-> new AuctionNotFoundException(auctionId));
        saveBookmarkOnDB(user, auction);

    }

    private void saveBookmarkOnDB(User user, Auction auction) {
        user.getAuctions().add(auction);
        auction.getUsers().add(user);
        userRepository.save(user);
        auctionRepository.save(auction);
    }
}
