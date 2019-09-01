package rahnema.tumaj.bid.backend.controllers;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import rahnema.tumaj.bid.backend.domains.Messages.AuctionOutputMessage;
import rahnema.tumaj.bid.backend.domains.bid.BidInputMessage;
import rahnema.tumaj.bid.backend.jobs.NewBidJob;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.services.auction.AuctionService;
import rahnema.tumaj.bid.backend.services.bid.BidService;
import rahnema.tumaj.bid.backend.services.user.UserService;
import rahnema.tumaj.bid.backend.storage.AuctionsBidStorage;
import rahnema.tumaj.bid.backend.utils.assemblers.MessageAssembler;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@RestController
public class BidController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private Scheduler scheduler;
    private final BidService bidService;
    private final UserService userService;
    private final AuctionService auctionService;
    private final AuctionsBidStorage bidStorage;
    private final MessageAssembler messageAssembler;

    public BidController(BidService bidService, UserService userService, AuctionService auctionService, AuctionsBidStorage bidStorage, MessageAssembler messageAssembler) {
        this.bidService = bidService;
        this.userService = userService;
        this.auctionService = auctionService;
        this.bidStorage = bidStorage;
        this.messageAssembler = messageAssembler;
    }

    @MessageMapping("/bid")
    public synchronized void sendMessage(BidInputMessage inputMessage,
                                         @Headers Map headers) {

        ConcurrentMap<Long, Auction> auctionsData = bidStorage.getAuctionsData();
        ConcurrentMap<String, Long> usersData = bidStorage.getUsersData();
        String userName = ((UsernamePasswordAuthenticationToken) headers.get("simpUser")).getName();
        Long auctionId = Long.valueOf(inputMessage.getAuctionId());
        AuctionOutputMessage message = new AuctionOutputMessage();
        Auction auction = auctionService.getAuction(auctionId, bidStorage);
        System.out.println("hello");
        if (!usersData.containsKey(userName) || !usersData.get(userName).equals(auctionId)) {
            System.out.println("1");
            message.setDescription("you can't bid ,please enter auction first");
            message.setMessageType("NotInTheAuction");
            this.simpMessagingTemplate.convertAndSendToUser(userName, "/auction/" + auctionId, message);
            return;
        }
        if (this.isBidMessageValid(inputMessage) && !auction.isFinished()) {
            System.out.println("2");
            saveNewAuction(inputMessage, auctionsData, userName, auction);
            scheduleBid(auctionId);
            this.simpMessagingTemplate.convertAndSend("/auction/" + auctionId, extractOutputMessage(auction));
        } else if (auction.isFinished()) {
            System.out.println("3");
            message.setIsFinished(auction.isFinished());
            message.setLastBid(auction.getLastBid());
            message.setDescription("you can not bid , auction is closed");
            message.setMessageType("BidForbidden");
            this.simpMessagingTemplate.convertAndSendToUser(userName, "/auction/" + auctionId, message);
        } else {
            System.out.println("4");
            message.setDescription("you can not bid , auction is closed");
            message.setMessageType("BidNotValid");
            this.simpMessagingTemplate.convertAndSendToUser(userName, "/auction/" + auctionId, message);
        }
    }
    private synchronized void scheduleBid(Long auctionId) {
        try {
            JobDetail jobDetail = buildJobDetail(auctionId);
            if (!bidStorage.getJobDetails().containsKey(auctionId)) {
                bidStorage.getJobDetails().put(auctionId, new ArrayList<>());
            }
            for (JobDetail j : bidStorage.getJobDetails().get(auctionId)) {
                System.out.println("j = " + j);
                scheduler.deleteJob(j.getKey());
            }
            ArrayList<JobDetail> jobs = bidStorage.getJobDetails().get(auctionId);
            jobs.add(jobDetail);
            bidStorage.getJobDetails().put(auctionId, jobs);
            Trigger trigger = buildJobTrigger(jobDetail, new Date(new Date().getTime() + 30000));
            System.out.println("trigger.getEndTime() = " + trigger.getEndTime());
            System.out.println("trigger.getStartTime() = " + trigger.getStartTime());
            bidStorage.getTriggers().put(auctionId, trigger);
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    private void saveNewAuction(BidInputMessage inputMessage, ConcurrentMap<Long, Auction> auctionsData, String userName, Auction auction) {
        Long bid = auction.getLastBid();
        if (bid != null)
            auction.setLastBid(bid + Long.valueOf(inputMessage.getBidPrice()));
        else
            auction.setLastBid(auction.getBasePrice() + Long.valueOf(inputMessage.getBidPrice()) );
        auction.setLastBidder(userName);
        auctionsData.put(auction.getId(), auction);
    }

    private AuctionOutputMessage extractOutputMessage(Auction auction) {
        AuctionOutputMessage message = new AuctionOutputMessage();
        message.setRemainingTime(messageAssembler.calculateRemainingTime(auction.getId(), bidStorage.getTriggers()));
        message.setBidPrice(String.valueOf(auction.getLastBid()));
        message.setActiveBidders(auction.getCurrentlyActiveBidders());
        message.setMessageType("newBid");
        return message;
    }

    //TODO
    private boolean isBidMessageValid(BidInputMessage inputMessage) {
        return Long.valueOf(inputMessage.getBidPrice()) > 0;
    }

    private JobDetail buildJobDetail(Long auctionId) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("auctionId", auctionId);
        String jobName = UUID.randomUUID().toString();
        String jobGroup = "auction-jobs";
        return JobBuilder.newJob(NewBidJob.class)
                .withIdentity(jobName, jobGroup)
                .withDescription("Send auction job")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    private Trigger buildJobTrigger(JobDetail jobDetail, Date startAt) {
        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "auction-triggers")
                .withDescription("Send auction Trigger")
                .startAt(startAt)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
        return trigger;
    }

}