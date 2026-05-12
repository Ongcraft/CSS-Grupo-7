package pt.ul.fc.css.tascaeats;

import jakarta.validation.ValidationException;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import pt.ul.fc.css.tascaeats.dtos.common.ErrorResponseDTO;
import pt.ul.fc.css.tascaeats.exception.BusinessRuleException;
import pt.ul.fc.css.tascaeats.exception.EntityNotFoundException;
import pt.ul.fc.css.tascaeats.exception.InvalidInputException;

/**
 * Centralized exception handler for Spring controllers.
 *
 * <p>This class catches and handles exceptions globally across all controllers, providing
 * consistent HTTP responses for errors.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

  private ResponseEntity<ErrorResponseDTO> buildResponse(HttpStatus status, String message) {
    return ResponseEntity.status(status)
        .body(new ErrorResponseDTO(Instant.now(), status.value(), message));
  }

  // -------------------------------------------------------------------------
  // Not Found (404)
  // -------------------------------------------------------------------------
  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorResponseDTO> handleNotFound(EntityNotFoundException e) {
    return buildResponse(HttpStatus.NOT_FOUND, e.getMessage());
  }

  // -------------------------------------------------------------------------
  // Validation / Bad Request (400)
  // -------------------------------------------------------------------------
  @ExceptionHandler({
    InvalidInputException.class,
    ValidationException.class,
    IllegalArgumentException.class
  })
  public ResponseEntity<ErrorResponseDTO> handleBadRequest(RuntimeException e) {
    return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<String> handleInvalidUUID() {
    return ResponseEntity.badRequest().body("Invalid UUID format");
  }

  // -------------------------------------------------------------------------
  // Business Rule (422)
  // -------------------------------------------------------------------------
  @ExceptionHandler(BusinessRuleException.class)
  public ResponseEntity<ErrorResponseDTO> handleBusiness(BusinessRuleException e) {
    return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
  }

  // -------------------------------------------------------------------------
  // Generic (500)
  // -------------------------------------------------------------------------
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDTO> handleGeneric(Exception e) {
    return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred");
  }
}
