package rahnema.tumaj.bid.backend.services.Bookmarks;

import org.springframework.stereotype.Service;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.models.User;
import rahnema.tumaj.bid.backend.repositories.UserRepository;
import rahnema.tumaj.bid.backend.utils.exceptions.UserNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class BookmarksServiceImpl implements BookmarksService {

    private final UserRepository userRepository;
    BookmarksServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    @Override
    public List<Auction> getAll(Long id) {
        User user = userRepository.findById(id).orElseThrow(()-> new UserNotFoundException(id));
        Set<Auction> auctions = user.getAuctions();
        return new ArrayList<>(auctions);
    }
}
