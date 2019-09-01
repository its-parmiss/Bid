package rahnema.tumaj.bid.backend.jobs;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import rahnema.tumaj.bid.backend.domains.Messages.AuctionOutputMessage;
import rahnema.tumaj.bid.backend.domains.Messages.HomeOutputMessage;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.services.auction.AuctionService;
import rahnema.tumaj.bid.backend.storage.AuctionsBidStorage;
import rahnema.tumaj.bid.backend.utils.assemblers.MessageAssembler;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.AuctionNotFoundException;

import java.util.concurrent.ConcurrentMap;

@Component
public class NewBidJob extends QuartzJobBean {
    private static final Logger logger = LoggerFactory.getLogger(NewBidJob.class);
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final AuctionService service;
    private final AuctionsBidStorage bidStorage;
    private final MessageAssembler messageAssembler;


    public NewBidJob(SimpMessagingTemplate simpMessagingTemplate, AuctionService service, AuctionsBidStorage bidStorage, MessageAssembler messageAssembler) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.service = service;
        this.bidStorage = bidStorage;
        this.messageAssembler = messageAssembler;
    }

    private synchronized void end(long auctionId) {
        ConcurrentMap<Long, Auction> auctionsData = bidStorage.getAuctionsData();
        Auction currentAuction = getAuction(auctionId);
        updateAuctionOnEnd(auctionId, auctionsData, currentAuction);
        AuctionOutputMessage message = messageAssembler.getEndAuctionMessage(auctionId, auctionsData,bidStorage.getTriggers());
        this.simpMessagingTemplate.convertAndSend("/auction/" + auctionId, message);
        this.sendMessageToHome(auctionId,currentAuction);

    }

    private void updateAuctionOnEnd(long auctionId, ConcurrentMap<Long, Auction> auctionsData, Auction currentAuction) {
        currentAuction.setFinished(true);
        auctionsData.put(auctionId, currentAuction);
        service.saveAuction(currentAuction);
    }


    private void sendMessageToHome(Long auctionId, Auction currentAuction) {
        HomeOutputMessage homeOutputMessage = new HomeOutputMessage();
        homeOutputMessage.setActiveBidders(currentAuction.getCurrentlyActiveBidders());
        homeOutputMessage.setIsFinished(currentAuction.isFinished());
        this.simpMessagingTemplate.convertAndSend("/home/auctions/" + auctionId, homeOutputMessage);
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