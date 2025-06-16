<?php
/**
 * Composant de navigation principale d'Optitop
 * Affiche le titre de page et la barre de navigation latérale
 */
?>

<!-- En-tête de section avec titre -->
<section id="titles" class="d-flex align-items-end py-3 border-bottom">
    <div id="title-container" class="px-4">
        <h2 id="page-title" class="mb-0 text-blue"><?php echo htmlspecialchars($pageTitle, ENT_QUOTES, 'UTF-8'); ?></h2>
    </div>
    <h6 id="lastUpdate" class="ms-auto mb-0">Mis à jour le ...</h6>
</section>

<!-- Conteneur principal avec navigation -->
<div class="d-flex flex-row justify-content-between">
    
    <!-- Navigation latérale -->
    <nav class="d-flex flex-column flex-shrink-0 sidebar">
        <ul class="nav nav-pills flex-column bg-body-tertiary">
        </ul>
        <hr class="m-0">
    </nav>

