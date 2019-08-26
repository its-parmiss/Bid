package rahnema.tumaj.bid.backend.domains.bid;

import lombok.Data;

@Data
public class BidInputMessage {
    String bidPrice;
    String auctionId;
}
