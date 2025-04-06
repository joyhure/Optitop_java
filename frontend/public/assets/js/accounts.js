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
        login: document.querySelector('#new-request-form input[placeholder="Login"]').value || '',
        role: document.querySelector('#role-select').value || '',
        email: document.querySelector('#new-request-form input[placeholder="Email"]').value || '',
        observations: document.querySelector('#new-request-form input[placeholder="Observations"]').value || '',
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
        
        console.log('Role:', selectedRole, 'Type:', selectedAskType); // Debug

        // Vérification que les deux valeurs sont sélectionnées et non vides
        if (selectedRole && selectedAskType) {
            if ((selectedRole === 'collaborator' || selectedRole === 'manager') && selectedAskType === 'ajout') {
                try {
                    const response = await fetch(`${CONFIG.API_BASE_URL}/sellers/available-sellers`);
                    if (!response.ok) throw new Error('Erreur lors de la récupération des vendeurs');
                    
                    const sellers = await response.json();
                    
                    identifiantSelect.innerHTML = `
                        <option value="" selected disabled>Sélectionner un vendeur</option>
                        ${sellers.map(seller => `
                            <option value="${seller.sellerRef}">${seller.sellerRef}</option>
                        `).join('')}
                    `;
                    
                    identifiantInput.style.display = 'none';
                    identifiantSelect.style.display = 'block';
                    
                } catch (error) {
                    console.error('Erreur:', error);
                }
            } else {
                identifiantSelect.style.display = 'none';
                identifiantInput.style.display = 'block';
            }
        }
    };

    // Écouteurs d'événements
    roleSelect.addEventListener('change', updateIdentifiantField);
    askSelect.addEventListener('change', updateIdentifiantField);
});