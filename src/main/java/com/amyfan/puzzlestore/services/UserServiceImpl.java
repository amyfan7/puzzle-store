package com.amyfan.puzzlestore.services;

import com.amyfan.puzzlestore.entities.User;
import com.amyfan.puzzlestore.repositories.RoleRepository;
import com.amyfan.puzzlestore.repositories.UserRepository;
import com.amyfan.puzzlestore.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepo, RoleRepository roleRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public void addUser(User user) {
        String password = passwordEncoder.encode(user.getPassword());
        user.setPassword(password);
        user.setRole(roleRepo.findRoleByNameContaining("CUSTOMER"));

        userRepo.save(user);
    }

    public void addAdminUser(User user) {
        String password = passwordEncoder.encode(user.getPassword());
        user.setPassword(password);
        userRepo.save(user);
    }

    public boolean emailExists(String email) {
        return userRepo.findAll().stream().map(User::getEmail).toList().contains(email);
    }

    public boolean validatePassword(String email, String password) {
        User user = userRepo.findUserByEmail(email);
        return passwordEncoder.matches(password, user.getPassword());
    }

    public void emptyCart(String email) {
        User user = userRepo.findUserByEmail(email);
        user.emptyCart();
        userRepo.save(user);
    }

    public User findUserByEmail(String email) {
        return userRepo.findUserByEmail(email);
    }

    public void updateName(String email, String first, String last) {
        User user = userRepo.findUserByEmail(email);

        user.setFirstName(first);
        user.setLastName(last);
        userRepo.save(user);
    }

    public void updatePassword(String email, String password) {
        User user = userRepo.findUserByEmail(email);

        user.setPassword(passwordEncoder.encode(password));
        userRepo.save(user);
    }

    public void validateAdmin(CustomUserDetails user) {
        if (user == null || !userRepo.findUserByEmail(user.getEmail()).getRole().getName().equals("ADMIN")) {
            throw new RuntimeException("Do not have access to page.");
        }
    }
}
