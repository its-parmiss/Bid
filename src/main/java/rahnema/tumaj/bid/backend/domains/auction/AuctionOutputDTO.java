package rahnema.tumaj.bid.backend.domains.auction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Resource;
import rahnema.tumaj.bid.backend.domains.user.UserOutputDTO;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.models.Category;
import rahnema.tumaj.bid.backend.models.Images;
import rahnema.tumaj.bid.backend.models.User;

import java.util.Date;
import java.util.Set;

@Data @AllArgsConstructor @NoArgsConstructor
public class AuctionOutputDTO {
    private Long id;
    private String title;
    private String description;
    private Date start_date;
    private Long last_bid;
    private int active_bidders_limit;
    private boolean is_active;
    private Date expire_date;
    private Category category;
    private User user;
    private Set<Images> images;
//    Set<Resource<UserOutputDTO>> users;

    public static AuctionOutputDTO fromModel(Auction auction){
        ModelMapper mapper = new ModelMapper();
        return mapper.map(auction, AuctionOutputDTO.class);
    }
}
