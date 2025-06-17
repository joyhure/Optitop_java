package com.optitop.optitop_api.service;

/**
 * Service de gestion des emails
 * 
 * Service responsable de l'envoi d'emails via SMTP avec configuration
 * dynamique depuis la base de données.
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import com.optitop.optitop_api.model.EmailConfig;
import com.optitop.optitop_api.repository.EmailConfigRepository;

import java.util.Properties;

@Service
public class EmailService {

    // ===== DÉPENDANCES =====

    @Autowired
    private EmailConfigRepository emailConfigRepository;

    // ===== MÉTHODES PUBLIQUES =====

    /**
     * Envoie un email avec les identifiants de connexion
     * 
     * @param to       Adresse email du destinataire
     * @param login    Login de l'utilisateur
     * @param password Mot de passe temporaire
     */
    public void sendPasswordEmail(String to, String login, String password) {
        EmailConfig config = emailConfigRepository.findFirstByOrderByIdDesc();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(config.getSmtpUsername());
        message.setTo(to);
        message.setSubject("Vos identifiants Optitop");
        message.setText(String.format("""
                Bonjour,

                Votre compte Optitop a été créé avec succès.

                Vos identifiants de connexion :
                Login : %s
                Mot de passe : %s

                Pour des raisons de sécurité, veuillez changer votre mot de passe lors de votre première connexion.

                Cordialement,
                L'équipe Optitop
                """, login, password));

        JavaMailSender sender = createMailSender();
        sender.send(message);
    }

    // ===== MÉTHODES PRIVÉES =====

    /**
     * Crée et configure un sender SMTP à partir de la configuration en base
     * 
     * @return JavaMailSender configuré
     */
    private JavaMailSender createMailSender() {
        EmailConfig config = emailConfigRepository.findFirstByOrderByIdDesc();
        if (config == null) {
            throw new RuntimeException("Aucune configuration email trouvée en base de données");
        }

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(config.getSmtpHost());
        mailSender.setPort(config.getSmtpPort());
        mailSender.setUsername(config.getSmtpUsername());
        mailSender.setPassword(config.getSmtpPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.debug", "true");

        return mailSender;
    }
}