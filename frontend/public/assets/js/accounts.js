/**
 * Gestionnaire de gestion des comptes utilisateurs Optitop
 * Gère les demandes de création/modification/suppression de comptes
 */

// ===== CONFIGURATION =====

const CONFIG = {
    API_BASE_URL: 'http://localhost:8080/api',
    ENDPOINTS: {
        PENDING_ACCOUNTS: '/pending-accounts',
        VALIDATE_ACCOUNT: '/pending-accounts/validate',
        REJECT_ACCOUNT: '/pending-accounts/reject',
        ALL_USERS: '/users/all',
        USER_LOGINS: '/users/logins',
        AVAILABLE_SELLERS: '/sellers/available-sellers'
    },
    AUTHORIZED_ROLES: ['admin', 'supermanager', 'manager'],
    LOCALE: 'fr-FR'
};

// ===== ÉLÉMENTS DOM =====

const elements = {
    form: null,
    askSelect: null,
    roleSelect: null,
    identifiantInput: null,
    identifiantSelect: null,
    pendingTable: null,
    usersTable: null
};

// ===== INITIALISATION =====

/**
 * Initialisation au chargement du DOM
 */
document.addEventListener('DOMContentLoaded', () => {
    try {
        initializeElements();
        accountsController.init();
    } catch (error) {
        console.error('Erreur lors de l\'initialisation des comptes:', error);
    }
});

/**
 * Initialise les références aux éléments DOM
 */
function initializeElements() {
    elements.form = document.getElementById('new-request-form');
    elements.askSelect = document.getElementById('ask-select');
    elements.roleSelect = document.getElementById('role-select');
    elements.identifiantInput = document.getElementById('identifiant');
    elements.pendingTable = document.querySelector('#accounts-ask-logs tbody');
    elements.usersTable = document.querySelector('#users-table tbody');
    
    // Création du select identifiant dynamique
    createIdentifiantSelect();
}

/**
 * Crée le select identifiant dynamique
 */
function createIdentifiantSelect() {
    if (elements.identifiantInput) {
        elements.identifiantSelect = document.createElement('select');
        elements.identifiantSelect.className = 'form-select form-select-sm';
        elements.identifiantSelect.id = 'identifiant-select';
        elements.identifiantSelect.style.display = 'none';
        elements.identifiantInput.parentNode.insertBefore(elements.identifiantSelect, elements.identifiantInput.nextSibling);
    }
}

// ===== UTILITAIRES =====

/**
 * Utilitaires généraux
 */
const utils = {
    /**
     * Valide le format d'un email
     * @param {string} email - Email à valider
     * @returns {boolean} - true si email valide
     */
    isValidEmail: (email) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email),
    
    /**
     * Vérifie si un utilisateur a les permissions nécessaires
     * @param {Object} user - Objet utilisateur
     * @returns {boolean} - true si autorisé
     */
    hasPermission: (user) => {
        return CONFIG.AUTHORIZED_ROLES.includes(user?.role);
    },
    
    /**
     * Récupère l'utilisateur courant depuis le sessionStorage
     * @returns {Object} - Objet utilisateur
     * @throws {Error} - Si utilisateur non connecté
     */
    getCurrentUser: () => {
        const userStr = sessionStorage.getItem('user');
        if (!userStr) {
            throw new Error('Utilisateur non connecté');
        }
        return JSON.parse(userStr);
    },

    /**
     * Formate une date selon la locale française
     * @param {string} dateString - Date à formater
     * @returns {string} - Date formatée
     */
    formatDate: (dateString) => {
        return new Date(dateString).toLocaleDateString(CONFIG.LOCALE);
    }
};

/**
 * Utilitaires de service API
 */
const apiUtils = {
    /**
     * Crée les en-têtes d'autorisation
     * @param {string} userId - ID de l'utilisateur
     * @returns {Object} - En-têtes HTTP
     */
    createAuthHeaders: (userId) => ({
        'Authorization': `Bearer ${userId}`,
        'Content-Type': 'application/json'
    }),

    /**
     * Gère les erreurs de réponse API
     * @param {Response} response - Réponse fetch
     * @throws {Error} - Erreur appropriée selon le status
     */
    handleApiError: async (response) => {
        if (response.status === 403) {
            throw new Error('Accès non autorisé');
        }
        
        if (!response.ok) {
            const error = await response.json().catch(() => ({}));
            throw new Error(error.message || `Erreur API: ${response.status}`);
        }
    }
};

// ===== SERVICES API =====

/**
 * Service de gestion des comptes utilisateurs
 */
const accountsService = {
    /**
     * Récupère les demandes de comptes en attente
     * @param {string} userId - ID de l'utilisateur
     * @returns {Promise<Array>} - Liste des demandes en attente
     */
    async getPendingAccounts(userId) {
        try {
            const response = await fetch(`${CONFIG.API_BASE_URL}${CONFIG.ENDPOINTS.PENDING_ACCOUNTS}`, {
                headers: apiUtils.createAuthHeaders(userId)
            });
            
            await apiUtils.handleApiError(response);
            return response.json();
        } catch (error) {
            console.error('Erreur lors de la récupération des demandes:', error);
            throw error;
        }
    },

    /**
     * Valide une demande de compte
     * @param {string} accountId - ID de la demande à valider
     * @param {string} userId - ID de l'utilisateur validant
     */
    async validateAccount(accountId, userId) {
        try {
            const response = await fetch(`${CONFIG.API_BASE_URL}${CONFIG.ENDPOINTS.VALIDATE_ACCOUNT}/${accountId}`, {
                method: 'POST',
                headers: apiUtils.createAuthHeaders(userId)
            });
            
            await apiUtils.handleApiError(response);
        } catch (error) {
            console.error('Erreur lors de la validation:', error);
            throw error;
        }
    },

    /**
     * Rejette une demande de compte
     * @param {string} accountId - ID de la demande à rejeter
     * @param {string} userId - ID de l'utilisateur rejetant
     */
    async rejectAccount(accountId, userId) {
        try {
            const response = await fetch(`${CONFIG.API_BASE_URL}${CONFIG.ENDPOINTS.REJECT_ACCOUNT}/${accountId}`, {
                method: 'POST',
                headers: apiUtils.createAuthHeaders(userId)
            });
            
            await apiUtils.handleApiError(response);
        } catch (error) {
            console.error('Erreur lors du rejet:', error);
            throw error;
        }
    },

    /**
     * Récupère tous les utilisateurs (admin uniquement)
     * @returns {Promise<Array>} - Liste des utilisateurs
     */
    async getAllUsers() {
        try {
            const user = utils.getCurrentUser();
            
            if (user?.role !== 'admin') {
                throw new Error('Accès non autorisé');
            }

            const response = await fetch(`${CONFIG.API_BASE_URL}${CONFIG.ENDPOINTS.ALL_USERS}`, {
                headers: apiUtils.createAuthHeaders(user.id)
            });

            await apiUtils.handleApiError(response);
            return response.json();
        } catch (error) {
            console.error('Erreur lors de la récupération des utilisateurs:', error);
            throw error;
        }
    },

    /**
     * Crée une nouvelle demande de compte
     * @param {Object} formData - Données de la demande
     * @param {string} userId - ID de l'utilisateur créant la demande
     */
    async createAccountRequest(formData, userId) {
        try {
            const response = await fetch(`${CONFIG.API_BASE_URL}${CONFIG.ENDPOINTS.PENDING_ACCOUNTS}`, {
                method: 'POST',
                headers: apiUtils.createAuthHeaders(userId),
                body: JSON.stringify(formData)
            });

            await apiUtils.handleApiError(response);
        } catch (error) {
            console.error('Erreur lors de la création de la demande:', error);
            throw error;
        }
    }
};

/**
 * Service de données auxiliaires
 */
const dataService = {
    /**
     * Récupère les vendeurs disponibles
     * @returns {Promise<Array>} - Liste des vendeurs disponibles
     */
    async getAvailableSellers() {
        try {
            const response = await fetch(`${CONFIG.API_BASE_URL}${CONFIG.ENDPOINTS.AVAILABLE_SELLERS}`);
            await apiUtils.handleApiError(response);
            return response.json();
        } catch (error) {
            console.error('Erreur lors de la récupération des vendeurs:', error);
            throw error;
        }
    },

    /**
     * Récupère les logins utilisateurs existants
     * @returns {Promise<Array>} - Liste des logins
     */
    async getUserLogins() {
        try {
            const response = await fetch(`${CONFIG.API_BASE_URL}${CONFIG.ENDPOINTS.USER_LOGINS}`);
            await apiUtils.handleApiError(response);
            return response.json();
        } catch (error) {
            console.error('Erreur lors de la récupération des logins:', error);
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
     * Génère les boutons d'action pour une demande
     * @param {Object} account - Demande de compte
     * @param {Object} user - Utilisateur courant
     * @returns {string} - HTML des boutons
     */
    renderActionButtons(account, user) {
        const isAdmin = user?.role === 'admin';
        
        if (!account.id) {
            console.error('Account sans ID:', account);
            return '';
        }
        
        return `
            <button class="btn btn-action btn-success btn-sm" 
                data-action="validate"
                data-account-id="${account.id}"
                onclick="accountsController.handleAction(this)"
                ${!isAdmin ? 'disabled title="Action réservée aux administrateurs"' : ''}>
                Valider
            </button>
            <button class="btn btn-action btn-danger btn-sm" 
                data-action="reject"
                data-account-id="${account.id}"
                onclick="accountsController.handleAction(this)"
                ${!isAdmin ? 'disabled title="Action réservée aux administrateurs"' : ''}>
                Refuser
            </button>
        `;
    },

    /**
     * Met à jour le tableau des demandes en attente
     * @param {Array} pendingAccounts - Liste des demandes
     * @param {Object} user - Utilisateur courant
     */
    updateTable(pendingAccounts, user) {
        if (!elements.pendingTable) return;

        elements.pendingTable.innerHTML = pendingAccounts.map(account => `
            <tr>
                <td class="text-center align-middle">${utils.formatDate(account.createdAt)}</td>
                <td class="text-center align-middle">${account.createdByLogin || 'N/A'}</td>
                <td class="text-center align-middle">${account.lastname || 'N/A'}</td>
                <td class="text-center align-middle">${account.firstname || 'N/A'}</td>
                <td class="text-center align-middle">${account.login || 'N/A'}</td>
                <td class="text-center align-middle">${account.role || 'N/A'}</td>
                <td class="text-center align-middle">${account.email || 'N/A'}</td>
                <td class="text-center align-middle">${account.requestType || 'N/A'}</td>
                <td class="text-center align-middle">
                    <div class="d-flex justify-content-center gap-1">
                        ${this.renderActionButtons(account, user)}
                    </div>
                </td>
            </tr>
        `).join('');
    },

    /**
     * Met à jour le tableau des utilisateurs
     * @param {Array} users - Liste des utilisateurs
     */
    updateUsersTable(users) {
        if (!elements.usersTable) return;

        elements.usersTable.innerHTML = users.map(user => `
            <tr>
                <td class="text-center align-middle">${utils.formatDate(user.createdAt)}</td>
                <td class="text-center align-middle">${user.login || 'N/A'}</td>
                <td class="text-center align-middle">${user.role || 'N/A'}</td>
                <td class="text-center align-middle">${user.lastname || 'N/A'}</td>
                <td class="text-center align-middle">${user.firstname || 'N/A'}</td>
                <td class="text-center align-middle">${user.email || 'N/A'}</td>
            </tr>
        `).join('');
    },

    /**
     * Initialise les fonctionnalités admin
     */
    initializeAdminFeatures() {
        try {
            const user = utils.getCurrentUser();
            const tableUserSection = document.getElementById('table-user');
            
            if (tableUserSection) {
                if (user?.role === 'admin') {
                    tableUserSection.style.display = 'block';
                    this.loadUsersTable();
                } else {
                    tableUserSection.remove();
                }
            }
        } catch (error) {
            console.error('Erreur lors de l\'initialisation des fonctionnalités admin:', error);
        }
    },

    /**
     * Charge le tableau des utilisateurs
     */
    async loadUsersTable() {
        try {
            const users = await accountsService.getAllUsers();
            this.updateUsersTable(users);
        } catch (error) {
            console.error('Erreur chargement utilisateurs:', error);
        }
    },

    /**
     * Affiche ou masque les champs selon le type de demande
     * @param {string} askType - Type de demande
     */
    toggleFieldsVisibility(askType) {
        const fieldsToToggle = [
            document.querySelector('#lastname')?.parentElement,
            document.querySelector('#firstname')?.parentElement,
            document.querySelector('#email')?.parentElement,
            document.querySelector('#role-select')?.parentElement
        ].filter(Boolean);

        if (askType === 'suppression') {
            fieldsToToggle.forEach(field => field.style.display = 'none');
        } else {
            fieldsToToggle.forEach(field => field.style.display = 'table-cell');
        }
    },

    /**
     * Met à jour le champ identifiant selon la sélection
     * @param {string} selectedRole - Rôle sélectionné
     * @param {string} selectedAskType - Type de demande sélectionné
     */
    async updateIdentifiantField(selectedRole, selectedAskType) {
        if (!elements.identifiantInput || !elements.identifiantSelect) return;

        try {
            let endpoint = '';
            
            if (selectedAskType === 'ajout' && (selectedRole === 'collaborator' || selectedRole === 'manager')) {
                endpoint = CONFIG.ENDPOINTS.AVAILABLE_SELLERS;
            } else if (selectedAskType === 'modification') {
                endpoint = CONFIG.ENDPOINTS.USER_LOGINS;
            }
            
            if (endpoint) {
                const response = await fetch(`${CONFIG.API_BASE_URL}${endpoint}`);
                await apiUtils.handleApiError(response);
                
                const data = await response.json();
                
                elements.identifiantSelect.innerHTML = `
                    <option value="" selected disabled hidden>
                        ${endpoint.includes('sellers') ? 'Vendeur' : 'Identifiant'}
                    </option>
                    ${data.map(item => `
                        <option value="${typeof item === 'object' ? item.sellerRef : item}">
                            ${typeof item === 'object' ? item.sellerRef : item}
                        </option>
                    `).join('')}
                `;
                
                elements.identifiantInput.style.display = 'none';
                elements.identifiantSelect.style.display = 'block';
            } else {
                elements.identifiantSelect.style.display = 'none';
                elements.identifiantInput.style.display = 'block';
            }
        } catch (error) {
            console.error('Erreur lors de la mise à jour du champ identifiant:', error);
            alert('Erreur lors de la récupération des données');
        }
    }
};

// ===== CONTRÔLEUR PRINCIPAL =====

/**
 * Contrôleur principal de gestion des comptes
 */
const accountsController = {
    /**
     * Initialise le contrôleur
     */
    async init() {
        try {
            this.setupEventListeners();
            await this.loadPendingAccounts();
            uiManager.initializeAdminFeatures();
        } catch (error) {
            console.error('Erreur lors de l\'initialisation du contrôleur:', error);
        }
    },

    /**
     * Configure les écouteurs d'événements
     */
    setupEventListeners() {
        if (elements.roleSelect) {
            elements.roleSelect.addEventListener('change', this.handleFieldUpdate.bind(this));
        }
        
        if (elements.askSelect) {
            elements.askSelect.addEventListener('change', this.handleFieldUpdate.bind(this));
        }
    },

    /**
     * Gère la mise à jour des champs du formulaire
     */
    async handleFieldUpdate() {
        const selectedRole = elements.roleSelect?.value;
        const selectedAskType = elements.askSelect?.value;
        
        if (selectedAskType) {
            uiManager.toggleFieldsVisibility(selectedAskType);
        }
        
        if (selectedRole && selectedAskType) {
            await uiManager.updateIdentifiantField(selectedRole, selectedAskType);
        }
    },

    /**
     * Charge les demandes de comptes en attente
     */
    async loadPendingAccounts() {
        try {
            const user = utils.getCurrentUser();
            const pendingAccounts = await accountsService.getPendingAccounts(user.id);
            uiManager.updateTable(pendingAccounts, user);
        } catch (error) {
            console.error('Erreur lors du chargement des demandes:', error);
            alert('Erreur lors du chargement des demandes');
        }
    },

    /**
     * Gère les actions sur les demandes (valider/rejeter)
     * @param {HTMLElement} button - Bouton cliqué
     */
    async handleAction(button) {
        try {
            const accountId = button.getAttribute('data-account-id');
            if (!accountId || isNaN(parseInt(accountId))) {
                throw new Error('ID de demande invalide');
            }

            const user = utils.getCurrentUser();
            if (!user || user.role !== 'admin') {
                throw new Error('Action non autorisée');
            }

            const action = button.getAttribute('data-action');
            
            switch(action) {
                case 'validate':
                    await accountsService.validateAccount(accountId, user.id);
                    alert('Demande validée avec succès');
                    await Promise.all([
                        this.loadPendingAccounts(),
                        this.loadUsers()
                    ]);
                    break;
                    
                case 'reject':
                    if (confirm('Êtes-vous sûr de vouloir refuser cette demande ?')) {
                        await accountsService.rejectAccount(accountId, user.id);
                        alert('Demande refusée avec succès');
                        await this.loadPendingAccounts();
                    }
                    break;
                    
                default:
                    return;
            }
        } catch (error) {
            console.error('Erreur lors de l\'action:', error);
            alert(error.message);
        }
    },

    /**
     * Charge la liste des utilisateurs
     */
    async loadUsers() {
        try {
            const users = await accountsService.getAllUsers();
            uiManager.updateUsersTable(users);
        } catch (error) {
            console.error('Erreur lors du chargement des utilisateurs:', error);
            alert('Erreur lors du chargement des utilisateurs');
        }
    }
};

// ===== FONCTIONS GLOBALES (conservées pour compatibilité) =====

/**
 * Affiche le formulaire de nouvelle demande
 */
function showNewRequestForm() {
    const form = document.getElementById('new-request-form');
    if (form) {
        form.style.display = 'block';
    }
    
    const collapseSection = document.getElementById('collapse-user-creation');
    if (collapseSection && !collapseSection.classList.contains('show')) {
        new bootstrap.Collapse(collapseSection).show();
    }
}

/**
 * Annule et réinitialise le formulaire de demande
 */
function cancelRequest() {
    const form = document.getElementById('new-request-form');
    if (form) {
        form.style.display = 'none';
        form.querySelectorAll('input, select').forEach(input => input.value = '');
    }
}

/**
 * Soumet une nouvelle demande de compte
 */
async function submitRequest() {
    try {
        const user = utils.getCurrentUser();
        
        const askType = elements.askSelect?.value;
        if (!askType) {
            alert('Veuillez sélectionner un type de demande');
            return;
        }

        // Récupération de l'identifiant selon le type d'affichage
        const identifiantValue = elements.identifiantSelect?.style.display === 'block' 
            ? elements.identifiantSelect.value
            : elements.identifiantInput?.value;

        // Construction des données de base
        let formData = {
            login: identifiantValue,
            requestType: askType
        };

        // Ajout des champs selon le type de demande
        if (askType === 'ajout') {
            formData = {
                ...formData,
                lastname: document.getElementById('lastname')?.value.trim(),
                firstname: document.getElementById('firstname')?.value.trim(),
                email: document.getElementById('email')?.value.trim(),
                role: elements.roleSelect?.value
            };

            // Validation des champs obligatoires
            if (!formData.lastname || !formData.firstname || !formData.email || !formData.role || !formData.login) {
                alert('Tous les champs sont obligatoires pour une création');
                return;
            }
            
            if (!utils.isValidEmail(formData.email)) {
                alert('Format d\'email invalide');
                return;
            }
        } else if (askType === 'modification') {
            // Ajout conditionnel des champs modifiés
            const roleValue = elements.roleSelect?.value;
            const lastnameValue = document.getElementById('lastname')?.value.trim();
            const firstnameValue = document.getElementById('firstname')?.value.trim();
            const emailValue = document.getElementById('email')?.value.trim();

            if (roleValue) formData.role = roleValue;
            if (lastnameValue) formData.lastname = lastnameValue;
            if (firstnameValue) formData.firstname = firstnameValue;
            if (emailValue) formData.email = emailValue;
        }

        // Envoi de la demande
        await accountsService.createAccountRequest(formData, user.id);
        
        alert('Demande envoyée avec succès');
        cancelRequest();
        await accountsController.loadPendingAccounts();
        
    } catch (error) {
        console.error('Erreur lors de la soumission:', error);
        alert(error.message || 'Erreur lors de l\'envoi de la demande');
    }
}