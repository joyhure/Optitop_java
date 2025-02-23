<!DOCTYPE html>
<html lang="fr">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mon compte</title>
    <meta name="author" content="Joy Huré">
    <link rel="icon" href="assets/images/favicon.png">
    <link rel="stylesheet" href="../node_modules/bootstrap/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="../node_modules/bootstrap-icons/font/bootstrap-icons.css">
    <link rel="stylesheet" href="assets/styles/custom.css">
</head>

<body>
    <?php
    $pageTitle = "Mon compte";
    require_once 'components/header.html';
    require_once 'components/navbar.php';
    ?>
    <main id="main" class="main d-flex justify-content-between m-5 w-75">
        <section class="section-card w-40">
            <div class="card w-100">
                <div class="card-body text-center">
                    <i class="bi bi-person-circle d-block mx-auto mb-3 fs-1"></i>
                    <h2 class="card-title">
                        <span id="profileFullname" data-testid="user-fullname"></span>
                    </h2>
                    <h3>
                        <span id="profileRole"></span>
                    </h3>
                    <h7>
                        <span id="profileDateCreate"></span>
                    </h7>
                </div>
            </div>
        </section>
        <section class="section-data w-50">

            <div class="card w-100">
                <div class="card-body pt-3">
                    <ul class="nav nav-tabs nav-tabs-bordered" role="tablist">
                        <li class="nav-item" role="presentation">
                            <button class="nav-link active" data-bs-toggle="tab" data-bs-target="#profile-overview" aria-selected="true" role="tab">Profil</button>
                        </li>

                        <li class="nav-item" role="presentation">
                            <button class="nav-link" data-bs-toggle="tab" data-bs-target="#profile-change-password" aria-selected="false" role="tab" tabindex="-1">Password</button>
                        </li>

                    </ul>
                    <div class="tab-content pt-2">

                        <div class="tab-pane fade profile-overview active show" id="profile-overview" role="tabpanel">

                            <h4 class="card-title my-4">Détails du Profil</h4>

                            <div class="row my-2">
                                <div class="col-lg-3 col-md-4 label w-40">Nom :</div>
                                <div class="col-lg-9 col-md-8 w-50" id="profileDetailName"></div>
                            </div>

                            <div class="row my-2">
                                <div class="col-lg-3 col-md-4 label w-40">Prénom :</div>
                                <div class="col-lg-9 col-md-8 w-50" id="profileDetailFirstname"></div>
                            </div>

                            <div class="row my-2">
                                <div class="col-lg-3 col-md-4 label w-40">Identifiant :</div>
                                <div class="col-lg-9 col-md-8 w-50" id="profileDetailLogin"></div>
                            </div>

                            <div class="row my-2">
                                <div class="col-lg-3 col-md-4 label w-40">Email :</div>
                                <div class="col-lg-9 col-md-8 w-50" id="profileDetailEmail"></div>
                            </div>

                            <div class="row my-2">
                                <div class="col-lg-3 col-md-4 label w-40">Création :</div>
                                <div class="col-lg-9 col-md-8 w-50" id="profileDetailCreatedAt"></div>
                            </div>

                        </div>

                        <div class="tab-pane fade pt-3" id="profile-change-password" role="tabpanel">
                            <form>

                                <div class="row mb-3">
                                    <label for="currentPassword" class="col-md-4 col-lg-3 col-form-label w-30">Actuel :</label>
                                    <div class="col-md-8 col-lg-9">
                                        <input name="password" type="password" class="form-control" id="currentPassword">
                                    </div>
                                </div>

                                <div class="row mb-3">
                                    <label for="newPassword" class="col-md-4 col-lg-3 col-form-label w-30">Nouveau :</label>
                                    <div class="col-md-8 col-lg-9">
                                        <input name="newpassword" type="password" class="form-control" id="newPassword">
                                    </div>
                                </div>

                                <div class="row mb-3">
                                    <label for="renewPassword" class="col-md-4 col-lg-3 col-form-label w-30">Confirmation :</label>
                                    <div class="col-md-8 col-lg-9">
                                        <input name="renewpassword" type="password" class="form-control" id="renewPassword">
                                    </div>
                                </div>

                                <div class="text-center">
                                    <button type="submit" class="btn btn-primary">Changer le mot de passe</button>
                                </div>
                            </form><!-- End Change Password Form -->

                        </div>

                    </div><!-- End Bordered Tabs -->

                </div>
            </div>


            </div>
        </section>

    </main>
    <script src="../node_modules/bootstrap/dist/js/bootstrap.bundle.min.js"></script>
    <script src="assets/js/header.js"></script>
    <script src="assets/js/navbar.js"></script>
    <script src="assets/js/user-account.js"></script>
</body>

</html>