document.addEventListener('DOMContentLoaded', function() {
    // 1. Configuration
    const CONFIG = {
        API_BASE_URL: 'http://localhost:8080/api'
    };

    // 2. Sélecteurs DOM
    const DOM = {
        tbody: document.querySelector('#table-quotations-section table tbody'),
        saveButton: document.getElementById('save-changes-button'),
        saveContainer: document.querySelector('.save-button-container'),
        summarySection: document.getElementById('summary'),
        successToast: document.getElementById('successToast'),
        errorToast: document.getElementById('errorToast'),
        sortIcons: document.querySelectorAll('.sort-icon')
    };

    // 3. État de l'application
    const STATE = {
        userSession: JSON.parse(sessionStorage.getItem('user')),
        availableActions: {},
        currentSort: {
            field: 'date',
            order: 'desc'
        }
    };

    // 4. Fonctions utilitaires
    const utils = {
        formatDate(dateString) {
            return new Date(dateString).toLocaleDateString('fr-FR');
        },

        getInitials(sellerRef) {
            return sellerRef?.substring(0, 2).toUpperCase() || 'XX';
        },

        parseFrenchDate(dateStr) {
            const [day, month, year] = dateStr.split('/').map(Number);
            return new Date(year, month - 1, day);
        },

        async fetchApi(endpoint, options = {}) {
            const response = await fetch(`${CONFIG.API_BASE_URL}${endpoint}`, {
                ...options,
                headers: {
                    'Content-Type': 'application/json',
                    ...options.headers
                }
            });
            if (!response.ok) throw new Error(`Erreur API: ${response.status}`);
            return response;
        },

        showToast(type = 'success') {
            const toast = new bootstrap.Toast(type === 'success' ? DOM.successToast : DOM.errorToast);
            toast.show();
        }
    };

    // 5. Gestion des données
    const dataManager = {
        async loadUnvalidatedQuotations() {
            try {
                const startDate = sessionStorage.getItem('startDate');
                const endDate = sessionStorage.getItem('endDate');
                
                const response = await utils.fetchApi(`/quotations/unvalidated?startDate=${startDate}&endDate=${endDate}`);
                const quotations = await response.json();
                console.log(quotations);
                
                return sortManager.sortQuotations(quotations, STATE.currentSort.field, STATE.currentSort.order);
            } catch (error) {
                console.error('Erreur chargement:', error);
                return [];
            }
        },

        async saveChanges(updates) {
            try {
                const response = await utils.fetchApi('/quotations/batch-update', {
                    method: 'PUT',
                    body: JSON.stringify(updates)
                });
                
                if (response.ok) {
                    utils.showToast('success');
                    return true;
                }
            } catch (error) {
                console.error('Erreur sauvegarde:', error);
                utils.showToast('error');
                return false;
            }
        },

        async loadQuotationStats() {
            try {
                const startDate = sessionStorage.getItem('startDate');
                const endDate = sessionStorage.getItem('endDate');
                
                const response = await utils.fetchApi(`/quotations/stats?startDate=${startDate}&endDate=${endDate}`);
                if (response.ok) {
                    const stats = await response.json();
                    this.updateStatsDisplay(stats);
                }
            } catch (error) {
                console.error('Erreur lors du chargement des statistiques:', error);
            }
        },

        updateStatsDisplay(stats) {
            // Mise à jour du taux global
            if (stats.concretizationRate !== undefined) {
                document.getElementById('store-concretization-rate').textContent = 
                    `${stats.concretizationRate.toFixed(1)}%`;
                console.log("taux de concret :", stats.concretizationRate);
            }

            // Mise à jour des nombres de devis
            if (stats.totalQuotations !== undefined && stats.unvalidatedQuotations !== undefined) {
                document.getElementById('quotations-numbers').innerHTML = `
                    Nb : ${stats.totalQuotations}<br>
                    Non validés : ${stats.unvalidatedQuotations}
                `;
                console.log("nombre de devis :", stats.totalQuotations);
                console.log("nombre de devis non validés :", stats.unvalidatedQuotations);
            }

            // Mise à jour du tableau des vendeurs
            const tbody = document.querySelector('#seller-stats-tbody');
            if (tbody && stats.sellerStats) {
                tbody.innerHTML = stats.sellerStats
                    .map(seller => `
                        <tr class="text-center">
                            <td>${utils.getInitials(seller.sellerRef)}</td>
                            <td>[25]</td>
                            <td>[1]</td>
                            <td>[%]</td>
                        </tr>
                    `)
                    .join('');
            }
        }
    };

    // 6. Gestionnaire de tri
    const sortManager = {
        sortQuotations(data, field, order) {
            const sortFunctions = {
                date: (a, b) => new Date(a.date) - new Date(b.date),
                name: (a, b) => (a.sellerRef || '').localeCompare(b.sellerRef || '')
            };

            const sortFunction = sortFunctions[field] || (() => 0);
            const sortedData = [...data].sort(sortFunction);
            
            return order === 'desc' ? sortedData.reverse() : sortedData;
        },

        updateSortIcons(field, order) {
            DOM.sortIcons.forEach(icon => {
                const isActive = icon.dataset.sort === field;
                icon.classList.toggle('active', isActive);
                icon.dataset.order = isActive ? order : 'asc';
                icon.querySelector('use').setAttribute('xlink:href', 
                    `../node_modules/bootstrap-icons/bootstrap-icons.svg#sort-${isActive ? (order === 'asc' ? 'down' : 'up') : 'down'}`
                );
            });
        }
    };

    // 7. Gestionnaire d'interface utilisateur
    const uiManager = {
        async init() {
            await Promise.all([
                this.loadAvailableActions(),
                dataManager.loadQuotationStats()
            ]);
            this.setupRoleBasedAccess();
            this.setupEventListeners();
            await this.refreshQuotations();
        },

        async loadAvailableActions() {
            try {
                const response = await utils.fetchApi('/quotations/actions');
                if (response.ok) {
                    STATE.availableActions = await response.json();
                }
            } catch (error) {
                console.error('Erreur lors du chargement des actions:', error);
            }
        },

        setupRoleBasedAccess() {
            if (DOM.summarySection && STATE.userSession?.role) {
                const requiredRoles = DOM.summarySection.dataset.requiresRole.split(',');
                DOM.summarySection.classList.toggle('d-none', 
                    !requiredRoles.includes(STATE.userSession.role.toLowerCase())
                );
            }

            if (STATE.userSession?.role?.toLowerCase() === 'collaborator') {
                this.hideSellerColumn();
            }
        },

        hideSellerColumn() {
            const nameHeader = document.querySelector('th.table-col-w4');
            if (nameHeader) nameHeader.style.display = 'none';
            
            document.querySelectorAll('td.text-center').forEach(cell => {
                if (cell.textContent.length === 2) cell.style.display = 'none';
            });
        },

        setupEventListeners() {
            // Tri
            DOM.sortIcons.forEach(icon => {
                icon.addEventListener('click', this.handleSort.bind(this));
            });

            // Sauvegarde
            DOM.tbody.addEventListener('change', this.updateSaveButtonState);
            DOM.tbody.addEventListener('input', this.updateSaveButtonState);
            DOM.saveButton?.addEventListener('click', this.handleSave.bind(this));

            // Mise à jour des dates
            window.addEventListener('storage', e => {
                if (e.key === 'startDate' || e.key === 'endDate') {
                    this.refreshQuotations();
                }
            });

            document.addEventListener('datesUpdated', this.refreshQuotations.bind(this));
        },

        async refreshQuotations() {
            const quotations = await dataManager.loadUnvalidatedQuotations();
            this.renderQuotations(quotations);
        },

        renderQuotations(quotations) {
            if (!DOM.tbody) return;

            DOM.tbody.innerHTML = quotations.length === 0 
                ? this.createEmptyRow() 
                : quotations.map(q => this.createQuotationRow(q)).join('');
        },

        createQuotationRow(quotation) {
            return `
                <tr data-quotation-id="${quotation.id}">
                    <td>${utils.formatDate(quotation.date)}</td>
                    <td class="text-center">${utils.getInitials(quotation.sellerRef)}</td>
                    <td>${quotation.client}</td>
                    <td>
                        <select class="form-select form-select-sm action-select" 
                                data-original-value="${quotation.action || ''}">
                            <option value="">${quotation.action || 'Sélectionner'}</option>
                            ${Object.entries(STATE.availableActions)
                                .map(([key, label]) => `
                                    <option value="${key}" ${quotation.action === key ? 'selected' : ''}>
                                        ${label}
                                    </option>
                                `).join('')}
                        </select>
                    </td>
                    <td>
                        <input type="text" class="form-control form-control-sm comment-input" 
                            value="${quotation.comment || ''}" 
                            data-original-value="${quotation.comment || ''}">
                            
                    </td>
                </tr>
            `;
        },

        createEmptyRow() {
            return `
                <tr>
                    <td colspan="5" class="text-center">
                        Aucun devis non validé pour cette période
                    </td>
                </tr>
            `;
        },

        async handleSort(event) {
            const icon = event.currentTarget;
            const field = icon.dataset.sort;
            const order = icon.dataset.order === 'asc' ? 'desc' : 'asc';

            STATE.currentSort = { field, order };
            sortManager.updateSortIcons(field, order);
            await this.refreshQuotations();
        },

        async handleSave() {
            const updates = this.getModifiedQuotations();
            if (updates.length === 0) return;

            if (await dataManager.saveChanges(updates)) {
                this.updateOriginalValues(updates);
                DOM.saveButton.disabled = true;
            }
        },

        getModifiedQuotations() {
            return Array.from(document.querySelectorAll('tr[data-quotation-id]'))
                .map(row => {
                    const id = row.dataset.quotationId;
                    const actionSelect = row.querySelector('.action-select');
                    const commentInput = row.querySelector('.comment-input');

                    if (actionSelect.value === actionSelect.dataset.originalValue &&
                        commentInput.value === commentInput.dataset.originalValue) {
                        return null;
                    }

                    return {
                        id,
                        action: actionSelect.value,
                        comment: commentInput.value
                    };
                })
                .filter(Boolean);
        },

        updateOriginalValues(updates) {
            updates.forEach(update => {
                const row = document.querySelector(`tr[data-quotation-id="${update.id}"]`);
                if (!row) return;

                const actionSelect = row.querySelector('.action-select');
                const commentInput = row.querySelector('.comment-input');
                
                actionSelect.dataset.originalValue = update.action;
                commentInput.dataset.originalValue = update.comment;
            });
        },

        updateSaveButtonState() {
            if (!DOM.saveButton) return;

            const hasChanges = Array.from(document.querySelectorAll('tr[data-quotation-id]'))
                .some(row => {
                    const actionSelect = row.querySelector('.action-select');
                    const commentInput = row.querySelector('.comment-input');
                    return actionSelect.value !== actionSelect.dataset.originalValue ||
                        commentInput.value !== commentInput.dataset.originalValue;
                });

            DOM.saveButton.disabled = !hasChanges;
        }
    };

    // 8. Initialisation
    uiManager.init().catch(console.error);
});