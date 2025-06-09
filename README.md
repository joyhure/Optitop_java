# 🏥 Optitop

**Système de gestion des statistiques pour magasin d'optique**

> Application multi-plateforme permettant la gestion et l'analyse des données de vente dans le secteur de l'optique.

## 🛠️ Stack Technique

- **Backend** : Spring Boot + Java (API REST)
- **Frontend Web** : Javascript + Bootstrap + PHP  
- **Mobile** : Flutter + Dart
- **Base de données** : MySQL
- **Outils** : XAMPP, npm, Maven

## 🏗️ Architecture

```
┌─────────────┐    ┌─────────────┐
│   Mobile    │    │  Frontend   │
│   Flutter   │    │  Javascript │
└──────┬──────┘    └──────┬──────┘
       │                  │
       │                  │ 
       │                  │
       └─────────┬────────┘
                 ▼
       ┌─────────────────┐
       │     Backend     │
       │   Spring Boot   │
       │   (API REST)    │
       └─────────────────┘
                 │
                 ▼
       ┌─────────────────┐
       │     MySQL       │
       │    Database     │
       └─────────────────┘
```
## 👥 Rôles et Permissions

| Rôle | Permissions |
|------|-------------|
| **Collaborateur** | Consultation statistiques personnelles et globales du magasin, renseigner statuts et commentaires de ses devis |
| **Manager** | Permissions des collaborateurs étendues aux statistiques de chacun et détaillées du magasin, demande sur les comptes |
| **Super Manager** | Permissions du manager, accès à l'application mobile |
| **Admin** | Permissions du supermanager, validation ou refus des demandes sur les comptes |

## ⭐ Fonctionnalités Principales

### 📊 **Analyse des Données**
- Statistiques de vente par période
- Calcul des paniers moyens
- Analyse des performances par vendeur
- Tableaux de bord interactifs

### 👥 **Gestion des Utilisateurs**  
- Système de rôles hiérarchiques
- Authentification sécurisée
- Gestion des permissions d'accès
- Modification des comptes réservés à certains rôles seulement

### 📱 **Multi-plateforme**
- Interface web
- Application mobile
- Synchronisation des données sur une BDD commune

## Démarrage rapide
```bash
# Backend API
cd backend/optitop-api
./mvnw spring-boot:run

# Frontend Web
# Démarrer XAMPP puis accéder à localhost/Optitop/frontend/

# Application Mobile
cd mobile/optitop_mobile
flutter pub get && flutter run
```

## 📋 Structure du Projet

```
Optitop/
├── backend/          # API Spring Boot
├── frontend/         # Interface web Javascript / PHP
├── mobile/           # Application Flutter
└── database/         # MySQL
```

## 📄 Documentation

La documentation complète est disponible dans le fichier `Optitop.pdf`.

## 🔧 Développement

**Technologies utilisées :**
- Spring Boot pour l'API REST robuste
- Flutter pour une expérience mobile native
- Bootstrap pour un design moderne et responsive
- MySQL pour la persistance des données

---
