package rahnema.tumaj.bid.backend.jobs;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;
import rahnema.tumaj.bid.backend.domains.Messages.HomeOutputMessage;

public class AuctionStartedJob extends QuartzJobBean {
    private static final Logger logger = LoggerFactory.getLogger(NewBidJob.class);
    private final SimpMessagingTemplate simpMessagingTemplate;

    public AuctionStartedJob(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("Executing Job with key {}", jobExecutionContext.getJobDetail().getKey());
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        Long auctionId= jobDataMap.getLong("auctionId");
        HomeOutputMessage message = new HomeOutputMessage();
        message.setIsFinished(false);
        message.setActiveBidders(0);
        message.setIsStarted(true);
        message.setType("started");
        this.simpMessagingTemplate.convertAndSend("/home/auctions/" + auctionId, message);


    }
}
