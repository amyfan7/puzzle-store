package com.amyfan.puzzlestore.repositories;

import com.amyfan.puzzlestore.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
