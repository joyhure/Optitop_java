/// Configuration du thème de l'application mobile Optitop
/// 
/// Définit les couleurs, styles et thèmes visuels :
/// - Couleurs personnalisées de l'identité visuelle
/// - Style de l'AppBar et de la navigation
/// - Thème des boutons et champs de saisie
/// - Configuration typographique
/// 
/// @author Joy Huré
/// @version 1.0
library;

import 'package:flutter/material.dart';

class AppTheme {
  
  // ===== COULEURS PERSONNALISÉES =====
  
  /// Couleur principale - Bleu Optitop
  static const primaryColor = Color(0xFF2196F3);
  
  /// Couleur secondaire - Bleu foncé
  static const secondaryColor = Color(0xFF1976D2);
  
  /// Couleur d'accent - Bleu clair
  static const accentColor = Color(0xFF64B5F6);

  // ===== THÈME PRINCIPAL =====
  
  /// Configuration du thème clair de l'application
  static final ThemeData lightTheme = ThemeData(
    primarySwatch: Colors.blue,
    scaffoldBackgroundColor: Colors.grey[50],
    
    // Style de la AppBar (navbar)
    appBarTheme: const AppBarTheme(
      backgroundColor: primaryColor,
      foregroundColor: Colors.white,
      elevation: 2,
      centerTitle: true,
      titleTextStyle: TextStyle(
        fontSize: 20,
        fontWeight: FontWeight.w600,
        letterSpacing: 0.5,
      ),
      iconTheme: IconThemeData(
        color: Colors.white,
        size: 24,
      ),
      actionsIconTheme: IconThemeData(
        color: Colors.white,
        size: 24,
      ),
    ),

    // Style des boutons élevés
    elevatedButtonTheme: ElevatedButtonThemeData(
      style: ElevatedButton.styleFrom(
        backgroundColor: primaryColor,
        foregroundColor: Colors.white,
        padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 12),
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(8),
        ),
        elevation: 2,
      ),
    ),

    // Style des champs de saisie
    inputDecorationTheme: InputDecorationTheme(
      border: OutlineInputBorder(
        borderRadius: BorderRadius.circular(8),
        borderSide: const BorderSide(color: primaryColor),
      ),
      focusedBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(8),
        borderSide: const BorderSide(color: secondaryColor, width: 2),
      ),
      filled: true,
      fillColor: Colors.grey[100],
      contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
    ),

    // Configuration typographique
    textTheme: const TextTheme(
      titleLarge: TextStyle(
        fontSize: 22,
        fontWeight: FontWeight.bold,
        color: primaryColor,
      ),
      titleMedium: TextStyle(
        fontSize: 18,
        fontWeight: FontWeight.w600,
        color: secondaryColor,
      ),
    ),
  );
}