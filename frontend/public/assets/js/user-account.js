/**
 * Gestionnaire du compte utilisateur Optitop
 * 
 * Gère les opérations liées au compte utilisateur :
 * - Affichage des informations de profil
 * - Récupération des données utilisateur depuis l'API
 * - Changement de mot de passe avec validation
 * - Mise à jour des détails personnels
 */

// ===== CONFIGURATION =====

const CONFIG = {
    API_BASE_URL: 'http://localhost:8080/api',
    ENDPOINTS: {
        USER_LASTNAME: '/users/{id}/lastname',
        USER_EMAIL: '/users/{id}/email',
        USER_LOGIN: '/users/{id}/login',
        USER_CREATED_AT: '/users/{id}/created-at',
        CHANGE_PASSWORD: '/users/{id}/change-password'
    },
    // Regex de validation des mots de passe :
    // - Au moins 1 minuscule (?=.*[a-z])
    // - Au moins 1 majuscule (?=.*[A-Z])
    // - Au moins 1 chiffre (?=.*\d)
    // - Au moins 1 caractère spécial (@$!%*?&) (?=.*[@$!%*?&])
    // - Minimum 12 caractères {12,}
    // - Seuls les caractères autorisés [A-Za-z\d@$!%*?&]
    PASSWORD_PATTERN: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{12,}$/,
    LOCALE: 'fr-FR'
};

// ===== ÉLÉMENTS DOM =====

const elements = {
    profileFullname: null,
    profileRole: null,
    profileDateCreate: null,
    profileDetailName: null,
    profileDetailFirstname: null,
    profileDetailLogin: null,
    profileDetailEmail: null,
    profileDetailCreatedAt: null,
    passwordForm: null,
    currentPassword: null,
    newPassword: null,
    renewPassword: null,
    passwordResult: null
};

// ===== ÉTAT DE L'APPLICATION =====

const state = {
    userSession: null
};

// ===== INITIALISATION =====

/**
 * Initialisation au chargement du DOM
 */
document.addEventListener('DOMContentLoaded', () => {
    try {
        initializeElements();
        initializeState();
        userAccountController.init();
    } catch (error) {
        console.error('Erreur lors de l\'initialisation du compte utilisateur:', error);
    }
});

/**
 * Initialise les références aux éléments DOM
 */
function initializeElements() {
    elements.profileFullname = document.querySelector('#profileFullname');
    elements.profileRole = document.querySelector('#profileRole');
    elements.profileDateCreate = document.querySelector('#profileDateCreate');
    elements.profileDetailName = document.querySelector('#profileDetailName');
    elements.profileDetailFirstname = document.querySelector('#profileDetailFirstname');
    elements.profileDetailLogin = document.querySelector('#profileDetailLogin');
    elements.profileDetailEmail = document.querySelector('#profileDetailEmail');
    elements.profileDetailCreatedAt = document.querySelector('#profileDetailCreatedAt');
    elements.passwordForm = document.querySelector('#passwordChangeForm');
    elements.currentPassword = document.querySelector('#currentPassword');
    elements.newPassword = document.querySelector('#newPassword');
    elements.renewPassword = document.querySelector('#renewPassword');
    elements.passwordResult = document.querySelector('#passwordChangeResult');
}

/**
 * Initialise l'état de l'application
 */
function initializeState() {
    state.userSession = JSON.parse(sessionStorage.getItem('user'));
    console.log('Session utilisateur:', state.userSession);
}

// ===== UTILITAIRES =====

/**
 * Utilitaires généraux
 */
const utils = {
    /**
     * Traduit un rôle utilisateur en français
     * @param {string} role - Rôle à traduire
     * @returns {string} - Rôle traduit
     */
    translateRole: (role) => {
        const roleTranslations = {
            'admin': 'Administrateur',
            'collaborator': 'Collaborateur',
            'manager': 'Manager',
            'supermanager': 'Super Manager'
        };
        return roleTranslations[role?.toLowerCase()] || 
               role?.charAt(0).toUpperCase() + role?.slice(1).toLowerCase() || 'Inconnu';
    },

    /**
     * Formate une date selon la locale française
     * @param {string} dateString - Date à formater
     * @returns {string} - Date formatée
     */
    formatDate: (dateString) => {
        const date = new Date(dateString);
        return new Intl.DateTimeFormat(CONFIG.LOCALE, {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
        }).format(date);
    },

    /**
     * Valide un mot de passe selon les critères de sécurité
     * @param {string} password - Mot de passe à valider
     * @returns {boolean} - true si valide
     */
    isPasswordValid: (password) => {
        return CONFIG.PASSWORD_PATTERN.test(password);
    },

    /**
     * Valide la session utilisateur
     * @returns {boolean} - true si la session est valide
     */
    validateUserSession: () => {
        return state.userSession && state.userSession.id;
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
            const error = await response.text().catch(() => 'Erreur inconnue');
            throw new Error(error || `Erreur API: ${response.status}`);
        }
    },

    /**
     * Construit l'URL d'un endpoint avec l'ID utilisateur
     * @param {string} endpoint - Endpoint à utiliser
     * @returns {string} - URL complète
     */
    buildUserUrl: (endpoint) => {
        return `${CONFIG.API_BASE_URL}${endpoint.replace('{id}', state.userSession.id)}`;
    },

    /**
     * Effectue un appel API GET générique
     * @param {string} endpoint - Point de terminaison
     * @returns {Promise<string>} - Données de réponse
     */
    fetchUserData: async (endpoint) => {
        try {
            const response = await fetch(apiUtils.buildUserUrl(endpoint), {
                headers: apiUtils.createHeaders()
            });
            await apiUtils.handleApiError(response);
            return response.text();
        } catch (error) {
            console.error(`Erreur API ${endpoint}:`, error);
            throw error;
        }
    }
};

// ===== SERVICES API =====

/**
 * Service de gestion des données utilisateur
 */
const userService = {
    /**
     * Récupère le nom de famille de l'utilisateur
     * @returns {Promise<string>} - Nom de famille
     */
    async getUserLastname() {
        try {
            return await apiUtils.fetchUserData(CONFIG.ENDPOINTS.USER_LASTNAME);
        } catch (error) {
            console.error('Erreur lors de la récupération du nom:', error);
            throw error;
        }
    },

    /**
     * Récupère l'email de l'utilisateur
     * @returns {Promise<string>} - Email
     */
    async getUserEmail() {
        try {
            return await apiUtils.fetchUserData(CONFIG.ENDPOINTS.USER_EMAIL);
        } catch (error) {
            console.error('Erreur lors de la récupération de l\'email:', error);
            throw error;
        }
    },

    /**
     * Récupère le login de l'utilisateur
     * @returns {Promise<string>} - Login
     */
    async getUserLogin() {
        try {
            return await apiUtils.fetchUserData(CONFIG.ENDPOINTS.USER_LOGIN);
        } catch (error) {
            console.error('Erreur lors de la récupération du login:', error);
            throw error;
        }
    },

    /**
     * Récupère la date de création du compte
     * @returns {Promise<string>} - Date de création
     */
    async getUserCreatedAt() {
        try {
            return await apiUtils.fetchUserData(CONFIG.ENDPOINTS.USER_CREATED_AT);
        } catch (error) {
            console.error('Erreur lors de la récupération de la date de création:', error);
            throw error;
        }
    },

    /**
     * Change le mot de passe utilisateur
     * @param {string} currentPassword - Mot de passe actuel
     * @param {string} newPassword - Nouveau mot de passe
     * @returns {Promise<boolean>} - true si succès
     */
    async changePassword(currentPassword, newPassword) {
        try {
            const response = await fetch(apiUtils.buildUserUrl(CONFIG.ENDPOINTS.CHANGE_PASSWORD), {
                method: 'POST',
                headers: apiUtils.createHeaders(),
                body: JSON.stringify({
                    currentPassword,
                    newPassword
                })
            });
            
            await apiUtils.handleApiError(response);
            return true;
        } catch (error) {
            console.error('Erreur lors du changement de mot de passe:', error);
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
     * Met à jour le nom complet dans l'en-tête du profil
     * @param {string} lastname - Nom de famille
     */
    async updateProfileFullname(lastname) {
        if (!elements.profileFullname) return;

        try {
            const fullname = `${state.userSession.firstname} ${lastname}`;
            elements.profileFullname.textContent = fullname;
            console.log('Nom complet mis à jour:', fullname);
        } catch (error) {
            console.error('Erreur lors de la mise à jour du nom complet:', error);
            elements.profileFullname.textContent = state.userSession.firstname || 'Utilisateur';
        }
    },

    /**
     * Met à jour le rôle utilisateur
     */
    updateProfileRole() {
        if (!elements.profileRole || !state.userSession.role) return;

        const translatedRole = utils.translateRole(state.userSession.role);
        elements.profileRole.textContent = translatedRole;
        console.log('Rôle mis à jour:', translatedRole);
    },

    /**
     * Met à jour la date de création du profil
     * @param {string} createdAt - Date de création
     */
    updateProfileCreatedAt(createdAt) {
        if (!elements.profileDateCreate) return;

        try {
            const formattedDate = utils.formatDate(createdAt);
            elements.profileDateCreate.textContent = `Depuis le ${formattedDate}`;
            console.log('Date de création mise à jour:', formattedDate);
        } catch (error) {
            console.error('Erreur lors de la mise à jour de la date:', error);
            elements.profileDateCreate.textContent = 'Date non disponible';
        }
    },

    /**
     * Met à jour les détails du profil
     * @param {Object} userData - Données utilisateur
     */
    updateProfileDetails(userData) {
        // Prénom (depuis la session)
        if (elements.profileDetailFirstname) {
            elements.profileDetailFirstname.textContent = state.userSession.firstname || 'Non disponible';
        }

        // Nom de famille
        if (elements.profileDetailName && userData.lastname) {
            elements.profileDetailName.textContent = userData.lastname;
        }

        // Login
        if (elements.profileDetailLogin && userData.login) {
            elements.profileDetailLogin.textContent = userData.login;
        }

        // Email
        if (elements.profileDetailEmail && userData.email) {
            elements.profileDetailEmail.textContent = userData.email;
        }

        // Date de création
        if (elements.profileDetailCreatedAt && userData.createdAt) {
            try {
                const formattedDate = utils.formatDate(userData.createdAt);
                elements.profileDetailCreatedAt.textContent = formattedDate;
            } catch (error) {
                console.error('Erreur formatage date:', error);
                elements.profileDetailCreatedAt.textContent = 'Non disponible';
            }
        }
    },

    /**
     * Affiche le résultat du changement de mot de passe
     * @param {boolean} success - true si succès
     * @param {string} message - Message à afficher
     */
    showPasswordResult(success, message) {
        if (!elements.passwordResult) return;

        elements.passwordResult.classList.remove('d-none', 'alert-success', 'alert-danger');
        elements.passwordResult.classList.add(success ? 'alert-success' : 'alert-danger');
        elements.passwordResult.textContent = message;
    },

    /**
     * Valide visuellement un champ
     * @param {HTMLElement} field - Champ à valider
     * @param {boolean} isValid - true si valide
     */
    validateField(field, isValid) {
        if (!field) return;

        field.classList.remove('is-invalid', 'is-valid');
        field.classList.add(isValid ? 'is-valid' : 'is-invalid');
    },

    /**
     * Réinitialise le formulaire de mot de passe
     */
    resetPasswordForm() {
        if (elements.passwordForm) {
            elements.passwordForm.reset();
            
            // Supprimer les classes de validation
            [elements.currentPassword, elements.newPassword, elements.renewPassword].forEach(field => {
                if (field) {
                    field.classList.remove('is-invalid', 'is-valid');
                }
            });
        }
    },

    /**
     * Affiche les valeurs par défaut en cas d'erreur
     */
    showDefaultValues() {
        const defaultValue = 'Non disponible';
        
        if (elements.profileDetailName) elements.profileDetailName.textContent = defaultValue;
        if (elements.profileDetailLogin) elements.profileDetailLogin.textContent = defaultValue;
        if (elements.profileDetailEmail) elements.profileDetailEmail.textContent = defaultValue;
        if (elements.profileDetailCreatedAt) elements.profileDetailCreatedAt.textContent = defaultValue;
    }
};

// ===== GESTIONNAIRE DE VALIDATION =====

/**
 * Gestionnaire de validation des formulaires
 */
const validationManager = {
    /**
     * Configure la validation en temps réel du mot de passe
     */
    setupPasswordValidation() {
        if (!elements.newPassword || !elements.renewPassword) return;

        // Validation du nouveau mot de passe
        elements.newPassword.addEventListener('input', () => {
            const isValid = utils.isPasswordValid(elements.newPassword.value);
            uiManager.validateField(elements.newPassword, isValid);
        });

        // Validation de la confirmation
        elements.renewPassword.addEventListener('input', () => {
            const isValid = elements.renewPassword.value === elements.newPassword.value;
            uiManager.validateField(elements.renewPassword, isValid);
        });
    },

    /**
     * Valide le formulaire de changement de mot de passe
     * @returns {Object} - Résultat de validation
     */
    validatePasswordForm() {
        const currentPassword = elements.currentPassword?.value;
        const newPassword = elements.newPassword?.value;
        const renewPassword = elements.renewPassword?.value;

        // Validation du nouveau mot de passe
        const isNewPasswordValid = utils.isPasswordValid(newPassword);
        if (!isNewPasswordValid) {
            uiManager.validateField(elements.newPassword, false);
            return { isValid: false, message: 'Le nouveau mot de passe ne respecte pas les critères de sécurité' };
        }

        // Validation de la confirmation
        const isConfirmationValid = newPassword === renewPassword;
        if (!isConfirmationValid) {
            uiManager.validateField(elements.renewPassword, false);
            return { isValid: false, message: 'Les mots de passe ne correspondent pas' };
        }

        return { 
            isValid: true, 
            data: { currentPassword, newPassword } 
        };
    }
};

// ===== CONTRÔLEUR PRINCIPAL =====

/**
 * Contrôleur principal de gestion du compte utilisateur
 */
const userAccountController = {
    /**
     * Initialise le contrôleur
     */
    async init() {
        try {
            if (!utils.validateUserSession()) {
                throw new Error('Session utilisateur invalide');
            }

            this.setupEventListeners();
            await this.loadUserData();
        } catch (error) {
            console.error('Erreur lors de l\'initialisation du contrôleur:', error);
            uiManager.showDefaultValues();
        }
    },

    /**
     * Configure les écouteurs d'événements
     */
    setupEventListeners() {
        // Validation en temps réel du mot de passe
        validationManager.setupPasswordValidation();

        // Soumission du formulaire de changement de mot de passe
        if (elements.passwordForm) {
            elements.passwordForm.addEventListener('submit', this.handlePasswordChange.bind(this));
        }
    },

    /**
     * Charge toutes les données utilisateur
     */
    async loadUserData() {
        try {
            // Mise à jour du rôle (depuis la session)
            uiManager.updateProfileRole();

            // Chargement en parallèle des données API
            const [lastname, email, login, createdAt] = await Promise.all([
                userService.getUserLastname().catch(() => null),
                userService.getUserEmail().catch(() => null),
                userService.getUserLogin().catch(() => null),
                userService.getUserCreatedAt().catch(() => null)
            ]);

            // Mise à jour de l'interface
            if (lastname) {
                await uiManager.updateProfileFullname(lastname);
            }

            if (createdAt) {
                uiManager.updateProfileCreatedAt(createdAt);
            }

            uiManager.updateProfileDetails({
                lastname,
                email,
                login,
                createdAt
            });

            console.log('Données utilisateur chargées avec succès');

        } catch (error) {
            console.error('Erreur lors du chargement des données:', error);
            uiManager.showDefaultValues();
        }
    },

    /**
     * Gère le changement de mot de passe
     * @param {Event} event - Événement de soumission
     */
    async handlePasswordChange(event) {
        event.preventDefault();

        try {
            // Validation du formulaire
            const validation = validationManager.validatePasswordForm();
            if (!validation.isValid) {
                uiManager.showPasswordResult(false, validation.message);
                return;
            }

            // Changement de mot de passe
            await userService.changePassword(
                validation.data.currentPassword,
                validation.data.newPassword
            );

            // Succès
            uiManager.showPasswordResult(true, 'Mot de passe modifié avec succès');
            uiManager.resetPasswordForm();

        } catch (error) {
            console.error('Erreur lors du changement de mot de passe:', error);
            uiManager.showPasswordResult(false, error.message || 'Erreur lors du changement de mot de passe');
        }
    },

    /**
     * Rafraîchit les données utilisateur
     */
    async refreshData() {
        try {
            await this.loadUserData();
        } catch (error) {
            console.error('Erreur lors du rafraîchissement:', error);
        }
    }
};

// ===== FONCTIONS GLOBALES (conservées pour compatibilité) =====

/**
 * Rafraîchit les données utilisateur (fonction globale)
 */
function refreshUserAccount() {
    userAccountController.refreshData();
}