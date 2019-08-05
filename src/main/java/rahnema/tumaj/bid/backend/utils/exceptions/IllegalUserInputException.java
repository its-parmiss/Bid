package rahnema.tumaj.bid.backend.utils.exceptions;

public class IllegalUserInputException extends RuntimeException {
    public IllegalUserInputException() {
        super("User registration failed. Fields were invalid.");
    }
}