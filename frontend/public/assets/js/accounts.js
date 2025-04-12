// Configuration
const CONFIG = {
    API_BASE_URL: 'http://localhost:8080/api',
    BUTTON_CONFIG: {
        validate: { class: 'success', text: 'Valider' },
        reject: { class: 'danger', text: 'Refuser' }
    }
};

// Service API
const accountsService = {
    async getPendingAccounts(userId) {
        const response = await fetch(`${CONFIG.API_BASE_URL}/pending-accounts`, {
            headers: { 'Authorization': `Bearer ${userId}` }
        });
        if (!response.ok) throw new Error('Erreur lors de la récupération des demandes');
        return response.json();
    },

    async validateAccount(accountId, userId) {
        const response = await fetch(`${CONFIG.API_BASE_URL}/pending-accounts/validate/${accountId}`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${userId}`,
                'Content-Type': 'application/json'
            }
        });
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Erreur lors de la validation');
        }
    },

    async rejectAccount(accountId, userId) {
        const response = await fetch(`${CONFIG.API_BASE_URL}/pending-accounts/reject/${accountId}`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${userId}`,
                'Content-Type': 'application/json'
            }
        });
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Erreur lors du rejet');
        }
    },

    async getAllUsers() {
        const user = utils.getCurrentUser();
        
        // Vérification préalable
        if (user?.role !== 'admin') {
            throw new Error('Accès non autorisé');
        }

        const response = await fetch(`${CONFIG.API_BASE_URL}/users/all`, {
            headers: { 
                'Authorization': `Bearer ${user.id}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.status === 403) {
            throw new Error('Accès non autorisé');
        }

        if (!response.ok) {
            throw new Error('Erreur lors de la récupération des utilisateurs');
        }

        return response.json();
    }
};

// Utilitaires
const utils = {
    isValidEmail: (email) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email),
    
    hasPermission: (user) => {
        const authorizedRoles = ['admin', 'manager', 'supermanager'];
        return authorizedRoles.includes(user?.role);
    },
    
    getCurrentUser: () => {
        const userStr = sessionStorage.getItem('user');
        if (!userStr) throw new Error('Utilisateur non connecté');
        return JSON.parse(userStr);
    }
};

// Gestionnaire du rendu UI
const uiManager = {
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

    updateTable(pendingAccounts, user) {
        const tbody = document.querySelector('#accounts-ask-logs tbody');
        tbody.innerHTML = pendingAccounts.map(account => `
            <tr>
                <td class="text-center align-middle">${new Date(account.createdAt).toLocaleDateString('fr-FR')}</td>
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

    updateUsersTable(users) {
        const tbody = document.querySelector('#users-table tbody');
        tbody.innerHTML = users.map(user => `
            <tr>
                <td class="text-center align-middle">${new Date(user.createdAt).toLocaleDateString('fr-FR')}</td>
                <td class="text-center align-middle">${user.login || 'N/A'}</td>
                <td class="text-center align-middle">${user.role || 'N/A'}</td>
                <td class="text-center align-middle">${user.lastname || 'N/A'}</td>
                <td class="text-center align-middle">${user.firstname || 'N/A'}</td>
                <td class="text-center align-middle">${user.email || 'N/A'}</td>
            </tr>
        `).join('');
    },

    initializeAdminFeatures() {
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
    },

    async loadUsersTable() {
        try {
            const users = await accountsService.getAllUsers();
            this.updateUsersTable(users);
        } catch (error) {
            console.error('Erreur chargement utilisateurs:', error);
        }
    }
};

// Contrôleur principal
const accountsController = {
    async init() {
        this.setupEventListeners();
        await this.loadPendingAccounts();
        uiManager.initializeAdminFeatures();
    },

    setupEventListeners() {
        document.getElementById('role-select')?.addEventListener('change', this.handleFieldUpdate);
        document.getElementById('ask-select')?.addEventListener('change', this.handleFieldUpdate);
    },

    async loadPendingAccounts() {
        try {
            const user = utils.getCurrentUser();
            const pendingAccounts = await accountsService.getPendingAccounts(user.id);
            uiManager.updateTable(pendingAccounts, user);
        } catch (error) {
            console.error('Erreur:', error);
            alert('Erreur lors du chargement des demandes');
        }
    },

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
                    // Recharger les deux tableaux
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
            console.error('Erreur:', error);
            alert(error.message);
        }
    },

    async loadUsers() {
        try {
            const users = await accountsService.getAllUsers();
            uiManager.updateUsersTable(users);
        } catch (error) {
            console.error('Erreur:', error);
            alert('Erreur lors du chargement des utilisateurs');
        }
    }
};

// Initialisation
document.addEventListener('DOMContentLoaded', () => accountsController.init());

function showNewRequestForm() {
    // Affiche le formulaire
    const form = document.getElementById('new-request-form');
    form.style.display = 'block';
    
    // Ouvre la section si elle est fermée
    const collapseSection = document.getElementById('collapse-user-creation');
    if (!collapseSection.classList.contains('show')) {
        new bootstrap.Collapse(collapseSection).show();
    }
}

function cancelRequest() {
    const form = document.getElementById('new-request-form');
    // Cache le formulaire
    form.style.display = 'none';
    // Réinitialise les champs
    form.querySelectorAll('input, select').forEach(input => input.value = '');
}

async function submitRequest() {
    // Récupération et parsing de l'objet user
    const userStr = sessionStorage.getItem('user');
    if (!userStr) {
        throw new Error('Utilisateur non connecté');
    }

    const user = JSON.parse(userStr);
    console.log('User data:', user); // Debug

    const askType = document.getElementById('ask-select').value;
    if (!askType) {
        alert('Veuillez sélectionner un type de demande');
        return;
    }

    try {
        const identifiantValue = document.getElementById('identifiant-select').style.display === 'block' 
            ? document.getElementById('identifiant-select').value
            : document.getElementById('identifiant').value;

        // Base formData avec uniquement les champs requis
        let formData = {
            login: identifiantValue,
            requestType: askType
        };

        // Ajout des champs selon le type de demande
        if (askType === 'ajout') {
            formData = {
                ...formData,
                lastname: document.getElementById('lastname').value.trim(),
                firstname: document.getElementById('firstname').value.trim(),
                email: document.getElementById('email').value.trim(),
                role: document.getElementById('role-select').value
            };

            if (!formData.lastname || !formData.firstname || !formData.email || !formData.role || !formData.login) {
                alert('Tous les champs sont obligatoires pour une création');
                return;
            }
            if (!utils.isValidEmail(formData.email)) {
                alert('Format d\'email invalide');
                return;
            }
        } else if (askType === 'modification') {
            const roleValue = document.getElementById('role-select').value;
            const lastnameValue = document.getElementById('lastname').value.trim();
            const firstnameValue = document.getElementById('firstname').value.trim();
            const emailValue = document.getElementById('email').value.trim();

            if (roleValue) formData.role = roleValue;
            if (lastnameValue) formData.lastname = lastnameValue;
            if (firstnameValue) formData.firstname = firstnameValue;
            if (emailValue) formData.email = emailValue;
        }

        console.log('Données à envoyer:', formData);

        const response = await fetch(`${CONFIG.API_BASE_URL}/pending-accounts`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${user.id}`
            },
            body: JSON.stringify(formData)
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Erreur lors de la soumission');
        }

        alert('Demande envoyée avec succès');
        cancelRequest();
        await accountsController.loadPendingAccounts();
        
    } catch (error) {
        console.error('Erreur:', error);
        alert(error.message || 'Erreur lors de l\'envoi de la demande');
    }
}

document.addEventListener('DOMContentLoaded', function() {
    const roleSelect = document.getElementById('role-select');
    const askSelect = document.getElementById('ask-select');
    const identifiantInput = document.getElementById('identifiant');
    const identifiantSelect = document.createElement('select');
    identifiantSelect.className = 'form-select form-select-sm';
    identifiantSelect.id = 'identifiant-select';
    identifiantSelect.style.display = 'none';
    identifiantInput.parentNode.insertBefore(identifiantSelect, identifiantInput.nextSibling);

    // Fonction pour gérer l'affichage identifiant
    const updateIdentifiantField = async () => {
        const selectedRole = roleSelect.value;
        const selectedAskType = askSelect.value;
        
        const fieldsToToggle = [
            document.querySelector('#lastname')?.parentElement,
            document.querySelector('#firstname')?.parentElement,
            document.querySelector('#email')?.parentElement,
            document.querySelector('#role-select')?.parentElement
        ].filter(Boolean);

        if (selectedAskType === 'suppression') {
            fieldsToToggle.forEach(field => field.style.display = 'none');

        } else {
            fieldsToToggle.forEach(field => field.style.display = 'table-cell');
            
            if (selectedRole && selectedAskType) {
                try {
                    let endpoint = '';
                    
                    if (selectedAskType === 'ajout' && (selectedRole === 'collaborator' || selectedRole === 'manager')) {
                        endpoint = '/sellers/available-sellers';
                    } else if (selectedAskType === 'modification') {
                        endpoint = '/users/logins';
                    }
                    
                    if (endpoint) {
                        const response = await fetch(`${CONFIG.API_BASE_URL}${endpoint}`);
                        if (!response.ok) throw new Error('Erreur lors de la récupération des données');
                        
                        const data = await response.json();
                        
                        identifiantSelect.innerHTML = `
                            <option value="" selected disabled hidden>
                                ${endpoint.includes('sellers') ? 'Vendeur' : 'Identifiant'}
                            </option>
                            ${data.map(item => `
                                <option value="${typeof item === 'object' ? item.sellerRef : item}">
                                    ${typeof item === 'object' ? item.sellerRef : item}
                                </option>
                            `).join('')}
                        `;
                        
                        identifiantInput.style.display = 'none';
                        identifiantSelect.style.display = 'block';
                    } else {
                        identifiantSelect.style.display = 'none';
                        identifiantInput.style.display = 'block';
                    }
                } catch (error) {
                    console.error('Erreur:', error);
                    alert('Erreur lors de la récupération des données');
                }
            }
        }
    };

    // Écouteurs d'événements
    roleSelect.addEventListener('change', updateIdentifiantField);
    askSelect.addEventListener('change', updateIdentifiantField);
    accountsController.loadPendingAccounts();
});