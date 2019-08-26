package rahnema.tumaj.bid.backend.utils.exceptions;

public class NotAllowedToLeaveAuctionException extends RuntimeException{
    public NotAllowedToLeaveAuctionException() {
        super("you are the top bidder,you can't leave the auction");
    }

}
