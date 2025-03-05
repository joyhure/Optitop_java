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
  require_once 'components/header.html';
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
            <th scope="col" class="table-col-date">
              <div class="d-flex align-items-center">
                Date
                <div class="ms-2 sort-icons">
                  <svg class="bi sort-icon" width="1em" height="1em" data-sort="date" data-order="asc">
                    <use xlink:href="../node_modules/bootstrap-icons/bootstrap-icons.svg#sort-down"></use>
                  </svg>
                </div>
              </div>
            </th>
            <th scope="col" class="table-col-w4">
              <div class="d-flex align-items-center">
                Nom
                <div class="ms-2 sort-icons">
                  <svg class="bi sort-icon" width="1em" height="1em" data-sort="name" data-order="asc">
                    <use xlink:href="../node_modules/bootstrap-icons/bootstrap-icons.svg#sort-down"></use>
                  </svg>
                </div>
              </div>
            </th>
            <th scope="col" class="table-col-w20">Client</th>
            <th scope="col" class="table-col-w20">Action</th>
            <th scope="col" class="table-col-commentaire">Commentaire</th>
          </tr>
        </thead>
        <tbody>
          <!-- Les données seront injectées ici par JavaScript -->
        </tbody>
      </table>

      <div id="save-button-container" class="mt-3">
        <button id="save-changes-button" class="btn row-blueperso" disabled>
            Enregistrer
        </button>
      </div>
    </section>
    <section id="summary" 
             class="d-none d-flex flex-column justify-content-center align-items-center gap-3" 
             data-requires-role="admin,manager,supermanager">
      <div id="quotation" class="card px-1 py-2 w-100">
        <div class="feature-icon d-inline-flex align-items-center justify-content-center fs-2">
          <svg class="bi svg-feature rounded" width="1em" height="1em">
            <use xlink:href="../node_modules/bootstrap-icons/bootstrap-icons.svg#clipboard2"></use>
          </svg>
        </div>
        <h2 id="store-concretization-rate" class="text-center pt-2"></h2>
        <p id="quotations-numbers" class="text-center mb-0"><br>
         
        </p>
      </div>
      <div class="table-responsive card small w-100">
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
            <!-- Les données seront injectées ici par JavaScript -->
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
  <script src="assets/js/quotations.js"></script>

  <div class="toast-container position-fixed top-0 end-0 p-3">
    <div id="successToast" class="toast align-items-center text-bg-success" role="alert" aria-live="assertive" aria-atomic="true">
        <div class="d-flex">
            <div class="toast-body">
                Modifications enregistrées avec succès
            </div>
            <button type="button" class="btn-close me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
        </div>
    </div>

    <div id="errorToast" class="toast align-items-center text-bg-danger" role="alert" aria-live="assertive" aria-atomic="true">
        <div class="d-flex">
            <div class="toast-body">
                Erreur lors de l'enregistrement des modifications
            </div>
            <button type="button" class="btn-close me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
        </div>
    </div>
  </div>
</body>

</html>