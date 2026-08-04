import 'package:flutter/material.dart';

import 'core/theme/app_theme.dart';

void main() {
  runApp(const VehicleInspectorApp());
}

class VehicleInspectorApp extends StatelessWidget {
  const VehicleInspectorApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Vehicle Inspector',
      debugShowCheckedModeBanner: false,
      theme: AppTheme.lightTheme,
      home: const DesignPreviewScreen(),
    );
  }
}

class DesignPreviewScreen extends StatelessWidget {
  const DesignPreviewScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Vehicle Inspector'),
      ),
      body: SafeArea(
        child: ListView(
          padding: const EdgeInsets.all(20),
          children: [
            Text(
              'Aracınızı kontrol edin',
              style: Theme.of(context)
                  .textTheme
                  .headlineMedium
                  ?.copyWith(
                fontWeight: FontWeight.w800,
                letterSpacing: -0.5,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              'Hasarlı bölgenin fotoğrafını çekin ve analiz sonucunu görüntüleyin.',
              style: Theme.of(context)
                  .textTheme
                  .bodyLarge
                  ?.copyWith(
                color: colorScheme.onSurfaceVariant,
                height: 1.4,
              ),
            ),
            const SizedBox(height: 28),

            Card(
              child: Padding(
                padding: const EdgeInsets.all(20),
                child: Column(
                  crossAxisAlignment:
                  CrossAxisAlignment.start,
                  children: [
                    Container(
                      width: 52,
                      height: 52,
                      decoration: BoxDecoration(
                        color:
                        colorScheme.primaryContainer,
                        borderRadius:
                        BorderRadius.circular(16),
                      ),
                      child: Icon(
                        Icons.document_scanner_outlined,
                        color: colorScheme.primary,
                        size: 28,
                      ),
                    ),
                    const SizedBox(height: 18),
                    Text(
                      'Yeni hasar analizi',
                      style: Theme.of(context)
                          .textTheme
                          .titleLarge
                          ?.copyWith(
                        fontWeight: FontWeight.w800,
                      ),
                    ),
                    const SizedBox(height: 8),
                    Text(
                      'Kamerayı açın, fotoğrafı yükleyin ve kısa hasar raporunu alın.',
                      style: Theme.of(context)
                          .textTheme
                          .bodyMedium
                          ?.copyWith(
                        color: colorScheme
                            .onSurfaceVariant,
                        height: 1.4,
                      ),
                    ),
                    const SizedBox(height: 20),
                    ElevatedButton.icon(
                      onPressed: () {},
                      icon: const Icon(
                        Icons.camera_alt_outlined,
                      ),
                      label: const Text(
                        'Fotoğraf Çek',
                      ),
                    ),
                  ],
                ),
              ),
            ),

            const SizedBox(height: 16),

            Row(
              children: [
                Expanded(
                  child: _InfoCard(
                    icon: Icons.directions_car_outlined,
                    title: 'Araçlarım',
                    value: '1',
                  ),
                ),
                const SizedBox(width: 14),
                Expanded(
                  child: _InfoCard(
                    icon: Icons.history_outlined,
                    title: 'Analizler',
                    value: '8',
                  ),
                ),
              ],
            ),

            const SizedBox(height: 24),

            Text(
              'Son analiz',
              style: Theme.of(context)
                  .textTheme
                  .titleLarge
                  ?.copyWith(
                fontWeight: FontWeight.w800,
              ),
            ),
            const SizedBox(height: 12),

            Card(
              child: Padding(
                padding: const EdgeInsets.all(18),
                child: Row(
                  children: [
                    Container(
                      width: 54,
                      height: 54,
                      decoration: BoxDecoration(
                        color: const Color(0xFFFFF2D8),
                        borderRadius:
                        BorderRadius.circular(16),
                      ),
                      child: const Icon(
                        Icons.warning_amber_rounded,
                        color: Color(0xFFB36A00),
                      ),
                    ),
                    const SizedBox(width: 15),
                    const Expanded(
                      child: Column(
                        crossAxisAlignment:
                        CrossAxisAlignment.start,
                        children: [
                          Text(
                            'Orta Seviyeli Hasar',
                            style: TextStyle(
                              fontSize: 16,
                              fontWeight: FontWeight.w800,
                            ),
                          ),
                          SizedBox(height: 5),
                          Text(
                            'Ön kapıda çizik ve göçük',
                            style: TextStyle(
                              color: Color(0xFF667085),
                            ),
                          ),
                        ],
                      ),
                    ),
                    Icon(
                      Icons.chevron_right_rounded,
                      color:
                      colorScheme.onSurfaceVariant,
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
      bottomNavigationBar: NavigationBar(
        selectedIndex: 0,
        destinations: const [
          NavigationDestination(
            icon: Icon(Icons.home_outlined),
            selectedIcon: Icon(Icons.home_rounded),
            label: 'Ana Sayfa',
          ),
          NavigationDestination(
            icon: Icon(
              Icons.directions_car_outlined,
            ),
            selectedIcon: Icon(
              Icons.directions_car_rounded,
            ),
            label: 'Araçlar',
          ),
          NavigationDestination(
            icon: Icon(Icons.history_outlined),
            selectedIcon: Icon(Icons.history_rounded),
            label: 'Analizler',
          ),
          NavigationDestination(
            icon: Icon(Icons.person_outline_rounded),
            selectedIcon: Icon(Icons.person_rounded),
            label: 'Profil',
          ),
        ],
      ),
    );
  }
}

class _InfoCard extends StatelessWidget {
  const _InfoCard({
    required this.icon,
    required this.title,
    required this.value,
  });

  final IconData icon;
  final String title;
  final String value;

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;

    return Card(
      child: Padding(
        padding: const EdgeInsets.all(17),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Icon(
              icon,
              color: colorScheme.primary,
            ),
            const SizedBox(height: 18),
            Text(
              value,
              style: Theme.of(context)
                  .textTheme
                  .headlineSmall
                  ?.copyWith(
                fontWeight: FontWeight.w800,
              ),
            ),
            const SizedBox(height: 3),
            Text(
              title,
              style: TextStyle(
                color: colorScheme.onSurfaceVariant,
              ),
            ),
          ],
        ),
      ),
    );
  }
}