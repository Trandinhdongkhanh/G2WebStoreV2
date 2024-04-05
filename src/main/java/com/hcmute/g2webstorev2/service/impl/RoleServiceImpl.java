package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.entity.Permission;
import com.hcmute.g2webstorev2.entity.Role;
import com.hcmute.g2webstorev2.repository.PermissionRepo;
import com.hcmute.g2webstorev2.repository.RoleRepo;
import com.hcmute.g2webstorev2.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static com.hcmute.g2webstorev2.enums.AppPermission.*;
import static com.hcmute.g2webstorev2.enums.AppRole.*;

@Service
@Slf4j
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private PermissionRepo permissionRepo;
    @Transactional
    public void createDefaultRole() {
        if (!roleRepo.findAll().isEmpty()) return;
        try {
            Permission createProduct = permissionRepo.findByAppPermission(CREATE_PRODUCT);
            Permission readProduct = permissionRepo.findByAppPermission(READ_PRODUCT);
            Permission updateProduct = permissionRepo.findByAppPermission(UPDATE_PRODUCT);
            Permission deleteProduct = permissionRepo.findByAppPermission(DELETE_PRODUCT);

            Permission createPromotion = permissionRepo.findByAppPermission(CREATE_PROMOTION);
            Permission readPromotion = permissionRepo.findByAppPermission(READ_PROMOTION);
            Permission updatePromotion = permissionRepo.findByAppPermission(UPDATE_PROMOTION);
            Permission deletePromotion = permissionRepo.findByAppPermission(DELETE_PROMOTION);

            Permission createOrder = permissionRepo.findByAppPermission(CREATE_ORDER);
            Permission readOrder = permissionRepo.findByAppPermission(READ_ORDER);
            Permission updateOrder = permissionRepo.findByAppPermission(UPDATE_ORDER);
            Permission deleteOrder = permissionRepo.findByAppPermission(DELETE_ORDER);

            Permission createReview = permissionRepo.findByAppPermission(CREATE_REVIEW);
            Permission readReview = permissionRepo.findByAppPermission(READ_REVIEW);
            Permission updateReview = permissionRepo.findByAppPermission(UPDATE_REVIEW);
            Permission deleteReview = permissionRepo.findByAppPermission(DELETE_REVIEW);

            Permission createUser = permissionRepo.findByAppPermission(CREATE_USER);
            Permission readUser = permissionRepo.findByAppPermission(READ_USER);
            Permission updateUser = permissionRepo.findByAppPermission(UPDATE_USER);
            Permission deleteUser = permissionRepo.findByAppPermission(DELETE_USER);

            roleRepo.save(new Role(ADMIN, new HashSet<>(permissionRepo.findAll())));
            roleRepo.save(new Role(
                    CUSTOMER,
                    null    //since we don't have to distinguish between users, we only need to check the user permissions
            ));
            roleRepo.save(new Role(
                    SELLER_PROMOTION_ACCESS,
                    Set.of(createPromotion, updatePromotion, readPromotion, deletePromotion)
            ));
            roleRepo.save(new Role(
                    SELLER_FULL_ACCESS,
                    Set.of(createProduct, readProduct, updateProduct, deleteProduct,
                            createPromotion, readPromotion, updatePromotion, deletePromotion,
                            readOrder, updateOrder, deleteOrder,
                            createReview, readReview, updateReview, deleteReview,
                            createUser, readUser, updateUser, deleteUser)
            ));
            roleRepo.save(new Role(
                    SELLER_PRODUCT_ACCESS,
                    Set.of(createProduct, readProduct, updateProduct, deleteProduct)
            ));
            roleRepo.save(new Role(
                    SELLER_ORDER_MANAGEMENT,
                    Set.of(readOrder, updateOrder, deleteOrder)
            ));
            roleRepo.save(new Role(
                    SELLER_READ_ONLY,
                    Set.of(readProduct, readPromotion, readOrder, readReview, readUser)
            ));
            roleRepo.save(new Role(
                    JUNIOR_CHAT_AGENT,
                    Set.of(createReview, readReview, updateReview, deleteReview)
            ));

            log.info("Roles created");

        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

}
