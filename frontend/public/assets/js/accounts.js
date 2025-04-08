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

function submitRequest() {
    const role = document.getElementById('role-select').value;
    const askType = document.getElementById('ask-select').value;
    
    const identifiant = (role === 'collaborator' || role === 'manager') && askType === 'ajout'
        ? document.getElementById('identifiant-select').value
        : document.getElementById('identifiant').value;

    const formData = {
        nom: document.querySelector('#new-request-form input[placeholder="Nom"]').value || '',
        prenom: document.querySelector('#new-request-form input[placeholder="Prénom"]').value || '',
        email: document.querySelector('#new-request-form input[placeholder="Email"]').value || '',
        role: document.querySelector('#role-select').value || '',
        identifiant: identifiant,
    };
    
    console.log('Données à envoyer:', formData);
    // TODO: Appel API
}

document.addEventListener('DOMContentLoaded', function() {
    const roleSelect = document.getElementById('role-select');
    const askSelect = document.getElementById('ask-select');
    const identifiantInput = document.getElementById('identifiant');
    
    // Création du select pour l'identifiant
    const identifiantSelect = document.createElement('select');
    identifiantSelect.className = 'form-select form-select-sm';
    identifiantSelect.id = 'identifiant-select';
    identifiantSelect.style.display = 'none';
    
    // Insertion du select après l'input
    identifiantInput.parentNode.insertBefore(identifiantSelect, identifiantInput.nextSibling);

    // Fonction pour gérer l'affichage du bon champ identifiant
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
                console.log('Données reçues:', sellers); // Debug
                
                identifiantSelect.innerHTML = `
                    <option value="" selected disabled hidden>Sélectionner un vendeur</option>
                    ${sellers.map(seller => {
                        console.log('Seller:', seller); // Debug
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
});