package com.amyfan.puzzlestore.services;

import com.amyfan.puzzlestore.entities.Puzzle;
import com.amyfan.puzzlestore.entities.User;
import com.amyfan.puzzlestore.entities.CartItem;

import java.math.BigDecimal;
import java.util.List;

public interface CartService {
    User getUser(String email);
    Puzzle getPuzzle(String productId);
    List<CartItem> getCart(String email);
    void addToCart(String email, String productId, int quantity);
    void updateItem(String email, String productId, int quantity);
    BigDecimal getSubtotal(String email);
    BigDecimal getTax(BigDecimal subtotal);
    BigDecimal getShipping(BigDecimal subtotal);
    BigDecimal getTotal(BigDecimal subtotal, BigDecimal tax, BigDecimal shipping);
    void removeInactive(String email);
    void removeCartItem(String productId, String email);
    boolean containsInactive(String email);
}
