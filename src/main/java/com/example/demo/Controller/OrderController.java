package com.example.demo.Controller;

import com.example.demo.Dto.CheckoutRequestDto;
import com.example.demo.Dto.OrderResponseDto;
import com.example.demo.Entity.User;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    public OrderController(OrderService orderService, UserRepository userRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Felhasználó nem található!"));
    }


    @PostMapping
    public ResponseEntity<OrderResponseDto> checkout(
            Authentication authentication,
            @Valid @RequestBody CheckoutRequestDto request) {

        User user = getAuthenticatedUser(authentication);

        OrderResponseDto response = orderService.checkout(user, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getOrders(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        return ResponseEntity.ok(orderService.getUserOrders(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(
            Authentication authentication,
            @PathVariable Long id) {

        User user = getAuthenticatedUser(authentication);
        return ResponseEntity.ok(orderService.getOrderById(user, id));
    }
}

