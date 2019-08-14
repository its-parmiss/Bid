package rahnema.tumaj.bid.backend.controllers;

import org.springframework.data.domain.Page;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rahnema.tumaj.bid.backend.domains.Image.ImageInputDTO;
import rahnema.tumaj.bid.backend.domains.auction.AuctionInputDTO;
import rahnema.tumaj.bid.backend.domains.auction.AuctionListDTO;
import rahnema.tumaj.bid.backend.domains.auction.AuctionOutputDTO;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.models.Category;
import rahnema.tumaj.bid.backend.models.Images;
import rahnema.tumaj.bid.backend.models.User;
import rahnema.tumaj.bid.backend.services.Images.ImageService;
import rahnema.tumaj.bid.backend.services.auction.AuctionService;
import rahnema.tumaj.bid.backend.services.category.CategoryService;
import rahnema.tumaj.bid.backend.services.user.UserService;
import rahnema.tumaj.bid.backend.storage.StorageService;
import rahnema.tumaj.bid.backend.utils.athentication.TokenUtil;
import rahnema.tumaj.bid.backend.utils.assemblers.AuctionAssemler;
import rahnema.tumaj.bid.backend.utils.assemblers.CategoryAssembler;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.AuctionNotFoundException;
import rahnema.tumaj.bid.backend.utils.exceptions.IllegalInputExceptions.IllegalAuctionInputException;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.CategoryNotFoundException;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.TokenNotFoundException;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.UserNotFoundException;

import java.util.*;
import java.util.stream.Collectors;


import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class AuctionController {
    private final StorageService storageService;
    private final ImageService imageService;
    private final AuctionService service;
    private final AuctionAssemler assembler;

    private final UserService userService;
    private final CategoryAssembler categoryAssembler;

    public AuctionController(CategoryAssembler categoryAssembler, StorageService storageService, ImageService imageService, AuctionService service, AuctionAssemler assembler, UserService userService) {
        this.categoryAssembler = categoryAssembler;
        this.storageService = storageService;
        this.imageService = imageService;
        this.service = service;
        this.assembler = assembler;
        this.userService = userService;
    }

    @PostMapping("/auctions")
    public Resource<AuctionOutputDTO> addAuction(@RequestBody AuctionInputDTO auctionInput, @RequestHeader("Authorization") String token) {
        if (isAuctionValid(auctionInput)) {
            User user = this.userService.getUserWithToken(token);
            return passAuctionToService(auctionInput, user);
        } else
            throw new IllegalAuctionInputException();

    }

    private Resource<AuctionOutputDTO> passAuctionToService(@RequestBody AuctionInputDTO auctionInput, User user) {
        auctionInput.setUser(user);
        Auction addedAuction = service.addAuction(auctionInput);

        return assembler.assemble(addedAuction);
    }

    private void getImagesAsSet(@RequestBody AuctionInputDTO auctionInput, Set<Images> images) {
        if (auctionInput.getImageUrls() != null)
            for (String url : auctionInput.getImageUrls()) {
                ImageInputDTO imageInputDTO = new ImageInputDTO();
                imageInputDTO.setUrl(url);
                Images img = imageService.addOne(imageInputDTO);
                images.add(img);
            }
    }

    @GetMapping("/auctions")
    public Resource<AuctionListDTO> getAll(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer limit, @RequestHeader("Authorization") String token, @RequestParam(required = false) String title, @RequestParam(required = false) Long categoryId) {
        User user = userService.getUserWithToken(token);
        page = defaultPage(page);
        limit = defaultLimit(limit);
        return evaluateAuctionsRequest(page, limit, title, categoryId, user);
    }

    private Resource<AuctionListDTO> evaluateAuctionsRequest(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer limit, @RequestParam(required = false) String title, @RequestParam(required = false) Long categoryId, User user) {
        if (categoryId == null && title == null)
            return getHottest(page, limit, user);
        else if (categoryId == null)
            return find(page, limit, title, user);
        else if (title == null)
            return filter(page, limit,categoryId, user);
        else
            return findByFilterAndCategory(title, categoryId, page, limit, user);
    }

    private Resource<AuctionListDTO> getHottest(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer limit, User user) {

        return getAuctionsWithPage(page, limit, user);
    }

    private Resource<AuctionListDTO> getAuctionsWithPage(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer limit, User user) {

        Page<Auction> auctionPage = service.getAll(page, limit);
        return getAuctionListDTOResource(user, auctionPage, page, limit);
    }

    private AuctionListDTO getAuctionListDTO(Page<Auction> auctionPage, List<Resource<AuctionOutputDTO>> auctions) {
        AuctionListDTO auctionListDTO = new AuctionListDTO();
        auctionListDTO.setList(auctions);
        auctionListDTO.setLastPage(auctionPage.getTotalPages() - 1);
        return auctionListDTO;
    }


    private void evaluateBookmarkedAuctions(User user, List<Resource<AuctionOutputDTO>> auctions) {
        for (Resource<AuctionOutputDTO> resource : auctions) {
            AuctionOutputDTO dto = resource.getContent();
            dto.searchForBookmarked(user);
        }
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



    @GetMapping("/auctions/{id}")
    public Resource<AuctionOutputDTO> getOne(@PathVariable Long id) {
        Optional<Auction> auctionOptional = service.getOne(id);
        Auction auction = auctionOptional.orElseThrow(() -> new AuctionNotFoundException(id));
        return this.assembler.assemble(auction);
    }


    private Resource<AuctionListDTO> findByFilterAndCategory(String title, Long categoryId, Integer page, Integer limit, User user) {
        Page<Auction> auctionPage = service.findByTitleAndCategory(title, categoryId, page,limit);
        return getAuctionListDTOResource(user, auctionPage, null, null);
    }

    private Resource<AuctionListDTO> find(Integer page, Integer limit, String title, User user) {
        page = defaultPage(page);
        limit = defaultLimit(limit);
        return CollectFoundAuctions(title, page, limit,user);
    }


    private Resource<AuctionListDTO> filter(Integer page, Integer limit,Long id, User user) {
        Page<Auction> auctionPage = service.findByCategory(id, page,limit);
        return getAuctionListDTOResource(user, auctionPage, null, null);
    }

    private Resource<AuctionListDTO> CollectFoundAuctions(String title, Integer page, Integer limit, User user) {
        Page<Auction> auctionPage = service.findByTitle(title,page, limit);
        return getAuctionListDTOResource(user, auctionPage, null, null);

    }

    private Resource<AuctionListDTO> getAuctionListDTOResource(User user, Page<Auction> auctionPage, Integer page, Integer limit) {
        List<Resource<AuctionOutputDTO>> auctions = auctionPage.stream()
                .map(this.assembler::assemble)
                .collect(Collectors.toList());
        evaluateBookmarkedAuctions(user, auctions);
        AuctionListDTO auctionListDTO = getAuctionListDTO(auctionPage, auctions);
        return new Resource<>(auctionListDTO, linkTo(methodOn(AuctionController.class).getAll(page, limit, null, null, null)).withSelfRel());
    }

    // TODO: auctions validations
    private boolean isAuctionValid(AuctionInputDTO auction) {
//        boolean isBaseValid = auction.getBase_price()!= null && auction.getBase_price() >= 1e3 &&  auction.getBase_price() <= 1e12;
//        boolean isDescValid = auction.getDescription().length() < 100;
//        boolean isTitleValid = auction.getTitle()!=null && auction.getTitle().length() >= 6 && auction.getTitle().length() <= 36;
//        boolean isStartValid = auction.getStartDate()!= null;
//        boolean isBiddersValid = auction.getActive_bidders_limit()<=50 && auction.getActive_bidders_limit()>= 2;
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

    @PostMapping("/auctions/upload")
    public String handleFileUpload(@RequestBody MultipartFile file) {
        String name = storageService.store(file, "auctionPicture");
        org.springframework.core.io.Resource tempFile = storageService.loadAsResource(name, "auctionPicture");
        return name;
    }
}

