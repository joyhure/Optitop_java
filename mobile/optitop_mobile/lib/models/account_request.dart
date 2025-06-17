/// Modèle de demande de compte utilisateur
/// 
/// Classe représentant une demande de création ou modification
/// d'un compte utilisateur dans le système Optitop.
/// 
/// @author Joy Huré
/// @version 1.0
library;

class AccountRequest {
  
  // ===== PROPRIÉTÉS =====
  
  /// Identifiant unique de la demande
  final int id;
  
  /// Nom de famille de l'utilisateur
  final String lastname;
  
  /// Prénom de l'utilisateur
  final String firstname;
  
  /// Adresse email de l'utilisateur
  final String email;
  
  /// Identifiant de connexion
  final String login;
  
  /// Rôle demandé
  final String role;
  
  /// Type de demande (création, modification, suppression)
  final String requestType;
  
  /// Date de création de la demande
  final String createdAt;
  
  /// Login de l'utilisateur qui a créé la demande (optionnel)
  final String? createdByLogin;

  // ===== CONSTRUCTEUR =====
  
  AccountRequest({
    required this.id,
    required this.lastname,
    required this.firstname,
    required this.email,
    required this.login,
    required this.role,
    required this.requestType,
    required this.createdAt,
    this.createdByLogin,
  });

  // ===== SÉRIALISATION =====
  
  /// Crée une instance AccountRequest à partir d'un JSON
  /// 
  /// @param json Map contenant les données JSON de l'API
  /// @return Instance AccountRequest avec valeurs par défaut si nécessaire
  factory AccountRequest.fromJson(Map<String, dynamic> json) {
    return AccountRequest(
      id: json['id'] ?? 0,
      lastname: json['lastname'] ?? '',
      firstname: json['firstname'] ?? '',
      email: json['email'] ?? '',
      login: json['login'] ?? '',
      role: json['role'] ?? '',
      requestType: json['requestType'] ?? '',
      createdAt: json['createdAt'] ?? '',
      createdByLogin: json['createdByLogin'],
    );
  }
}