/// Constantes de configuration de l'application mobile Optitop
/// 
/// Centralise les valeurs de configuration globales :
/// - URL de base de l'API backend
/// - Définition des rôles administrateurs
/// - Paramètres de configuration réseau
/// 
/// @author Joy Huré
/// @version 1.0
library;

class AppConstants {
  
  // ===== CONFIGURATION API =====
  
  /// URL de base de l'API backend
  /// Utilise 10.0.2.2 pour accéder à localhost depuis l'émulateur Android
  static const String apiBaseUrl = 'http://10.0.2.2:8080/api';
  
  // ===== RÔLES ET PERMISSIONS =====
  
  /// Liste des rôles ayant des privilèges administrateurs
  /// Permet l'accès aux fonctionnalités de gestion des comptes
  static const List<String> adminRoles = ['admin', 'supermanager'];
}