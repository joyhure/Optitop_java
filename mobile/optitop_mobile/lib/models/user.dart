/// Modèle utilisateur de l'application Optitop
/// 
/// Classe représentant un utilisateur avec ses informations
/// personnelles et son rôle dans le système.
/// 
/// @author Joy Huré
/// @version 1.0
library;

class User {
  
  // ===== PROPRIÉTÉS =====
  
  /// Identifiant unique de l'utilisateur
  final int id;
  
  /// Identifiant de connexion
  final String login;
  
  /// Prénom de l'utilisateur
  final String firstname;
  
  /// Nom de famille de l'utilisateur
  final String lastname;
  
  /// Adresse email de l'utilisateur
  final String email;
  
  /// Rôle de l'utilisateur
  final String role;

  // ===== CONSTRUCTEUR =====
  
  User({
    required this.id,
    required this.login,
    required this.firstname,
    required this.lastname,
    required this.email,
    required this.role,
  });

  // ===== SÉRIALISATION =====
  
  /// Crée une instance User à partir d'un JSON
  /// 
  /// @param json Map contenant les données JSON de l'API
  /// @return Instance User avec valeurs par défaut si nécessaire
  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      id: json['id'],
      login: json['login'] ?? '',
      firstname: json['firstname'] ?? '',
      lastname: json['lastname'] ?? '',
      email: json['email'] ?? '',
      role: json['role'] ?? '',
    );
  }

  /// Convertit l'instance User en JSON
  /// 
  /// @return Map contenant les données de l'utilisateur
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'login': login,
      'firstname': firstname,
      'lastname': lastname,
      'email': email,
      'role': role,
    };
  }
}