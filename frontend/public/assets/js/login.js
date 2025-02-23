document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('loginForm').addEventListener('submit', handleLogin);
});

async function handleLogin(event) {
    event.preventDefault();
    
    try {
        // Appel à l'API Spring Boot
        const response = await fetch('http://localhost:8080/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                login: document.getElementById('login').value,
                password: document.getElementById('password').value
            })
        });

        if (response.ok) {
            const userData = await response.json();
            
            // Stocker dans sessionStorage
            sessionStorage.setItem('user', JSON.stringify({
                id: userData.id,
                firstname: userData.firstname,
                role: userData.role
            }));

            // Synchroniser avec la session PHP
            await fetch('login.php', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(userData)
            });

            window.location.href = 'dashboard.php';
        } else {
            alert('Identifiants incorrects');
        }
    } catch (error) {
        console.error('Erreur:', error);
        alert('Erreur de connexion au serveur');
    }
}