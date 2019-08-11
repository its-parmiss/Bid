package rahnema.tumaj.bid.backend.controllers;


import org.springframework.web.bind.annotation.*;
import rahnema.tumaj.bid.backend.models.User;
import rahnema.tumaj.bid.backend.services.Bookmarks.BookmarksService;
import rahnema.tumaj.bid.backend.services.user.UserService;
import rahnema.tumaj.bid.backend.utils.athentication.TokenUtil;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.TokenNotFoundException;

@RestController
public class BookmarkingController {

    private final BookmarksService bookmarksService;
    private final TokenUtil tokenUtil;
    private final UserService userService;

    public BookmarkingController(BookmarksService bookmarksService, TokenUtil tokenUtil, UserService userService) {
        this.bookmarksService = bookmarksService;
        this.tokenUtil = tokenUtil;
        this.userService = userService;
    }

    @PostMapping("/auctions/bookmark")

    public void bookmarkAuction(@RequestHeader("Authorization") String token,
                                @RequestParam("auctionId") Long auctionId) {
        User user = userService.getUserWithToken(token);
        bookmarksService.bookmarkAuction(auctionId, user);
    }

}
