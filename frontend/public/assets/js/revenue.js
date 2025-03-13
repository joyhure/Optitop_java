document.addEventListener('DOMContentLoaded', function() {
    // Configuration
    const CONFIG = {
        API_BASE_URL: 'http://localhost:8080/api'
    };

    // Éléments DOM
    const DOM = {
        mainRevenue: document.getElementById('main-revenue'),
        revenueSections: document.getElementById('revenue-sections')
    };

    // Utilitaires
    const utils = {
        // Communication API
        async fetchApi(endpoint) {
            const response = await fetch(`${CONFIG.API_BASE_URL}${endpoint}`);
            if (!response.ok) throw new Error(`Erreur API: ${response.status}`);
            return response.json();
        },

        async getMonthlyRevenue(year) {
            return await this.fetchApi(`/invoices/monthly-revenue/${year}`);
        },

        async getPeriodRevenue(startDate, endDate) {
            return await this.fetchApi(`/invoices/period-revenue?startDate=${startDate}&endDate=${endDate}`);
        },

        async getSellerStats(startDate, endDate) {
            return await this.fetchApi(`/invoices/seller-stats?startDate=${startDate}&endDate=${endDate}`);
        },

        // Formatage des données
        formatCurrency(amount) {
            return new Intl.NumberFormat('fr-FR', {
                style: 'currency',
                currency: 'EUR',
                minimumFractionDigits: 0,
                maximumFractionDigits: 0
            }).format(amount);
        },

        formatDelta(delta, isFutureMonth = false) {
            if (isFutureMonth) return 'À venir';
            if (delta === undefined || delta === null) return 'Indéfini';
            return new Intl.NumberFormat('fr-FR', {
                style: 'currency',
                currency: 'EUR',
                minimumFractionDigits: 0,
                maximumFractionDigits: 0,
                signDisplay: 'always'
            }).format(delta);
        },

        formatDeltaPercent(currentValue, previousValue, isFutureMonth = false) {
            if (isFutureMonth) return 'À venir';
            if (currentValue === undefined || previousValue === undefined || previousValue === 0) return 'Indéfini';
            const percentChange = ((currentValue - previousValue) / Math.abs(previousValue)) * 100;
            return new Intl.NumberFormat('fr-FR', {
                style: 'percent',
                minimumFractionDigits: 1,
                maximumFractionDigits: 1,
                signDisplay: 'always'
            }).format(percentChange / 100);
        },

        formatPercentage(value) {
            return new Intl.NumberFormat('fr-FR', {
                style: 'percent',
                minimumFractionDigits: 1,
                maximumFractionDigits: 1
            }).format(value / 100);
        },

        getInitials(sellerRef) {
            return sellerRef ? sellerRef.substring(0, 2).toUpperCase() : 'XX';
        },

        // Mises à jour UI
        updateSummaryCard(data) {
            const totalRevenueElement = document.getElementById('total-revenue');
            const totalDeltaPercentElement = document.getElementById('total-delta-percent');

            const currentAmount = data.currentAmount || 0;
            const previousAmount = data.previousAmount || 0;

            totalRevenueElement.textContent = this.formatCurrency(currentAmount);
            totalDeltaPercentElement.textContent = this.formatDeltaPercent(currentAmount, previousAmount);
        },

        updateSellersTable(data) {
            const tbody = document.getElementById('sellers-revenue-body');
            if (!tbody || !Array.isArray(data)) return;
            
            tbody.innerHTML = '';
            data.forEach(seller => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td class="text-center align-middle">${this.getInitials(seller.sellerRef)}</td>
                    <td class="text-center align-middle">${this.formatCurrency(seller.amount)}</td>
                    <td class="text-center align-middle">${this.formatPercentage(seller.percentage)}</td>
                `;
                tbody.appendChild(row);
            });
        },

        // Génération HTML
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

    // Gestionnaire de données
    const dataManager = {
        revenueCache: new Map(),

        async initialize() {
            try {
                const startDate = sessionStorage.getItem('startDate');
                const endDate = sessionStorage.getItem('endDate');

                const [periodData, sellerData, years] = await Promise.all([
                    utils.getPeriodRevenue(startDate, endDate),
                    utils.getSellerStats(startDate, endDate),
                    utils.fetchApi('/invoices/years')
                ]);

                utils.updateSummaryCard(periodData);
                utils.updateSellersTable(sellerData);
                this.displayYearSections(years);
                await this.loadAllRevenueData(years);
            } catch (error) {
                console.error('Erreur lors du chargement des données:', error);
            }
        },

        displayYearSections(years) {
            if (!DOM.revenueSections || !years.length) return;
            const sections = years.map((year, index) => 
                utils.generateYearSection(year, index === 0)
            ).join('');
            DOM.revenueSections.innerHTML = sections;
        },

        async loadAllRevenueData(years) {
            // Chargement des données
            for (const year of years) {
                const monthlyRevenue = await utils.getMonthlyRevenue(year);
                this.revenueCache.set(year, monthlyRevenue);
            }
            
            // Mise à jour des tableaux
            years.forEach(year => this.updateTableRows(year));
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
                }
                
                cells[month].textContent = utils.formatCurrency(revenue, isFutureMonth);
            }
            cells[13].textContent = utils.formatCurrency(yearTotal);
        },

        updateDeltaRow(year, row) {
            if (!row) return;
            
            const currentDate = new Date();
            const currentYear = currentDate.getFullYear();
            const currentMonth = currentDate.getMonth() + 1;
            
            const cells = row.querySelectorAll('td');
            const currentYearData = this.revenueCache.get(year) || {};
            const previousYearData = this.revenueCache.get(year - 1) || {};
            
            let yearDeltaTotal = 0;
            let hasValidPreviousData = false;
            
            for (let month = 1; month <= 12; month++) {
                const currentRevenue = currentYearData[month] || 0;
                const previousRevenue = previousYearData[month] || 0;
                const isFutureMonth = year === currentYear && month > currentMonth;
                
                let delta;
                if (isFutureMonth) {
                    cells[month].textContent = utils.formatDelta(null, true); // Affiche "À venir"
                } else if (!previousYearData[month]) {
                    cells[month].textContent = utils.formatDelta(undefined); // Affiche "Indéfini"
                } else {
                    hasValidPreviousData = true;
                    delta = currentRevenue - previousRevenue;
                    if (!isNaN(delta)) {
                        yearDeltaTotal += delta;
                    }
                    cells[month].textContent = utils.formatDelta(delta);
                }
            }

            // Affichage du total annuel
            cells[13].textContent = hasValidPreviousData ? 
                utils.formatDelta(yearDeltaTotal) : 
                utils.formatDelta(undefined); // Affiche "Indéfini" si pas de données n-1
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
                    cells[month].textContent = 'Indéfini';
                } else {
                    hasValidPreviousData = true;
                    cells[month].textContent = utils.formatDeltaPercent(currentRevenue, previousRevenue);
                }
            }

            // Affichage du total annuel en pourcentage
            cells[13].textContent = hasValidPreviousData ? 
                utils.formatDeltaPercent(yearTotalCurrent, yearTotalPrevious) : 
                'Indéfini';
        }
    };

    // Initialisation
    dataManager.initialize();
});