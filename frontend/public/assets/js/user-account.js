document.addEventListener('DOMContentLoaded', function() {
    // Récupérer les données utilisateur du sessionStorage
    const userSession = JSON.parse(sessionStorage.getItem('user'));
    console.log('Session utilisateur :', userSession);
    
    // Fonction pour traduire le rôle
    const translateRole = (role) => {
        const roleTranslations = {
            'admin': 'Administrateur',
            'collaborator': 'Collaborateur'
        };
        return roleTranslations[role.toLowerCase()] || 
               role.charAt(0).toUpperCase() + role.slice(1).toLowerCase();
    };

    // Fonction pour récupérer et afficher le nom complet
    const displayFullName = async () => {
        // Pour l'en-tête du profil
        const profileFullname = document.getElementById('profileFullname');
        // Pour les détails du profil
        const profileDetailName = document.getElementById('profileDetailName');
        
        if ((profileFullname || profileDetailName) && userSession?.id) {
            try {
                const response = await fetch(`http://localhost:8080/api/users/${userSession.id}/lastname`);
                if (response.ok) {
                    const lastname = await response.text();
                    
                    // Nom complet pour le profil
                    if (profileFullname) {
                        profileFullname.textContent = `${userSession.firstname} ${lastname}`;
                    }
                    // Uniquement le nom de famille pour les détails
                    if (profileDetailName) {
                        profileDetailName.textContent = lastname;
                    }
                    console.log('Noms mis à jour');
                }
            } catch (error) {
                console.error('Erreur lors de la récupération du nom :', error);
                if (profileFullname) profileFullname.textContent = userSession.firstname;
                if (profileDetailName) profileDetailName.textContent = 'Non disponible';
            }
        }
    };

    // Fonction pour récupérer et afficher la date de création
    const displayCreatedAt = async () => {
        const profileDateCreate = document.getElementById('profileDateCreate');
        
        if (profileDateCreate && userSession?.id) {
            try {
                const response = await fetch(`http://localhost:8080/api/users/${userSession.id}/created-at`);
                if (response.ok) {
                    const createdAt = await response.text();
                    // Formatage de la date en français
                    const date = new Date(createdAt);
                    const formattedDate = new Intl.DateTimeFormat('fr-FR', {
                        day: '2-digit',
                        month: '2-digit',
                        year: 'numeric'
                    }).format(date);
                    
                    profileDateCreate.textContent = `Depuis le ${formattedDate}`;
                    console.log('Date de création mise à jour');
                }
            } catch (error) {
                console.error('Erreur lors de la récupération de la date de création :', error);
                profileDateCreate.textContent = 'Date non disponible';
            }
        }
    };

    // Fonction pour récupérer et afficher les détails utilisateur
    const displayUserDetails = async () => {
        if (userSession?.id) {
            try {
                // Récupérer et afficher le login
                const loginResponse = await fetch(`http://localhost:8080/api/users/${userSession.id}/login`);
                if (loginResponse.ok) {
                    const login = await loginResponse.text();
                    const profileDetailLogin = document.getElementById('profileDetailLogin');
                    if (profileDetailLogin) {
                        profileDetailLogin.textContent = login;
                        console.log('Login mis à jour');
                    }
                }

                // Récupérer et afficher l'email
                const emailResponse = await fetch(`http://localhost:8080/api/users/${userSession.id}/email`);
                if (emailResponse.ok) {
                    const email = await emailResponse.text();
                    const profileDetailEmail = document.getElementById('profileDetailEmail');
                    if (profileDetailEmail) {
                        profileDetailEmail.textContent = email;
                        console.log('Email mis à jour');
                    }
                }
            } catch (error) {
                console.error('Erreur lors de la récupération des détails :', error);
                // Gestion des erreurs
                const profileDetailLogin = document.getElementById('profileDetailLogin');
                const profileDetailEmail = document.getElementById('profileDetailEmail');
                if (profileDetailLogin) profileDetailLogin.textContent = 'Non disponible';
                if (profileDetailEmail) profileDetailEmail.textContent = 'Non disponible';
            }
        }
    };

    // Fonction pour mettre à jour les détails du profil
    const updateProfileDetails = async () => {
        if (userSession?.id) {
            try {
                // Afficher le prénom (depuis la session)
                const profileDetailFirstname = document.getElementById('profileDetailFirstname');
                if (profileDetailFirstname) {
                    profileDetailFirstname.textContent = userSession.firstname;
                }

                // Afficher le login (depuis la session)
                const profileDetailLogin = document.getElementById('profileDetailLogin');
                if (profileDetailLogin) {
                    profileDetailLogin.textContent = userSession.login;
                }

                // Récupérer et afficher le nom
                const response = await fetch(`http://localhost:8080/api/users/${userSession.id}/lastname`);
                if (response.ok) {
                    const lastname = await response.text();
                    const profileDetailName = document.getElementById('profileDetailName');
                    if (profileDetailName) {
                        profileDetailName.textContent = lastname;
                    }
                }

                // Récupérer et afficher l'email
                const emailResponse = await fetch(`http://localhost:8080/api/users/${userSession.id}/email`);
                if (emailResponse.ok) {
                    const email = await emailResponse.text();
                    const profileDetailEmail = document.getElementById('profileDetailEmail');
                    if (profileDetailEmail) {
                        profileDetailEmail.textContent = email;
                    }
                }

                // Récupérer et afficher la date de création
                const createdAtResponse = await fetch(`http://localhost:8080/api/users/${userSession.id}/created-at`);
                if (createdAtResponse.ok) {
                    const createdAt = await createdAtResponse.text();
                    const date = new Date(createdAt);
                    const formattedDate = new Intl.DateTimeFormat('fr-FR', {
                        day: '2-digit',
                        month: '2-digit',
                        year: 'numeric'
                    }).format(date);
                    
                    const profileDetailCreatedAt = document.getElementById('profileDetailCreatedAt');
                    if (profileDetailCreatedAt) {
                        profileDetailCreatedAt.textContent = formattedDate;
                    }
                }
            } catch (error) {
                console.error('Erreur lors de la récupération des détails du profil :', error);
            }
        }
    };

    // Mettre à jour le profil utilisateur
    if (userSession) {
        // Appeler la fonction pour récupérer le nom complet
        displayFullName();

        // Appeler la fonction pour récupérer la date de création
        displayCreatedAt();

        // Mise à jour du rôle
        const profileRole = document.getElementById('profileRole');
        if (profileRole && userSession.role) {
            const translatedRole = translateRole(userSession.role);
            profileRole.textContent = translatedRole;
            console.log('Rôle mis à jour :', translatedRole);
        }

        // Appeler la fonction de mise à jour des détails
        updateProfileDetails();

        // Appeler la fonction de mise à jour des détails utilisateur
        displayUserDetails();
    }
});