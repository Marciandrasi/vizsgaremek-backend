package com.example.demo.Service;

import com.example.demo.Dto.CheckoutRequestDto;
import com.example.demo.Dto.OrderResponseDto;
import com.example.demo.Entity.*;
import com.example.demo.Exception.EmptyCartException;
import com.example.demo.Exception.OrderNotFoundException;
import com.example.demo.Exception.UnauthorizedOrderAccessException;
import com.example.demo.Mapper.OrderMapper;
import com.example.demo.Repository.CartItemRepository;
import com.example.demo.Repository.OrderItemRepository;
import com.example.demo.Repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        CartItemRepository cartItemRepository,
                        OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderMapper = orderMapper;
    }

    @Transactional
    public OrderResponseDto checkout(User user, CheckoutRequestDto request) {

        List<CartItem> cartItems = cartItemRepository.findByUser(user);

        if (cartItems.isEmpty()) {
            throw new EmptyCartException(user.getId());
        }

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(request.getShippingAddress());
        order.setShippingName(request.getShippingName());
        order.setShippingEmail(request.getShippingEmail());
        order.setShippingPhone(request.getShippingPhone());

        order.setPaymentMethod(request.getPaymentMethod());

        if (request.getPaymentMethod() == PaymentMethod.CARD) {
            order.setPaid(true);
        } else {
            order.setPaid(false);
        }

        BigDecimal totalAmount = cartItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(totalAmount);

        List<OrderItem> orderItems = cartItems.stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setProduct(cartItem.getProduct());
                    orderItem.setQuantity(cartItem.getQuantity());
                    // ARA RÖGZÍTÉSE - így később, ha a termék ára megváltozik,
                    // az történeti adatok (régi rendelések) nem módosulnak
                    orderItem.setPriceAtPurchase(cartItem.getProduct().getPrice());
                    return orderItem;
                })
                .collect(Collectors.toList());

        order.setOrderItems(orderItems);

        cartItemRepository.deleteAll(cartItems);

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toOrderResponseDto(savedOrder);
    }


    @Transactional(readOnly = true)
    public List<OrderResponseDto> getUserOrders(User user) {
        return orderRepository.findByUserWithEagerLoad(user.getId()).stream()
                .map(orderMapper::toOrderResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(User user, Long orderId) {
        Order order = orderRepository.findByIdWithEagerLoad(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedOrderAccessException(user.getId(), orderId);
        }

        return orderMapper.toOrderResponseDto(order);
    }
}

