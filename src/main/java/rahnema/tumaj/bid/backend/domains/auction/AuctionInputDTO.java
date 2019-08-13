package rahnema.tumaj.bid.backend.domains.auction;

import lombok.Data;
import org.modelmapper.ModelMapper;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.models.Category;
import rahnema.tumaj.bid.backend.models.Images;
import rahnema.tumaj.bid.backend.models.User;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class AuctionInputDTO {

    private String title;
    private String description;
    private Date startDate;
    private Long lastBid;
    private int activeBiddersLimit;
    private Long categoryId;
    private User user;
    private Long basePrice;
    private List<String> imageUrls;

    public Auction toModel() {

        ModelMapper mapper = new ModelMapper();
        Auction auction = mapper.map(this, Auction.class);
        Set<Images> images = new HashSet<>();
        if (imageUrls != null)
            for (String url : imageUrls) {
                Images image = new Images();
                image.setUrl(url);
                image.setAuction(auction);
                images.add(image);
            }
        auction.setImages(images);
        return auction;
    }
}
