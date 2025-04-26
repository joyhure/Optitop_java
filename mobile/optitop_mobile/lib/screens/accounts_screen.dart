import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/auth_service.dart';
import '../services/accounts_service.dart';
import '../models/account_request.dart';
import '../models/user.dart';

class AccountsScreen extends StatefulWidget {
  const AccountsScreen({super.key});

  @override
  State<AccountsScreen> createState() => _AccountsScreenState();
}

class _AccountsScreenState extends State<AccountsScreen> {
  late Future<List<AccountRequest>> _pendingAccountsFuture;
  late Future<List<User>> _usersFuture;

  @override
  void initState() {
    super.initState();
    final userId = context.read<AuthService>().currentUser?.id ?? 0;
    _pendingAccountsFuture = AccountsService().getPendingAccounts(userId);
    _usersFuture = AccountsService().getAllUsers(userId);
  }

  void _refresh() {
    setState(() {
      final userId = context.read<AuthService>().currentUser?.id ?? 0;
      _pendingAccountsFuture = AccountsService().getPendingAccounts(userId);
      _usersFuture = AccountsService().getAllUsers(userId);
    });
  }

  @override
  Widget build(BuildContext context) {
    final auth = context.watch<AuthService>();
    final isAdmin = auth.isAdmin;

    return Scaffold(
      appBar: AppBar(title: const Text('Gestion des comptes utilisateurs')),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
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
                      subtitle: Text('Login: ${demande.login} - Rôle: ${demande.role}'),
                      trailing: isAdmin
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
            if (isAdmin) ...[
              Text('Comptes utilisateurs', style: Theme.of(context).textTheme.titleMedium),
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
                    return const Text('Aucun utilisateur.');
                  }
                  return ListView.separated(
                    shrinkWrap: true,
                    physics: const NeverScrollableScrollPhysics(),
                    itemCount: users.length,
                    separatorBuilder: (_, __) => const Divider(),
                    itemBuilder: (context, i) {
                      final user = users[i];
                      return ListTile(
                        title: Text('${user.firstname} ${user.lastname}'),
                        subtitle: Text('Login: ${user.login} - Rôle: ${user.role}'),
                      );
                    },
                  );
                },
              ),
            ],
          ],
        ),
      ),
    );
  }
}

// Formulaire de nouvelle demande (à compléter selon tes besoins)
class _NewAccountRequestDialog extends StatelessWidget {
  const _NewAccountRequestDialog();

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: const Text('Nouvelle demande'),
      content: const Text('Formulaire à implémenter ici.'),
      actions: [
        TextButton(
          onPressed: () => Navigator.of(context).pop(),
          child: const Text('Fermer'),
        ),
      ],
    );
  }
}