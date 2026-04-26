package com.example.demo.Mapper;

import com.example.demo.Dto.OrderItemResponseDto;
import com.example.demo.Dto.OrderResponseDto;
import com.example.demo.Entity.Order;
import com.example.demo.Entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
public class OrderMapper {


    public OrderResponseDto toOrderResponseDto(Order order) {
        List<OrderItemResponseDto> orderItemDtos = order.getOrderItems().stream()
                .map(this::toOrderItemResponseDto)
                .collect(Collectors.toList());

        return OrderResponseDto.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .userEmail(order.getUser().getEmail())
                .totalAmount(order.getTotalAmount())
                .shippingAddress(order.getShippingAddress())
                .shippingName(order.getShippingName())
                .shippingEmail(order.getShippingEmail())
                .shippingPhone(order.getShippingPhone())
                .paymentMethod(order.getPaymentMethod())
                .isPaid(order.isPaid())
                .orderItems(orderItemDtos)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }


    public OrderItemResponseDto toOrderItemResponseDto(OrderItem orderItem) {
        return OrderItemResponseDto.builder()
                .id(orderItem.getId())
                .orderId(orderItem.getOrder().getId())
                .productId(orderItem.getProduct().getId())
                .productName(orderItem.getProduct().getName())
                .productPrice(orderItem.getProduct().getPrice())
                .productDescription(orderItem.getProduct().getDescription())
                .productImageUrl(orderItem.getProduct().getImageUrl())
                .quantity(orderItem.getQuantity())
                .priceAtPurchase(orderItem.getPriceAtPurchase())
                .build();
    }
}

