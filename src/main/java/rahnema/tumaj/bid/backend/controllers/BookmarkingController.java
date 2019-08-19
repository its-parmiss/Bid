package rahnema.tumaj.bid.backend.controllers;


import org.springframework.web.bind.annotation.*;
import rahnema.tumaj.bid.backend.models.User;
import rahnema.tumaj.bid.backend.services.Bookmarks.BookmarksService;
import rahnema.tumaj.bid.backend.services.user.UserService;
import rahnema.tumaj.bid.backend.utils.athentication.TokenUtil;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.TokenNotFoundException;

import java.util.Map;

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
                                @RequestBody Map<String, String> params) {

        String auctionId = params.get("auctionId");
        User user = userService.getUserWithToken(token);
        bookmarksService.bookmarkAuction(Long.valueOf(auctionId), user);
    }
    @PostMapping("/auctions/unbookmark")

    public void unbookmarkAuction(@RequestHeader("Authorization") String token,
                                @RequestBody Map<String, String> params) {

        String auctionId = params.get("auctionId");
        User user = userService.getUserWithToken(token);
        bookmarksService.unbookmarkAuction(Long.valueOf(auctionId), user);
    }

}
