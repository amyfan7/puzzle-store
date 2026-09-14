package com.amyfan.puzzlestore.services;

import com.amyfan.puzzlestore.entities.OrderNumber;
import com.amyfan.puzzlestore.repositories.OrderNumberRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class OrderNumberServiceImpl implements OrderNumberService {
    private OrderNumberRepository orderNumberRepo;

    public OrderNumberServiceImpl(OrderNumberRepository orderNumberRepo) {
        this.orderNumberRepo = orderNumberRepo;
    }

    public String generateOrderNumber(OrderNumber orderNumber) {
        String yyMM = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMM"));
        String dd = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd"));

        OrderNumber saved = orderNumberRepo.save(orderNumber);
        Long id = saved.getId();
        String number = "ORD-" + yyMM + "-" + dd + "0" + (id == null ? "0" : id.toString());
        saved.setOrderNumber(number);
        orderNumberRepo.save(saved);

        return number;
    }
}
