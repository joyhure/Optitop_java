<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    header('Location: Dashboard.html');
    exit();
}
?>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Optitop</title>
    <meta name="author" content="Joy Huré">
    <link rel="icon" href="assets/images/favicon.png">
    <link rel="stylesheet" href="../node_modules/bootstrap/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="../node_modules/bootstrap-icons/font/bootstrap-icons.css">
    <link rel="stylesheet" href="assets/styles/custom.css">
    
  </head>
  </head>
<body class="mt-10">
  <main class="form-signin w-25 m-auto">
    <form class="d-flex justify-content-center flex-column" method="POST" action="index.php">
      <div class="d-flex justify-content-center">
        <img class="mb-4" src="assets/images/logo_optitop.png" alt="" width="300" height="128">
      </div>
      <h1 class="h3 mb-3 fw-normal text-center">Connexion</h1>
  
      <div class="form-floating mb-1">
        <input type="identifiant" class="form-control" id="floatingInput" placeholder="Identifiant">
        <label for="floatingInput">Identifiant</label>
      </div>
      <div class="form-floating">
        <input type="password" class="form-control" id="floatingPassword" placeholder="Password">
        <label for="floatingPassword">Password</label>
      </div>
  
      <div class="text-start mb-4">
        <a href="forgot-password.html" class="text-muted small">Mot de passe oublié ?</a>
      </div>
      </div>
      <button class="btn btn-primary w-100 py-2" type="submit">Se Connecter</button>
    </form>
  </main>
  <script src="../node_modules/bootstrap/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>