package com.kole.platform.common.exception;

import com.kole.platform.common.api.ApiError;
import com.kole.platform.common.api.ApiResponse;
import com.kole.platform.common.web.RequestContext;
import jakarta.validation.ConstraintViolationException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
        LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(KoleException.class)
    public ResponseEntity<ApiResponse<Void>> handleKoleException(
        KoleException exception
    ) {
        ErrorCode errorCode = exception.getErrorCode();

        log.warn(
            "Application error code={} message={}",
            errorCode.code(),
            exception.getMessage()
        );

        return ResponseEntity
            .status(errorCode.status())
            .body(
                ApiResponse.failure(
                    exception.getMessage(),
                    ApiError.of(errorCode.code()),
                    RequestContext.requestId()
                )
            );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
        MethodArgumentNotValidException exception
    ) {
        Map<String, String> fields = new LinkedHashMap<>();

        for (FieldError fieldError :
            exception.getBindingResult().getFieldErrors()) {

            fields.putIfAbsent(
                fieldError.getField(),
                fieldError.getDefaultMessage()
            );
        }

        return ResponseEntity
            .badRequest()
            .body(
                ApiResponse.failure(
                    "Validation failed",
                    ApiError.of(
                        ErrorCode.VALIDATION_ERROR.code(),
                        fields
                    ),
                    RequestContext.requestId()
                )
            );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(
        ConstraintViolationException exception
    ) {
        Map<String, String> fields = new LinkedHashMap<>();

        exception.getConstraintViolations().forEach(violation ->
            fields.put(
                violation.getPropertyPath().toString(),
                violation.getMessage()
            )
        );

        return ResponseEntity
            .badRequest()
            .body(
                ApiResponse.failure(
                    "Validation failed",
                    ApiError.of(
                        ErrorCode.VALIDATION_ERROR.code(),
                        fields
                    ),
                    RequestContext.requestId()
                )
            );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnreadableMessage(
        HttpMessageNotReadableException exception
    ) {
        return ResponseEntity
            .badRequest()
            .body(
                ApiResponse.failure(
                    "The request body is malformed or unreadable",
                    ApiError.of(
                        ErrorCode.MALFORMED_REQUEST.code()
                    ),
                    RequestContext.requestId()
                )
            );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpectedException(
        Exception exception
    ) {
        log.error(
            "Unexpected application error",
            exception
        );

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ApiResponse.failure(
                    "An unexpected error occurred",
                    ApiError.of(
                        ErrorCode.INTERNAL_ERROR.code()
                    ),
                    RequestContext.requestId()
                )
            );
    }
}