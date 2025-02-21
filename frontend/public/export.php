<?php
session_start();

// Vérification PHP principale
if (!isset($_SESSION['user']) || 
    !isset($_SESSION['user']['role']) || 
    !in_array($_SESSION['user']['role'], ['admin', 'supermanager'])) {
    header('Location: dashboard.php');
    exit();
}
?>
<!DOCTYPE html>
<html lang="fr">

<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Exportation</title>
  <meta name="author" content="Joy Huré">
  <link rel="icon" href="assets/images/favicon.png">
  <link rel="stylesheet" href="../node_modules/bootstrap/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="../node_modules/bootstrap-icons/font/bootstrap-icons.css">
  <link rel="stylesheet" href="assets/styles/custom.css">
</head>

<body>
  <?php
  $pageTitle = "Exportation";
  require_once 'components/header.html';
  require_once 'components/navbar.php';
  ?>
  <main id="main-quotations" class="d-flex flex-row justify-content-between">
    <section id="table-quotations-section" class="table-responsive small">
      <div class="d-flex d-inline-flex my-4">
        <div class="d-inline-flex align-items-center justify-content-center fs-2">
          <svg class="bi svg-average-basket rounded" width="1em" height="1em">
            <use xlink:href="../node_modules/bootstrap-icons/bootstrap-icons.svg#file-earmark-arrow-up"></use>
          </svg>
        </div>
        <h3 class="px-4 mb-0">Exportation des données de vente</h3>
      </div>
      <form action="import_csv.php" method="post" enctype="multipart/form-data" class="m-5">
        <div class="mb-3">
          <label for="csvFile" class="form-label">Choisir un fichier CSV</label>
          <input type="file" class="form-control" id="csvFile" name="csvFile" accept=".csv" required>
        </div>
        <button type="submit" class="btn btn-primary">Importer</button>
      </form>
    </section>
  </main>

  </div>

  </div>
  <script src="../node_modules/bootstrap/dist/js/bootstrap.bundle.min.js"></script>
  <script src="assets/js/header.js"></script>
  <script src="assets/js/navbar.js"></script>
  
</body>

</html>