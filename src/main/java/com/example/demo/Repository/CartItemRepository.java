package com.example.demo.Repository;

import com.example.demo.Entity.CartItem;
import com.example.demo.Entity.Product;
import com.example.demo.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUser(User user);

    @Query("SELECT ci FROM CartItem ci " +
           "JOIN FETCH ci.user " +
           "JOIN FETCH ci.product " +
           "WHERE ci.user.id = :userId")
    List<CartItem> findByUserWithEagerLoad(@Param("userId") Long userId);

    Optional<CartItem> findByUserAndProduct(User user, Product product);

    void deleteByUser(User user);
}