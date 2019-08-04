package rahnema.tumaj.bid.backend.controllers;

import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.*;
import rahnema.tumaj.bid.backend.domains.AuctionInputDTO;
import rahnema.tumaj.bid.backend.domains.AuctionOutputDTO;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.services.AuctionService;
import rahnema.tumaj.bid.backend.utils.assemblers.AuctionAssemler;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

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
        Auction auction = auctionInput.toModel();
        Auction addedAuction = service.addAuction(auction);
        return assembler.assemble(addedAuction);
    }

    @GetMapping("/auctions")
    public Resource<AuctionOutputDTO> getAll (){
            return null;
    }
    @GetMapping("/auctions/{id}")
    public Resource<AuctionOutputDTO> getOne (@PathVariable Long id){
        Optional<Auction> auctionOptional = service.getOne(id);
        Auction auction = auctionOptional.orElseThrow( ()  -> new EntityNotFoundException(id.toString()));
        return this.assembler.assemble(auction);
    }


}
