<?php

/**
 * Page de gestion du compte utilisateur
 * 
 * Permet aux utilisateurs connectés de :
 * - Consulter les informations de leur profil
 * - Modifier leur mot de passe personnel
 * - Visualiser leurs données de compte (création, rôle, etc.)
 * - Accéder aux paramètres de sécurité
 * 
 * Interface sécurisée de gestion du compte personnel
 * avec validation des mots de passe et mise à jour des données.
 */

// Démarrage de la session
session_start();

// ===== VÉRIFICATION DES DROITS D'ACCÈS =====

/**
 * Contrôle des permissions utilisateur
 * Tous les utilisateurs connectés peuvent accéder à leur compte.
 */
if (!isset($_SESSION['user'])) {
    header('Location: index.html');
    exit();
}
?>
<!DOCTYPE html>
<html lang="fr">

<head>
    <!-- ===== MÉTADONNÉES ===== -->
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Optitop - Mon Compte</title>
    <meta name="description" content="Gestion du compte utilisateur - Profil et paramètres personnels">
    <meta name="author" content="Joy Huré">

    <!-- ===== FAVICON ===== -->
    <link rel="icon" href="assets/images/favicon.png">

    <!-- ===== STYLES ===== -->
    <link rel="stylesheet" href="assets/vendor/bootstrap/bootstrap.min.css">
    <link rel="stylesheet" href="assets/vendor/bootstrap-icons/bootstrap-icons.css">
    <link rel="stylesheet" href="assets/styles/custom.css">
</head>

<body>
    <?php
    /**
     * Configuration de la page et inclusion des composants
     */
    $pageTitle = "Mon compte";
    require_once 'components/header.html';
    require_once 'components/navbar.php';
    ?>

    <!-- ===== CONTENU PRINCIPAL ===== -->
    <main id="main" class="main d-flex justify-content-between m-5 w-75">
        
        <!-- Section carte profil -->
        <section class="section-card w-40">
            <div class="card w-100">
                <div class="card-body text-center">
                    
                    <!-- Icône utilisateur -->
                    <i class="bi bi-person-circle d-block mx-auto mb-3 fs-1"></i>
                    
                    <!-- Nom complet -->
                    <h2 class="card-title">
                        <span id="profileFullname" data-testid="user-fullname"></span>
                    </h2>
                    
                    <!-- Rôle utilisateur -->
                    <h3>
                        <span id="profileRole"></span>
                    </h3>
                    
                    <!-- Date de création -->
                    <h7>
                        <span id="profileDateCreate"></span>
                    </h7>
                </div>
            </div>
        </section>

        <!-- Section données utilisateur -->
        <section class="section-data w-50">
            <div class="card w-100">
                <div class="card-body pt-3">
                    
                    <!-- Navigation par onglets -->
                    <ul class="nav nav-tabs nav-tabs-bordered" role="tablist">
                        
                        <!-- Onglet Profil -->
                        <li class="nav-item" role="presentation">
                            <button class="nav-link active" 
                                    data-bs-toggle="tab" 
                                    data-bs-target="#profile-overview" 
                                    aria-selected="true" 
                                    role="tab">
                                Profil
                            </button>
                        </li>

                        <!-- Onglet Mot de passe -->
                        <li class="nav-item" role="presentation">
                            <button class="nav-link" 
                                    data-bs-toggle="tab" 
                                    data-bs-target="#profile-change-password" 
                                    aria-selected="false" 
                                    role="tab" 
                                    tabindex="-1">
                                Password
                            </button>
                        </li>
                    </ul>

                    <!-- Contenu des onglets -->
                    <div class="tab-content pt-2">

                        <!-- Onglet Vue d'ensemble du profil -->
                        <div class="tab-pane fade profile-overview active show" 
                             id="profile-overview" 
                             role="tabpanel">

                            <h4 class="card-title my-4">Détails du Profil</h4>

                            <!-- Champ Nom -->
                            <div class="row my-2">
                                <div class="col-lg-3 col-md-4 label w-40">Nom :</div>
                                <div class="col-lg-9 col-md-8 w-50" id="profileDetailName"></div>
                            </div>

                            <!-- Champ Prénom -->
                            <div class="row my-2">
                                <div class="col-lg-3 col-md-4 label w-40">Prénom :</div>
                                <div class="col-lg-9 col-md-8 w-50" id="profileDetailFirstname"></div>
                            </div>

                            <!-- Champ Identifiant -->
                            <div class="row my-2">
                                <div class="col-lg-3 col-md-4 label w-40">Identifiant :</div>
                                <div class="col-lg-9 col-md-8 w-50" id="profileDetailLogin"></div>
                            </div>

                            <!-- Champ Email -->
                            <div class="row my-2">
                                <div class="col-lg-3 col-md-4 label w-40">Email :</div>
                                <div class="col-lg-9 col-md-8 w-50" id="profileDetailEmail"></div>
                            </div>

                            <!-- Champ Date de création -->
                            <div class="row my-2">
                                <div class="col-lg-3 col-md-4 label w-40">Création :</div>
                                <div class="col-lg-9 col-md-8 w-50" id="profileDetailCreatedAt"></div>
                            </div>
                        </div>

                        <!-- Onglet Changement de mot de passe -->
                        <div class="tab-pane fade pt-3" 
                             id="profile-change-password" 
                             role="tabpanel">
                            
                            <form id="passwordChangeForm" method="POST" novalidate>
                                
                                <!-- Champ mot de passe actuel -->
                                <div class="row mb-3">
                                    <label for="currentPassword" class="col-md-4 col-lg-3 col-form-label w-30">
                                        Actuel :
                                    </label>
                                    <div class="col-md-8 col-lg-9">
                                        <input name="password" 
                                               type="password" 
                                               class="form-control" 
                                               id="currentPassword" 
                                               required>
                                        <div class="invalid-feedback">
                                            Veuillez saisir votre mot de passe actuel
                                        </div>
                                    </div>
                                </div>

                                <!-- Champ nouveau mot de passe -->
                                <div class="row mb-3">
                                    <label for="newPassword" class="col-md-4 col-lg-3 col-form-label w-30">
                                        Nouveau :
                                    </label>
                                    <div class="col-md-8 col-lg-9">
                                        <input name="newpassword" 
                                               type="password" 
                                               class="form-control" 
                                               id="newPassword" 
                                               pattern="^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{12,}$" 
                                               required>
                                        <div class="invalid-feedback">
                                            Le mot de passe doit contenir au moins 12 caractères avec :
                                            <ul>
                                                <li>Une majuscule</li>
                                                <li>Une minuscule</li>
                                                <li>Un chiffre</li>
                                                <li>Un caractère spécial (@$!%*?&)</li>
                                            </ul>
                                        </div>
                                    </div>
                                </div>

                                <!-- Champ confirmation mot de passe -->
                                <div class="row mb-3">
                                    <label for="renewPassword" class="col-md-4 col-lg-3 col-form-label w-30">
                                        Confirmation :
                                    </label>
                                    <div class="col-md-8 col-lg-9">
                                        <input name="renewpassword" 
                                               type="password" 
                                               class="form-control" 
                                               id="renewPassword" 
                                               required>
                                        <div class="invalid-feedback">
                                            Les mots de passe ne correspondent pas
                                        </div>
                                    </div>
                                </div>

                                <!-- Bouton de soumission -->
                                <div class="text-center">
                                    <button type="submit" class="btn btn-primary">
                                        Changer le mot de passe
                                    </button>
                                </div>

                                <!-- Zone de résultat -->
                                <div id="passwordChangeResult" class="alert mt-3 d-none"></div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </main>

    <!-- ===== SCRIPTS ===== -->
    <script src="assets/vendor/bootstrap/bootstrap.bundle.min.js"></script>
    <script src="assets/js/header.js"></script>
    <script src="assets/js/navbar.js"></script>
    <script src="assets/js/user-account.js"></script>
</body>

</html>