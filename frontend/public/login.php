<?php
/**
 * Script de gestion de session utilisateur pour Optitop
 * 
 * Reçoit les données utilisateur depuis l'API Java et les stocke en session PHP
 * pour maintenir l'état de connexion côté frontend
 */

// ===== INITIALISATION =====

session_start();

// ===== TRAITEMENT DES DONNÉES =====

/**
 * Récupération des données JSON envoyées par le frontend
 */
$jsonData = file_get_contents('php://input');
$userData = json_decode($jsonData, true);

/**
 * Validation basique des données reçues
 */
if (!$userData || !isset($userData['id'], $userData['firstname'], $userData['role'])) {
    http_response_code(400);
    header('Content-Type: application/json');
    echo json_encode(['success' => false, 'error' => 'Données utilisateur invalides']);
    exit;
}

// ===== MISE À JOUR SESSION =====

/**
 * Stockage des informations utilisateur en session PHP
 */
$_SESSION['user'] = [
    'id' => (int)$userData['id'],
    'firstname' => htmlspecialchars($userData['firstname'], ENT_QUOTES, 'UTF-8'),
    'role' => htmlspecialchars($userData['role'], ENT_QUOTES, 'UTF-8')
];

// ===== RÉPONSE =====

/**
 * Retour de confirmation au frontend
 */
header('Content-Type: application/json');
echo json_encode(['success' => true]);
?>