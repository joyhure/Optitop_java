/**
 * Gestionnaire de connexion Optitop
 */

// Configuration
const CONFIG = {
    API_BASE_URL: 'http://localhost:8080',
    ENDPOINTS: {
        LOGIN: '/api/auth/login'
    },
    REDIRECTS: {
        SUCCESS: 'dashboard.php',
        FORGOT_PASSWORD: 'forgot-password.html'
    }
};

// Elements DOM
const elements = {
    form: null,
    loginInput: null,
    passwordInput: null,
    submitButton: null,
    buttonText: null,
    spinner: null,
    errorMessage: null,
    errorText: null
};

/**
 * Initialisation au chargement du DOM
 */
document.addEventListener('DOMContentLoaded', function() {
    initializeElements();
    attachEventListeners();
});

/**
 * Initialise les références aux éléments DOM
 */
function initializeElements() {
    elements.form = document.getElementById('loginForm');
    elements.loginInput = document.getElementById('login');
    elements.passwordInput = document.getElementById('password');
    elements.submitButton = document.getElementById('loginButton');
    elements.buttonText = elements.submitButton?.querySelector('.button-text');
    elements.spinner = elements.submitButton?.querySelector('.spinner-border');
    elements.errorMessage = document.getElementById('error-message');
    elements.errorText = document.getElementById('error-text');
}

/**
 * Attache les gestionnaires d'événements
 */
function attachEventListeners() {
    if (elements.form) {
        elements.form.addEventListener('submit', handleLogin);
    }
    
    // Masquer l'erreur lors de la saisie
    [elements.loginInput, elements.passwordInput].forEach(input => {
        if (input) {
            input.addEventListener('input', hideError);
        }
    });
}

/**
 * Gestionnaire principal de connexion
 * @param {Event} event - Événement de soumission du formulaire
 */
async function handleLogin(event) {
    event.preventDefault();
    
    // Validation des données
    if (!validateForm()) {
        return;
    }
    
    const credentials = getFormData();
    
    try {
        setLoadingState(true);
        hideError();
        
        // Étape 1: Authentification API
        const userData = await authenticateUser(credentials);
        
        // Étape 2: Stockage session côté client
        storeUserSession(userData);
        
        // Étape 3: Synchronisation session PHP
        await synchronizePhpSession(userData);
        
        // Étape 4: Redirection
        redirectToApp();
        
    } catch (error) {
        handleLoginError(error);
    } finally {
        setLoadingState(false);
    }
}

/**
 * Valide les données du formulaire
 * @returns {boolean} - True si valide
 */
function validateForm() {
    const login = elements.loginInput?.value?.trim();
    const password = elements.passwordInput?.value;
    
    if (!login) {
        showError('Veuillez saisir votre identifiant');
        elements.loginInput?.focus();
        return false;
    }
    
    if (!password) {
        showError('Veuillez saisir votre mot de passe');
        elements.passwordInput?.focus();
        return false;
    }
    
    if (password.length < 4) {
        showError('Le mot de passe doit contenir au moins 4 caractères');
        elements.passwordInput?.focus();
        return false;
    }
    
    return true;
}

/**
 * Récupère les données du formulaire
 * @returns {Object} - Identifiants de connexion
 */
function getFormData() {
    return {
        login: elements.loginInput.value.trim(),
        password: elements.passwordInput.value
    };
}

/**
 * Authentifie l'utilisateur via l'API
 * @param {Object} credentials - Identifiants
 * @returns {Promise<Object>} - Données utilisateur
 */
async function authenticateUser(credentials) {
    const response = await fetch(`${CONFIG.API_BASE_URL}${CONFIG.ENDPOINTS.LOGIN}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(credentials)
    });
    
    if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.error || 'Erreur lors de la connexion');
    }
    
    return await response.json();
}

/**
 * Stocke la session utilisateur côté client
 * @param {Object} userData - Données utilisateur
 */
function storeUserSession(userData) {
    const sessionData = {
        id: userData.id,
        firstname: userData.firstname,
        role: userData.role,
        seller_ref: userData.seller_ref,
        loginTime: new Date().toISOString()
    };
    
    sessionStorage.setItem('user', JSON.stringify(sessionData));
}

/**
 * Synchronise la session PHP
 * @param {Object} userData - Données utilisateur
 */
async function synchronizePhpSession(userData) {
    await fetch('login.php', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(userData)
    });
}

/**
 * Redirige vers l'application
 */
function redirectToApp() {
    window.location.href = CONFIG.REDIRECTS.SUCCESS;
}

/**
 * Gère les erreurs de connexion
 * @param {Error} error - Erreur capturée
 */
function handleLoginError(error) {
    console.error('Erreur de connexion:', error);
    
    let errorMessage = 'Erreur de connexion au serveur';
    
    if (error.message) {
        errorMessage = error.message;
    }
    
    showError(errorMessage);
    clearPasswordField();
}

/**
 * Affiche un message d'erreur
 * @param {string} message - Message à afficher
 */
function showError(message) {
    if (elements.errorText && elements.errorMessage) {
        elements.errorText.textContent = message;
        elements.errorMessage.classList.remove('d-none');
        elements.errorMessage.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    }
}

/**
 * Masque le message d'erreur
 */
function hideError() {
    if (elements.errorMessage) {
        elements.errorMessage.classList.add('d-none');
    }
}

/**
 * Vide le champ mot de passe
 */
function clearPasswordField() {
    if (elements.passwordInput) {
        elements.passwordInput.value = '';
        elements.passwordInput.focus();
    }
}

/**
 * Gère l'état de chargement de l'interface
 * @param {boolean} isLoading - État de chargement
 */
function setLoadingState(isLoading) {
    if (!elements.submitButton || !elements.buttonText || !elements.spinner) return;
    
    elements.submitButton.disabled = isLoading;
    
    if (isLoading) {
        elements.buttonText.textContent = 'Connexion...';
        elements.spinner.classList.remove('d-none');
    } else {
        elements.buttonText.textContent = 'Se connecter';
        elements.spinner.classList.add('d-none');
    }
}