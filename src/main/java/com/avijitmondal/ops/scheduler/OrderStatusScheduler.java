package com.avijitmondal.ops.scheduler;

import com.avijitmondal.ops.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OrderStatusScheduler {

    private static final Logger logger = LoggerFactory.getLogger(OrderStatusScheduler.class);
    
    private final OrderService orderService;

    public OrderStatusScheduler(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Updates PENDING orders to PROCESSING every 5 minutes
     */
    @Scheduled(fixedRate = 300000) // 5 minutes = 300,000 milliseconds
    public void updatePendingOrders() {
        logger.info("[Scheduler] Starting task: update PENDING -> PROCESSING");
        try {
            int updated = orderService.updatePendingOrdersToProcessing();
            if (updated > 0) {
                logger.info("[Scheduler] Updated {} PENDING orders to PROCESSING", updated);
            } else {
                logger.debug("[Scheduler] No PENDING orders to update this run");
            }
        } catch (Exception e) {
            logger.error("[Scheduler] Error during PENDING -> PROCESSING update", e);
        }
    }
}
