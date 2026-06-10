package com.WillKopa.CardIdentifier.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler for the Card Identifier application.
 * <p>
 * Provides centralized exception handling for all controllers, converting
 * exceptions into appropriate HTTP responses with error messages.
 * </p>
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Handles InvalidImageException by returning a bad request response.
     *
     * @param e the exception that was thrown
     * @return ResponseEntity containing the error message with BAD_REQUEST status
     */
    @ExceptionHandler(InvalidImageException.class)
    public ResponseEntity<String> handleInvalidImageError(InvalidImageException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    /**
     * Handles NoOcrResultException by returning a bad request response.
     *
     * @param e the exception that was thrown
     * @return ResponseEntity containing a generic error message with BAD_REQUEST status
     */
    @ExceptionHandler(NoOcrResultException.class)
    public ResponseEntity<String> handleNoOCRResultError(NoOcrResultException e) {
        return ResponseEntity.badRequest().body("Unable to read image");
    }

    /**
     * Handles CardNotFoundException and UserNotFoundException by returning a not found response.
     *
     * @param e the exception that was thrown
     * @return ResponseEntity containing the error message with NOT_FOUND status
     */
    @ExceptionHandler({CardNotFoundException.class, UserNotFoundException.class})
    public ResponseEntity<String> handleCardNotFoundException(Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    /**
     * Handles UserAlreadyExistsException by returning a bad request response.
     *
     * @param e the exception that was thrown
     * @return ResponseEntity containing a generic error message with BAD_REQUEST status
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        return ResponseEntity.badRequest().body("User already exists");
    }


    /**
     * Handles all other exceptions by returning an internal server error response.
     * <p>
     * This is a catch-all handler for any unexpected exceptions.
     * </p>
     *
     * @param e the exception that was thrown
     * @return ResponseEntity containing a generic error message with INTERNAL_SERVER_ERROR status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> defaultErrorResponse(Exception e) {
        log.error("Unknown exception triggered: ", e);
        return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
