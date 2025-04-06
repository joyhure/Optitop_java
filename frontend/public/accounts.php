<!DOCTYPE html>
<html lang="fr">

<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Comptes</title>
  <meta name="author" content="Joy Huré">
  <link rel="icon" href="assets/images/favicon.png">
  <link rel="stylesheet" href="../node_modules/bootstrap/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="../node_modules/bootstrap-icons/font/bootstrap-icons.css">
  <link rel="stylesheet" href="assets/styles/custom.css">
</head>

<body>
  <?php
  $pageTitle = "Comptes";
  require_once 'components/header.html';
  require_once 'components/navbar.php';
  ?>
  <main id="main-revenue" class="d-flex flex-column justify-content-between">
    <section id="table-accounts-ask" class="table-responsive small w-100">
      <div class="d-flex d-inline-flex my-4">
        <div class="d-inline-flex align-items-center justify-content-center fs-2">
          <svg class="bi svg-average-basket rounded" width="1em" height="1em">
            <use xlink:href="../node_modules/bootstrap-icons/bootstrap-icons.svg#people"></use>
          </svg>
        </div>
        <h3 class="px-4 mb-0">Comptes Utilisateurs</h3>
      </div>
      <h4 class="py-3">
        <a class="text-decoration-none dropdown-toggle text-dark nav-link" data-bs-toggle="collapse" href="#collapse-user-creation" role="button" aria-expanded="false" aria-controls="collapse-asks">
          Mes Demandes
        </a>
      </h4>
      <div class="d-flex justify-content-start mb-3">
        <button class="btn btn-primary" onclick="showNewRequestForm()">
          <i class="bi bi-plus-circle me-2"></i>Nouvelle demande
        </button>
      </div>
    
      <div class="collapse" id="collapse-user-creation">
        <div id="new-request-form" class="mb-3" style="display: none;">
          <table class="table table-sm">
            <tbody>
              <tr>
                <td class="text-center align-middle"><input type="text" class="form-control form-control-sm" placeholder="Nom"></td>
                <td class="text-center align-middle"><input type="text" class="form-control form-control-sm" placeholder="Prénom"></td>
                <td class="text-center align-middle"><input type="text" class="form-control form-control-sm" placeholder="Login"></td>
                <td class="text-center align-middle">
                  <select class="form-select form-select-sm">
                    <option value="">Sélectionner un rôle</option>
                    <option value="collaborateur">Collaborateur</option>
                    <option value="admin">Administrateur</option>
                  </select>
                </td>
                <td class="text-center align-middle"><input type="email" class="form-control form-control-sm" placeholder="Email"></td>
                <td class="text-center align-middle"><input type="text" class="form-control form-control-sm" placeholder="Observations"></td>
                <td class="text-center align-middle">
                  <button class="btn btn-success btn-sm me-1" onclick="submitRequest()">Envoyer</button>
                  <button class="btn btn-secondary btn-sm" onclick="cancelRequest()">Annuler</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
    
        <!-- Table existante -->
        <table id="accounts-ask-logs" class="table table-striped table-sm">
          <thead>
            <tr>
              <th scope="col" class="table-col-w4 text-center align-middle">Date</th>
              <th scope="col" class="table-col-w4 text-center align-middle">Initiateur</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Nom</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Prénom</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Login</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Rôle</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Email</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Observations</th>
              <th scope="col" class="table-col-w4 text-center align-middle">Action</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td class="text-center align-middle fw-bold">01/07/2024</td>
              <td class="text-center align-middle">mdupanloup</td>
              <td class="text-center align-middle">Haterisk</td>
              <td class="text-center align-middle">Jack</td>
              <td class="text-center align-middle">jhaterisk</td>
              <td class="text-center align-middle">collaborateur</td>
              <td class="text-center align-middle">jhaterisk@hotmail.com</td>
              <td class="text-center align-middle">Urgent SVP !!!!</td>
              <td class="text-center align-middle">
                <button class="btn btn-success py-1 mb-1" onclick="toggleAction(this)">Valider</button>
                <button class="btn btn-danger py-1" onclick="toggleAction(this)">Refuser</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      
    </section>
    <section id="table-user-actions" class="table-responsive small w-100">
    <h4 class="py-3">
        <a class="text-decoration-none dropdown-toggle text-dark nav-link" data-bs-toggle="collapse" href="#collapse-gestion" role="button" aria-expanded="false" aria-controls="collapse-gestion">
          Gestion des Demandes
        </a>
      </h4>
      <div class="collapse show" id="collapse-gestion">
        <table class="table table-striped table-sm">
          <thead>
            <tr>
              <th scope="col" class="table-col-w4 text-center align-middle">Date</th>
              <th scope="col" class="table-col-w4 text-center align-middle">Initiateur</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Nom</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Prénom</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Login</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Rôle</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Email</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Observations</th>
              <th scope="col" class="table-col-w4 text-center align-middle">Action</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td class="text-center align-middle fw-bold">01/07/2024</td>
              <td class="text-center align-middle">mdupanloup</td>
              <td class="text-center align-middle">Haterisk</td>
              <td class="text-center align-middle">Jack</td>
              <td class="text-center align-middle">jhaterisk</td>
              <td class="text-center align-middle">collaborateur</td>
              <td class="text-center align-middle">jhaterisk@hotmail.com</td>
              <td class="text-center align-middle">Urgent SVP !!!!</td>
              <td class="text-center align-middle">
                <button class="btn btn-success py-1 mb-1" onclick="toggleAction(this)">Valider</button>
                <button class="btn btn-danger py-1" onclick="toggleAction(this)">Refuser</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      
    </section>
    <section id="table-user" class="table-responsive small w-100">
      <h4 class="py-3">
        <a class="text-decoration-none dropdown-toggle text-dark nav-link" data-bs-toggle="collapse" href="#collapse-user" role="button" aria-expanded="false" aria-controls="collapseRevenue2023">
          Comptes Utilisateurs Existants
        </a>
      </h4>
      <div class="collapse" id="collapse-user">
        <table class="table table-striped table-sm">
          <thead>
            <tr>
              <th scope="col" class="table-col-w8 text-center align-middle">Date Validation</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Nom</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Prénom</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Login</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Rôle</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Email</th>
              <th scope="col" class="table-col-w8 text-center align-middle">Action</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td class="text-center align-middle fw-bold">01/07/2024</td>
              <td class="text-center align-middle">Haterisk</td>
              <td class="text-center align-middle">Jack</td>
              <td class="text-center align-middle">jhaterisk</td>
              <td class="text-center align-middle">collaborateur</td>
              <td class="text-center align-middle">jhaterisk@hotmail.com</td>
              <td class="text-center align-middle">
                <button class="btn btn-danger" onclick="toggleAction(this)">Supprimer</button>
              </td>
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