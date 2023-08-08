package com.modakmakers.modakchallenge.service;

import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class GatewayService {
    private static final Logger logger = Logger.getLogger(GatewayService.class.getName());

    void send(String userId, String message) {

        logger.info("sending message to user " + userId);

    }
}
