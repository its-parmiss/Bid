package rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions;

public class TokenNotFoundException extends RuntimeException {
    public TokenNotFoundException() {
        super("This link is invalid or expired.");
    }
}