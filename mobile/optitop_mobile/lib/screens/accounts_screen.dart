import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import '../services/auth_service.dart';
import '../services/accounts_service.dart';
import '../models/account_request.dart';
import '../models/user.dart';
import '../config/constants.dart';

class AccountsScreen extends StatefulWidget {
  const AccountsScreen({super.key});

  @override
  State<AccountsScreen> createState() => _AccountsScreenState();
}

class _AccountsScreenState extends State<AccountsScreen> {
  late Future<List<AccountRequest>> _pendingAccountsFuture;
  late Future<List<User>> _usersFuture;
  bool _isUserListExpanded = false;

  @override
  void initState() {
    super.initState();
    _initializeData();
  }

  void _initializeData() {
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (!mounted) return;
      final userId = context.read<AuthService>().currentUser?.id ?? 0;
      setState(() {
        _pendingAccountsFuture = AccountsService().getPendingAccounts(userId);
        _usersFuture = AccountsService().getAllUsers(userId);
      });
    });
  }

  void _refresh() {
    setState(() {
      final userId = context.read<AuthService>().currentUser?.id ?? 0;
      _pendingAccountsFuture = AccountsService().getPendingAccounts(userId);
      _usersFuture = AccountsService().getAllUsers(userId);
    });
  }

  Future<void> _handleLogout() async {
    final auth = context.read<AuthService>();
    
    final shouldLogout = await showDialog<bool>(
      context: context,
      builder: (dialogContext) => AlertDialog(
        title: const Text('Déconnexion'),
        content: const Text('Voulez-vous vraiment vous déconnecter ?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(dialogContext).pop(false),
            child: const Text('Annuler'),
          ),
          TextButton(
            onPressed: () => Navigator.of(dialogContext).pop(true),
            child: const Text('Se déconnecter'),
          ),
        ],
      ),
    ) ?? false;

    if (!mounted) return;
    
    if (shouldLogout) {
      try {
        await auth.logout();
        if (!mounted) return;
        
        Navigator.of(context).pushNamedAndRemoveUntil(
          '/login',
          (route) => false,
        );
      } catch (e) {
        if (!mounted) return;
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Erreur lors de la déconnexion: $e')),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final auth = context.watch<AuthService>();
    final isAdmin = auth.currentUser?.role == 'admin';

    return Scaffold(
      appBar: AppBar(
        backgroundColor: Colors.grey[100],
        elevation: 2,
        title: Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Image.asset(
              'assets/images/logo_optitop.png',
              height: 40,
              // Ajouter errorBuilder pour gérer les erreurs de chargement
              errorBuilder: (context, error, stackTrace) {
                return const Icon(Icons.store, size: 32);  
              },
            ),
          ],
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.logout),
            tooltip: 'Déconnexion',
            onPressed: _handleLogout,
          ),
          const SizedBox(width: 8), 
        ],
        bottom: PreferredSize(
          preferredSize: const Size.fromHeight(1),
          child: Container(
            color: Colors.grey[100], 
            height: 1,
          ),
        ),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Padding(
              padding: const EdgeInsets.only(bottom: 24),
              child: Text(
                'Gestion des comptes',
                style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                  color: Theme.of(context).primaryColor,
                  fontWeight: FontWeight.bold,
                ),
                textAlign: TextAlign.center,
              ),
            ),

            // 1. Bouton nouvelle demande
            ElevatedButton.icon(
              icon: const Icon(Icons.add),
              label: const Text('Nouvelle demande'),
              onPressed: () {
                showDialog(
                  context: context,
                  builder: (_) => const _NewAccountRequestDialog(),
                ).then((_) => _refresh());
              },
            ),
            const SizedBox(height: 24),

            // 2. Liste des demandes en cours
            Text('Demandes en cours', style: Theme.of(context).textTheme.titleMedium),
            FutureBuilder<List<AccountRequest>>(
              future: _pendingAccountsFuture,
              builder: (context, snapshot) {
                if (snapshot.connectionState == ConnectionState.waiting) {
                  return const Center(child: CircularProgressIndicator());
                }
                if (snapshot.hasError) {
                  return Text('Erreur : ${snapshot.error}');
                }
                final demandes = snapshot.data ?? [];
                if (demandes.isEmpty) {
                  return const Text('Aucune demande en cours.');
                }
                return ListView.separated(
                  shrinkWrap: true,
                  physics: const NeverScrollableScrollPhysics(),
                  itemCount: demandes.length,
                  separatorBuilder: (_, __) => const Divider(),
                  itemBuilder: (context, i) {
                    final demande = demandes[i];
                    return ListTile(
                      title: Text('${demande.firstname} ${demande.lastname} (${demande.requestType})'),
                      subtitle: Text('${demande.login} - ${demande.role}'),
                      // Affiche les boutons uniquement pour les admins
                      trailing: isAdmin // Maintenant cela ne s'applique qu'aux vrais admins
                        ? Row(
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              IconButton(
                                icon: const Icon(Icons.check, color: Colors.green),
                                onPressed: () async {
                                  await AccountsService().validateAccount(demande.id, auth.currentUser!.id);
                                  _refresh();
                                },
                              ),
                              IconButton(
                                icon: const Icon(Icons.close, color: Colors.red),
                                onPressed: () async {
                                  await AccountsService().rejectAccount(demande.id, auth.currentUser!.id);
                                  _refresh();
                                },
                              ),
                            ],
                          )
                        : null,
                    );
                  },
                );
              },
            ),
            const SizedBox(height: 24),

            // 3. Liste des comptes utilisateurs (admin uniquement)
            if (isAdmin) ...[  // Ne s'affiche que pour les vrais admins
              ExpansionTile(
                title: Text(
                  'Comptes utilisateurs', 
                  style: Theme.of(context).textTheme.titleMedium,
                ),
                initiallyExpanded: _isUserListExpanded,
                onExpansionChanged: (expanded) {
                  setState(() => _isUserListExpanded = expanded);
                },
                children: [
                  FutureBuilder<List<User>>(
                    future: _usersFuture,
                    builder: (context, snapshot) {
                      if (snapshot.connectionState == ConnectionState.waiting) {
                        return const Center(child: CircularProgressIndicator());
                      }
                      if (snapshot.hasError) {
                        return Text('Erreur : ${snapshot.error}');
                      }
                      final users = snapshot.data ?? [];
                      if (users.isEmpty) {
                        return const Padding(
                          padding: EdgeInsets.all(16.0),
                          child: Text('Aucun utilisateur.'),
                        );
                      }
                      return ListView.separated(
                        shrinkWrap: true,
                        physics: const NeverScrollableScrollPhysics(),
                        itemCount: users.length,
                        separatorBuilder: (_, __) => const Divider(),
                        itemBuilder: (context, i) {
                          final user = users[i];
                          return ListTile(
                            title: Text('${user.firstname} ${user.lastname} - ${user.role}'),
                            subtitle: Text(user.login),
                          );
                        },
                      );
                    },
                  ),
                ],
              ),
            ],
          ],
        ),
      ),
    );
  }
}

class _NewAccountRequestDialog extends StatefulWidget {
  const _NewAccountRequestDialog();

  @override
  State<_NewAccountRequestDialog> createState() => _NewAccountRequestDialogState();
}

class _NewAccountRequestDialogState extends State<_NewAccountRequestDialog> {
  final _formKey = GlobalKey<FormState>();
  String _requestType = 'ajout';
  String? _role;
  String? _login;
  String? _firstname;
  String? _lastname;
  String? _email;
  bool _isLoading = false;
  List<String> _availableLogins = [];
  List<Map<String, String>> _availableSellers = [];
  User? _selectedUser;
  bool _hasChanges = false;
  Map<String, dynamic> _originalValues = {};

  @override
  void initState() {
    super.initState();
    _loadAvailableLoginsOrSellers();
  }

  Future<void> _loadAvailableLoginsOrSellers() async {
    try {
      final userId = context.read<AuthService>().currentUser?.id ?? 0;
      
      if (_requestType == 'suppression') {
        // Pour suppression, charge tous les utilisateurs
        final users = await AccountsService().getAllUsers(userId);
        setState(() {
          _availableLogins = users
              .map((user) => user.login)
              .toList();
          _login = null;
        });
      } else if (_requestType == 'modification') {
        // Pour modification, charge aussi tous les utilisateurs
        final users = await AccountsService().getAllUsers(userId);
        setState(() {
          _availableLogins = users
              .map((user) => user.login)
              .toList();
          _login = null;
        });
      } else if (_requestType == 'ajout' && (_role == 'collaborator' || _role == 'manager')) {
        // Pour ajout de collaborator/manager, charge les vendeurs disponibles
        final sellers = await AccountsService().getAvailableSellers(userId);
        setState(() {
          _availableSellers = sellers;
          _login = null;
        });
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Erreur: $e')),
        );
      }
    }
  }

  Future<void> _loadUserData(String login) async {
    try {
      final userId = context.read<AuthService>().currentUser?.id ?? 0;
      final users = await AccountsService().getAllUsers(userId);
      final user = users.firstWhere((u) => u.login == login);
      setState(() {
        _selectedUser = user;
        _originalValues = {
          'role': user.role,
          'firstname': user.firstname,
          'lastname': user.lastname,
          'email': user.email,
        };
        _role = user.role;
        _firstname = user.firstname;
        _lastname = user.lastname;
        _email = user.email;
      });
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Erreur: $e')),
        );
      }
    }
  }

  void _checkChanges() {
    setState(() {
      _hasChanges = false;
      if (_role != _originalValues['role']) _hasChanges = true;
      if (_firstname != _originalValues['firstname']) _hasChanges = true;
      if (_lastname != _originalValues['lastname']) _hasChanges = true;
      if (_email != _originalValues['email']) _hasChanges = true;
    });
  }

  Future<void> _submitForm() async {
    if (!_formKey.currentState!.validate()) return;
    _formKey.currentState!.save();

    setState(() => _isLoading = true);

    try {
      final userId = context.read<AuthService>().currentUser?.id ?? 0;
      Map<String, dynamic> formData = {
        'requestType': _requestType,
        'login': _login,
      };

      if (_requestType == 'ajout') {
        formData.addAll({
          'lastname': _lastname,
          'firstname': _firstname,
          'email': _email,
          'role': _role,
        });
      } else if (_requestType == 'modification') {
        if (_role != null) formData['role'] = _role;
        if (_lastname != null) formData['lastname'] = _lastname;
        if (_firstname != null) formData['firstname'] = _firstname;
        if (_email != null) formData['email'] = _email;
      }

      final response = await http.post(
        Uri.parse('${AppConstants.apiBaseUrl}/pending-accounts'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $userId',
        },
        body: json.encode(formData),
      );

      if (response.statusCode == 201) {
        if (!mounted) return;
        Navigator.of(context).pop(true);
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Demande envoyée avec succès')),
        );
      } else {
        throw Exception('Erreur lors de l\'envoi de la demande');
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Erreur: $e')),
        );
      }
    } finally {
      if (mounted) {
        setState(() => _isLoading = false);
      }
    }
  }

  Widget _buildLoginField() {
    if (_requestType == 'suppression' || _requestType == 'modification') {
      // Pour suppression et modification, affiche la liste des logins existants
      return DropdownButtonFormField<String>(
        value: _login,
        decoration: const InputDecoration(
          labelText: 'Login',
          border: OutlineInputBorder(),
        ),
        items: _availableLogins.map((login) => DropdownMenuItem(
          value: login,
          child: Text(login),
        )).toList(),
        onChanged: (value) => setState(() => _login = value),
        validator: (value) => value == null ? 'Champ requis' : null,
      );
    } else if (_requestType == 'ajout' && (_role == 'collaborator' || _role == 'manager')) {
      // Pour ajout de collaborator/manager, affiche les vendeurs disponibles
      return DropdownButtonFormField<String>(
        value: _login,
        decoration: const InputDecoration(
          labelText: 'Vendeur',
          border: OutlineInputBorder(),
        ),
        items: _availableSellers.map((seller) => DropdownMenuItem(
          value: seller['sellerRef'],
          child: Text('${seller['sellerRef']}'),
        )).toList(),
        onChanged: (value) => setState(() => _login = value),
        validator: (value) => value == null ? 'Champ requis' : null,
      );
    } else {
      // Pour autres cas (ajout admin/supermanager), champ texte libre
      return TextFormField(
        decoration: const InputDecoration(
          labelText: 'Login',
          border: OutlineInputBorder(),
        ),
        onSaved: (value) => _login = value,
        validator: (value) => value?.isEmpty ?? true ? 'Champ requis' : null,
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Dialog(
      child: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Form(
          key: _formKey,
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              Text(
                'Nouvelle demande',
                style: Theme.of(context).textTheme.titleLarge,
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 24),
              
              // Type de demande
              DropdownButtonFormField<String>(
                value: _requestType,
                decoration: const InputDecoration(
                  labelText: 'Type de demande',
                  border: OutlineInputBorder(),
                ),
                items: const [
                  DropdownMenuItem(value: 'ajout', child: Text('Ajout')),
                  DropdownMenuItem(value: 'modification', child: Text('Modification')),
                  DropdownMenuItem(value: 'suppression', child: Text('Suppression')),
                ],
                onChanged: (value) {
                  setState(() {
                    _requestType = value!;
                    _login = null; // Reset login
                  });
                  _loadAvailableLoginsOrSellers(); // Recharge les options selon le type
                },
              ),
              const SizedBox(height: 16),

              if (_requestType == 'modification') ...[
                DropdownButtonFormField<String>(
                  value: _login,
                  decoration: const InputDecoration(
                    labelText: 'Login',
                    border: OutlineInputBorder(),
                  ),
                  items: _availableLogins.map((login) => DropdownMenuItem(
                    value: login,
                    child: Text(login),
                  )).toList(),
                  onChanged: (value) {
                    setState(() => _login = value);
                    if (value != null) {
                      _loadUserData(value);
                    }
                  },
                  validator: (value) => value == null ? 'Champ requis' : null,
                ),
                const SizedBox(height: 16),

                if (_selectedUser != null) ...[
                  DropdownButtonFormField<String>(
                    value: _role,
                    decoration: const InputDecoration(
                      labelText: 'Rôle',
                      border: OutlineInputBorder(),
                    ),
                    items: const [
                      DropdownMenuItem(value: 'collaborator', child: Text('Collaborateur')),
                      DropdownMenuItem(value: 'manager', child: Text('Manager')),
                      DropdownMenuItem(value: 'supermanager', child: Text('SuperManager')),
                      DropdownMenuItem(value: 'admin', child: Text('Admin')),
                    ],
                    onChanged: (value) {
                      setState(() => _role = value);
                      _checkChanges();
                    },
                  ),
                  const SizedBox(height: 16),

                  TextFormField(
                    initialValue: _selectedUser?.lastname,
                    decoration: const InputDecoration(
                      labelText: 'Nom',
                      border: OutlineInputBorder(),
                    ),
                    onChanged: (value) {
                      _lastname = value;
                      _checkChanges();
                    },
                  ),
                  const SizedBox(height: 16),

                  TextFormField(
                    initialValue: _selectedUser?.firstname,
                    decoration: const InputDecoration(
                      labelText: 'Prénom',
                      border: OutlineInputBorder(),
                    ),
                    onChanged: (value) {
                      _firstname = value;
                      _checkChanges();
                    },
                  ),
                  const SizedBox(height: 16),

                  TextFormField(
                    initialValue: _selectedUser?.email,
                    decoration: const InputDecoration(
                      labelText: 'Email',
                      border: OutlineInputBorder(),
                    ),
                    keyboardType: TextInputType.emailAddress,
                    onChanged: (value) {
                      _email = value;
                      _checkChanges();
                    },
                    validator: (value) {
                      if (value?.isNotEmpty ?? false) {
                        final emailRegex = RegExp(r'^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$');
                        if (!emailRegex.hasMatch(value!)) {
                          return 'Email invalide';
                        }
                      }
                      return null;
                    },
                  ),
                ],
              ] else if (_requestType == 'suppression') ...[
                const SizedBox(height: 16),
                _buildLoginField(),
              ] else ...[
                const SizedBox(height: 16),
                DropdownButtonFormField<String>(
                  value: _role,
                  decoration: const InputDecoration(
                    labelText: 'Rôle',
                    border: OutlineInputBorder(),
                  ),
                  items: const [
                    DropdownMenuItem(value: 'collaborator', child: Text('Collaborateur')),
                    DropdownMenuItem(value: 'manager', child: Text('Manager')),
                    DropdownMenuItem(value: 'supermanager', child: Text('SuperManager')),
                    DropdownMenuItem(value: 'admin', child: Text('Admin')),
                    
                  ],
                  onChanged: (value) {
                    setState(() {
                      _role = value;
                      _login = null; // Reset login when role changes
                    });
                    _loadAvailableLoginsOrSellers(); // Reload login options based on role
                  },
                  validator: (value) => _requestType == 'ajout' && value == null
                      ? 'Champ requis'
                      : null,
                ),

                const SizedBox(height: 16),
              _buildLoginField(),

                const SizedBox(height: 16),
                TextFormField(
                  decoration: const InputDecoration(
                    labelText: 'Nom',
                    border: OutlineInputBorder(),
                  ),
                  onSaved: (value) => _lastname = value,
                  validator: (value) => _requestType == 'ajout' && (value?.isEmpty ?? true)
                      ? 'Champ requis'
                      : null,
                ),

                const SizedBox(height: 16),
                TextFormField(
                  decoration: const InputDecoration(
                    labelText: 'Prénom',
                    border: OutlineInputBorder(),
                  ),
                  onSaved: (value) => _firstname = value,
                  validator: (value) => _requestType == 'ajout' && (value?.isEmpty ?? true)
                      ? 'Champ requis'
                      : null,
                ),

                const SizedBox(height: 16),
                TextFormField(
                  decoration: const InputDecoration(
                    labelText: 'Email',
                    border: OutlineInputBorder(),
                  ),
                  keyboardType: TextInputType.emailAddress,
                  onSaved: (value) => _email = value,
                  validator: (value) {
                    if (_requestType == 'ajout' && (value?.isEmpty ?? true)) {
                      return 'Champ requis';
                    }
                    if (value?.isNotEmpty ?? false) {
                      final emailRegex = RegExp(r'^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$');
                      if (!emailRegex.hasMatch(value!)) {
                        return 'Email invalide';
                      }
                    }
                    return null;
                  },
                ),
              ],

              const SizedBox(height: 24),
              Row(
                mainAxisAlignment: MainAxisAlignment.end,
                children: [
                  TextButton(
                    onPressed: () => Navigator.of(context).pop(),
                    child: const Text('Annuler'),
                  ),
                  const SizedBox(width: 8),
                  ElevatedButton(
                    onPressed: (_requestType == 'modification' && !_hasChanges) || _isLoading 
                      ? null 
                      : _submitForm,
                    child: _isLoading
                        ? const SizedBox(
                            width: 20,
                            height: 20,
                            child: CircularProgressIndicator(strokeWidth: 2),
                          )
                        : const Text('Envoyer'),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}