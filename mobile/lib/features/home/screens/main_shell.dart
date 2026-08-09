import 'package:flutter/material.dart';

import '../../../core/widgets/coming_soon_screen.dart';
import 'home_screen.dart';
import '../../vehicle/screens/vehicle_list_screen.dart';
import '../../inspection/screens/inspection_history_screen.dart';

class MainShell extends StatefulWidget {
  const MainShell({
    super.key,
    required this.fullName,
  });

  final String fullName;

  @override
  State<MainShell> createState() => _MainShellState();
}

class _MainShellState extends State<MainShell> {
  int _selectedIndex = 0;

  late final List<Widget> _screens = [
    HomeScreen(
      fullName: widget.fullName,
    ),
    const VehicleListScreen(),
    const InspectionHistoryScreen(),
    const ComingSoonScreen(
      title: 'Profil',
      icon: Icons.person_outline_rounded,
    ),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: IndexedStack(
        index: _selectedIndex,
        children: _screens,
      ),
      bottomNavigationBar: NavigationBar(
        selectedIndex: _selectedIndex,
        onDestinationSelected: (index) {
          setState(() {
            _selectedIndex = index;
          });
        },
        destinations: const [
          NavigationDestination(
            icon: Icon(Icons.home_outlined),
            selectedIcon: Icon(Icons.home_rounded),
            label: 'Ana Sayfa',
          ),
          NavigationDestination(
            icon: Icon(Icons.directions_car_outlined),
            selectedIcon: Icon(Icons.directions_car_rounded),
            label: 'Araçlar',
          ),
          NavigationDestination(
            icon: Icon(Icons.description_outlined),
            selectedIcon: Icon(Icons.description_rounded),
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