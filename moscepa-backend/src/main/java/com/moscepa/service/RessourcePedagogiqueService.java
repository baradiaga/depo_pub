
package com.moscepa.service;
import java.util.Set;
import java.util.HashSet;
import com.moscepa.security.UserPrincipal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moscepa.dto.RessourceTeleversementDto;
import com.moscepa.entity.*;
import com.moscepa.repository.ChapitreRepository;
import com.moscepa.repository.RessourcePedagogiqueRepository;
import com.moscepa.repository.TagRepository;
import com.moscepa.repository.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class RessourcePedagogiqueService {

    // Correction : Utilisation de la propriété du fichier de configuration fournie par l'utilisateur
    // La propriété 'file.upload-dir' est définie dans application.properties.
    // L'utilisation de '${app.storage.location:/data/ressources}' est incorrecte si l'intention est d'utiliser 'file.upload-dir'.
    // De plus, l'injection @Value n'est pas garantie d'être terminée avant l'appel du constructeur.
    // La meilleure pratique est d'injecter la valeur directement dans le constructeur.
    private final String storageLocation;

    private final RessourcePedagogiqueRepository ressourceRepository;
    private final ChapitreRepository chapitreRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final TagRepository tagRepository;
    private final ObjectMapper objectMapper;

    public RessourcePedagogiqueService(
            RessourcePedagogiqueRepository ressourceRepository,
            ChapitreRepository chapitreRepository,
            UtilisateurRepository utilisateurRepository,
            TagRepository tagRepository,
            ObjectMapper objectMapper,
            // Correction : Injection de la valeur dans le constructeur pour garantir qu'elle est disponible
            @Value("${file.upload-dir}") String storageLocation) {
        this.ressourceRepository = ressourceRepository;
        this.chapitreRepository = chapitreRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.tagRepository = tagRepository;
        this.objectMapper = objectMapper;
        this.storageLocation = storageLocation; // Affectation de la valeur injectée
        initialiserDossierStockage();
    }

    private void initialiserDossierStockage() {
        try {
            // Correction : Ajout d'une vérification pour s'assurer que storageLocation n'est pas null
            if (storageLocation == null || storageLocation.trim().isEmpty()) {
                throw new IllegalStateException("La propriété 'file.upload-dir' n'a pas été injectée ou est vide.");
            }
            Files.createDirectories(Paths.get(storageLocation));
        } catch (IOException e) {
            throw new RuntimeException("Impossible d'initialiser le dossier de stockage: " + storageLocation, e);
        }
    }

    // ====================================================================
    // === TÉLÉVERSEMENT ET SAUVEGARDE                                  ===
    // ====================================================================

    @Transactional
    public RessourcePedagogique televerserEtSauvegarder(MultipartFile fichier, String metadataJson) throws IOException {
        // 1. Récupérer les métadonnées
        RessourceTeleversementDto metadata = objectMapper.readValue(metadataJson, RessourceTeleversementDto.class);

        // 2. Stocker le fichier physiquement
        String nomUnique = UUID.randomUUID().toString() + "_" + fichier.getOriginalFilename();
        Path destinationFile = Paths.get(storageLocation).resolve(nomUnique);
        Files.copy(fichier.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

        // 3. Créer l'entité JPA
        RessourcePedagogique ressource = new RessourcePedagogique();

        // Récupérer l'utilisateur courant
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Utilisateur auteur = utilisateurRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new EntityNotFoundException("Auteur non trouvé."));

        // Récupérer le chapitre
        Chapitre chapitre = chapitreRepository.findById(metadata.getChapitreId())
                .orElseThrow(() -> new EntityNotFoundException("Chapitre non trouvé."));

        // Gestion des Tags
        Set<Tag> tags = metadata.getTags().stream()
                .map(nom -> tagRepository.findByNomIgnoreCase(nom).orElseGet(() -> tagRepository.save(new Tag(nom))))
                .collect(Collectors.toSet());

        // Remplir l'entité
        ressource.setTitre(metadata.getTitre());
        ressource.setDescription(metadata.getDescription());
        ressource.setNomFichier(fichier.getOriginalFilename());
        ressource.setCheminStockage(nomUnique); // On stocke le nom unique
        ressource.setTypeMime(fichier.getContentType());
        ressource.setTailleOctets(fichier.getSize());
        ressource.setAuteur(auteur);
        ressource.setChapitre(chapitre);
        ressource.setTags(tags);

        return ressourceRepository.save(ressource);
    }

    // ====================================================================
    // === LECTURE ET TÉLÉCHARGEMENT                                    ===
    // ====================================================================

    public List<RessourcePedagogique> findAll() {
        return ressourceRepository.findAll();
    }

    public Resource chargerFichierCommeRessource(String nomFichierStocke) {
        try {
            Path file = Paths.get(storageLocation).resolve(nomFichierStocke);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Impossible de lire le fichier: " + nomFichierStocke);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Erreur de chemin de fichier: " + nomFichierStocke, e);
        }
    }

    // ====================================================================
    // === SUPPRESSION                                                  ===
    // ====================================================================

    @Transactional
    public void supprimerRessource(Long id) {
        RessourcePedagogique ressource = ressourceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ressource non trouvée avec l'ID: " + id));

        // 1. Supprimer le fichier physique
        try {
            Path fileToDelete = Paths.get(storageLocation).resolve(ressource.getCheminStockage());
            Files.deleteIfExists(fileToDelete);
        } catch (IOException e) {
            // Log l'erreur mais continue pour supprimer l'entrée de la DB
            System.err.println("Erreur lors de la suppression physique du fichier: " + e.getMessage());
        }

        // 2. Supprimer l'entrée de la base de données
        ressourceRepository.delete(ressource);
    }
}