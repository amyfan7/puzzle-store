package com.amyfan.puzzlestore.services;

import com.amyfan.puzzlestore.entities.Order;
import com.amyfan.puzzlestore.entities.User;
import com.amyfan.puzzlestore.security.CustomUserDetails;

public interface OrderService {
    void addOrderToUserHistory(User user, Order order);
    Order findOrderByOrderNumber(String orderNumber);
    void validateUser(CustomUserDetails user, String orderNumber);
}
