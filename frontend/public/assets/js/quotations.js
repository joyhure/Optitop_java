document.addEventListener('DOMContentLoaded', function() {
    const userSession = JSON.parse(sessionStorage.getItem('user'));
    const summarySection = document.getElementById('summary');
    
    console.log('Session utilisateur :', userSession);

    // Gestion de la section summary
    if (summarySection) {
        const requiredRoles = summarySection.dataset.requiresRole.split(',');
        if (!userSession?.role || !requiredRoles.includes(userSession.role.toLowerCase())) {
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

    // Fonction pour formater la date
    function formatDate(dateString) {
        const date = new Date(dateString);
        return date.toLocaleDateString('fr-FR');
    }

    // Fonction pour extraire les initiales
    function getInitials(sellerRef) {
        return sellerRef.substring(0, 2).toUpperCase();
    }

    // Fonction pour charger les devis
    async function loadQuotations() {
        try {
            const startDate = sessionStorage.getItem('startDate');
            const endDate = sessionStorage.getItem('endDate');
            
            console.log('Chargement des devis pour la période:', startDate, 'à', endDate);

            const response = await fetch(`http://localhost:8080/api/quotations/unvalidated?startDate=${startDate}&endDate=${endDate}`);
            
            if (response.ok) {
                const quotations = await response.json();
                console.log('Devis récupérés:', quotations);

                const tbody = document.querySelector('#table-quotations-section table tbody');
                tbody.innerHTML = '';

                // Dans la fonction loadQuotations(), modifions la façon dont la cellule de statut est remplie
                quotations.forEach(quotation => {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                        <td>${formatDate(quotation.date)}</td>
                        <td class="text-center">${getInitials(quotation.sellerRef)}</td>
                        <td>${quotation.client}</td>
                        <td>${quotation.action || ''}</td>
                        <td>${quotation.comment || ''}</td>
                    `;
                    tbody.appendChild(row);
                });
            } else {
                console.error('Erreur lors de la récupération des devis');
            }
        } catch (error) {
            console.error('Erreur:', error);
        }
    }

    // Charger les devis initialement
    loadQuotations();

    // Écouter les changements de période depuis le header
    window.addEventListener('storage', function(e) {
        if (e.key === 'startDate' || e.key === 'endDate') {
            console.log('Période modifiée, rechargement des devis');
            loadQuotations();
        }
    });

    // Écouter un événement personnalisé pour la mise à jour des dates
    document.addEventListener('datesUpdated', function() {
        console.log('Événement datesUpdated reçu');
        loadQuotations();
    });
});