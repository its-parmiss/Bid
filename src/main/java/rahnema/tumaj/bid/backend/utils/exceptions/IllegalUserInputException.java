package rahnema.tumaj.bid.backend.utils.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY)
public class IllegalUserInputException extends RuntimeException {
    public IllegalUserInputException() {
        super("Fields were invalid. Please check them out.");
    }
}