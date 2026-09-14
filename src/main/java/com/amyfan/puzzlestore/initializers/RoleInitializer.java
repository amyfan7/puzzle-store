package com.amyfan.puzzlestore.initializers;

import com.amyfan.puzzlestore.entities.Role;
import com.amyfan.puzzlestore.entities.User;
import com.amyfan.puzzlestore.repositories.RoleRepository;
import com.amyfan.puzzlestore.services.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoleInitializer {
    private final RoleRepository roleRepo;
    private final UserService userService;

    @Autowired
    public RoleInitializer(RoleRepository roleRepo, UserService userService) {
        this.roleRepo = roleRepo;
        this.userService = userService;
    }

    @PostConstruct
    public void init() {
        if (roleRepo.count() != 0) {
            System.out.println("Data already present - not executing role initializer.");
        } else {
            roleRepo.saveAll(List.of(new Role("CUSTOMER"), new Role("ADMIN")));
        }

        if (userService.findUserByEmail("admin.user@gmail.com") != null) {
            System.out.println("Data already present - not executing admin initializer.");
            return;
        }
        userService.addAdminUser(new User("Admin", "User",
                "admin.user@gmail.com", "admin123", roleRepo.findRoleByNameContaining("ADMIN")));
    }
}
