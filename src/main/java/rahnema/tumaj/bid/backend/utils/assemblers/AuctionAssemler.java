package rahnema.tumaj.bid.backend.utils.assemblers;

import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Component;
import rahnema.tumaj.bid.backend.controllers.auction.AuctionController;
import rahnema.tumaj.bid.backend.domains.auction.AuctionOutputDTO;
import rahnema.tumaj.bid.backend.models.Auction;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
@Component
public class AuctionAssemler {
    public Resource<AuctionOutputDTO> assemble(Auction auction) {
        return new Resource<>(AuctionOutputDTO.fromModel(auction),
                linkTo(methodOn(AuctionController.class).getOne(auction.getId())).withSelfRel(),
                linkTo(methodOn(AuctionController.class).getAll()).withRel("all")
        );
    }
}
