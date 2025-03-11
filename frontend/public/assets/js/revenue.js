document.addEventListener('DOMContentLoaded', function() {
    const CONFIG = {
        API_BASE_URL: 'http://localhost:8080/api'
    };

    const DOM = {
        mainRevenue: document.getElementById('main-revenue'),
        revenueSections: document.getElementById('revenue-sections')
    };

    const utils = {
        async fetchApi(endpoint) {
            const response = await fetch(`${CONFIG.API_BASE_URL}${endpoint}`);
            if (!response.ok) throw new Error(`Erreur API: ${response.status}`);
            return response.json();
        },

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
                                    <td class="text-center align-middle">-</td>
                                    <td class="text-center align-middle">-</td>
                                    <td class="text-center align-middle">-</td>
                                    <td class="text-center align-middle">-</td>
                                    <td class="text-center align-middle">-</td>
                                    <td class="text-center align-middle">-</td>
                                    <td class="text-center align-middle">-</td>
                                    <td class="text-center align-middle">-</td>
                                    <td class="text-center align-middle">-</td>
                                    <td class="text-center align-middle">-</td>
                                    <td class="text-center align-middle">-</td>
                                    <td class="text-center align-middle">-</td>
                                    <td class="text-center align-middle">-</td>
                                </tr>
                                <tr>
                                    <td class="text-center align-middle fw-bold">Delta n-1</td>
                                    <td class="text-center align-middle">-</td>
                                    <td class="text-center align-middle">-</td>
                                    <td class="text-center align-middle">-</td>
                                    <td class="text-center align-middle">-</td>
                                    <td class="text-center align-middle">-</td>
                                    <td class="text-center align-middle">-</td>
                                    <td class="text-center align-middle">-</td>
                                    <td class="text-center align-middle">-</td>
                                    <td class="text-center align-middle">-</td>
                                    <td class="text-center align-middle">-</td>
                                    <td class="text-center align-middle">-</td>
                                    <td class="text-center align-middle">-</td>
                                    <td class="text-center align-middle">-</td>
                                </tr>
                                <tr>
                                    <td class="text-center align-middle fw-bold">Delta %</td>
                                    <td class="text-center align-middle">-</td>
                                    <td class="text-center align-middle">-</td>
                                    <td class="text-center align-middle">-</td>
                                    <td class="text-center align-middle">-</td>
                                    <td class="text-center align-middle">-</td>
                                    <td class="text-center align-middle">-</td>
                                    <td class="text-center align-middle">-</td>
                                    <td class="text-center align-middle">-</td>
                                    <td class="text-center align-middle">-</td>
                                    <td class="text-center align-middle">-</td>
                                    <td class="text-center align-middle">-</td>
                                    <td class="text-center align-middle">-</td>
                                    <td class="text-center align-middle">-</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </section>
            `;
        },

        formatCurrency(amount) {
            return new Intl.NumberFormat('fr-FR', {
                style: 'currency',
                currency: 'EUR',
                minimumFractionDigits: 0,
                maximumFractionDigits: 0
            }).format(amount);
        },

        async getMonthlyRevenue(year) {
            return await this.fetchApi(`/invoices/monthly-revenue/${year}`);
        }
    };

    const dataManager = {
        async initialize() {
            try {
                const years = await utils.fetchApi('/invoices/years');
                await this.displayYearSections(years);
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
            for (const year of years) {
                const monthlyRevenue = await utils.getMonthlyRevenue(year);
                this.updateRevenueRow(year, monthlyRevenue);
            }
        },

        updateRevenueRow(year, monthlyRevenue) {
            const tableBody = document.querySelector(`#collapseRevenue${year} tbody`);
            if (!tableBody) return;

            const revenueRow = tableBody.querySelector('tr:first-child');
            if (!revenueRow) return;

            const cells = revenueRow.querySelectorAll('td');
            let yearTotal = 0;

            // Mise à jour des cellules mensuelles (index 1-12 pour les mois)
            for (let month = 1; month <= 12; month++) {
                const revenue = monthlyRevenue[month] || 0;
                yearTotal += revenue;
                cells[month].textContent = utils.formatCurrency(revenue);
            }

            // Mise à jour du total annuel (dernière cellule)
            cells[13].textContent = utils.formatCurrency(yearTotal);
        }
    };

    // Initialisation
    dataManager.initialize();
});