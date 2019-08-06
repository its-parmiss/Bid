package rahnema.tumaj.bid.backend.controllers;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rahnema.tumaj.bid.backend.models.User;
import rahnema.tumaj.bid.backend.services.Bookmarks.BookmarksService;
import rahnema.tumaj.bid.backend.utils.exceptions.IllegalUserInputException;
import rahnema.tumaj.bid.backend.utils.exceptions.TokenNotFoundException;

import java.util.Map;

@RestController
public class BookmarkingController {

    private final BookmarksService bookmarksService;

    public BookmarkingController(BookmarksService bookmarksService) {
        this.bookmarksService = bookmarksService;
    }

    @PostMapping("/auctions/bookmark")
    public void bookmarkAuction(@RequestBody Map<String, Long> params) {
        Long auctionId = params.get("auctionId");
        Long userId = params.get("userId");
        bookmarksService.bookmarkAuction(auctionId,userId);
    }



}
