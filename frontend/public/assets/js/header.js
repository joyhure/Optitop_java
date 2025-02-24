document.addEventListener('DOMContentLoaded', function() {
    // User session
    const userSession = JSON.parse(sessionStorage.getItem('user'));
    if (userSession && userSession.firstname) {
        // Gestion du prénom
        document.getElementById('userFirstname').textContent = userSession.firstname;
        
        // Gestion de la couleur du header
        const header = document.querySelector('.text-bg-light');
        header.classList.remove('text-bg-light');
        header.classList.add(`bg-role-${userSession.role.toLowerCase()}`);
    } else {
        window.location.href = 'index.html';
    }

    // Period selection
    const periodItems = document.querySelectorAll('[data-period]');
    const selectedPeriod = document.getElementById('selectedPeriod');

    periodItems.forEach(item => {
        item.addEventListener('click', function(e) {
            e.preventDefault();
            const period = this.getAttribute('data-period');
            selectedPeriod.textContent = period;
            sessionStorage.setItem('selectedPeriod', period);
        });
    });

    const savedPeriod = sessionStorage.getItem('selectedPeriod') || 'Mois';
    selectedPeriod.textContent = savedPeriod;

    // Custom date range functionality
    const customDateBtn = document.getElementById('customDateBtn');
    const dateRangeContainer = document.getElementById('dateRangeContainer');
    const startDateInput = document.getElementById('startDate');
    const endDateInput = document.getElementById('endDate');
    const applyDateRange = document.getElementById('applyDateRange');

    // Format date as dd/mm/yyyy
    function formatDate(date) {
        const d = new Date(date);
        const day = String(d.getDate()).padStart(2, '0');
        const month = String(d.getMonth() + 1).padStart(2, '0');
        const year = d.getFullYear();
        return `${day}/${month}/${year}`;
    }

    customDateBtn.addEventListener('click', function(e) {
        e.preventDefault();
        e.stopPropagation();
        dateRangeContainer.classList.toggle('d-none');
        dateRangeContainer.classList.toggle('show');
    });

    applyDateRange.addEventListener('click', function(e) {
        e.preventDefault();
        const startDate = startDateInput.value;
        const endDate = endDateInput.value;
        
        if (startDate && endDate) {
            const formattedRange = `${formatDate(startDate)} au ${formatDate(endDate)}`;
            selectedPeriod.textContent = formattedRange;
            sessionStorage.setItem('selectedPeriod', formattedRange);
            sessionStorage.setItem('startDate', startDate);
            sessionStorage.setItem('endDate', endDate);
            dateRangeContainer.classList.add('d-none');
            dateRangeContainer.classList.remove('show');
        }
    });

    // Close date range container when clicking outside
    document.addEventListener('click', function(e) {
        if (!dateRangeContainer.contains(e.target) && 
            !customDateBtn.contains(e.target) && 
            !dateRangeContainer.classList.contains('d-none')) {
            dateRangeContainer.classList.add('d-none');
            dateRangeContainer.classList.remove('show');
        }
    });

    // Restore saved dates if they exist
    const savedStartDate = sessionStorage.getItem('startDate');
    const savedEndDate = sessionStorage.getItem('endDate');
    if (savedStartDate && savedEndDate) {
        startDateInput.value = savedStartDate;
        endDateInput.value = savedEndDate;
    }

    // Sign out functionality
    document.getElementById('signOutBtn').addEventListener('click', async function(e) {
        e.preventDefault();
        
        try {
            // Appel à l'API Spring Boot
            const apiResponse = await fetch('http://localhost:8080/api/auth/logout', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            // Appel au PHP pour détruire la session
            const phpResponse = await fetch('logout.php');

            // Nettoyer le sessionStorage
            sessionStorage.clear();

            // Rediriger vers la page de connexion
            window.location.href = 'index.html';
        } catch (error) {
            console.error('Erreur de déconnexion:', error);
            // En cas d'erreur, forcer la déconnexion
            sessionStorage.clear();
            window.location.href = 'index.html';
        }
    });
});