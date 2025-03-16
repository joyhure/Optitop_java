document.addEventListener('DOMContentLoaded', function() {
    const CONFIG = {
        API_BASE_URL: 'http://localhost:8080/api'
    };

    const DOM = {
        basketsBody: document.getElementById('table-baskets-body'),
        framesBody: document.getElementById('table-frames-body'),
        cardPm: document.getElementById('card-pm'),
        cardP2: document.getElementById('card-p2')
    };

    const STATE = {
        startDate: sessionStorage.getItem('startDate'),
        endDate: sessionStorage.getItem('endDate')
    };

    const utils = {
        formatCurrency(amount) {
            if (amount === null || amount === undefined) return 'Indéfini';
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
        async loadAllData() {
            await Promise.all([
                this.loadAverageBaskets(),
                this.loadFrameStats()
            ]);
        },

        async loadAverageBaskets() {
            try {
                console.log('Config:', CONFIG);
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
            if (!DOM.basketsBody) return;

            // Calcul des totaux
            let totalCount = 0;
            let sumBasket = 0;       
            let sumFramesP1 = 0; 
            let sumLensesP1 = 0; 
            let totalP2Amount = 0;    // Un seul compteur pour le montant P2
            let totalP2Count = 0;     // Un seul compteur pour le nombre de P2

            // Lignes des vendeurs
            const rows = stats.map(seller => {
                console.log('Données vendeur P2:', {
                    ref: seller.sellerRef,
                    amount: seller.totalAmount,
                    p2Count: seller.p2Count,
                    averageP2: seller.averageP2
                });

                totalCount += seller.invoiceCount || 0;
                sumBasket += (seller.averageBasket || 0) * (seller.invoiceCount || 0);
                sumFramesP1 += (seller.averageP1MON || 0) * (seller.invoiceCount || 0); 
                sumLensesP1 += (seller.averageP1VER || 0) * (seller.invoiceCount || 0); 
                
                // Pour les P2, utiliser averageP2 * p2Count pour avoir le montant total
                totalP2Amount += (seller.averageP2 || 0) * (seller.p2Count || 0);
                totalP2Count += seller.p2Count || 0;

                return `
                    <tr>
                        <td class="text-center">${utils.getInitials(seller.sellerRef)}</td>
                        <td class="text-center">${utils.formatCurrency(seller.averageBasket)}</td>
                        <td class="text-center">${seller.invoiceCount}</td>
                        <td class="text-center">${utils.formatCurrency(seller.averageP1MON)}</td>
                        <td class="text-center">${utils.formatCurrency(seller.averageP1VER)}</td>
                        <td class="text-center">${utils.formatCurrency(seller.averageP2)}</td>
                    </tr>
                `;
            });

            // Calcul des moyennes globales avec logs pour le P2 Total
            console.log('Calcul PM P2 Total:', {
                totalP2Amount,
                totalP2Count,
                avgP2Total: totalP2Count ? totalP2Amount / totalP2Count : 0
            });

            const avgBasket = totalCount ? sumBasket / totalCount : 0;
            const avgFramesP1 = totalCount ? sumFramesP1 / totalCount : 0;
            const avgLensesP1 = totalCount ? sumLensesP1 / totalCount : 0;
            const avgP2Total = totalP2Count ? totalP2Amount / totalP2Count : 0;

            console.log('Variables P2 utilisées:', {
                seller_totalAmountP2: stats[0]?.totalAmountP2, // exemple première ligne
                seller_p2Count: stats[0]?.p2Count,
                totalP2Amount,
                totalP2Count
            });

            // Mise à jour des cartes de résumé
            DOM.cardPm.textContent = utils.formatCurrency(avgBasket);
            DOM.cardP2.textContent = utils.formatCurrency(avgP2Total);

            // Ajout de la ligne Total
            rows.push(`
                <tr class="fw-bold">
                    <td class="text-center">Total</td>
                    <td class="text-center">${utils.formatCurrency(avgBasket)}</td>
                    <td class="text-center">${totalCount}</td>
                    <td class="text-center">${utils.formatCurrency(avgFramesP1)}</td>
                    <td class="text-center">${utils.formatCurrency(avgLensesP1)}</td>
                    <td class="text-center">${utils.formatCurrency(avgP2Total)}</td>
                </tr>
            `);

            DOM.basketsBody.innerHTML = rows.join('');
        },

        async loadFrameStats() {
            try {
                const stats = await utils.fetchApi(
                    `/invoices/frame-stats?startDate=${STATE.startDate}&endDate=${STATE.endDate}`
                );
                this.updateFrameStats(stats);
            } catch (error) {
                console.error('Erreur chargement statistiques montures:', error);
            }
        },

        updateFrameStats(stats) {
            if (!DOM.framesBody) return;

            let totalFrames = 0;
            let totalPremiumFrames = 0;
            let totalBonus = 0;
            const BONUS_PER_FRAME = 5;

            const rows = stats.map(seller => {
                totalFrames += seller.totalFrames || 0;
                totalPremiumFrames += seller.premiumFrames || 0;
                const bonus = (seller.premiumFrames || 0) * BONUS_PER_FRAME;
                totalBonus += bonus;

                const percentage = seller.totalFrames > 0 
                    ? (seller.premiumFrames * 100 / seller.totalFrames).toFixed(1) 
                    : 0;

                return `
                    <tr>
                        <td class="text-center">${utils.getInitials(seller.sellerRef)}</td>
                        <td class="text-center">${seller.totalFrames || 0}</td>
                        <td class="text-center">${seller.premiumFrames || 0}</td>
                        <td class="text-center">${percentage}%</td>
                        <td class="text-center">${utils.formatCurrency(bonus)}</td>
                    </tr>
                `;
            });

            // Calcul du pourcentage total
            const totalPercentage = totalFrames > 0 
                ? (totalPremiumFrames * 100 / totalFrames).toFixed(1) 
                : 0;

            // Ajout ligne Total
            rows.push(`
                <tr class="fw-bold">
                    <td class="text-center">Total</td>
                    <td class="text-center">${totalFrames}</td>
                    <td class="text-center">${totalPremiumFrames}</td>
                    <td class="text-center">${totalPercentage}%</td>
                    <td class="text-center">${utils.formatCurrency(totalBonus)}</td>
                </tr>
            `);

            DOM.framesBody.innerHTML = rows.join('');
        }
    };

    // Initialisation
    dataManager.loadAllData();
});