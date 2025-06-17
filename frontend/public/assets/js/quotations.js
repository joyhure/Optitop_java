/**
 * Gestionnaire de gestion des devis optiques Optitop
 * 
 * Gère les opérations liées aux devis optiques :
 * - Affichage et tri des devis non validés
 * - Validation et mise à jour des actions sur devis
 * - Sauvegarde des commentaires et actions
 * - Affichage des statistiques de concrétisation
 */

// ===== CONFIGURATION =====

const CONFIG = {
    API_BASE_URL: 'http://localhost:8080/api',
    ENDPOINTS: {
        UNVALIDATED_QUOTATIONS: '/quotations/unvalidated',
        QUOTATION_ACTIONS: '/quotations/actions',
        BATCH_UPDATE: '/quotations/batch-update',
        QUOTATION_STATS: '/quotations/stats'
    },
    SORT_FIELDS: ['date', 'name', 'client'],
    LOCALE: 'fr-FR'
};

// ===== ÉLÉMENTS DOM =====

const elements = {
    tbody: null,
    saveButton: null,
    saveContainer: null,
    successToast: null,
    errorToast: null,
    sortIcons: null,
    collaboratorsTable: null,
    concretizationRate: null,
    quotationsNumbers: null,
    sellerStatsBody: null
};

// ===== ÉTAT DE L'APPLICATION =====

const state = {
    userSession: null,
    availableActions: {},
    currentSort: {
        field: 'date',
        order: 'desc'
    },
    startDate: null,
    endDate: null,
    quotations: []
};

// ===== INITIALISATION =====

/**
 * Initialisation au chargement du DOM
 */
document.addEventListener('DOMContentLoaded', () => {
    try {
        initializeElements();
        initializeState();
        quotationsController.init();
    } catch (error) {
        console.error('Erreur lors de l\'initialisation des devis:', error);
    }
});

/**
 * Initialise les références aux éléments DOM
 */
function initializeElements() {
    elements.tbody = document.querySelector('#table-quotations-section table tbody');
    elements.saveButton = document.querySelector('#save-changes-button');
    elements.saveContainer = document.querySelector('.save-button-container');
    elements.successToast = document.querySelector('#successToast');
    elements.errorToast = document.querySelector('#errorToast');
    elements.sortIcons = document.querySelectorAll('.sort-icon');
    elements.collaboratorsTable = document.querySelector('#card-table-collaborators');
    elements.concretizationRate = document.querySelector('#store-concretization-rate');
    elements.quotationsNumbers = document.querySelector('#quotations-numbers');
    elements.sellerStatsBody = document.querySelector('#seller-stats-tbody');
}

/**
 * Initialise l'état de l'application
 */
function initializeState() {
    state.userSession = JSON.parse(sessionStorage.getItem('user'));
    state.startDate = sessionStorage.getItem('startDate');
    state.endDate = sessionStorage.getItem('endDate');
}

// ===== UTILITAIRES =====

/**
 * Utilitaires généraux
 */
const utils = {
    /**
     * Formate une date selon la locale française
     * @param {string} dateString - Date à formater
     * @returns {string} - Date formatée
     */
    formatDate: (dateString) => {
        return new Date(dateString).toLocaleDateString(CONFIG.LOCALE);
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
     * Parse une date française en objet Date
     * @param {string} dateStr - Date au format DD/MM/YYYY
     * @returns {Date} - Objet Date
     */
    parseFrenchDate: (dateStr) => {
        const [day, month, year] = dateStr.split('/').map(Number);
        return new Date(year, month - 1, day);
    },

    /**
     * Vérifie si l'utilisateur a accès aux statistiques avancées
     * @returns {boolean} - true si autorisé
     */
    hasStatsAccess: () => {
        const role = state.userSession?.role?.toLowerCase();
        return ['admin', 'manager', 'supermanager'].includes(role);
    },

    /**
     * Vérifie la validité des dates de session
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
     * Construit l'URL avec paramètres de date et utilisateur
     * @param {string} endpoint - Point de terminaison API
     * @returns {string} - URL complète avec paramètres
     */
    buildUrlWithParams: (endpoint) => {
        const params = new URLSearchParams({
            startDate: state.startDate,
            endDate: state.endDate
        });

        // Ajouter les paramètres utilisateur si c'est un collaborateur
        const userRole = state.userSession?.role?.toLowerCase();
        if (userRole === 'collaborator') {
            params.append('userRole', userRole);
            params.append('userSellerRef', state.userSession?.seller_ref);
        }

        return `${CONFIG.API_BASE_URL}${endpoint}?${params}`;
    },

    /**
     * Affiche une notification toast
     * @param {string} type - Type de toast ('success' ou 'error')
     */
    showToast: (type = 'success') => {
        const toastElement = type === 'success' ? elements.successToast : elements.errorToast;
        if (toastElement) {
            const toast = new bootstrap.Toast(toastElement);
            toast.show();
        }
    }
};

// ===== SERVICES API =====

/**
 * Service de gestion des devis
 */
const quotationsService = {
    /**
     * Récupère les devis non validés
     * @returns {Promise<Array>} - Liste des devis non validés
     */
    async getUnvalidatedQuotations() {
        try {
            if (!utils.validateDates()) {
                throw new Error('Dates de période non définies');
            }

            const response = await fetch(apiUtils.buildUrlWithParams(CONFIG.ENDPOINTS.UNVALIDATED_QUOTATIONS), {
                headers: apiUtils.createHeaders()
            });
            
            await apiUtils.handleApiError(response);
            return response.json();
        } catch (error) {
            console.error('Erreur lors de la récupération des devis:', error);
            throw error;
        }
    },

    /**
     * Récupère les actions disponibles pour les devis
     * @returns {Promise<Object>} - Dictionnaire des actions disponibles
     */
    async getAvailableActions() {
        try {
            const response = await fetch(`${CONFIG.API_BASE_URL}${CONFIG.ENDPOINTS.QUOTATION_ACTIONS}`, {
                headers: apiUtils.createHeaders()
            });
            
            await apiUtils.handleApiError(response);
            return response.json();
        } catch (error) {
            console.error('Erreur lors de la récupération des actions:', error);
            throw error;
        }
    },

    /**
     * Sauvegarde les modifications en lot
     * @param {Array} updates - Liste des modifications à sauvegarder
     * @returns {Promise<boolean>} - true si succès
     */
    async saveBatchUpdates(updates) {
        try {
            const response = await fetch(`${CONFIG.API_BASE_URL}${CONFIG.ENDPOINTS.BATCH_UPDATE}`, {
                method: 'PUT',
                headers: apiUtils.createHeaders(),
                body: JSON.stringify(updates)
            });
            
            await apiUtils.handleApiError(response);
            return true;
        } catch (error) {
            console.error('Erreur lors de la sauvegarde:', error);
            throw error;
        }
    },

    /**
     * Récupère les statistiques de devis
     * @returns {Promise<Object>} - Statistiques globales et par vendeur
     */
    async getQuotationStats() {
        try {
            if (!utils.validateDates()) {
                throw new Error('Dates de période non définies');
            }

            const params = new URLSearchParams({
                startDate: state.startDate,
                endDate: state.endDate
            });

            const response = await fetch(`${CONFIG.API_BASE_URL}${CONFIG.ENDPOINTS.QUOTATION_STATS}?${params}`, {
                headers: apiUtils.createHeaders()
            });
            
            await apiUtils.handleApiError(response);
            return response.json();
        } catch (error) {
            console.error('Erreur lors de la récupération des statistiques:', error);
            throw error;
        }
    }
};

// ===== GESTIONNAIRE DE TRI =====

/**
 * Gestionnaire du tri des devis
 */
const sortManager = {
    /**
     * Trie les devis selon le critère spécifié
     * @param {Array} data - Données à trier
     * @param {string} field - Champ de tri
     * @param {string} order - Ordre de tri ('asc' ou 'desc')
     * @returns {Array} - Données triées
     */
    sortQuotations(data, field, order) {
        const sortFunctions = {
            date: (a, b) => new Date(a.date) - new Date(b.date),
            name: (a, b) => (a.seller || '').localeCompare(b.seller || ''),
            client: (a, b) => (a.client || '').localeCompare(b.client || '')
        };

        const sortFunction = sortFunctions[field] || (() => 0);
        const sortedData = [...data].sort(sortFunction);
        
        return order === 'desc' ? sortedData.reverse() : sortedData;
    },

    /**
     * Met à jour l'affichage des icônes de tri
     * @param {string} field - Champ actif
     * @param {string} order - Ordre actuel
     */
    updateSortIcons(field, order) {
        if (!elements.sortIcons) return;

        elements.sortIcons.forEach(icon => {
            const isActive = icon.dataset.sort === field;
            icon.classList.toggle('active', isActive);
            icon.dataset.order = isActive ? order : 'asc';
            
            const useElement = icon.querySelector('use');
            if (useElement) {
                const iconName = isActive ? (order === 'asc' ? 'sort-down' : 'sort-up') : 'sort-down';
                useElement.setAttribute('xlink:href', 
                    `assets/vendor/bootstrap-icons/bootstrap-icons.svg#${iconName}`
                );
            }
        });
    }
};

// ===== GESTIONNAIRE D'INTERFACE =====

/**
 * Gestionnaire du rendu et des interactions UI
 */
const uiManager = {
    /**
     * Génère une ligne de devis
     * @param {Object} quotation - Données du devis
     * @returns {string} - HTML de la ligne
     */
    renderQuotationRow(quotation) {
        return `
            <tr data-quotation-id="${quotation.id}">
                <td class="text-center align-middle">${utils.formatDate(quotation.date)}</td>
                <td class="text-center align-middle">${utils.getInitials(quotation.seller)}</td>
                <td class="text-center align-middle">${quotation.client || 'N/A'}</td>
                <td class="text-center align-middle">
                    <select class="form-select form-select-sm action-select" 
                            data-original-value="${quotation.action || ''}">
                        <option value="">${quotation.action || 'Sélectionner'}</option>
                        ${Object.entries(state.availableActions)
                            .map(([key, label]) => `
                                <option value="${key}" ${quotation.action === key ? 'selected' : ''}>
                                    ${label}
                                </option>
                            `).join('')}
                    </select>
                </td>
                <td class="text-center align-middle">
                    <input type="text" class="form-control form-control-sm comment-input" 
                        value="${quotation.comment || ''}" 
                        data-original-value="${quotation.comment || ''}"
                        placeholder="Commentaire...">
                </td>
            </tr>
        `;
    },

    /**
     * Génère la ligne vide
     * @returns {string} - HTML de la ligne vide
     */
    renderEmptyRow() {
        return `
            <tr>
                <td colspan="5" class="text-center align-middle">
                    Aucun devis non validé pour cette période
                </td>
            </tr>
        `;
    },

    /**
     * Met à jour le tableau des devis
     * @param {Array} quotations - Liste des devis
     */
    updateQuotationsTable(quotations) {
        if (!elements.tbody) return;

        const content = quotations.length === 0 
            ? this.renderEmptyRow()
            : quotations.map(q => this.renderQuotationRow(q)).join('');

        elements.tbody.innerHTML = content;
    },

    /**
     * Met à jour l'affichage des statistiques
     * @param {Object} stats - Statistiques à afficher
     */
    updateStatsDisplay(stats) {
        // Mise à jour du taux global
        if (elements.concretizationRate && stats.concretizationRate !== undefined) {
            elements.concretizationRate.textContent = `${stats.concretizationRate.toFixed(1)}%`;
        }

        // Mise à jour des nombres de devis
        if (elements.quotationsNumbers && stats.totalQuotations !== undefined && stats.unvalidatedQuotations !== undefined) {
            elements.quotationsNumbers.innerHTML = `
                Nb : ${stats.totalQuotations}<br>
                Non validés : ${stats.unvalidatedQuotations}
            `;
        }

        // Mise à jour du tableau des vendeurs
        if (elements.sellerStatsBody && stats.sellerStats) {
            elements.sellerStatsBody.innerHTML = stats.sellerStats
                .map(seller => `
                    <tr class="text-center">
                        <td class="text-center align-middle">${utils.getInitials(seller.sellerRef)}</td>
                        <td class="text-center align-middle">${seller.totalQuotations}</td>
                        <td class="text-center align-middle">${seller.unvalidatedQuotations}</td>
                        <td class="text-center align-middle">${seller.concretizationRate.toFixed(1)}%</td>
                    </tr>
                `)
                .join('');
        }
    },

    /**
     * Configure l'accès basé sur les rôles
     */
    setupRoleBasedAccess() {
        if (elements.collaboratorsTable && utils.hasStatsAccess()) {
            elements.collaboratorsTable.classList.remove('d-none');
        }
    },

    /**
     * Récupère les modifications apportées aux devis
     * @returns {Array} - Liste des modifications
     */
    getModifiedQuotations() {
        return Array.from(document.querySelectorAll('tr[data-quotation-id]'))
            .map(row => {
                const id = row.dataset.quotationId;
                const actionSelect = row.querySelector('.action-select');
                const commentInput = row.querySelector('.comment-input');

                if (!actionSelect || !commentInput) return null;

                const actionChanged = actionSelect.value !== actionSelect.dataset.originalValue;
                const commentChanged = commentInput.value !== commentInput.dataset.originalValue;

                if (!actionChanged && !commentChanged) return null;

                return {
                    id: parseInt(id),
                    action: actionSelect.value,
                    comment: commentInput.value
                };
            })
            .filter(Boolean);
    },

    /**
     * Met à jour les valeurs originales après sauvegarde
     * @param {Array} updates - Modifications sauvegardées
     */
    updateOriginalValues(updates) {
        updates.forEach(update => {
            const row = document.querySelector(`tr[data-quotation-id="${update.id}"]`);
            if (!row) return;

            const actionSelect = row.querySelector('.action-select');
            const commentInput = row.querySelector('.comment-input');
            
            if (actionSelect) actionSelect.dataset.originalValue = update.action;
            if (commentInput) commentInput.dataset.originalValue = update.comment;
        });
    },

    /**
     * Met à jour l'état du bouton de sauvegarde
     */
    updateSaveButtonState() {
        if (!elements.saveButton) return;

        const hasChanges = this.getModifiedQuotations().length > 0;
        elements.saveButton.disabled = !hasChanges;
    },

    /**
     * Affiche un message d'erreur dans le tableau
     * @param {string} message - Message d'erreur à afficher
     */
    showError(message) {
        if (elements.tbody) {
            elements.tbody.innerHTML = `
                <tr>
                    <td colspan="5" class="text-center align-middle text-danger">
                        ${message}
                    </td>
                </tr>
            `;
        }
    }
};

// ===== CONTRÔLEUR PRINCIPAL =====

/**
 * Contrôleur principal de gestion des devis
 */
const quotationsController = {
    /**
     * Initialise le contrôleur
     */
    async init() {
        try {
            this.setupEventListeners();
            await this.loadInitialData();
            uiManager.setupRoleBasedAccess();
        } catch (error) {
            console.error('Erreur lors de l\'initialisation du contrôleur:', error);
            uiManager.showError('Erreur lors du chargement des données');
        }
    },

    /**
     * Configure les écouteurs d'événements
     */
    setupEventListeners() {
        // Écouteurs de tri
        if (elements.sortIcons) {
            elements.sortIcons.forEach(icon => {
                icon.addEventListener('click', this.handleSort.bind(this));
            });
        }

        // Écouteurs de modification
        if (elements.tbody) {
            elements.tbody.addEventListener('change', uiManager.updateSaveButtonState.bind(uiManager));
            elements.tbody.addEventListener('input', uiManager.updateSaveButtonState.bind(uiManager));
        }

        // Écouteur de sauvegarde
        if (elements.saveButton) {
            elements.saveButton.addEventListener('click', this.handleSave.bind(this));
        }

        // Écouteurs de mise à jour des dates
        window.addEventListener('storage', (e) => {
            if (e.key === 'startDate' || e.key === 'endDate') {
                initializeState();
                this.refreshData();
            }
        });

        document.addEventListener('datesUpdated', this.refreshData.bind(this));
    },

    /**
     * Charge les données initiales
     */
    async loadInitialData() {
        try {
            // Chargement en parallèle des données
            const [actions, quotations, stats] = await Promise.all([
                quotationsService.getAvailableActions(),
                quotationsService.getUnvalidatedQuotations(),
                quotationsService.getQuotationStats().catch(() => null)
            ]);

            // Mise à jour de l'état
            state.availableActions = actions;
            state.quotations = sortManager.sortQuotations(quotations, state.currentSort.field, state.currentSort.order);

            // Mise à jour de l'interface
            uiManager.updateQuotationsTable(state.quotations);
            if (stats) {
                uiManager.updateStatsDisplay(stats);
            }

        } catch (error) {
            console.error('Erreur lors du chargement des données:', error);
            uiManager.showError('Impossible de charger les devis');
            throw error;
        }
    },

    /**
     * Gère le tri des colonnes
     * @param {Event} event - Événement de clic
     */
    async handleSort(event) {
        try {
            const icon = event.currentTarget;
            const field = icon.dataset.sort;
            const order = icon.dataset.order === 'asc' ? 'desc' : 'asc';

            state.currentSort = { field, order };
            sortManager.updateSortIcons(field, order);

            // Re-tri des données existantes
            state.quotations = sortManager.sortQuotations(state.quotations, field, order);
            uiManager.updateQuotationsTable(state.quotations);

        } catch (error) {
            console.error('Erreur lors du tri:', error);
        }
    },

    /**
     * Gère la sauvegarde des modifications
     */
    async handleSave() {
        try {
            const updates = uiManager.getModifiedQuotations();
            if (updates.length === 0) return;

            await quotationsService.saveBatchUpdates(updates);
            
            // Mise à jour des valeurs originales et du bouton
            uiManager.updateOriginalValues(updates);
            uiManager.updateSaveButtonState();
            
            apiUtils.showToast('success');

        } catch (error) {
            console.error('Erreur lors de la sauvegarde:', error);
            apiUtils.showToast('error');
        }
    },

    /**
     * Rafraîchit toutes les données
     */
    async refreshData() {
        try {
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
function refreshQuotations() {
    quotationsController.refreshData();
}

/**
 * Sauvegarde manuelle (fonction globale)
 */
function saveChanges() {
    quotationsController.handleSave();
}

/**
 * Exporte les données au format CSV (fonction future)
 */
function exportToCSV() {
    console.log('Export CSV non implémenté');
    alert('Fonctionnalité d\'export en cours de développement');
}