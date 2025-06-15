/**
 * Gestionnaire de connexion Optitop
 */

// Configuration
const CONFIG = {
  API_BASE_URL: "http://localhost:8080",
  ENDPOINTS: {
    LOGIN: "/api/auth/login",
  },
  PAGES: {
    DASHBOARD: "dashboard.php",
  },
};

// Éléments DOM
const elements = {
  form: null,
  loginInput: null,
  passwordInput: null,
};

/**
 * Initialisation au chargement du DOM
 */
document.addEventListener("DOMContentLoaded", function () {
  initializeElements();
  attachEventListeners();
});

/**
 * Initialise les références aux éléments DOM
 */
function initializeElements() {
  elements.form = document.querySelector("#loginForm");
  elements.loginInput = document.querySelector("#login");
  elements.passwordInput = document.querySelector("#password");
}

/**
 * Attache les gestionnaires d'événements
 */
function attachEventListeners() {
  if (elements.form) {
    elements.form.addEventListener("submit", handleLogin);
  }
}

/**
 * Gestionnaire principal de connexion
 * @param {Event} event - Événement de soumission du formulaire
 */
async function handleLogin(event) {
  event.preventDefault();

  try {
    // Étape 1: Authentification via API
    const userData = await authenticateUser();

    // Étape 2: Stockage session côté client
    storeUserSession(userData);

    // Étape 3: Synchronisation session PHP
    await synchronizePhpSession(userData);

    // Étape 4: Redirection
    redirectToApp();
  } catch (error) {
    handleLoginError(error);
  }
}

/**
 * Authentifie l'utilisateur via l'API
 * @returns {Promise<Object>} - Données utilisateur
 */
async function authenticateUser() {
  const credentials = {
    login: elements.loginInput.value,
    password: elements.passwordInput.value,
  };

  const response = await fetch(
    `${CONFIG.API_BASE_URL}${CONFIG.ENDPOINTS.LOGIN}`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(credentials),
    }
  );

  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.error || "Erreur lors de la connexion");
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
  };

  sessionStorage.setItem("user", JSON.stringify(sessionData));
}

/**
 * Synchronise la session PHP
 * @param {Object} userData - Données utilisateur
 */
async function synchronizePhpSession(userData) {
  await fetch("login.php", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(userData),
  });
}

/**
 * Redirige vers l'application
 */
function redirectToApp() {
  window.location.href = CONFIG.PAGES.DASHBOARD;
}

/**
 * Gère les erreurs de connexion
 * @param {Error} error - Erreur capturée
 */
function handleLoginError(error) {
  console.error("Erreur:", error);

  // Afficher l'erreur à l'utilisateur
  const errorMessage = error.message || "Erreur de connexion au serveur";
  alert(errorMessage);

  // Réinitialiser le champ mot de passe
  clearPasswordField();
}

/**
 * Vide le champ mot de passe
 */
function clearPasswordField() {
  if (elements.passwordInput) {
    elements.passwordInput.value = "";
  }
}
