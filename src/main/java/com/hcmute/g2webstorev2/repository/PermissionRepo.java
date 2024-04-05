package com.hcmute.g2webstorev2.repository;

import com.hcmute.g2webstorev2.entity.Permission;
import com.hcmute.g2webstorev2.enums.AppPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepo extends JpaRepository<Permission, Integer> {
    Permission findByAppPermission(AppPermission permission);
}
