/**
 * Gestionnaire du tableau de bord Optitop
 * Charge et affiche les statistiques magasin et personnelles
 */

// ===== CONFIGURATION =====

const CONFIG = {
    API_BASE_URL: 'http://localhost:8080',
    ENDPOINTS: {
        // Endpoints factures
        PERIOD_REVENUE: '/api/invoices/period-revenue',
        TOTAL_STATS: '/api/invoices/total-stats',
        SELLER_STATS: '/api/invoices/seller-stats',
        AVERAGE_BASKETS: '/api/invoices/average-baskets',
        FRAME_STATS: '/api/invoices/frame-stats',
        
        // Endpoints devis
        QUOTATION_STATS: '/api/quotations/stats'
    },
    LOCALE: 'fr-FR',
    CURRENCY: 'EUR',
    BONUS_PER_FRAME: 5
};

// ===== ÉLÉMENTS DOM =====

const elements = {
    // Éléments magasin
    totalRevenue: null,
    storeConcretizationRate: null,
    storeAverageBasket: null,
    storeAverageP2: null,
    storeNbPremiumFrame: null,
    storeRatePremiumFrame: null,
    
    // Éléments personnels
    personalSection: null,
    personalRevenue: null,
    personalRevenuePercent: null,
    personalConcretizationRate: null,
    personalUnvalidatedQuotations: null,
    personalAverageBasketValue: null,
    personalAverageP2Value: null,
    personalRatePremiumFrame: null,
    personalBonusFrame: null
};

// ===== INITIALISATION =====

/**
 * Initialisation au chargement du DOM
 */
document.addEventListener('DOMContentLoaded', async () => {
    try {
        initializeElements();
        updatePersonalInfo();
        await loadAllData();
        applyUserRestrictions();
    } catch (error) {
        console.error('Erreur lors de l\'initialisation du dashboard:', error);
    }
});

/**
 * Initialise les références aux éléments DOM
 */
function initializeElements() {
    // Éléments magasin
    elements.totalRevenue = document.querySelector('#total-revenue');
    elements.storeConcretizationRate = document.querySelector('#store-concretization-rate');
    elements.storeAverageBasket = document.querySelector('#store-average-basket');
    elements.storeAverageP2 = document.querySelector('#store-average-p2');
    elements.storeNbPremiumFrame = document.querySelector('#store-nb-premium-frame');
    elements.storeRatePremiumFrame = document.querySelector('#store-rate-premium-frame');
    
    // Éléments personnels
    elements.personalSection = document.querySelector('#personal-section h4');
    elements.personalRevenue = document.querySelector('#personal-revenue');
    elements.personalRevenuePercent = document.querySelector('#personal-revenue-percent');
    elements.personalConcretizationRate = document.querySelector('#personal-concretization-rate');
    elements.personalUnvalidatedQuotations = document.querySelector('#personal-unvalidated-quotations');
    elements.personalAverageBasketValue = document.querySelector('#personal-average-basket-value');
    elements.personalAverageP2Value = document.querySelector('#personal-average-p2-value');
    elements.personalRatePremiumFrame = document.querySelector('#personal-rate-premium-frame');
    elements.personalBonusFrame = document.querySelector('#personal-bonus-frame');
}

// ===== UTILITAIRES =====

/**
 * Utilitaire pour les appels API
 */
const apiUtils = {
    /**
     * Effectue un appel API standardisé
     * @param {string} endpoint - Endpoint de CONFIG.ENDPOINTS
     * @param {Object} params - Paramètres de requête
     * @param {Object} options - Options pour fetch
     * @returns {Promise<Object>} - Réponse JSON
     */
    async fetchApi(endpoint, params = {}, options = {}) {
        try {
            // Construction de l'URL avec paramètres
            const url = new URL(`${CONFIG.API_BASE_URL}${endpoint}`);
            Object.keys(params).forEach(key => {
                if (params[key] !== null && params[key] !== undefined) {
                    url.searchParams.append(key, params[key]);
                }
            });

            const response = await fetch(url.toString(), {
                ...options,
                headers: {
                    'Content-Type': 'application/json',
                    ...options.headers
                }
            });
            
            if (!response.ok) {
                throw new Error(`Erreur API: ${response.status}`);
            }
            
            return await response.json();
        } catch (error) {
            console.error(`Erreur lors de l'appel API ${endpoint}:`, error);
            throw error;
        }
    }
};

/**
 * Utilitaires de formatage
 */
const formatUtils = {
    /**
     * Formate un montant en devise
     * @param {number} amount - Montant à formater
     * @param {number} decimals - Nombre de décimales (défaut: 0)
     * @returns {string} - Montant formaté
     */
    formatCurrency(amount, decimals = 0) {
        if (amount === null || amount === undefined) return '-';
        return new Intl.NumberFormat(CONFIG.LOCALE, {
            style: 'currency',
            currency: CONFIG.CURRENCY,
            minimumFractionDigits: decimals,
            maximumFractionDigits: decimals
        }).format(amount);
    },

    /**
     * Formate un pourcentage
     * @param {number} value - Valeur à formater
     * @returns {string} - Pourcentage formaté
     */
    formatPercent(value) {
        return new Intl.NumberFormat(CONFIG.LOCALE, {
            style: 'percent',
            minimumFractionDigits: 1,
            maximumFractionDigits: 1
        }).format(value / 100);
    },

    /**
     * Calcule un pourcentage
     * @param {number} total - Total
     * @param {number} part - Partie
     * @returns {number} - Pourcentage calculé
     */
    calculatePercentage(total, part) {
        return total > 0 ? (part / total * 100).toFixed(1) : 0;
    }
};

/**
 * Utilitaires de session
 */
const sessionUtils = {
    /**
     * Récupère les dates de session
     * @returns {Object} - Dates de début et fin
     */
    getSessionDates() {
        return {
            startDate: sessionStorage.getItem('startDate'),
            endDate: sessionStorage.getItem('endDate')
        };
    },

    /**
     * Récupère les données utilisateur de session
     * @returns {Object|null} - Données utilisateur
     */
    getUserData() {
        const userData = sessionStorage.getItem('user');
        return userData ? JSON.parse(userData) : null;
    }
};

// ===== MISE À JOUR INTERFACE =====

/**
 * Met à jour les informations personnelles affichées
 */
function updatePersonalInfo() {
    const user = sessionUtils.getUserData();
    if (user?.firstname && elements.personalSection) {
        elements.personalSection.textContent = user.firstname;
    }
}

/**
 * Applique les restrictions selon le rôle utilisateur
 */
function applyUserRestrictions() {
    const user = sessionUtils.getUserData();
    if (user?.role === 'collaborator') {
        const shopLinks = document.querySelectorAll('#shop-selection a');
        shopLinks.forEach(link => {
            link.classList.add('collaborator-disabled-link');
        });
    }
}

// ===== CHARGEMENT DES DONNÉES =====

/**
 * Charge toutes les données du tableau de bord
 */
async function loadAllData() {
    const { startDate, endDate } = sessionUtils.getSessionDates();
    
    if (!startDate || !endDate) {
        console.warn('Dates manquantes dans le sessionStorage');
        return;
    }

    // Chargement en parallèle des données magasin
    await Promise.allSettled([
        loadStoreRevenue(),
        loadStoreConcretizationRate(),
        loadStoreAverageBaskets(),
        loadStoreFrameStats()
    ]);

    // Chargement en parallèle des données personnelles
    await Promise.allSettled([
        loadPersonalRevenue(),
        loadPersonalQuotationStats(),
        loadPersonalAverageBaskets(),
        loadPersonalFrameStats()
    ]);
}

/**
 * Charge et affiche le chiffre d'affaires total
 */
async function loadStoreRevenue() {
    try {
        const { startDate, endDate } = sessionUtils.getSessionDates();
        const data = await apiUtils.fetchApi(CONFIG.ENDPOINTS.PERIOD_REVENUE, { startDate, endDate });
        
        if (elements.totalRevenue) {
            elements.totalRevenue.textContent = data.currentAmount ? 
                formatUtils.formatCurrency(data.currentAmount) : '-';
        }
    } catch (error) {
        console.error('Erreur lors du chargement du CA:', error);
        if (elements.totalRevenue) {
            elements.totalRevenue.textContent = '-';
        }
    }
}

/**
 * Charge et affiche le taux de concrétisation magasin
 */
async function loadStoreConcretizationRate() {
    try {
        const { startDate, endDate } = sessionUtils.getSessionDates();
        const data = await apiUtils.fetchApi(CONFIG.ENDPOINTS.QUOTATION_STATS, { startDate, endDate });
        
        if (elements.storeConcretizationRate && data.concretizationRate !== undefined) {
            const formattedRate = formatUtils.formatPercent(data.concretizationRate);
            elements.storeConcretizationRate.textContent = formattedRate;
        }
    } catch (error) {
        console.error('Erreur lors du chargement du taux de concrétisation:', error);
        if (elements.storeConcretizationRate) {
            elements.storeConcretizationRate.textContent = '-';
        }
    }
}

/**
 * Charge et affiche les paniers moyens magasin
 */
async function loadStoreAverageBaskets() {
    try {
        const { startDate, endDate } = sessionUtils.getSessionDates();
        const data = await apiUtils.fetchApi(CONFIG.ENDPOINTS.TOTAL_STATS, { startDate, endDate });
        
        if (elements.storeAverageBasket && data.averageBasket !== undefined) {
            elements.storeAverageBasket.textContent = formatUtils.formatCurrency(data.averageBasket, 1);
        }
        
        if (elements.storeAverageP2 && data.averageP2 !== undefined) {
            elements.storeAverageP2.textContent = formatUtils.formatCurrency(data.averageP2, 1);
        }
    } catch (error) {
        console.error('Erreur lors du chargement des paniers moyens:', error);
    }
}

/**
 * Charge et affiche les statistiques des montures primées magasin
 */
async function loadStoreFrameStats() {
    try {
        const { startDate, endDate } = sessionUtils.getSessionDates();
        const stats = await apiUtils.fetchApi(CONFIG.ENDPOINTS.FRAME_STATS, { startDate, endDate });
        const totals = calculateFrameTotals(stats);
        
        if (elements.storeNbPremiumFrame) {
            elements.storeNbPremiumFrame.textContent = totals.totalPremiumFrames;
        }
        
        if (elements.storeRatePremiumFrame) {
            const percentage = formatUtils.calculatePercentage(totals.totalFrames, totals.totalPremiumFrames);
            elements.storeRatePremiumFrame.textContent = `${percentage}%`;
        }
    } catch (error) {
        console.error('Erreur lors du chargement des stats de montures:', error);
    }
}

/**
 * Charge et affiche le chiffre d'affaires personnel
 */
async function loadPersonalRevenue() {
    try {
        const { startDate, endDate } = sessionUtils.getSessionDates();
        const user = sessionUtils.getUserData();

        if (!user?.seller_ref) return;

        const [personalData, storeData] = await Promise.all([
            apiUtils.fetchApi(CONFIG.ENDPOINTS.SELLER_STATS, { startDate, endDate }),
            apiUtils.fetchApi(CONFIG.ENDPOINTS.PERIOD_REVENUE, { startDate, endDate })
        ]);

        const sellerStats = personalData.find(s => s.sellerRef === user.seller_ref) || {};

        if (elements.personalRevenue) {
            elements.personalRevenue.textContent = formatUtils.formatCurrency(sellerStats.amount || 0);
        }

        if (elements.personalRevenuePercent && storeData.currentAmount) {
            const percentage = ((sellerStats.amount || 0) / storeData.currentAmount * 100).toFixed(1);
            elements.personalRevenuePercent.textContent = `${percentage}%`;
        }
    } catch (error) {
        console.error('Erreur lors du chargement du CA personnel:', error);
    }
}

/**
 * Charge et affiche les statistiques des devis personnels
 */
async function loadPersonalQuotationStats() {
    try {
        const { startDate, endDate } = sessionUtils.getSessionDates();
        const user = sessionUtils.getUserData();

        if (!user?.seller_ref) return;

        const stats = await apiUtils.fetchApi(CONFIG.ENDPOINTS.QUOTATION_STATS, { startDate, endDate });
        const userStats = stats.sellerStats.find(s => s.sellerRef === user.seller_ref) || {};

        if (elements.personalConcretizationRate && userStats.concretizationRate !== undefined) {
            elements.personalConcretizationRate.textContent = `${userStats.concretizationRate.toFixed(1)}%`;
        }

        if (elements.personalUnvalidatedQuotations && userStats.unvalidatedQuotations !== undefined) {
            elements.personalUnvalidatedQuotations.textContent = userStats.unvalidatedQuotations;
        }
    } catch (error) {
        console.error('Erreur lors du chargement des stats de devis personnels:', error);
    }
}

/**
 * Charge et affiche les paniers moyens personnels
 */
async function loadPersonalAverageBaskets() {
    try {
        const { startDate, endDate } = sessionUtils.getSessionDates();
        const user = sessionUtils.getUserData();

        if (!user?.seller_ref) return;

        const stats = await apiUtils.fetchApi(CONFIG.ENDPOINTS.AVERAGE_BASKETS, { startDate, endDate });
        const sellerStats = stats.find(s => s.sellerRef === user.seller_ref) || {};

        if (elements.personalAverageBasketValue) {
            elements.personalAverageBasketValue.textContent = formatUtils.formatCurrency(sellerStats.averageBasket || 0, 1);
        }

        if (elements.personalAverageP2Value) {
            elements.personalAverageP2Value.textContent = formatUtils.formatCurrency(sellerStats.averageP2 || 0, 1);
        }
    } catch (error) {
        console.error('Erreur lors du chargement des paniers moyens personnels:', error);
    }
}

/**
 * Charge et affiche les statistiques personnelles des montures primées
 */
async function loadPersonalFrameStats() {
    try {
        const { startDate, endDate } = sessionUtils.getSessionDates();
        const user = sessionUtils.getUserData();

        if (!user?.seller_ref) return;

        const stats = await apiUtils.fetchApi(CONFIG.ENDPOINTS.FRAME_STATS, { startDate, endDate });
        const sellerStats = stats.find(s => s.sellerRef === user.seller_ref) || {};
        
        if (elements.personalRatePremiumFrame) {
            const percentage = formatUtils.calculatePercentage(sellerStats.totalFrames || 0, sellerStats.premiumFrames || 0);
            elements.personalRatePremiumFrame.textContent = `${percentage}%`;
        }

        if (elements.personalBonusFrame) {
            const bonus = (sellerStats.premiumFrames || 0) * CONFIG.BONUS_PER_FRAME;
            elements.personalBonusFrame.textContent = formatUtils.formatCurrency(bonus);
        }
    } catch (error) {
        console.error('Erreur lors du chargement des stats personnelles de montures:', error);
    }
}

// ===== FONCTIONS UTILITAIRES =====

/**
 * Calcule les totaux des statistiques de montures
 * @param {Array} stats - Statistiques par vendeur
 * @returns {Object} - Totaux calculés
 */
function calculateFrameTotals(stats) {
    return stats.reduce((acc, seller) => {
        acc.totalFrames += seller.totalFrames || 0;
        acc.totalPremiumFrames += seller.premiumFrames || 0;
        return acc;
    }, { totalFrames: 0, totalPremiumFrames: 0 });
}
