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
    private String startDate;
    private Long lastBid;
    private Long basePrice;
    private int activeBiddersLimit;
    private Boolean done;
    private String expireDate;
    private String createdAt;
    private Category category;
    private UserOutputDTO user;
    private Set<Images> images;
    private boolean isForUser;

    public static AuctionOutputDTO fromModel(Auction auction){
        ModelMapper mapper = new ModelMapper();
        AuctionOutputDTO outputDTO = mapper.map(auction, AuctionOutputDTO.class);
        outputDTO.setCreatedAt(auction.getCreatedAt().toString());
        outputDTO.setUser(UserOutputDTO.fromModel(auction.getUser()));
        outputDTO.setDone(auction.isFinished());
        return outputDTO;
    }

    public void searchForBookmarked(User user) {
        for (Auction userAuction : user.getAuctions())
            if (this.isBookmarkedByUser(userAuction)) {
                this.setForUser(true);
                break;
            }
    }

    private boolean isBookmarkedByUser(Auction userAuction){

            return userAuction.getId().equals(this.getId());

    }
}
