<?php

/**
 * Page de synthèse des données commerciales
 * 
 * Permet aux utilisateurs autorisés (admin, supermanager, manager) de :
 * - Visualiser une synthèse globale du chiffre d'affaires
 * - Consulter les comparaisons avec l'année précédente
 * - Analyser les performances individuelles des collaborateurs
 * - Suivre les primes, P2, devis et taux de concrétisation
 * - Obtenir une vue d'ensemble des indicateurs clés du magasin
 * 
 * Interface centralisée de pilotage commercial avec
 * tableaux de bord synthétiques et KPI essentiels.
 */

// Démarrage de la session
session_start();

// ===== VÉRIFICATION DES DROITS D'ACCÈS =====

/**
 * Contrôle des permissions utilisateur
 * Seuls les rôles admin, supermanager et manager peuvent accéder
 * aux données de synthèse commerciale du magasin.
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
    <title>Optitop - Synthèse Commerciale</title>
    <meta name="description" content="Synthèse des données commerciales et performances - Optitop">
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
    $pageTitle = "Synthèse";
    require_once 'components/header.html';
    require_once 'components/navbar.php';
    ?>

    <!-- ===== CONTENU PRINCIPAL ===== -->
    <main id="main-quotations" class="d-flex flex-row justify-content-between">
        
        <!-- Section synthèse des données -->
        <section id="table-quotations-section" class="table-responsive small">
            
            <!-- En-tête synthèse -->
            <div class="d-flex d-inline-flex my-4">
                <div class="d-inline-flex align-items-center justify-content-center fs-2">
                    <svg class="bi svg-average-basket rounded" width="1em" height="1em">
                        <use xlink:href="assets/vendor/bootstrap-icons/bootstrap-icons.svg#file-earmark-spreadsheet"></use>
                    </svg>
                </div>
                <h3 class="px-4 mb-0">Synthèse</h3>
            </div>

            <!-- Container des tableaux -->
            <div class="container">
                
                <!-- Tableau chiffres du magasin -->
                <table class="table table-bordered">
                    <thead>
                        <tr>
                            <th id="table-header" colspan="5">
                                <h2 class="text-center fw-bold">Chiffres du Magasin</h2>
                            </th>
                        </tr>
                        <tr>
                            <th class="text-center" colspan="2">
                                <h5 id="period-dates" class="text-secondary">Chargement...</h5>
                            </th>
                            <th class="bg-secondary-subtle"></th>
                            <th class="text-center" colspan="2">
                                <h5 id="last-update" class="text-secondary">Chargement...</h5>
                            </th>
                        </tr>
                        <tr>
                            <th id="table-subtitle" class="text-center">Catégorie</th>
                            <th id="table-subtitle" class="text-center">CA Facturé</th>
                            <th id="table-subtitle" class="text-center">Delta CA</th>
                            <th id="table-subtitle" class="text-center">Delta CA %</th>
                            <th id="table-subtitle" class="text-center">Taux Concret</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td class="text-center">N</td>
                            <td id="current-revenue" class="text-center">-</td>
                            <td id="revenue-delta" rowspan="2" class="align-middle text-center">-</td>
                            <td id="revenue-delta-percent" rowspan="2" class="align-middle text-center">-</td>
                            <td id="current-rate" class="text-center">-</td>
                        </tr>
                        <tr>
                            <td class="text-center">N-1</td>
                            <td id="previous-revenue" class="text-center">-</td>
                            <td id="previous-rate" class="text-center">-</td>
                        </tr>
                    </tbody>
                </table>

                <!-- Tableau statistiques collaborateurs -->
                <table class="table table-bordered table-striped">
                    <thead>
                        <tr>
                            <th id="table-header" colspan="8">
                                <h2 class="text-center fw-bold">Statistiques Collaborateurs</h2>
                            </th>
                        </tr>
                        <tr>
                            <th class="text-center">
                                <h5 class="text-secondary">Nom</h5>
                            </th>
                            <th class="text-center" colspan="2">
                                <h5 class="text-secondary">Primes</h5>
                            </th>
                            <th class="text-center" colspan="2">
                                <h5 class="text-secondary">P2</h5>
                            </th>
                            <th class="text-center" colspan="2">
                                <h5 class="text-secondary">Devis</h5>
                            </th>
                            <th class="text-center">
                                <h5 class="text-secondary">CA</h5>
                            </th>
                        </tr>
                        <tr>
                            <th class="bg-secondary-subtle"></th>
                            <th id="table-subtitle" class="text-center">Prime Monture</th>
                            <th id="table-subtitle" class="text-center">% Mont. primées</th>
                            <th id="table-subtitle" class="text-center">Nombre</th>
                            <th id="table-subtitle" class="text-center">Panier Moyen</th>
                            <th id="table-subtitle" class="text-center">Nombre</th>
                            <th id="table-subtitle" class="text-center">Concrétisation</th>
                            <th class="bg-secondary-subtle"></th>
                        </tr>
                    </thead>
                    <tbody id="collaborators-data">
                        <!-- Le contenu sera généré dynamiquement par JavaScript -->
                        <tr>
                            <td class="text-center align-middle"></td>
                            <td class="text-center align-middle"></td>
                            <td class="text-center align-middle"></td>
                            <td class="text-center align-middle"></td>
                            <td class="text-center align-middle"></td>
                            <td class="text-center align-middle"></td>
                            <td class="text-center align-middle"></td>
                            <td class="text-center align-middle"></td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </section>
    </main>

    <!-- ===== SCRIPTS ===== -->
    <script src="assets/vendor/bootstrap/bootstrap.bundle.min.js"></script>
    <script src="assets/js/header.js"></script>
    <script src="assets/js/navbar.js"></script>
    <script src="assets/js/summary.js"></script>
</body>

</html>