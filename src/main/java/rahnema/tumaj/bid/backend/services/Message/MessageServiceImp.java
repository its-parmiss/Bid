package rahnema.tumaj.bid.backend.services.Message;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import rahnema.tumaj.bid.backend.domains.Messages.AuctionOutputMessage;
import rahnema.tumaj.bid.backend.domains.Messages.HomeOutputMessage;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.storage.AuctionsBidStorage;
import rahnema.tumaj.bid.backend.utils.assemblers.MessageAssembler;

import java.util.concurrent.ConcurrentMap;

@Component
public class MessageServiceImp implements MessageService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final AuctionsBidStorage bidStorage;
    private final MessageAssembler messageAssembler;

    public MessageServiceImp(SimpMessagingTemplate simpMessagingTemplate, AuctionsBidStorage bidStorage, MessageAssembler messageAssembler) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.bidStorage = bidStorage;
        this.messageAssembler = messageAssembler;
    }

    @Override
    public void enterAuction(ConcurrentMap<Long, Auction> auctionsData, ConcurrentMap<String, Long> usersData, UsernamePasswordAuthenticationToken user, Long auctionId, Auction currentAuction) {
        if (currentAuction.isFinished())
            this.simpMessagingTemplate.convertAndSendToUser(user.getName(), getAuctionDestination(auctionId), messageAssembler.getFinishedMessage(currentAuction));
        else if (isUserAlreadyIn(usersData, user, currentAuction))
            this.simpMessagingTemplate.convertAndSendToUser(user.getName(), getAuctionDestination(auctionId), messageAssembler.getAlreadyInMessage());
        else if (isEnterOk(auctionsData, currentAuction))
            enterUserToAuction(auctionsData, usersData, user, auctionId, currentAuction);
        else
            this.simpMessagingTemplate.convertAndSendToUser(user.getName(), getAuctionDestination(auctionId), messageAssembler.getFullMessage());
    }

    private void enterUserToAuction(ConcurrentMap<Long, Auction> auctionsData, ConcurrentMap<String, Long> usersData, UsernamePasswordAuthenticationToken user, Long auctionId, Auction currentAuction) {
        updateAuctionOnEnter(auctionsData, usersData, user, currentAuction);
        AuctionOutputMessage message = messageAssembler.getUpdateMessage(auctionsData, currentAuction, bidStorage.getTriggers());
        this.simpMessagingTemplate.convertAndSend(getAuctionDestination(auctionId), message);
        sendMessageToHome(currentAuction.getId(), currentAuction);
    }


    @Override
    public void exitAuction(ConcurrentMap<Long, Auction> auctionsData, ConcurrentMap<String, Long> usersData, UsernamePasswordAuthenticationToken user, Long auctionId, Auction currentAuction) {
        if (isUserAlreadyIn(usersData, user, auctionId))
            this.simpMessagingTemplate.convertAndSendToUser(user.getName(), getAuctionDestination(auctionId), messageAssembler.getNotInMessage());
        else if (isExitOk(user, currentAuction))
            exitUserFromAuction(auctionsData, usersData, user, auctionId, currentAuction);
        else if (isUserLastBidder(user, currentAuction))
            this.simpMessagingTemplate.convertAndSendToUser(user.getName(), getAuctionDestination(auctionId), messageAssembler.getLastBidderMessage());
    }

    private void exitUserFromAuction(ConcurrentMap<Long, Auction> auctionsData, ConcurrentMap<String, Long> usersData, UsernamePasswordAuthenticationToken user, Long auctionId, Auction currentAuction) {
        updateAuctionOnExit(auctionsData, usersData, user, auctionId, currentAuction);
        AuctionOutputMessage message = messageAssembler.getUpdateOnExitMessage(auctionsData, auctionId, bidStorage.getTriggers());
        this.simpMessagingTemplate.convertAndSend("/auction/" + auctionId, message);
        sendMessageToHome(auctionId, currentAuction);
    }

    private boolean isUserLastBidder(UsernamePasswordAuthenticationToken user, Auction currentAuction) {
        return currentAuction.getLastBidder().equals(user.getName());
    }

    private boolean isExitOk(UsernamePasswordAuthenticationToken user, Auction currentAuction) {
        return !currentAuction.getLastBidder().equals(user.getName()) || currentAuction.isFinished();
    }

    private boolean isUserAlreadyIn(ConcurrentMap<String, Long> usersData, UsernamePasswordAuthenticationToken user, Long auctionId) {
        return !usersData.containsKey(user.getName()) && usersData.get(user.getName()).equals(auctionId);
    }

    private boolean isEnterOk(ConcurrentMap<Long, Auction> auctionsData, Auction currentAuction) {
        return currentAuction.getActiveBiddersLimit() > auctionsData.get(currentAuction.getId()).getCurrentlyActiveBidders();
    }

    private boolean isUserAlreadyIn(ConcurrentMap<String, Long> usersData, UsernamePasswordAuthenticationToken user, Auction currentAuction) {
        return usersData.containsKey(user.getName()) && usersData.get(user.getName()).equals(currentAuction.getId());
    }


    private void updateAuctionOnExit(ConcurrentMap<Long, Auction> auctionsData, ConcurrentMap<String, Long> usersData, UsernamePasswordAuthenticationToken user, Long auctionId, Auction currentAuction) {
        currentAuction.setCurrentlyActiveBidders(currentAuction.getCurrentlyActiveBidders() - 1);
        auctionsData.put(auctionId, currentAuction);
        usersData.remove(user.getName());
    }

    private void updateAuctionOnEnter(ConcurrentMap<Long, Auction> auctionsData, ConcurrentMap<String, Long> usersData, UsernamePasswordAuthenticationToken user, Auction currentAuction) {
        currentAuction.setCurrentlyActiveBidders(currentAuction.getCurrentlyActiveBidders() + 1);
        auctionsData.put(currentAuction.getId(), currentAuction);
        usersData.put(user.getName(), currentAuction.getId());
    }


    private void sendMessageToHome(Long auctionId, Auction currentAuction) {
        HomeOutputMessage homeOutputMessage = new HomeOutputMessage();
        homeOutputMessage.setActiveBidders(currentAuction.getCurrentlyActiveBidders());
        homeOutputMessage.setIsFinished(currentAuction.isFinished());
        this.simpMessagingTemplate.convertAndSend("/home/auctions/" + auctionId, homeOutputMessage);
    }

    private String getAuctionDestination(Long Id) {
        return "/auction/" + Id;
    }


}
