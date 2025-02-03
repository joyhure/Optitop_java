<section id="titles" class="d-flex align-items-end py-3 border-bottom">
    <div id="title-container" class="px-4">
        <h2 class="mb-0 text-blue"><?php echo htmlspecialchars($pageTitle); ?></h2>
    </div>
    <h6 class="ms-auto mb-0">Mis à Jour le [dd/mm/yyyy] à [hh:mm]</h6>
</section>
<div class="d-flex flex-row justify-content-between">
    <nav class="d-flex flex-column flex-shrink-0 sidebar">
        <ul class="nav nav-pills flex-column bg-body-tertiary rounded">
            <?php if ($pageTitle !== "Dashboard") : ?>
                <li>
                    <a href="dashboard.php" class="nav-link link-body-emphasis px-4">
                        Dashboard
                    </a>
                </li>
            <?php endif; ?>
            <?php if ($pageTitle !== "Devis Optiques") : ?>
                <li>
                    <a href="quotations.php" class="nav-link link-body-emphasis px-4">
                        Devis Optiques
                    </a>
                </li>
            <?php endif; ?>
            <?php if ($pageTitle !== "Paniers Moyens") : ?>
                <li>
                    <a href="average-basket.php" class="nav-link link-body-emphasis px-4">
                        Paniers Moyens
                    </a>
                </li>
            <?php endif; ?>
            <?php if ($pageTitle !== "Chiffre d'Affaires") : ?>
                <li>
                    <a href="revenue.php" class="nav-link link-body-emphasis px-4">
                        CA
                    </a>
                </li>
            <?php endif; ?>
            <?php if ($pageTitle !== "Synthèse") : ?>
                <li>
                    <a href="summary.php" class="nav-link link-body-emphasis px-4">
                        Synthèse
                    </a>
                </li>
            <?php endif; ?>
            <?php if ($pageTitle !== "Exportation") : ?>
                <li>
                    <a href="export.php" class="nav-link link-body-emphasis px-4">
                        Exportation
                    </a>
                </li>
            <?php endif; ?>
            <?php if ($pageTitle !== "Comptes") : ?>
                <li>
                    <a href="accounts.php" class="nav-link link-body-emphasis px-4">
                        Comptes
                    </a>
                </li>
            <?php endif; ?>
        </ul>
        <hr class="m-0">
    </nav>