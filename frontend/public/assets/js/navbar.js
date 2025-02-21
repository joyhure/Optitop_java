document.addEventListener('DOMContentLoaded', function() {
    // Récupérer le titre de la page actuelle
    const currentPage = document.getElementById('page-title').textContent;
    
    // Structure de navigation
    const navItems = [
        { title: "Dashboard", href: "dashboard.php" },
        { title: "Devis Optiques", href: "quotations.php" },
        { title: "Paniers Moyens & Primes", href: "average-basket.php", requiresRole: ['admin', 'supermanager', 'manager'] },
        { title: "CA", href: "revenue.php", requiresRole: ['admin', 'supermanager', 'manager'] },
        { title: "Synthèse", href: "summary.php", requiresRole: ['admin', 'supermanager', 'manager'] },
        { title: "Exportation", href: "export.php", requiresRole: ['admin', 'supermanager'] },
        { title: "Comptes", href: "accounts.php", requiresRole: ['admin'] }
    ];

    // Récupérer l'élément ul de navigation
    const navList = document.querySelector('.nav.nav-pills');
    navList.innerHTML = ''; // Nettoyer la navigation existante

    // Récupérer la session utilisateur
    const userSession = JSON.parse(sessionStorage.getItem('user'));

    // Générer les liens de navigation
    navItems.forEach(item => {
        // Skip si c'est la page courante
        if (item.title === currentPage) return;

        // Skip si l'item requiert un rôle spécifique et l'utilisateur n'a pas les droits
        if (item.requiresRole && (!userSession || 
            !userSession.role || 
            !item.requiresRole.includes(userSession.role.toLowerCase()))) {
            return;
        }

        // Créer l'élément de navigation
        const li = document.createElement('li');
        li.innerHTML = `
            <a href="${item.href}" class="nav-link link-body-emphasis px-4">
                ${item.title}
            </a>
        `;
        navList.appendChild(li);
    });
});