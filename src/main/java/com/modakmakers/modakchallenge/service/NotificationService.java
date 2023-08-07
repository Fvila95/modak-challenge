package com.modakmakers.modakchallenge.service;

import com.modakmakers.modakchallenge.exception.TooManyRequestsException;

public interface NotificationService {

    void send(String type, String userId, String message) throws TooManyRequestsException;

}
