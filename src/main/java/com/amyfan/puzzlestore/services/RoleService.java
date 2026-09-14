package com.amyfan.puzzlestore.services;

import com.amyfan.puzzlestore.entities.Role;

public interface RoleService {
    Role findRoleByName(String name);
}
