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

<body class="mt-10">
  <main class="form-signin w-25 m-auto">
    <form class="d-flex justify-content-center flex-column" autocomplete="off" onsubmit="handleLogin(event)">
      <div class="d-flex justify-content-center">
        <img class="mb-4" src="assets/images/logo_optitop.png" alt="Optitop logo" width="300" height="128">
      </div>
      <h1 class="h3 mb-3 fw-normal text-center">Connexion</h1>

      <div class="form-floating mb-3">
        <input type="text" class="form-control" id="login" name="login" placeholder="Identifiant" autocomplete="off" required>
        <label for="login">Identifiant</label>
      </div>

      <div class="form-floating mb-3">
        <input type="password" class="form-control" id="password" name="password" placeholder="Mot de Passe" autocomplete="off" required>
        <label for="password">Mot de Passe</label>
      </div>

      <div class="text-start mb-4">
        <a href="forgot-password.html" class="text-muted small">Mot de passe oublié ?</a>
      </div>
      <button class="btn btn-primary w-100 py-2" type="submit">Se connecter</button>
    </form>
  </main>
  <script src="../node_modules/bootstrap/dist/js/bootstrap.bundle.min.js"></script>
  <script src="assets/js/login.js"></script>
</body>

</html>