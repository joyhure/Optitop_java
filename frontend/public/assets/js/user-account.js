document.addEventListener('DOMContentLoaded', function() {
    // Récupérer et définir le prénom comme variable globale
    window.userFirstname = JSON.parse(sessionStorage.getItem('user'))?.firstname || '';

    // Récupérer les données utilisateur du sessionStorage
    const userSession = JSON.parse(sessionStorage.getItem('user'));
    console.log('Session utilisateur :', userSession);
    
    // Fonction pour traduire le rôle
    const translateRole = (role) => {
        const roleTranslations = {
            'admin': 'Administrateur',
            'collaborator': 'Collaborateur',
            // Pour les autres rôles, on retourne le rôle tel quel
        };
        return roleTranslations[role.toLowerCase()] || role;
    };

    // Mettre à jour tous les éléments avec les données utilisateur
    const updateUserInterface = () => {
        const firstnameElement = document.getElementById('userFirstname');
        
        if (firstnameElement && userSession && userSession.firstname) {
            // Mise à jour du prénom
            firstnameElement.innerHTML = userSession.firstname;
            console.log('Prénom mis à jour :', userSession.firstname);
            
            // Mettre à jour les autres champs du profil
            document.querySelector('[id="fullName"]').value = `${userSession.firstname} ${userSession.lastname || ''}`;
            document.querySelector('[id="Email"]').value = userSession.email || '';
        } else {
            console.error('Éléments manquants:', {
                firstnameElement: !!firstnameElement,
                userSession: !!userSession,
                firstname: userSession?.firstname
            });
        }
    };

    // Appeler la fonction de mise à jour
    updateUserInterface();

    // Mettre à jour le profil utilisateur
    if (userSession) {
        // Mise à jour du prénom et nom
        const profileFirstname = document.getElementById('profileFirstname');
        if (profileFirstname && userSession.firstname) {
            const fullName = userSession.lastname 
                ? `${userSession.firstname} ${userSession.lastname}`
                : userSession.firstname;
            profileFirstname.textContent = fullName;
            console.log('Nom complet mis à jour :', fullName);
        }

        // Mise à jour du rôle
        const profileRole = document.getElementById('profileRole');
        if (profileRole && userSession.role) {
            const translatedRole = translateRole(userSession.role);
            profileRole.textContent = translatedRole;
            console.log('Rôle mis à jour :', translatedRole);
        }
    }
});