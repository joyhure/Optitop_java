<?php

/**
 * Script de déconnexion utilisateur
 * 
 * Gère la déconnexion sécurisée des utilisateurs en :
 * - Démarrant la session existante
 * - Détruisant toutes les données de session
 * - Retournant une réponse JSON de confirmation
 * 
 * Utilisé par l'interface JavaScript pour les déconnexions AJAX.
 * 
 * @author Joy Huré
 * @version 1.0
 */

// ===== GESTION DE SESSION =====

/**
 * Démarrage et destruction de la session utilisateur
 */
session_start();
session_destroy();

// ===== RÉPONSE JSON =====

/**
 * Configuration de la réponse et envoi du statut de déconnexion
 */
header('Content-Type: application/json');
echo json_encode(['success' => true]);