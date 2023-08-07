package com.modakmakers.modakchallenge.service;

import com.modakmakers.modakchallenge.exception.TooManyRequestsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final GatewayService gatewayService;
    private final RedisTemplate<String, Long> redisTemplate;

    @Autowired
    public NotificationServiceImpl(GatewayService gatewayService, RedisTemplate<String, Long> redisTemplate) {
        this.gatewayService = gatewayService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void send(String type, String userId, String message) throws TooManyRequestsException {
        String key = type + ":" + userId;
        long currentTime = System.currentTimeMillis();
        long oneMinuteAgo = currentTime - TimeUnit.MINUTES.toMillis(1);
        long oneHourAgo = currentTime - TimeUnit.HOURS.toMillis(1);
        long oneDayAgo = currentTime - TimeUnit.DAYS.toMillis(1);

        long minuteCount = redisTemplate.opsForZSet().count(key, oneMinuteAgo, currentTime);
        long hourCount = redisTemplate.opsForZSet().count(key, oneHourAgo, currentTime);
        long dayCount = redisTemplate.opsForZSet().count(key, oneDayAgo, currentTime);

        if (minuteCount >= 2) {
            throw new TooManyRequestsException("Rate limit exceeded: Maximum 2 per minute allowed.");
        }
        if (type.equals("news") && dayCount >= 1) {
            throw new TooManyRequestsException("Rate limit exceeded: Maximum 1 per day allowed for news.");
        }
        if (type.equals("marketing") && hourCount >= 3) {
            throw new TooManyRequestsException("Rate limit exceeded: Maximum 3 per hour allowed for marketing.");
        }

        gatewayService.send(userId, message);

        redisTemplate.opsForZSet().add(key, currentTime, currentTime);
    }
}