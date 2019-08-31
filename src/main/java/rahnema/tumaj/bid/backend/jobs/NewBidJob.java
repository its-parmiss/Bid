package rahnema.tumaj.bid.backend.jobs;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import rahnema.tumaj.bid.backend.domains.Messages.AuctionOutputMessage;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.services.auction.AuctionService;
import rahnema.tumaj.bid.backend.utils.AuctionsBidStorage;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.AuctionNotFoundException;

import java.util.concurrent.ConcurrentMap;

@Component
public class NewBidJob extends QuartzJobBean {
    private static final Logger logger = LoggerFactory.getLogger(NewBidJob.class);
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private final AuctionService service;
    @Autowired
    private final AuctionsBidStorage bidStorage;

    public NewBidJob(AuctionService service, AuctionsBidStorage bidStorage) {
        this.service = service;
        this.bidStorage = bidStorage;
    }

    public synchronized void end(long auctionId) {
        ConcurrentMap<Long, Auction> auctionsData = bidStorage.getAuctionsData();
        Auction currentAuction = getAuction(auctionId);
        currentAuction.setFinished(true);
        auctionsData.put(auctionId, currentAuction);
        service.saveAuction(currentAuction);
        AuctionOutputMessage message = new AuctionOutputMessage();
        message.setLastBidder(auctionsData.get(auctionId).getLastBidder());
        message.setLastBid(auctionsData.get(auctionId).getLastBid());
        message.setFinished(true);
        message.setMessageType("AuctionEnded");
        System.out.println("heree");
        this.simpMessagingTemplate.convertAndSend("/auction/" + auctionId, message);
    }
    private synchronized Auction getAuction(Long auctionId) {
        ConcurrentMap <Long, Auction> auctionsData = bidStorage.getAuctionsData();
        Auction currentAuction;
        if (auctionsData.get(auctionId) != null) {
            currentAuction = auctionsData.get(auctionId);
        } else {
            currentAuction = service.getOne(auctionId).orElseThrow(() -> new AuctionNotFoundException(auctionId));
            auctionsData.put(auctionId, currentAuction);
        }
        return currentAuction;
    }
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("Executing Job with key {}", jobExecutionContext.getJobDetail().getKey());
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        Long auctionId= jobDataMap.getLong("auctionId");
        System.out.println("bj before end");
        end(auctionId);
        System.out.println("bj after end");
    }

}