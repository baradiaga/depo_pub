#!/bin/bash

# Script de démarrage pour MOSCEPA Backend
echo "=== MOSCEPA Backend - Script de démarrage ==="

# Vérifier Java 17
echo "Vérification de Java 17..."
if ! java -version 2>&1 | grep -q "17"; then
    echo "❌ Java 17 requis. Veuillez installer Java 17."
    exit 1
fi
echo "✅ Java 17 détecté"

# Vérifier Maven
echo "Vérification de Maven..."
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven requis. Veuillez installer Maven."
    exit 1
fi
echo "✅ Maven détecté"

# Vérifier MySQL
echo "Vérification de MySQL..."
if ! systemctl is-active --quiet mysql; then
    echo "⚠️  MySQL n'est pas démarré. Tentative de démarrage..."
    sudo systemctl start mysql
    if [ $? -eq 0 ]; then
        echo "✅ MySQL démarré"
    else
        echo "❌ Impossible de démarrer MySQL"
        exit 1
    fi
else
    echo "✅ MySQL est actif"
fi

# Vérifier la base de données
echo "Vérification de la base de données moscepa..."
if ! mysql -u root -e "USE moscepa;" 2>/dev/null; then
    echo "⚠️  Base de données moscepa non trouvée. Création..."
    mysql -u root -e "CREATE DATABASE IF NOT EXISTS moscepa CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
    echo "✅ Base de données moscepa créée"
else
    echo "✅ Base de données moscepa existe"
fi

# Compiler le projet
echo "Compilation du projet..."
mvn clean compile -q
if [ $? -eq 0 ]; then
    echo "✅ Compilation réussie"
else
    echo "❌ Erreur de compilation"
    exit 1
fi

# Démarrer l'application
echo "Démarrage de l'application MOSCEPA Backend..."
echo "🚀 L'application sera disponible sur http://localhost:8080"
echo "📚 Documentation API : http://localhost:8080/swagger-ui.html"
echo "⏹️  Pour arrêter : Ctrl+C"
echo ""

mvn spring-boot:run -Dmaven.test.skip=true

