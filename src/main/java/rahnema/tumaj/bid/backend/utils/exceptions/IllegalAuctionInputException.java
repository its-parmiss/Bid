package rahnema.tumaj.bid.backend.utils.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY)
public class IllegalAuctionInputException extends RuntimeException {
    public IllegalAuctionInputException() {
        super("Auction creation fields are invalid");
    }
}
