package rahnema.tumaj.bid.backend.storage;

import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.repositories.AuctionRepository;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class AuctionsBidStorage {

    private final AuctionRepository auctionRepository;

    private ConcurrentMap<Long, Auction> auctionsData;
    private ConcurrentMap<String, Long> usersData;
    private ConcurrentMap<Long, Trigger> triggers;
    private final ConcurrentMap<Long, ArrayList<JobDetail>> jobDetails = new ConcurrentHashMap<>();


    public AuctionsBidStorage(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
        this.auctionsData =  new ConcurrentHashMap<>();
        this.usersData=new ConcurrentHashMap<>();
        this.triggers = new ConcurrentHashMap<>();
    }

    public ConcurrentMap<Long, Auction> getAuctionsData() {
        return auctionsData;
    }
    public ConcurrentMap<String ,Long > getUsersData(){return usersData; }

    public ConcurrentMap<Long, Trigger> getTriggers() {
        return triggers;
    }

    public ConcurrentMap<Long, ArrayList<JobDetail>> getJobDetails() {
        return jobDetails;
    }
    //    //TODO
//    public void saveCacheToDB(){
//
//
//    }
}
