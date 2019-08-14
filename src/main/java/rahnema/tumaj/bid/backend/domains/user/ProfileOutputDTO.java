package rahnema.tumaj.bid.backend.domains.user;

import lombok.Data;
import org.springframework.hateoas.Resource;
import rahnema.tumaj.bid.backend.domains.auction.AuctionOutputDTO;

import java.util.List;


@Data
public class ProfileOutputDTO {

    List<Resource<AuctionOutputDTO>> bookmarked;
    List<Resource<AuctionOutputDTO>> myAuctions;
    Resource<UserOutputDTO> me;

}
