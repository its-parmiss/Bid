package rahnema.tumaj.bid.backend.utils.exceptions;

public class FullAuctionException extends RuntimeException{
    public FullAuctionException() {
        super("auction is Full");
    }

}
