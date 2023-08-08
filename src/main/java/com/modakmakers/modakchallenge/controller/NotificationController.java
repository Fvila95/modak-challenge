package com.modakmakers.modakchallenge.controller;

import com.modakmakers.modakchallenge.dto.NotificationRequestDTO;
import com.modakmakers.modakchallenge.dto.NotificationResponseDTO;
import com.modakmakers.modakchallenge.exception.TooManyRequestsException;
import com.modakmakers.modakchallenge.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    private static final Logger logger = Logger.getLogger(NotificationController.class.getName());

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<NotificationResponseDTO> sendNotification(@Valid @RequestBody NotificationRequestDTO request) {
        try {
            logger.info(String.format("Attempting to send notification of type %s for user %s", request.type(), request.userId()));

            notificationService.send(request.type(), request.userId(), request.message());

            logger.info(String.format("Notification of type %s sent successfully for user %s", request.type(), request.userId()));
            return ResponseEntity.ok(new NotificationResponseDTO("success", "Notification sent successfully."));
        } catch (TooManyRequestsException e) {
            logger.warning(String.format("Rate limit exceeded for user %s: %s", request.userId(), e.getMessage()));
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(new NotificationResponseDTO("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            logger.warning(String.format("Notification type %s does not exists.", request.userId(), e.getMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new NotificationResponseDTO("error", "Notification type does not exists."));
        } catch (Exception e) {
            logger.severe(String.format("Unhandled exception occurred: %s", e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new NotificationResponseDTO("error", "An error occurred while processing your request."));
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<NotificationResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String validationMessage = ex.getBindingResult().getAllErrors().stream()
                .filter(error -> error instanceof FieldError)
                .map(error -> ((FieldError) error).getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        logger.warning(String.format("Validation error: %s", validationMessage));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new NotificationResponseDTO("error", validationMessage));
    }
}
