package rahnema.tumaj.bid.backend.utils.assemblers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import rahnema.tumaj.bid.backend.controllers.AuctionController;
import rahnema.tumaj.bid.backend.domains.auction.AuctionOutputDTO;
import rahnema.tumaj.bid.backend.models.Auction;

import java.util.HashMap;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
@Component
public class AuctionAssemler {
    private static final Integer defaultPage = 0;
    private static final Integer defaultPageLimit = 20;

    @Autowired
    UserResourceAssembler assembler;

    public Resource<AuctionOutputDTO> assemble(Auction auction) {
//        assembler.toResource()
        return new Resource<>(AuctionOutputDTO.fromModel(auction),
                linkTo(methodOn(AuctionController.class).getOne(auction.getId())).withSelfRel(),
                linkTo(methodOn(AuctionController.class).getAll(defaultPage, defaultPageLimit, new HttpHeaders())).withRel("all")
        );
    }
}
