package com.modakmakers.modakchallenge.controller;

import com.modakmakers.modakchallenge.exception.TooManyRequestsException;
import com.modakmakers.modakchallenge.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<String> sendNotification(
            @RequestParam String type,
            @RequestParam String userId,
            @RequestParam String message
    ) {
        try {
            notificationService.send(type, userId, message);
            return ResponseEntity.ok("Notification sent successfully.");
        } catch (TooManyRequestsException e) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(e.getMessage());
        } catch (NullPointerException ne) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ne.getMessage());
        }
    }
}
