package rahnema.tumaj.bid.backend.domains.auction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.models.Category;
import rahnema.tumaj.bid.backend.models.Image;
import rahnema.tumaj.bid.backend.models.User;

import java.sql.Date;
import java.util.Set;

@Data @AllArgsConstructor @NoArgsConstructor
public class AuctionOutputDTO {
    private Long id;
    private String title;
    private String description;
    private Date start_date;
    private Long last_bid;
    private Long base_price;
    private int active_bidders_limit;
    private boolean is_active;
    private String expire_date;
    private String created_at;
    private Category category;
    private User user;
    private Set<Image> images;
    private boolean is_for_user;

    public static AuctionOutputDTO fromModel(Auction auction){
        ModelMapper mapper = new ModelMapper();
        AuctionOutputDTO outputDTO = mapper.map(auction, AuctionOutputDTO.class);
        outputDTO.setCreated_at(auction.getCreated_at().toString());
        return outputDTO;
    }
}
