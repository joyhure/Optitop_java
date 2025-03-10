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
    <section id="table-revenue-2024" class="table-responsive small w-100">
    <div class="d-flex d-inline-flex my-4">
        <div class="d-inline-flex align-items-center justify-content-center fs-2">
          <svg class="bi svg-average-basket rounded" width="1em" height="1em">
            <use xlink:href="../node_modules/bootstrap-icons/bootstrap-icons.svg#calendar2"></use>
          </svg>
        </div>
        <h3 class="px-4 mb-0">Année</h3>
      </div>
      <h4 class="py-3">
        <a class="text-decoration-none dropdown-toggle text-dark nav-link" data-bs-toggle="collapse" href="#collapseRevenue2024" role="button" aria-expanded="false" aria-controls="collapseRevenue2024">
          2024
        </a>
      </h4>
      <div class="collapse show" id="collapseRevenue2024">
        <table class="table table-striped table-sm">
          <thead>
            <tr>
              <th scope="col" class="table-col-w4 text-center align-middle">2024</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Janv.</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Fev.</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Mars</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Avril</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Mai</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Juin</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Juill.</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Août</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Sept.</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Oct.</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Nov.</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Déc.</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Année</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td class="text-center align-middle fw-bold">CA</td>
              <td class="text-center align-middle">119000€</td>
              <td class="text-center align-middle">119000€</td>
              <td class="text-center align-middle">119000€</td>
              <td class="text-center align-middle">119000€</td>
              <td class="text-center align-middle">119000€</td>
              <td class="text-center align-middle">119000€</td>
              <td class="text-center align-middle">119000€</td>
              <td class="text-center align-middle">119000€</td>
              <td class="text-center align-middle">119000€</td>
              <td class="text-center align-middle">119000€</td>
              <td class="text-center align-middle">119000€</td>
              <td class="text-center align-middle">119000€</td>
              <td class="text-center align-middle">1119000€</td>
            </tr>
            <tr>
              <td class="text-center align-middle fw-bold">Delta n-1</td>
              <td class="text-center align-middle">-3000</td>
              <td class="text-center align-middle">+21500</td>
              <td class="text-center align-middle">+21500</td>
              <td class="text-center align-middle">+21500</td>
              <td class="text-center align-middle">+21500</td>
              <td class="text-center align-middle">+21500</td>
              <td class="text-center align-middle">+21500</td>
              <td class="text-center align-middle">+21500</td>
              <td class="text-center align-middle">+21500</td>
              <td class="text-center align-middle">+21500</td>
              <td class="text-center align-middle">+21500</td>
              <td class="text-center align-middle">+21500</td>
              <td class="text-center align-middle">+21500</td>
            </tr>
            <tr>
              <td class="text-center align-middle fw-bold"> Delta %</td>
              <td class="text-center align-middle">-9%</td>
              <td class="text-center align-middle">+6%</td>
              <td class="text-center align-middle">+6%</td>
              <td class="text-center align-middle">+6%</td>
              <td class="text-center align-middle">+6%</td>
              <td class="text-center align-middle">+6%</td>
              <td class="text-center align-middle">+6%</td>
              <td class="text-center align-middle">+6%</td>
              <td class="text-center align-middle">+6%</td>
              <td class="text-center align-middle">+6%</td>
              <td class="text-center align-middle">+6%</td>
              <td class="text-center align-middle">+6%</td>
              <td class="text-center align-middle">+6%</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
    <section id="table-revenue-2023" class="table-responsive small w-100">
      <h4 class="py-3">
        <a class="text-decoration-none dropdown-toggle text-dark nav-link" data-bs-toggle="collapse" href="#collapseRevenue2023" role="button" aria-expanded="false" aria-controls="collapseRevenue2023">
          2023
        </a>
      </h4>
      <div class="collapse" id="collapseRevenue2023">
        <table class="table table-striped table-sm">
          <thead>
            <tr>
              <th scope="col" class="table-col-w4 text-center align-middle">2024</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Janv.</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Fev.</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Mars</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Avril</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Mai</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Juin</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Juill.</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Août</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Sept.</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Oct.</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Nov.</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Déc.</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Année</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td class="text-center align-middle fw-bold">CA</td>
              <td class="text-center align-middle">119000€</td>
              <td class="text-center align-middle">119000€</td>
              <td class="text-center align-middle">119000€</td>
              <td class="text-center align-middle">119000€</td>
              <td class="text-center align-middle">119000€</td>
              <td class="text-center align-middle">119000€</td>
              <td class="text-center align-middle">119000€</td>
              <td class="text-center align-middle">119000€</td>
              <td class="text-center align-middle">119000€</td>
              <td class="text-center align-middle">119000€</td>
              <td class="text-center align-middle">119000€</td>
              <td class="text-center align-middle">119000€</td>
              <td class="text-center align-middle">1119000€</td>
            </tr>
            <tr>
              <td class="text-center align-middle fw-bold">Delta n-1</td>
              <td class="text-center align-middle">-3000</td>
              <td class="text-center align-middle">+21500</td>
              <td class="text-center align-middle">+21500</td>
              <td class="text-center align-middle">+21500</td>
              <td class="text-center align-middle">+21500</td>
              <td class="text-center align-middle">+21500</td>
              <td class="text-center align-middle">+21500</td>
              <td class="text-center align-middle">+21500</td>
              <td class="text-center align-middle">+21500</td>
              <td class="text-center align-middle">+21500</td>
              <td class="text-center align-middle">+21500</td>
              <td class="text-center align-middle">+21500</td>
              <td class="text-center align-middle">+21500</td>
            </tr>
            <tr>
              <td class="text-center align-middle fw-bold"> Delta %</td>
              <td class="text-center align-middle">-9%</td>
              <td class="text-center align-middle">+6%</td>
              <td class="text-center align-middle">+6%</td>
              <td class="text-center align-middle">+6%</td>
              <td class="text-center align-middle">+6%</td>
              <td class="text-center align-middle">+6%</td>
              <td class="text-center align-middle">+6%</td>
              <td class="text-center align-middle">+6%</td>
              <td class="text-center align-middle">+6%</td>
              <td class="text-center align-middle">+6%</td>
              <td class="text-center align-middle">+6%</td>
              <td class="text-center align-middle">+6%</td>
              <td class="text-center align-middle">+6%</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
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

  </div>

  </div>
  <script src="../node_modules/bootstrap/dist/js/bootstrap.bundle.min.js"></script>
  <script src="assets/js/header.js"></script>
  <script src="assets/js/navbar.js"></script>
</body>

</html>