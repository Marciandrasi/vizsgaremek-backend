package com.example.demo.Service;

import com.example.demo.Dto.CartItemResponseDto;
import com.example.demo.Dto.CartSummaryDto;
import com.example.demo.Entity.CartItem;
import com.example.demo.Entity.Product;
import com.example.demo.Entity.User;
import com.example.demo.Exception.CartItemNotFoundException;
import com.example.demo.Exception.UnauthorizedCartAccessException;
import com.example.demo.Mapper.CartMapper;
import com.example.demo.Repository.CartItemRepository;
import com.example.demo.Repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;

    public CartService(CartItemRepository cartItemRepository, ProductRepository productRepository, CartMapper cartMapper) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.cartMapper = cartMapper;
    }

    @Transactional(readOnly = true)
    public CartSummaryDto getCartItems(User user) {
        List<CartItemResponseDto> items = cartItemRepository.findByUser(user).stream()
                .map(cartMapper::toCartItemResponseDto)
                .collect(Collectors.toList());

        BigDecimal total = items.stream()
                .map(item -> item.getProductPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartSummaryDto.builder()
                .items(items)
                .totalCartValue(total)
                .build();
    }

    @Transactional
    public CartItemResponseDto addToCart(User user, Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("A termék nem található!"));

        Optional<CartItem> existingItem = cartItemRepository.findByUserAndProduct(user, product);

        CartItem cartItem;
        if (existingItem.isPresent()) {
            cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem = cartItemRepository.save(cartItem);
        } else {
            CartItem newItem = new CartItem(null, user, product, quantity);
            cartItem = cartItemRepository.save(newItem);
        }

        return cartMapper.toCartItemResponseDto(cartItem);
    }

    @Transactional
    public void removeCartItem(User user, Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CartItemNotFoundException(cartItemId));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedCartAccessException(user.getId(), cartItemId);
        }

        cartItemRepository.delete(item);
    }

    @Transactional
    public CartItemResponseDto updateCartItemQuantity(User user, Long cartItemId, Integer quantity) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CartItemNotFoundException(cartItemId));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedCartAccessException(user.getId(), cartItemId);
        }

        item.setQuantity(quantity);
        CartItem updatedItem = cartItemRepository.save(item);
        return cartMapper.toCartItemResponseDto(updatedItem);
    }
}