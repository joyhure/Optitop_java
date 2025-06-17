<?php
/**
 * Page d'importation des données de vente
 * 
 * Permet aux utilisateurs autorisés (admin, supermanager, manager) d'importer
 * des fichiers CSV contenant les données de vente dans le système.
 */

// Démarrage de la session
session_start();

// ===== VÉRIFICATION DES DROITS D'ACCÈS =====

/**
 * Contrôle des permissions utilisateur
 * Seuls les rôles admin, supermanager et manager peuvent accéder
 * à la fonctionnalité d'importation des données.
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
  <title>Optitop - Importation</title>
  <meta name="description" content="Interface d'importation des données de vente - Optitop">
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
  $pageTitle = "Importation";
  require_once 'components/header.html';
  require_once 'components/navbar.php';
  ?>

  <!-- ===== CONTENU PRINCIPAL ===== -->
  <main id="main-quotations" class="d-flex flex-row justify-content-between">
    
    <!-- Section formulaire d'importation -->
    <section id="table-quotations-section" class="table-responsive small">
      
      <!-- En-tête de la page -->
      <div class="d-flex d-inline-flex my-4">
        <div class="d-inline-flex align-items-center justify-content-center fs-2">
          <svg class="bi svg-average-basket rounded" width="1em" height="1em">
            <use xlink:href="assets/vendor/bootstrap-icons/bootstrap-icons.svg#file-earmark-arrow-up"></use>
          </svg>
        </div>
        <h3 class="px-4 mb-0">Importation des données de vente</h3>
      </div>

      <!-- Formulaire d'importation -->
      <form id="importForm" class="m-5">
        
        <!-- Sélection du fichier CSV -->
        <div class="mb-3">
          <label for="csvFile" class="form-label">Choisir un fichier CSV</label>
          <input type="file" class="form-control" id="csvFile" name="csvFile" accept=".csv" required>
        </div>

        <!-- Barre de progression -->
        <div class="progress mb-3 d-none" id="uploadProgress">
          <div class="progress-bar" role="progressbar" style="width: 0%"></div>
        </div>

        <!-- Zone d'affichage du statut -->
        <div id="importStatus" class="alert d-none"></div>

        <!-- Bouton de soumission -->
        <button type="submit" class="btn btn-primary">Importer</button>
      </form>
    </section>
  </main>

  <!-- ===== SCRIPTS ===== -->
  <script src="assets/vendor/bootstrap/bootstrap.bundle.min.js"></script>
  <script src="assets/js/header.js"></script>
  <script src="assets/js/navbar.js"></script>
  <script src="assets/js/import.js"></script>
</body>

</html>