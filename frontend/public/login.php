<?php
session_start();

// Récupérer les données JSON
$jsonData = file_get_contents('php://input');
$userData = json_decode($jsonData, true);

// Mettre à jour la session PHP
$_SESSION['user'] = [
    'id' => $userData['id'],
    'firstname' => $userData['firstname'],
    'role' => $userData['role']
];

header('Content-Type: application/json');
echo json_encode(['success' => true]);