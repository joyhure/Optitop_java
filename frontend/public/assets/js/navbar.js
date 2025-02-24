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

    // Obtenir le nom du fichier actuel
    const currentFile = window.location.pathname.split('/').pop();

    // Générer les liens de navigation
    navItems.forEach(item => {
        // Skip si l'item requiert un rôle spécifique et l'utilisateur n'a pas les droits
        if (item.requiresRole && (!userSession || 
            !userSession.role || 
            !item.requiresRole.includes(userSession.role.toLowerCase()))) {
            return;
        }

        // Vérifier si c'est la page active
        const isActive = currentFile === item.href;

        // Créer l'élément de navigation
        const li = document.createElement('li');
        li.innerHTML = `
            <a href="${item.href}" class="nav-link link-body-emphasis px-4 ${isActive ? 'active' : ''}">
                ${item.title}
            </a>
        `;
        navList.appendChild(li);
    });

    updateLastUpdateDate();
});

// Fonction pour formater la date
const formatDate = (dateString) => {
    const date = new Date(dateString);
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');

    return `${day}/${month}/${year} à ${hours}:${minutes}`;
};

// Fonction pour récupérer la dernière mise à jour
const updateLastUpdateDate = async () => {
    try {
        const response = await fetch('http://localhost:8080/api/updates/last');
        if (response.ok) {
            const lastUpdate = await response.text();
            const formattedDate = formatDate(lastUpdate);
            const updateElement = document.querySelector('#lastUpdate');
            if (updateElement) {
                updateElement.textContent = `Mis à jour le ${formattedDate}`;
            }
        }
    } catch (error) {
        console.error('Erreur lors de la récupération de la date de mise à jour:', error);
    }
};