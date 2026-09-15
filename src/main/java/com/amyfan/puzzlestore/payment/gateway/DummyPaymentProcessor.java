package com.amyfan.puzzlestore.payment.gateway;

import com.amyfan.puzzlestore.payment.model.PaymentRequest;
import com.amyfan.puzzlestore.payment.model.PaymentResult;
import com.amyfan.puzzlestore.payment.model.PaymentStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class DummyPaymentProcessor implements PaymentProcessor {

    private static final Set<String> DECLINED_CARDS = Set.of(
            "4000000000000002",
            "4111111111111112",
            "4222222222222220"
    );

    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        List<String> messages = List.of(validateRequest(request),
                validateRequiredText(request.getCardholderName(), "Cardholder name"),
                validateRequiredText(request.getCardNumber(), "Card number"),
                validateRequiredText(request.getExpirationMonth(), "Expiration month"),
                validateRequiredText(request.getExpirationYear(), "Expiration year"),
                validateRequiredText(request.getCvv(), "CVV"));

        for (String m : messages) {
            if (!m.isBlank()) {
                return new PaymentResult(
                        PaymentStatus.DECLINED,
                        m,
                        null
                );
            }
        }

        String normalizedCardNumber = normalizeCardNumber(request.getCardNumber());

        if (DECLINED_CARDS.contains(normalizedCardNumber)) {
            return new PaymentResult(
                    PaymentStatus.DECLINED,
                    "The payment was declined by the payment processor.",
                    null
            );
        }

        return new PaymentResult(
                PaymentStatus.APPROVED,
                "Payment approved.",
                UUID.randomUUID().toString()
        );
    }

    private String validateRequest(PaymentRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Payment request cannot be null");
        }

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return "Payment amount must be greater than 0";
        }

        String normalizedCardNumber = normalizeCardNumber(request.getCardNumber());
        if (!normalizedCardNumber.matches("\\d{13,19}")) {
            return "Card number must contain 13 to 19 digits";
        }

        if (!request.getCvv().matches("\\d{3,4}")) {
            return "CVV must be 3 or 4 digits";
        }

        if (!request.getExpirationMonth().matches("\\d{2}")) {
            return "Expiration month must be 2 digits";
        }

        if (!request.getExpirationYear().matches("\\d{4}")) {
            return "Expiration year must be 4 digits";
        }
        return "";
    }

    private String validateRequiredText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            return fieldName + " cannot be blank";
        }
        return "";
    }

    private String normalizeCardNumber(String cardNumber) {
        return cardNumber.replaceAll("[^\\d]", "");
    }
}
