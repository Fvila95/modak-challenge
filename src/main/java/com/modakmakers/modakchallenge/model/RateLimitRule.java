package com.modakmakers.modakchallenge.model;

public record RateLimitRule(String type, long limit, long timeWindowInMillis) {
}

