package rahnema.tumaj.bid.backend.utils.exceptions;

public class IllegalUserInputException extends RuntimeException {
    public IllegalUserInputException() {
        super("Fields were invalid. Please check them out.");
    }
}