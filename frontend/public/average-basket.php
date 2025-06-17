<?php

/**
 * Page d'affichage des paniers moyens et primes
 * 
 * Permet aux utilisateurs autorisés (admin, supermanager, manager) de :
 * - Visualiser les statistiques de paniers moyens par vendeur
 * - Consulter les primes sur les montures
 * - Analyser les performances commerciales globales
 * - Suivre l'évolution des ventes P1 et P2
 */

// Démarrage de la session
session_start();

// ===== VÉRIFICATION DES DROITS D'ACCÈS =====

/**
 * Contrôle des permissions utilisateur
 * Seuls les rôles admin, supermanager et manager peuvent accéder
 * aux statistiques de paniers moyens et primes.
 */
if (!isset($_SESSION['user']) || 
    !isset($_SESSION['user']['role']) || 
    !in_array($_SESSION['user']['role'], ['admin', 'supermanager', 'manager'])) {
    header('Location: dashboard.php');
    exit();
}
?>
<!DOCTYPE html>
<html lang="fr">

<head>
    <!-- ===== MÉTADONNÉES ===== -->
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Optitop - Paniers Moyens & Primes</title>
    <meta name="description" content="Statistiques des paniers moyens et primes - Optitop">
    <meta name="author" content="Joy Huré">

    <!-- ===== FAVICON ===== -->
    <link rel="icon" href="assets/images/favicon.png">

    <!-- ===== STYLES ===== -->
    <link rel="stylesheet" href="assets/vendor/bootstrap/bootstrap.min.css">
    <link rel="stylesheet" href="assets/vendor/bootstrap-icons/bootstrap-icons.css">
    <link rel="stylesheet" href="assets/styles/custom.css">
</head>

<body>
    <?php
    /**
     * Configuration de la page et inclusion des composants
     */
    $pageTitle = "Paniers Moyens & Primes";
    require_once 'components/header.html';
    require_once 'components/navbar.php';
    ?>

    <!-- ===== CONTENU PRINCIPAL ===== -->
    <main id="main-quotations" class="d-flex flex-row justify-content-between">
        
        <!-- Section tableaux statistiques -->
        <section id="table-quotations-section" class="table-responsive small">
            
            <!-- En-tête paniers moyens -->
            <div class="d-flex d-inline-flex my-4">
                <div class="d-inline-flex align-items-center justify-content-center fs-2">
                    <svg class="bi svg-average-basket rounded" width="1em" height="1em">
                        <use xlink:href="assets/vendor/bootstrap-icons/bootstrap-icons.svg#cart4"></use>
                    </svg>
                </div>
                <h3 class="px-4 mb-0">Paniers Moyens</h3>
            </div>
            
            <!-- Tableau paniers moyens -->
            <table class="table table-striped table-sm">
                <thead>
                    <tr>
                        <th scope="col" class="table-col-w15 text-center align-middle">Nom</th>
                        <th scope="col" class="table-col-w15 text-center align-middle">PM</th>
                        <th scope="col" class="table-col-w15 text-center align-middle">Nb factures</th>
                        <th scope="col" class="table-col-w15 text-center align-middle">PM P1 Montures</th>
                        <th scope="col" class="table-col-w15 text-center align-middle">PM P1 Verres</th>
                        <th scope="col" class="table-col-w15 text-center align-middle">PM P2</th>
                    </tr>
                </thead>
                <tbody id="table-baskets-body">
                    <tr>
                        <td class="text-center align-middle"></td>
                        <td class="text-center align-middle"></td>
                        <td class="text-center align-middle"></td>
                        <td class="text-center align-middle"></td>
                        <td class="text-center align-middle"></td>
                        <td class="text-center align-middle"></td>
                    </tr>
                </tbody>
            </table>

            <!-- En-tête primes montures -->
            <div class="d-flex d-inline-flex my-4">
                <div class="d-inline-flex align-items-center justify-content-center fs-2">
                    <svg class="bi svg-average-basket rounded" width="1em" height="1em">
                        <use xlink:href="assets/vendor/bootstrap-icons/bootstrap-icons.svg#piggy-bank"></use>
                    </svg>
                </div>
                <h3 class="px-4 mb-0">Primes Montures</h3>
            </div>
            
            <!-- Tableau primes montures -->
            <table class="table table-striped table-sm">
                <thead>
                    <tr>
                        <th scope="col" class="table-col-w15 text-center align-middle">Nom</th>
                        <th scope="col" class="table-col-w15 text-center align-middle">Nb montures</th>
                        <th scope="col" class="table-col-w15 text-center align-middle">Nb primées</th>
                        <th scope="col" class="table-col-w15 text-center align-middle">%</th>
                        <th scope="col" class="table-col-w15 text-center align-middle">Montant</th>
                    </tr>
                </thead>
                <tbody id="table-frames-body">
                    <tr>
                        <td class="text-center align-middle"></td>
                        <td class="text-center align-middle"></td>
                        <td class="text-center align-middle"></td>
                        <td class="text-center align-middle"></td>
                        <td class="text-center align-middle"></td>
                    </tr>
                </tbody>
            </table>
        </section>

        <!-- Section résumé -->
        <section id="summary" class="d-flex flex-column justify-content-center align-items-center gap-3">
            
            <!-- Carte panier moyen global -->
            <div class="card px-1 py-3 w-100">
                <div class="feature-icon d-inline-flex align-items-center justify-content-center fs-2">
                    <svg class="bi svg-feature rounded" width="1em" height="1em">
                        <use xlink:href="assets/vendor/bootstrap-icons/bootstrap-icons.svg#cart4"></use>
                    </svg>
                </div>
                <h2 id="card-pm" class="text-center pt-2"></h2>
                <p class="text-center mb-0">Panier Moyen</p>
            </div>

            <!-- Carte panier moyen P2 -->
            <div class="card px-1 py-3 w-100">
                <div class="feature-icon d-inline-flex align-items-center justify-content-center fs-2">
                    <svg class="bi svg-feature rounded" width="1em" height="1em">
                        <use xlink:href="assets/vendor/bootstrap-icons/bootstrap-icons.svg#2-square"></use>
                    </svg>
                </div>
                <h2 id="card-p2" class="text-center pt-2"></h2>
                <p class="text-center mb-0">Panier Moyen P2</p>
            </div>
        </section>
    </main>

    <!-- ===== SCRIPTS ===== -->
    <script src="assets/vendor/bootstrap/bootstrap.bundle.min.js"></script>
    <script src="assets/js/header.js"></script>
    <script src="assets/js/navbar.js"></script>
    <script src="assets/js/average-basket.js"></script>
</body>

</html>