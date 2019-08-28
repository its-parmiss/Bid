package rahnema.tumaj.bid.backend.domains.Messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuctionOutputMessage {
    Integer activeBidders;
    Long lastBid;
    String lastBidder;
    String bidPrice;
    String auctionId;
    boolean finished;
    String description;
    String messageType;
    Long remainingTime;
}
