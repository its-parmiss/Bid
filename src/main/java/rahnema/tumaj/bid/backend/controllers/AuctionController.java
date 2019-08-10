package rahnema.tumaj.bid.backend.controllers;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rahnema.tumaj.bid.backend.domains.Image.ImageInputDTO;
import rahnema.tumaj.bid.backend.domains.auction.AuctionInputDTO;
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
import rahnema.tumaj.bid.backend.utils.TokenUtil;
import rahnema.tumaj.bid.backend.utils.assemblers.AuctionAssemler;
import rahnema.tumaj.bid.backend.utils.assemblers.CategoryAssembler;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.AuctionNotFoundException;
import rahnema.tumaj.bid.backend.utils.exceptions.IllegalInputExceptions.IllegalAuctionInputException;
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
    private final CategoryAssembler categoryAssembler;
    private final CategoryService categoryService;
    private final UserService userService;


    private final TokenUtil tokenUtil;

    public AuctionController(CategoryService categoryService,CategoryAssembler categoryAssembler,StorageService storageService, ImageService imageService, AuctionService service, AuctionAssemler assembler, UserService userService, TokenUtil tokenUtil) {
        this.storageService = storageService;
        this.imageService = imageService;
        this.service = service;
        this.assembler = assembler;
        this.userService = userService;
        this.tokenUtil = tokenUtil;
        this.categoryAssembler=categoryAssembler;
        this.categoryService=categoryService;
    }

    @PostMapping("/auctions")
    public Resource<AuctionOutputDTO> addAuction(@RequestBody AuctionInputDTO auctionInput) {
        if (isAuctionValid(auctionInput)){
            return passAuctionToService(auctionInput);
        }
        else
            throw new IllegalAuctionInputException();

    }

    private Resource<AuctionOutputDTO> passAuctionToService(@RequestBody AuctionInputDTO auctionInput) {
        Auction addedAuction = service.addAuction(auctionInput);
        Set<Images> images=new HashSet<>();
            for(String url:auctionInput.getImageUrls()){
              ImageInputDTO imageInputDTO=new ImageInputDTO();
              imageInputDTO.setUrl(url);
              Images img=imageService.addOne(imageInputDTO);
              images.add(img);
        }
        addedAuction.setImages(images);
        return assembler.assemble(addedAuction);
    }
    
    @GetMapping("/auctions")
    public Resources<Resource<AuctionOutputDTO>> getAll(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer limit, @RequestHeader("Authorization") String token,@RequestParam(required = false)String title,@RequestParam(required = false)Long categoryId) {
        if(categoryId!=null){
            return filter(categoryId);
        }else if(title!=null){
            return find(page,limit,title);
        }
        else {
            User user = getUserWithToken(token);
            page = defaultPage(page);
            limit = defaultLimit(limit);
            return getAuctionsWithPage(page, limit, user);
        }
    }
    private Resources<Resource<AuctionOutputDTO>> getAuctionsWithPage(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer limit, User user) {
        List<Resource<AuctionOutputDTO>> auctions = collectAllAuctions(page, limit);
        evaluateBookmarkedAuctions(user, auctions);
        return new Resources<>(auctions, linkTo(methodOn(AuctionController.class).getAll(page, limit, null,null,null )).withSelfRel());
    }

    private User getUserWithToken(String token) {
        String email = tokenUtil
                .getUsernameFromToken(token.split(" ")[1])
                .orElseThrow(TokenNotFoundException::new);
       return userService.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
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

//    @GetMapping("/auctions/find")
    public Resources<Resource<AuctionOutputDTO>> find(Integer page,  Integer limit, String title) {
        page = defaultPage(page);
        limit = defaultLimit(limit);
        List<Resource<AuctionOutputDTO>> auctions = CollectFoundAuctions(title, page, limit);
        return new Resources<>(auctions, linkTo(methodOn(AuctionController.class).getAll(page, limit,null,title,null)).withSelfRel());
    }

    public Resources<Resource<AuctionOutputDTO>> filter( Long id) {
        Category category = categoryService.findById(id).get();
        List<Auction> auctions = new ArrayList<>(category.getAuctions());
        System.out.println("auctions.size = " + auctions.size());
        for (Auction a : auctions) {
            System.out.println("a.getTitle() = " + a.getTitle());
        }
        List<Resource<AuctionOutputDTO>> auctionlists = auctions.stream()
                .map(this.assembler::assemble)
                .collect(Collectors.toList());
        return new Resources<>(auctionlists, linkTo(methodOn(AuctionController.class).getAll(null,null,null,null,id)).withSelfRel());

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
