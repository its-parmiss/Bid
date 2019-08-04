package rahnema.tumaj.bid.backend.utils.exceptions;

public class IllegalAuctionInputException extends RuntimeException {
    public IllegalAuctionInputException() {
        super("Auction creation fields are invalid");
    }
}
