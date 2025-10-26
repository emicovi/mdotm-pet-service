package com.mdotm.pets.interfaces.rest.error;

import com.mdotm.pets.domain.exception.PetNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(PetNotFoundException.class)
    public ProblemDetail handleNotFound(PetNotFoundException ex, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("Resource not found");
        pd.setType(Objects.requireNonNull(URI.create("https://http.dev/problems/not-found")));
        pd.setProperty("timestamp", OffsetDateTime.now());
        pd.setProperty("path", Objects.requireNonNull(req.getRequestURI()));
        return pd;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        pd.setTitle("Invalid request");
        pd.setType(Objects.requireNonNull(URI.create("https://http.dev/problems/validation-error")));
        Map<String, Object> errors = new HashMap<>();
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(fe -> fe.getField(), fe -> fe.getDefaultMessage(), (a, b) -> a));
        errors.put("fields", fieldErrors);
        pd.setProperty("errors", errors);
        pd.setProperty("timestamp", OffsetDateTime.now());
        pd.setProperty("path", Objects.requireNonNull(req.getRequestURI()));
        return pd;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        pd.setTitle("Constraint violation");
        pd.setType(Objects.requireNonNull(URI.create("https://http.dev/problems/constraint-violation")));
        pd.setProperty("timestamp", OffsetDateTime.now());
        pd.setProperty("path", Objects.requireNonNull(req.getRequestURI()));
        return pd;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleMalformedJson(HttpMessageNotReadableException ex, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Malformed JSON request");
        pd.setTitle("Malformed request body");
        pd.setType(Objects.requireNonNull(URI.create("https://http.dev/problems/malformed-request")));
        pd.setProperty("timestamp", OffsetDateTime.now());
        pd.setProperty("path", Objects.requireNonNull(req.getRequestURI()));
        return pd;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error");
        pd.setTitle("Internal Server Error");
        pd.setType(Objects.requireNonNull(URI.create("https://http.dev/problems/internal-error")));
        pd.setProperty("timestamp", OffsetDateTime.now());
        pd.setProperty("path", Objects.requireNonNull(req.getRequestURI()));
        return pd;
    }
}
