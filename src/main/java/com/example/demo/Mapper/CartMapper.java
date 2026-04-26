package com.example.demo.Mapper;

import com.example.demo.Dto.CartItemResponseDto;
import com.example.demo.Entity.CartItem;
import org.springframework.stereotype.Component;

@Component
public class CartMapper {


    public CartItemResponseDto toCartItemResponseDto(CartItem cartItem) {
        return CartItemResponseDto.builder()
                .id(cartItem.getId())
                .userId(cartItem.getUser().getId())
                .userEmail(cartItem.getUser().getEmail())
                .productId(cartItem.getProduct().getId())
                .productName(cartItem.getProduct().getName())
                .productPrice(cartItem.getProduct().getPrice())
                .productDescription(cartItem.getProduct().getDescription())
                .productImageUrl(cartItem.getProduct().getImageUrl())
                .quantity(cartItem.getQuantity())
                .build();
    }
}

