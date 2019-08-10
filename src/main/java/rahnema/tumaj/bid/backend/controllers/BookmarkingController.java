package rahnema.tumaj.bid.backend.controllers;


import org.springframework.web.bind.annotation.*;
import rahnema.tumaj.bid.backend.services.Bookmarks.BookmarksService;
import rahnema.tumaj.bid.backend.utils.TokenUtil;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.TokenNotFoundException;

@RestController
public class BookmarkingController {

    private final BookmarksService bookmarksService;
    private final TokenUtil tokenUtil;

    public BookmarkingController(BookmarksService bookmarksService,
                                 TokenUtil tokenUtil) {
        this.bookmarksService = bookmarksService;
        this.tokenUtil = tokenUtil;
    }

    @PostMapping("/auctions/bookmark")
    public void bookmarkAuction(@RequestHeader("Authorization") String token,
                                @RequestParam("auctionId") Long auctionId) {
        String email = tokenUtil
                .getUsernameFromToken(token)
                .orElseThrow(TokenNotFoundException::new);
        bookmarksService.bookmarkAuction(auctionId, email);
    }

}
