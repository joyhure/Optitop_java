class User {
  final int id;
  final String login;
  final String firstname;
  final String lastname;
  final String email;
  final String role;

  User({
    required this.id,
    required this.login,
    required this.firstname,
    required this.lastname,
    required this.email,
    required this.role,
  });

  factory User.fromJson(Map<String, dynamic> json) {
    print('lastname: ${json['lastname']} (${json['lastname'].runtimeType})');
    print('firstname: ${json['firstname']} (${json['firstname'].runtimeType})');
    return User(
      id: json['id'],
      login: json['login'] ?? '',
      firstname: json['firstname'] ?? '',
      lastname: json['lastname'] ?? '',
      email: json['email'] ?? '',
      role: json['role'] ?? '',
    );
  }

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