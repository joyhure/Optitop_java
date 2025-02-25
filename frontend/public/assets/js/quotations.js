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

    // Fonction pour convertir une date française en date JS
    function parseFrenchDate(dateStr) {
        const [day, month, year] = dateStr.split('/').map(Number);
        return new Date(year, month - 1, day);
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
                console.log('Devis reçus:', quotations); // Debug

                // Tri par défaut
                const sortedQuotations = sortQuotations(quotations, 'date', 'desc');
                console.log('Devis triés:', sortedQuotations); // Debug
                
                const tbody = document.querySelector('#table-quotations-section table tbody');
                tbody.innerHTML = '';

                sortedQuotations.forEach(quotation => {
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

                // Réinitialiser les icônes de tri
                document.querySelectorAll('.sort-icon').forEach(icon => {
                    icon.classList.remove('active');
                    icon.dataset.order = 'asc';
                    icon.querySelector('use').setAttribute('xlink:href', '../node_modules/bootstrap-icons/bootstrap-icons.svg#sort-down');
                });

                // Activer l'icône de tri par date
                const dateIcon = document.querySelector('.sort-icon[data-sort="date"]');
                if (dateIcon) {
                    dateIcon.classList.add('active');
                    dateIcon.dataset.order = 'desc';
                    dateIcon.querySelector('use').setAttribute('xlink:href', '../node_modules/bootstrap-icons/bootstrap-icons.svg#sort-up');
                }
            }
        } catch (error) {
            console.error('Erreur lors du chargement des devis:', error);
        }
    }

    // Fonction de tri modifiée
    function sortQuotations(data, field, order) {
        return [...data].sort((a, b) => {
            let valueA, valueB;
            
            switch (field) {
                case 'date':
                    // Pour les données du serveur
                    if (typeof a.date === 'string') {
                        valueA = new Date(a.date).getTime();
                        valueB = new Date(b.date).getTime();
                    } 
                    // Pour les données du DOM
                    else {
                        valueA = a.date.getTime();
                        valueB = b.date.getTime();
                    }
                    break;
                case 'name':
                    // Pour les données du serveur
                    if (a.sellerRef && typeof a.sellerRef === 'string') {
                        valueA = getInitials(a.sellerRef).toLowerCase();
                        valueB = getInitials(b.sellerRef).toLowerCase();
                    } 
                    // Pour les données du DOM
                    else {
                        valueA = a.sellerRef.toLowerCase();
                        valueB = b.sellerRef.toLowerCase();
                    }
                    break;
                default:
                    return 0;
            }

            if (valueA === valueB) return 0;
            if (order === 'asc') {
                return valueA < valueB ? -1 : 1;
            } else {
                return valueA > valueB ? -1 : 1;
            }
        });
    }

    // Gestionnaire de tri modifié
    document.querySelectorAll('.sort-icon').forEach(icon => {
        icon.addEventListener('click', function() {
            const field = this.dataset.sort;
            let order = this.dataset.order;

            // Réinitialiser toutes les icônes
            document.querySelectorAll('.sort-icon').forEach(i => {
                i.classList.remove('active');
                i.dataset.order = 'asc';
                i.querySelector('use').setAttribute('xlink:href', '../node_modules/bootstrap-icons/bootstrap-icons.svg#sort-down');
            });

            // Inverser l'ordre pour l'icône cliquée
            order = order === 'asc' ? 'desc' : 'asc';
            this.dataset.order = order;
            this.classList.add('active');

            // Mettre à jour l'icône
            this.querySelector('use').setAttribute('xlink:href', 
                `../node_modules/bootstrap-icons/bootstrap-icons.svg#sort-${order === 'asc' ? 'down' : 'up'}`
            );

            // Récupérer et trier les données
            const tbody = document.querySelector('#table-quotations-section table tbody');
            const rows = Array.from(tbody.querySelectorAll('tr'));
            const data = rows.map(row => {
                const dateCell = row.cells[0].textContent;
                return {
                    rawDate: dateCell, // Garder la date formatée
                    date: parseFrenchDate(dateCell), // Date pour le tri
                    sellerRef: row.cells[1].textContent,
                    client: row.cells[2].textContent,
                    action: row.cells[3].textContent,
                    comment: row.cells[4].textContent
                };
            });

            const sortedData = sortQuotations(data, field, order);

            // Mettre à jour l'affichage avec les données triées
            tbody.innerHTML = '';
            sortedData.forEach(quotation => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${quotation.rawDate}</td>
                    <td class="text-center">${quotation.sellerRef}</td>
                    <td>${quotation.client}</td>
                    <td>${quotation.action || ''}</td>
                    <td>${quotation.comment || ''}</td>
                `;
                tbody.appendChild(row);
            });
        });
    });

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