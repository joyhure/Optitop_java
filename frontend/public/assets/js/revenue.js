document.addEventListener('DOMContentLoaded', function() {
    // Configuration
    const CONFIG = {
        API_BASE_URL: 'http://localhost:8080/api',
        LOCALE: 'fr-FR',
        CURRENCY: 'EUR'
    };

    // Éléments DOM
    const DOM = {
        mainRevenue: document.getElementById('main-revenue'),
        revenueSections: document.getElementById('revenue-sections'),
        totalRevenue: document.getElementById('total-revenue'),
        totalDeltaPercent: document.getElementById('total-delta-percent'),
        sellersRevenueBody: document.getElementById('sellers-revenue-body')
    };

    // Utilitaires de formatage
    const formatUtils = {
        currency(amount) {
            return new Intl.NumberFormat(CONFIG.LOCALE, {
                style: 'currency',
                currency: CONFIG.CURRENCY,
                minimumFractionDigits: 0,
                maximumFractionDigits: 0
            }).format(amount);
        },

        delta(delta, isFutureMonth = false) {
            if (isFutureMonth) return 'À venir';
            if (delta === undefined || delta === null) return 'Indéfini';
            return new Intl.NumberFormat(CONFIG.LOCALE, {
                style: 'currency',
                currency: CONFIG.CURRENCY,
                minimumFractionDigits: 0,
                maximumFractionDigits: 0,
                signDisplay: 'always'
            }).format(delta);
        },

        deltaPercent(currentValue, previousValue, isFutureMonth = false) {
            if (isFutureMonth) return 'À venir';
            if (!currentValue || !previousValue || previousValue === 0) return 'Indéfini';
            const percentChange = ((currentValue - previousValue) / Math.abs(previousValue)) * 100;
            return new Intl.NumberFormat(CONFIG.LOCALE, {
                style: 'percent',
                minimumFractionDigits: 1,
                maximumFractionDigits: 1,
                signDisplay: 'always'
            }).format(percentChange / 100);
        },

        percentage(value) {
            return new Intl.NumberFormat(CONFIG.LOCALE, {
                style: 'percent',
                minimumFractionDigits: 1,
                maximumFractionDigits: 1
            }).format(value / 100);
        },

        initiales(sellerRef) {
            return sellerRef ? sellerRef.substring(0, 2).toUpperCase() : 'XX';
        }
    };

    // Service API
    const apiService = {
        async fetchApi(endpoint) {
            try {
                const response = await fetch(`${CONFIG.API_BASE_URL}${endpoint}`);
                if (!response.ok) throw new Error(`Erreur ${response.status}`);
                return await response.json();
            } catch (error) {
                console.error(`Erreur API ${endpoint}:`, error);
                throw error;
            }
        },

        getMonthlyRevenue(year) {
            return this.fetchApi(`/invoices/monthly-revenue/${year}`);
        },

        getPeriodRevenue(startDate, endDate) {
            return this.fetchApi(`/invoices/period-revenue?startDate=${startDate}&endDate=${endDate}`);
        },

        getSellerStats(startDate, endDate) {
            return this.fetchApi(`/invoices/seller-stats?startDate=${startDate}&endDate=${endDate}`);
        }
    };

    // Gestionnaire de données
    const dataManager = {
        revenueCache: new Map(),

        async initialize() {
            try {
                // Récupération des années
                const years = await apiService.fetchApi('/invoices/years');
                if (!years?.length) throw new Error('Aucune année disponible');

                // Affichage des sections
                this.displayYearSections(years);

                // Récupération des dates depuis sessionStorage
                const startDate = sessionStorage.getItem('startDate');
                const endDate = sessionStorage.getItem('endDate');

                // Chargement des données mensuelles (toujours nécessaire)
                const monthlyData = await apiService.getMonthlyRevenue(new Date().getFullYear());
                if (monthlyData) {
                    this.revenueCache.set(new Date().getFullYear(), monthlyData);
                }

                // Chargement des données de période si dates disponibles
                if (startDate && endDate) {
                    const [periodData, sellerStats] = await Promise.all([
                        apiService.getPeriodRevenue(startDate, endDate),
                        apiService.getSellerStats(startDate, endDate)
                    ]);

                    this.updateSummaryCard(periodData);
                    this.updateSellersTable(sellerStats);
                } else {
                    this.updateSummaryCard({ currentAmount: null, previousAmount: null });
                    this.updateSellersTable([]);
                }

                // Chargement des données annuelles
                await this.loadAllRevenueData(years);

            } catch (error) {
                console.error('Erreur de chargement:', error);
                this.displayErrorMessage('Les données sont temporairement indisponibles');
            }
        },

        displayErrorMessage(message) {
            const errorDiv = document.createElement('div');
            errorDiv.className = 'alert alert-danger';
            errorDiv.textContent = message;
            DOM.mainRevenue.prepend(errorDiv);
        },

        displayYearSections(years) {
            if (!DOM.revenueSections || !years.length) return;
            const sections = years.map((year, index) => 
                utils.generateYearSection(year, index === 0)
            ).join('');
            DOM.revenueSections.innerHTML = sections;
        },

        async loadAllRevenueData(years) {
            try {
                for (const year of years) {
                    try {
                        const [currentYear, previousYear] = await Promise.all([
                            apiService.getMonthlyRevenue(year),
                            apiService.getMonthlyRevenue(year - 1)
                        ]);

                        if (currentYear) this.revenueCache.set(year, currentYear);
                        if (previousYear) this.revenueCache.set(year - 1, previousYear);
                        
                        this.updateTableRows(year);
                    } catch (error) {
                        console.error(`Erreur pour l'année ${year}:`, error);
                    }
                }
            } catch (error) {
                console.error('Erreur lors du chargement des données:', error);
            }
        },

        updateTableRows(year) {
            const tableBody = document.querySelector(`#collapseRevenue${year} tbody`);
            if (!tableBody) return;

            const rows = tableBody.querySelectorAll('tr');
            const [revenueRow, deltaRow, deltaPercentRow] = rows;

            this.updateRevenueRow(year, revenueRow);
            this.updateDeltaRow(year, deltaRow);
            this.updateDeltaPercentRow(year, deltaPercentRow);
        },

        updateRevenueRow(year, row) {
            if (!row) return;
            const cells = row.querySelectorAll('td');
            const yearData = this.revenueCache.get(year) || {};           
            const currentDate = new Date();
            const currentYear = currentDate.getFullYear();
            const currentMonth = currentDate.getMonth() + 1;
            
            let yearTotal = 0;
            for (let month = 1; month <= 12; month++) {
                const revenue = yearData[month] || 0;
                const isFutureMonth = year === currentYear && month > currentMonth;
                
                if (!isFutureMonth) {
                    yearTotal += revenue;
                    cells[month].textContent = formatUtils.currency(revenue);
                } else {
                    cells[month].textContent = 'À venir';
                }
            }
            cells[13].textContent = formatUtils.currency(yearTotal);
        },

        updateDeltaRow(year, row) {
            if (!row) return;
            
            const currentDate = new Date();
            const currentYear = currentDate.getFullYear();
            const currentMonth = currentDate.getMonth() + 1;
            
            const cells = row.querySelectorAll('td');
            const yearData = this.revenueCache.get(year) || {};
            const previousYearData = this.revenueCache.get(year - 1) || {};
            
            let yearDeltaTotal = 0;
            let hasValidPreviousData = false;
            
            for (let month = 1; month <= 12; month++) {
                const { current, previous } = this.hasValidDataForYear(year, month);
                const currentRevenue = current ? yearData[month] : 0;
                const previousRevenue = previous ? previousYearData[month] : 0;
                const isFutureMonth = year === currentYear && month > currentMonth;
                
                let delta = currentRevenue - previousRevenue;
                
                if (isFutureMonth) {
                    cells[month].textContent = 'À venir';
                } else if (!current || !previous) {
                    cells[month].textContent = 'Inconnu';
                } else {
                    hasValidPreviousData = true;
                    cells[month].textContent = formatUtils.delta(delta);
                    yearDeltaTotal += delta;
                }
            }

            cells[13].textContent = hasValidPreviousData ? 
                formatUtils.delta(yearDeltaTotal) : 'Inconnu';
        },

        updateDeltaPercentRow(year, row) {
            if (!row) return;
            
            const currentDate = new Date();
            const currentYear = currentDate.getFullYear();
            const currentMonth = currentDate.getMonth() + 1;
            
            const cells = row.querySelectorAll('td');
            const currentYearData = this.revenueCache.get(year) || {};
            const previousYearData = this.revenueCache.get(year - 1) || {};
            
            let yearTotalCurrent = 0;
            let yearTotalPrevious = 0;
            let hasValidPreviousData = false;
            
            for (let month = 1; month <= 12; month++) {
                const currentRevenue = currentYearData[month] || 0;
                const previousRevenue = previousYearData[month] || 0;
                const isFutureMonth = year === currentYear && month > currentMonth;
                
                if (!isFutureMonth) {
                    yearTotalCurrent += currentRevenue;
                    yearTotalPrevious += previousRevenue;
                }

                if (isFutureMonth) {
                    cells[month].textContent = 'À venir';
                } else if (!previousYearData[month]) {
                    cells[month].textContent = 'Inconnu';
                } else {
                    hasValidPreviousData = true;
                    cells[month].textContent = formatUtils.deltaPercent(currentRevenue, previousRevenue);
                }
            }

            // Affichage du total annuel en pourcentage
            cells[13].textContent = hasValidPreviousData ? 
                formatUtils.deltaPercent(yearTotalCurrent, yearTotalPrevious) : 
                'Inconnu';
        },

        // Ajouter dans dataManager
        hasValidDataForYear(year, month) {
            const currentYearData = this.revenueCache.get(year);
            const previousYearData = this.revenueCache.get(year - 1);
            
            return {
                current: currentYearData && currentYearData[month] !== undefined,
                previous: previousYearData && previousYearData[month] !== undefined
            };
        },

        updateSummaryCard(data) {
            
            const totalRevenueElement = DOM.totalRevenue;
            const totalDeltaPercentElement = DOM.totalDeltaPercent;
        
            if (!data || data.currentAmount === null || data.previousAmount === null) {
                totalRevenueElement.textContent = '-';
                totalDeltaPercentElement.textContent = '-';
                return;
            }
        
            const currentAmount = data.currentAmount || 0;
            const previousAmount = data.previousAmount || 0;
        
            totalRevenueElement.textContent = formatUtils.currency(currentAmount);
            totalDeltaPercentElement.textContent = formatUtils.deltaPercent(currentAmount, previousAmount);
        },

        updateSellersTable(data) {
            const tbody = DOM.sellersRevenueBody;
            if (!tbody || !Array.isArray(data)) return;
            
            // Tri des données par initiales vendeurs
            const sortedData = [...data].sort((a, b) => 
                formatUtils.initiales(a.sellerRef).localeCompare(formatUtils.initiales(b.sellerRef))
            );
            
            // Génération du HTML
            tbody.innerHTML = sortedData
                .map(seller => `
                    <tr>
                        <td class="text-center align-middle">${formatUtils.initiales(seller.sellerRef)}</td>
                        <td class="text-center align-middle">${formatUtils.currency(seller.amount)}</td>
                        <td class="text-center align-middle">${formatUtils.percentage(seller.percentage)}</td>
                    </tr>
                `)
                .join('');
        }
    };

    // Génération HTML
    const utils = {
        generateYearSection(year, isFirst = false) {
            return `
                <section id="table-revenue-${year}" class="table-responsive small w-100">
                    <h4 class="py-3">
                        <a class="text-decoration-none dropdown-toggle text-dark nav-link" 
                           data-bs-toggle="collapse" 
                           href="#collapseRevenue${year}" 
                           role="button" 
                           aria-expanded="${isFirst ? 'true' : 'false'}" 
                           aria-controls="collapseRevenue${year}">
                            ${year}
                        </a>
                    </h4>
                    <div class="collapse ${isFirst ? 'show' : ''}" id="collapseRevenue${year}">
                        <table class="table table-striped table-sm">
                            <thead>
                                <tr>
                                    <th scope="col" class="table-col-w4 text-center align-middle">${year}</th>
                                    <th scope="col" class="table-col-w8 text-center align-middle">Janv.</th>
                                    <th scope="col" class="table-col-w8 text-center align-middle">Fev.</th>
                                    <th scope="col" class="table-col-w8 text-center align-middle">Mars</th>
                                    <th scope="col" class="table-col-w8 text-center align-middle">Avril</th>
                                    <th scope="col" class="table-col-w8 text-center align-middle">Mai</th>
                                    <th scope="col" class="table-col-w8 text-center align-middle">Juin</th>
                                    <th scope="col" class="table-col-w8 text-center align-middle">Juill.</th>
                                    <th scope="col" class="table-col-w8 text-center align-middle">Août</th>
                                    <th scope="col" class="table-col-w8 text-center align-middle">Sept.</th>
                                    <th scope="col" class="table-col-w8 text-center align-middle">Oct.</th>
                                    <th scope="col" class="table-col-w8 text-center align-middle">Nov.</th>
                                    <th scope="col" class="table-col-w8 text-center align-middle">Déc.</th>
                                    <th scope="col" class="table-col-w8 text-center align-middle">Année</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td class="text-center align-middle fw-bold">CA</td>
                                    ${Array(13).fill('<td class="text-center align-middle">-</td>').join('')}
                                </tr>
                                <tr>
                                    <td class="text-center align-middle fw-bold">Delta n-1</td>
                                    ${Array(13).fill('<td class="text-center align-middle">-</td>').join('')}
                                </tr>
                                <tr>
                                    <td class="text-center align-middle fw-bold">Delta %</td>
                                    ${Array(13).fill('<td class="text-center align-middle">-</td>').join('')}
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </section>
            `;
        }
    };

    // Initialisation
    dataManager.initialize();
});