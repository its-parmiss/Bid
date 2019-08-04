package rahnema.tumaj.bid.backend.domains;

import lombok.Data;
import org.modelmapper.ModelMapper;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.models.Category;
import rahnema.tumaj.bid.backend.models.Images;
import rahnema.tumaj.bid.backend.models.User;

import java.util.Date;
import java.util.Set;

@Data
public class AuctionInputDTO {

    private String title;
    private String description;
    private Date start_date;
    private Long last_bid;
    private int active_bidders_limit;
    private Date expire_date;
    private Category category;
    private User user;
    private Set<Images> images;

    public Auction toModel(){
        ModelMapper mapper = new ModelMapper();
        return mapper.map(this, Auction.class);
    }
}
