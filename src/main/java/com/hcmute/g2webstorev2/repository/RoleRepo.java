package com.hcmute.g2webstorev2.repository;

import com.hcmute.g2webstorev2.entity.Role;
import com.hcmute.g2webstorev2.enums.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RoleRepo extends JpaRepository<Role, Integer> {
    Role findByAppRole(AppRole role);
}
