/**
 * Gestionnaire de synthèse commerciale Optitop
 * 
 * Gère les opérations liées à la synthèse commerciale :
 * - Affichage des données globales de chiffre d'affaires
 * - Calcul et affichage des écarts avec l'année précédente
 * - Synthèse des statistiques par collaborateur
 * - Mise à jour des taux de concrétisation
 */

// ===== CONFIGURATION =====

const CONFIG = {
    API_BASE_URL: 'http://localhost:8080/api',
    ENDPOINTS: {
        PERIOD_REVENUE: '/invoices/period-revenue',
        QUOTATIONS_STATS: '/quotations/stats',
        PREVIOUS_CONCRETIZATION: '/quotations/previous-concretization',
        FRAME_STATS: '/invoices/frame-stats',
        AVERAGE_BASKETS: '/invoices/average-baskets',
        SELLER_STATS: '/invoices/seller-stats'
    },
    BONUS_PER_FRAME: 5,
    LOCALE: 'fr-FR',
    CURRENCY: 'EUR'
};

// ===== ÉLÉMENTS DOM =====

const elements = {
    periodDates: null,
    lastUpdate: null,
    currentRevenue: null,
    previousRevenue: null,
    revenueDelta: null,
    revenueDeltaPercent: null,
    currentRate: null,
    previousRate: null,
    collaboratorsData: null
};

// ===== ÉTAT DE L'APPLICATION =====

const state = {
    startDate: null,
    endDate: null,
    revenueData: null,
    quotationStats: null,
    collaboratorStats: {
        frameStats: null,
        averageBaskets: null,
        revenueStats: null
    }
};

// ===== INITIALISATION =====

/**
 * Initialisation au chargement du DOM
 */
document.addEventListener('DOMContentLoaded', () => {
    try {
        initializeElements();
        initializeState();
        summaryController.init();
    } catch (error) {
        console.error('Erreur lors de l\'initialisation de la synthèse:', error);
    }
});

/**
 * Initialise les références aux éléments DOM
 */
function initializeElements() {
    elements.periodDates = document.querySelector('#period-dates');
    elements.lastUpdate = document.querySelector('#last-update');
    elements.currentRevenue = document.querySelector('#current-revenue');
    elements.previousRevenue = document.querySelector('#previous-revenue');
    elements.revenueDelta = document.querySelector('#revenue-delta');
    elements.revenueDeltaPercent = document.querySelector('#revenue-delta-percent');
    elements.currentRate = document.querySelector('#current-rate');
    elements.previousRate = document.querySelector('#previous-rate');
    elements.collaboratorsData = document.querySelector('#collaborators-data');
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
 * Utilitaires de formatage
 */
const utils = {
    /**
     * Formate un montant en devise
     * @param {number} amount - Montant à formater
     * @returns {string} - Montant formaté
     */
    formatCurrency: (amount) => {
        return new Intl.NumberFormat(CONFIG.LOCALE, {
            style: 'currency',
            currency: CONFIG.CURRENCY,
            minimumFractionDigits: 0,
            maximumFractionDigits: 0
        }).format(amount);
    },

    /**
     * Formate un delta avec signe
     * @param {number} delta - Delta à formater
     * @returns {string} - Delta formaté
     */
    formatDelta: (delta) => {
        return new Intl.NumberFormat(CONFIG.LOCALE, {
            style: 'currency',
            currency: CONFIG.CURRENCY,
            minimumFractionDigits: 0,
            maximumFractionDigits: 0,
            signDisplay: 'always'
        }).format(delta);
    },

    /**
     * Calcule et formate un pourcentage d'évolution
     * @param {number} currentValue - Valeur actuelle
     * @param {number} previousValue - Valeur précédente
     * @returns {string} - Pourcentage formaté
     */
    formatDeltaPercent: (currentValue, previousValue) => {
        const percentChange = ((currentValue - previousValue) / Math.abs(previousValue)) * 100;
        return new Intl.NumberFormat(CONFIG.LOCALE, {
            style: 'percent',
            minimumFractionDigits: 1,
            maximumFractionDigits: 1,
            signDisplay: 'always'
        }).format(percentChange / 100);
    },

    /**
     * Formate un pourcentage simple
     * @param {number} value - Valeur en pourcentage
     * @returns {string} - Pourcentage formaté
     */
    formatPercentage: (value) => {
        return new Intl.NumberFormat(CONFIG.LOCALE, {
            style: 'percent',
            minimumFractionDigits: 1,
            maximumFractionDigits: 1
        }).format(value / 100);
    },

    /**
     * Formate une date selon la locale française
     * @param {string} date - Date à formater
     * @returns {string} - Date formatée
     */
    formatDate: (date) => {
        return new Intl.DateTimeFormat(CONFIG.LOCALE).format(new Date(date));
    },

    /**
     * Récupère les initiales d'un vendeur
     * @param {string} sellerRef - Référence du vendeur
     * @returns {string} - Initiales en majuscules
     */
    getInitials: (sellerRef) => {
        return sellerRef?.substring(0, 2).toUpperCase() || 'XX';
    },

    /**
     * Valide les dates de session
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
     * Effectue un appel API générique
     * @param {string} endpoint - Point de terminaison
     * @returns {Promise<Object>} - Données de réponse
     */
    fetchApi: async (endpoint) => {
        try {
            const response = await fetch(`${CONFIG.API_BASE_URL}${endpoint}`, {
                headers: apiUtils.createHeaders()
            });
            await apiUtils.handleApiError(response);
            return response.json();
        } catch (error) {
            console.error(`Erreur API ${endpoint}:`, error);
            throw error;
        }
    },

    /**
     * Construit une URL avec paramètres de date
     * @param {string} endpoint - Point de terminaison
     * @returns {string} - URL complète avec paramètres
     */
    buildUrlWithDates: (endpoint) => {
        const params = new URLSearchParams({
            startDate: state.startDate,
            endDate: state.endDate
        });
        return `${endpoint}?${params}`;
    }
};

// ===== SERVICES API =====

/**
 * Service de gestion des données de synthèse
 */
const summaryService = {
    /**
     * Récupère les données de revenus de période
     * @returns {Promise<Object>} - Données de revenus
     */
    async getPeriodRevenue() {
        try {
            if (!utils.validateDates()) {
                throw new Error('Dates de période non définies');
            }
            return await apiUtils.fetchApi(apiUtils.buildUrlWithDates(CONFIG.ENDPOINTS.PERIOD_REVENUE));
        } catch (error) {
            console.error('Erreur lors de la récupération des revenus de période:', error);
            throw error;
        }
    },

    /**
     * Récupère les statistiques de devis
     * @returns {Promise<Object>} - Statistiques de devis
     */
    async getQuotationStats() {
        try {
            if (!utils.validateDates()) {
                throw new Error('Dates de période non définies');
            }
            return await apiUtils.fetchApi(apiUtils.buildUrlWithDates(CONFIG.ENDPOINTS.QUOTATIONS_STATS));
        } catch (error) {
            console.error('Erreur lors de la récupération des statistiques de devis:', error);
            throw error;
        }
    },

    /**
     * Récupère le taux de concrétisation précédent
     * @returns {Promise<number>} - Taux de concrétisation N-1
     */
    async getPreviousConcretization() {
        try {
            if (!utils.validateDates()) {
                throw new Error('Dates de période non définies');
            }
            return await apiUtils.fetchApi(apiUtils.buildUrlWithDates(CONFIG.ENDPOINTS.PREVIOUS_CONCRETIZATION));
        } catch (error) {
            console.error('Erreur lors de la récupération du taux précédent:', error);
            throw error;
        }
    },

    /**
     * Récupère toutes les données collaborateurs
     * @returns {Promise<Object>} - Toutes les données collaborateurs
     */
    async getCollaboratorData() {
        try {
            if (!utils.validateDates()) {
                throw new Error('Dates de période non définies');
            }

            const [frameStats, averageBaskets, revenueStats] = await Promise.all([
                apiUtils.fetchApi(apiUtils.buildUrlWithDates(CONFIG.ENDPOINTS.FRAME_STATS)),
                apiUtils.fetchApi(apiUtils.buildUrlWithDates(CONFIG.ENDPOINTS.AVERAGE_BASKETS)),
                apiUtils.fetchApi(apiUtils.buildUrlWithDates(CONFIG.ENDPOINTS.SELLER_STATS))
            ]);

            return { frameStats, averageBaskets, revenueStats };
        } catch (error) {
            console.error('Erreur lors de la récupération des données collaborateurs:', error);
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
     * Met à jour l'affichage des dates de période
     */
    updatePeriodDates() {
        if (!elements.periodDates || !utils.validateDates()) return;
        
        elements.periodDates.textContent = 
            `Du ${utils.formatDate(state.startDate)} au ${utils.formatDate(state.endDate)}`;
    },

    /**
     * Met à jour la date de dernière mise à jour
     */
    async updateLastUpdateDate() {
        if (!elements.lastUpdate) return;

        try {
            // Copie depuis l'élément de la navbar si disponible
            const navbarUpdate = document.getElementById('lastUpdate');
            if (navbarUpdate) {
                elements.lastUpdate.textContent = navbarUpdate.textContent;
            } else {
                // Si pas disponible, on utilise la fonction globale si elle existe
                if (typeof updateLastUpdateDate === 'function') {
                    await updateLastUpdateDate();
                    const navbarUpdateRetry = document.getElementById('lastUpdate');
                    if (navbarUpdateRetry) {
                        elements.lastUpdate.textContent = navbarUpdateRetry.textContent;
                    }
                }
            }
        } catch (error) {
            console.error('Erreur lors de la mise à jour de la date:', error);
        }
    },

    /**
     * Met à jour les données de revenus
     * @param {Object} data - Données de revenus
     */
    updateRevenueData(data) {
        if (!data || !elements.currentRevenue) return;

        // Mise à jour du CA actuel
        elements.currentRevenue.textContent = utils.formatCurrency(data.currentAmount || 0);

        // Gestion des données précédentes et des deltas
        const hasPreviousData = data.previousAmount !== null && 
                               data.previousAmount !== undefined && 
                               data.previousAmount !== 0;

        if (!hasPreviousData) {
            this.setUnknownValues(['previousRevenue', 'revenueDelta', 'revenueDeltaPercent']);
        } else {
            elements.previousRevenue.textContent = utils.formatCurrency(data.previousAmount);
            
            const delta = data.currentAmount - data.previousAmount;
            elements.revenueDelta.textContent = utils.formatDelta(delta);
            elements.revenueDeltaPercent.textContent = 
                utils.formatDeltaPercent(data.currentAmount, data.previousAmount);
        }
    },

    /**
     * Met à jour les taux de concrétisation
     * @param {Object} stats - Statistiques de devis
     * @param {number} previousRate - Taux précédent
     */
    updateConcretizationRates(stats, previousRate) {
        // Taux actuel
        if (elements.currentRate && stats?.concretizationRate !== undefined) {
            elements.currentRate.textContent = utils.formatPercentage(stats.concretizationRate);
        }

        // Taux précédent
        if (elements.previousRate) {
            if (previousRate !== undefined && previousRate !== null && previousRate !== 0) {
                elements.previousRate.textContent = utils.formatPercentage(previousRate);
            } else {
                elements.previousRate.textContent = 'Inconnu';
            }
        }
    },

    /**
     * Génère une ligne de collaborateur
     * @param {Object} seller - Données du vendeur
     * @param {Object} collaboratorData - Toutes les données collaborateurs
     * @returns {string} - HTML de la ligne
     */
    renderCollaboratorRow(seller, collaboratorData) {
        const sellerRef = seller.sellerRef;
        const { averageBaskets, revenueStats } = collaboratorData;
        
        // Recherche des données associées
        const p2Data = averageBaskets.find(b => b.sellerRef === sellerRef);
        const quoteData = state.quotationStats?.sellerStats?.find(s => s.sellerRef === sellerRef);
        const revenueData = revenueStats.find(r => r.sellerRef === sellerRef);

        // Calculs
        const bonus = (seller.premiumFrames || 0) * CONFIG.BONUS_PER_FRAME;
        const premiumPercent = seller.totalFrames > 0 
            ? ((seller.premiumFrames * 100) / seller.totalFrames).toFixed(1)
            : 0;

        return `
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
    },

    /**
     * Génère la ligne de total
     * @param {Object} totals - Totaux calculés
     * @returns {string} - HTML de la ligne de total
     */
    renderTotalRow(totals) {
        return `
            <tr class="fw-bold table-light">
                <td class="text-center">Total</td>
                <td class="text-center">${utils.formatCurrency(totals.totalBonus)}</td>
                <td class="text-center">${totals.globalPremiumPercent}%</td>
                <td class="text-center">${totals.totalP2Count}</td>
                <td class="text-center">${totals.totalP2Count > 0 ? utils.formatCurrency(totals.globalP2Average) : 'Aucun'}</td>
                <td class="text-center">${totals.totalQuotations}</td>
                <td class="text-center">${utils.formatPercentage(totals.globalConcretizationRate)}</td>
                <td class="text-center">${utils.formatCurrency(totals.totalRevenue)}</td>
            </tr>
        `;
    },

    /**
     * Calcule les totaux pour le tableau
     * @param {Array} frameStats - Statistiques de montures
     * @param {Object} collaboratorData - Données collaborateurs
     * @returns {Object} - Totaux calculés
     */
    calculateTotals(frameStats, collaboratorData) {
        const { averageBaskets } = collaboratorData;
        
        return frameStats.reduce((totals, seller) => {
            const sellerRef = seller.sellerRef;
            const p2Data = averageBaskets.find(b => b.sellerRef === sellerRef);
            const quoteData = state.quotationStats?.sellerStats?.find(s => s.sellerRef === sellerRef);
            const revenueData = collaboratorData.revenueStats.find(r => r.sellerRef === sellerRef);

            // Accumulation
            totals.totalBonus += (seller.premiumFrames || 0) * CONFIG.BONUS_PER_FRAME;
            totals.totalValidFrames += seller.totalFrames || 0;
            totals.totalPremiumPercent += seller.premiumFrames || 0;
            totals.totalP2Count += p2Data?.p2Count || 0;
            totals.totalP2Amount += (p2Data?.averageP2 || 0) * (p2Data?.p2Count || 0);
            totals.totalQuotations += quoteData?.totalQuotations || 0;
            totals.totalConcretized += quoteData?.concretizedQuotations || 0;
            totals.totalRevenue += revenueData?.amount || 0;

            return totals;
        }, {
            totalBonus: 0,
            totalValidFrames: 0,
            totalPremiumPercent: 0,
            totalP2Count: 0,
            totalP2Amount: 0,
            totalQuotations: 0,
            totalConcretized: 0,
            totalRevenue: 0,
            get globalPremiumPercent() {
                return this.totalValidFrames > 0 
                    ? ((this.totalPremiumPercent * 100) / this.totalValidFrames).toFixed(1)
                    : 0;
            },
            get globalP2Average() {
                return this.totalP2Count > 0 ? this.totalP2Amount / this.totalP2Count : 0;
            },
            get globalConcretizationRate() {
                return state.quotationStats?.concretizationRate || 0;
            }
        });
    },

    /**
     * Met à jour le tableau des collaborateurs
     * @param {Object} collaboratorData - Données des collaborateurs
     */
    updateCollaboratorsData(collaboratorData) {
        if (!elements.collaboratorsData || !collaboratorData.frameStats) return;

        const { frameStats } = collaboratorData;
        
        // Calcul des totaux
        const totals = this.calculateTotals(frameStats, collaboratorData);

        // Génération des lignes vendeurs
        const vendorRows = frameStats
            .map(seller => this.renderCollaboratorRow(seller, collaboratorData))
            .join('');

        // Mise à jour du tableau
        elements.collaboratorsData.innerHTML = vendorRows + this.renderTotalRow(totals);
    },

    /**
     * Définit des valeurs comme "Inconnu"
     * @param {Array} elementIds - IDs des éléments à mettre à jour
     */
    setUnknownValues(elementIds) {
        elementIds.forEach(id => {
            const element = elements[id];
            if (element) {
                element.textContent = 'Inconnu';
            }
        });
    },

    /**
     * Affiche un message d'erreur
     * @param {string} message - Message d'erreur
     */
    showError(message) {
        console.error('Erreur de synthèse:', message);
        // Optionnel : afficher une notification utilisateur
    }
};

// ===== CONTRÔLEUR PRINCIPAL =====

/**
 * Contrôleur principal de gestion de la synthèse
 */
const summaryController = {
    /**
     * Initialise le contrôleur
     */
    async init() {
        try {
            await this.loadAllData();
        } catch (error) {
            console.error('Erreur lors de l\'initialisation du contrôleur:', error);
            uiManager.showError('Erreur lors du chargement des données de synthèse');
        }
    },

    /**
     * Charge toutes les données nécessaires
     */
    async loadAllData() {
        try {
            // Mise à jour des dates et de la dernière mise à jour
            uiManager.updatePeriodDates();
            await uiManager.updateLastUpdateDate();

            // Chargement en parallèle des données principales
            const [revenueData, quotationStats, previousRate] = await Promise.all([
                summaryService.getPeriodRevenue(),
                summaryService.getQuotationStats(),
                summaryService.getPreviousConcretization()
            ]);

            // Stockage dans l'état
            state.revenueData = revenueData;
            state.quotationStats = quotationStats;

            // Mise à jour de l'interface
            uiManager.updateRevenueData(revenueData);
            uiManager.updateConcretizationRates(quotationStats, previousRate);

            // Chargement des données collaborateurs
            const collaboratorData = await summaryService.getCollaboratorData();
            uiManager.updateCollaboratorsData(collaboratorData);

        } catch (error) {
            console.error('Erreur lors du chargement des données:', error);
            throw error;
        }
    },

    /**
     * Rafraîchit toutes les données
     */
    async refreshData() {
        try {
            initializeState();
            await this.loadAllData();
        } catch (error) {
            console.error('Erreur lors du rafraîchissement:', error);
        }
    }
};

// ===== FONCTIONS GLOBALES (conservées pour compatibilité) =====

/**
 * Rafraîchit les données (fonction globale)
 */
function refreshSummary() {
    summaryController.refreshData();
}

/**
 * Exporte les données au format CSV (fonction future)
 */
function exportToCSV() {
    console.log('Export CSV non implémenté');
    alert('Fonctionnalité d\'export en cours de développement');
}