# Documentation des Endpoints d'Administration - MOSCEPA

## Vue d'ensemble

Ce document décrit les nouveaux endpoints d'administration ajoutés au système MOSCEPA pour permettre aux administrateurs de gérer les utilisateurs. Ces endpoints sont sécurisés et accessibles uniquement aux utilisateurs ayant le rôle `ADMIN`.

## Authentification

Tous les endpoints d'administration nécessitent une authentification JWT. L'utilisateur doit :
1. Se connecter via `/api/auth/login` avec des identifiants d'administrateur
2. Utiliser le token JWT retourné dans l'en-tête `Authorization: Bearer <token>`

## Endpoints Disponibles

### 1. Créer un utilisateur
**POST** `/api/admin/users`

Crée un nouvel utilisateur dans le système.

**Corps de la requête :**
```json
{
  "nom": "Dupont",
  "prenom": "Jean",
  "email": "jean.dupont@example.com",
  "motDePasse": "motdepasse123",
  "role": "ETUDIANT",
  "actif": true
}
```

**Rôles disponibles :**
- `ADMIN` - Administrateur
- `ETUDIANT` - Étudiant
- `ENSEIGNANT` - Enseignant
- `TUTEUR` - Tuteur
- `TECHNOPEDAGOGUE` - Technopédagogue
- `RESPONSABLE_FORMATION` - Responsable de Formation

**Réponse de succès (201) :**
```json
{
  "message": "Utilisateur créé avec succès",
  "user": {
    "id": 1,
    "nom": "Dupont",
    "prenom": "Jean",
    "email": "jean.dupont@example.com",
    "role": "ETUDIANT",
    "actif": true,
    "dateCreation": "2024-01-15T10:30:00",
    "dateModification": "2024-01-15T10:30:00"
  }
}
```

### 2. Lister tous les utilisateurs
**GET** `/api/admin/users`

Récupère la liste de tous les utilisateurs, triés par date de création (plus récents en premier).

**Réponse de succès (200) :**
```json
[
  {
    "id": 1,
    "nom": "Admin",
    "prenom": "Système",
    "email": "admin@moscepa.com",
    "role": "ADMIN",
    "actif": true,
    "dateCreation": "2024-01-01T00:00:00",
    "dateModification": "2024-01-01T00:00:00"
  },
  {
    "id": 2,
    "nom": "Dupont",
    "prenom": "Jean",
    "email": "jean.dupont@example.com",
    "role": "ETUDIANT",
    "actif": true,
    "dateCreation": "2024-01-15T10:30:00",
    "dateModification": "2024-01-15T10:30:00"
  }
]
```

### 3. Récupérer un utilisateur par ID
**GET** `/api/admin/users/{id}`

Récupère les détails d'un utilisateur spécifique.

**Paramètres :**
- `id` (path) - ID de l'utilisateur

**Réponse de succès (200) :**
```json
{
  "id": 1,
  "nom": "Dupont",
  "prenom": "Jean",
  "email": "jean.dupont@example.com",
  "role": "ETUDIANT",
  "actif": true,
  "dateCreation": "2024-01-15T10:30:00",
  "dateModification": "2024-01-15T10:30:00"
}
```

### 4. Mettre à jour un utilisateur
**PUT** `/api/admin/users/{id}`

Met à jour les informations d'un utilisateur existant.

**Paramètres :**
- `id` (path) - ID de l'utilisateur

**Corps de la requête :**
```json
{
  "nom": "Dupont",
  "prenom": "Jean-Michel",
  "email": "jean.dupont@example.com",
  "motDePasse": "nouveaumotdepasse123",
  "role": "ENSEIGNANT",
  "actif": true
}
```

**Note :** Le mot de passe est optionnel. S'il n'est pas fourni, l'ancien mot de passe est conservé.

### 5. Désactiver un utilisateur
**PATCH** `/api/admin/users/{id}/deactivate`

Désactive un utilisateur (le compte reste dans la base mais l'utilisateur ne peut plus se connecter).

**Réponse de succès (200) :**
```json
{
  "message": "Utilisateur désactivé avec succès"
}
```

### 6. Activer un utilisateur
**PATCH** `/api/admin/users/{id}/activate`

Réactive un utilisateur précédemment désactivé.

**Réponse de succès (200) :**
```json
{
  "message": "Utilisateur activé avec succès"
}
```

### 7. Supprimer un utilisateur
**DELETE** `/api/admin/users/{id}`

Supprime définitivement un utilisateur de la base de données.

**⚠️ Attention :** Cette action est irréversible.

**Réponse de succès (200) :**
```json
{
  "message": "Utilisateur supprimé avec succès"
}
```

### 8. Lister les utilisateurs par rôle
**GET** `/api/admin/users/role/{role}`

Récupère tous les utilisateurs d'un rôle spécifique, triés par nom.

**Paramètres :**
- `role` (path) - Rôle des utilisateurs à récupérer

**Exemple :** `/api/admin/users/role/ETUDIANT`

### 9. Rechercher des utilisateurs
**GET** `/api/admin/users/search?q={terme}`

Recherche des utilisateurs par nom ou prénom (insensible à la casse).

**Paramètres :**
- `q` (query) - Terme de recherche

**Exemple :** `/api/admin/users/search?q=Dupont`

### 10. Statistiques des utilisateurs
**GET** `/api/admin/users/stats`

Récupère les statistiques du nombre d'utilisateurs par rôle.

**Réponse de succès (200) :**
```json
{
  "admin": 1,
  "etudiant": 15,
  "enseignant": 8,
  "tuteur": 3,
  "technopedagogue": 2,
  "responsable_formation": 1,
  "total": 30
}
```

### 11. Vérifier l'existence d'un email
**GET** `/api/admin/users/check-email?email={email}`

Vérifie si un email est déjà utilisé dans le système.

**Paramètres :**
- `email` (query) - Email à vérifier

**Réponse de succès (200) :**
```json
{
  "exists": true
}
```

## Codes d'erreur

### 400 - Bad Request
Données invalides ou email déjà existant.

```json
{
  "message": "Un utilisateur avec cet email existe déjà",
  "error": "USER_CREATION_FAILED"
}
```

### 401 - Unauthorized
Token JWT manquant ou invalide.

```json
{
  "message": "Utilisateur non authentifié",
  "error": "NOT_AUTHENTICATED"
}
```

### 403 - Forbidden
Utilisateur authentifié mais sans les droits d'administrateur.

```json
{
  "message": "Accès refusé",
  "error": "ACCESS_DENIED"
}
```

### 404 - Not Found
Utilisateur non trouvé.

```json
{
  "message": "Utilisateur non trouvé avec l'ID: 999",
  "error": "USER_NOT_FOUND"
}
```

## Validation des données

### Champs obligatoires pour la création/modification :
- `nom` : 1-100 caractères, non vide
- `prenom` : 1-100 caractères, non vide
- `email` : Format email valide, 1-255 caractères, unique
- `motDePasse` : 6-100 caractères (obligatoire à la création, optionnel à la modification)
- `role` : Doit être un des rôles valides

### Champs optionnels :
- `actif` : Boolean, par défaut `true`

## Sécurité

1. **Authentification JWT** : Tous les endpoints nécessitent un token valide
2. **Autorisation par rôle** : Seuls les utilisateurs avec le rôle `ADMIN` peuvent accéder
3. **Hachage des mots de passe** : Les mots de passe sont automatiquement hachés avec BCrypt
4. **Validation des données** : Toutes les entrées sont validées côté serveur
5. **CORS configuré** : Permet les requêtes cross-origin pour les applications frontend

## Utilisation avec un client HTTP

### Exemple avec curl :

1. **Connexion :**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@moscepa.com","password":"password123"}'
```

2. **Créer un utilisateur :**
```bash
curl -X POST http://localhost:8080/api/admin/users \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"nom":"Test","prenom":"User","email":"test@example.com","motDePasse":"password123","role":"ETUDIANT"}'
```

3. **Lister les utilisateurs :**
```bash
curl -X GET http://localhost:8080/api/admin/users \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Compte administrateur par défaut

Le système est livré avec un compte administrateur par défaut :
- **Email :** `admin@moscepa.com`
- **Mot de passe :** `password123`
- **Rôle :** `ADMIN`

**⚠️ Important :** Changez le mot de passe par défaut en production pour des raisons de sécurité.

