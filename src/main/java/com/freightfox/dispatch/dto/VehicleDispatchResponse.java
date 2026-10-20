
package com.freightfox.dispatch.dto;

import com.freightfox.dispatch.entity.DeliveryOrder;
import java.util.List;

public record VehicleDispatchResponse(
        String vehicleId,
        double totalLoad,
        String totalDistance,
        List<DeliveryOrder> assignedOrders
) {
}
