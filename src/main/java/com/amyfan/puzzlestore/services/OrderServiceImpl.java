package com.amyfan.puzzlestore.services;

import com.amyfan.puzzlestore.repositories.UserRepository;
import com.amyfan.puzzlestore.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amyfan.puzzlestore.repositories.OrderRepository;
import com.amyfan.puzzlestore.entities.User;
import com.amyfan.puzzlestore.entities.Order;

@Service
public class OrderServiceImpl implements OrderService {
    OrderRepository orderRepo;
    UserRepository userRepo;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepo, UserRepository userRepo) {
        this.orderRepo = orderRepo;
        this.userRepo = userRepo;
    }

    @Transactional
    public void addOrderToUserHistory(User user, Order order) {
        orderRepo.save(order);
    }

    public Order findOrderByOrderNumber(String orderNumber) {
        return orderRepo.findOrderByOrderNumber(orderNumber);
    }

    public void validateUser(CustomUserDetails user, String orderNumber) {
        User orderUser = orderRepo.findOrderByOrderNumber(orderNumber).getUser();
        User authUser = user.getUser();

        if (!orderUser.getEmail().equals(authUser.getEmail())) {
            throw new RuntimeException("Access not granted");
        }
    }
}
