
package com.freightfox.dispatch.repository;

import com.freightfox.dispatch.entity.DeliveryOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryOrderRepository
        extends JpaRepository<DeliveryOrder, String> {
}
