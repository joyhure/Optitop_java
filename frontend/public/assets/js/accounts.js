// Ajout en haut du fichier
const CONFIG = {
    API_BASE_URL: 'http://localhost:8080/api'
};

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
            // Pour un ajout, tous les champs sont requis
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
            if (!isValidEmail(formData.email)) {
                alert('Format d\'email invalide');
                return;
            }
        } else if (askType === 'modification') {
            // Pour une modification, seuls les champs remplis sont envoyés
            const roleValue = document.getElementById('role-select').value;
            const lastnameValue = document.getElementById('lastname').value.trim();
            const firstnameValue = document.getElementById('firstname').value.trim();
            const emailValue = document.getElementById('email').value.trim();

            if (roleValue) formData.role = roleValue;
            if (lastnameValue) formData.lastname = lastnameValue;
            if (firstnameValue) formData.firstname = firstnameValue;
            if (emailValue) formData.email = emailValue;
        }
        // Pour une suppression, seuls login et requestType sont envoyés

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
        await loadPendingAccounts();
        
    } catch (error) {
        console.error('Erreur:', error);
        alert(error.message || 'Erreur lors de l\'envoi de la demande');
    }
}

function isValidEmail(email) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

function hasPermission(user) {
    const authorizedRoles = ['admin', 'manager', 'supermanager'];
    return authorizedRoles.includes(user?.role);
}

const buttonConfig = {
    validate: { class: 'success', text: 'Valider' },
    reject: { class: 'danger', text: 'Refuser' }
};

function renderActionButton(type, user) {
    const config = buttonConfig[type];
    const isAdmin = user?.role === 'admin';
    
    return `
        <button class="btn btn-action btn-${config.class} btn-sm"
            onclick="toggleAction(this)"
            ${!isAdmin ? 'disabled title="Action réservée aux administrateurs"' : ''}>
            ${config.text}
        </button>
    `;
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
        
        // Récupération de tous les champs à masquer/afficher
        const fieldsToToggle = [
            document.querySelector('#lastname').parentElement,
            document.querySelector('#firstname').parentElement,
            document.querySelector('#email').parentElement,
            document.querySelector('#role-select').parentElement
        ];

        if (selectedAskType === 'suppression') {
            // Masquer les champs non nécessaires
            fieldsToToggle.forEach(field => field.style.display = 'none');
            
            try {
                const response = await fetch(`${CONFIG.API_BASE_URL}/users/logins`);
                if (!response.ok) throw new Error('Erreur lors de la récupération des utilisateurs');
                
                const logins = await response.json();
                
                identifiantSelect.innerHTML = `
                    <option value="" selected disabled hidden>Identifiant</option>
                    ${logins.map(login => `
                        <option value="${login}">${login}</option>
                    `).join('')}
                `;
                
                identifiantInput.style.display = 'none';
                identifiantSelect.style.display = 'block';
            } catch (error) {
                console.error('Erreur:', error);
                alert('Erreur lors de la récupération des utilisateurs');
            }
        } else {
            // Réafficher les champs pour les autres types de demande
            fieldsToToggle.forEach(field => field.style.display = 'table-cell');
            
            // Logique existante pour ajout/modification
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
                            <option value="" selected disabled hidden>Identifiant</option>
                            ${data.map(item => `
                                <option value="${item}">${item}</option>
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

        if (selectedAskType === 'ajout' && (selectedRole === 'collaborator' || selectedRole === 'manager')) {
            try {
                const response = await fetch(`${CONFIG.API_BASE_URL}/sellers/available-sellers`);
                if (!response.ok) throw new Error('Erreur lors de la récupération des vendeurs');
                
                const sellers = await response.json();
                console.log('Données reçues:', sellers);
                
                identifiantSelect.innerHTML = `
                    <option value="" selected disabled hidden>Sélectionner un vendeur</option>
                    ${sellers.map(seller => {
                        console.log('Seller:', seller);
                        return `<option value="${seller.sellerRef}">${seller.sellerRef}</option>`;
                    }).join('')}
                `;
                
                identifiantInput.style.display = 'none';
                identifiantSelect.style.display = 'block';
                
            } catch (error) {
                console.error('Erreur:', error);
                alert('Erreur lors de la récupération des vendeurs');
            }
        }
    };

    // Écouteurs d'événements
    roleSelect.addEventListener('change', updateIdentifiantField);
    askSelect.addEventListener('change', updateIdentifiantField);
    loadPendingAccounts();
});

async function loadPendingAccounts() {
    try {
        const userStr = sessionStorage.getItem('user');
        if (!userStr) {
            throw new Error('Utilisateur non connecté');
        }
        const user = JSON.parse(userStr);
        
        const response = await fetch(`${CONFIG.API_BASE_URL}/pending-accounts`, {
            headers: {
                'Authorization': `Bearer ${user.id}`
            }
        });
        
        if (!response.ok) {
            throw new Error('Erreur lors de la récupération des demandes');
        }
        
        const pendingAccounts = await response.json();
        
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
                        ${renderActionButton('validate', user)}
                        ${renderActionButton('reject', user)}
                    </div>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        alert('Erreur lors du chargement des demandes');
    }
}

// Appel de la fonction au chargement et après chaque soumission
document.addEventListener('DOMContentLoaded', loadPendingAccounts);