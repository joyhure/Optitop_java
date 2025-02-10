async function handleLogin(event) {
    event.preventDefault();
    
    const login = document.getElementById('login').value;
    const password = document.getElementById('password').value;

    console.log('Tentative de connexion...');

    try {
        const response = await fetch('http://localhost:8080/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ login, password })
        });

        console.log('Réponse du serveur:', response.status);

        if (response.ok) {
            const userData = await response.json();
            // Stockage des données utilisateur en session
            sessionStorage.setItem('user', JSON.stringify(userData));
            // Redirection vers le dashboard
            window.location.href = 'dashboard.php';
        } else {
            // Afficher un message d'erreur à l'utilisateur
            alert('Identifiants incorrects');
        }
    } catch (error) {
        console.error('Erreur:', error);
        alert('Erreur de connexion au serveur');
    }
}