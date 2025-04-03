document.addEventListener('DOMContentLoaded', function() {
    // Configuration
    const CONFIG = {
        API_BASE_URL: 'http://localhost:8080/api',
        BONUS_PER_FRAME: 5
    };

    // Éléments du DOM
    const DOM = {
        basketsBody: document.getElementById('table-baskets-body'),
        framesBody: document.getElementById('table-frames-body'),
        cardPm: document.getElementById('card-pm'),
        cardP2: document.getElementById('card-p2')
    };

    // État de l'application
    const STATE = {
        startDate: sessionStorage.getItem('startDate'),
        endDate: sessionStorage.getItem('endDate')
    };

    // Utilitaires
    const utils = {
        formatCurrency(amount) {
            if (amount === null || amount === undefined) return 'Indéfini';
            return `${amount.toFixed(2)}€`;
        },
        
        getInitials(seller) {
            return seller?.substring(0, 2).toUpperCase() || 'XX';
        },

        async fetchApi(endpoint, options = {}) {
            try {
                const response = await fetch(`${CONFIG.API_BASE_URL}${endpoint}`, {
                    ...options,
                    headers: {
                        'Content-Type': 'application/json',
                        ...options.headers
                    }
                });
                if (!response.ok) throw new Error(`Erreur API: ${response.status}`);
                return response.json();
            } catch (error) {
                console.error(`Erreur lors de l'appel API ${endpoint}:`, error);
                throw error;
            }
        }
    };

    // Gestionnaire des paniers moyens
    const averageBasketManager = {
        calculateTotals(stats) {
            return stats.reduce((acc, seller) => {
                acc.totalCount += seller.invoiceCount || 0;
                acc.sumBasket += (seller.averageBasket || 0) * (seller.invoiceCount || 0);
                acc.sumFramesP1 += (seller.averageP1MON || 0) * (seller.invoiceCount || 0);
                acc.sumLensesP1 += (seller.averageP1VER || 0) * (seller.invoiceCount || 0);
                acc.totalP2Amount += (seller.averageP2 || 0) * (seller.p2Count || 0);
                acc.totalP2Count += seller.p2Count || 0;
                return acc;
            }, {
                totalCount: 0,
                sumBasket: 0,
                sumFramesP1: 0,
                sumLensesP1: 0,
                totalP2Amount: 0,
                totalP2Count: 0
            });
        },

        calculateAverages(totals) {
            return {
                avgBasket: totals.totalCount ? totals.sumBasket / totals.totalCount : null,
                avgFramesP1: totals.totalCount ? totals.sumFramesP1 / totals.totalCount : null,
                avgLensesP1: totals.totalCount ? totals.sumLensesP1 / totals.totalCount : null,
                avgP2Total: totals.totalP2Count ? totals.totalP2Amount / totals.totalP2Count : null
            };
        },

        createSellerRow(seller) {
            return `
                <tr>
                    <td class="text-center">${utils.getInitials(seller.sellerRef)}</td>
                    <td class="text-center">${utils.formatCurrency(seller.averageBasket)}</td>
                    <td class="text-center">${seller.invoiceCount}</td>
                    <td class="text-center">${utils.formatCurrency(seller.averageP1MON)}</td>
                    <td class="text-center">${utils.formatCurrency(seller.averageP1VER)}</td>
                    <td class="text-center">${seller.p2Count > 0 ? utils.formatCurrency(seller.averageP2) : 'Aucun'}</td>
                </tr>
            `;
        },

        createTotalRow(totalStats) {
            return `
                <tr class="fw-bold">
                    <td class="text-center">Total</td>
                    <td class="text-center">${utils.formatCurrency(totalStats.averageBasket)}</td>
                    <td class="text-center">${totalStats.invoiceCount}</td>
                    <td class="text-center">${utils.formatCurrency(totalStats.averageP1MON)}</td>
                    <td class="text-center">${utils.formatCurrency(totalStats.averageP1VER)}</td>
                    <td class="text-center">${totalStats.p2Count > 0 ? utils.formatCurrency(totalStats.averageP2) : 'Aucun'}</td>
                </tr>
            `;
        },

        updateDisplay(stats, totalStats) {
            if (!DOM.basketsBody) return;

            const rows = [
                ...stats.map(seller => this.createSellerRow(seller)),
                this.createTotalRow(totalStats)
            ];

            DOM.basketsBody.innerHTML = rows.join('');
            DOM.cardPm.textContent = utils.formatCurrency(totalStats.averageBasket);
            DOM.cardP2.textContent = utils.formatCurrency(totalStats.averageP2);
        }
    };

    // Gestionnaire des statistiques de montures
    const frameStatsManager = {
        calculateTotals(stats) {
            return stats.reduce((acc, seller) => {
                acc.totalFrames += seller.totalFrames || 0;
                acc.totalPremiumFrames += seller.premiumFrames || 0;
                acc.totalBonus += (seller.premiumFrames || 0) * CONFIG.BONUS_PER_FRAME;
                return acc;
            }, { totalFrames: 0, totalPremiumFrames: 0, totalBonus: 0 });
        },

        calculatePercentage(total, part) {
            return total > 0 ? (part * 100 / total).toFixed(1) : 0;
        },

        createSellerRow(seller) {
            const bonus = (seller.premiumFrames || 0) * CONFIG.BONUS_PER_FRAME;
            const percentage = this.calculatePercentage(seller.totalFrames, seller.premiumFrames);

            return `
                <tr>
                    <td class="text-center">${utils.getInitials(seller.sellerRef)}</td>
                    <td class="text-center">${seller.totalFrames || 0}</td>
                    <td class="text-center">${seller.premiumFrames || 0}</td>
                    <td class="text-center">${percentage}%</td>
                    <td class="text-center">${utils.formatCurrency(bonus)}</td>
                </tr>
            `;
        },

        createTotalRow(totals) {
            const percentage = this.calculatePercentage(totals.totalFrames, totals.totalPremiumFrames);

            return `
                <tr class="fw-bold">
                    <td class="text-center">Total</td>
                    <td class="text-center">${totals.totalFrames}</td>
                    <td class="text-center">${totals.totalPremiumFrames}</td>
                    <td class="text-center">${percentage}%</td>
                    <td class="text-center">${utils.formatCurrency(totals.totalBonus)}</td>
                </tr>
            `;
        },

        updateDisplay(stats) {
            if (!DOM.framesBody) return;

            const totals = this.calculateTotals(stats);
            const rows = [
                ...stats.map(seller => this.createSellerRow(seller)),
                this.createTotalRow(totals)
            ];

            DOM.framesBody.innerHTML = rows.join('');
        }
    };

    // Gestionnaire principal des données
    const dataManager = {
        async loadAllData() {
            try {
                const [stats, totalStats] = await Promise.all([
                    this.loadAverageBaskets(),
                    this.loadTotalStats()
                ]);
                averageBasketManager.updateDisplay(stats, totalStats);
                await this.loadFrameStats();
            } catch (error) {
                console.error('Erreur lors du chargement des données:', error);
            }
        },

        async loadAverageBaskets() {
            try {
                const stats = await utils.fetchApi(
                    `/invoices/average-baskets?startDate=${STATE.startDate}&endDate=${STATE.endDate}`
                );
                return stats;
            } catch (error) {
                console.error('Erreur chargement paniers moyens:', error);
            }
        },

        async loadTotalStats() {
            return await utils.fetchApi(
                `/invoices/total-stats?startDate=${STATE.startDate}&endDate=${STATE.endDate}`
            );
        },

        async loadFrameStats() {
            try {
                const stats = await utils.fetchApi(
                    `/invoices/frame-stats?startDate=${STATE.startDate}&endDate=${STATE.endDate}`
                );
                frameStatsManager.updateDisplay(stats);
            } catch (error) {
                console.error('Erreur chargement statistiques montures:', error);
            }
        }
    };

    // Initialisation
    dataManager.loadAllData();
});