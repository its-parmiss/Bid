package rahnema.tumaj.bid.backend.utils;

import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import rahnema.tumaj.bid.backend.domains.Messages.HomeOutputMessage;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.services.auction.AuctionService;

import java.util.List;
import java.util.Map;


@Component
public class SubscribeHandler {


    private final AuctionService service;
    private final AuctionsBidStorage bidStorage;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public SubscribeHandler(AuctionService service, AuctionsBidStorage bidStorage, SimpMessagingTemplate simpMessagingTemplate) {
        this.service = service;
        this.bidStorage = bidStorage;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void invoke(SessionSubscribeEvent event) {
        Message<?> message = event.getMessage();
        String command = message.getHeaders().get("stompCommand").toString();
        if (command.equals("SUBSCRIBE")) {
            String dist = getDestinationFromMessage(message);
            if (dist.startsWith("/home/auctions/")) {
                Long auctionId = Long.valueOf(dist.substring(15));
                System.out.println("dist : " + auctionId);
                Auction auction = service.getAuction(auctionId, bidStorage);
                sendMessageToHome(auctionId, auction);
            }
        }
    }


    private String getDestinationFromMessage(Message<?> message) {
        return (String) ( (List<Object>) getNativeHeadersFromMessage(message) ).get(0);
    }

    private Object getNativeHeadersFromMessage(Message<?> message) {
        return ((Map)(message.getHeaders().get("nativeHeaders"))).get("destination");
    }

    private void sendMessageToHome(Long auctionId, Auction currentAuction) {
        HomeOutputMessage homeOutputMessage = new HomeOutputMessage();
        homeOutputMessage.setActiveBidders(currentAuction.getCurrentlyActiveBidders());
        homeOutputMessage.setIsFinished(currentAuction.isFinished());
        this.simpMessagingTemplate.convertAndSend("/home/auctions/" + auctionId, homeOutputMessage);
    }
}