package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.entity.Permission;
import com.hcmute.g2webstorev2.repository.PermissionRepo;
import com.hcmute.g2webstorev2.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.hcmute.g2webstorev2.enums.AppPermission.*;

@Service
@Slf4j
public class PermissionServiceImpl implements PermissionService {
    @Autowired
    private PermissionRepo permissionRepo;
    @Override
    public void createDefaultPermission() {
        if (!permissionRepo.findAll().isEmpty()) return;
        try {
            List<Permission> permissions = defaultPermissions();
            permissionRepo.saveAll(permissions);
            log.info("Permissions created successfully");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private List<Permission> defaultPermissions() {
        List<Permission> permissions = new ArrayList<>();

        permissions.add(new Permission(CREATE_PRODUCT));
        permissions.add(new Permission(READ_PRODUCT));
        permissions.add(new Permission(UPDATE_PRODUCT));
        permissions.add(new Permission(DELETE_PRODUCT));

        permissions.add(new Permission(CREATE_ORDER));
        permissions.add(new Permission(READ_ORDER));
        permissions.add(new Permission(UPDATE_ORDER));
        permissions.add(new Permission(DELETE_ORDER));

        permissions.add(new Permission(CREATE_PROMOTION));
        permissions.add(new Permission(READ_PROMOTION));
        permissions.add(new Permission(UPDATE_PROMOTION));
        permissions.add(new Permission(DELETE_PROMOTION));

        permissions.add(new Permission(CREATE_REVIEW));
        permissions.add(new Permission(READ_REVIEW));
        permissions.add(new Permission(UPDATE_REVIEW));
        permissions.add(new Permission(DELETE_REVIEW));

        permissions.add(new Permission(CREATE_USER));
        permissions.add(new Permission(READ_USER));
        permissions.add(new Permission(UPDATE_USER));
        permissions.add(new Permission(DELETE_USER));

        permissions.add(new Permission(CREATE_CATEGORY));
        permissions.add(new Permission(READ_CATEGORY));
        permissions.add(new Permission(UPDATE_CATEGORY));
        permissions.add(new Permission(DELETE_CATEGORY));

        return permissions;
    }
}
