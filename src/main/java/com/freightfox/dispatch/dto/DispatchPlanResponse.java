
package com.freightfox.dispatch.dto;

import java.util.List;

public record DispatchPlanResponse(
        List<VehicleDispatchResponse> dispatchPlan
) {
}
