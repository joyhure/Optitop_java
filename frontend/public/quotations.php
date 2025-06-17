<?php

/**
 * Page de gestion des devis optiques
 * 
 * Permet aux utilisateurs autorisés de :
 * - Visualiser les devis non validés en attente
 * - Valider ou rejeter les devis clients
 * - Ajouter des commentaires sur les devis
 * - Consulter les statistiques de validation par vendeur
 * - Suivre le taux de concrétisation global du magasin
 * 
 * Interface de tri et filtrage avancée pour optimiser
 * la gestion quotidienne des devis optiques.
 */

// ===== VÉRIFICATION DES DROITS D'ACCÈS =====

/**
 * Contrôle des permissions utilisateur
 * Tous les utilisateurs connectés peuvent accéder aux devis,
 * mais certaines statistiques sont réservées aux managers.
 */
// session_start(); // Géré par les composants header/navbar
?>
<!DOCTYPE html>
<html lang="fr">

<head>
    <!-- ===== MÉTADONNÉES ===== -->
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Optitop - Devis Optiques</title>
    <meta name="description" content="Gestion des devis optiques - Validation et suivi des devis clients">
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
    $pageTitle = "Devis Optiques";
    require_once 'components/header.html';
    require_once 'components/navbar.php';
    ?>

    <!-- ===== CONTENU PRINCIPAL ===== -->
    <main id="main-quotations" class="d-flex flex-row justify-content-between">
        
        <!-- Section tableau des devis -->
        <section id="table-quotations-section" class="table-responsive small">
            
            <!-- En-tête devis non validés -->
            <div class="d-flex d-inline-flex my-4">
                <div class="d-inline-flex align-items-center justify-content-center fs-2">
                    <svg class="bi svg-unvalidated-quotations rounded" width="1em" height="1em">
                        <use xlink:href="assets/vendor/bootstrap-icons/bootstrap-icons.svg#clipboard2-x"></use>
                    </svg>
                </div>
                <h3 class="px-4 mb-0">Devis Non Validés</h3>
            </div>
            
            <!-- Tableau des devis avec tri -->
            <table class="table table-striped table-sm">
                <thead>
                    <tr>
                        <!-- Colonne Date avec tri -->
                        <th scope="col" class="table-col-date">
                            <div class="d-flex align-items-center">
                                Date
                                <div class="ms-2 sort-icons">
                                    <svg class="bi sort-icon" width="1em" height="1em" data-sort="date" data-order="asc">
                                        <use xlink:href="assets/vendor/bootstrap-icons/bootstrap-icons.svg#sort-down"></use>
                                    </svg>
                                </div>
                            </div>
                        </th>
                        
                        <!-- Colonne Nom avec tri -->
                        <th scope="col" class="table-col-w4">
                            <div class="d-flex align-items-center">
                                Nom
                                <div class="ms-2 sort-icons">
                                    <svg class="bi sort-icon" width="1em" height="1em" data-sort="name" data-order="asc">
                                        <use xlink:href="assets/vendor/bootstrap-icons/bootstrap-icons.svg#sort-down"></use>
                                    </svg>
                                </div>
                            </div>
                        </th>
                        
                        <!-- Colonne Client avec tri -->
                        <th scope="col" class="table-col-w20">
                            <div class="d-flex align-items-center">
                                Client
                                <div class="ms-2 sort-icons">
                                    <svg class="bi sort-icon" width="1em" height="1em" data-sort="client" data-order="asc">
                                        <use xlink:href="assets/vendor/bootstrap-icons/bootstrap-icons.svg#sort-down"></use>
                                    </svg>
                                </div>
                            </div>
                        </th>
                        
                        <!-- Colonne Action -->
                        <th scope="col" class="table-col-w20">Action</th>
                        
                        <!-- Colonne Commentaire -->
                        <th scope="col" class="table-col-commentaire">Commentaire</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td class="text-center align-middle"></td>
                        <td class="text-center align-middle"></td>
                        <td class="text-center align-middle"></td>
                        <td class="text-center align-middle"></td>
                        <td class="text-center align-middle"></td>
                    </tr>
                </tbody>
            </table>

            <!-- Bouton d'enregistrement -->
            <div id="save-button-container" class="mt-3">
                <button id="save-changes-button" class="btn row-blueperso" disabled>
                    Enregistrer
                </button>
            </div>
        </section>

        <!-- Section résumé et statistiques -->
        <section id="summary" class="d-flex flex-column justify-content-center align-items-center gap-3">
            
            <!-- Carte taux de concrétisation -->
            <div id="quotation" class="card px-1 py-2 w-100">
                <div class="feature-icon d-inline-flex align-items-center justify-content-center fs-2">
                    <svg class="bi svg-feature rounded" width="1em" height="1em">
                        <use xlink:href="assets/vendor/bootstrap-icons/bootstrap-icons.svg#clipboard2"></use>
                    </svg>
                </div>
                <h2 id="store-concretization-rate" class="text-center pt-2"></h2>
                <p id="quotations-numbers" class="text-center mb-0">
                    <br>
                </p>
            </div>
            
            <!-- Tableau statistiques collaborateurs -->
            <div id="card-table-collaborators" 
                 class="table-responsive card small w-100 d-none"
                 data-requires-role="admin,manager,supermanager">
                <table class="table table-striped table-sm mb-0">
                    <thead>
                        <tr class="text-center">
                            <th scope="col" class="row-blueperso">Nom</th>
                            <th scope="col" class="row-blueperso">Nb</th>
                            <th scope="col" class="row-blueperso">Non V.</th>
                            <th scope="col" class="row-blueperso">Taux</th>
                        </tr>
                    </thead>
                    <tbody id="seller-stats-tbody">
                        <tr>
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

    <!-- ===== NOTIFICATIONS TOAST ===== -->
    <div class="toast-container position-fixed top-0 end-0 p-3">
        
        <!-- Toast succès -->
        <div id="successToast" class="toast align-items-center text-bg-success" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="d-flex">
                <div class="toast-body">
                    Modifications enregistrées avec succès
                </div>
                <button type="button" class="btn-close me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        </div>

        <!-- Toast erreur -->
        <div id="errorToast" class="toast align-items-center text-bg-danger" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="d-flex">
                <div class="toast-body">
                    Erreur lors de l'enregistrement des modifications
                </div>
                <button type="button" class="btn-close me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        </div>
    </div>

    <!-- ===== SCRIPTS ===== -->
    <script src="assets/vendor/bootstrap/bootstrap.bundle.min.js"></script>
    <script src="assets/js/header.js"></script>
    <script src="assets/js/navbar.js"></script>
    <script src="assets/js/quotations.js"></script>
</body>

</html>