document.addEventListener('DOMContentLoaded', function() {
    const CONFIG = {
        API_BASE_URL: 'http://localhost:8080/api'  // Modification de l'URL de base
    };

    const DOM = {
        tbody: document.getElementById('table-baskets-body')
    };

    const STATE = {
        startDate: sessionStorage.getItem('startDate'),
        endDate: sessionStorage.getItem('endDate')
    };

    const utils = {
        formatCurrency(amount) {
            // Gestion des valeurs null ou undefined
            if (amount === null || amount === undefined) return '0.00€';
            return `${amount.toFixed(2)}€`;
        },
        
        getInitials(sellerRef) {
            return sellerRef?.substring(0, 2).toUpperCase() || 'XX';
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
            return response.json();
        }
    };

    const dataManager = {
        async loadAverageBaskets() {
            try {
                console.log('Config:', CONFIG);  // Debug
                console.log('Dates:', { startDate: STATE.startDate, endDate: STATE.endDate });
                const stats = await utils.fetchApi(
                    `/invoices/average-baskets?startDate=${STATE.startDate}&endDate=${STATE.endDate}`
                );
                console.log('Statistiques reçues:', stats);
                this.updateDisplay(stats);
            } catch (error) {
                console.error('Erreur chargement paniers moyens:', error, error.message);
            }
        },

        updateDisplay(stats) {
            if (!DOM.tbody) return;

            // Calcul des totaux
            let totalBasket = 0;
            let totalCount = 0;
            let sumBasket = 0;       
            let sumFramesP1 = 0; 
            let sumLensesP1 = 0; 
            let sumP2 = 0;          

            // Lignes des vendeurs
            const rows = stats.map(seller => {
                totalCount += seller.invoiceCount || 0;
                sumBasket += (seller.averageBasket || 0) * (seller.invoiceCount || 0);
                sumFramesP1 += (seller.averageFramesP1 || 0) * (seller.invoiceCount || 0);
                sumLensesP1 += (seller.averageLensesP1 || 0) * (seller.invoiceCount || 0);
                sumP2 += (seller.averageP2 || 0) * (seller.invoiceCount || 0);

                return `
                    <tr>
                        <td class="text-center">${utils.getInitials(seller.sellerRef)}</td>
                        <td class="text-center">${utils.formatCurrency(seller.averageBasket)}</td>
                        <td class="text-center">${seller.invoiceCount}</td>
                        <td class="text-center">${utils.formatCurrency(seller.averageFramesP1)}</td>
                        <td class="text-center">${utils.formatCurrency(seller.averageLensesP1)}</td>
                        <td class="text-center">${utils.formatCurrency(seller.averageP2)}</td>
                    </tr>
                `;
            });

            // Calcul des vraies moyennes globales
            const avgBasket = totalCount ? sumBasket / totalCount : 0;
            const avgFramesP1 = totalCount ? sumFramesP1 / totalCount : 0;
            const avgLensesP1 = totalCount ? sumLensesP1 / totalCount : 0;
            const avgP2 = totalCount ? sumP2 / totalCount : 0;

            // Ajout de la ligne Total
            rows.push(`
                <tr class="fw-bold">
                    <td class="text-center">Total</td>
                    <td class="text-center">${utils.formatCurrency(avgBasket)}</td>
                    <td class="text-center">${totalCount}</td>
                    <td class="text-center">${utils.formatCurrency(avgFramesP1)}</td>
                    <td class="text-center">${utils.formatCurrency(avgLensesP1)}</td>
                    <td class="text-center">${utils.formatCurrency(avgP2)}</td>
                </tr>
            `);

            DOM.tbody.innerHTML = rows.join('');
        }
    };

    // Initialisation
    dataManager.loadAverageBaskets();
});