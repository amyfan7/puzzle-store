package com.amyfan.puzzlestore.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

@Entity
public class CartItem {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name="product_id")
    private Puzzle puzzle;

    @Column
    @Min(0)
    private int quantity;

    public CartItem() {}

    public CartItem(Puzzle puzzle, int quantity) {
        this.puzzle = puzzle;
        this.quantity = quantity;
    }

    public Puzzle getPuzzle() {
        return puzzle;
    }

    public void setPuzzle(Puzzle puzzle) {
        this.puzzle = puzzle;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
