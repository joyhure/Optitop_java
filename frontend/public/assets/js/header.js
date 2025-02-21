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