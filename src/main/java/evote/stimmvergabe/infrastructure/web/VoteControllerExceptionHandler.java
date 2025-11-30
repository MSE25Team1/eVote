package evote.stimmvergabe.infrastructure.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Globaler Exception Handler für Vote-API Fehler.
 * Konvertiert Domain-Exceptions zu HTTP Response Codes.
 */
@RestControllerAdvice
public class VoteControllerExceptionHandler {

    /**
     * Konvertiert IllegalArgumentException zu 400 Bad Request
     * (bei ungültigen Eingabedaten wie ungültiger Poll ID)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Invalid input: " + ex.getMessage());
    }
}

