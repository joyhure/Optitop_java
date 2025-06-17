<?php
session_start();

if (
  !isset($_SESSION['user']) ||
  !isset($_SESSION['user']['role']) ||
  !in_array($_SESSION['user']['role'], ['admin', 'supermanager', 'manager'])
) {
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
  <title>Optitop - Chiffre d'Affaires</title>
  <meta name="description" content="Analyse du chiffre d'affaires et performances commerciales - Optitop">
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
  $pageTitle = "Chiffre d'Affaires";
  require_once 'components/header.html';
  require_once 'components/navbar.php';
  ?>

  <!-- ===== CONTENU PRINCIPAL ===== -->
  <main id="main-revenue" class="d-flex flex-column justify-content-between">

    <!-- En-tête section chiffre d'affaires -->
    <div class="d-flex d-inline-flex my-4">
      <div class="d-inline-flex align-items-center justify-content-center fs-2">
        <svg class="bi svg-average-basket rounded" width="1em" height="1em">
          <use xlink:href="assets/vendor/bootstrap-icons/bootstrap-icons.svg#calendar2"></use>
        </svg>
      </div>
      <h3 class="px-4 mb-0">Année</h3>
    </div>

    <!-- Section dynamique des revenus -->
    <div id="revenue-sections">
      <!-- Le contenu sera généré dynamiquement par JavaScript -->
    </div>

    <!-- Section résumé et statistiques -->
    <section id="summary" class="d-flex flex-column justify-content-center align-items-center gap-3">

      <!-- Carte chiffre d'affaires total -->
      <div class="card px-1 py-3 w-100">
        <div class="feature-icon d-inline-flex align-items-center justify-content-center fs-2">
          <svg class="bi svg-feature rounded" width="1em" height="1em">
            <use xlink:href="assets/vendor/bootstrap-icons/bootstrap-icons.svg#currency-euro"></use>
          </svg>
        </div>
        <h2 id="total-revenue" class="text-center pt-2">-</h2>
        <h2 id="total-delta-percent" class="text-center pt-2">-</h2>
      </div>

      <!-- Tableau répartition vendeurs -->
      <div class="table-responsive table-card small w-100">
        <table class="table table-striped table-sm mb-0">
          <thead>
            <tr class="text-center">
              <th scope="col" class="row-blueperso">Nom</th>
              <th scope="col" class="row-blueperso">CA</th>
              <th scope="col" class="row-blueperso">% CA</th>
            </tr>
          </thead>
          <tbody id="sellers-revenue-body">
            <tr>
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
  <script src="assets/js/revenue.js"></script>
</body>

</html>