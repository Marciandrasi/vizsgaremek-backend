package com.example.demo.Controller;

import com.example.demo.Dto.CartItemRequestDto;
import com.example.demo.Dto.CartItemResponseDto;
import com.example.demo.Dto.CartSummaryDto;
import com.example.demo.Entity.User;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    public CartController(CartService cartService, UserRepository userRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Felhasználó nem található!"));
    }

    @GetMapping
    public ResponseEntity<CartSummaryDto> getCart(Principal principal) {
        User user = getAuthenticatedUser(principal);
        return ResponseEntity.ok(cartService.getCartItems(user));
    }

    @PostMapping
    public ResponseEntity<CartItemResponseDto> addToCart(
            Principal principal,
            @Valid @RequestBody CartItemRequestDto request) {
        User user = getAuthenticatedUser(principal);
        return ResponseEntity.ok(cartService.addToCart(user, request.getProductId(), request.getQuantity()));
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> removeCartItem(Principal principal, @PathVariable Long cartItemId) {
        User user = getAuthenticatedUser(principal);
        cartService.removeCartItem(user, cartItemId);
        return ResponseEntity.ok().build(); // 200 OK, tartalom nélkül
    }

    @PutMapping("/{cartItemId}")
    public ResponseEntity<CartItemResponseDto> updateCartItemQuantity(
            Principal principal,
            @PathVariable Long cartItemId,
            @RequestBody Map<String, Integer> request) {
        Integer quantity = request.get("quantity");
        if (quantity == null || quantity < 1 || quantity > 1000) {
            throw new IllegalArgumentException("Mennyiségnek 1 és 1000 között kell lennie");
        }
        User user = getAuthenticatedUser(principal);
        return ResponseEntity.ok(cartService.updateCartItemQuantity(user, cartItemId, quantity));
    }
}