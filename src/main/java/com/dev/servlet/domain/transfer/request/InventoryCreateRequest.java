package com.dev.servlet.domain.transfer.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public record InventoryCreateRequest(@NotNull Integer quantity,
                                     @Size(min = 3, max = 500) String description,
                                     @Pattern(regexp = "^.{36}$") String productId) {
}
