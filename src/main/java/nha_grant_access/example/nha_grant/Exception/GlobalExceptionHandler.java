package nha_grant_access.example.nha_grant.Exception;

import io.jsonwebtoken.ExpiredJwtException;
import nha_grant_access.example.nha_grant.dto.Error;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        Error errorResponse = new Error(errors.toString(),"Validation failed for "+ errors.toString());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleUserAlreadyExistsException(Exception ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        ex.getClass().getSimpleName().startsWith("Grant");

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Error> handleAccessDeniedException(AccessDeniedException ex) {
        Error errorResponse = new Error("Access denied", "You do not have permission to perform this action");
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Error> dataBaseException(DataAccessException ex) {
        Error errorResponse = new Error("Access denied", "You do not have permission to perform this action");
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<Error> handleGenericException(Exception ex) {
//        Error errorResponse = new Error("Internal Server Error", ex.getMessage());
//        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
@ExceptionHandler(ExpiredJwtException.class)
public ResponseEntity<String> handleExpiredJwtException(ExpiredJwtException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("Token expired. Please log in again.");
}

}
