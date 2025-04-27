import 'dart:convert';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:optitop_mobile/config/constants.dart';
import 'package:optitop_mobile/models/user.dart';

class AuthService extends ChangeNotifier {
  User? _currentUser;
  final _storage = const FlutterSecureStorage();
  
  User? get currentUser => _currentUser;
  bool get isAuthenticated => _currentUser != null;
  bool get isAdmin => _currentUser?.role == 'admin' || _currentUser?.role == 'supermanager';

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