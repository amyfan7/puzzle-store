package com.amyfan.puzzlestore.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class ShippingInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Cannot be blank")
    @Column(nullable = false)
    private String fullName;

    @NotBlank(message = "Cannot be blank")
    @Column(nullable = false)
    private String address1;

    @Column
    private String address2;

    @NotBlank(message = "Cannot be blank")
    @Column(nullable = false)
    private String city;

    @NotBlank(message = "Cannot be blank")
    @Column(nullable = false)
    private String state;

    @NotBlank(message = "Cannot be blank")
    @Size(min = 5, max = 5, message = "Format is XXXXX")
    @Column(nullable = false)
    private String zipCode;

//    @NotBlank(message = "Cannot be blank")
//    @Column(nullable = false)
//    private String cardholderName;
//
//    @NotBlank(message = "Cannot be blank")
//    @Column(nullable = false)
//    private String cardNumber;
//
//    @NotBlank(message = "Cannot be blank")
//    @Size(min = 2, max = 2, message = "Format is MM")
//    @Column(nullable = false)
//    private String expMonth;
//
//    @NotBlank(message = "Cannot be blank")
//    @Size(min = 2, max = 2, message = "Format is YY")
//    @Column(nullable = false)
//    private String expYear;
//
//    @NotBlank(message = "Cannot be blank")
//    @Column(nullable = false)
//    private String cvv;

    public ShippingInfo() {}

    public ShippingInfo(String fullName, String address1, String address2, String city,
                        String state, String zipCode) {
        this.fullName = fullName;
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
//        this.cardholderName = cardholderName;
//        this.cardNumber = cardNumber;
//        this.expMonth = expMonth;
//        this.expYear = expYear;
//        this.cvv = cvv;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

//    public String getCardholderName() {
//        return cardholderName;
//    }
//
//    public void setCardholderName(String cardholderName) {
//        this.cardholderName = cardholderName;
//    }
//
//    public String getCardNumber() {
//        return cardNumber;
//    }
//
//    public void setCardNumber(String cardNumber) {
//        this.cardNumber = cardNumber;
//    }
//
//    public String getExpMonth() {
//        return expMonth;
//    }
//
//    public void setExpMonth(String expMonth) {
//        this.expMonth = expMonth;
//    }
//
//    public String getExpYear() {
//        return expYear;
//    }
//
//    public void setExpYear(String expYear) {
//        this.expYear = expYear;
//    }
//
//    public String getCvv() {
//        return cvv;
//    }
//
//    public void setCvv(String cvv) {
//        this.cvv = cvv;
//    }
}
