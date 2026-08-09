import 'package:flutter/material.dart';

import '../../../core/storage/token_storage.dart';
import '../services/auth_service.dart';
import 'login_screen.dart';
import '../../home/screens/main_shell.dart';

class SplashScreen extends StatefulWidget {
  const SplashScreen({super.key});

  @override
  State<SplashScreen> createState() =>
      _SplashScreenState();
}

class _SplashScreenState extends State<SplashScreen> {
  final AuthService _authService =
  const AuthService();

  @override
  void initState() {
    super.initState();
    _restoreSession();
  }

  Future<void> _restoreSession() async {
    final hasToken =
    await TokenStorage.hasAccessToken();

    if (!hasToken) {
      _goToLogin();
      return;
    }

    try {
      final user =
      await _authService.getCurrentUser();

      if (!mounted) {
        return;
      }

      Navigator.of(context).pushReplacement(
        MaterialPageRoute<void>(
          builder: (_) => MainShell(
            fullName: user.fullName,
            email: user.email,
            role: user.role,
          ),
        ),
      );
    } catch (_) {
      await TokenStorage.clearAll();

      if (!mounted) {
        return;
      }

      _goToLogin();
    }
  }

  void _goToLogin() {
    if (!mounted) {
      return;
    }

    Navigator.of(context).pushReplacement(
      MaterialPageRoute<void>(
        builder: (_) => const LoginScreen(),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final colorScheme =
        Theme.of(context).colorScheme;

    final textTheme =
        Theme.of(context).textTheme;

    return Scaffold(
      body: SafeArea(
        child: Center(
          child: Padding(
            padding:
            const EdgeInsets.symmetric(
              horizontal: 32,
            ),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Container(
                  width: 96,
                  height: 96,
                  decoration: BoxDecoration(
                    color: colorScheme.primary,
                    borderRadius:
                    BorderRadius.circular(26),
                  ),
                  child: Icon(
                    Icons.car_crash_outlined,
                    color: colorScheme.onPrimary,
                    size: 44,
                  ),
                ),
                const SizedBox(height: 28),
                Text(
                  'Vehicle Inspector',
                  textAlign: TextAlign.center,
                  style:
                  textTheme.headlineMedium?.copyWith(
                    fontWeight: FontWeight.w800,
                    letterSpacing: -0.6,
                  ),
                ),
                const SizedBox(height: 10),
                Text(
                  'Yapay zekâ destekli araç hasar analizi',
                  textAlign: TextAlign.center,
                  style:
                  textTheme.bodyLarge?.copyWith(
                    color: colorScheme
                        .onSurfaceVariant,
                  ),
                ),
                const SizedBox(height: 28),
                const CircularProgressIndicator(),
              ],
            ),
          ),
        ),
      ),
    );
  }
}