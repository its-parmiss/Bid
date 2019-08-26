package rahnema.tumaj.bid.backend.utils;

import rahnema.tumaj.bid.backend.models.Auction;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AuctionsBidStorage {
    private static AuctionsBidStorage ourInstance = new AuctionsBidStorage();

    private ConcurrentMap<Long, Auction> auctionsData;
    public static AuctionsBidStorage getInstance() {
        return ourInstance;
    }

    private AuctionsBidStorage() {
        this.auctionsData =  new ConcurrentHashMap<>();
    }


    public ConcurrentMap<Long, Auction> getAuctionsData() {
        return auctionsData;
    }

    //TODO
    public void saveCacheToDB(){}
}
