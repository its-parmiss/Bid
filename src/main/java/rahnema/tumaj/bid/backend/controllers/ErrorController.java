package rahnema.tumaj.bid.backend.controllers;

import com.google.gson.Gson;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import rahnema.tumaj.bid.backend.utils.exceptions.*;
import rahnema.tumaj.bid.backend.utils.exceptions.AlreadyExistExceptions.EmailAlreadyExistsException;
import rahnema.tumaj.bid.backend.utils.exceptions.IllegalInputExceptions.IllegalAuctionInputException;
import rahnema.tumaj.bid.backend.utils.exceptions.IllegalInputExceptions.IllegalUserInputException;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.AuctionNotFoundException;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.CategoryNotFoundException;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.TokenNotFoundException;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.UserNotFoundException;

@ControllerAdvice
public class ErrorController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {AuctionNotFoundException.class})
    public ResponseEntity<Object> entityNotFound(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex,
                new ExceptionMessage(ex.getMessage(), 404),
                new HttpHeaders(),
                HttpStatus.NOT_FOUND, request
        );
    }

    @ExceptionHandler(value = {IllegalAuctionInputException.class})
    public ResponseEntity<Object> actionNotFound(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex,
                new ExceptionMessage(ex.getMessage(), 400),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST, request
        );
    }

    @ExceptionHandler(value = {UserNotFoundException.class})
    public ResponseEntity<Object> userNotFound(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex,
                new ExceptionMessage(ex.getMessage(), 404),
                new HttpHeaders(),
                HttpStatus.NOT_FOUND, request
        );
    }

    @ExceptionHandler(value = {IllegalUserInputException.class})
    public ResponseEntity<Object> illegalInput(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex,
                new ExceptionMessage(ex.getMessage(), 400),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST, request
        );
    }

    @ExceptionHandler(value = {EmailAlreadyExistsException.class})
    public ResponseEntity<Object> emailExists(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex,
                new ExceptionMessage(ex.getMessage(), 4003),
                new HttpHeaders(),
                HttpStatus.CONFLICT, request
        );
    }

    @ExceptionHandler(value = {CategoryNotFoundException.class})
    public ResponseEntity<Object> catNotFound(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex,
                new ExceptionMessage(ex.getMessage(), 404),
                new HttpHeaders(),
                HttpStatus.NOT_FOUND, request
        );
    }

    @ExceptionHandler(value = {BadCredentialsException.class})
    public ResponseEntity<Object> badCredentialsException(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex,
                new ExceptionMessage(ex.getMessage(), 4001),
                new HttpHeaders(),
                HttpStatus.FORBIDDEN, request
        );
    }

    @ExceptionHandler(value = {InternalAuthenticationServiceException.class})
    public ResponseEntity<Object> tokenNotConfirmed(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex,
                new ExceptionMessage(ex.getMessage(), 404),
                new HttpHeaders(),
                HttpStatus.NOT_FOUND, request
        );
    }

    @ExceptionHandler(value = {TokenNotFoundException.class})
    public String tokenNotFound() {
        return "errors/tokenNotFound";
    }

}