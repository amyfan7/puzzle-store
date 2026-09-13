package com.amyfan.puzzlestore.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Puzzle {
    @Id
    @NotBlank(message = "Cannot be blank")
    private String productId;

    @Column(nullable = false)
    @NotBlank(message = "Cannot be blank")
    private String name;

    @Column(nullable = false)
    @NotBlank(message = "Cannot be blank")
    private String manufacturerName;

    @ManyToOne
    @JoinColumn(name = "manufacturer_id")
    private Manufacturer manufacturer;

    @Column(nullable = false)
    @NotNull(message = "Cannot be blank")
    @Min(0)
    private Integer pieces;

    @Column(nullable = false)
    @NotBlank(message = "Cannot be blank")
    private String difficulty;

    @Column(nullable = false)
    @NotNull(message = "Cannot be blank")
    @Min(0)
    private BigDecimal price;

    @Column(nullable = false)
    @NotNull(message = "Cannot be blank")
    @Min(0)
    private BigDecimal wholesaleCost;

    @Column(nullable = false)
    @NotBlank(message = "Cannot be blank")
    private String category;

    @Column(nullable = false)
    @NotBlank(message = "Cannot be blank")
    private String shortDescription;

    @Column(nullable = false)
    @NotBlank(message = "Cannot be blank")
    private String longDescription;

    @Column(nullable = false)
    private Boolean active;

    @Column
    private LocalDate acquiredDate;

    public Puzzle() {}

    public Puzzle(String productId, String name, String manufacturerName,
                  Manufacturer manufacturer, Integer pieces, String difficulty,
                  BigDecimal price, BigDecimal wholesaleCost, String category,
                  String shortDescription, String longDescription) {
        this.productId = productId;
        this.name = name;
        this.manufacturerName = manufacturerName;
        this.manufacturer = manufacturer;
        this.pieces = pieces;
        this.difficulty = difficulty;
        this.price = price;
        this.wholesaleCost = wholesaleCost;
        this.category = category;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.active = true;
        this.acquiredDate = LocalDate.now();
    }

    public Puzzle(String productId, String name, String manufacturerName,
                  Manufacturer manufacturer, Integer pieces, String difficulty,
                  BigDecimal price, BigDecimal wholesaleCost, String category,
                  String shortDescription, String longDescription, LocalDate acquiredDate) {
        this.productId = productId;
        this.name = name;
        this.manufacturerName = manufacturerName;
        this.manufacturer = manufacturer;
        this.pieces = pieces;
        this.difficulty = difficulty;
        this.price = price;
        this.wholesaleCost = wholesaleCost;
        this.category = category;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.active = true;
        this.acquiredDate = acquiredDate;
    }

    public Puzzle(String productId, String name, String manufacturerName,
                  Manufacturer manufacturer, Integer pieces, String difficulty,
                  BigDecimal price, BigDecimal wholesaleCost, String category,
                  String shortDescription, String longDescription, Boolean active) {
        this.productId = productId;
        this.name = name;
        this.manufacturerName = manufacturerName;
        this.manufacturer = manufacturer;
        this.pieces = pieces;
        this.difficulty = difficulty;
        this.price = price;
        this.wholesaleCost = wholesaleCost;
        this.category = category;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.active = active;
        this.acquiredDate = LocalDate.now();
    }


    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Integer getPieces() {
        return pieces;
    }

    public void setPieces(Integer pieces) {
        this.pieces = pieces;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getWholesaleCost() {
        return wholesaleCost;
    }

    public void setWholesaleCost(BigDecimal wholesaleCost) {
        this.wholesaleCost = wholesaleCost;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDate getAcquiredDate() {
        return acquiredDate;
    }

    public void setAcquiredDate(LocalDate acquiredDate) {
        this.acquiredDate = acquiredDate;
    }
}
