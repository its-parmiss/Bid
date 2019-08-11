package rahnema.tumaj.bid.backend.domains.auction;

import lombok.Data;
import org.springframework.hateoas.Resource;
import rahnema.tumaj.bid.backend.services.auction.AuctionService;

import java.util.List;

@Data
public class AuctionListDTO {

    private List<Resource<AuctionOutputDTO>> list;

    private Integer lastPage;
}
