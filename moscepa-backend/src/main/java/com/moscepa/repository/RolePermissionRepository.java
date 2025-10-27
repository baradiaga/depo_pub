package com.moscepa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.moscepa.entity.RolePermission;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, String> {
    // Cette interface peut rester vide.
    // JpaRepository fournit déjà toutes les méthodes dont nous avons besoin (findAll, findById,
    // save).
}
