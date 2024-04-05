package com.hcmute.g2webstorev2;

import com.hcmute.g2webstorev2.entity.Admin;
import com.hcmute.g2webstorev2.entity.Role;
import com.hcmute.g2webstorev2.exception.ResourceNotFoundException;
import com.hcmute.g2webstorev2.repository.AdminRepo;
import com.hcmute.g2webstorev2.repository.RoleRepo;
import com.hcmute.g2webstorev2.service.PermissionService;
import com.hcmute.g2webstorev2.service.RoleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static com.hcmute.g2webstorev2.enums.AppRole.ADMIN;

@SpringBootApplication
public class G2WebStoreV2Application {

    public static void main(String[] args) {
        SpringApplication.run(G2WebStoreV2Application.class, args);
    }

//    @Bean
//    @Transactional
//    CommandLineRunner run(AdminRepo userRepo,
//                          RoleRepo roleRepo,
//                          PermissionService permissionService,
//                          RoleService roleService,
//                          PasswordEncoder passwordEncoder) {
//        return args -> {
//            permissionService.createDefaultPermission();
//            roleService.createDefaultRole();
//
//            if (userRepo.findAll().isEmpty()) {
//                Admin appUser = new Admin();
//                Role role = roleRepo.findByAppRole(ADMIN);
//                if (role == null)
//                    throw new ResourceNotFoundException("Role ADMIN not found");
//
//                appUser.setRole(role);
//                appUser.setEmail("admin@gmail.com");
//                appUser.setPassword(passwordEncoder.encode("password"));
//                userRepo.save(appUser);
//            }
//        };
//    }
}
