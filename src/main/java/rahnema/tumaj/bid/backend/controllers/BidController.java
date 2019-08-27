package rahnema.tumaj.bid.backend.controllers;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import rahnema.tumaj.bid.backend.domains.Messages.AuctionOutputMessage;
import rahnema.tumaj.bid.backend.domains.bid.BidInputDTO;
import rahnema.tumaj.bid.backend.domains.bid.BidInputMessage;
import rahnema.tumaj.bid.backend.domains.bid.BidOutputDTO;
import rahnema.tumaj.bid.backend.jobs.NewBidJob;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.services.auction.AuctionService;
import rahnema.tumaj.bid.backend.services.bid.BidService;
import rahnema.tumaj.bid.backend.services.user.UserService;
import rahnema.tumaj.bid.backend.utils.AuctionsBidStorage;
import rahnema.tumaj.bid.backend.utils.exceptions.IllegalInputExceptions.IllegalAuctionInputException;
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
    private final ConcurrentMap<Long,ArrayList<JobDetail>> jobDetails = new ConcurrentHashMap<>();
    private final BidService bidService;
    private final UserService userService;
    private final AuctionService auctionService;
    private final AuctionsBidStorage bidStorage;

    public BidController(BidService bidService, UserService userService, AuctionService auctionService, AuctionsBidStorage bidStorage) {
        this.bidService = bidService;
        this.userService = userService;
        this.auctionService = auctionService;
        this.bidStorage = bidStorage;
    }

    @PostMapping("/auctions/{auctionId}/bids")
    public BidOutputDTO addBid(@RequestBody BidInputDTO bidInput,
                               @RequestHeader("Authorization") String token,
                               @PathVariable Long auctionId) {
        bidInput.setBidder(this.userService.getUserWithToken(token));
        Auction relatedAuction = this.auctionService
                .getOne(auctionId)
                .orElseThrow(IllegalAuctionInputException::new);
        bidInput.setAuction(relatedAuction);
        return BidOutputDTO.fromModel(bidService.addBid(bidInput));
    }

    @MessageMapping("/bid")
    public synchronized void sendMessage(BidInputMessage inputMessage,
                                         @Headers Map headers, @Header("simpSessionId") String sId,
                                         SimpMessageHeaderAccessor headerAccessor) {
        ConcurrentMap<Long, Auction> auctionsData = bidStorage.getAuctionsData();
        String userName = ((UsernamePasswordAuthenticationToken) headers.get("simpUser")).getName();
//        String sessionId =  (String)headers.get("simpSessionId");
        Long auctionId = Long.valueOf(inputMessage.getAuctionId());
        Auction auction = auctionService.getAuction(auctionId, bidStorage);
        if (this.isBidMessageValid(inputMessage) && !auction.isFinished()) {
            saveNewAuction(inputMessage, auctionsData, userName, auction);
            try {
                JobDetail jobDetail = buildJobDetail(auctionId);
                for (JobDetail j : jobDetails.get(auctionId)) {
                     scheduler.deleteJob(j.getKey());
                }
                if(!jobDetails.containsKey(auctionId)){
                    jobDetails.put(auctionId,new ArrayList<>());
            }
                ArrayList<JobDetail> jobs=jobDetails.get(auctionId);
                jobs.add(jobDetail);
                jobDetails.put(auctionId,jobs);
                Trigger trigger = buildJobTrigger(jobDetail, new Date(new Date().getTime() + 30000));
                scheduler.scheduleJob(jobDetail, trigger);
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
            this.simpMessagingTemplate.convertAndSend("/auction/" + auctionId, extractOutputMessage(auction));
        } else if (auction.isFinished()) {
            AuctionOutputMessage message = new AuctionOutputMessage();
            message.setFinished(auction.isFinished());
            message.setLastBid(auction.getLastBid());
            message.setDescription("you can not bid , auction is closed");
            message.setMessageType("BidForbidden");
            this.simpMessagingTemplate.convertAndSendToUser(sId, "/auction/" + auctionId, message, headerAccessor.getMessageHeaders());
        }
    }

    private void saveNewAuction(BidInputMessage inputMessage, ConcurrentMap<Long, Auction> auctionsData, String userName, Auction auction) {
        Long bid = auction.getLastBid();
        if (bid != null)
            auction.setLastBid(bid + Long.valueOf(inputMessage.getBidPrice()));
        else
            auction.setLastBid(Long.valueOf(inputMessage.getBidPrice()));
        auction.setLastBidder(userName);
        auctionsData.put(auction.getId(), auction);
    }

    private AuctionOutputMessage extractOutputMessage(Auction auction) {
        AuctionOutputMessage message = new AuctionOutputMessage();
        message.setBidPrice(String.valueOf(auction.getLastBid()));
        message.setCurrentlyActiveBiddersNumber(auction.getCurrentlyActiveBidders());
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