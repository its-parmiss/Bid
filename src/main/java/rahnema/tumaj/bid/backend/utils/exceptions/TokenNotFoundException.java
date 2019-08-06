package rahnema.tumaj.bid.backend.utils.exceptions;

public class TokenNotFoundException extends RuntimeException {
    public TokenNotFoundException() {
        super("This link is invalid or expired.");
    }
}