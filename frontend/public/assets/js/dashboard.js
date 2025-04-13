document.addEventListener('DOMContentLoaded', async () => {
    // Configuration
    const CONFIG = {
        API_BASE_URL: 'http://localhost:8080/api',
        LOCALE: 'fr-FR',
        CURRENCY: 'EUR',
        BONUS_PER_FRAME: 5
    };

    const utils = {
        async fetchApi(endpoint, options = {}) {
            try {
                const response = await fetch(`${CONFIG.API_BASE_URL}${endpoint}`, {
                    ...options,
                    headers: {
                        'Content-Type': 'application/json',
                        ...options.headers
                    }
                });
                if (!response.ok) throw new Error(`Erreur API: ${response.status}`);
                return await response.json();
            } catch (error) {
                console.error(`Erreur lors de l'appel API ${endpoint}:`, error);
                throw error;
            }
        }
    };

    // Formatage monétaire
    const formatCurrency = (amount) => {
        if (amount === null || amount === undefined) return '-';
        return new Intl.NumberFormat(CONFIG.LOCALE, {
            style: 'currency',
            currency: CONFIG.CURRENCY,
            minimumFractionDigits: 0,
            maximumFractionDigits: 0
        }).format(amount);
    };

    const formatPercent = (value) => {
        return new Intl.NumberFormat(CONFIG.LOCALE, {
            style: 'percent',
            minimumFractionDigits: 1,
            maximumFractionDigits: 1
        }).format(value/100);
    };

    // Mise à jour des données personnelles
    const user = JSON.parse(sessionStorage.getItem('user'));
    if (user?.firstname) {
        const firstNameElement = document.querySelector('#personal-section h4');
        if (firstNameElement) {
            firstNameElement.textContent = user.firstname;
        }
    }

    // Récupération et affichage du CA total
    const loadTotalRevenue = async () => {
        try {
            // Récupérer les dates depuis le sessionStorage
            const startDate = sessionStorage.getItem('startDate');
            const endDate = sessionStorage.getItem('endDate');

            if (startDate && endDate) {
                const response = await fetch(
                    `${CONFIG.API_BASE_URL}/invoices/period-revenue?startDate=${startDate}&endDate=${endDate}`
                );

                if (!response.ok) throw new Error('Erreur lors de la récupération du CA');

                const data = await response.json();
                const totalRevenueElement = document.getElementById('total-revenue');
                
                if (totalRevenueElement) {
                    totalRevenueElement.textContent = data.currentAmount ? 
                        formatCurrency(data.currentAmount) : '-';
                }
            }
        } catch (error) {
            console.error('Erreur:', error);
            const totalRevenueElement = document.getElementById('total-revenue');
            if (totalRevenueElement) {
                totalRevenueElement.textContent = '-';
            }
        }
    };

    // Récupération et affichage du taux de concrétisation
    const loadStoreRate = async () => {
        try {
            const startDate = sessionStorage.getItem('startDate');
            const endDate = sessionStorage.getItem('endDate');

            console.log('Dates récupérées:', { startDate, endDate });

            if (startDate && endDate) {
                const url = `${CONFIG.API_BASE_URL}/quotations/stats?startDate=${startDate}&endDate=${endDate}`;
                console.log('URL de requête:', url);

                const response = await fetch(url);
                console.log('Statut de la réponse:', response.status);

                if (!response.ok) throw new Error('Erreur lors de la récupération du taux de concrétisation');

                const data = await response.json();
                console.log('Données reçues:', data);

                const rateElement = document.getElementById('store-concretization-rate');
                console.log('Élément trouvé:', rateElement);
                
                if (rateElement && data.concretizationRate !== undefined) {
                    const formattedRate = formatPercent(data.concretizationRate);
                    console.log('Taux formaté:', formattedRate);
                    rateElement.textContent = formattedRate;
                } else {
                    console.error("L'élément store-concretization-rate n'a pas été trouvé dans le DOM ou le taux n'est pas défini");
                }
            } else {
                console.error('Dates manquantes dans le sessionStorage');
            }
        } catch (error) {
            console.error('Erreur:', error);
            const rateElement = document.getElementById('store-concretization-rate');
            if (rateElement) {
                rateElement.textContent = '-';
            }
        }
    };

    // Récupération et affichage des paniers moyens
    const loadAverageBaskets = async () => {
        try {
            const startDate = sessionStorage.getItem('startDate');
            const endDate = sessionStorage.getItem('endDate');

            const response = await fetch(
                `${CONFIG.API_BASE_URL}/invoices/total-stats?startDate=${startDate}&endDate=${endDate}`
            );

            if (!response.ok) throw new Error('Erreur lors de la récupération des paniers moyens');

            const data = await response.json();
            
            const avgBasketElement = document.getElementById('store-average-basket');
            const avgP2Element = document.getElementById('store-average-p2');
            
            if (avgBasketElement && data.averageBasket !== undefined) {
                avgBasketElement.textContent = formatCurrency(data.averageBasket);
            }
            
            if (avgP2Element && data.averageP2 !== undefined) {
                avgP2Element.textContent = formatCurrency(data.averageP2);
            }
        } catch (error) {
            console.error('Erreur:', error);
        }
    };

    // Récupération et affichage des statistiques des montures primées
    const loadFrameStats = async () => {
        try {
            const startDate = sessionStorage.getItem('startDate');
            const endDate = sessionStorage.getItem('endDate');

            const response = await fetch(
                `${CONFIG.API_BASE_URL}/invoices/frame-stats?startDate=${startDate}&endDate=${endDate}`
            );

            if (!response.ok) throw new Error('Erreur lors de la récupération des stats de montures');

            const stats = await response.json();
            const totals = calculateFrameTotals(stats);
            
            const nbPremiumFrameElement = document.getElementById('store-nb-premium-frame');
            const ratePremiumFrameElement = document.getElementById('store-rate-premium-frame');
            
            if (nbPremiumFrameElement) {
                nbPremiumFrameElement.textContent = totals.totalPremiumFrames;
            }
            
            if (ratePremiumFrameElement) {
                const percentage = calculatePercentage(totals.totalFrames, totals.totalPremiumFrames);
                ratePremiumFrameElement.textContent = `${percentage}%`;
            }
        } catch (error) {
            console.error('Erreur:', error);
        }
    };

    const calculateFrameTotals = (stats) => {
        return stats.reduce((acc, seller) => {
            acc.totalFrames += seller.totalFrames || 0;
            acc.totalPremiumFrames += seller.premiumFrames || 0;
            return acc;
        }, { totalFrames: 0, totalPremiumFrames: 0 });
    };

    const calculatePercentage = (total, part) => {
        return total > 0 ? (part * 100 / total).toFixed(1) : 0;
    };

    // Récupération et affichage du CA personnel
    const loadPersonalRevenue = async () => {
        try {
            const startDate = sessionStorage.getItem('startDate');
            const endDate = sessionStorage.getItem('endDate');
            const user = JSON.parse(sessionStorage.getItem('user'));

            if (startDate && endDate && user?.seller_ref) {
                const [personalData, storeData] = await Promise.all([
                    utils.fetchApi(`/invoices/seller-stats?startDate=${startDate}&endDate=${endDate}`),
                    utils.fetchApi(`/invoices/period-revenue?startDate=${startDate}&endDate=${endDate}`)
                ]);

                const sellerStats = personalData.find(s => s.sellerRef === user.seller_ref) || {};
                const personalRevenueElement = document.getElementById('personal-revenue');
                const personalRevenuePercentElement = document.getElementById('personal-revenue-percent');

                if (personalRevenueElement) {
                    personalRevenueElement.textContent = formatCurrency(sellerStats.amount || 0);
                }

                if (personalRevenuePercentElement && storeData.currentAmount) {
                    const percentage = ((sellerStats.amount || 0) / storeData.currentAmount * 100).toFixed(1);
                    personalRevenuePercentElement.textContent = `${percentage}%`;
                }
            }
        } catch (error) {
            console.error('Erreur:', error);
        }
    };

    // Récupération et affichage des statistiques des devis personnels
    const loadPersonalQuotationStats = async () => {
        try {
            const startDate = sessionStorage.getItem('startDate');
            const endDate = sessionStorage.getItem('endDate');
            const user = JSON.parse(sessionStorage.getItem('user'));

            if (startDate && endDate && user?.seller_ref) {
                const response = await fetch(
                    `${CONFIG.API_BASE_URL}/quotations/stats?startDate=${startDate}&endDate=${endDate}`
                );

                if (!response.ok) throw new Error('Erreur lors de la récupération des stats de devis');

                const stats = await response.json();
                const userStats = stats.sellerStats.find(s => s.sellerRef === user.seller_ref) || {};

                const rateElement = document.getElementById('personal-concretization-rate');
                const unvalidatedElement = document.getElementById('personal-unvalidated-quotations');

                if (rateElement && userStats.concretizationRate !== undefined) {
                    rateElement.textContent = `${userStats.concretizationRate.toFixed(1)}%`;
                }

                if (unvalidatedElement && userStats.unvalidatedQuotations !== undefined) {
                    unvalidatedElement.textContent = userStats.unvalidatedQuotations;
                }
            }
        } catch (error) {
            console.error('Erreur:', error);
        }
    };

    // Récupération et affichage des paniers moyens personnels
    const loadPersonalAverageBaskets = async () => {
        try {
            const startDate = sessionStorage.getItem('startDate');
            const endDate = sessionStorage.getItem('endDate');
            const user = JSON.parse(sessionStorage.getItem('user'));

            if (startDate && endDate && user?.seller_ref) {
                const stats = await utils.fetchApi(`/invoices/average-baskets?startDate=${startDate}&endDate=${endDate}`);
                const sellerStats = stats.find(s => s.sellerRef === user.seller_ref) || {};

                const personalAvgBasketElement = document.getElementById('personal-average-basket-value');
                const personalAvgP2Element = document.getElementById('personal-average-p2-value');

                if (personalAvgBasketElement) {
                    personalAvgBasketElement.textContent = formatCurrency(sellerStats.averageBasket || 0);
                }

                if (personalAvgP2Element) {
                    personalAvgP2Element.textContent = formatCurrency(sellerStats.averageP2 || 0);
                }
            }
        } catch (error) {
            console.error('Erreur:', error);
        }
    };

    // Récupération et affichage des statistiques personnelles des montures primées
    const loadPersonalFrameStats = async () => {
        try {
            const startDate = sessionStorage.getItem('startDate');
            const endDate = sessionStorage.getItem('endDate');
            const user = JSON.parse(sessionStorage.getItem('user'));

            if (startDate && endDate && user?.seller_ref) {
                const stats = await utils.fetchApi(`/invoices/frame-stats?startDate=${startDate}&endDate=${endDate}`);
                const sellerStats = stats.find(s => s.sellerRef === user.seller_ref) || {};
                
                const personalRatePremiumElement = document.getElementById('personal-rate-premium-frame');
                const personalBonusFrameElement = document.getElementById('personal-bonus-frame');

                if (personalRatePremiumElement) {
                    const percentage = calculatePercentage(sellerStats.totalFrames || 0, sellerStats.premiumFrames || 0);
                    personalRatePremiumElement.textContent = `${percentage}%`;
                }

                if (personalBonusFrameElement) {
                    const bonus = (sellerStats.premiumFrames || 0) * CONFIG.BONUS_PER_FRAME;
                    personalBonusFrameElement.textContent = formatCurrency(bonus);
                }
            }
        } catch (error) {
            console.error('Erreur:', error);
        }
    };

    // Chargement initial
    await loadTotalRevenue();
    await loadStoreRate();
    await loadAverageBaskets();
    await loadFrameStats();
    await loadPersonalRevenue();
    await loadPersonalQuotationStats();
    await loadPersonalAverageBaskets();
    await loadPersonalFrameStats();  // Ajout de l'appel à la nouvelle fonction
});
