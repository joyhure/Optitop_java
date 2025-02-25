document.addEventListener('DOMContentLoaded', function() {
    // User session
    const userSession = JSON.parse(sessionStorage.getItem('user'));
    if (userSession && userSession.firstname) {
        document.getElementById('userFirstname').textContent = userSession.firstname;
        const header = document.querySelector('.text-bg-light');
        header.classList.remove('text-bg-light');
        header.classList.add(`bg-role-${userSession.role.toLowerCase()}`);
    } else {
        window.location.href = 'index.html';
    }

    // Date range functionality
    const selectedPeriod = document.getElementById('selectedPeriod');
    const dateRangeContainer = document.getElementById('dateRangeContainer');
    const startDateInput = document.getElementById('startDate');
    const endDateInput = document.getElementById('endDate');
    const applyDateRange = document.getElementById('applyDateRange');

    function formatDate(date) {
        const d = new Date(date);
        const day = String(d.getDate()).padStart(2, '0');
        const month = String(d.getMonth() + 1).padStart(2, '0');
        const year = d.getFullYear();
        return `${day}/${month}/${year}`;
    }

    applyDateRange.addEventListener('click', function(e) {
        e.preventDefault();
        const startDate = startDateInput.value;
        const endDate = endDateInput.value;
        
        if (startDate && endDate) {
            const formattedRange = `${formatDate(startDate)} au ${formatDate(endDate)}`;
            selectedPeriod.textContent = formattedRange;
            updateDates(startDate, endDate);
            const dropdownMenu = dateRangeContainer.closest('.dropdown-menu');
            const dropdownToggle = document.querySelector('[data-bs-toggle="dropdown"]');
            bootstrap.Dropdown.getInstance(dropdownToggle).hide();
        }
    });

    function updateDates(startDate, endDate) {
        sessionStorage.setItem('startDate', startDate);
        sessionStorage.setItem('endDate', endDate);
        
        const event = new CustomEvent('datesUpdated', {
            detail: { startDate, endDate },
            bubbles: true
        });
        document.dispatchEvent(event);
        window.location.reload();
    }

    // Restore saved dates
    const savedStartDate = sessionStorage.getItem('startDate');
    const savedEndDate = sessionStorage.getItem('endDate');
    if (savedStartDate && savedEndDate) {
        startDateInput.value = savedStartDate;
        endDateInput.value = savedEndDate;
        selectedPeriod.textContent = `${formatDate(savedStartDate)} au ${formatDate(savedEndDate)}`;
    }

    // Sign out functionality
    document.getElementById('signOutBtn').addEventListener('click', async function(e) {
        e.preventDefault();
        try {
            await fetch('http://localhost:8080/api/auth/logout', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' }
            });
            await fetch('logout.php');
            sessionStorage.clear();
            window.location.href = 'index.html';
        } catch (error) {
            console.error('Erreur de déconnexion:', error);
            sessionStorage.clear();
            window.location.href = 'index.html';
        }
    });
});