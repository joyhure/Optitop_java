<!DOCTYPE html>
<html lang="fr">

<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Synthèse</title>
  <meta name="author" content="Joy Huré">
  <link rel="icon" href="assets/images/favicon.png">
  <link rel="stylesheet" href="../node_modules/bootstrap/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="../node_modules/bootstrap-icons/font/bootstrap-icons.css">
  <link rel="stylesheet" href="assets/styles/custom.css">
</head>

<body>
  <?php
  $pageTitle = "Synthèse";
  require_once 'components/header.php';
  require_once 'components/navbar.php';
  ?>
  <main id="main-quotations" class="d-flex flex-row justify-content-between">
    <section id="table-quotations-section" class="table-responsive small">
      <div class="d-flex d-inline-flex my-4">
        <div class="d-inline-flex align-items-center justify-content-center fs-2">
          <svg class="bi svg-average-basket rounded" width="1em" height="1em">
            <use xlink:href="../node_modules/bootstrap-icons/bootstrap-icons.svg#file-earmark-spreadsheet"></use>
          </svg>
        </div>
        <h3 class="px-4 mb-0">Synthèse</h3>
      </div>
      <div class="container mt-5">
        <table class="table table-bordered table-striped">
          <thead>
            <tr>
              <th id="table-header" colspan="7">
                <h2 class="text-center fw-bold">Chiffres & Stats Mensuels</h2>
              </th>
            </tr>
            <tr>
              <th>Catégorie</th>
              <th>CA Encaissé</th>
              <th>CA Facturé</th>
              <th>Passages</th>
              <th>Pass %</th>
              <th>NPS</th>
              <th>Taux Concret</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>N</td>
              <td>68 381 €</td>
              <td>62 541 €</td>
              <td>194</td>
              <td>5,43%</td>
              <td>8,83</td>
              <td>80%</td>
            </tr>
            <tr>
              <td>N-1</td>
              <td>69 350 €</td>
              <td>59 450 €</td>
              <td>184</td>
              <td>-</td>
              <td>-</td>
              <td>-</td>
            </tr>
          </tbody>
        </table>

        <h2 class="text-center my-4">2ème Paire & Primes</h2>

        <table class="table table-bordered table-striped">
          <thead class="table-dark">
            <tr>
              <th>Nom</th>
              <th>Nombre</th>
              <th>PM</th>
              <th>Primes</th>
              <th>CA Mensuel</th>
              <th>Objectif Janv.</th>
              <th>Réalisé</th>
              <th>%</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>LG</td>
              <td>15</td>
              <td>37,5€</td>
              <td>50€</td>
              <td>15 236 €</td>
              <td>2 985 €</td>
              <td>20%</td>
            </tr>
            <tr>
              <td>LAM</td>
              <td>21</td>
              <td>7,7€</td>
              <td>0€</td>
              <td>6 135 €</td>
              <td>4 764 €</td>
              <td>78%</td>
            </tr>
            <tr>
              <td>GM</td>
              <td>15</td>
              <td>10,2€</td>
              <td>0€</td>
              <td>23 519 €</td>
              <td>3 888 €</td>
              <td>17%</td>
            </tr>
            <tr>
              <td>ID</td>
              <td>52</td>
              <td>14,6€</td>
              <td>40€</td>
              <td>26 586 €</td>
              <td>13 348 €</td>
              <td>50%</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
    <section id="summary" class="d-flex flex-column justify-content-center align-items-center gap-5">
      <div id="quotation" class="card px-1 py-3 w-100">
        <div class="feature-icon d-inline-flex align-items-center justify-content-center fs-2">
          <svg class="bi svg-feature rounded" width="1em" height="1em">
            <use xlink:href="../node_modules/bootstrap-icons/bootstrap-icons.svg#clipboard2"></use>
          </svg>
        </div>
        <h2 class="text-center pt-2">100%</h2>
        <p class="text-center mb-0">Non validés : [5]
        </p>
      </div>
      <div class="table-responsive card small w-100">
        <table class="table table-striped table-sm">
          <thead>
            <tr class="text-center">
              <th scope="col">Nom</th>
              <th scope="col">Non V.</th>
              <th scope="col">Taux</th>
            </tr>
          </thead>
          <tbody>
            <tr class="text-center">
              <td>BS</td>
              <td>[1]</td>
              <td>[%]</td>
            </tr>
            <tr class="text-center">
              <td>EG</td>
              <td>[2]</td>
              <td>[%]</td>
            </tr>
            <tr class="text-center">
              <td>IH</td>
              <td>[1]</td>
              <td>[%]</td>
            </tr>
            <tr class="text-center">
              <td>IR</td>
              <td>[1]</td>
              <td>[%]</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </main>

  </div>

  </div>
  <script src="../node_modules/bootstrap/dist/js/bootstrap.bundle.min.js"></script>
</body>

</html>