package com.modakmakers.modakchallenge.service;

import com.modakmakers.modakchallenge.exception.TooManyRequestsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceImplTest {

    @Mock
    private GatewayService gatewayService;

    @Mock
    private RedisTemplate<String, Long> redisTemplate;

    @Mock
    private ZSetOperations<String, Long> zSetOperations;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @BeforeEach
    public void setUp() {
        lenient().when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
    }

    @Test
    public void shouldSendAnStatusNotificationSuccessfully() throws TooManyRequestsException {
        String type = "status";
        String userId = "user1";
        String message = "Test Message";

        when(redisTemplate.opsForZSet().count(anyString(), anyDouble(), anyDouble())).thenReturn(0L);

        notificationService.send(type, userId, message);

        verify(gatewayService, times(1)).send(userId, message);
        verify(zSetOperations, times(1)).add(anyString(), anyLong(), anyDouble());
    }

    @Test
    public void shouldSendANonExistentTypeAndThrowIllegalArgumentException() {
        String type = "newsletter";
        String userId = "user1";
        String message = "Test Message";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> notificationService.send(type, userId, message));

        assertEquals("Notification type: newsletter not found in rate limit rules.", exception.getMessage());
    }

    @Test
    public void shouldReturnATooManyRequestsException() {
        String type = "status";
        String userId = "user1";
        String message = "Test Message";

        when(redisTemplate.opsForZSet().count(anyString(), anyDouble(), anyDouble())).thenReturn(2L);

        Exception exception = assertThrows(TooManyRequestsException.class, () -> notificationService.send(type, userId, message));

        assertEquals("Rate limit exceeded: Maximum 2 requests per 60000 ms allowed for status notifications.", exception.getMessage());
    }
}