package com.amyfan.puzzlestore.repositories;

import com.amyfan.puzzlestore.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findRoleByNameContaining(String name);
}
