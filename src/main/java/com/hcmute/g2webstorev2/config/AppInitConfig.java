package com.hcmute.g2webstorev2.config;

import com.hcmute.g2webstorev2.entity.Admin;
import com.hcmute.g2webstorev2.entity.Role;
import com.hcmute.g2webstorev2.exception.ResourceNotFoundException;
import com.hcmute.g2webstorev2.repository.AdminRepo;
import com.hcmute.g2webstorev2.repository.RoleRepo;
import com.hcmute.g2webstorev2.service.PermissionService;
import com.hcmute.g2webstorev2.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static com.hcmute.g2webstorev2.enums.AppRole.ADMIN;

@Configuration
@Slf4j
public class AppInitConfig {
    @Bean
    @Transactional
    CommandLineRunner run(AdminRepo userRepo,
                          RoleRepo roleRepo,
                          PermissionService permissionService,
                          RoleService roleService,
                          PasswordEncoder passwordEncoder) {
        return args -> {
            permissionService.createDefaultPermission();
            roleService.createDefaultRole();

            if (userRepo.findAll().isEmpty()) {
                Admin appUser = new Admin();
                Role role = roleRepo.findByAppRole(ADMIN);
                if (role == null)
                    throw new ResourceNotFoundException("Role ADMIN not found");

                appUser.setRole(role);
                appUser.setEmail("admin@gmail.com");
                appUser.setPassword(passwordEncoder.encode("password"));
                userRepo.save(appUser);
                log.warn("Admin user has been created with default password, please change it");
            }
        };
    }
}
