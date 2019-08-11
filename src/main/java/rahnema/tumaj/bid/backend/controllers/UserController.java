package rahnema.tumaj.bid.backend.controllers;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import rahnema.tumaj.bid.backend.domains.auction.AuctionOutputDTO;
import rahnema.tumaj.bid.backend.models.User;
import rahnema.tumaj.bid.backend.services.Bookmarks.BookmarksService;
import rahnema.tumaj.bid.backend.services.user.UserService;
import rahnema.tumaj.bid.backend.utils.athentication.TokenUtil;
import rahnema.tumaj.bid.backend.utils.assemblers.AuctionAssemler;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.TokenNotFoundException;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.UserNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class UserController {
    private final UserService userService;
    private final AuctionAssemler assembler;
    private final BookmarksService bookmarksService;

    public UserController(UserService userService,
                          AuctionAssemler assembler,
                          BookmarksService bookmarksService,
                          TokenUtil tokenUtil) {
        this.userService = userService;
        this.assembler = assembler;
        this.bookmarksService = bookmarksService;
    }

    @GetMapping("/user/my-auctions")

    public Resources<Resource<AuctionOutputDTO>> getAllAuctions(@RequestHeader("Authorization") String token) {
        User user = userService.getUserWithToken(token);

        List<Resource<AuctionOutputDTO>> auctions = user.getMyAuctions().stream()
                .map(assembler::assemble)
                .collect(Collectors.toList());
        return new Resources<>(
                auctions,
                linkTo(methodOn(AuctionController.class).getAll(null, null, null,null,null)).withSelfRel()
        );
    }

    @GetMapping("/user/auctions")

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
                .map(this.assembler::assemble)
                .collect(Collectors.toList());
    }
}
