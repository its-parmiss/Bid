package rahnema.tumaj.bid.backend.controllers;

import com.google.gson.Gson;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import rahnema.tumaj.bid.backend.utils.exceptions.*;

@ControllerAdvice
public class ErrorController extends ResponseEntityExceptionHandler {

    private Gson gson = new Gson();

    @ExceptionHandler(value = {AuctionNotFoundException.class})
    public ResponseEntity<Object> entityNotFound(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex,
                gson.toJson(new ExceptionMessage(ex.getMessage(), 404)),
                new HttpHeaders(),
                HttpStatus.NOT_FOUND, request
        );
    }

    @ExceptionHandler(value = {IllegalAuctionInputException.class})
    public ResponseEntity<Object> actionNotFound(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex,
                gson.toJson(new ExceptionMessage(ex.getMessage(), 400)),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST, request
        );
    }

    @ExceptionHandler(value = {UserNotFoundException.class})
    public ResponseEntity<Object> userNotFound(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex,
                gson.toJson(new ExceptionMessage(ex.getMessage(), 404)),
                new HttpHeaders(),
                HttpStatus.NOT_FOUND, request
        );
    }

    @ExceptionHandler(value = {IllegalUserInputException.class})
    public ResponseEntity<Object> illegalInput(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex,
                gson.toJson(new ExceptionMessage(ex.getMessage(), 400)),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST, request
        );
    }
}