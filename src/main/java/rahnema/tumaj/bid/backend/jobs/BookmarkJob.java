package rahnema.tumaj.bid.backend.jobs;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;
import rahnema.tumaj.bid.backend.domains.Messages.AuctionOutputMessage;
import rahnema.tumaj.bid.backend.domains.Messages.BookmarkOutputMessage;
import rahnema.tumaj.bid.backend.domains.auction.AuctionOutputDTO;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.models.User;

import java.util.Optional;

public class BookmarkJob extends QuartzJobBean {

    private static final Logger logger = LoggerFactory.getLogger(BookmarkJob.class);

    private final SimpMessagingTemplate template;

    public BookmarkJob(SimpMessagingTemplate template) {
        this.template = template;
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        logger.info("Executing Job with key {}", jobExecutionContext.getJobDetail().getKey());

        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        long auctionId = jobDataMap.getLong("auctionId");
        Auction auction = (Auction) jobDataMap.get("auction");

        String description = "Your bookmarked auction \"" + auction.getTitle() + "\" is going to start in 10 minutes.";
        BookmarkOutputMessage payload = new BookmarkOutputMessage();
        payload.setDescription(description);
        payload.setMessageType("bookmarkNotification");
//        AuctionOutputMessage msg = new AuctionOutputMessage();
//        msg.setMessageType("dish 1");
//        msg.setDescription("dishdish 2");

        this.template.convertAndSend(
                "/auctions/"+auctionId+"/bookmark",
                payload
        );

//        this.template.convertAndSend(
//                "/auctions/"+auctionId+"/bookmark",
//                msg
//        );

    }
}
