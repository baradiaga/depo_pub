package com.moscepa.config;

import com.moscepa.entity.Fonctionnalite;
import com.moscepa.entity.Role;
import com.moscepa.entity.RolePermission;
import com.moscepa.entity.SousFonctionnalite;
import com.moscepa.entity.Utilisateur;
import com.moscepa.repository.FonctionnaliteRepository;
import com.moscepa.repository.RolePermissionRepository;
import com.moscepa.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@DependsOn("entityManagerFactory") // Important pour l'ordre d'exécution
public class DataInitializer implements CommandLineRunner {

    // --- Repositories ---
    @Autowired private UtilisateurRepository utilisateurRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private FonctionnaliteRepository fonctionnaliteRepository;
    @Autowired private RolePermissionRepository permissionRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        
        // --- 1. Initialisation de l'utilisateur Admin ---
        if (!utilisateurRepository.existsByEmail("admin@moscepa.com")) {
            Utilisateur admin = new Utilisateur();
            admin.setNom("Admin");
            admin.setPrenom("Super");
            admin.setEmail("admin@moscepa.com");
            admin.setMotDePasse(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setActif(true);
            utilisateurRepository.save(admin);
            System.out.println("✅ Admin par défaut créé : admin@moscepa.com / admin123");
        } else {
            System.out.println("ℹ️ Admin par défaut déjà présent dans la base.");
        }

        // --- 2. Initialisation des fonctionnalités par défaut ---
        if (fonctionnaliteRepository.count() == 0) {
            System.out.println("⏳ Initialisation des fonctionnalités par défaut...");
            
            List<Fonctionnalite> defaultFeatures = Arrays.asList(
                createFonctionnalite("Paramétrage", "parametrage", "settings",
                    createSousFonctionnalite("Profil utilisateur", "profil_utilisateur", "/app/profil"),
                    createSousFonctionnalite("Modifier mot de passe", "modifier_mot_passe", "/app/profil/change-password"),
                    createSousFonctionnalite("Modifier profil", "modifier_profil", "/app/profil/edit")),
                createFonctionnalite("Gestion des utilisateurs", "gestion_utilisateurs", "users",
                    createSousFonctionnalite("Gestion Des rôles", "gestion_roles", "/app/admin/roles"),
                    createSousFonctionnalite("Permissions", "gestion_permissions", "/app/admin/permissions"),
                    createSousFonctionnalite("Gestion utilisateur", "gestion_utilisateur", "/app/admin/users")),
                createFonctionnalite("Gestion des fonctionnalités", "gestion_fonctionnalites", "settings",
                    createSousFonctionnalite("Ajouter fonctionnalité", "ajouter_fonctionnalite", "/app/admin/features"),
                    createSousFonctionnalite("Attribuer une fonctionnalité", "attribuer_fonctionnalite", "/app/admin/feature-assignment"),
                    createSousFonctionnalite("Liste des fonctionnalités", "liste_fonctionnalites", "/app/admin/permssionsManegement"),
                    createSousFonctionnalite("Gestion des Permissions", "gestion_permissions_admin", "/app/admin/permissions")),
                createFonctionnalite("Gestion des formations", "gestion_formations", "book",
                    createSousFonctionnalite("Créer formation", "creer_formation", "/app/enseignant/formations"),
                    createSousFonctionnalite("Liste des formations", "liste_formations", "/app/admin/formations"),
                    createSousFonctionnalite("Gérer inscriptions", "gerer_inscriptions", "/app/admin/inscriptions")),
                createFonctionnalite("Gestion des maquettes", "gestion_maquettes", "layout",
                    createSousFonctionnalite("Unité d'enseignement", "gestion_unites", "/app/admin/unites-enseignement"),
                    createSousFonctionnalite("Éléments constitutifs", "gestion_elements", "/app/admin/elements-constitutifs"),
                    createSousFonctionnalite("Paramétrage des chapitres", "parametrage_chapitres", "/app/admin/parametrage-chapitre"),
                    createSousFonctionnalite("Structure des matières", "structure_matieres", "/app/admin/matieres"),
                    createSousFonctionnalite("Syllabus", "syllabus", "/app/admin/chapitres")),
                createFonctionnalite("Gestion des inscriptions aux classes", "gestion_inscriptions_classes", "user-plus",
                    createSousFonctionnalite("Inscriptions aux classes", "inscriptions_classes", "/app/admin/inscriptions"),
                    createSousFonctionnalite("Validation inscriptions", "validation_inscriptions", "/app/admin/validation-inscriptions")),
                createFonctionnalite("Gestion des équivalences", "gestion_equivalences", "equal",
                    createSousFonctionnalite("Créer équivalence", "creer_equivalence", "/app/admin/equivalences/create"),
                    createSousFonctionnalite("Liste équivalences", "liste_equivalences", "/app/admin/equivalences")),
                createFonctionnalite("Gestion des parcours (Admin)", "gestion_parcours_admin", "route",
                    createSousFonctionnalite("Parcours recommandés", "parcours_recommandes_admin", "/app/admin/parcours"),
                    createSousFonctionnalite("Parcours choisis", "parcours_choisis_admin", "/app/admin/parcours"),
                    createSousFonctionnalite("Parcours mixtes", "parcours_mixtes_admin", "/app/admin/parcours")),
                createFonctionnalite("Gestion des ressources pédagogiques", "gestion_ressources_pedagogiques", "folder",
                createSousFonctionnalite("Gérer mes ressources", "gerer_mes_ressources", "/app/enseignant/gestion-contenu"),
                    createSousFonctionnalite("Mes ressources", "mes_ressources", "/app/enseignant/ressources"),
                    createSousFonctionnalite("Bibliothèque", "bibliotheque", "/app/enseignant/banques")),
                createFonctionnalite("Gestion des échelles de connaissances", "gestion_echelles_connaissances", "bar-chart",
                    createSousFonctionnalite("Échelles de connaissances", "echelles_connaissances", "/app/admin/echelles"),
                    createSousFonctionnalite("Évaluation compétences", "evaluation_competences", "/app/admin/evaluation-competences")),
                createFonctionnalite("Gestion des catégories", "gestion_categories", "tag",
                    createSousFonctionnalite("Catégories", "categories", "/app/admin/categories"),
                    createSousFonctionnalite("Sous-catégories", "sous_categories", "/app/admin/sous-categories")),
                createFonctionnalite("Gestion des questionnaires", "gestion_questionnaires", "help-circle",
                    createSousFonctionnalite("Créer questionnaires", "liste_questionnaire", "/app/enseignant/gestion-questionnaire"),
                    createSousFonctionnalite("Créer questionnaire", "creer_questionnaire", "/app/enseignant/gestion-questionnaire/create"),
                    createSousFonctionnalite("Banque de questions", "banque_questions", "/app/enseignant/banque-questions")),
               createFonctionnalite("Ressources externes", "gestion_ressources_externes", "external-link",
    createSousFonctionnalite("Ressources externes", "external_resources_list", "/app/admin/external-resources"),
    createSousFonctionnalite("Banque de ressources", "external_resources_bank", "/app/admin/external-resources/bank")),
                createFonctionnalite("Gestion des remédiation", "gestion_remediation", "refresh-cw",
                    createSousFonctionnalite("Matières", "matieres", "/app/curriculum/matieres"),
                    createSousFonctionnalite("Test de connaissance", "test_connaissance", "/app/student/test-connaissance"),
                    createSousFonctionnalite("Résultats", "resultats", "/app/student/mes-resultats")),
                createFonctionnalite("Apprentissage asynchrone", "apprentissage_asynchrone", "play-circle",
                    createSousFonctionnalite("Séquences", "sequences", "/app/curriculum/sequences"),
                    createSousFonctionnalite("Activités", "activites", "/app/curriculum/activites"),
                    createSousFonctionnalite("Évaluations", "evaluations", "/app/curriculum/evaluations")),
                createFonctionnalite("Tutorat", "tutorat", "user-check",
                    createSousFonctionnalite("Mes étudiants", "mes_etudiants", "/app/tuteur/mes-etudiants"),
                    createSousFonctionnalite("Suivi progression", "suivi_progression", "/app/tuteur/suivi"),
                    createSousFonctionnalite("Planifier séances", "planifier_seances", "/app/tuteur/planifier-seances")),
                createFonctionnalite("Enseignement", "enseignement", "graduation-cap",
                    createSousFonctionnalite("Mes cours", "mes_cours", "/app/enseignant/mes-cours"),
                    createSousFonctionnalite("Créer contenu", "creer_contenu", "app/enseignant/gestion-questionnaire"),
                    createSousFonctionnalite("Évaluer étudiants", "evaluer_etudiants", "/app/enseignant/evaluer-etudiants")),
                createFonctionnalite("Mes parcours (Étudiant)", "mes_parcours_etudiant", "route",
                    createSousFonctionnalite("Parcours recommandés", "parcours_recommandes_etudiant", "/app/student/parcours"),
                    createSousFonctionnalite("Parcours choisis", "parcours_choisis_etudiant", "/app/student/parcours"),
                    createSousFonctionnalite("Parcours mixtes", "parcours_mixtes_etudiant", "/app/student/parcours")),
                createFonctionnalite("Innovation pédagogique", "innovation_pedagogique", "lightbulb",
                    createSousFonctionnalite("Outils numériques", "outils_numeriques", "/app/technopedagogue/outils-numeriques"),
                    createSousFonctionnalite("Méthodes innovantes", "methodes_innovantes", "/app/technopedagogue/methodes-innovantes"),
                    createSousFonctionnalite("Formation enseignants", "formation_enseignants", "/app/technopedagogue/formation-enseignants"))
            );
            fonctionnaliteRepository.saveAll(defaultFeatures);
            System.out.println("✅ " + defaultFeatures.size() + " fonctionnalités par défaut ont été créées.");
        } else {
            System.out.println("ℹ️ Les fonctionnalités par défaut sont déjà présentes.");
        }

        // --- 3. Initialisation des permissions par défaut ---
        if (permissionRepository.count() == 0) {
            System.out.println("⏳ Initialisation des permissions par défaut...");
            
            Set<String> allKeys = fonctionnaliteRepository.findAll().stream()
                .flatMap(f -> {
                    Set<String> keys = f.getSousFonctionnalites().stream()
                        .map(SousFonctionnalite::getFeatureKey)
                        .collect(Collectors.toSet());
                    keys.add(f.getFeatureKey());
                    return keys.stream();
                }).collect(Collectors.toSet());
            permissionRepository.save(createRolePermission("ADMIN", allKeys));

            permissionRepository.save(createRolePermission("ETUDIANT", Set.of("parametrage", "mes_parcours_etudiant", "gestion_remediation", "apprentissage_asynchrone")));
            permissionRepository.save(createRolePermission("ENSEIGNANT", Set.of("parametrage", "enseignement", "gestion_ressources_pedagogiques", "gestion_echelles_connaissances", "gestion_questionnaires", "gestion_remediation", "apprentissage_asynchrone")));
            permissionRepository.save(createRolePermission("TUTEUR", Set.of("parametrage", "tutorat", "gestion_remediation")));
            permissionRepository.save(createRolePermission("TECHNOPEDAGOGUE", Set.of("parametrage", "innovation_pedagogique", "gestion_ressources_pedagogiques", "gestion_questionnaires", "apprentissage_asynchrone")));
            permissionRepository.save(createRolePermission("RESPONSABLE_FORMATION", Set.of("parametrage", "gestion_formations", "gestion_maquettes", "gestion_inscriptions_classes", "gestion_equivalences", "gestion_parcours_admin")));
            
            System.out.println("✅ Permissions par défaut créées pour les rôles de base.");
        } else {
            System.out.println("ℹ️ Les permissions par défaut sont déjà présentes.");
        }
    }

    private Fonctionnalite createFonctionnalite(String nom, String key, String icon, SousFonctionnalite... sous) {
        Fonctionnalite f = new Fonctionnalite();
        f.setNom(nom);
        f.setFeatureKey(key);
        f.setIcon(icon);
        f.setSousFonctionnalites(Arrays.asList(sous));
        return f;
    }

    private SousFonctionnalite createSousFonctionnalite(String label, String key, String route) {
        SousFonctionnalite sf = new SousFonctionnalite();
        sf.setLabel(label);
        sf.setFeatureKey(key);
        sf.setRoute(route);
        return sf;
    }
    
    private RolePermission createRolePermission(String roleName, Set<String> keys) {
        RolePermission rp = new RolePermission();
        rp.setRoleName(roleName);
        rp.setAllowedFeatures(keys);
        return rp;
    }
}
