package com.example.demo;

import com.example.demo.Dto.CheckoutRequestDto;
import com.example.demo.Dto.OrderResponseDto;
import com.example.demo.Entity.*;
import com.example.demo.Exception.EmptyCartException;
import com.example.demo.Mapper.OrderMapper;
import com.example.demo.Repository.CartItemRepository;
import com.example.demo.Repository.OrderItemRepository;
import com.example.demo.Repository.OrderRepository;
import com.example.demo.Service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    private User testUser;
    private Product testProduct;
    private CartItem testCartItem;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        // User entitás létrehozása
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setPhone("1234567890");
        testUser.setRole(Role.USER);

        // Product entitás létrehozása
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setDescription("A test product");
        testProduct.setPrice(new BigDecimal("100.00"));
        testProduct.setImageUrl("http://example.com/image.jpg");

        // CartItem entitás létrehozása
        testCartItem = new CartItem();
        testCartItem.setId(1L);
        testCartItem.setUser(testUser);
        testCartItem.setProduct(testProduct);
        testCartItem.setQuantity(2);

        // Order entitás létrehozása
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUser(testUser);
        testOrder.setTotalAmount(new BigDecimal("200.00"));
        testOrder.setShippingAddress("123 Main St");
        testOrder.setShippingName("Test User");
        testOrder.setShippingEmail("test@example.com");
        testOrder.setShippingPhone("1234567890");
    }

    /**
     * Test 1: Checkout sikeres - rendelés létrehozása kosárból
     */
    @Test
    void testCheckoutSuccess() {
        // ARRANGE
        CheckoutRequestDto request = new CheckoutRequestDto();
        request.setShippingAddress("123 Main St");
        request.setShippingName("Test User");
        request.setShippingEmail("test@example.com");
        request.setShippingPhone("1234567890");
        request.setPaymentMethod(PaymentMethod.CARD);

        List<CartItem> cartItems = List.of(testCartItem);
        when(cartItemRepository.findByUser(testUser)).thenReturn(cartItems);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        OrderResponseDto expectedDto = new OrderResponseDto();
        expectedDto.setId(1L);
        when(orderMapper.toOrderResponseDto(testOrder)).thenReturn(expectedDto);

        // ACT
        OrderResponseDto result = orderService.checkout(testUser, request);

        // ASSERT
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(cartItemRepository, times(1)).findByUser(testUser);
        verify(cartItemRepository, times(1)).deleteAll(cartItems);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    /**
     * Test 2: Checkout sikertelen - üres kosár
     */
    @Test
    void testCheckoutEmptyCart() {
        // ARRANGE
        CheckoutRequestDto request = new CheckoutRequestDto();
        request.setShippingAddress("123 Main St");
        request.setShippingName("Test User");
        request.setShippingEmail("test@example.com");
        request.setShippingPhone("1234567890");
        request.setPaymentMethod(PaymentMethod.CASH);

        when(cartItemRepository.findByUser(testUser)).thenReturn(new ArrayList<>());

        // ACT & ASSERT
        assertThrows(EmptyCartException.class, () -> {
            orderService.checkout(testUser, request);
        });

        verify(cartItemRepository, times(1)).findByUser(testUser);
        verify(orderRepository, never()).save(any(Order.class));
        verify(cartItemRepository, never()).deleteAll(any());
    }

    /**
     * Test 3: Kosár törlése után checkout
     */
    @Test
    void testCheckoutClearsCart() {
        // ARRANGE
        CheckoutRequestDto request = new CheckoutRequestDto();
        request.setShippingAddress("123 Main St");
        request.setShippingName("Test User");
        request.setShippingEmail("test@example.com");
        request.setShippingPhone("1234567890");
        request.setPaymentMethod(PaymentMethod.CARD);

        List<CartItem> cartItems = List.of(testCartItem);
        when(cartItemRepository.findByUser(testUser)).thenReturn(cartItems);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        OrderResponseDto expectedDto = new OrderResponseDto();
        expectedDto.setId(1L);
        when(orderMapper.toOrderResponseDto(testOrder)).thenReturn(expectedDto);

        // ACT
        orderService.checkout(testUser, request);

        // ASSERT
        verify(cartItemRepository, times(1)).deleteAll(cartItems);
    }
}


