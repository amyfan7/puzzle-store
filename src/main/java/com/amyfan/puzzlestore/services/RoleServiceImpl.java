package com.amyfan.puzzlestore.services;

import com.amyfan.puzzlestore.entities.Role;
import com.amyfan.puzzlestore.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepo;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepo) {
        this.roleRepo= roleRepo;
    }

    public Role findRoleByName(String name) {
        return roleRepo.findRoleByNameContaining(name);
    }
}
