/// Service d'authentification de l'application Optitop
/// 
/// Service responsable de la gestion de l'authentification :
/// - Connexion et déconnexion des utilisateurs
/// - Persistance sécurisée des données utilisateur
/// - Vérification des rôles et permissions
/// - Gestion de l'état d'authentification
/// 
library;

import 'dart:convert';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:optitop_mobile/config/constants.dart';
import 'package:optitop_mobile/models/user.dart';

class AuthService extends ChangeNotifier {
  
  // ===== PROPRIÉTÉS PRIVÉES =====
  
  /// Utilisateur actuellement connecté
  User? _currentUser;
  
  /// Instance de stockage sécurisé pour la persistance
  final _storage = const FlutterSecureStorage();
  
  // ===== GETTERS PUBLICS =====
  
  /// Utilisateur actuellement connecté
  User? get currentUser => _currentUser;
  
  /// Indique si un utilisateur est authentifié
  bool get isAuthenticated => _currentUser != null;
  
  /// Indique si l'utilisateur connecté a des privilèges admin
  bool get isAdmin => _currentUser?.role == 'admin' || _currentUser?.role == 'supermanager';

  // ===== MÉTHODES D'AUTHENTIFICATION =====
  
  /// Connecte un utilisateur avec ses identifiants
  /// 
  /// @param login Identifiant de connexion
  /// @param password Mot de passe
  /// @throws Exception si les identifiants sont incorrects ou rôle insuffisant
  Future<void> login(String login, String password) async {
    try {
      final url = '${AppConstants.apiBaseUrl}/auth/login';
      
      final response = await http.post(
        Uri.parse(url),
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
        },
        body: json.encode({
          'login': login,
          'password': password,
        }),
      );

      if (response.statusCode == 200) {
        final userData = json.decode(response.body);
        _currentUser = User.fromJson(userData);
        
        if (!AppConstants.adminRoles.contains(_currentUser!.role)) {
          _currentUser = null;
          throw Exception('Accès non autorisé : rôle insuffisant');
        }
        
        await _storage.write(key: 'user', value: json.encode(userData));
        notifyListeners();
      } else {
        throw Exception('Identifiants incorrects');
      }
    } catch (e) {
      rethrow;
    }
  }

  /// Déconnecte l'utilisateur actuel
  /// 
  /// Effectue la déconnexion côté serveur puis nettoie les données locales.
  /// En cas d'erreur serveur, nettoie quand même les données locales.
  /// 
  /// @throws Exception si erreur lors de la déconnexion
  Future<void> logout() async {
    try {
      // Appel à l'API pour la déconnexion
      final response = await http.post(
        Uri.parse('${AppConstants.apiBaseUrl}/auth/logout'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ${_currentUser?.id}',
        },
      );

      if (response.statusCode != 200) {
        throw Exception('Erreur lors de la déconnexion côté serveur');
      }

      // Si la déconnexion serveur réussit, on nettoie localement
      await _storage.delete(key: 'user');
      _currentUser = null;
      notifyListeners();
    } catch (e) {
      // En cas d'erreur on tente quand même de nettoyer localement
      await _storage.delete(key: 'user');
      _currentUser = null;
      notifyListeners();
      throw Exception('Erreur lors de la déconnexion: $e');
    }
  }

  // ===== GESTION DE LA PERSISTANCE =====
  
  /// Charge les données utilisateur depuis le stockage sécurisé
  /// 
  /// Utilisé au démarrage de l'application pour restaurer la session.
  /// Supprime les données corrompues si elles ne peuvent être lues.
  Future<void> loadUserFromStorage() async {
    final userStr = await _storage.read(key: 'user');
    if (userStr != null) {
      try {
        _currentUser = User.fromJson(json.decode(userStr));
        notifyListeners();
      } catch (e) {
        await _storage.delete(key: 'user');
      }
    }
  }
}