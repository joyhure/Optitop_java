<?php
session_start();

// Vérification PHP principale
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
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>CA</title>
  <meta name="author" content="Joy Huré">
  <link rel="icon" href="assets/images/favicon.png">
  <link rel="stylesheet" href="../node_modules/bootstrap/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="../node_modules/bootstrap-icons/font/bootstrap-icons.css">
  <link rel="stylesheet" href="assets/styles/custom.css">
</head>

<body>
  <?php
  $pageTitle = "Chiffre d'Affaires";
  require_once 'components/header.html';
  require_once 'components/navbar.php';
  ?>
  <main id="main-revenue" class="d-flex flex-column justify-content-between">

    <div class="d-flex d-inline-flex my-4">
      <div class="d-inline-flex align-items-center justify-content-center fs-2">
        <svg class="bi svg-average-basket rounded" width="1em" height="1em">
          <use xlink:href="../node_modules/bootstrap-icons/bootstrap-icons.svg#calendar2"></use>
        </svg>
      </div>
      <h3 class="px-4 mb-0">Année</h3>
    </div>

    <div id="revenue-sections"></div>

    <section id="summary" class="d-flex flex-column justify-content-center align-items-center gap-3">
      <div class="card px-1 py-3 w-100">
        <div class="feature-icon d-inline-flex align-items-center justify-content-center fs-2">
          <svg class="bi svg-feature rounded" width="1em" height="1em">
            <use xlink:href="../node_modules/bootstrap-icons/bootstrap-icons.svg#currency-euro"></use>
          </svg>
        </div>
        <h2 class="text-center pt-2">1119000€</h2>
        <h2 class="text-center pt-2">+10%</h2>
      </div>
      <div class="table-responsive table-card small w-100">
        <table class="table table-striped table-sm mb-0">
          <thead>
            <tr class="text-center">
              <th scope="col" class="row-blueperso">Nom</th>
              <th scope="col" class="row-blueperso">CA</th>
              <th scope="col" class="row-blueperso">% CA</th>
            </tr>
          </thead>
          <tbody>
            <tr class="text-center">
              <td>BS</td>
              <td>11506€</td>
              <td>25%</td>
            </tr>
            <tr class="text-center">
              <td>EG</td>
              <td>110000€</td>
              <td>51%</td>
            </tr>
            <tr class="text-center">
              <td>IH</td>
              <td>90000€</td>
              <td>15%</td>
            </tr>
            <tr class="text-center">
              <td>IR</td>
              <td>60000€</td>
              <td>18%</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </main>

  <script src="../node_modules/bootstrap/dist/js/bootstrap.bundle.min.js"></script>
  <script src="assets/js/header.js"></script>
  <script src="assets/js/navbar.js"></script>
  <script src="assets/js/revenue.js"></script>
</body>

</html>