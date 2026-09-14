package com.sentinelscm.api;

import com.sentinelscm.service.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/** Maps service and framework exceptions to HTTP codes for the JSON API only. */
@RestControllerAdvice(basePackages = "com.sentinelscm.api")
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<ApiError> notFound(NotFoundException e, HttpServletRequest req) {
        return respond(HttpStatus.NOT_FOUND, e.getMessage(), req);
    }

    @ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    ResponseEntity<ApiError> badRequest(Exception e, HttpServletRequest req) {
        return respond(HttpStatus.BAD_REQUEST, e.getMessage(), req);
    }

    @ExceptionHandler(IllegalStateException.class)
    ResponseEntity<ApiError> conflict(IllegalStateException e, HttpServletRequest req) {
        return respond(HttpStatus.CONFLICT, e.getMessage(), req);
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ApiError> forbidden(AccessDeniedException e, HttpServletRequest req) {
        return respond(HttpStatus.FORBIDDEN, "Your role does not permit this action", req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> validation(MethodArgumentNotValidException e, HttpServletRequest req) {
        Map<String, String> fields = new LinkedHashMap<>();
        for (FieldError fe : e.getBindingResult().getFieldErrors()) {
            fields.putIfAbsent(fe.getField(), fe.getDefaultMessage());
        }
        HttpStatus s = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(s).body(
            new ApiError(s.value(), s.getReasonPhrase(), "Validation failed", req.getRequestURI(), Instant.now(), fields));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiError> unexpected(Exception e, HttpServletRequest req) {
        log.error("Unhandled API error on {}", req.getRequestURI(), e);
        return respond(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", req);
    }

    private static ResponseEntity<ApiError> respond(HttpStatus status, String message, HttpServletRequest req) {
        return ResponseEntity.status(status)
            .body(ApiError.of(status.value(), status.getReasonPhrase(), message, req.getRequestURI()));
    }
}
