package com.amyfan.puzzlestore.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class Password {
    @NotBlank(message = "Cannot be blank")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String current;

    @NotBlank(message = "Cannot be blank")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String newPassword;

    @NotBlank(message = "Cannot be blank")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String confirm;

    public Password() {}

    public Password (String current, String newPassword, String confirm) {
        this.current = current;
        this.newPassword = newPassword;
        this.confirm = confirm;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirm() {
        return confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }
}
