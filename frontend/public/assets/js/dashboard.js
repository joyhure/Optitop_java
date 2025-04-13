document.addEventListener('DOMContentLoaded', async () => {
    // Configuration
    const CONFIG = {
        API_BASE_URL: 'http://localhost:8080/api',
        LOCALE: 'fr-FR',
        CURRENCY: 'EUR'
    };

    // Formatage monétaire
    const formatCurrency = (amount) => {
        return new Intl.NumberFormat(CONFIG.LOCALE, {
            style: 'currency',
            currency: CONFIG.CURRENCY,
            minimumFractionDigits: 0,
            maximumFractionDigits: 0
        }).format(amount);
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

    // Chargement initial
    await loadTotalRevenue();
});
