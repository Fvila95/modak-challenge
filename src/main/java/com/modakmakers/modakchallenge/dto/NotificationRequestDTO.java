package com.modakmakers.modakchallenge.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NotificationRequestDTO (
        @NotNull @NotEmpty @Size(min = 1, max = 10, message = "Type should be between 1 and 10 characters.") String type,
        @NotNull @NotEmpty @Size(min = 1, max = 25, message = "User ID should be between 1 and 25 characters.") String userId,
        @NotNull @NotEmpty @Size(min = 1, max = 256, message = "Message should be between 1 and 256 characters.") String message
) {}
