package backend.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handle(Exception ex) {
    ex.printStackTrace(); // ✅ affiche la vraie cause dans la console
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of(
            "error", ex.getClass().getSimpleName(),
            "message", ex.getMessage() == null ? "No message" : ex.getMessage()
        ));
  }
}
