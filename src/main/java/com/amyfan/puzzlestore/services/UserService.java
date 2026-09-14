package com.amyfan.puzzlestore.services;

import com.amyfan.puzzlestore.entities.User;
import com.amyfan.puzzlestore.security.CustomUserDetails;


public interface UserService {
    void addUser(User user);
    void addAdminUser(User user);
    boolean validatePassword(String email, String password);
    boolean emailExists(String email);
    void emptyCart(String email);
    User findUserByEmail(String email);
    void updateName(String email, String first, String last);
    void updatePassword(String email, String password);
    void validateAdmin(CustomUserDetails user);
}
