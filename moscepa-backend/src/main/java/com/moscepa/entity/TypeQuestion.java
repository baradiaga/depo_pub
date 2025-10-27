package com.moscepa.entity;

/**
 * Énumération représentant les différents types de questions possibles dans un test.
 * - QCU: Question à Choix Unique
 * - QCM: Question à Choix Multiples
 * - VRAI_FAUX: Question binaire Vrai ou Faux
 * - TEXTE_LIBRE: Question ouverte où l'utilisateur saisit du texte
 */
public enum TypeQuestion {
    QCU,
    QCM,
    VRAI_FAUX,
    TEXTE_LIBRE
}
