package com.moscepa.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "moscepa_role_permissions")
public class RolePermission {

    @Id
    @Column(name = "role_name", nullable = false)
    private String roleName; // Ex: "ADMIN", "TUTEUR"

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "moscepa_allowed_features", joinColumns = @JoinColumn(name = "role_name"))
    @Column(name = "feature_key")
    private Set<String> allowedFeatures = new HashSet<>();

    // Getters et Setters
    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Set<String> getAllowedFeatures() {
        return allowedFeatures;
    }

    public void setAllowedFeatures(Set<String> allowedFeatures) {
        this.allowedFeatures = allowedFeatures;
    }
}
