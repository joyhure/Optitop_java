<!DOCTYPE html>
<html lang="fr">

<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Optitop - Dashboard</title>
  <meta name="description" content="Tableau de bord Optitop - Suivi des performances magasin et personnelles">
  <meta name="author" content="Joy Huré">

  <!-- Favicon -->
  <link rel="icon" href="assets/images/favicon.png">

  <!-- Styles -->
  <link rel="stylesheet" href="assets/vendor/bootstrap/bootstrap.min.css">
  <link rel="stylesheet" href="assets/vendor/bootstrap-icons/bootstrap-icons.css">
  <link rel="stylesheet" href="assets/styles/custom.css">
</head>

<body>
  <?php
  /**
   * Configuration de la page et inclusion des composants
   */
  $pageTitle = "Dashboard";
  require_once 'components/header.html';
  require_once 'components/navbar.php';
  ?>

  <!-- Contenu principal -->
  <main id="main-dashboard" class="d-flex justify-content-around flex-wrap my-3">

    <!-- Section magasin -->
    <section id="shop-selection" class="feature col w-25">
      
      <!-- En-tête section magasin -->
      <div class="d-flex flex-column justify-content-center align-items-center mb-4 mt-3">
        <svg class="bi d-block mx-auto mb-1" width="30" height="30">
          <use xlink:href="assets/vendor/bootstrap-icons/bootstrap-icons.svg#building"></use>
        </svg>
        <h4>Magasin</h4>
      </div>

      <!-- Première ligne - CA et Devis -->
      <div id="first-line" class="flex-row d-flex justify-content-between align-items-center">
        
        <!-- Card Chiffre d'affaires -->
        <a id="ca" href="revenue.php" class="text-decoration-none card card-dashboard my-3 px-1 pt-2">
          <div class="feature-icon d-inline-flex align-items-center justify-content-center fs-2">
            <svg class="bi svg-feature rounded text-primary" width="1em" height="1em">
              <use xlink:href="assets/vendor/bootstrap-icons/bootstrap-icons.svg#currency-euro"></use>
            </svg>
          </div>
          <h2 id="total-revenue" class="text-center pt-2 text-dark">-</h2>
        </a>

        <!-- Card Devis -->
        <a id="quotation" href="quotations.php" class="text-decoration-none card card-dashboard my-3 px-1 pt-2">
          <div class="feature-icon d-inline-flex align-items-center justify-content-center fs-2">
            <svg class="bi svg-feature rounded" width="1em" height="1em">
              <use xlink:href="assets/vendor/bootstrap-icons/bootstrap-icons.svg#clipboard2"></use>
            </svg>
          </div>
          <h2 id="store-concretization-rate" class="text-center pt-2 text-dark">-</h2>
        </a>
      </div>

      <!-- Deuxième ligne - Panier moyen et Bonus -->
      <div id="second-line" class="flex-row d-flex justify-content-between align-items-center">
        
        <!-- Card Panier moyen -->
        <a id="average-basket" href="average-basket.php" class="text-decoration-none card card-dashboard my-3 px-1 pt-2">
          <div class="feature-icon d-inline-flex align-items-center justify-content-center fs-2">
            <svg class="bi svg-feature rounded" width="1em" height="1em">
              <use xlink:href="assets/vendor/bootstrap-icons/bootstrap-icons.svg#cart4"></use>
            </svg>
          </div>
          <div class="p-2">
            Panier Moyen (opt) : <h4 id="store-average-basket" class="text-dark text-center">-</h4>
            Panier Moyen P2 : <h4 id="store-average-p2" class="text-dark text-center mb-0">-</h4>
          </div>
        </a>

        <!-- Card Bonus -->
        <a id="bonus" href="average-basket.php" class="text-decoration-none card card-dashboard my-3 px-3 pt-2">
          <div class="feature-icon d-inline-flex align-items-center justify-content-center fs-2 mb-2">
            <svg class="bi svg-feature rounded" width="1em" height="1em">
              <use xlink:href="assets/vendor/bootstrap-icons/bootstrap-icons.svg#piggy-bank"></use>
            </svg>
          </div>
          <div class="p-2">
            Montures Primées : <h4 id="store-rate-premium-frame" class="text-dark text-center mb-0">-</h4>
            Nombre : <h4 id="store-nb-premium-frame" class="text-dark text-center mb-0">-</h4>
          </div>
        </a>
      </div>
    </section>

    <!-- Section personnelle -->
    <section id="personal-section" class="col w-25">
      
      <!-- En-tête section personnelle -->
      <div class="d-flex flex-column justify-content-center align-items-center mb-4 mt-3">
        <svg class="bi d-block mx-auto mb-1" width="30" height="30">
          <use xlink:href="assets/vendor/bootstrap-icons/bootstrap-icons.svg#person-circle"></use>
        </svg>
        <h4>-</h4>
      </div>

      <!-- Première ligne - CA personnel et Devis personnels -->
      <div id="first-line" class="flex-row d-flex justify-content-between align-items-center">
        
        <!-- Card CA personnel -->
        <div id="personal-ca" class="card my-3 px-3 pt-2">
          <div class="feature-icon d-inline-flex align-items-center justify-content-center fs-2 mb-2">
            <svg class="bi svg-feature rounded" width="1em" height="1em">
              <use xlink:href="assets/vendor/bootstrap-icons/bootstrap-icons.svg#currency-euro"></use>
            </svg>
          </div>
          <h2 id="personal-revenue" class="text-center text-dark">-</h2>
          % CA Magasin : <h4 id="personal-revenue-percent" class="text-dark text-center mb-0 pb-2">-</h4>
        </div>

        <!-- Card Devis personnels -->
        <a id="personal-quotations" href="quotations.php" class="text-decoration-none card card-dashboard my-3 px-3 pt-2">
          <div class="feature-icon d-inline-flex align-items-center justify-content-center fs-2 mb-2">
            <svg class="bi svg-feature rounded" width="1em" height="1em">
              <use xlink:href="assets/vendor/bootstrap-icons/bootstrap-icons.svg#clipboard2"></use>
            </svg>
          </div>
          <h2 id="personal-concretization-rate" class="text-center text-dark">-</h2>
          Nb Non validés : <h4 id="personal-unvalidated-quotations" class="text-dark text-center mb-0 pb-2">-</h4>
        </a>
      </div>

      <!-- Deuxième ligne - Panier personnel et Bonus personnel -->
      <div id="second-line" class="flex-row d-flex justify-content-between align-items-center">
        
        <!-- Card Panier personnel -->
        <div id="personal-basket-container" class="card my-3 px-3 pt-2">
          <div class="feature-icon d-inline-flex align-items-center justify-content-center fs-2 mb-2">
            <svg class="bi svg-feature rounded" width="1em" height="1em">
              <use xlink:href="assets/vendor/bootstrap-icons/bootstrap-icons.svg#cart4"></use>
            </svg>
          </div>
          <div class="p-2">
            Panier Moyen (opt) : <h4 id="personal-average-basket-value" class="text-dark text-center">-</h4>
            Panier Moyen P2 : <h4 id="personal-average-p2-value" class="text-dark text-center mb-0">-</h4>
          </div>
        </div>

        <!-- Card Bonus personnel -->
        <div id="personal-bonus-frame-card" class="card card my-3 px-3 pt-2">
          <div class="feature-icon d-inline-flex align-items-center justify-content-center fs-2 mb-2">
            <svg class="bi svg-feature rounded" width="1em" height="1em">
              <use xlink:href="assets/vendor/bootstrap-icons/bootstrap-icons.svg#piggy-bank"></use>
            </svg>
          </div>
          <div class="p-2">
            Montures primées : <h4 id="personal-rate-premium-frame" class="text-dark text-center">-</h4>
            Prime Montures : <h4 id="personal-bonus-frame" class="text-dark text-center mb-0">-</h4>
          </div>
        </div>
      </div>
    </section>
  </main>

  <!-- Scripts -->
  <script src="assets/vendor/bootstrap/bootstrap.bundle.min.js"></script>
  <script src="assets/js/header.js"></script>
  <script src="assets/js/navbar.js"></script>
  <script src="assets/js/dashboard.js"></script>
</body>

</html>