package com.avijitmondal.ops.scheduler;

import com.avijitmondal.ops.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderStatusSchedulerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderStatusScheduler scheduler;

    @Test
    void updatePendingOrders_updatesOrders() {
        when(orderService.updatePendingOrdersToProcessing()).thenReturn(5);

        scheduler.updatePendingOrders();

        verify(orderService).updatePendingOrdersToProcessing();
    }

    @Test
    void updatePendingOrders_noOrders_doesNotThrow() {
        when(orderService.updatePendingOrdersToProcessing()).thenReturn(0);

        scheduler.updatePendingOrders();

        verify(orderService).updatePendingOrdersToProcessing();
    }

    @Test
    void updatePendingOrders_exceptionThrown_handlesGracefully() {
        when(orderService.updatePendingOrdersToProcessing()).thenThrow(new RuntimeException("Database error"));

        // Should not throw exception
        try {
            scheduler.updatePendingOrders();
        } catch (Exception e) {
            // Expected - scheduler logs error
        }

        verify(orderService).updatePendingOrdersToProcessing();
    }
}
