package rahnema.tumaj.bid.backend.utils.assemblers;

import org.quartz.Trigger;
import org.springframework.stereotype.Component;
import rahnema.tumaj.bid.backend.domains.Messages.AuctionOutputMessage;
import rahnema.tumaj.bid.backend.domains.Messages.MessageContents;
import rahnema.tumaj.bid.backend.domains.Messages.MessageTypes;
import rahnema.tumaj.bid.backend.models.Auction;

import java.util.Date;
import java.util.concurrent.ConcurrentMap;

@Component
public class MessageAssembler {

    public AuctionOutputMessage getFullMessage() {
        AuctionOutputMessage message = new AuctionOutputMessage();
        message.setDescription(MessageContents.FORBIDDEN_ENTER_FULL);
        message.setMessageType(MessageTypes.AUCTION_FULL);
        return message;
    }

    public AuctionOutputMessage getEndAuctionMessage(long auctionId, ConcurrentMap<Long, Auction> auctionsData, ConcurrentMap<Long, Trigger> triggers) {
        AuctionOutputMessage message = new AuctionOutputMessage();
        message.setLastBidder(auctionsData.get(auctionId).getLastBidder());
        message.setLastBid(auctionsData.get(auctionId).getLastBid());
        message.setIsFinished(true);
        message.setMessageType("AuctionEnded");
        message.setRemainingTime(this.calculateRemainingTime(auctionId, triggers));
        return message;
    }

    public AuctionOutputMessage getNotStartedMessage() {
        AuctionOutputMessage message = new AuctionOutputMessage();
        message.setDescription(MessageContents.FORBIDDEN_ENTER_NOT_STARTED);
        message.setMessageType(MessageTypes.NOT_STARTED);
        return message;
    }

    public AuctionOutputMessage getAlreadyInMessage() {
        AuctionOutputMessage message = new AuctionOutputMessage();
        message.setDescription(MessageContents.FORBIDDEN_ENTER_DUPLICATE);
        message.setMessageType(MessageTypes.ALREADY_IN);
        return message;
    }

    public AuctionOutputMessage getUpdateMessage(ConcurrentMap<Long, Auction> auctionsData, Auction currentAuction,
                                                 ConcurrentMap<Long, Trigger> triggers) {
        AuctionOutputMessage message = new AuctionOutputMessage();
        message.setAuctionId(String.valueOf(currentAuction.getId()));
        setMessageLastBid(currentAuction, message);
        message.setActiveBidders(auctionsData.get(currentAuction.getId()).getCurrentlyActiveBidders());
        message.setMessageType(MessageTypes.UPDATE_BIDDERS);
        message.setRemainingTime(calculateRemainingTime(currentAuction.getId(), triggers));
        return message;
    }

    private void setMessageLastBid(Auction currentAuction, AuctionOutputMessage message) {
        if (currentAuction.getLastBid() != null)
            message.setBidPrice(String.valueOf(currentAuction.getLastBid()));
        else
            message.setBidPrice(String.valueOf(currentAuction.getBasePrice()));
    }


    public AuctionOutputMessage getFinishedMessage(Auction currentAuction) {
        AuctionOutputMessage message = new AuctionOutputMessage();
        message.setIsFinished(currentAuction.isFinished());
        message.setLastBid(currentAuction.getLastBid());
        message.setDescription(MessageContents.FORBIDDEN_ENTER_CLOSED);
        message.setMessageType(MessageTypes.AUCTION_FINISHED);
        message.setRemainingTime(0L);
        return message;
    }


    public AuctionOutputMessage getLastBidderMessage() {
        AuctionOutputMessage message = new AuctionOutputMessage();
        message.setDescription(MessageContents.FORBIDDEN_EXIT_LAST_BIDDER);
        message.setMessageType(MessageTypes.EXIT_FORBIDDEN);
        message.setRemainingTime(-1L);
        return message;
    }

    public AuctionOutputMessage getUpdateOnExitMessage(ConcurrentMap<Long, Auction> auctionsData, Long auctionId,
                                                       ConcurrentMap<Long, Trigger> triggers) {
        AuctionOutputMessage message = new AuctionOutputMessage();
        message.setActiveBidders(auctionsData.get(auctionId).getCurrentlyActiveBidders());
        message.setMessageType(MessageTypes.UPDATE_BIDDERS);
        message.setRemainingTime(this.calculateRemainingTime(auctionId, triggers));
        return message;
    }

    public AuctionOutputMessage getNotInMessage() {
        AuctionOutputMessage message = new AuctionOutputMessage();
        message.setDescription(MessageContents.FORBIDDEN_EXIT_NOT_IN);
        message.setMessageType(MessageTypes.EXIT_FORBIDDEN);
        return message;
    }


    public long calculateRemainingTime(Long auctionId, ConcurrentMap<Long, Trigger> triggers) {
        if (triggers.get(auctionId) != null) {
            return (triggers.get(auctionId).getStartTime().getTime() - new Date().getTime()) / 1000;
        } else return -1;
    }


}
