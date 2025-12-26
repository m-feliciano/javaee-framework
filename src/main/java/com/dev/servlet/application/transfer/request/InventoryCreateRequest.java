package com.dev.servlet.application.transfer.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record InventoryCreateRequest(@NotNull Integer quantity,
                                     @Size(min = 3,
                                             max = 500,
                                             message = "Description must be between {min} and {max} characters") String description,
                                     @NotNull UUID productId) {
}
