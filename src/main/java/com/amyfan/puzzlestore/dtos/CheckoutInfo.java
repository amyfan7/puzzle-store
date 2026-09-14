package com.amyfan.puzzlestore.dtos;

import com.amyfan.puzzlestore.entities.ShippingInfo;
import com.amyfan.puzzlestore.payment.model.PaymentRequest;
import jakarta.validation.Valid;

public class CheckoutInfo {
    @Valid
    private ShippingInfo shipping;
    @Valid
    private PaymentRequest payment;

    public CheckoutInfo() {
        this.shipping = new ShippingInfo();
        this.payment = new PaymentRequest();
    }

    public CheckoutInfo(ShippingInfo shipping, PaymentRequest payment) {
        this.shipping = shipping;
        this.payment = payment;
    }

    public ShippingInfo getShipping() {
        return shipping;
    }

    public void setShipping(ShippingInfo shipping) {
        this.shipping = shipping;
    }

    public PaymentRequest getPayment() {
        return payment;
    }

    public void setPayment(PaymentRequest payment) {
        this.payment = payment;
    }
}
