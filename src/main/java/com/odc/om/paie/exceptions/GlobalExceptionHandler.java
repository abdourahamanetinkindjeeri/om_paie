package com.odc.om.paie.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleWalletNotFoundException(WalletNotFoundException ex, WebRequest request) {
        log.error("Wallet not found: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                "WALLET_NOT_FOUND",
                ex.getMessage(),
                request.getDescription(false),
                Instant.now()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        log.error("User not found: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                "USER_NOT_FOUND",
                ex.getMessage(),
                request.getDescription(false),
                Instant.now()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MerchantNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMerchantNotFoundException(MerchantNotFoundException ex, WebRequest request) {
        log.error("Merchant not found: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                "MERCHANT_NOT_FOUND",
                ex.getMessage(),
                request.getDescription(false),
                Instant.now()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientBalanceException(InsufficientBalanceException ex, WebRequest request) {
        log.error("Insufficient balance: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                "INSUFFICIENT_BALANCE",
                ex.getMessage(),
                request.getDescription(false),
                Instant.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidTransactionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTransactionException(InvalidTransactionException ex, WebRequest request) {
        log.error("Invalid transaction: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                "INVALID_TRANSACTION",
                ex.getMessage(),
                request.getDescription(false),
                Instant.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateMainWalletException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateMainWalletException(DuplicateMainWalletException ex, WebRequest request) {
        log.error("Duplicate main wallet: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                "DUPLICATE_MAIN_WALLET",
                ex.getMessage(),
                request.getDescription(false),
                Instant.now()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        log.error("Validation error: {}", errors);
        ErrorResponse error = new ErrorResponse(
                "VALIDATION_ERROR",
                "Erreur de validation",
                errors.toString(),
                Instant.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        log.error("Unexpected error: ", ex);
        ErrorResponse error = new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "Une erreur interne s'est produite",
                request.getDescription(false),
                Instant.now()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static class ErrorResponse {
        private String code;
        private String message;
        private String details;
        private Instant timestamp;

        public ErrorResponse(String code, String message, String details, Instant timestamp) {
            this.code = code;
            this.message = message;
            this.details = details;
            this.timestamp = timestamp;
        }

        // Getters
        public String getCode() { return code; }
        public String getMessage() { return message; }
        public String getDetails() { return details; }
        public Instant getTimestamp() { return timestamp; }
    }
}