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
    const role = document.querySelector('#role-select').value;
    const identifiant = (role === 'collaborator' || role === 'manager')
        ? document.querySelector('#identifiant-select').value
        : document.querySelector('#identifiant').value;

    const formData = {
        nom: document.querySelector('#new-request-form input[placeholder="Nom"]').value || '',
        prenom: document.querySelector('#new-request-form input[placeholder="Prénom"]').value || '',
        login: document.querySelector('#new-request-form input[placeholder="Login"]').value || '',
        role: document.querySelector('#role-select').value || 'collaborator',
        email: document.querySelector('#new-request-form input[placeholder="Email"]').value || '',
        observations: document.querySelector('#new-request-form input[placeholder="Observations"]').value || '',
        identifiant: identifiant,
    };
    
    console.log('Données à envoyer:', formData);
    // TODO: Appel API
}

document.addEventListener('DOMContentLoaded', function() {
    const roleSelect = document.getElementById('role-select');
    const identifiantInput = document.getElementById('identifiant');
    
    // Création du select pour l'identifiant
    const identifiantSelect = document.createElement('select');
    identifiantSelect.className = 'form-select form-select-sm';
    identifiantSelect.id = 'identifiant-select';
    identifiantSelect.style.display = 'none';
    
    // Insertion du select après l'input
    identifiantInput.parentNode.insertBefore(identifiantSelect, identifiantInput.nextSibling);

    roleSelect.addEventListener('change', async function() {
        const selectedRole = this.value;
        console.log('Rôle sélectionné:', selectedRole); // Debug

        if (selectedRole === 'collaborator' || selectedRole === 'manager') {
            try {
                const response = await fetch(`${CONFIG.API_BASE_URL}/sellers/available-sellers`);
                console.log('Réponse API:', response); // Debug
                
                if (!response.ok) throw new Error('Erreur lors de la récupération des vendeurs');
                
                const sellers = await response.json();
                console.log('Vendeurs disponibles:', sellers); // Debug
                
                // Modification ici pour gérer le format DTO
                identifiantSelect.innerHTML = `
                    <option value="">Vendeur</option>
                    ${sellers.map(seller => `
                        <option value="${seller.sellerRef}">${seller.sellerRef}</option>
                    `).join('')}
                `;
                
                // Afficher/masquer les éléments
                identifiantInput.style.display = 'none';
                identifiantSelect.style.display = 'block';
                
            } catch (error) {
                console.error('Erreur:', error);
                // Afficher un message d'erreur à l'utilisateur
                alert('Impossible de récupérer la liste des vendeurs');
            }
        } else {
            // Rôles admin ou supermanager
            identifiantSelect.style.display = 'none';
            identifiantInput.style.display = 'block';
        }
    });
});