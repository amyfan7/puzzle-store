package com.amyfan.puzzlestore.dtos;

import jakarta.validation.constraints.NotBlank;

public class Name {
    @NotBlank(message = "Cannot be blank")
    private String first;

    @NotBlank(message = "Cannot be blank")
    private String last;

    public Name() {}

    public Name(String first, String last) {
        this.first = first;
        this.last = last;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }
}
