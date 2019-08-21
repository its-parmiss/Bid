package rahnema.tumaj.bid.backend.domains.bid;

import lombok.Data;
import org.modelmapper.ModelMapper;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.models.Bid;
import rahnema.tumaj.bid.backend.models.User;

@Data
public class BidInputDTO {
    String title;
    Auction auction;
    Long auctionId;
    User bidder;

    public static Bid toModel(BidInputDTO bidDTO){
        ModelMapper mapper = new ModelMapper();
        return mapper.map(bidDTO, Bid.class);
    }
}
