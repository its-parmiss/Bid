package rahnema.tumaj.bid.backend.domains.Messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnterAuctionInputMessage {
    String auctionId;
}
