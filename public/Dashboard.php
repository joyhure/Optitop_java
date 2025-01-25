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
  <?php require_once 'components/header.php'; ?>
  <div id="page-container" class="container px-5">
    <?php require_once 'components/titles.php'; ?>
    <div class="d-flex flex-row justify-content-between g-3">
      <?php require_once 'components/navbar.php'; ?>
      <main class="main-content d-flex justify-content-around flex-wrap my-3">
        <section id="personal-section" class="col w-25">
          <div class="d-flex flex-column justify-content-center align-items-center mb-3">
            <svg class="bi d-block mx-auto mb-1" width="30" height="30">
              <use xlink:href="../node_modules/bootstrap-icons/bootstrap-icons.svg#person-circle"></use>
            </svg>
            <h4>[firstName]</h4>
          </div>
          <div id="first-line" class="flex-row d-flex justify-content-between align-items-center">
            <div id="ca" class="card my-3 px-1 pt-1">
              <div class="feature-icon d-inline-flex align-items-center justify-content-center fs-2">
                <svg class="bi svg-feature rounded" width="1em" height="1em">
                  <use xlink:href="../node_modules/bootstrap-icons/bootstrap-icons.svg#currency-euro"></use>
                </svg>
              </div>
              <p>CA : €<br>
              Nombre de factures : <br>
              <br>
              <a href="#" class="icon-link">
                Accès aux détails
                <svg class="bi">
                  <use xlink:href="../node_modules/bootstrap-icons/bootstrap-icons.svg#chevron-right"></use>
                </svg>
              </a>
              </p>
            </div>

            <div id="quotation" class="card my-3 px-1 pt-1">
              <div class="feature-icon d-inline-flex align-items-center justify-content-center fs-2">
                <svg class="bi svg-feature rounded" width="1em" height="1em">
                  <use xlink:href="../node_modules/bootstrap-icons/bootstrap-icons.svg#clipboard2"></use>
                </svg>
              </div>
              <p>Concrétisation : %<br>
              Devis non validés : <br>
              Devis validés : <br>
              <a href="#" class="icon-link">
                Accès aux détails
                <svg class="bi">
                  <use xlink:href="../node_modules/bootstrap-icons/bootstrap-icons.svg#chevron-right"></use>
                </svg>
              </a>
              </p>
            </div>
          </div>
          <div id="second-line" class="flex-row d-flex justify-content-between align-items-center">
            <div id="average-basket" class="card my-3 px-1 pt-1">
              <div class="feature-icon d-inline-flex align-items-center justify-content-center fs-2">
                <svg class="bi svg-feature rounded" width="1em" height="1em">
                  <use xlink:href="../node_modules/bootstrap-icons/bootstrap-icons.svg#cart4"></use>
                </svg>
              </div>
              <p>Panier moy optique : €<br>
              Nombre de factures : <br>
              <br>
              <a href="#" class="icon-link">
                Accès aux détails
                <svg class="bi">
                  <use xlink:href="../node_modules/bootstrap-icons/bootstrap-icons.svg#chevron-right"></use>
                </svg>
              </a>
              </p>
            </div>
            <div id="bonus" class="card my-3 px-1 pt-1">
              <div class="feature-icon d-inline-flex align-items-center justify-content-center fs-2">
                <svg class="bi svg-feature rounded" width="1em" height="1em">
                  <use xlink:href="../node_modules/bootstrap-icons/bootstrap-icons.svg#piggy-bank"></use>
                </svg>
              </div>
              <p>Montures primées : %<br>
              Prime : €<br>
              Nombre : <br>
              <br>
              </p>
            </div>
          </div>
        </section>

        <section id="shop-selection" class="feature col w-25">
          <div class="d-flex flex-column justify-content-center align-items-center mb-3">
            <svg class="bi d-block mx-auto mb-1" width="30" height="30">
              <use xlink:href="../node_modules/bootstrap-icons/bootstrap-icons.svg#building"></use>
            </svg>
            <h4>Magasin</h4>
          </div>
          <div id="first-line" class="flex-row d-flex justify-content-between align-items-center">
            <div id="ca" class="card my-3 px-1 pt-1">
              <div class="feature-icon d-inline-flex align-items-center justify-content-center fs-2">
                <svg class="bi svg-feature rounded" width="1em" height="1em">
                  <use xlink:href="../node_modules/bootstrap-icons/bootstrap-icons.svg#currency-euro"></use>
                </svg>
              </div>
              <p>CA : €<br>
              Nombre de factures : <br>
              <br>
              <a href="#" class="icon-link">
                Accès aux détails
                <svg class="bi">
                  <use xlink:href="../node_modules/bootstrap-icons/bootstrap-icons.svg#chevron-right"></use>
                </svg>
              </a>
              </p>
            </div>

            <div id="quotation" class="card my-3 px-1 pt-1">
              <div class="feature-icon d-inline-flex align-items-center justify-content-center fs-2">
                <svg class="bi svg-feature rounded" width="1em" height="1em">
                  <use xlink:href="../node_modules/bootstrap-icons/bootstrap-icons.svg#clipboard2"></use>
                </svg>
              </div>
              <p>Concrétisation : %<br>
              Devis non validés : <br>
              Devis validés : <br>
              <a href="#" class="icon-link">
                Accès aux détails
                <svg class="bi">
                  <use xlink:href="../node_modules/bootstrap-icons/bootstrap-icons.svg#chevron-right"></use>
                </svg>
              </a>
              </p>
            </div>
          </div>
          <div id="second-line" class="flex-row d-flex justify-content-between align-items-center">
            <div id="average-basket" class="card my-3 px-1 pt-1">
              <div class="feature-icon d-inline-flex align-items-center justify-content-center fs-2">
                <svg class="bi svg-feature rounded" width="1em" height="1em">
                  <use xlink:href="../node_modules/bootstrap-icons/bootstrap-icons.svg#cart4"></use>
                </svg>
              </div>
              <p>Panier moy optique : €<br>
              Nombre de factures : <br>
              <br>
              <a href="#" class="icon-link">
                Accès aux détails
                <svg class="bi">
                  <use xlink:href="../node_modules/bootstrap-icons/bootstrap-icons.svg#chevron-right"></use>
                </svg>
              </a>
              </p>
            </div>
            <div id="bonus" class="card my-3 px-1 pt-1">
              <div class="feature-icon d-inline-flex align-items-center justify-content-center fs-2">
                <svg class="bi svg-feature rounded" width="1em" height="1em">
                  <use xlink:href="../node_modules/bootstrap-icons/bootstrap-icons.svg#piggy-bank"></use>
                </svg>
              </div>
              <p>Montures primées : %<br>
              Prime : €<br>
              Nombre : <br>
              <br>
              </p>
            </div>
          </div>
        </section>
      </main>
    </div>

  </div>
  <script src="../node_modules/bootstrap/dist/js/bootstrap.bundle.min.js"></script>
</body>

</html>