package rahnema.tumaj.bid.backend.services.Message;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import rahnema.tumaj.bid.backend.models.Auction;

import java.util.concurrent.ConcurrentMap;

public interface MessageService {
    void enterAuction(ConcurrentMap<Long, Auction> auctionsData, ConcurrentMap<String, Long> usersData, UsernamePasswordAuthenticationToken user, Long auctionId, Auction currentAuction);

    void exitAuction(ConcurrentMap<Long, Auction> auctionsData, ConcurrentMap<String, Long> usersData, UsernamePasswordAuthenticationToken user, Long auctionId, Auction currentAuction);
}
