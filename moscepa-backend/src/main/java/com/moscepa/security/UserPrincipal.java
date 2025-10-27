// Fichier : src/main/java/com/moscepa/security/UserPrincipal.java

package com.moscepa.security;

// =================================================================
// === LES IMPORTS MANQUANTS SONT AJOUTÉS ICI ===
// =================================================================
import com.moscepa.entity.Utilisateur;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.ArrayList; // Ajout pour la liste mutable
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
// =================================================================

public class UserPrincipal implements UserDetails {
    private Long id;
    private String email;
    private String motDePasse;
    private Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Long id, String email, String motDePasse, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.motDePasse = motDePasse;
        this.authorities = authorities;
    }

    public static UserPrincipal create(Utilisateur utilisateur) {
        // CORRECTION : On suppose que getRole() retourne un Enum. Si c'est une String, il faut enlever .name()
        List<GrantedAuthority> roleAuthorities = new ArrayList<>();
        if (utilisateur.getRole() != null) {
            roleAuthorities.add(new SimpleGrantedAuthority("ROLE_" + utilisateur.getRole().name()));
        }

        // Ajouter les permissions spécifiques
        if (utilisateur.getPermissions() != null && !utilisateur.getPermissions().isEmpty()) {
            List<GrantedAuthority> permissionAuthorities = utilisateur.getPermissions().stream()
                    .map(permission -> new SimpleGrantedAuthority(permission.getNom().replace(" ", "_").toUpperCase()))
                    .collect(Collectors.toList());
            roleAuthorities.addAll(permissionAuthorities);
        }

        return new UserPrincipal(
                utilisateur.getId(),
                utilisateur.getEmail(),
                utilisateur.getMotDePasse(),
                roleAuthorities
        );
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return motDePasse;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPrincipal that = (UserPrincipal) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
