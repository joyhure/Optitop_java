document.addEventListener('DOMContentLoaded', function() {
    // Configuration
    const CONFIG = {
        API_BASE_URL: 'http://localhost:8080/api'
    };

    // Utilitaires
    const utils = {
        async fetchApi(endpoint) {
            const response = await fetch(`${CONFIG.API_BASE_URL}${endpoint}`);
            if (!response.ok) throw new Error(`Erreur API: ${response.status}`);
            return response.json();
        },

        formatDate(date) {
            return new Intl.DateTimeFormat('fr-FR').format(new Date(date));
        },

        formatCurrency(amount) {
            return new Intl.NumberFormat('fr-FR', {
                style: 'currency',
                currency: 'EUR',
                minimumFractionDigits: 0,
                maximumFractionDigits: 0
            }).format(amount);
        },

        formatPercentage(value) {
            return new Intl.NumberFormat('fr-FR', {
                style: 'percent',
                minimumFractionDigits: 0,
                maximumFractionDigits: 0
            }).format(value / 100);
        },

        updatePeriodDates() {
            const startDate = sessionStorage.getItem('startDate');
            const endDate = sessionStorage.getItem('endDate');
            document.getElementById('period-dates').textContent = 
                `Du ${this.formatDate(startDate)} au ${this.formatDate(endDate)}`;
        }
    };

    // Gestionnaire de données
    const dataManager = {
        async initialize() {
            try {
                utils.updatePeriodDates();

                const lastUpdateElement = document.getElementById('last-update');
                if (lastUpdateElement) {
                    // On utilise la même fonction que dans navbar.js
                    await updateLastUpdateDate();
                    // On copie le contenu depuis l'élément de la navbar
                    const navbarUpdate = document.getElementById('lastUpdate');
                    if (navbarUpdate) {
                        lastUpdateElement.textContent = navbarUpdate.textContent;
                    }
                }

                // Autres appels API et mises à jour à venir...
            } catch (error) {
                console.error('Erreur lors du chargement des données:', error);
            }
        }
    };

    // Initialisation
    dataManager.initialize();
});