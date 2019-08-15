package rahnema.tumaj.bid.backend.domains.auction;

import lombok.Data;
import org.modelmapper.ModelMapper;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.models.Category;
import rahnema.tumaj.bid.backend.models.Images;
import rahnema.tumaj.bid.backend.models.User;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class AuctionInputDTO {

    private String title;
    private String description;
    private String Stringstrt;
    private Long lastBid;
    private int activeBiddersLimit;
    private Long categoryId;
    private User user;
    private Long basePrice;
    private List<String> imageUrls;

    public Auction toModel() {
        ModelMapper mapper = new ModelMapper();

        Auction auction = mapper.map(this, Auction.class);
        try {
            auction.setStartDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(this.getStringstrt()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println("\n\n\n" + auction.getStartDate() + "\n\n\n");

      /*  try {
            auction.setStartDate((new SimpleDateFormat()).parse(this.getAaa()));
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
        return auction;

    }
}
