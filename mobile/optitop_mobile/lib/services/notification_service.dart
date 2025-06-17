/// Service de gestion des notifications
/// 
/// Service responsable de la gestion des notifications :
// Diffusion des notifications de nouvelles demandes
// Affichage des pop-ups de notification aux admins
// Gestion du stream d'événements temps réel
/// 
library;

import 'dart:async';
import 'package:flutter/material.dart';
import '../models/account_request.dart';

class NotificationService with ChangeNotifier {
  
  // ===== STREAM CONTROLLER =====
  
  /// Contrôleur de stream pour diffuser les nouvelles demandes
  final StreamController<AccountRequest> _requestController = StreamController<AccountRequest>.broadcast();
  
  /// Stream des nouvelles demandes de compte
  Stream<AccountRequest> get requestStream => _requestController.stream;
  
  // ===== GESTION DES NOTIFICATIONS =====
  
  /// Notifie une nouvelle demande de compte
  /// 
  /// @param request Demande de compte à notifier
  void notifyNewRequest(AccountRequest request) {
    _requestController.add(request);
    notifyListeners();
  }
  
  /// Affiche une notification pop-up pour une nouvelle demande
  /// 
  /// @param context Contexte Flutter pour l'affichage
  /// @param request Demande de compte à afficher
  void showNewRequestNotification(BuildContext context, AccountRequest request) {
    if (!context.mounted) return;
    
    showDialog(
      context: context,
      barrierDismissible: true,
      builder: (BuildContext context) {
        return AlertDialog(
          alignment: Alignment.topCenter,
          backgroundColor: const Color(0xFF70B8FF),
          insetPadding: EdgeInsets.zero,
          contentPadding: const EdgeInsets.all(16),
          title: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Image.asset(
                'assets/images/logo_optitop.png',
                height: 50,
              ),
              const SizedBox(height: 8),
              Text(
                'Demande de ${request.requestType} reçue !',
                style: const TextStyle(
                  color: Color(0xFFF4FAFF),
                  fontSize: 14,
                  fontWeight: FontWeight.bold,
                ),
                textAlign: TextAlign.center,
              ),
            ],
          ),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                'Nom: ${request.firstname} ${request.lastname}',
                style: const TextStyle(color: Color(0xFFF4FAFF)),
              ),
              Text(
                'Rôle: ${request.role}',
                style: const TextStyle(color: Color(0xFFF4FAFF)),
              ),
            ],
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.of(context).pop(),
              child: const Text(
                'OK',
                style: TextStyle(
                  color: Color(0xFFF4FAFF),
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
          ],
        );
      },
    );
  }

  // ===== NETTOYAGE =====
  
  @override
  void dispose() {
    _requestController.close();
    super.dispose();
  }
}