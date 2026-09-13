package com.amyfan.puzzlestore.repositories;

import com.amyfan.puzzlestore.entities.OrderNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderNumberRepository extends JpaRepository<OrderNumber, Long> {
}
