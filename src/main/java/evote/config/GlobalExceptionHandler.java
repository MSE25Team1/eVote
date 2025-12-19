package evote.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global Exception Handler für die gesamte eVote-Anwendung.
 * Konvertiert Domain-Exceptions zu HTTP Response Codes.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Konvertiert IllegalArgumentException zu 400 Bad Request
     * (bei ungültigen Eingabedaten)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Invalid input: " + ex.getMessage());
    }

    /**
     * Konvertiert IllegalStateException zu 409 Conflict
     * (bei Zustandskonflikten wie bereits erfolgte Abstimmung)
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("Conflict: " + ex.getMessage());
    }
}

