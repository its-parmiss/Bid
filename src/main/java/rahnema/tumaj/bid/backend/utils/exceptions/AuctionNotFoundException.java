package rahnema.tumaj.bid.backend.utils.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class AuctionNotFoundException extends RuntimeException {
    public AuctionNotFoundException(Long id) {
        super("Auction with id " + id + " not found");
    }
}