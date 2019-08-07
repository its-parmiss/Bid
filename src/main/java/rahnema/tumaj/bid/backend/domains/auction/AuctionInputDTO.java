package rahnema.tumaj.bid.backend.domains.auction;

import lombok.Data;
import org.modelmapper.ModelMapper;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.models.Category;
import rahnema.tumaj.bid.backend.models.Image;
import rahnema.tumaj.bid.backend.models.User;

import java.util.Date;
import java.util.Set;

@Data
public class AuctionInputDTO {

    private String title;
    private String description;
    private String startDate;
    private Long last_bid;
    private int active_bidders_limit;
    private String expireDate;
    private Category category;
    private User user;
    private Long base_price;
    private Set<Image> images;

    public Auction toModel(){
        ModelMapper mapper = new ModelMapper();
        return mapper.map(this, Auction.class);
    }
}
