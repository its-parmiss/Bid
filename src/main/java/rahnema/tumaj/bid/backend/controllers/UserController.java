package rahnema.tumaj.bid.backend.controllers;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import rahnema.tumaj.bid.backend.domains.auction.AuctionOutputDTO;
import rahnema.tumaj.bid.backend.domains.user.ProfileOutputDTO;
import rahnema.tumaj.bid.backend.domains.user.UserOutputDTO;
import rahnema.tumaj.bid.backend.models.User;
import rahnema.tumaj.bid.backend.services.Bookmarks.BookmarksService;
import rahnema.tumaj.bid.backend.services.user.UserService;
import rahnema.tumaj.bid.backend.utils.assemblers.UserResourceAssembler;
import rahnema.tumaj.bid.backend.utils.athentication.TokenUtil;
import rahnema.tumaj.bid.backend.utils.assemblers.AuctionAssembler;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class UserController {
    private final UserService userService;
    private final AuctionAssembler auctionAssembler;
    private final UserResourceAssembler userAssembler;
    private final BookmarksService bookmarksService;

    public UserController(UserService userService,
                          AuctionAssembler auctionAssembler,
                          BookmarksService bookmarksService,
                          TokenUtil tokenUtil,
                          UserResourceAssembler userAssembler) {
        this.userService = userService;
        this.auctionAssembler = auctionAssembler;
        this.bookmarksService = bookmarksService;
        this.userAssembler = userAssembler;
    }

    @GetMapping("/profile")

    public Resource<ProfileOutputDTO> getProfile(@RequestHeader("Authorization") String token) {
        ProfileOutputDTO outputDTO = new ProfileOutputDTO();
        extractFieldsAndSetProfile(token, outputDTO);
        return getProfileOutputDTOResource(token, outputDTO);
    }

    private void extractFieldsAndSetProfile(@RequestHeader("Authorization") String token, ProfileOutputDTO outputDTO) {
        User user = userService.getUserWithToken(token);
        List<Resource<AuctionOutputDTO>> myAuctions = collectMyAuctions(user);
        List<Resource<AuctionOutputDTO>> bookmarked = collectAllBookmarks(user);

        setProfileFields(user, outputDTO, myAuctions, bookmarked);
    }

    private Resource<ProfileOutputDTO> getProfileOutputDTOResource(@RequestHeader("Authorization") String token, ProfileOutputDTO outputDTO) {
        return new Resource<>(
                outputDTO,
                linkTo(methodOn(UserController.class).getProfile(token)).withSelfRel()
        );
    }

    private void setProfileFields(User user, ProfileOutputDTO outputDTO, List<Resource<AuctionOutputDTO>> myAuctions, List<Resource<AuctionOutputDTO>> bookmarked) {
        outputDTO.setMe( this.userAssembler.toResource(UserOutputDTO.fromModel(user)) );
        outputDTO.setBookmarked(bookmarked);
        outputDTO.setMyAuctions(myAuctions);
    }

    private List<Resource<AuctionOutputDTO>> collectMyAuctions(User user) {
        return user.getMyAuctions().stream()
                .map(this.auctionAssembler::assemble)
                .collect(Collectors.toList());
    }

    @GetMapping("/user/bookmarked")

    public Resources<Resource<AuctionOutputDTO>> getAll(@RequestHeader("Authorization") String token) {
        User user = userService.getUserWithToken(token);
        List<Resource<AuctionOutputDTO>> auctions = collectAllBookmarks(user);
        return new Resources<>(
                auctions,
                linkTo(methodOn(UserController.class).getAll(token)).withSelfRel()
        );
    }

    private List<Resource<AuctionOutputDTO>> collectAllBookmarks(User user) {
        return bookmarksService.getAll(user).stream()
                .map(this.auctionAssembler::assemble)
                .collect(Collectors.toList());
    }
}
