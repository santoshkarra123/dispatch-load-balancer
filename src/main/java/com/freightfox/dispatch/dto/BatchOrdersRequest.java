
package com.freightfox.dispatch.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record BatchOrdersRequest(
        @NotEmpty(message = "At least one order is required.")
        List<@Valid OrderRequest> orders
) {
}
