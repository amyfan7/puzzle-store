package com.amyfan.puzzlestore.payment.gateway;

import com.amyfan.puzzlestore.payment.model.PaymentRequest;
import com.amyfan.puzzlestore.payment.model.PaymentResult;

public interface PaymentProcessor {
    PaymentResult processPayment(PaymentRequest request);
}
