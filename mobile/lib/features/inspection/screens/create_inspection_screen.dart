import 'package:flutter/material.dart';

import '../../../core/network/api_exception.dart';
import '../../../core/widgets/app_card.dart';
import '../../../core/widgets/primary_button.dart';
import '../../vehicle/models/vehicle.dart';
import '../../vehicle/services/vehicle_service.dart';
import '../models/damage_inspection.dart';
import '../services/inspection_service.dart';
import 'upload_damage_image_screen.dart';

class CreateInspectionScreen extends StatefulWidget {
  const CreateInspectionScreen({super.key});

  @override
  State<CreateInspectionScreen> createState() =>
      _CreateInspectionScreenState();
}

class _CreateInspectionScreenState
    extends State<CreateInspectionScreen> {
  final VehicleService _vehicleService =
  const VehicleService();

  final InspectionService _inspectionService =
  const InspectionService();

  late Future<List<Vehicle>> _vehiclesFuture;

  Vehicle? _selectedVehicle;
  String? _selectedCity;

  bool _isCreating = false;

  static const List<String> _cities = [
    'Adana',
    'Ankara',
    'Antalya',
    'Bursa',
    'İstanbul',
    'İzmir',
    'Kocaeli',
    'Konya',
    'Mersin',
  ];

  @override
  void initState() {
    super.initState();
    _vehiclesFuture = _vehicleService.getVehicles();
  }

  Future<void> _createInspection() async {
    final vehicle = _selectedVehicle;
    final city = _selectedCity;

    if (vehicle == null) {
      _showMessage(
        'Lütfen analiz yapılacak aracı seçin.',
      );
      return;
    }

    if (city == null || city.isEmpty) {
      _showMessage(
        'Lütfen şehir seçin.',
      );
      return;
    }

    setState(() {
      _isCreating = true;
    });

    try {
      final inspection =
      await _inspectionService.createInspection(
        vehicleId: vehicle.id,
        city: city,
      );

      if (!mounted) {
        return;
      }

      final uploadedInspection =
      await Navigator.of(context).push<DamageInspection>(
        MaterialPageRoute<DamageInspection>(
          builder: (_) => UploadDamageImageScreen(
            inspection: inspection,
          ),
        ),
      );

      if (!mounted ||
          uploadedInspection == null) {
        return;
      }

      Navigator.of(context).pop<DamageInspection>(
        uploadedInspection,
      );
    } catch (exception) {
      if (!mounted) {
        return;
      }

      final message = exception is ApiException
          ? exception.message
          : 'Hasar incelemesi oluşturulamadı.';

      _showMessage(message);
    } finally {
      if (mounted) {
        setState(() {
          _isCreating = false;
        });
      }
    }
  }

  void _showMessage(
      String message,
      ) {
    ScaffoldMessenger.of(context)
      ..hideCurrentSnackBar()
      ..showSnackBar(
        SnackBar(
          content: Text(message),
          behavior: SnackBarBehavior.floating,
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
      appBar: AppBar(
        title: const Text(
          'Yeni Hasar Analizi',
        ),
      ),
      body: SafeArea(
          child: Center(
            child: ConstrainedBox(
              constraints: const BoxConstraints(
                maxWidth: 1100,
              ),
        child: FutureBuilder<List<Vehicle>>(
          future: _vehiclesFuture,
          builder: (context, snapshot) {
            if (snapshot.connectionState ==
                ConnectionState.waiting) {
              return const Center(
                child: CircularProgressIndicator(),
              );
            }

            if (snapshot.hasError) {
              final error = snapshot.error;

              final message =
              error is ApiException
                  ? error.message
                  : 'Araçlar yüklenemedi.';

              return Center(
                child: Padding(
                  padding:
                  const EdgeInsets.all(32),
                  child: Column(
                    mainAxisSize:
                    MainAxisSize.min,
                    children: [
                      Icon(
                        Icons.error_outline_rounded,
                        size: 52,
                        color: colorScheme.error,
                      ),
                      const SizedBox(height: 18),
                      Text(
                        message,
                        textAlign:
                        TextAlign.center,
                      ),
                      const SizedBox(height: 20),
                      FilledButton.tonalIcon(
                        onPressed: () {
                          setState(() {
                            _vehiclesFuture =
                                _vehicleService
                                    .getVehicles();
                          });
                        },
                        icon: const Icon(
                          Icons.refresh_rounded,
                        ),
                        label: const Text(
                          'Tekrar Dene',
                        ),
                      ),
                    ],
                  ),
                ),
              );
            }

            final vehicles =
                snapshot.data ??
                    const <Vehicle>[];

            if (vehicles.isEmpty) {
              return Center(
                child: Padding(
                  padding:
                  const EdgeInsets.all(32),
                  child: Column(
                    mainAxisSize:
                    MainAxisSize.min,
                    children: [
                      Icon(
                        Icons
                            .directions_car_outlined,
                        size: 54,
                        color:
                        colorScheme.primary,
                      ),
                      const SizedBox(height: 18),
                      Text(
                        'Kayıtlı araç bulunamadı',
                        style: textTheme
                            .titleLarge
                            ?.copyWith(
                          fontWeight:
                          FontWeight.w800,
                        ),
                      ),
                      const SizedBox(height: 8),
                      Text(
                        'Hasar analizi başlatmak için önce bir araç eklemelisiniz.',
                        textAlign:
                        TextAlign.center,
                        style: textTheme
                            .bodyMedium
                            ?.copyWith(
                          color: colorScheme
                              .onSurfaceVariant,
                        ),
                      ),
                    ],
                  ),
                ),
              );
            }

            return ListView(
              padding:
              const EdgeInsets.fromLTRB(
                20,
                12,
                20,
                28,
              ),
              children: [
                Text(
                  'Analiz bilgileri',
                  style: textTheme
                      .headlineSmall
                      ?.copyWith(
                    fontWeight:
                    FontWeight.w800,
                  ),
                ),
                const SizedBox(height: 8),
                Text(
                  'Hasar analizi yapılacak aracı ve fiyat tahmini için kullanılacak şehri seçin.',
                  style:
                  textTheme.bodyLarge?.copyWith(
                    color: colorScheme
                        .onSurfaceVariant,
                    height: 1.4,
                  ),
                ),
                const SizedBox(height: 28),

                Text(
                  'Araç',
                  style: textTheme
                      .labelLarge
                      ?.copyWith(
                    fontWeight:
                    FontWeight.w700,
                  ),
                ),
                const SizedBox(height: 10),

                ...vehicles.map(
                      (vehicle) => Padding(
                    padding:
                    const EdgeInsets.only(
                      bottom: 10,
                    ),
                    child: AppCard(
                      onTap: () {
                        setState(() {
                          _selectedVehicle =
                              vehicle;
                        });
                      },
                      child: Row(
                        children: [
                          Radio<int>(
                            value: vehicle.id,
                            groupValue:
                            _selectedVehicle
                                ?.id,
                            onChanged: (_) {
                              setState(() {
                                _selectedVehicle =
                                    vehicle;
                              });
                            },
                          ),
                          const SizedBox(
                            width: 8,
                          ),
                          Expanded(
                            child: Column(
                              crossAxisAlignment:
                              CrossAxisAlignment
                                  .start,
                              children: [
                                Text(
                                  vehicle
                                      .displayName,
                                  style:
                                  const TextStyle(
                                    fontSize: 16,
                                    fontWeight:
                                    FontWeight
                                        .w800,
                                  ),
                                ),
                                const SizedBox(
                                  height: 5,
                                ),
                                Text(
                                  '${vehicle.plate} • ${vehicle.modelYear}',
                                  style: TextStyle(
                                    color: colorScheme
                                        .onSurfaceVariant,
                                  ),
                                ),
                              ],
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                ),

                const SizedBox(height: 24),

                Text(
                  'Şehir',
                  style: textTheme
                      .labelLarge
                      ?.copyWith(
                    fontWeight:
                    FontWeight.w700,
                  ),
                ),
                const SizedBox(height: 8),

                DropdownButtonFormField<String>(
                  value: _selectedCity,
                  decoration:
                  const InputDecoration(
                    hintText: 'Şehir seçin',
                    prefixIcon: Icon(
                      Icons.location_city_outlined,
                    ),
                  ),
                  items: _cities
                      .map(
                        (city) =>
                        DropdownMenuItem<String>(
                          value: city,
                          child: Text(city),
                        ),
                  )
                      .toList(),
                  onChanged: (value) {
                    setState(() {
                      _selectedCity = value;
                    });
                  },
                ),

                const SizedBox(height: 30),

                PrimaryButton(
                  label: 'Devam Et',
                  icon:
                  Icons.arrow_forward_rounded,
                  isLoading: _isCreating,
                  onPressed:
                  _createInspection,
                ),
              ],
            );
          },
        ),
      ),
          ),
      ),
    );
  }
}