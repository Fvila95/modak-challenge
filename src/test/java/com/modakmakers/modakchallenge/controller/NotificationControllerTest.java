package com.modakmakers.modakchallenge.controller;

import com.modakmakers.modakchallenge.dto.NotificationRequestDTO;
import com.modakmakers.modakchallenge.dto.NotificationResponseDTO;
import com.modakmakers.modakchallenge.exception.TooManyRequestsException;
import com.modakmakers.modakchallenge.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class NotificationControllerTest {
    @InjectMocks
    NotificationController notificationController;

    @Mock
    NotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldSendNotificationSuccessfully() throws TooManyRequestsException {
        NotificationRequestDTO request = new NotificationRequestDTO("status", "user1", "Hello");

        doNothing().when(notificationService).send(anyString(), anyString(), anyString());

        ResponseEntity<NotificationResponseDTO> response = notificationController.sendNotification(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().status());
    }

    @Test
    void shouldHandleTooManyRequestsException() throws TooManyRequestsException {
        NotificationRequestDTO request = new NotificationRequestDTO("status", "user1", "Hello");

        doThrow(new TooManyRequestsException("Rate limit exceeded")).when(notificationService).send(anyString(), anyString(), anyString());

        ResponseEntity<NotificationResponseDTO> response = notificationController.sendNotification(request);

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        assertEquals("error", response.getBody().status());
    }

    @Test
    void shouldHandleIllegalArgumentException() throws IllegalArgumentException, TooManyRequestsException {
        NotificationRequestDTO request = new NotificationRequestDTO("status", "user1", "Hello");

        doThrow(new IllegalArgumentException("Notification type does not exists")).when(notificationService).send(anyString(), anyString(), anyString());

        ResponseEntity<NotificationResponseDTO> response = notificationController.sendNotification(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("error", response.getBody().status());
    }

    @Test
    void shouldHandleNullPointerException() throws IllegalArgumentException, TooManyRequestsException {
        NotificationRequestDTO request = new NotificationRequestDTO("status", "user1", "Hello");

        doThrow(new NullPointerException()).when(notificationService).send(anyString(), anyString(), anyString());

        ResponseEntity<NotificationResponseDTO> response = notificationController.sendNotification(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("error", response.getBody().status());
    }

}