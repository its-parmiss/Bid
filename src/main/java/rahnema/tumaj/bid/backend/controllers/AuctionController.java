package rahnema.tumaj.bid.backend.controllers;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.*;
import rahnema.tumaj.bid.backend.domains.auction.AuctionInputDTO;
import rahnema.tumaj.bid.backend.domains.auction.AuctionOutputDTO;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.services.auction.AuctionService;
import rahnema.tumaj.bid.backend.utils.assemblers.AuctionAssemler;
import rahnema.tumaj.bid.backend.utils.exceptions.AuctionNotFoundException;
import rahnema.tumaj.bid.backend.utils.exceptions.IllegalAuctionInputException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class AuctionController {

    private final AuctionService service;
    private final AuctionAssemler assembler;

    public AuctionController(AuctionService service, AuctionAssemler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @PostMapping("/auctions")
    public Resource<AuctionOutputDTO> addAuction (@RequestBody AuctionInputDTO auctionInput){
        if (!isAuctionValid(auctionInput))
            return passAuctionToService(auctionInput);
        else
            throw new IllegalAuctionInputException();

    }

    private Resource<AuctionOutputDTO> passAuctionToService(@RequestBody AuctionInputDTO auctionInput) {
        Auction auction = auctionInput.toModel();
        Auction addedAuction = service.addAuction(auction);
        return assembler.assemble(addedAuction);
    }

    @GetMapping("/auctions")
    public Resources<Resource<AuctionOutputDTO>> getAll (){
        List<Resource<AuctionOutputDTO>> auctions = collectAllAuctions();
        return new Resources<>(auctions, linkTo(methodOn(AuctionController.class).getAll()).withSelfRel());
    }

    private List<Resource<AuctionOutputDTO>> collectAllAuctions() {
        return service.getAll().stream()
                .map(this.assembler::assemble)
                .collect(Collectors.toList());
    }

    @GetMapping("/auctions/{id}")
    public Resource<AuctionOutputDTO> getOne (@PathVariable Long id){
        Optional<Auction> auctionOptional = service.getOne(id);
        Auction auction = auctionOptional.orElseThrow( ()  -> new AuctionNotFoundException(id));
        return this.assembler.assemble(auction);
    }

    private boolean isAuctionValid(AuctionInputDTO auction){
        return true;
    }


}
