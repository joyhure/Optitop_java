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
      print('Tentative de connexion...');
      final url = '${AppConstants.apiBaseUrl}/auth/login';
      print('URL complète: $url');
      print('Données envoyées: ${json.encode({
        'login': login,
        'password': password,
      })}');
      
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

      print('Status code: ${response.statusCode}');
      print('Response headers: ${response.headers}');
      print('Response body: ${response.body}');

      if (response.statusCode == 200) {
        final userData = json.decode(response.body);
        _currentUser = User.fromJson(userData);
        
        // Vérifier si l'utilisateur a les droits appropriés
        if (!AppConstants.adminRoles.contains(_currentUser!.role)) {
          _currentUser = null;
          throw Exception('Accès non autorisé : rôle insuffisant');
        }
        
        // Stocker les données utilisateur
        await _storage.write(key: 'user', value: json.encode(userData));
        notifyListeners();
      } else {
        throw Exception('Identifiants incorrects');
      }
    } catch (e) {
      print('Erreur de connexion: $e');
      rethrow;
    }
  }

  Future<void> logout() async {
    await _storage.delete(key: 'user');
    _currentUser = null;
    notifyListeners();
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