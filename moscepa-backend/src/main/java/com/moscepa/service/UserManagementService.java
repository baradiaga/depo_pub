// Fichier : src/main/java/com/moscepa/service/UserManagementService.java (Version finale et corrigée)

package com.moscepa.service;

import com.moscepa.dto.UserRegistrationDto;
import com.moscepa.dto.UserResponseDto;
import com.moscepa.entity.Role;
import com.moscepa.entity.Utilisateur;
import com.moscepa.repository.UtilisateurRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserManagementService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    // ... (Les autres méthodes comme createUser, getAllUsers, etc. sont inchangées)

    /**
     * Met à jour un utilisateur existant
     */
    public UserResponseDto updateUser(Long id, UserRegistrationDto userRegistrationDto) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + id));
        
        if (!utilisateur.getEmail().equals(userRegistrationDto.getEmail()) && 
            utilisateurRepository.existsByEmail(userRegistrationDto.getEmail())) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

        // Mises à jour des champs simples
        utilisateur.setNom(userRegistrationDto.getNom());
        utilisateur.setPrenom(userRegistrationDto.getPrenom());
        utilisateur.setEmail(userRegistrationDto.getEmail());
        utilisateur.setActif(userRegistrationDto.getActif() != null ? userRegistrationDto.getActif() : true);

        // ====================================================================
        // === CORRECTION APPLIQUÉE ICI                                     ===
        // ====================================================================
        // On ne met à jour le rôle que s'il est explicitement fourni dans la requête.
        // Si le DTO a un rôle null, on ne touche pas au rôle existant de l'utilisateur.
        if (userRegistrationDto.getRole() != null) {
            utilisateur.setRole(userRegistrationDto.getRole());
        }

        // La protection pour le mot de passe est déjà correcte :
        // On ne met à jour le mot de passe que s'il est fourni et non vide.
        if (userRegistrationDto.getMotDePasse() != null && !userRegistrationDto.getMotDePasse().isEmpty()) {
            utilisateur.setMotDePasse(passwordEncoder.encode(userRegistrationDto.getMotDePasse()));
        }

        Utilisateur updatedUser = utilisateurRepository.save(utilisateur);
        return convertToUserResponseDto(updatedUser);
    }

    // --- TOUTES LES AUTRES MÉTHODES RESTENT INCHANGÉES ---

    public UserResponseDto createUser(UserRegistrationDto userRegistrationDto) {
        if (utilisateurRepository.existsByEmail(userRegistrationDto.getEmail())) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(userRegistrationDto.getNom());
        utilisateur.setPrenom(userRegistrationDto.getPrenom());
        utilisateur.setEmail(userRegistrationDto.getEmail());
        utilisateur.setMotDePasse(passwordEncoder.encode(userRegistrationDto.getMotDePasse()));
        // Pour la création, le rôle doit être fourni, sinon une erreur se produira ici si le DTO a un rôle null
        utilisateur.setRole(userRegistrationDto.getRole());
        utilisateur.setActif(userRegistrationDto.getActif() != null ? userRegistrationDto.getActif() : true);
        Utilisateur savedUser = utilisateurRepository.save(utilisateur);
        return convertToUserResponseDto(savedUser);
    }

    public List<UserResponseDto> getAllUsers() {
        return utilisateurRepository.findAllByOrderByDateCreationDesc().stream()
                .map(this::convertToUserResponseDto)
                .collect(Collectors.toList());
    }

    public Optional<UserResponseDto> getUserById(Long id) {
        return utilisateurRepository.findById(id)
                .map(this::convertToUserResponseDto);
    }

    public List<UserResponseDto> getUsersByRole(Role role) {
        return utilisateurRepository.findByRoleOrderByNomAscPrenomAsc(role).stream()
                .map(this::convertToUserResponseDto)
                .collect(Collectors.toList());
    }

    public void deactivateUser(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        utilisateur.setActif(false);
        utilisateurRepository.save(utilisateur);
    }

    public void activateUser(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        utilisateur.setActif(true);
        utilisateurRepository.save(utilisateur);
    }

    public void deleteUser(Long id) {
        if (!utilisateurRepository.existsById(id)) {
            throw new RuntimeException("Utilisateur non trouvé");
        }
        utilisateurRepository.deleteById(id);
    }

    public List<UserResponseDto> searchUsers(String searchTerm) {
        return utilisateurRepository.findByNomOrPrenomContainingIgnoreCase(searchTerm).stream()
                .map(this::convertToUserResponseDto)
                .collect(Collectors.toList());
    }

    public long countUsersByRole(Role role) {
        return utilisateurRepository.countByRole(role);
    }

    public boolean emailExists(String email) {
        return utilisateurRepository.existsByEmail(email);
    }

    public List<UserResponseDto> getAllEnseignantsActifs() {
        return utilisateurRepository.findAllEnseignantsActifs().stream()
                          .map(this::convertToUserResponseDto)
                          .collect(Collectors.toList());
    }

    private UserResponseDto convertToUserResponseDto(Utilisateur utilisateur) {
        return modelMapper.map(utilisateur, UserResponseDto.class);
    }
}
