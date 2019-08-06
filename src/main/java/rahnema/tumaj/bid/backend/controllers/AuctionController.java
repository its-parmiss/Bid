package rahnema.tumaj.bid.backend.controllers;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import rahnema.tumaj.bid.backend.domains.auction.AuctionInputDTO;
import rahnema.tumaj.bid.backend.domains.auction.AuctionOutputDTO;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.services.auction.AuctionService;
import rahnema.tumaj.bid.backend.storage.StorageProperties;
import rahnema.tumaj.bid.backend.storage.StorageService;
import rahnema.tumaj.bid.backend.utils.assemblers.AuctionAssemler;
import rahnema.tumaj.bid.backend.utils.exceptions.AuctionNotFoundException;
import rahnema.tumaj.bid.backend.utils.exceptions.IllegalAuctionInputException;
import sun.text.resources.FormatData;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class AuctionController {
    private final StorageService storageService;

    private final AuctionService service;
    private final AuctionAssemler assembler;

    public AuctionController(AuctionService service, AuctionAssemler assembler, StorageService storageService) {
        this.service = service;
        this.assembler = assembler;
        this.storageService = storageService;
    }

    @PostMapping("/auctions")
    public Resource<AuctionOutputDTO> addAuction (@RequestBody AuctionInputDTO auctionInput){
        if (isAuctionValid(auctionInput))
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
    public Resources<Resource<AuctionOutputDTO>> getAll (@RequestParam (required = false)Integer page ,@RequestParam (required = false) Integer limit){
        if(page == null)
            page = 0;
        if (limit == null)
            limit = 10;
        List<Resource<AuctionOutputDTO>> auctions = collectAllAuctions(page, limit);
        return new Resources<>(auctions, linkTo(methodOn(AuctionController.class).getAll(page, limit)).withSelfRel());
    }

    private List<Resource<AuctionOutputDTO>> collectAllAuctions(Integer page, Integer limit) {
        return service.getAll(page, limit).stream()
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

    @GetMapping("/auctions/images/{filename:.+}")
    @ResponseBody
    public ResponseEntity<org.springframework.core.io.Resource> serveFile(@PathVariable String filename) {

        org.springframework.core.io.Resource file = storageService.loadAsResource(filename,"auctionPicture");
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/upload/auctionImage")
    public ResponseEntity<org.springframework.core.io.Resource> handleFileUpload(@RequestBody MultipartFile file) {
        storageService.store(file,"auctionPicture");

        org.springframework.core.io.Resource tempFile = storageService.loadAsResource( StringUtils.cleanPath(file.getOriginalFilename()),"auctionPicture");
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + tempFile.getFilename() + "\"").body(tempFile);


    }






}
