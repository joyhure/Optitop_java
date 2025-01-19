<!DOCTYPE html>
<html lang="fr">

<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Dashboard</title>
  <meta name="author" content="Joy Huré">
  <link rel="icon" href="assets/images/favicon.png">
  <link rel="stylesheet" href="../node_modules/bootstrap/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="../node_modules/bootstrap-icons/font/bootstrap-icons.css">
  <link rel="stylesheet" href="assets/styles/custom.css">
</head>

<body>
  <?php include 'components/header.php'; ?>
  <main class="container">
  <div class="d-flex flex-column flex-shrink-0 my-5 sidebar">
    
    <hr>
    <ul class="nav nav-pills flex-column mb-auto bg-body-tertiary rounded">
      <li class="nav-item">
        <a href="#" class="nav-link active px-1" aria-current="page">
          <svg class="bi pe-none me-2" width="16" height="16"><use xlink:href="#home"></use></svg>
          Dashboard
        </a>
      </li>
      <li>
        <a href="#" class="nav-link link-body-emphasis px-1">
          <svg class="bi pe-none me-2" width="16" height="16"><use xlink:href="#speedometer2"></use></svg>
          Devis Optiques
        </a>
      </li>
      <li>
        <a href="#" class="nav-link link-body-emphasis px-1">
          <svg class="bi pe-none me-2" width="16" height="16"><use xlink:href="#table"></use></svg>
          Primes Montures
        </a>
      </li>
      <li>
        <a href="#" class="nav-link link-body-emphasis px-1">
          <svg class="bi pe-none me-2" width="16" height="16"><use xlink:href="#grid"></use></svg>
          Paniers Moyens
        </a>
      </li>
      <li>
        <a href="#" class="nav-link link-body-emphasis px-1">
          <svg class="bi pe-none me-2" width="16" height="16"><use xlink:href="#people-circle"></use></svg>
          CA
        </a>
      </li>
      <li>
        <a href="#" class="nav-link link-body-emphasis px-1">
          <svg class="bi pe-none me-2" width="16" height="16"><use xlink:href="#people-circle"></use></svg>
          Synthèse
        </a>
      </li>
      <li>
        <a href="#" class="nav-link link-body-emphasis px-1">
          <svg class="bi pe-none me-2" width="16" height="16"><use xlink:href="#people-circle"></use></svg>
          Importation
        </a>
      </li>
      <li>
        <a href="#" class="nav-link link-body-emphasis px-1">
          <svg class="bi pe-none me-2" width="16" height="16"><use xlink:href="#people-circle"></use></svg>
          Comptes
        </a>
      </li>
    </ul>
    <hr>
  </div>
</main>
  <script src="../node_modules/bootstrap/dist/js/bootstrap.bundle.min.js"></script>
</body>

</html>