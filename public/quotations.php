<!DOCTYPE html>
<html lang="fr">

<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Devis</title>
  <meta name="author" content="Joy Huré">
  <link rel="icon" href="assets/images/favicon.png">
  <link rel="stylesheet" href="../node_modules/bootstrap/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="../node_modules/bootstrap-icons/font/bootstrap-icons.css">
  <link rel="stylesheet" href="assets/styles/custom.css">
</head>

<body>
  <?php
  $pageTitle = "Devis Optiques";
  require_once 'components/header.php';
  require_once 'components/navbar.php';
  ?>
  <main id="main-quotations" class="d-flex flex-row justify-content-between">
    <section id="table-quotations-section" class="table-responsive small">
      <div class="d-flex d-inline-flex my-4">
        <div class="d-inline-flex align-items-center justify-content-center fs-2">
          <svg class="bi svg-unvalidated-quotations rounded" width="1em" height="1em">
            <use xlink:href="../node_modules/bootstrap-icons/bootstrap-icons.svg#clipboard2-x"></use>
          </svg>
        </div>
        <h3 class="px-4 mb-0">Devis Non Validés</h3>
      </div>
      <table class="table table-striped table-sm">
        <thead>
          <tr>
            <th scope="col" class="table-col-date">Date</th>
            <th scope="col" class="table-col-w4">Nom</th>
            <th scope="col" class="table-col-client">Client</th>
            <th scope="col" class="table-col-w15">Statut</th>
            <th scope="col" class="table-col-commentaire">Commentaire</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>dd/mm/yyyy</td>
            <td class="text-center">IR</td>
            <td>[NOM PRENOM]</td>
            <td>Voir opticien</td>
            <td>Lorem ipsum dolor, sit amet consectetur adipisicing elit. Itaque rerum vel consectetur numquam consequuntur minima quisquam saepe, eum aliquid et odit ut inventore ratione laborum necessitatibus excepturi dolorum repudiandae. Obcaecati?</td>
          </tr>
          <tr>
            <td>dd/mm/yyyy</td>
            <td class="text-center">IH</td>
            <td>[NOM PRENOM]</td>
            <td>Attente de retour</td>
            <td>Lorem ipsum dolor, sit amet consectetur adipisicing elit. Itaque rerum vel consectetur numquam consequuntur minima quisquam saepe, eum aliquid et odit ut inventore ratione laborum necessitatibus excepturi dolorum repudiandae. Obcaecati?</td>
          </tr>
          <tr>
            <td>dd/mm/yyyy</td>
            <td class="text-center">BD</td>
            <td>[NOM PRENOM]</td>
            <td>Attente Mutuelle</td>
            <td>Lorem ipsum dolor, sit amet consectetur adipisicing elit. Itaque rerum vel consectetur numquam consequuntur minima quisquam saepe, eum aliquid et odit ut inventore ratione laborum necessitatibus excepturi dolorum repudiandae. Obcaecati?</td>
          </tr>
          <tr>
            <td>dd/mm/yyyy</td>
            <td class="text-center">EG</td>
            <td>[NOM PRENOM]</td>
            <td>Non Validé</td>
            <td>Lorem ipsum dolor, sit amet consectetur adipisicing elit. Itaque rerum vel consectetur numquam consequuntur minima quisquam saepe, eum aliquid et odit ut inventore ratione laborum necessitatibus excepturi dolorum repudiandae. Obcaecati?</td>
          </tr>
          <tr>
            <td>dd/mm/yyyy</td>
            <td class="text-center">EG</td>
            <td>[NOM PRENOM]</td>
            <td>A Relancer</td>
            <td>Lorem ipsum dolor, sit amet consectetur adipisicing elit. Itaque rerum vel consectetur numquam consequuntur minima quisquam saepe, eum aliquid et odit ut inventore ratione laborum necessitatibus excepturi dolorum repudiandae. Obcaecati?</td>
          </tr>
          <tr>
            <td>dd/mm/yyyy</td>
            <td class="text-center">IR</td>
            <td>[NOM PRENOM]</td>
            <td>Voir opticien</td>
            <td>Lorem ipsum dolor, sit amet consectetur adipisicing elit. Itaque rerum vel consectetur numquam consequuntur minima quisquam saepe, eum aliquid et odit ut inventore ratione laborum necessitatibus excepturi dolorum repudiandae. Obcaecati?</td>
          </tr>
          <tr>
            <td>dd/mm/yyyy</td>
            <td class="text-center">IH</td>
            <td>[NOM PRENOM]</td>
            <td>Attente de retour</td>
            <td>Lorem ipsum dolor, sit amet consectetur adipisicing elit. Itaque rerum vel consectetur numquam consequuntur minima quisquam saepe, eum aliquid et odit ut inventore ratione laborum necessitatibus excepturi dolorum repudiandae. Obcaecati?</td>
          </tr>
          <tr>
            <td>dd/mm/yyyy</td>
            <td class="text-center">BD</td>
            <td>[NOM PRENOM]</td>
            <td>Attente Mutuelle</td>
            <td>Lorem ipsum dolor, sit amet consectetur adipisicing elit. Itaque rerum vel consectetur numquam consequuntur minima quisquam saepe, eum aliquid et odit ut inventore ratione laborum necessitatibus excepturi dolorum repudiandae. Obcaecati?</td>
          </tr>
          <tr>
            <td>dd/mm/yyyy</td>
            <td class="text-center">EG</td>
            <td>[NOM PRENOM]</td>
            <td>Non Validé</td>
            <td>Lorem ipsum dolor, sit amet consectetur adipisicing elit. Itaque rerum vel consectetur numquam consequuntur minima quisquam saepe, eum aliquid et odit ut inventore ratione laborum necessitatibus excepturi dolorum repudiandae. Obcaecati?</td>
          </tr>
          <tr>
            <td>dd/mm/yyyy</td>
            <td class="text-center">EG</td>
            <td>[NOM PRENOM]</td>
            <td>A Relancer</td>
            <td>Lorem ipsum dolor, sit amet consectetur adipisicing elit. Itaque rerum vel consectetur numquam consequuntur minima quisquam saepe, eum aliquid et odit ut inventore ratione laborum necessitatibus excepturi dolorum repudiandae. Obcaecati?</td>
          </tr>
        </tbody>
      </table>
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