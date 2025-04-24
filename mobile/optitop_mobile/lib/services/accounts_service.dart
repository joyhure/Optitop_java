import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:optitop_mobile/config/constants.dart';
import 'package:optitop_mobile/models/account_request.dart';
import 'package:optitop_mobile/models/user.dart';

class AccountsService {
  Future<List<AccountRequest>> getPendingAccounts(int userId) async {
    try {
      final response = await http.get(
        Uri.parse('${AppConstants.apiBaseUrl}/accounts/pending/$userId'),
        headers: {'Content-Type': 'application/json'},
      );

      if (response.statusCode == 200) {
        final List<dynamic> data = json.decode(response.body);
        return data.map((json) => AccountRequest.fromJson(json)).toList();
      } else {
        throw Exception('Échec du chargement des demandes');
      }
    } catch (e) {
      rethrow;
    }
  }

  Future<void> validateAccount(int accountId, int validatorId) async {
    try {
      final response = await http.post(
        Uri.parse('${AppConstants.apiBaseUrl}/accounts/validate'),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'accountId': accountId,
          'validatorId': validatorId,
        }),
      );

      if (response.statusCode != 200) {
        throw Exception('Échec de la validation');
      }
    } catch (e) {
      rethrow;
    }
  }

  Future<void> rejectAccount(int accountId, int validatorId) async {
    try {
      final response = await http.post(
        Uri.parse('${AppConstants.apiBaseUrl}/accounts/reject'),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'accountId': accountId,
          'validatorId': validatorId,
        }),
      );

      if (response.statusCode != 200) {
        throw Exception('Échec du rejet');
      }
    } catch (e) {
      rethrow;
    }
  }

  Future<List<User>> getAllUsers() async {
    try {
      final response = await http.get(
        Uri.parse('${AppConstants.apiBaseUrl}/users'),
        headers: {'Content-Type': 'application/json'},
      );

      if (response.statusCode == 200) {
        final List<dynamic> data = json.decode(response.body);
        return data.map((json) => User.fromJson(json)).toList();
      } else {
        throw Exception('Échec du chargement des utilisateurs');
      }
    } catch (e) {
      rethrow;
    }
  }
}