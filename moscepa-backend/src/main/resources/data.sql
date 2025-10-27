-- Données de test pour l'application MOSCEPA

-- Insertion des permissions
INSERT IGNORE INTO moscepa_permissions (nom, description) VALUES
('Paramétrage', 'Accès aux paramètres du système'),
('Créer utilisateur', 'Créer de nouveaux utilisateurs'),
('Modifier utilisateur', 'Modifier les utilisateurs existants'),
('Supprimer utilisateur', 'Supprimer des utilisateurs'),
('Lister utilisateurs', 'Voir la liste des utilisateurs'),
('Matières', 'Accès aux matières'),
('Test de connaissance', 'Passer des tests de connaissance'),
('Résultats', 'Voir les résultats des tests'),
('Parcours recommandé', 'Accès aux parcours recommandés'),
('Ressources pédagogiques', 'Accès aux ressources pédagogiques');

-- Insertion des utilisateurs de test (mot de passe: "password123" encodé en BCrypt)
INSERT IGNORE INTO moscepa_utilisateurs (nom, prenom, email, mot_de_passe, role, actif, date_creation) VALUES
('Admin', 'Système', 'admin@moscepa.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ADMIN', true, NOW()),
('Dupont', 'Jean', 'jean.dupont@moscepa.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ETUDIANT', true, NOW()),
('Martin', 'Marie', 'marie.martin@moscepa.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ENSEIGNANT', true, NOW()),
('Bernard', 'Pierre', 'pierre.bernard@moscepa.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'TUTEUR', true, NOW()),
('Leroy', 'Sophie', 'sophie.leroy@moscepa.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'TECHNOPEDAGOGUE', true, NOW());

-- Insertion des matières
INSERT IGNORE INTO moscepa_matieres (nom, description) VALUES
('Mathématiques', 'Matière fondamentale couvrant algèbre, géométrie et analyse'),
('Physique', 'Étude des phénomènes naturels et des lois qui les régissent'),
('Chimie', 'Science de la matière et de ses transformations'),
('Informatique', 'Science du traitement automatique de l''information'),
('Français', 'Langue française, littérature et expression écrite'),
('Anglais', 'Langue anglaise et communication internationale');

-- Insertion des chapitres
INSERT IGNORE INTO moscepa_chapitres (nom, description, matiere_id) VALUES
('Algèbre linéaire', 'Étude des espaces vectoriels et des applications linéaires', 1),
('Géométrie analytique', 'Géométrie dans le plan et l''espace avec coordonnées', 1),
('Mécanique classique', 'Lois du mouvement et forces', 2),
('Thermodynamique', 'Étude de la chaleur et des transformations énergétiques', 2),
('Chimie organique', 'Étude des composés carbonés', 3),
('Chimie inorganique', 'Étude des éléments et composés non organiques', 3),
('Programmation orientée objet', 'Concepts et pratiques de la POO', 4),
('Bases de données', 'Conception et gestion des bases de données', 4),
('Grammaire française', 'Règles et structures de la langue française', 5),
('Littérature classique', 'Œuvres et auteurs de la littérature française', 5),
('Grammar and Vocabulary', 'English grammar rules and vocabulary building', 6),
('Business English', 'Professional communication in English', 6);

-- Insertion des étudiants
INSERT IGNORE INTO moscepa_etudiants (utilisateur_id, niveau_etudes, date_inscription) VALUES
(2, 'Licence 2', NOW());

-- Insertion des matières à reprendre pour l'étudiant
INSERT IGNORE INTO moscepa_etudiant_matieres_reprendre (etudiant_id, matiere_nom) VALUES
(1, 'Mathématiques'),
(1, 'Physique');

-- Insertion des tests
INSERT IGNORE INTO moscepa_tests (titre, description, chapitre_id) VALUES
('Test d''algèbre linéaire', 'Évaluation des connaissances en algèbre linéaire', 1),
('Test de géométrie analytique', 'Évaluation des connaissances en géométrie analytique', 2),
('Test de mécanique', 'Évaluation des connaissances en mécanique classique', 3),
('Test de thermodynamique', 'Évaluation des connaissances en thermodynamique', 4),
('Test de chimie organique', 'Évaluation des connaissances en chimie organique', 5),
('Test de POO', 'Évaluation des connaissances en programmation orientée objet', 7),
('Test de bases de données', 'Évaluation des connaissances en bases de données', 8),
('Test de grammaire', 'Évaluation des connaissances en grammaire française', 9),
('Test de littérature', 'Évaluation des connaissances en littérature classique', 10),
('English Grammar Test', 'Assessment of English grammar knowledge', 11),
('Business English Test', 'Assessment of business English skills', 12);

-- Insertion des questions pour le test d'algèbre linéaire
INSERT IGNORE INTO moscepa_questions (question_text, correct_answer_index, test_id) VALUES
('Qu''est-ce qu''un espace vectoriel ?', 0, 1),
('Comment calcule-t-on le déterminant d''une matrice 2x2 ?', 1, 1),
('Qu''est-ce qu''une application linéaire ?', 2, 1);

-- Insertion des options pour les questions
INSERT IGNORE INTO moscepa_question_options (question_id, option_text, option_order) VALUES
(1, 'Un ensemble muni d''une addition et d''une multiplication par un scalaire', 0),
(1, 'Un ensemble de vecteurs orthogonaux', 1),
(1, 'Un ensemble de matrices carrées', 2),
(1, 'Un ensemble de nombres réels', 3),
(2, 'ad + bc', 0),
(2, 'ad - bc', 1),
(2, 'ab + cd', 2),
(2, 'ac - bd', 3),
(3, 'Une fonction qui préserve l''addition et la multiplication scalaire', 0),
(3, 'Une fonction continue', 1),
(3, 'Une fonction dérivable', 2),
(3, 'Une fonction bijective', 3);

-- Insertion des questions pour le test de POO
INSERT IGNORE INTO moscepa_questions (question_text, correct_answer_index, test_id) VALUES
('Qu''est-ce que l''encapsulation en POO ?', 0, 6),
('Qu''est-ce que l''héritage ?', 1, 6),
('Qu''est-ce que le polymorphisme ?', 2, 6);

INSERT IGNORE INTO moscepa_question_options (question_id, option_text, option_order) VALUES
(4, 'Le fait de cacher les détails d''implémentation', 0),
(4, 'Le fait de créer plusieurs objets', 1),
(4, 'Le fait d''utiliser des interfaces', 2),
(4, 'Le fait de compiler le code', 3),
(5, 'La création d''objets', 0),
(5, 'La capacité d''une classe à hériter des propriétés d''une autre classe', 1),
(5, 'La destruction d''objets', 2),
(5, 'L''utilisation de pointeurs', 3),
(6, 'L''utilisation de plusieurs langages', 0),
(6, 'L''utilisation de plusieurs processeurs', 1),
(6, 'La capacité d''un objet à prendre plusieurs formes', 2),
(6, 'L''utilisation de plusieurs fichiers', 3);

