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

        // Formatage identique à revenue.js
        formatCurrency(amount) {
            return new Intl.NumberFormat('fr-FR', {
                style: 'currency',
                currency: 'EUR',
                minimumFractionDigits: 0,
                maximumFractionDigits: 0
            }).format(amount);
        },

        formatDelta(delta) {
            return new Intl.NumberFormat('fr-FR', {
                style: 'currency',
                currency: 'EUR',
                minimumFractionDigits: 0,
                maximumFractionDigits: 0,
                signDisplay: 'always'
            }).format(delta);
        },

        formatDeltaPercent(currentValue, previousValue) {
            const percentChange = ((currentValue - previousValue) / Math.abs(previousValue)) * 100;
            return new Intl.NumberFormat('fr-FR', {
                style: 'percent',
                minimumFractionDigits: 1,
                maximumFractionDigits: 1,
                signDisplay: 'always'
            }).format(percentChange / 100);
        },

        formatPercentage(value) {
            return new Intl.NumberFormat('fr-FR', {
                style: 'percent',
                minimumFractionDigits: 1,
                maximumFractionDigits: 1
            }).format(value / 100);
        },

        updatePeriodDates() {
            const startDate = sessionStorage.getItem('startDate');
            const endDate = sessionStorage.getItem('endDate');
            document.getElementById('period-dates').textContent = 
                `Du ${this.formatDate(startDate)} au ${this.formatDate(endDate)}`;
        },

        // Mise à jour des données
        updateRevenueData(data) {
            // Gestion du CA N
            document.getElementById('current-revenue').textContent = this.formatCurrency(data.currentAmount || 0);
            
            // Gestion du CA N-1 et des deltas
            if (data.previousAmount === null || data.previousAmount === undefined || data.previousAmount === 0) {
                document.getElementById('previous-revenue').textContent = 'Inconnu';
                document.getElementById('revenue-delta').textContent = 'Inconnu';
                document.getElementById('revenue-delta-percent').textContent = 'Inconnu';
                document.getElementById('previous-rate').textContent = 'Inconnu';
            } else {
                document.getElementById('previous-revenue').textContent = this.formatCurrency(data.previousAmount);
                const delta = data.currentAmount - data.previousAmount;
                document.getElementById('revenue-delta').textContent = this.formatDelta(delta);
                document.getElementById('revenue-delta-percent').textContent = 
                    this.formatDeltaPercent(data.currentAmount, data.previousAmount);
            }
        },

        updateConcretizationRates(stats) {
            if (stats.concretizationRate !== undefined) {
                document.getElementById('current-rate').textContent = this.formatPercentage(stats.concretizationRate);
            }

        },

        updatePreviousConcretizationRate(rate) {
            if (rate !== undefined && rate !== null && rate !== 0) {
                document.getElementById('previous-rate').textContent = this.formatPercentage(rate);
            }
        }
    };

    // Gestionnaire de données
    const dataManager = {
        async initialize() {
            try {
                utils.updatePeriodDates();
                await updateLastUpdateDate();

                const startDate = sessionStorage.getItem('startDate');
                const endDate = sessionStorage.getItem('endDate');

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

                // Récupération des données période comme dans revenue.js
                const periodData = await utils.fetchApi(
                    `/invoices/period-revenue?startDate=${startDate}&endDate=${endDate}`
                );

                utils.updateRevenueData(periodData);

                // Chargement des taux de concrétisation
                const statsData = await utils.fetchApi(
                    `/quotations/stats?startDate=${startDate}&endDate=${endDate}`
                );
                utils.updateConcretizationRates(statsData);

                // Chargement du taux de concrétisation N-1
                const previousRateData = await utils.fetchApi(
                    `/quotations/previous-concretization?startDate=${startDate}&endDate=${endDate}`
                );
                utils.updatePreviousConcretizationRate(previousRateData);

            } catch (error) {
                console.error('Erreur lors du chargement des données:', error);
            }
        }
    };

    // Initialisation
    dataManager.initialize();
});