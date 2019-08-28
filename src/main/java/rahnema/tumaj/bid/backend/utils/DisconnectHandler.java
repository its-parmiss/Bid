package rahnema.tumaj.bid.backend.utils;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import rahnema.tumaj.bid.backend.controllers.EnterExitAuctionController;
import rahnema.tumaj.bid.backend.domains.Messages.AuctionOutputMessage;
import rahnema.tumaj.bid.backend.models.Auction;

@Component
public class DisconnectHandler {

    private final AuctionsBidStorage bidStorage;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public DisconnectHandler(AuctionsBidStorage bidStorage, SimpMessagingTemplate simpMessagingTemplate) {
        this.bidStorage = bidStorage;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void invoke(SessionDisconnectEvent event) {
        if (event.getUser()!= null) {
            Long auctionId = bidStorage.getUsersData().get(event.getUser().getName());
            if (userIsInAuction(auctionId)) {
                doClientExit(event, auctionId);
            }
            System.out.println("user " + event.getUser().getName() + " disconnected ");
        }
    }

    private boolean userIsInAuction(Long auctionId) {
        return auctionId!= null && auctionId != -1L;
    }

    private void doClientExit(SessionDisconnectEvent event, Long auctionId) {
        updateAuctionsBidders(event, auctionId);
        sendExitFromAuctionMessage(auctionId);
    }

    private void updateAuctionsBidders(SessionDisconnectEvent event, Long auctionId) {
        Auction auction = bidStorage.getAuctionsData().get(auctionId);
        auction.setCurrentlyActiveBidders(auction.getCurrentlyActiveBidders() - 1);
        bidStorage.getAuctionsData().put(auctionId, auction);
        bidStorage.getUsersData().put(event.getUser().getName(),-1L);
    }


    private void sendExitFromAuctionMessage(Long auctionId) {
        AuctionOutputMessage message = new AuctionOutputMessage();
        message.setActiveBidders(bidStorage.getAuctionsData().get(auctionId).getCurrentlyActiveBidders());
        message.setMessageType("UpdateActiveBiddersNumber");
        this.simpMessagingTemplate.convertAndSend("/auction/" + auctionId, message);
    }
}