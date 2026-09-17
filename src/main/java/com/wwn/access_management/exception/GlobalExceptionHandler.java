package com.wwn.access_management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleNotFound(
            ResourceNotFoundException exception
    ) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "timestamp",
                        LocalDateTime.now(),

                        "status",
                        404,

                        "error",
                        "NOT_FOUND",

                        "message",
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(ApprovalNotAllowedException.class)
    public ResponseEntity<?> handleApprovalNotAllowed(
            ApprovalNotAllowedException exception
    ) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of(
                        "timestamp",
                        LocalDateTime.now(),

                        "status",
                        403,

                        "error",
                        "FORBIDDEN",

                        "message",
                        exception.getMessage()
                ));
    }
}
