package com.amyfan.puzzlestore.services;

import com.amyfan.puzzlestore.entities.Puzzle;
import com.amyfan.puzzlestore.entities.User;
import com.amyfan.puzzlestore.entities.CartItem;
import com.amyfan.puzzlestore.repositories.PuzzleRepository;
import com.amyfan.puzzlestore.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    UserRepository userRepo;
    PuzzleRepository puzzleRepo;

    @Autowired
    public CartServiceImpl(UserRepository userRepo, PuzzleRepository puzzleRepo) {
        this.userRepo = userRepo;
        this.puzzleRepo = puzzleRepo;
    }

    public User getUser(String email) {
        return userRepo.findUserByEmail(email);
    }

    public Puzzle getPuzzle(String productId) {
        return puzzleRepo.findPuzzleByProductId(productId);
    }

    @Transactional
    public List<CartItem> getCart(String email) {
        return userRepo.findUserByEmail(email).getCart();
    }

    @Transactional
    public void addToCart(String email, String productId, int quantity) {
        User user = getUser(email);
        List<CartItem> cart = user.getCart();
        boolean exists = false;

        for (CartItem c : cart) {
            if (c.getPuzzle().getProductId().equals(productId)) {
                c.setQuantity(c.getQuantity() + quantity);
                exists = true;
            }
        }

        if (!exists) {
            cart.add(new CartItem(puzzleRepo.findPuzzleByProductId(productId), quantity));
        }

        for (CartItem c : user.getCart()) {
            System.out.println("ID: " + c.getPuzzle().getProductId() + " | Quantity: " + c.getQuantity());
        }

        user.setCart(cart);
        userRepo.save(user);
    }

    public void updateItem(String email, String productId, int quantity) {
        User user = getUser(email);
        List<CartItem> cart = user.getCart();

        for (CartItem c : cart) {
            if (c.getPuzzle().getProductId().equals(productId)) {
                c.setQuantity(quantity);
                if (c.getQuantity() == 0) cart.remove(c);

                user.setCart(cart);
                userRepo.save(user);
                return;
            }
        }
        System.err.println("Puzzle not found.");
    }

    public BigDecimal getSubtotal(String email) {
        List<CartItem> cart = getCart(email);
        BigDecimal subtotal = new BigDecimal("0.0");

        for (CartItem c : cart) {
            subtotal = subtotal.add(c.getPuzzle().getPrice().multiply(BigDecimal.valueOf(c.getQuantity())));
        }

        return subtotal.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTax(BigDecimal subtotal) {
        return subtotal.multiply(BigDecimal.valueOf(0.0825)).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getShipping(BigDecimal subtotal) {
        return subtotal.compareTo(BigDecimal.valueOf(75)) != -1 || subtotal.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO : BigDecimal.TEN.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotal(BigDecimal subtotal, BigDecimal tax, BigDecimal shipping) {
        return subtotal.add(tax).add(shipping).setScale(2, RoundingMode.HALF_UP);
    }

    public void removeInactive(String email) {
        User user = userRepo.findUserByEmail(email);
        List<CartItem> cart = new ArrayList<>();

        for (CartItem c : user.getCart()) {
            if (c.getPuzzle().getActive()) {
                cart.add(c);
            }
        }

        user.setCart(cart);
        userRepo.save(user);
    }

    public void removeCartItem(String productId, String email) {
        User user = userRepo.findUserByEmail(email);
        List<CartItem> cart = user.getCart();
        cart.removeIf(c -> c.getPuzzle().getProductId().equals(productId));

        user.setCart(cart);
        userRepo.save(user);
    }

    public boolean containsInactive(String email) {
        User user = userRepo.findUserByEmail(email);
        List<CartItem> cart = user.getCart();
        List<Boolean> active = cart.stream().map(c -> c.getPuzzle().getActive()).toList();

        return active.contains(false);
    }
}
