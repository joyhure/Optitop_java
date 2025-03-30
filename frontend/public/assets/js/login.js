document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('loginForm').addEventListener('submit', handleLogin);
});

async function handleLogin(event) {
    event.preventDefault();
    
    const loginForm = document.getElementById('loginForm');
    
    try {
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
            
            // Stocker dans sessionStorage avec seller_ref
            sessionStorage.setItem('user', JSON.stringify({
                id: userData.id,
                firstname: userData.firstname,
                role: userData.role,
                seller_ref: userData.seller_ref
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
            const errorData = await response.json();
            alert(errorData.error || 'Erreur lors de la connexion');
            
            // Réinitialiser le champ mot de passe
            document.getElementById('password').value = '';
        }
    } catch (error) {
        console.error('Erreur:', error);
        alert('Erreur de connexion au serveur');
    }
}