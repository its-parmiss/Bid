package rahnema.tumaj.bid.backend.domains.bid;

import lombok.Data;
import org.modelmapper.ModelMapper;
import rahnema.tumaj.bid.backend.domains.auction.AuctionOutputDTO;
import rahnema.tumaj.bid.backend.domains.user.UserOutputDTO;
import rahnema.tumaj.bid.backend.models.Bid;

import java.util.Date;

@Data
public class BidOutputDTO {
    Long id;
    Date createdAt;
    AuctionOutputDTO auction;
    UserOutputDTO bidder;

    public static BidOutputDTO fromModel(Bid bid){
        ModelMapper mapper = new ModelMapper();
        return mapper.map(bid, BidOutputDTO.class);
    }
}
