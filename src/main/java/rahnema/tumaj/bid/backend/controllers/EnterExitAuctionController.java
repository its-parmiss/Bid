package rahnema.tumaj.bid.backend.controllers;

import com.sun.javafx.scene.EnteredExitedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import rahnema.tumaj.bid.backend.domains.Messages.AuctionEndedMessage;
import rahnema.tumaj.bid.backend.domains.Messages.AuctionInputMessage;
import rahnema.tumaj.bid.backend.domains.Messages.AuctionOutputMessage;
import rahnema.tumaj.bid.backend.jobs.NewBidJob;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.models.User;
import rahnema.tumaj.bid.backend.services.auction.AuctionService;
import rahnema.tumaj.bid.backend.services.user.UserService;
import rahnema.tumaj.bid.backend.utils.AuctionsBidStorage;
import rahnema.tumaj.bid.backend.utils.exceptions.FullAuctionException;
import rahnema.tumaj.bid.backend.utils.exceptions.NotAllowedToLeaveAuctionException;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.AuctionNotFoundException;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class EnterExitAuctionController {
    private static final Logger logger = LoggerFactory.getLogger(EnterExitAuctionController.class);

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    private final AuctionService service;
    private final AuctionsBidStorage bidStorage;


    public EnterExitAuctionController(AuctionService service, AuctionsBidStorage bidStorage) {
        this.service = service;
        this.bidStorage = bidStorage;
    }

    @MessageMapping("/enter")
    public synchronized void sendMessage(AuctionInputMessage inputMessage, /*("Authorization")*/ @Headers Map headers) {

            ConcurrentMap <Long, Auction> auctionsData = bidStorage.getAuctionsData();
            UsernamePasswordAuthenticationToken user = (UsernamePasswordAuthenticationToken) headers.get("simpUser");
            System.out.println("user.getName() = " + user.getName());
            Long longId = Long.valueOf(inputMessage.getAuctionId());

            Auction currentAuction;
            if (auctionsData.get(longId) != null) {
                currentAuction = auctionsData.get(longId);
            } else {
                currentAuction = service.getOne(longId).orElseThrow(() -> new AuctionNotFoundException(longId));
                auctionsData.put(longId, currentAuction);
            }
            System.out.println("currentlyActiveBidders = " + currentAuction.getCurrentlyActiveBidders());
            if (currentAuction.getActiveBiddersLimit() > auctionsData.get(longId).getCurrentlyActiveBidders()) {
                currentAuction.setCurrentlyActiveBidders(currentAuction.getCurrentlyActiveBidders() + 1);
                auctionsData.put(longId, currentAuction);
                AuctionOutputMessage message = new AuctionOutputMessage();
                message.setCurrentlyActiveBiddersNumber(auctionsData.get(longId).getCurrentlyActiveBidders());
                message.setBidPrice(String.valueOf(auctionsData.get(longId).getLastBid()));
                this.simpMessagingTemplate.convertAndSend("/auction/" + inputMessage.getAuctionId(), message);
            } else {
                throw new FullAuctionException();
            }

    }


    @MessageMapping("/exit")
    public synchronized void exit(AuctionInputMessage auctionInputMessage, @Headers Map headers) {
        ConcurrentMap <Long, Auction> auctionsData = bidStorage.getAuctionsData();

        UsernamePasswordAuthenticationToken user = (UsernamePasswordAuthenticationToken) headers.get("simpUser");
        System.out.println("user.getName() = " + user.getName());
        Long auctionId = Long.valueOf(auctionInputMessage.getAuctionId());
        Auction currentAuction = getAuction(auctionId);
        if (currentAuction.isFinished()) {
            return;
        }
        System.out.println("currentlyActiveBidders = " + currentAuction.getCurrentlyActiveBidders());
        if (!currentAuction.getLastBidder().equals(user.getName())) {
            currentAuction.setCurrentlyActiveBidders(currentAuction.getCurrentlyActiveBidders() - 1);
            auctionsData.put(auctionId, currentAuction);
            AuctionOutputMessage message = new AuctionOutputMessage();
            message.setCurrentlyActiveBiddersNumber(auctionsData.get(auctionId).getCurrentlyActiveBidders());
            this.simpMessagingTemplate.convertAndSend("/auction/" + auctionInputMessage.getAuctionId(), message);
        } else {
            throw new NotAllowedToLeaveAuctionException();
        }
    }

    @MessageMapping("/endOfAuction")
    public synchronized void end(AuctionInputMessage auctionInputMessage) {
        ConcurrentMap <Long, Auction> auctionsData = bidStorage.getAuctionsData();
        Long auctionId = Long.valueOf(auctionInputMessage.getAuctionId());
        Auction currentAuction = getAuction(auctionId);
        currentAuction.setFinished(true);
        auctionsData.put(auctionId, currentAuction);
        service.saveAuction(currentAuction);
        AuctionEndedMessage message = new AuctionEndedMessage();
        message.setWinner(auctionsData.get(auctionId).getLastBidder());
        message.setWinningPrice(auctionsData.get(auctionId).getLastBid());
        this.simpMessagingTemplate.convertAndSend("/auction/" + auctionInputMessage.getAuctionId(), message);
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
//    private JobDetail buildJobDetail() {
//        JobDataMap jobDataMap = new JobDataMap();
//
//
//        return JobBuilder.newJob(NewBidJob.class)
//                .withIdentity(UUID.randomUUID().toString(), "email-jobs")
//                .withDescription("Send Email Job")
//                .usingJobData(jobDataMap)
//                .storeDurably()
//                .build();
//    }
//    private Trigger buildJobTrigger(JobDetail jobDetail, ZonedDateTime startAt) {
//        return TriggerBuilder.newTrigger()
//                .forJob(jobDetail)
//                .withIdentity(jobDetail.getKey().getName(), "email-triggers")
//                .withDescription("Send Email Trigger")
//                .startAt(Date.from(startAt.toInstant()))
//                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
//                .build();
//    }
//}

}
