package rahnema.tumaj.bid.backend.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.repositories.AuctionRepository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class AuctionsBidStorage {

    private final AuctionRepository auctionRepository;

    private ConcurrentMap<Long, Auction> auctionsData;

    public AuctionsBidStorage(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
        this.auctionsData =  new ConcurrentHashMap<>();
    }

    public ConcurrentMap<Long, Auction> getAuctionsData() {
        return auctionsData;
    }

    //TODO
    public void saveCacheToDB(){


    }
}
