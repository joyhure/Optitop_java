document.addEventListener('DOMContentLoaded', function() {
    const userSession = JSON.parse(sessionStorage.getItem('user'));
    const summarySection = document.getElementById('summary');
    
    console.log('Session utilisateur :', userSession);

    // Gestion de la section summary
    if (summarySection) {
        const requiredRoles = summarySection.dataset.requiresRole.split(',');
        console.log('Rôles requis :', requiredRoles);
        console.log('Rôle utilisateur :', userSession?.role);
        
        if (!userSession?.role || !requiredRoles.includes(userSession.role.toLowerCase())) {
            console.log('Masquage de la section summary');
            summarySection.classList.add('d-none');
        }
    }

    // Gestion de la colonne "Nom" pour les collaborateurs
    if (userSession?.role?.toLowerCase() === 'collaborator') {
        // Masquer l'en-tête de la colonne
        const nameHeader = document.querySelector('th.table-col-w4');
        if (nameHeader) {
            nameHeader.style.display = 'none';
        }

        // Masquer les cellules correspondantes
        const nameCells = document.querySelectorAll('td.text-center');
        nameCells.forEach(cell => {
            if (cell.textContent.length === 2) { // Pour les initiales (2 caractères)
                cell.style.display = 'none';
            }
        });
    }
});