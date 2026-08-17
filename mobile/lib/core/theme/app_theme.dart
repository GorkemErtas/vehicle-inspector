import 'package:flutter/material.dart';

class AppTheme {
  AppTheme._();

  static const Color primaryColor =
  Color(0xFF245B8F);

  static const Color backgroundColor =
  Color(0xFFF6F8FB);

  static const Color surfaceColor =
  Color(0xFFFFFFFF);

  static const Color textPrimary =
  Color(0xFF172033);

  static const Color textSecondary =
  Color(0xFF667085);

  static ThemeData get lightTheme {
    final colorScheme = ColorScheme.fromSeed(
      seedColor: primaryColor,
      brightness: Brightness.light,
      surface: surfaceColor,
    ).copyWith(
      primary: primaryColor,
      surface: surfaceColor,
      onSurface: textPrimary,
      outline: const Color(0xFFD0D5DD),
      outlineVariant: const Color(0xFFE4E7EC),
    );

    final borderRadius =
    BorderRadius.circular(16);

    return ThemeData(
      useMaterial3: true,
      colorScheme: colorScheme,
      scaffoldBackgroundColor: backgroundColor,

      fontFamily: 'Roboto',

      textTheme: const TextTheme(
        headlineLarge: TextStyle(
          color: textPrimary,
          fontWeight: FontWeight.w800,
          letterSpacing: -0.8,
        ),
        headlineMedium: TextStyle(
          color: textPrimary,
          fontWeight: FontWeight.w800,
          letterSpacing: -0.6,
        ),
        headlineSmall: TextStyle(
          color: textPrimary,
          fontWeight: FontWeight.w800,
          letterSpacing: -0.4,
        ),
        titleLarge: TextStyle(
          color: textPrimary,
          fontWeight: FontWeight.w700,
        ),
        titleMedium: TextStyle(
          color: textPrimary,
          fontWeight: FontWeight.w700,
        ),
        bodyLarge: TextStyle(
          color: textPrimary,
          height: 1.4,
        ),
        bodyMedium: TextStyle(
          color: textSecondary,
          height: 1.4,
        ),
      ),

      appBarTheme: const AppBarTheme(
        centerTitle: false,
        elevation: 0,
        scrolledUnderElevation: 0,
        backgroundColor: Colors.transparent,
        foregroundColor: textPrimary,
        surfaceTintColor: Colors.transparent,
        titleTextStyle: TextStyle(
          color: textPrimary,
          fontSize: 20,
          fontWeight: FontWeight.w800,
          letterSpacing: -0.3,
        ),
      ),

      inputDecorationTheme: InputDecorationTheme(
        filled: true,
        fillColor: surfaceColor,
        contentPadding: const EdgeInsets.symmetric(
          horizontal: 16,
          vertical: 17,
        ),
        hintStyle: const TextStyle(
          color: Color(0xFF98A2B3),
        ),
        prefixIconColor: textSecondary,
        suffixIconColor: textSecondary,
        border: OutlineInputBorder(
          borderRadius: borderRadius,
          borderSide: const BorderSide(
            color: Color(0xFFD0D5DD),
          ),
        ),
        enabledBorder: OutlineInputBorder(
          borderRadius: borderRadius,
          borderSide: const BorderSide(
            color: Color(0xFFE0E4EA),
          ),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: borderRadius,
          borderSide: const BorderSide(
            color: primaryColor,
            width: 1.6,
          ),
        ),
        errorBorder: OutlineInputBorder(
          borderRadius: borderRadius,
          borderSide: const BorderSide(
            color: Color(0xFFD92D20),
          ),
        ),
        focusedErrorBorder: OutlineInputBorder(
          borderRadius: borderRadius,
          borderSide: const BorderSide(
            color: Color(0xFFD92D20),
            width: 1.6,
          ),
        ),
      ),

      filledButtonTheme: FilledButtonThemeData(
        style: FilledButton.styleFrom(
          backgroundColor: primaryColor,
          foregroundColor: Colors.white,
          minimumSize: const Size.fromHeight(52),
          padding: const EdgeInsets.symmetric(
            horizontal: 22,
            vertical: 15,
          ),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(15),
          ),
          textStyle: const TextStyle(
            fontSize: 15,
            fontWeight: FontWeight.w700,
          ),
        ),
      ),

      outlinedButtonTheme: OutlinedButtonThemeData(
        style: OutlinedButton.styleFrom(
          foregroundColor: primaryColor,
          minimumSize: const Size.fromHeight(50),
          padding: const EdgeInsets.symmetric(
            horizontal: 20,
            vertical: 14,
          ),
          side: const BorderSide(
            color: Color(0xFFD0D5DD),
          ),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(15),
          ),
          textStyle: const TextStyle(
            fontWeight: FontWeight.w700,
          ),
        ),
      ),

      textButtonTheme: TextButtonThemeData(
        style: TextButton.styleFrom(
          foregroundColor: primaryColor,
          textStyle: const TextStyle(
            fontWeight: FontWeight.w700,
          ),
        ),
      ),

      cardTheme: CardThemeData(
        elevation: 0,
        color: surfaceColor,
        surfaceTintColor: Colors.transparent,
        margin: EdgeInsets.zero,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(20),
          side: const BorderSide(
            color: Color(0xFFE4E7EC),
          ),
        ),
      ),

      navigationBarTheme: NavigationBarThemeData(
        height: 72,
        elevation: 0,
        backgroundColor: surfaceColor,
        indicatorColor:
        colorScheme.primaryContainer,
        labelTextStyle:
        WidgetStateProperty.resolveWith(
              (states) => TextStyle(
            fontSize: 12,
            fontWeight: states.contains(
              WidgetState.selected,
            )
                ? FontWeight.w700
                : FontWeight.w500,
          ),
        ),
      ),

      navigationRailTheme:
      NavigationRailThemeData(
        backgroundColor: surfaceColor,
        indicatorColor:
        colorScheme.primaryContainer,
        selectedIconTheme: const IconThemeData(
          color: primaryColor,
          size: 25,
        ),
        unselectedIconTheme:
        const IconThemeData(
          color: textSecondary,
          size: 24,
        ),
        selectedLabelTextStyle:
        const TextStyle(
          color: primaryColor,
          fontWeight: FontWeight.w700,
        ),
        unselectedLabelTextStyle:
        const TextStyle(
          color: textSecondary,
          fontWeight: FontWeight.w500,
        ),
      ),

      floatingActionButtonTheme:
      const FloatingActionButtonThemeData(
        backgroundColor: primaryColor,
        foregroundColor: Colors.white,
        elevation: 3,
        focusElevation: 4,
        hoverElevation: 5,
      ),

      chipTheme: ChipThemeData(
        backgroundColor:
        const Color(0xFFF2F4F7),
        selectedColor:
        colorScheme.primaryContainer,
        side: BorderSide.none,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(12),
        ),
        labelStyle: const TextStyle(
          fontWeight: FontWeight.w600,
          color: textPrimary,
        ),
        padding: const EdgeInsets.symmetric(
          horizontal: 8,
          vertical: 4,
        ),
      ),

      snackBarTheme: SnackBarThemeData(
        behavior: SnackBarBehavior.floating,
        backgroundColor:
        const Color(0xFF1D2939),
        contentTextStyle:
        const TextStyle(
          color: Colors.white,
          fontWeight: FontWeight.w500,
        ),
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(14),
        ),
      ),

      dividerTheme: const DividerThemeData(
        color: Color(0xFFE4E7EC),
        thickness: 1,
        space: 1,
      ),
    );
  }
}