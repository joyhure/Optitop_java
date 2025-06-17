/**
 * Gestionnaire du chiffre d'affaires Optitop
 * 
 * Gère les opérations liées au chiffre d'affaires :
 * - Affichage des revenus mensuels et annuels
 * - Calcul et affichage des écarts avec l'année précédente
 * - Visualisation des statistiques par vendeur
 * - Mise à jour des données de période sélectionnée
 */

// ===== CONFIGURATION =====

const CONFIG = {
    API_BASE_URL: 'http://localhost:8080/api',
    ENDPOINTS: {
        YEARS: '/invoices/years',
        MONTHLY_REVENUE: '/invoices/monthly-revenue',
        PERIOD_REVENUE: '/invoices/period-revenue',
        SELLER_STATS: '/invoices/seller-stats'
    },
    LOCALE: 'fr-FR',
    CURRENCY: 'EUR'
};

// ===== ÉLÉMENTS DOM =====

const elements = {
    mainRevenue: null,
    revenueSections: null,
    totalRevenue: null,
    totalDeltaPercent: null,
    sellersRevenueBody: null
};

// ===== ÉTAT DE L'APPLICATION =====

const state = {
    revenueCache: new Map(),
    availableYears: [],
    startDate: null,
    endDate: null,
    currentYear: new Date().getFullYear(),
    currentMonth: new Date().getMonth() + 1
};

// ===== INITIALISATION =====

/**
 * Initialisation au chargement du DOM
 */
document.addEventListener('DOMContentLoaded', () => {
    try {
        initializeElements();
        initializeState();
        revenueController.init();
    } catch (error) {
        console.error('Erreur lors de l\'initialisation du chiffre d\'affaires:', error);
    }
});

/**
 * Initialise les références aux éléments DOM
 */
function initializeElements() {
    elements.mainRevenue = document.querySelector('#main-revenue');
    elements.revenueSections = document.querySelector('#revenue-sections');
    elements.totalRevenue = document.querySelector('#total-revenue');
    elements.totalDeltaPercent = document.querySelector('#total-delta-percent');
    elements.sellersRevenueBody = document.querySelector('#sellers-revenue-body');
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
     * @param {boolean} isFutureMonth - Si c'est un mois futur
     * @returns {string} - Delta formaté
     */
    formatDelta: (delta, isFutureMonth = false) => {
        if (isFutureMonth) return 'À venir';
        if (delta === undefined || delta === null) return 'Indéfini';
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
     * @param {boolean} isFutureMonth - Si c'est un mois futur
     * @returns {string} - Pourcentage formaté
     */
    formatDeltaPercent: (currentValue, previousValue, isFutureMonth = false) => {
        if (isFutureMonth) return 'À venir';
        if (!currentValue || !previousValue || previousValue === 0) return 'Indéfini';
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
     * Récupère les initiales d'un vendeur
     * @param {string} sellerRef - Référence du vendeur
     * @returns {string} - Initiales en majuscules
     */
    getInitials: (sellerRef) => {
        return sellerRef ? sellerRef.substring(0, 2).toUpperCase() : 'XX';
    },

    /**
     * Vérifie si un mois est dans le futur
     * @param {number} year - Année à vérifier
     * @param {number} month - Mois à vérifier
     * @returns {boolean} - true si le mois est futur
     */
    isFutureMonth: (year, month) => {
        return year === state.currentYear && month > state.currentMonth;
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
    }
};

// ===== SERVICES API =====

/**
 * Service de gestion des revenus
 */
const revenueService = {
    /**
     * Récupère les années disponibles
     * @returns {Promise<Array>} - Liste des années
     */
    async getAvailableYears() {
        try {
            return await apiUtils.fetchApi(CONFIG.ENDPOINTS.YEARS);
        } catch (error) {
            console.error('Erreur lors de la récupération des années:', error);
            throw error;
        }
    },

    /**
     * Récupère les revenus mensuels pour une année
     * @param {number} year - Année demandée
     * @returns {Promise<Object>} - Revenus par mois
     */
    async getMonthlyRevenue(year) {
        try {
            return await apiUtils.fetchApi(`${CONFIG.ENDPOINTS.MONTHLY_REVENUE}/${year}`);
        } catch (error) {
            console.error(`Erreur lors de la récupération des revenus ${year}:`, error);
            throw error;
        }
    },

    /**
     * Récupère les revenus pour une période
     * @param {string} startDate - Date de début
     * @param {string} endDate - Date de fin
     * @returns {Promise<Object>} - Revenus de période
     */
    async getPeriodRevenue(startDate, endDate) {
        try {
            const params = new URLSearchParams({ startDate, endDate });
            return await apiUtils.fetchApi(`${CONFIG.ENDPOINTS.PERIOD_REVENUE}?${params}`);
        } catch (error) {
            console.error('Erreur lors de la récupération des revenus de période:', error);
            throw error;
        }
    },

    /**
     * Récupère les statistiques par vendeur
     * @param {string} startDate - Date de début
     * @param {string} endDate - Date de fin
     * @returns {Promise<Array>} - Statistiques par vendeur
     */
    async getSellerStats(startDate, endDate) {
        try {
            const params = new URLSearchParams({ startDate, endDate });
            return await apiUtils.fetchApi(`${CONFIG.ENDPOINTS.SELLER_STATS}?${params}`);
        } catch (error) {
            console.error('Erreur lors de la récupération des statistiques vendeur:', error);
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
     * Génère une section d'année
     * @param {number} year - Année à afficher
     * @param {boolean} isFirst - Si c'est la première section
     * @returns {string} - HTML de la section
     */
    generateYearSection(year, isFirst = false) {
        return `
            <section id="table-revenue-${year}" class="table-responsive small w-100">
                <h4 class="py-3">
                    <a class="text-decoration-none dropdown-toggle text-dark nav-link" 
                       data-bs-toggle="collapse" 
                       href="#collapseRevenue${year}" 
                       role="button" 
                       aria-expanded="${isFirst ? 'true' : 'false'}" 
                       aria-controls="collapseRevenue${year}">
                        ${year}
                    </a>
                </h4>
                <div class="collapse ${isFirst ? 'show' : ''}" id="collapseRevenue${year}">
                    <table class="table table-striped table-sm">
                        <thead>
                            <tr>
                                <th scope="col" class="table-col-w4 text-center align-middle">${year}</th>
                                <th scope="col" class="table-col-w8 text-center align-middle">Janv.</th>
                                <th scope="col" class="table-col-w8 text-center align-middle">Fév.</th>
                                <th scope="col" class="table-col-w8 text-center align-middle">Mars</th>
                                <th scope="col" class="table-col-w8 text-center align-middle">Avril</th>
                                <th scope="col" class="table-col-w8 text-center align-middle">Mai</th>
                                <th scope="col" class="table-col-w8 text-center align-middle">Juin</th>
                                <th scope="col" class="table-col-w8 text-center align-middle">Juill.</th>
                                <th scope="col" class="table-col-w8 text-center align-middle">Août</th>
                                <th scope="col" class="table-col-w8 text-center align-middle">Sept.</th>
                                <th scope="col" class="table-col-w8 text-center align-middle">Oct.</th>
                                <th scope="col" class="table-col-w8 text-center align-middle">Nov.</th>
                                <th scope="col" class="table-col-w8 text-center align-middle">Déc.</th>
                                <th scope="col" class="table-col-w8 text-center align-middle">Année</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td class="text-center align-middle fw-bold">CA</td>
                                ${Array(13).fill('<td class="text-center align-middle">-</td>').join('')}
                            </tr>
                            <tr>
                                <td class="text-center align-middle fw-bold">Delta n-1</td>
                                ${Array(13).fill('<td class="text-center align-middle">-</td>').join('')}
                            </tr>
                            <tr>
                                <td class="text-center align-middle fw-bold">Delta %</td>
                                ${Array(13).fill('<td class="text-center align-middle">-</td>').join('')}
                            </tr>
                        </tbody>
                    </table>
                </div>
            </section>
        `;
    },

    /**
     * Affiche les sections d'années
     * @param {Array} years - Liste des années
     */
    displayYearSections(years) {
        if (!elements.revenueSections || !years.length) return;
        const sections = years.map((year, index) => 
            this.generateYearSection(year, index === 0)
        ).join('');
        elements.revenueSections.innerHTML = sections;
    },

    /**
     * Met à jour les lignes de tableau pour une année
     * @param {number} year - Année à mettre à jour
     */
    updateTableRows(year) {
        const tableBody = document.querySelector(`#collapseRevenue${year} tbody`);
        if (!tableBody) return;

        const rows = tableBody.querySelectorAll('tr');
        const [revenueRow, deltaRow, deltaPercentRow] = rows;

        this.updateRevenueRow(year, revenueRow);
        this.updateDeltaRow(year, deltaRow);
        this.updateDeltaPercentRow(year, deltaPercentRow);
    },

    /**
     * Met à jour la ligne des revenus
     * @param {number} year - Année
     * @param {HTMLElement} row - Ligne à mettre à jour
     */
    updateRevenueRow(year, row) {
        if (!row) return;
        const cells = row.querySelectorAll('td');
        const yearData = state.revenueCache.get(year) || {};
        
        let yearTotal = 0;
        for (let month = 1; month <= 12; month++) {
            const revenue = yearData[month] || 0;
            const isFutureMonth = utils.isFutureMonth(year, month);
            
            if (!isFutureMonth) {
                yearTotal += revenue;
                cells[month].textContent = utils.formatCurrency(revenue);
            } else {
                cells[month].textContent = 'À venir';
            }
        }
        cells[13].textContent = utils.formatCurrency(yearTotal);
    },

    /**
     * Met à jour la ligne des deltas
     * @param {number} year - Année
     * @param {HTMLElement} row - Ligne à mettre à jour
     */
    updateDeltaRow(year, row) {
        if (!row) return;
        
        const cells = row.querySelectorAll('td');
        const yearData = state.revenueCache.get(year) || {};
        const previousYearData = state.revenueCache.get(year - 1) || {};
        
        let yearDeltaTotal = 0;
        let hasValidPreviousData = false;
        
        for (let month = 1; month <= 12; month++) {
            const { current, previous } = this.hasValidDataForYear(year, month);
            const currentRevenue = current ? yearData[month] : 0;
            const previousRevenue = previous ? previousYearData[month] : 0;
            const isFutureMonth = utils.isFutureMonth(year, month);
            
            let delta = currentRevenue - previousRevenue;
            
            if (isFutureMonth) {
                cells[month].textContent = 'À venir';
            } else if (!current || !previous) {
                cells[month].textContent = 'Inconnu';
            } else {
                hasValidPreviousData = true;
                cells[month].textContent = utils.formatDelta(delta);
                yearDeltaTotal += delta;
            }
        }

        cells[13].textContent = hasValidPreviousData ? 
            utils.formatDelta(yearDeltaTotal) : 'Inconnu';
    },

    /**
     * Met à jour la ligne des pourcentages de delta
     * @param {number} year - Année
     * @param {HTMLElement} row - Ligne à mettre à jour
     */
    updateDeltaPercentRow(year, row) {
        if (!row) return;
        
        const cells = row.querySelectorAll('td');
        const currentYearData = state.revenueCache.get(year) || {};
        const previousYearData = state.revenueCache.get(year - 1) || {};
        
        let yearTotalCurrent = 0;
        let yearTotalPrevious = 0;
        let hasValidPreviousData = false;
        
        for (let month = 1; month <= 12; month++) {
            const currentRevenue = currentYearData[month] || 0;
            const previousRevenue = previousYearData[month] || 0;
            const isFutureMonth = utils.isFutureMonth(year, month);
            
            if (!isFutureMonth) {
                yearTotalCurrent += currentRevenue;
                yearTotalPrevious += previousRevenue;
            }

            if (isFutureMonth) {
                cells[month].textContent = 'À venir';
            } else if (!previousYearData[month]) {
                cells[month].textContent = 'Inconnu';
            } else {
                hasValidPreviousData = true;
                cells[month].textContent = utils.formatDeltaPercent(currentRevenue, previousRevenue);
            }
        }

        cells[13].textContent = hasValidPreviousData ? 
            utils.formatDeltaPercent(yearTotalCurrent, yearTotalPrevious) : 
            'Inconnu';
    },

    /**
     * Vérifie la validité des données pour une année/mois
     * @param {number} year - Année
     * @param {number} month - Mois
     * @returns {Object} - État de validité des données
     */
    hasValidDataForYear(year, month) {
        const currentYearData = state.revenueCache.get(year);
        const previousYearData = state.revenueCache.get(year - 1);
        
        return {
            current: currentYearData && currentYearData[month] !== undefined,
            previous: previousYearData && previousYearData[month] !== undefined
        };
    },

    /**
     * Met à jour la carte de résumé
     * @param {Object} data - Données de période
     */
    updateSummaryCard(data) {
        if (!elements.totalRevenue || !elements.totalDeltaPercent) return;

        if (!data || data.currentAmount === null || data.previousAmount === null) {
            elements.totalRevenue.textContent = '-';
            elements.totalDeltaPercent.textContent = '-';
            return;
        }

        const currentAmount = data.currentAmount || 0;
        const previousAmount = data.previousAmount || 0;

        elements.totalRevenue.textContent = utils.formatCurrency(currentAmount);
        elements.totalDeltaPercent.textContent = utils.formatDeltaPercent(currentAmount, previousAmount);
    },

    /**
     * Met à jour le tableau des vendeurs
     * @param {Array} data - Données des vendeurs
     */
    updateSellersTable(data) {
        if (!elements.sellersRevenueBody || !Array.isArray(data)) return;
        
        // Tri des données par initiales vendeurs
        const sortedData = [...data].sort((a, b) => 
            utils.getInitials(a.sellerRef).localeCompare(utils.getInitials(b.sellerRef))
        );
        
        // Génération du HTML
        elements.sellersRevenueBody.innerHTML = sortedData
            .map(seller => `
                <tr>
                    <td class="text-center align-middle">${utils.getInitials(seller.sellerRef)}</td>
                    <td class="text-center align-middle">${utils.formatCurrency(seller.amount)}</td>
                    <td class="text-center align-middle">${utils.formatPercentage(seller.percentage)}</td>
                </tr>
            `)
            .join('');
    },

    /**
     * Affiche un message d'erreur
     * @param {string} message - Message d'erreur
     */
    showError(message) {
        if (elements.mainRevenue) {
            const errorDiv = document.createElement('div');
            errorDiv.className = 'alert alert-danger';
            errorDiv.textContent = message;
            elements.mainRevenue.prepend(errorDiv);
        }
    }
};

// ===== CONTRÔLEUR PRINCIPAL =====

/**
 * Contrôleur principal de gestion du chiffre d'affaires
 */
const revenueController = {
    /**
     * Initialise le contrôleur
     */
    async init() {
        try {
            await this.loadInitialData();
        } catch (error) {
            console.error('Erreur lors de l\'initialisation du contrôleur:', error);
            uiManager.showError('Les données sont temporairement indisponibles');
        }
    },

    /**
     * Charge les données initiales
     */
    async loadInitialData() {
        try {
            // Récupération des années
            const years = await revenueService.getAvailableYears();
            if (!years?.length) throw new Error('Aucune année disponible');

            state.availableYears = years;
            uiManager.displayYearSections(years);

            // Chargement des données mensuelles pour l'année courante
            const monthlyData = await revenueService.getMonthlyRevenue(state.currentYear);
            if (monthlyData) {
                state.revenueCache.set(state.currentYear, monthlyData);
            }

            // Chargement des données de période si disponibles
            if (utils.validateDates()) {
                const [periodData, sellerStats] = await Promise.all([
                    revenueService.getPeriodRevenue(state.startDate, state.endDate),
                    revenueService.getSellerStats(state.startDate, state.endDate)
                ]);

                uiManager.updateSummaryCard(periodData);
                uiManager.updateSellersTable(sellerStats);
            } else {
                uiManager.updateSummaryCard({ currentAmount: null, previousAmount: null });
                uiManager.updateSellersTable([]);
            }

            // Chargement des données pour toutes les années
            await this.loadAllRevenueData(years);

        } catch (error) {
            console.error('Erreur lors du chargement des données:', error);
            throw error;
        }
    },

    /**
     * Charge les données de revenus pour toutes les années
     * @param {Array} years - Liste des années
     */
    async loadAllRevenueData(years) {
        try {
            for (const year of years) {
                try {
                    const [currentYear, previousYear] = await Promise.all([
                        revenueService.getMonthlyRevenue(year),
                        revenueService.getMonthlyRevenue(year - 1)
                    ]);

                    if (currentYear) state.revenueCache.set(year, currentYear);
                    if (previousYear) state.revenueCache.set(year - 1, previousYear);
                    
                    uiManager.updateTableRows(year);
                } catch (error) {
                    console.error(`Erreur pour l'année ${year}:`, error);
                }
            }
        } catch (error) {
            console.error('Erreur lors du chargement des données:', error);
        }
    },

    /**
     * Rafraîchit toutes les données
     */
    async refreshData() {
        try {
            initializeState();
            await this.loadInitialData();
        } catch (error) {
            console.error('Erreur lors du rafraîchissement:', error);
        }
    }
};

// ===== FONCTIONS GLOBALES (conservées pour compatibilité) =====

/**
 * Rafraîchit les données (fonction globale)
 */
function refreshRevenue() {
    revenueController.refreshData();
}

/**
 * Exporte les données au format CSV (fonction future)
 */
function exportToCSV() {
    console.log('Export CSV non implémenté');
    alert('Fonctionnalité d\'export en cours de développement');
}