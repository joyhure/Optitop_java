/// Point d'entrée principal de l'application mobile Optitop
/// 
/// Configure l'application Flutter avec :
/// - Providers pour la gestion d'état (auth, notifications)
/// - Thème et configuration visuelle
/// - Routes de navigation entre écrans
/// 
library;

import 'package:flutter/material.dart';
import 'package:optitop_mobile/screens/login_screen.dart';
import 'package:provider/provider.dart';
import 'package:optitop_mobile/services/auth_service.dart';
import 'package:optitop_mobile/services/notification_service.dart';
import 'package:optitop_mobile/screens/accounts_screen.dart';

// ===== POINT D'ENTRÉE =====

/// Lance l'application avec la configuration des providers
void main() {
  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => AuthService()),
        ChangeNotifierProvider(create: (_) => NotificationService()),
      ],
      child: const OptiTopApp(),
    ),
  );
}

// ===== APPLICATION PRINCIPALE =====

/// Widget racine de l'application Optitop
class OptiTopApp extends StatelessWidget {
  const OptiTopApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Optitop Mobile',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.blue),
        useMaterial3: true,
      ),
      initialRoute: '/login',
      routes: {
        '/login': (context) => const LoginScreen(),
        '/accounts': (context) => const AccountsScreen(),
      },
      debugShowCheckedModeBanner: false,
    );
  }
}
