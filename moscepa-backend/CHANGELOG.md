# Changelog - Système de Registre d'Utilisateurs MOSCEPA

## Version 1.1.0 - Ajout du système de gestion des utilisateurs par les administrateurs

### 🆕 Nouvelles fonctionnalités

#### Gestion des utilisateurs par les administrateurs
- **Création d'utilisateurs** : Les administrateurs peuvent maintenant créer de nouveaux comptes utilisateurs
- **Gestion complète** : CRUD complet pour les utilisateurs (Create, Read, Update, Delete)
- **Contrôle des rôles** : Attribution et modification des rôles utilisateur
- **Activation/Désactivation** : Possibilité d'activer ou désactiver des comptes
- **Recherche avancée** : Recherche d'utilisateurs par nom, prénom ou rôle
- **Statistiques** : Vue d'ensemble du nombre d'utilisateurs par rôle

#### Nouveaux endpoints d'administration
- `POST /api/admin/users` - Créer un utilisateur
- `GET /api/admin/users` - Lister tous les utilisateurs
- `GET /api/admin/users/{id}` - Récupérer un utilisateur par ID
- `PUT /api/admin/users/{id}` - Mettre à jour un utilisateur
- `PATCH /api/admin/users/{id}/activate` - Activer un utilisateur
- `PATCH /api/admin/users/{id}/deactivate` - Désactiver un utilisateur
- `DELETE /api/admin/users/{id}` - Supprimer un utilisateur
- `GET /api/admin/users/role/{role}` - Lister par rôle
- `GET /api/admin/users/search` - Rechercher des utilisateurs
- `GET /api/admin/users/stats` - Statistiques des utilisateurs
- `GET /api/admin/users/check-email` - Vérifier l'existence d'un email

### 🔧 Améliorations techniques

#### Nouveaux DTOs
- **UserRegistrationDto** : DTO pour l'enregistrement d'utilisateurs avec validation
- **UserResponseDto** : DTO pour les réponses sans exposer le mot de passe

#### Services étendus
- **UserManagementService** : Service dédié à la gestion des utilisateurs par les admins
- Méthodes de validation et de sécurité renforcées

#### Repository amélioré
- **UtilisateurRepository** : Nouvelles méthodes de recherche et de tri
  - `findAllByOrderByDateCreationDesc()` - Tri par date de création
  - `findByRoleOrderByNomAscPrenomAsc()` - Tri par rôle et nom
  - `countByRole()` - Comptage par rôle
  - `findAllAdmins()` - Recherche des administrateurs

#### Sécurité renforcée
- **Autorisation par rôle** : Endpoints protégés par `@PreAuthorize("hasRole('ADMIN')")`
- **Configuration de sécurité** : Mise à jour de SecurityConfig pour les nouveaux endpoints
- **UserPrincipal amélioré** : Gestion correcte des permissions et rôles

### 📋 Validation et contrôles

#### Validation des données
- Validation complète des champs obligatoires
- Contrôle de format pour les emails
- Vérification de l'unicité des emails
- Validation de la longueur des mots de passe (minimum 6 caractères)

#### Gestion des erreurs
- Messages d'erreur explicites et localisés
- Codes d'erreur standardisés
- Gestion des cas d'edge (email existant, utilisateur non trouvé, etc.)

### 🧪 Tests et documentation

#### Tests HTTP
- **test-admin-endpoints.http** : Suite complète de tests pour tous les endpoints
- Tests d'authentification et d'autorisation
- Tests de validation des données
- Tests des cas d'erreur

#### Documentation
- **ADMIN_ENDPOINTS_DOCUMENTATION.md** : Documentation complète des nouveaux endpoints
- Exemples d'utilisation avec curl
- Description des codes d'erreur
- Guide de sécurité

### 🔒 Sécurité

#### Contrôles d'accès
- Seuls les utilisateurs avec le rôle `ADMIN` peuvent accéder aux endpoints d'administration
- Authentification JWT obligatoire
- Validation côté serveur pour toutes les entrées

#### Protection des données
- Hachage automatique des mots de passe avec BCrypt
- Les mots de passe ne sont jamais retournés dans les réponses API
- Validation et échappement des données d'entrée

### 📦 Fichiers modifiés/ajoutés

#### Nouveaux fichiers
- `src/main/java/com/moscepa/dto/UserRegistrationDto.java`
- `src/main/java/com/moscepa/dto/UserResponseDto.java`
- `src/main/java/com/moscepa/service/UserManagementService.java`
- `src/main/java/com/moscepa/controller/AdminController.java`
- `test-admin-endpoints.http`
- `ADMIN_ENDPOINTS_DOCUMENTATION.md`
- `CHANGELOG.md`

#### Fichiers modifiés
- `src/main/java/com/moscepa/repository/UtilisateurRepository.java` - Ajout de nouvelles méthodes
- `src/main/java/com/moscepa/config/SecurityConfig.java` - Configuration des nouveaux endpoints
- `src/main/java/com/moscepa/security/UserPrincipal.java` - Correction de la gestion des permissions

### 🚀 Déploiement

#### Prérequis
- Java 17+
- Maven 3.6+
- MySQL 8.0+

#### Instructions
1. Compiler le projet : `mvn clean compile`
2. Lancer l'application : `mvn spring-boot:run`
3. Accéder à la documentation Swagger : `http://localhost:8080/swagger-ui.html`

#### Compte administrateur par défaut
- Email : `admin@moscepa.com`
- Mot de passe : `password123`
- **⚠️ Important** : Changer le mot de passe en production

### 🔄 Migration

#### Base de données
Aucune migration de base de données n'est nécessaire. Les nouvelles fonctionnalités utilisent les tables existantes :
- `moscepa_utilisateurs`
- `moscepa_permissions`
- `moscepa_utilisateur_permissions`

#### Compatibilité
Cette version est entièrement rétrocompatible avec la version précédente. Tous les endpoints existants continuent de fonctionner normalement.

### 📝 Notes pour les développeurs

#### Bonnes pratiques implémentées
- Séparation des responsabilités (Controller, Service, Repository)
- DTOs pour l'encapsulation des données
- Validation côté serveur
- Gestion centralisée des erreurs
- Documentation complète des APIs

#### Extensibilité
Le système est conçu pour être facilement extensible :
- Ajout de nouveaux rôles dans l'enum `Role`
- Ajout de nouvelles permissions dans la table `moscepa_permissions`
- Extension des DTOs pour de nouveaux champs
- Ajout de nouveaux endpoints d'administration

---

**Développé par :** Équipe MOSCEPA  
**Date :** Août 2024  
**Version Spring Boot :** 3.1.5  
**Version Java :** 17

