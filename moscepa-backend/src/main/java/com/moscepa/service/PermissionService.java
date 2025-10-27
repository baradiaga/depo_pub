package com.moscepa.service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.moscepa.entity.RolePermission;
import com.moscepa.repository.RolePermissionRepository;

@Service
public class PermissionService {

    @Autowired
    private RolePermissionRepository permissionRepository;

    @Transactional(readOnly = true)
    public Map<String, Set<String>> getAllPermissionsByRole() {
        return permissionRepository.findAll().stream().collect(
                Collectors.toMap(RolePermission::getRoleName, RolePermission::getAllowedFeatures));
    }

    @Transactional
    public void updatePermissionsForRole(String roleName, Set<String> allowedFeatures) {
        RolePermission permissions =
                permissionRepository.findById(roleName).orElse(new RolePermission());
        permissions.setRoleName(roleName);
        permissions.setAllowedFeatures(allowedFeatures);
        permissionRepository.save(permissions);
    }
}
