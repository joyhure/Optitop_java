<?php
session_start();

// Vérification PHP principale
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
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Paniers Moyens & Primes</title>
  <meta name="author" content="Joy Huré">
  <link rel="icon" href="assets/images/favicon.png">
  <link rel="stylesheet" href="../node_modules/bootstrap/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="../node_modules/bootstrap-icons/font/bootstrap-icons.css">
  <link rel="stylesheet" href="assets/styles/custom.css">
</head>

<body>
  <?php
  $pageTitle = "Paniers Moyens & Primes";
  require_once 'components/header.html';
  require_once 'components/navbar.php';
  ?>
  <main id="main-quotations" class="d-flex flex-row justify-content-between">
    <section id="table-quotations-section" class="table-responsive small">
      <div class="d-flex d-inline-flex my-4">
        <div class="d-inline-flex align-items-center justify-content-center fs-2">
          <svg class="bi svg-average-basket rounded" width="1em" height="1em">
            <use xlink:href="../node_modules/bootstrap-icons/bootstrap-icons.svg#cart4"></use>
          </svg>
        </div>
        <h3 class="px-4 mb-0">Paniers Moyens</h3>
      </div>
      <table class="table table-striped table-sm">
        <thead>
          <tr>
            <th scope="col" class="table-col-w15 text-center">Nom</th>
            <th scope="col" class="table-col-w15 text-center">PM</th>
            <th scope="col" class="table-col-w15 text-center">Nb factures</th>
            <th scope="col" class="table-col-w15 text-center">PM P1 Montures</th>
            <th scope="col" class="table-col-w15 text-center">PM P1 Verres</th>
            <th scope="col" class="table-col-w15 text-center">PM P2</th>
          </tr>
        </thead>
        <tbody id="table-baskets-body">
          <!-- Les données seront injectées ici par JavaScript -->
        </tbody>
      </table>

      <div class="d-flex d-inline-flex my-4">
        <div class="d-inline-flex align-items-center justify-content-center fs-2">
          <svg class="bi svg-average-basket rounded" width="1em" height="1em">
            <use xlink:href="../node_modules/bootstrap-icons/bootstrap-icons.svg#piggy-bank"></use>
          </svg>
        </div>
        <h3 class="px-4 mb-0">Primes Montures</h3>
      </div>
      <table class="table table-striped table-sm">
        <thead>
          <tr>
            <th scope="col" class="table-col-w15 text-center">Nom</th>
            <th scope="col" class="table-col-w15 text-center">Nb montures</th>
            <th scope="col" class="table-col-w15 text-center">Nb primées</th>
            <th scope="col" class="table-col-w15 text-center">%</th>
            <th scope="col" class="table-col-w15 text-center">Montant</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td class="text-center">IR</td>
            <td class="text-center">35</td>
            <td class="text-center">10</td>
            <td class="text-center">35%</td>
            <td class="text-center">70€</td>
          </tr>
          <tr>
            <td class="text-center">IH</td>
            <td class="text-center">35</td>
            <td class="text-center">10</td>
            <td class="text-center">35%</td>
            <td class="text-center">70€</td>
          </tr>
          <tr>
            <td class="text-center">BD</td>
            <td class="text-center">35</td>
            <td class="text-center">10</td>
            <td class="text-center">35%</td>
            <td class="text-center">70€</td>
          </tr>
          <tr>
            <td class="text-center">EG</td>
            <td class="text-center">35</td>
            <td class="text-center">10</td>
            <td class="text-center">35%</td>
            <td class="text-center">70€</td>
          </tr>
          <tr>
            <td class="text-center fw-bold">Total</td>
            <td class="text-center">35</td>
            <td class="text-center">10</td>
            <td class="text-center">35%</td>
            <td class="text-center">70€</td>
          </tr>
        </tbody>
      </table>
    </section>
    <section id="summary" class="d-flex flex-column justify-content-center align-items-center gap-5">
      <div class="card px-1 py-3 w-100">
        <div class="feature-icon d-inline-flex align-items-center justify-content-center fs-2">
          <svg class="bi svg-feature rounded" width="1em" height="1em">
            <use xlink:href="../node_modules/bootstrap-icons/bootstrap-icons.svg#cart4"></use>
          </svg>
        </div>
        <h2 id = "card-pm" class="text-center pt-2"></h2>
        <p class="text-center mb-0"> Panier Moyen
        </p>
      </div>
      <div class="card px-1 py-3 w-100">
        <div class="feature-icon d-inline-flex align-items-center justify-content-center fs-2">
          <svg class="bi svg-feature rounded" width="1em" height="1em">
            <use xlink:href="../node_modules/bootstrap-icons/bootstrap-icons.svg#2-square"></use>
          </svg>
        </div>
        <h2 id = "card-p2" class="text-center pt-2"></h2>
        <p class="text-center mb-0"> Panier Moyen P2
        </p>
      </div>
    </section>
  </main>

  </div>

  </div>
  <script src="../node_modules/bootstrap/dist/js/bootstrap.bundle.min.js"></script>
  <script src="assets/js/header.js"></script>
  <script src="assets/js/navbar.js"></script>
  <script src="assets/js/average-basket.js"></script>
</body>

</html>