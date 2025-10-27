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

/**
 * Service pour la gestion des utilisateurs par les administrateurs
 */
@Service
@Transactional
public class UserManagementService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Crée un nouvel utilisateur (réservé aux administrateurs)
     */
    public UserResponseDto createUser(UserRegistrationDto userRegistrationDto) {
        // Vérifier si l'email existe déjà
        if (utilisateurRepository.existsByEmail(userRegistrationDto.getEmail())) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

        // Créer le nouvel utilisateur
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(userRegistrationDto.getNom());
        utilisateur.setPrenom(userRegistrationDto.getPrenom());
        utilisateur.setEmail(userRegistrationDto.getEmail());
        utilisateur.setMotDePasse(passwordEncoder.encode(userRegistrationDto.getMotDePasse()));
        utilisateur.setRole(userRegistrationDto.getRole());
        utilisateur.setActif(userRegistrationDto.getActif() != null ? userRegistrationDto.getActif() : true);

        // Sauvegarder l'utilisateur
        Utilisateur savedUser = utilisateurRepository.save(utilisateur);

        // Convertir en DTO de réponse
        return convertToUserResponseDto(savedUser);
    }

    /**
     * Récupère tous les utilisateurs
     */
    public List<UserResponseDto> getAllUsers() {
        List<Utilisateur> utilisateurs = utilisateurRepository.findAllByOrderByDateCreationDesc();
        return utilisateurs.stream()
                .map(this::convertToUserResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Récupère un utilisateur par son ID
     */
    public Optional<UserResponseDto> getUserById(Long id) {
        return utilisateurRepository.findById(id)
                .map(this::convertToUserResponseDto);
    }

    /**
     * Récupère tous les utilisateurs par rôle
     */
    public List<UserResponseDto> getUsersByRole(Role role) {
        List<Utilisateur> utilisateurs = utilisateurRepository.findByRoleOrderByNomAscPrenomAsc(role);
        return utilisateurs.stream()
                .map(this::convertToUserResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Met à jour un utilisateur existant
     */
    public UserResponseDto updateUser(Long id, UserRegistrationDto userRegistrationDto) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + id));

        // Vérifier si l'email existe déjà pour un autre utilisateur
        if (!utilisateur.getEmail().equals(userRegistrationDto.getEmail()) && 
            utilisateurRepository.existsByEmail(userRegistrationDto.getEmail())) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

        // Mettre à jour les informations
        utilisateur.setNom(userRegistrationDto.getNom());
        utilisateur.setPrenom(userRegistrationDto.getPrenom());
        utilisateur.setEmail(userRegistrationDto.getEmail());
        utilisateur.setRole(userRegistrationDto.getRole());
        utilisateur.setActif(userRegistrationDto.getActif() != null ? userRegistrationDto.getActif() : true);

        // Mettre à jour le mot de passe seulement s'il est fourni
        if (userRegistrationDto.getMotDePasse() != null && !userRegistrationDto.getMotDePasse().isEmpty()) {
            utilisateur.setMotDePasse(passwordEncoder.encode(userRegistrationDto.getMotDePasse()));
        }

        Utilisateur updatedUser = utilisateurRepository.save(utilisateur);
        return convertToUserResponseDto(updatedUser);
    }

    /**
     * Désactive un utilisateur
     */
    public void deactivateUser(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + id));
        
        utilisateur.setActif(false);
        utilisateurRepository.save(utilisateur);
    }

    /**
     * Active un utilisateur
     */
    public void activateUser(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + id));
        
        utilisateur.setActif(true);
        utilisateurRepository.save(utilisateur);
    }

    /**
     * Supprime un utilisateur (attention: suppression définitive)
     */
    public void deleteUser(Long id) {
        if (!utilisateurRepository.existsById(id)) {
            throw new RuntimeException("Utilisateur non trouvé avec l'ID: " + id);
        }
        utilisateurRepository.deleteById(id);
    }

    /**
     * Recherche des utilisateurs par nom ou prénom
     */
    public List<UserResponseDto> searchUsers(String searchTerm) {
        List<Utilisateur> utilisateurs = utilisateurRepository.findByNomOrPrenomContainingIgnoreCase(searchTerm);
        return utilisateurs.stream()
                .map(this::convertToUserResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Compte le nombre d'utilisateurs par rôle
     */
    public long countUsersByRole(Role role) {
        return utilisateurRepository.countByRole(role);
    }

    /**
     * Vérifie si un email existe
     */
    public boolean emailExists(String email) {
        return utilisateurRepository.existsByEmail(email);
    }

    /**
     * Convertit une entité Utilisateur en UserResponseDto
     */
    private UserResponseDto convertToUserResponseDto(Utilisateur utilisateur) {
        return modelMapper.map(utilisateur, UserResponseDto.class);
    }
}

