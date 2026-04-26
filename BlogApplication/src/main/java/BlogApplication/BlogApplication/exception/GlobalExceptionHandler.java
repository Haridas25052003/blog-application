package BlogApplication.BlogApplication.exception;

import BlogApplication.BlogApplication.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ==================== VALIDATION ERRORS ====================
    // Triggered when @Valid fails on request body
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        // Collect all field errors into a map
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
          .getAllErrors()
          .forEach(error -> {
              String fieldName = ((FieldError) error).getField();
              String message = error.getDefaultMessage();
              errors.put(fieldName, message);
          });

        // Return first validation error message
        String firstError = errors.values().iterator().next();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure(firstError));
    }

    // ==================== RESOURCE NOT FOUND ====================
    // Triggered when ResourceNotFoundException is thrown
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFound(
            ResourceNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.failure(ex.getMessage()));
    }

    // ==================== ILLEGAL ARGUMENT ====================
    // Triggered for bad inputs like wrong ID format
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgument(
            IllegalArgumentException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure(ex.getMessage()));
    }

    // ==================== NUMBER FORMAT ====================
    // Triggered when path variable is not a valid number
    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ApiResponse> handleNumberFormat(
            NumberFormatException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure("Invalid ID format provided"));
    }

    // ==================== ACCESS DENIED ====================
    // Triggered when user tries to access protected resource
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleAccessDenied(
            org.springframework.security.access.AccessDeniedException ex) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.failure("Access Denied: You don't have permission"));
    }

    // ==================== NULL POINTER ====================
    // Catches unexpected null values
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse> handleNullPointer(
            NullPointerException ex) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure("Something went wrong. Please try again"));
    }

    // ==================== GLOBAL FALLBACK ====================
    // Catches ANY exception not handled above
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGlobalException(
            Exception ex) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure("Internal server error: " + ex.getMessage()));
    }
}