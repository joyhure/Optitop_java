class AccountRequest {
  final int id; 
  final String lastname;
  final String firstname;
  final String email;
  final String login;
  final String role;
  final String requestType;
  final String createdAt;
  final String? createdByLogin;

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