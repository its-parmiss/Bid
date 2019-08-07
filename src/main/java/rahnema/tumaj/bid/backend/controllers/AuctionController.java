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
import rahnema.tumaj.bid.backend.domains.user.UserOutputDTO;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.models.User;
import rahnema.tumaj.bid.backend.services.auction.AuctionService;
import rahnema.tumaj.bid.backend.services.user.UserService;
import rahnema.tumaj.bid.backend.storage.StorageProperties;
import rahnema.tumaj.bid.backend.storage.StorageService;
import rahnema.tumaj.bid.backend.utils.assemblers.AuctionAssemler;
import rahnema.tumaj.bid.backend.utils.exceptions.AuctionNotFoundException;
import rahnema.tumaj.bid.backend.utils.exceptions.IllegalAuctionInputException;
import rahnema.tumaj.bid.backend.utils.exceptions.UserNotFoundException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class AuctionController {
    private final StorageService storageService;

    private final AuctionService service;
    private final AuctionAssemler assembler;

    private final UserService userService;

    public AuctionController(StorageService storageService, AuctionService service, AuctionAssemler assembler, UserService userService) {
        this.storageService = storageService;
        this.service = service;
        this.assembler = assembler;
        this.userService = userService;
    }


    @PostMapping("/auctions")

    public Resource<AuctionOutputDTO> addAuction(@RequestBody AuctionInputDTO auctionInput) {
        if (isAuctionValid(auctionInput))
            return passAuctionToService(auctionInput);
        else
            throw new IllegalAuctionInputException();

    }

    private Resource<AuctionOutputDTO> passAuctionToService(@RequestBody AuctionInputDTO auctionInput) {

        Auction addedAuction = service.addAuction(auctionInput);
        return assembler.assemble(addedAuction);
    }




    @GetMapping("/auctions")
    public Resources<Resource<AuctionOutputDTO>> getAll(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer limit, @RequestHeader HttpHeaders headers) {

        User user = getUserWithId(headers);
        page = defaultPage(page);
        limit = defaultLimit(limit);
        return getAuctionsWithPage(page, limit, user);
    }

    private String getAuthorization(@RequestHeader HttpHeaders headers) {
        List<String> authList = headers.get(HttpHeaders.AUTHORIZATION);
        if (authList != null)
            return authList.get(0);
        else
            throw new UserNotFoundException("-1");
    }

    private Resources<Resource<AuctionOutputDTO>> getAuctionsWithPage(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer limit, User user) {
        List<Resource<AuctionOutputDTO>> auctions = collectAllAuctions(page, limit);
        evaluateBookmarkedAuctions(user, auctions);
        return new Resources<>(auctions, linkTo(methodOn(AuctionController.class).getAll(page, limit, new HttpHeaders()/*params*/)).withSelfRel());
    }

    private User getUserWithId(HttpHeaders headers) {
        Long token = Long.valueOf(getAuthorization(headers));
        return userService.getOne(token).orElseThrow(() -> new UserNotFoundException(token));
    }

    private void evaluateBookmarkedAuctions(User user, List<Resource<AuctionOutputDTO>> auctions) {
        for (Resource<AuctionOutputDTO> resource : auctions) {
            AuctionOutputDTO dto = resource.getContent();
            searchForBookmarked(user, dto);
        }
    }

    private void searchForBookmarked(User user, AuctionOutputDTO dto) {
        for (Auction userAuction : user.getAuctions())
            if (isBookmarked(dto, userAuction)) {
                dto.set_for_user(true);
                break;
            }
    }

    private boolean isBookmarked(AuctionOutputDTO dto, Auction userAuction) {
        return userAuction.getId().equals(dto.getId());
    }

    private Integer defaultPage(@RequestParam(required = false) Integer page) {
        if (page == null)
            page = 0;
        return page;
    }

    private Integer defaultLimit(@RequestParam(required = false) Integer limit) {
        if (limit == null)
            limit = 10;
        return limit;
    }

    private List<Resource<AuctionOutputDTO>> collectAllAuctions(Integer page, Integer limit) {
        return service.getAll(page, limit).stream()
                .map(this.assembler::assemble)
                .collect(Collectors.toList());
    }

    @GetMapping("/auctions/{id}")
    public Resource<AuctionOutputDTO> getOne(@PathVariable Long id) {
        Optional<Auction> auctionOptional = service.getOne(id);
        Auction auction = auctionOptional.orElseThrow(() -> new AuctionNotFoundException(id));
        return this.assembler.assemble(auction);
    }

    @GetMapping("/auctions/find")
    public Resources<Resource<AuctionOutputDTO>> find(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer limit, @RequestParam String title) {
        page = defaultPage(page);
        limit = defaultLimit(limit);
        List<Resource<AuctionOutputDTO>> auctions = CollectFoundAuctions(title, page, limit);
        return new Resources<>(auctions, linkTo(methodOn(AuctionController.class).find(page, limit, title)).withSelfRel());
    }

    private List<Resource<AuctionOutputDTO>> CollectFoundAuctions(String title, Integer page, Integer limit) {
        return service.findByTitle(title, page, limit).stream()
                .map(this.assembler::assemble)
                .collect(Collectors.toList());
    }

    private boolean isAuctionValid(AuctionInputDTO auction) {
//        boolean isBaseValid = auction.getBase_price()!= null && auction.getBase_price() >= 1e3 &&  auction.getBase_price() <= 1e12;
//        boolean isDescValid = auction.getDescription().length() < 100;
//        boolean isTitleValid = auction.getTitle()!=null && auction.getTitle().length() >= 6 && auction.getTitle().length() <= 36;
//        boolean isStartValid = auction.getStartDate()!= null;
//        boolean isBiddersValid = auction.getActive_bidders_limit()<=50 && auction.getActive_bidders_limit()>= 2;
//
//
//        return isBaseValid && isBiddersValid && isDescValid && isTitleValid && isStartValid;
        return true;

    }

    @GetMapping("/auctions/images/{filename:.+}")
    @ResponseBody
    public ResponseEntity<org.springframework.core.io.Resource> serveFile(@PathVariable String filename) {
        org.springframework.core.io.Resource file = storageService.loadAsResource(filename, "auctionPicture");
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/upload/auctionImage")
    public ResponseEntity<org.springframework.core.io.Resource> handleFileUpload(@RequestBody MultipartFile file) {
        String name = storageService.store(file, "auctionPicture");
        org.springframework.core.io.Resource tempFile = storageService.loadAsResource(name, "auctionPicture");
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + tempFile.getFilename() + "\"").body(tempFile);
    }


}
