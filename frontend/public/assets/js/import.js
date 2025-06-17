/**
 * Gestionnaire d'importation de données CSV Optitop
 * Gère l'upload par chunks et le suivi de progression
 */

// ===== CONFIGURATION =====

const CONFIG = {
    API_BASE_URL: 'http://localhost:8080',
    ENDPOINTS: {
        IMPORT: '/api/sales/import'
    },
    CHUNK_SIZE: 1024 * 1024, // 1MB
    LOCALE: 'fr-FR'
};

// ===== ÉLÉMENTS DOM =====

const elements = {
    form: null,
    fileInput: null,
    progressBar: null,
    progressDiv: null,
    statusDiv: null
};

// ===== INITIALISATION =====

/**
 * Initialisation au chargement du DOM
 */
document.addEventListener('DOMContentLoaded', () => {
    try {
        initializeElements();
        bindEvents();
    } catch (error) {
        console.error('Erreur lors de l\'initialisation de l\'import:', error);
    }
});

/**
 * Initialise les références aux éléments DOM
 */
function initializeElements() {
    elements.form = document.querySelector('#importForm');
    elements.fileInput = document.querySelector('#csvFile');
    elements.progressBar = document.querySelector('#uploadProgress .progress-bar');
    elements.progressDiv = document.querySelector('#uploadProgress');
    elements.statusDiv = document.querySelector('#importStatus');
}

/**
 * Associe les événements aux éléments
 */
function bindEvents() {
    if (elements.form) {
        elements.form.addEventListener('submit', handleFormSubmit);
    }
}

// ===== UTILITAIRES =====

/**
 * Utilitaire pour les appels API
 */
const apiUtils = {
    /**
     * Effectue un appel API pour l'upload d'un chunk
     * @param {FormData} formData - Données du chunk à envoyer
     * @returns {Promise<Response>} - Réponse de l'API
     */
    async uploadChunk(formData) {
        try {
            const response = await fetch(`${CONFIG.API_BASE_URL}${CONFIG.ENDPOINTS.IMPORT}`, {
                method: 'POST',
                body: formData
            });
            
            if (!response.ok) {
                throw new Error(`Erreur API: ${response.status}`);
            }
            
            return response;
        } catch (error) {
            console.error('Erreur lors de l\'upload du chunk:', error);
            throw error;
        }
    }
};

/**
 * Utilitaires de gestion des fichiers
 */
const fileUtils = {
    /**
     * Calcule le nombre total de chunks nécessaires
     * @param {File} file - Fichier à traiter
     * @returns {number} - Nombre total de chunks
     */
    calculateTotalChunks(file) {
        return Math.ceil(file.size / CONFIG.CHUNK_SIZE);
    },

    /**
     * Extrait un chunk du fichier
     * @param {File} file - Fichier source
     * @param {number} chunkIndex - Index du chunk
     * @returns {Blob} - Chunk extrait
     */
    getChunk(file, chunkIndex) {
        const start = chunkIndex * CONFIG.CHUNK_SIZE;
        const end = Math.min(start + CONFIG.CHUNK_SIZE, file.size);
        return file.slice(start, end);
    }
};

/**
 * Utilitaires d'interface utilisateur
 */
const uiUtils = {
    /**
     * Affiche la barre de progression
     */
    showProgress() {
        if (elements.progressDiv) {
            elements.progressDiv.classList.remove('d-none');
        }
    },

    /**
     * Met à jour la progression
     * @param {number} percentage - Pourcentage de progression (0-100)
     */
    updateProgress(percentage) {
        if (elements.progressBar) {
            elements.progressBar.style.width = `${percentage}%`;
            elements.progressBar.textContent = `${percentage}%`;
        }
    },

    /**
     * Affiche un message de statut
     * @param {string} message - Message à afficher
     * @param {string} type - Type de message (info, success, danger)
     */
    showStatus(message, type = 'info') {
        if (elements.statusDiv) {
            elements.statusDiv.classList.remove('d-none');
            elements.statusDiv.className = `alert alert-${type}`;
            elements.statusDiv.textContent = message;
        }
    },

    /**
     * Remet à zéro le formulaire
     */
    resetForm() {
        if (elements.form) {
            elements.form.reset();
        }
    }
};

// ===== GESTION DES ÉVÉNEMENTS =====

/**
 * Gestionnaire de soumission du formulaire
 * @param {Event} e - Événement de soumission
 */
async function handleFormSubmit(e) {
    e.preventDefault();
    
    const file = elements.fileInput?.files[0];
    if (!file) {
        uiUtils.showStatus('Veuillez sélectionner un fichier', 'danger');
        return;
    }

    try {
        await processFileUpload(file);
    } catch (error) {
        console.error('Erreur lors du traitement du fichier:', error);
        uiUtils.showStatus('Erreur lors de l\'import', 'danger');
    }
}

// ===== TRAITEMENT DE L'UPLOAD =====

/**
 * Traite l'upload complet d'un fichier
 * @param {File} file - Fichier à uploader
 */
async function processFileUpload(file) {
    const totalChunks = fileUtils.calculateTotalChunks(file);
    let currentChunk = 0;

    // Initialisation de l'interface
    uiUtils.showProgress();
    uiUtils.showStatus('Import en cours...', 'info');

    try {
        while (currentChunk < totalChunks) {
            await uploadSingleChunk(file, currentChunk, totalChunks);
            currentChunk++;
            
            // Mise à jour de la progression
            const progress = Math.round((currentChunk / totalChunks) * 100);
            uiUtils.updateProgress(progress);
        }

        // Succès de l'import
        uiUtils.showStatus('Import réussi !', 'success');
        uiUtils.resetForm();
        
    } catch (error) {
        throw error;
    }
}

/**
 * Upload un chunk individuel
 * @param {File} file - Fichier source
 * @param {number} chunkIndex - Index du chunk actuel
 * @param {number} totalChunks - Nombre total de chunks
 */
async function uploadSingleChunk(file, chunkIndex, totalChunks) {
    try {
        const chunk = fileUtils.getChunk(file, chunkIndex);
        const formData = createChunkFormData(chunk, chunkIndex, totalChunks);
        
        await apiUtils.uploadChunk(formData);
        
    } catch (error) {
        console.error(`Erreur lors de l'upload du chunk ${chunkIndex}:`, error);
        throw new Error('Erreur serveur lors de l\'upload');
    }
}

/**
 * Crée les données FormData pour un chunk
 * @param {Blob} chunk - Données du chunk
 * @param {number} chunkIndex - Index du chunk
 * @param {number} totalChunks - Nombre total de chunks
 * @returns {FormData} - Données formatées pour l'API
 */
function createChunkFormData(chunk, chunkIndex, totalChunks) {
    const formData = new FormData();
    formData.append('file', chunk);
    formData.append('chunk', chunkIndex);
    formData.append('totalChunks', totalChunks);
    return formData;
}