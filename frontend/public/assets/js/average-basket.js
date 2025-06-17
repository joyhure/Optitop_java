/**
 * Gestionnaire des paniers moyens et primes Optitop
 * 
 * Gère les opérations liées aux statistiques de vente :
 * - Affichage des paniers moyens par vendeur
 * - Calcul et affichage des primes sur montures
 * - Récupération des statistiques de vente P1/P2
 * - Mise à jour des cartes résumés
 */

// ===== CONFIGURATION =====

const CONFIG = {
    API_BASE_URL: 'http://localhost:8080/api',
    ENDPOINTS: {
        AVERAGE_BASKETS: '/invoices/average-baskets',
        TOTAL_STATS: '/invoices/total-stats',
        FRAME_STATS: '/invoices/frame-stats'
    },
    BONUS_PER_FRAME: 5,
    LOCALE: 'fr-FR'
};

// ===== ÉLÉMENTS DOM =====

const elements = {
    basketsBody: null,
    framesBody: null,
    cardPm: null,
    cardP2: null
};

// ===== ÉTAT DE L'APPLICATION =====

const state = {
    startDate: null,
    endDate: null,
    currentStats: null,
    currentTotalStats: null
};

// ===== INITIALISATION =====

/**
 * Initialisation au chargement du DOM
 */
document.addEventListener('DOMContentLoaded', () => {
    try {
        initializeElements();
        initializeState();
        averageBasketController.init();
    } catch (error) {
        console.error('Erreur lors de l\'initialisation des paniers moyens:', error);
    }
});

/**
 * Initialise les références aux éléments DOM
 */
function initializeElements() {
    elements.basketsBody = document.querySelector('#table-baskets-body');
    elements.framesBody = document.querySelector('#table-frames-body');
    elements.cardPm = document.querySelector('#card-pm');
    elements.cardP2 = document.querySelector('#card-p2');
}

/**
 * Initialise l'état de l'application
 */
function initializeState() {
    state.startDate = sessionStorage.getItem('startDate');
    state.endDate = sessionStorage.getItem('endDate');
}

// ===== UTILITAIRES =====

/**
 * Utilitaires généraux
 */
const utils = {
    /**
     * Formate un montant en devise
     * @param {number} amount - Montant à formater
     * @returns {string} - Montant formaté
     */
    formatCurrency: (amount) => {
        if (amount === null || amount === undefined) return 'Indéfini';
        return `${amount.toFixed(2)}€`;
    },
    
    /**
     * Récupère les initiales d'un vendeur
     * @param {string} seller - Référence du vendeur
     * @returns {string} - Initiales en majuscules
     */
    getInitials: (seller) => {
        return seller?.substring(0, 2).toUpperCase() || 'XX';
    },

    /**
     * Calcule un pourcentage
     * @param {number} total - Valeur totale
     * @param {number} part - Partie du total
     * @returns {string} - Pourcentage formaté
     */
    calculatePercentage: (total, part) => {
        return total > 0 ? (part * 100 / total).toFixed(1) : '0';
    },

    /**
     * Valide les dates de période
     * @returns {boolean} - true si les dates sont valides
     */
    validateDates: () => {
        return state.startDate && state.endDate;
    }
};

/**
 * Utilitaires de service API
 */
const apiUtils = {
    /**
     * Crée les en-têtes de requête
     * @returns {Object} - En-têtes HTTP
     */
    createHeaders: () => ({
        'Content-Type': 'application/json'
    }),

    /**
     * Gère les erreurs de réponse API
     * @param {Response} response - Réponse fetch
     * @throws {Error} - Erreur appropriée selon le status
     */
    handleApiError: async (response) => {
        if (!response.ok) {
            const error = await response.json().catch(() => ({}));
            throw new Error(error.message || `Erreur API: ${response.status}`);
        }
    },

    /**
     * Construit l'URL avec paramètres de date
     * @param {string} endpoint - Point de terminaison API
     * @returns {string} - URL complète avec paramètres
     */
    buildUrlWithDates: (endpoint) => {
        const params = new URLSearchParams({
            startDate: state.startDate,
            endDate: state.endDate
        });
        return `${CONFIG.API_BASE_URL}${endpoint}?${params}`;
    }
};

// ===== SERVICES API =====

/**
 * Service de gestion des statistiques de paniers
 */
const statisticsService = {
    /**
     * Récupère les statistiques de paniers moyens
     * @returns {Promise<Array>} - Liste des statistiques par vendeur
     */
    async getAverageBaskets() {
        try {
            if (!utils.validateDates()) {
                throw new Error('Dates de période non définies');
            }

            const response = await fetch(apiUtils.buildUrlWithDates(CONFIG.ENDPOINTS.AVERAGE_BASKETS), {
                headers: apiUtils.createHeaders()
            });
            
            await apiUtils.handleApiError(response);
            return response.json();
        } catch (error) {
            console.error('Erreur lors de la récupération des paniers moyens:', error);
            throw error;
        }
    },

    /**
     * Récupère les statistiques totales
     * @returns {Promise<Object>} - Statistiques globales
     */
    async getTotalStats() {
        try {
            if (!utils.validateDates()) {
                throw new Error('Dates de période non définies');
            }

            const response = await fetch(apiUtils.buildUrlWithDates(CONFIG.ENDPOINTS.TOTAL_STATS), {
                headers: apiUtils.createHeaders()
            });
            
            await apiUtils.handleApiError(response);
            return response.json();
        } catch (error) {
            console.error('Erreur lors de la récupération des statistiques totales:', error);
            throw error;
        }
    },

    /**
     * Récupère les statistiques de montures
     * @returns {Promise<Array>} - Liste des statistiques de montures par vendeur
     */
    async getFrameStats() {
        try {
            if (!utils.validateDates()) {
                throw new Error('Dates de période non définies');
            }

            const response = await fetch(apiUtils.buildUrlWithDates(CONFIG.ENDPOINTS.FRAME_STATS), {
                headers: apiUtils.createHeaders()
            });
            
            await apiUtils.handleApiError(response);
            return response.json();
        } catch (error) {
            console.error('Erreur lors de la récupération des statistiques de montures:', error);
            throw error;
        }
    }
};

// ===== GESTIONNAIRE D'INTERFACE =====

/**
 * Gestionnaire du rendu et des interactions UI
 */
const uiManager = {
    /**
     * Génère une ligne de vendeur pour le tableau des paniers
     * @param {Object} seller - Données du vendeur
     * @returns {string} - HTML de la ligne
     */
    renderSellerBasketRow(seller) {
        return `
            <tr>
                <td class="text-center align-middle">${utils.getInitials(seller.sellerRef)}</td>
                <td class="text-center align-middle">${utils.formatCurrency(seller.averageBasket)}</td>
                <td class="text-center align-middle">${seller.invoiceCount || 0}</td>
                <td class="text-center align-middle">${utils.formatCurrency(seller.averageP1MON)}</td>
                <td class="text-center align-middle">${utils.formatCurrency(seller.averageP1VER)}</td>
                <td class="text-center align-middle">${seller.p2Count > 0 ? utils.formatCurrency(seller.averageP2) : 'Aucun'}</td>
            </tr>
        `;
    },

    /**
     * Génère la ligne de total pour le tableau des paniers
     * @param {Object} totalStats - Statistiques totales
     * @returns {string} - HTML de la ligne de total
     */
    renderBasketTotalRow(totalStats) {
        return `
            <tr class="fw-bold">
                <td class="text-center align-middle">Total</td>
                <td class="text-center align-middle">${utils.formatCurrency(totalStats.averageBasket)}</td>
                <td class="text-center align-middle">${totalStats.invoiceCount || 0}</td>
                <td class="text-center align-middle">${utils.formatCurrency(totalStats.averageP1MON)}</td>
                <td class="text-center align-middle">${utils.formatCurrency(totalStats.averageP1VER)}</td>
                <td class="text-center align-middle">${totalStats.p2Count > 0 ? utils.formatCurrency(totalStats.averageP2) : 'Aucun'}</td>
            </tr>
        `;
    },

    /**
     * Génère une ligne de vendeur pour le tableau des montures
     * @param {Object} seller - Données du vendeur
     * @returns {string} - HTML de la ligne
     */
    renderSellerFrameRow(seller) {
        const bonus = (seller.premiumFrames || 0) * CONFIG.BONUS_PER_FRAME;
        const percentage = utils.calculatePercentage(seller.totalFrames, seller.premiumFrames);

        return `
            <tr>
                <td class="text-center align-middle">${utils.getInitials(seller.sellerRef)}</td>
                <td class="text-center align-middle">${seller.totalFrames || 0}</td>
                <td class="text-center align-middle">${seller.premiumFrames || 0}</td>
                <td class="text-center align-middle">${percentage}%</td>
                <td class="text-center align-middle">${utils.formatCurrency(bonus)}</td>
            </tr>
        `;
    },

    /**
     * Génère la ligne de total pour le tableau des montures
     * @param {Object} totals - Totaux calculés
     * @returns {string} - HTML de la ligne de total
     */
    renderFrameTotalRow(totals) {
        const percentage = utils.calculatePercentage(totals.totalFrames, totals.totalPremiumFrames);

        return `
            <tr class="fw-bold">
                <td class="text-center align-middle">Total</td>
                <td class="text-center align-middle">${totals.totalFrames}</td>
                <td class="text-center align-middle">${totals.totalPremiumFrames}</td>
                <td class="text-center align-middle">${percentage}%</td>
                <td class="text-center align-middle">${utils.formatCurrency(totals.totalBonus)}</td>
            </tr>
        `;
    },

    /**
     * Met à jour le tableau des paniers moyens
     * @param {Array} stats - Statistiques par vendeur
     * @param {Object} totalStats - Statistiques totales
     */
    updateBasketsTable(stats, totalStats) {
        if (!elements.basketsBody) return;

        const rows = [
            ...stats.map(seller => this.renderSellerBasketRow(seller)),
            this.renderBasketTotalRow(totalStats)
        ];

        elements.basketsBody.innerHTML = rows.join('');
    },

    /**
     * Met à jour le tableau des montures
     * @param {Array} stats - Statistiques par vendeur
     */
    updateFramesTable(stats) {
        if (!elements.framesBody) return;

        const totals = this.calculateFrameTotals(stats);
        const rows = [
            ...stats.map(seller => this.renderSellerFrameRow(seller)),
            this.renderFrameTotalRow(totals)
        ];

        elements.framesBody.innerHTML = rows.join('');
    },

    /**
     * Met à jour les cartes de résumé
     * @param {Object} totalStats - Statistiques totales
     */
    updateSummaryCards(totalStats) {
        if (elements.cardPm) {
            elements.cardPm.textContent = utils.formatCurrency(totalStats.averageBasket);
        }
        if (elements.cardP2) {
            elements.cardP2.textContent = utils.formatCurrency(totalStats.averageP2);
        }
    },

    /**
     * Calcule les totaux pour les montures
     * @param {Array} stats - Statistiques par vendeur
     * @returns {Object} - Totaux calculés
     */
    calculateFrameTotals(stats) {
        return stats.reduce((acc, seller) => {
            acc.totalFrames += seller.totalFrames || 0;
            acc.totalPremiumFrames += seller.premiumFrames || 0;
            acc.totalBonus += (seller.premiumFrames || 0) * CONFIG.BONUS_PER_FRAME;
            return acc;
        }, { totalFrames: 0, totalPremiumFrames: 0, totalBonus: 0 });
    },

    /**
     * Affiche un message d'erreur dans les tableaux
     * @param {string} message - Message d'erreur à afficher
     */
    showError(message) {
        const errorRow = `
            <tr>
                <td colspan="6" class="text-center align-middle text-danger">
                    ${message}
                </td>
            </tr>
        `;

        if (elements.basketsBody) {
            elements.basketsBody.innerHTML = errorRow;
        }
        if (elements.framesBody) {
            elements.framesBody.innerHTML = errorRow.replace('colspan="6"', 'colspan="5"');
        }
    }
};

// ===== CONTRÔLEUR PRINCIPAL =====

/**
 * Contrôleur principal de gestion des paniers moyens
 */
const averageBasketController = {
    /**
     * Initialise le contrôleur
     */
    async init() {
        try {
            if (!utils.validateDates()) {
                throw new Error('Dates de période non définies dans le sessionStorage');
            }
            await this.loadAllData();
        } catch (error) {
            console.error('Erreur lors de l\'initialisation du contrôleur:', error);
            uiManager.showError('Erreur lors du chargement des données');
        }
    },

    /**
     * Charge toutes les données nécessaires
     */
    async loadAllData() {
        try {
            // Chargement en parallèle des données
            const [stats, totalStats, frameStats] = await Promise.all([
                statisticsService.getAverageBaskets(),
                statisticsService.getTotalStats(),
                statisticsService.getFrameStats()
            ]);

            // Mise à jour de l'état
            state.currentStats = stats;
            state.currentTotalStats = totalStats;

            // Mise à jour de l'interface
            uiManager.updateBasketsTable(stats, totalStats);
            uiManager.updateFramesTable(frameStats);
            uiManager.updateSummaryCards(totalStats);

        } catch (error) {
            console.error('Erreur lors du chargement des données:', error);
            uiManager.showError('Impossible de charger les statistiques');
            throw error;
        }
    },

    /**
     * Recharge les données (méthode publique)
     */
    async refresh() {
        try {
            await this.loadAllData();
        } catch (error) {
            console.error('Erreur lors du rafraîchissement:', error);
            alert('Erreur lors du rafraîchissement des données');
        }
    }
};

// ===== FONCTIONS GLOBALES (conservées pour compatibilité) =====

/**
 * Rafraîchit les données (fonction globale)
 */
function refreshData() {
    averageBasketController.refresh();
}

/**
 * Exporte les données au format CSV (fonction future)
 */
function exportToCSV() {
    console.log('Export CSV non implémenté');
    alert('Fonctionnalité d\'export en cours de développement');
}