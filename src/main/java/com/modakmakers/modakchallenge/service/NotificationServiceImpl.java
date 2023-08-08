package com.modakmakers.modakchallenge.service;

import com.modakmakers.modakchallenge.exception.TooManyRequestsException;
import com.modakmakers.modakchallenge.model.RateLimitRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final GatewayService gatewayService;
    private final RedisTemplate<String, Long> redisTemplate;
    private final List<RateLimitRule> limitRules;

    @Autowired
    public NotificationServiceImpl(GatewayService gatewayService, RedisTemplate<String, Long> redisTemplate) {
        this.gatewayService = gatewayService;
        this.redisTemplate = redisTemplate;
        this.limitRules = Arrays.asList(
                new RateLimitRule("status", 2, TimeUnit.MINUTES.toMillis(1)),
                new RateLimitRule("news", 1, TimeUnit.DAYS.toMillis(1)),
                new RateLimitRule("marketing", 3,TimeUnit.HOURS.toMillis(1))
        );
    }

    @Override
    public void send(String type, String userId, String message) throws TooManyRequestsException {
        validateRateLimit(type, userId);
        gatewayService.send(userId, message);

        long currentTime = System.currentTimeMillis();
        String key = type + ":" + userId;
        redisTemplate.opsForZSet().add(key, currentTime, currentTime);
    }

    private long getMessageCountForType(String key, long startTime, long endTime) {
        return redisTemplate.opsForZSet().count(key, startTime, endTime);
    }

    private void validateRateLimit(String type, String userId) throws TooManyRequestsException {
        long currentTime = System.currentTimeMillis();
        String key = type + ":" + userId;

        Optional<RateLimitRule> matchingRule = limitRules.stream()
                .filter(rule -> rule.type().equals(type))
                .findFirst();

        RateLimitRule rule = matchingRule.orElseThrow(() ->
                new IllegalArgumentException("Notification type: " + type + " not found in rate limit rules."));

        long startTime = currentTime - rule.timeWindowInMillis();
        long count = getMessageCountForType(key, startTime, currentTime);

        if (count >= rule.limit()) {
            throw new TooManyRequestsException("Rate limit exceeded: Maximum " + rule.limit() + " requests per " +
                    rule.timeWindowInMillis() + " ms allowed for " + rule.type() + " notifications.");
        }
    }
}