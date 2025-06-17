/// Service de gestion des comptes utilisateurs
/// 
/// Service responsable des opérations liées aux comptes :
/// - Récupération des demandes de comptes en cours
/// - Validation et rejet des demandes (admin)
/// - Gestion de la liste des utilisateurs
/// - Récupération des vendeurs disponibles
/// 
library;

import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:optitop_mobile/config/constants.dart';
import 'package:optitop_mobile/models/account_request.dart';
import 'package:optitop_mobile/models/user.dart';

class AccountsService {
  
  // ===== GESTION DES DEMANDES =====
  
  /// Récupère les demandes de comptes en cours
  /// 
  /// @param userId ID de l'utilisateur pour l'authentification
  /// @return Liste des demandes en attente de validation
  Future<List<AccountRequest>> getPendingAccounts(int userId) async {
    try {
      final response = await http.get(
        Uri.parse('${AppConstants.apiBaseUrl}/pending-accounts'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $userId',
        },
      );

      if (response.statusCode == 200) {
        final List<dynamic> data = json.decode(utf8.decode(response.bodyBytes));
        return data.map((json) => AccountRequest.fromJson(json)).toList();
      } else {
        throw Exception('Échec du chargement des demandes : ${response.body}');
      }
    } catch (e) {
      rethrow;
    }
  }

  /// Valide une demande de compte (admin uniquement)
  /// 
  /// @param accountId ID de la demande à valider
  /// @param validatorId ID de l'administrateur validant
  Future<void> validateAccount(int accountId, int validatorId) async {
    try {
      final response = await http.post(
        Uri.parse('${AppConstants.apiBaseUrl}/pending-accounts/validate/$accountId'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $validatorId',
        },
      );

      if (response.statusCode != 200) {
        throw Exception('Échec de la validation : ${response.body}');
      }
    } catch (e) {
      rethrow;
    }
  }

  /// Rejette une demande de compte (admin uniquement)
  /// 
  /// @param accountId ID de la demande à rejeter
  /// @param validatorId ID de l'administrateur rejetant
  Future<void> rejectAccount(int accountId, int validatorId) async {
    try {
      final response = await http.post(
        Uri.parse('${AppConstants.apiBaseUrl}/pending-accounts/reject/$accountId'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $validatorId',
        },
      );

      if (response.statusCode != 200) {
        throw Exception('Échec du rejet : ${response.body}');
      }
    } catch (e) {
      rethrow;
    }
  }

  // ===== GESTION DES UTILISATEURS =====
  
  /// Récupère tous les comptes utilisateurs (admin uniquement)
  /// 
  /// @param userId ID de l'utilisateur pour l'authentification
  /// @return Liste de tous les utilisateurs du système
  Future<List<User>> getAllUsers(int userId) async {
    final response = await http.get(
      Uri.parse('${AppConstants.apiBaseUrl}/users/all'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $userId',
      },
    );
    
    if (response.statusCode == 200) {
      final List<dynamic> data = json.decode(utf8.decode(response.bodyBytes));
      return data.map((json) => User.fromJson(json)).toList();
    } else {
      throw Exception('Échec du chargement des utilisateurs : ${response.body}');
    }
  }

  // ===== GESTION DES VENDEURS =====
  
  /// Récupère la liste des vendeurs disponibles
  /// 
  /// @param userId ID de l'utilisateur pour l'authentification
  /// @return Liste des vendeurs disponibles pour création de compte
  Future<List<Map<String, String>>> getAvailableSellers(int userId) async {
    final response = await http.get(
      Uri.parse('${AppConstants.apiBaseUrl}/sellers/available-sellers'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $userId',
      },
    );
    
    if (response.statusCode == 200) {
      final List<dynamic> data = json.decode(utf8.decode(response.bodyBytes));
      return data.map((seller) => {
        'sellerRef': seller['sellerRef']?.toString() ?? '',
        'name': seller['name']?.toString() ?? ''
      }).toList();
    } else {
      throw Exception('Échec du chargement des vendeurs disponibles');
    }
  }
}