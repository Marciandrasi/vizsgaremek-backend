package com.example.demo;

import com.example.demo.Dto.CartItemResponseDto;
import com.example.demo.Entity.CartItem;
import com.example.demo.Entity.Product;
import com.example.demo.Entity.Role;
import com.example.demo.Entity.User;
import com.example.demo.Exception.CartItemNotFoundException;
import com.example.demo.Exception.UnauthorizedCartAccessException;
import com.example.demo.Mapper.CartMapper;
import com.example.demo.Repository.CartItemRepository;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartMapper cartMapper;

    @InjectMocks
    private CartService cartService;

    private User testUser;
    private User otherUser;
    private Product testProduct;
    private CartItem testCartItem;
    private CartItemResponseDto testCartItemDto;

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

        // Másik user (IDOR teszt számára)
        otherUser = new User();
        otherUser.setId(2L);
        otherUser.setName("Other User");
        otherUser.setEmail("other@example.com");
        otherUser.setPassword("password");
        otherUser.setRole(Role.USER);

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

        // CartItemResponseDto
        testCartItemDto = new CartItemResponseDto();
        testCartItemDto.setId(1L);
        testCartItemDto.setQuantity(2);
    }

    /**
     * Test 1: Kosár lekérése - üres kosár
     */
    @Test
    void testGetCartItemsEmpty() {
        // ARRANGE
        when(cartItemRepository.findByUser(testUser)).thenReturn(List.of());

        // ACT
        List<CartItemResponseDto> result = cartService.getCartItems(testUser);

        // ASSERT
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(cartItemRepository, times(1)).findByUser(testUser);
    }

    /**
     * Test 2: Kosár lekérése - tételek vannak
     */
    @Test
    void testGetCartItemsWithItems() {
        // ARRANGE
        List<CartItem> cartItems = List.of(testCartItem);
        when(cartItemRepository.findByUser(testUser)).thenReturn(cartItems);
        when(cartMapper.toCartItemResponseDto(testCartItem)).thenReturn(testCartItemDto);

        // ACT
        List<CartItemResponseDto> result = cartService.getCartItems(testUser);

        // ASSERT
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(cartItemRepository, times(1)).findByUser(testUser);
        verify(cartMapper, times(1)).toCartItemResponseDto(testCartItem);
    }

    /**
     * Test 3: Termék hozzáadása a kosárhoz - új tétel
     */
    @Test
    void testAddToCartNewItem() {
        // ARRANGE
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(cartItemRepository.findByUserAndProduct(testUser, testProduct)).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(testCartItem);
        when(cartMapper.toCartItemResponseDto(testCartItem)).thenReturn(testCartItemDto);

        // ACT
        CartItemResponseDto result = cartService.addToCart(testUser, 1L, 2);

        // ASSERT
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(productRepository, times(1)).findById(1L);
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    /**
     * Test 4: Termék hozzáadása a kosárhoz - meglévő tétel mennyiségének növelése
     */
    @Test
    void testAddToCartExistingItem() {
        // ARRANGE
        CartItem existingItem = new CartItem();
        existingItem.setId(1L);
        existingItem.setUser(testUser);
        existingItem.setProduct(testProduct);
        existingItem.setQuantity(2);

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(cartItemRepository.findByUserAndProduct(testUser, testProduct)).thenReturn(Optional.of(existingItem));
        when(cartItemRepository.save(existingItem)).thenReturn(existingItem);
        when(cartMapper.toCartItemResponseDto(existingItem)).thenReturn(testCartItemDto);

        // ACT
        CartItemResponseDto result = cartService.addToCart(testUser, 1L, 1);

        // ASSERT
        assertNotNull(result);
        assertEquals(3, existingItem.getQuantity()); // 2 + 1 = 3
        verify(productRepository, times(1)).findById(1L);
        verify(cartItemRepository, times(1)).save(existingItem);
    }

    /**
     * Test 5: Kosárból tétel törlése - sikeres
     */
    @Test
    void testRemoveCartItemSuccess() {
        // ARRANGE
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(testCartItem));

        // ACT
        cartService.removeCartItem(testUser, 1L);

        // ASSERT
        verify(cartItemRepository, times(1)).findById(1L);
        verify(cartItemRepository, times(1)).delete(testCartItem);
    }

    /**
     * Test 6: Kosárból tétel törlése - IDOR sebezhetőség megelőzése (más user)
     */
    @Test
    void testRemoveCartItemUnauthorized() {
        // ARRANGE
        CartItem otherUserItem = new CartItem();
        otherUserItem.setId(1L);
        otherUserItem.setUser(otherUser); // Más user!
        otherUserItem.setProduct(testProduct);
        otherUserItem.setQuantity(2);

        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(otherUserItem));

        // ACT & ASSERT
        assertThrows(UnauthorizedCartAccessException.class, () -> {
            cartService.removeCartItem(testUser, 1L);
        });

        verify(cartItemRepository, never()).delete(any());
    }

    /**
     * Test 7: Mennyiség frissítése - sikeres
     */
    @Test
    void testUpdateCartItemQuantitySuccess() {
        // ARRANGE
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(testCartItem));
        when(cartItemRepository.save(testCartItem)).thenReturn(testCartItem);
        when(cartMapper.toCartItemResponseDto(testCartItem)).thenReturn(testCartItemDto);

        // ACT
        CartItemResponseDto result = cartService.updateCartItemQuantity(testUser, 1L, 5);

        // ASSERT
        assertNotNull(result);
        assertEquals(5, testCartItem.getQuantity());
        verify(cartItemRepository, times(1)).save(testCartItem);
    }

    /**
     * Test 8: Mennyiség frissítése - IDOR sebezhetőség megelőzése (más user)
     */
    @Test
    void testUpdateCartItemQuantityUnauthorized() {
        // ARRANGE
        CartItem otherUserItem = new CartItem();
        otherUserItem.setId(1L);
        otherUserItem.setUser(otherUser); // Más user!
        otherUserItem.setProduct(testProduct);
        otherUserItem.setQuantity(2);

        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(otherUserItem));

        // ACT & ASSERT
        assertThrows(UnauthorizedCartAccessException.class, () -> {
            cartService.updateCartItemQuantity(testUser, 1L, 5);
        });

        verify(cartItemRepository, never()).save(any());
    }

    /**
     * Test 9: Nem létező tétel törlésének kísérlete
     */
    @Test
    void testRemoveCartItemNotFound() {
        // ARRANGE
        when(cartItemRepository.findById(999L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(CartItemNotFoundException.class, () -> {
            cartService.removeCartItem(testUser, 999L);
        });

        verify(cartItemRepository, never()).delete(any());
    }
}

