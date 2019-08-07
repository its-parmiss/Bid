package rahnema.tumaj.bid.backend.utils.exceptions;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("user with email: " + email + " already exists");
    }
}
