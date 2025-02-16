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
});