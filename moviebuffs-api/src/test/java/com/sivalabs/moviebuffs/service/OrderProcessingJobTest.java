package com.sivalabs.moviebuffs.service;

import com.sivalabs.moviebuffs.entity.Order;
import com.sivalabs.moviebuffs.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static com.sivalabs.moviebuffs.datafactory.TestDataFactory.createOrder;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderProcessingJobTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderProcessingJob orderProcessingJob;

    private List<Order> orderList = null;

    @BeforeEach
    void setUp() {
        orderList = new ArrayList<>();
        orderList.add(createOrder(1L));
        orderList.add(createOrder(2L));
        orderList.add(createOrder(3L));
    }

    @Test
    void should_process_orders() {
        given(orderRepository.findByStatus(Order.OrderStatus.NEW)).willReturn(orderList);

        orderProcessingJob.processOrders();

        verify(orderRepository, times(orderList.size())).save(any(Order.class));
    }

    @Test
    void should_ignore_if_no_orders_to_process() {
        given(orderRepository.findByStatus(Order.OrderStatus.NEW)).willReturn(new ArrayList<>());

        orderProcessingJob.processOrders();

        verify(orderRepository, never()).save(any(Order.class));
    }
}
