import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:optitop_mobile/config/constants.dart';
import 'package:optitop_mobile/models/account_request.dart';
import 'package:optitop_mobile/models/user.dart';

class AccountsService {
  // Récupérer les demandes en cours (tous les utilisateurs)
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
        final List<dynamic> data = json.decode(response.body);
        return data.map((json) => AccountRequest.fromJson(json)).toList();
      } else {
        throw Exception('Échec du chargement des demandes : ${response.body}');
      }
    } catch (e) {
      rethrow;
    }
  }

  // Valider une demande (admin uniquement)
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

  // Rejeter une demande (admin uniquement)
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

  // Récupérer tous les comptes utilisateurs (admin uniquement)
  Future<List<User>> getAllUsers(int userId) async {
    try {
      final response = await http.get(
        Uri.parse('${AppConstants.apiBaseUrl}/users/all'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $userId',
        },
      );

      if (response.statusCode == 200) {
        final List<dynamic> data = json.decode(response.body);
        return data.map((json) => User.fromJson(json)).toList();
      } else {
        throw Exception('Échec du chargement des utilisateurs : ${response.body}');
      }
    } catch (e) {
      rethrow;
    }
  }
}