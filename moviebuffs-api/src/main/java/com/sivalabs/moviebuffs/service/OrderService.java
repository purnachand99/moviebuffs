package com.sivalabs.moviebuffs.service;

import com.sivalabs.moviebuffs.exception.OrderProcessingException;
import com.sivalabs.moviebuffs.entity.Order;
import com.sivalabs.moviebuffs.exception.OrderNotFoundException;
import com.sivalabs.moviebuffs.models.OrderConfirmation;
import com.sivalabs.moviebuffs.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public OrderConfirmation createOrder(Order order) {
        order.setOrderId(UUID.randomUUID().toString());
        order.setStatus(Order.OrderStatus.NEW);
        order.getItems().forEach(lineItem -> lineItem.setOrder(order));
        Order savedOrder = this.orderRepository.save(order);
        log.info("Created Order ID=" + savedOrder.getId() + ", ref_num=" + savedOrder.getOrderId());
        return new OrderConfirmation(savedOrder.getOrderId(), savedOrder.getStatus());
    }

    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> findOrderByOrderId(String orderId) {
        return this.orderRepository.findByOrderId(orderId);
    }

    public void cancelOrder(String orderId) {
        Order order = findOrderByOrderId(orderId).orElse(null);
        if (order == null) {
            throw new OrderNotFoundException("Order with id: "+ orderId + " is not found");
        }

        if (order.getStatus() == Order.OrderStatus.DELIVERED) {
            throw new OrderProcessingException("Order is already delivered");
        }
        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);
    }
}