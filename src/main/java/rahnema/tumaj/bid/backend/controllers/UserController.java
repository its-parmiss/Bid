package rahnema.tumaj.bid.backend.controllers;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import rahnema.tumaj.bid.backend.domains.auction.AuctionOutputDTO;
import rahnema.tumaj.bid.backend.models.User;
import rahnema.tumaj.bid.backend.services.Bookmarks.BookmarksService;
import rahnema.tumaj.bid.backend.services.user.UserService;
import rahnema.tumaj.bid.backend.utils.assemblers.AuctionAssemler;
import rahnema.tumaj.bid.backend.utils.exceptions.UserNotFoundException;

import java.util.HashMap;
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
                          BookmarksService bookmarksService) {
        this.userService = userService;
        this.assembler = assembler;
        this.bookmarksService = bookmarksService;
    }

    @GetMapping("/users/{id}/my-auctions")
    public Resources<Resource<AuctionOutputDTO>> getAllAuctions(@PathVariable Long id) {
        User user = userService.getOne(id).orElseThrow(() -> new UserNotFoundException(id));
        List<Resource<AuctionOutputDTO>> auctions = user.getMyAuctions().stream()
                .map(assembler::assemble)
                .collect(Collectors.toList());
        return new Resources<>(
                auctions,
                linkTo(methodOn(AuctionController.class).getAll(null, null, new HttpHeaders())).withSelfRel()
        );
    }
    @GetMapping("/users/{id}/auctions")
    public Resources<Resource<AuctionOutputDTO>> getAll(@PathVariable(required = false) Long id) {
        List<Resource<AuctionOutputDTO>> auctions = collectAllBookmarks(id);
        return new Resources<>(auctions ,linkTo(methodOn(UserController.class).getAll(id)).withSelfRel());
    }

    private List<Resource<AuctionOutputDTO>> collectAllBookmarks(Long id) {
        return bookmarksService.getAll(id).stream()
                .map(this.assembler::assemble)
                .collect(Collectors.toList());

    }
}
