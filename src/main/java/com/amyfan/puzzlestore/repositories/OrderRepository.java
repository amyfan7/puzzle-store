package com.amyfan.puzzlestore.repositories;

import com.amyfan.puzzlestore.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    Order findOrderByOrderNumber(String orderNumber);
}
