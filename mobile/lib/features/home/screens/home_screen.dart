import 'package:flutter/material.dart';

import '../../auth/services/auth_service.dart';

class HomeScreen extends StatelessWidget {
  const HomeScreen({
    super.key,
    required this.fullName,
  });

  final String fullName;

  Future<void> _logout(BuildContext context) async {
    await const AuthService().logout();

    if (!context.mounted) {
      return;
    }

    Navigator.of(context).popUntil(
          (route) => route.isFirst,
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Vehicle Inspector'),
        actions: [
          IconButton(
            tooltip: 'Çıkış Yap',
            onPressed: () => _logout(context),
            icon: const Icon(
              Icons.logout_rounded,
            ),
          ),
        ],
      ),
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(24),
          child: Column(
            crossAxisAlignment:
            CrossAxisAlignment.start,
            children: [
              Text(
                'Hoş geldiniz,',
                style: Theme.of(context)
                    .textTheme
                    .bodyLarge
                    ?.copyWith(
                  color: Theme.of(context)
                      .colorScheme
                      .onSurfaceVariant,
                ),
              ),
              const SizedBox(height: 4),
              Text(
                fullName,
                style: Theme.of(context)
                    .textTheme
                    .headlineMedium
                    ?.copyWith(
                  fontWeight: FontWeight.w800,
                ),
              ),
              const SizedBox(height: 32),
              const Card(
                child: Padding(
                  padding: EdgeInsets.all(20),
                  child: Row(
                    children: [
                      Icon(
                        Icons.check_circle_outline_rounded,
                        size: 32,
                      ),
                      SizedBox(width: 16),
                      Expanded(
                        child: Text(
                          'Spring Boot ile giriş başarıyla tamamlandı.',
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}