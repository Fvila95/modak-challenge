package com.modakmakers.modakchallenge.service;

import com.modakmakers.modakchallenge.exception.TooManyRequestsException;
import com.modakmakers.modakchallenge.model.RateLimitRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
    }

    @Test
    public void testSend_successful() throws TooManyRequestsException {
        String userId = "user1";
        String type = "status";
        String message = "Hello!";
        when(zSetOperations.count(anyString(), anyDouble(), anyDouble())).thenReturn(0L);

        notificationService.send(type, userId, message);

        verify(gatewayService, times(1)).send(userId, message);
        verify(zSetOperations, times(1)).add(anyString(), anyLong(), anyDouble());
    }

   /* @Test
    public void testSend_rateLimitExceeded_minute() {
        String userId = "user1";
        String type = "status";
        String message = "Hello!";
        when(zSetOperations.count(eq(type + ":" + userId), anyLong(), anyLong())).thenReturn(5L);

        assertThrows(TooManyRequestsException.class, () -> {
            notificationService.send(type, userId, message);
        });
    }

    @Test
    public void testSend_rateLimitExceeded_news() {
        String userId = "user1";
        String type = "news";
        String message = "Hello!";
        when(zSetOperations.count(eq(type + ":" + userId), anyLong(), anyLong())).thenReturn(1L);

        assertThrows(TooManyRequestsException.class, () -> {
            notificationService.send(type, userId, message);
        });
    }

    @Test
    public void testSend_rateLimitExceeded_marketing() {
        String userId = "user1";
        String type = "marketing";
        String message = "Hello!";
        when(zSetOperations.count(eq(type + ":" + userId), anyLong(), anyLong())).thenReturn(3L);

        assertThrows(TooManyRequestsException.class, () -> {
            notificationService.send(type, userId, message);
        });
    }*/
}