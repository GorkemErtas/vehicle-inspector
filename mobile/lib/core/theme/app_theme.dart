import 'package:flutter/material.dart';

class AppTheme {
  AppTheme._();

  static const Color _seedColor = Color(0xFF174A7E);

  static ThemeData get lightTheme {
    final colorScheme = ColorScheme.fromSeed(
      seedColor: _seedColor,
      brightness: Brightness.light,
      surface: const Color(0xFFF7F9FC),
    );

    return ThemeData(
      useMaterial3: true,
      colorScheme: colorScheme,
      scaffoldBackgroundColor: const Color(0xFFF4F6F9),

      fontFamily: 'Roboto',

      appBarTheme: AppBarTheme(
        elevation: 0,
        centerTitle: false,
        backgroundColor: colorScheme.surface,
        foregroundColor: colorScheme.onSurface,
        surfaceTintColor: Colors.transparent,
        titleTextStyle: TextStyle(
          color: colorScheme.onSurface,
          fontSize: 20,
          fontWeight: FontWeight.w700,
        ),
      ),

      cardTheme: CardThemeData(
        elevation: 0,
        color: Colors.white,
        margin: EdgeInsets.zero,
        surfaceTintColor: Colors.transparent,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(20),
          side: BorderSide(
            color: colorScheme.outlineVariant.withValues(
              alpha: 0.55,
            ),
          ),
        ),
      ),

      inputDecorationTheme: InputDecorationTheme(
        filled: true,
        fillColor: Colors.white,
        contentPadding: const EdgeInsets.symmetric(
          horizontal: 18,
          vertical: 17,
        ),
        hintStyle: TextStyle(
          color: colorScheme.onSurfaceVariant,
          fontSize: 15,
        ),
        labelStyle: TextStyle(
          color: colorScheme.onSurfaceVariant,
        ),
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(16),
          borderSide: BorderSide(
            color: colorScheme.outlineVariant,
          ),
        ),
        enabledBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(16),
          borderSide: BorderSide(
            color: colorScheme.outlineVariant,
          ),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(16),
          borderSide: BorderSide(
            color: colorScheme.primary,
            width: 1.6,
          ),
        ),
        errorBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(16),
          borderSide: BorderSide(
            color: colorScheme.error,
          ),
        ),
      ),

      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          elevation: 0,
          minimumSize: const Size.fromHeight(54),
          backgroundColor: colorScheme.primary,
          foregroundColor: colorScheme.onPrimary,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(16),
          ),
          textStyle: const TextStyle(
            fontSize: 16,
            fontWeight: FontWeight.w700,
          ),
        ),
      ),

      outlinedButtonTheme: OutlinedButtonThemeData(
        style: OutlinedButton.styleFrom(
          minimumSize: const Size.fromHeight(54),
          foregroundColor: colorScheme.primary,
          side: BorderSide(
            color: colorScheme.outlineVariant,
          ),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(16),
          ),
          textStyle: const TextStyle(
            fontSize: 16,
            fontWeight: FontWeight.w700,
          ),
        ),
      ),

      navigationBarTheme: NavigationBarThemeData(
        elevation: 0,
        height: 72,
        backgroundColor: Colors.white,
        indicatorColor: colorScheme.primaryContainer,
        labelTextStyle: WidgetStateProperty.resolveWith(
          (states) {
            final isSelected =
                states.contains(WidgetState.selected);

            return TextStyle(
              fontSize: 12,
              fontWeight: isSelected
                  ? FontWeight.w700
                  : FontWeight.w500,
              color: isSelected
                  ? colorScheme.primary
                  : colorScheme.onSurfaceVariant,
            );
          },
        ),
      ),

      dividerTheme: DividerThemeData(
        color: colorScheme.outlineVariant,
        thickness: 1,
        space: 1,
      ),
    );
  }
}