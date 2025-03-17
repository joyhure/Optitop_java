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
        },

        updateCollaboratorsData: async function() {
            const startDate = sessionStorage.getItem('startDate');
            const endDate = sessionStorage.getItem('endDate');
            
            try {
                // Récupération des statistiques de montures (Prime + %)
                const frameStats = await utils.fetchApi(
                    `/invoices/frame-stats?startDate=${startDate}&endDate=${endDate}`
                );

                // Récupération des données P2
                const averageBaskets = await utils.fetchApi(
                    `/invoices/average-baskets?startDate=${startDate}&endDate=${endDate}`
                );

                // Récupération des stats de devis
                const quotationStats = await utils.fetchApi(
                    `/quotations/stats?startDate=${startDate}&endDate=${endDate}`
                );

                // Récupération du CA par vendeur
                const revenueStats = await utils.fetchApi(
                    `/invoices/seller-stats?startDate=${startDate}&endDate=${endDate}`
                );

                // Mise à jour du tableau
                const tbody = document.getElementById('collaborators-data');
                if (!tbody) return;

                // Calcul des totaux
                let totalBonus = 0;
                let totalPremiumPercent = 0;
                let totalP2Count = 0;
                let totalP2Amount = 0;
                let totalQuotations = 0;
                let totalConcretized = 0;
                let totalRevenue = 0;
                let totalValidFrames = 0;

                // Lignes des vendeurs
                let vendorRows = ''; // Initialisation d'une chaîne vide pour stocker les lignes

                frameStats.forEach(seller => {
                    const sellerRef = seller.sellerRef;
                    const p2Data = averageBaskets.find(b => b.sellerRef === sellerRef);
                    const quoteData = quotationStats.sellerStats?.find(s => s.sellerRef === sellerRef);
                    const revenueData = revenueStats.find(r => r.sellerRef === sellerRef);
                    
                    // Accumulation des totaux
                    const bonus = (seller.premiumFrames || 0) * 5;
                    totalBonus += bonus;
                    totalValidFrames += seller.totalFrames || 0;
                    totalPremiumPercent += seller.premiumFrames || 0;
                    totalP2Count += p2Data?.p2Count || 0;
                    totalP2Amount += (p2Data?.averageP2 || 0) * (p2Data?.p2Count || 0);
                    totalQuotations += quoteData?.totalQuotations || 0;
                    totalConcretized += quoteData?.concretizedQuotations || 0;
                    totalRevenue += revenueData?.amount || 0;

                    const premiumPercent = seller.totalFrames > 0 
                        ? ((seller.premiumFrames * 100) / seller.totalFrames).toFixed(1)
                        : 0;

                    vendorRows += `
                        <tr>
                            <td class="fw-bold text-center">${utils.getInitials(sellerRef)}</td>
                            <td class="text-center">${utils.formatCurrency(bonus)}</td>
                            <td class="text-center">${premiumPercent}%</td>
                            <td class="text-center">${p2Data?.p2Count || 0}</td>
                            <td class="text-center">${p2Data?.p2Count > 0 ? utils.formatCurrency(p2Data?.averageP2) : 'Aucun'}</td>
                            <td class="text-center">${quoteData?.totalQuotations || 0}</td>
                            <td class="text-center">${utils.formatPercentage(quoteData?.concretizationRate || 0)}</td>
                            <td class="text-center">${utils.formatCurrency(revenueData?.amount || 0)}</td>
                        </tr>
                    `;
                });

                // Calcul des moyennes globales
                const globalPremiumPercent = totalValidFrames > 0 
                    ? ((totalPremiumPercent * 100) / totalValidFrames).toFixed(1)
                    : 0;
                const globalP2Average = totalP2Count > 0 
                    ? totalP2Amount / totalP2Count 
                    : 0;
                const globalConcretizationRate = totalQuotations > 0
                    ? (totalConcretized * 100 / totalQuotations)
                    : 0;

                // Ajout de la ligne Total au HTML
                tbody.innerHTML = vendorRows + `
                    <tr class="fw-bold table-light">
                        <td class="text-center">Total</td>
                        <td class="text-center">${utils.formatCurrency(totalBonus)}</td>
                        <td class="text-center">${globalPremiumPercent}%</td>
                        <td class="text-center">${totalP2Count}</td>
                        <td class="text-center">${totalP2Count > 0 ? utils.formatCurrency(globalP2Average) : 'Aucun'}</td>
                        <td class="text-center">${totalQuotations}</td>
                        <td class="text-center">${utils.formatPercentage(globalConcretizationRate)}</td>
                        <td class="text-center">${utils.formatCurrency(totalRevenue)}</td>
                    </tr>
                `;

            } catch (error) {
                console.error('Erreur lors de la récupération des données collaborateurs:', error);
            }
        },

        // Ajout de la fonction getInitials
        getInitials(sellerRef) {
            return sellerRef?.substring(0, 2).toUpperCase() || 'XX';
        },
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

                // Ajout de la mise à jour des données collaborateurs
                await utils.updateCollaboratorsData();

            } catch (error) {
                console.error('Erreur lors du chargement des données:', error);
            }
        }
    };

    // Initialisation
    dataManager.initialize();
});