package rahnema.tumaj.bid.backend.controllers;


import org.quartz.*;
import org.springframework.web.bind.annotation.*;

import rahnema.tumaj.bid.backend.models.User;
import rahnema.tumaj.bid.backend.services.Bookmarks.BookmarksService;
import rahnema.tumaj.bid.backend.services.auction.AuctionService;
import rahnema.tumaj.bid.backend.services.user.UserService;

import java.util.Map;

@RestController
public class BookmarkingController {

    private final BookmarksService bookmarksService;
    private final UserService userService;
    private final AuctionService auctionService;
    private final Scheduler scheduler;

    public BookmarkingController(
            BookmarksService bookmarksService,
            UserService userService,
            AuctionService auctionService,
            Scheduler scheduler) {

        this.bookmarksService = bookmarksService;
        this.userService = userService;
        this.auctionService = auctionService;
        this.scheduler = scheduler;
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
