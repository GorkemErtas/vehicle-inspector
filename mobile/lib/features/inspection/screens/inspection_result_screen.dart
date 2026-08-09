import 'package:flutter/material.dart';

import '../../../core/network/api_exception.dart';
import '../../../core/widgets/app_card.dart';
import '../models/damage_inspection.dart';
import '../services/inspection_service.dart';

class InspectionResultScreen extends StatefulWidget {
  const InspectionResultScreen({
    super.key,
    required this.inspection,
  });

  final DamageInspection inspection;

  @override
  State<InspectionResultScreen> createState() =>
      _InspectionResultScreenState();
}

class _InspectionResultScreenState
    extends State<InspectionResultScreen> {
  final InspectionService _inspectionService =
  const InspectionService();

  late DamageInspection _inspection;
  bool _isRegeneratingReport = false;

  @override
  void initState() {
    super.initState();
    _inspection = widget.inspection;
  }

  Future<void> _regenerateReport() async {
    setState(() {
      _isRegeneratingReport = true;
    });

    try {
      final updated =
      await _inspectionService.regenerateReport(
        _inspection.id,
      );

      if (!mounted) {
        return;
      }

      setState(() {
        _inspection = updated;
      });
    } catch (exception) {
      if (!mounted) {
        return;
      }

      final message = exception is ApiException
          ? exception.message
          : 'AI raporu yeniden oluşturulamadı.';

      ScaffoldMessenger.of(context)
        ..hideCurrentSnackBar()
        ..showSnackBar(
          SnackBar(
            content: Text(message),
            behavior: SnackBarBehavior.floating,
          ),
        );
    } finally {
      if (mounted) {
        setState(() {
          _isRegeneratingReport = false;
        });
      }
    }
  }

  String _severityLabel(
      String? severity,
      ) {
    return switch (severity) {
      'MINOR' => 'Hafif',
      'MODERATE' => 'Orta',
      'SEVERE' => 'Ağır',
      _ => 'Belirsiz',
    };
  }

  String _damageTypeLabel(
      String type,
      ) {
    return switch (type) {
      'BROKEN_PART' => 'Kırık Parça',
      'DENT' => 'Göçük',
      'SCRATCH' => 'Çizik',
      _ => type,
    };
  }

  String _vehiclePartLabel(
      String part,
      ) {
    return switch (part) {
      'HEADLIGHT' => 'Far',
      'GRILLE' => 'Ön Izgara',
      'FRONT_BUMPER' => 'Ön Tampon',
      'REAR_BUMPER' => 'Arka Tampon',
      'HOOD' => 'Kaput',
      'FENDER' => 'Çamurluk',
      'FRONT_DOOR' => 'Ön Kapı',
      'REAR_DOOR' => 'Arka Kapı',
      'FRONT_WHEEL' => 'Ön Tekerlek',
      'REAR_WHEEL' => 'Arka Tekerlek',
      _ => part,
    };
  }

  String _repairActionLabel(
      String action,
      ) {
    return switch (action) {
      'PART_REPLACEMENT' => 'Parça Değişimi',
      'DENT_REPAIR' => 'Göçük Düzeltme',
      'PAINT_REPAIR' => 'Boya Onarımı',
      'SCRATCH_REPAIR' => 'Çizik Onarımı',
      _ => action,
    };
  }

  String _formatPrice(
      double value,
      ) {
    final rounded = value.round();
    final source = rounded.toString();
    final result = StringBuffer();

    for (int i = 0; i < source.length; i++) {
      final remaining = source.length - i;

      result.write(source[i]);

      if (remaining > 1 &&
          remaining % 3 == 1) {
        result.write('.');
      }
    }

    return result.toString();
  }

  @override
  Widget build(BuildContext context) {
    final colorScheme =
        Theme.of(context).colorScheme;

    final textTheme =
        Theme.of(context).textTheme;

    final report = _inspection.report;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Analiz Sonucu'),
        leading: IconButton(
          icon: const Icon(
            Icons.close_rounded,
          ),
          onPressed: () {
            Navigator.of(context).popUntil(
                  (route) => route.isFirst,
            );
          },
        ),
      ),
      body: SafeArea(
        child: ListView(
          padding: const EdgeInsets.fromLTRB(
            20,
            12,
            20,
            32,
          ),
          children: [
            AppCard(
              child: Row(
                children: [
                  Container(
                    width: 58,
                    height: 58,
                    decoration: BoxDecoration(
                      color:
                      colorScheme.primaryContainer,
                      borderRadius:
                      BorderRadius.circular(18),
                    ),
                    child: Icon(
                      Icons
                          .directions_car_filled_outlined,
                      color: colorScheme.primary,
                      size: 30,
                    ),
                  ),
                  const SizedBox(width: 16),
                  Expanded(
                    child: Column(
                      crossAxisAlignment:
                      CrossAxisAlignment.start,
                      children: [
                        Text(
                          _inspection.vehiclePlate,
                          style:
                          textTheme.titleMedium
                              ?.copyWith(
                            fontWeight:
                            FontWeight.w800,
                          ),
                        ),
                        const SizedBox(height: 5),
                        Text(
                          _inspection.locationCity,
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

            const SizedBox(height: 20),

            AppCard(
              child: Column(
                crossAxisAlignment:
                CrossAxisAlignment.start,
                children: [
                  Text(
                    'Hasar Seviyesi',
                    style:
                    textTheme.labelLarge?.copyWith(
                      color: colorScheme
                          .onSurfaceVariant,
                      fontWeight: FontWeight.w700,
                    ),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    '${_severityLabel(_inspection.damageSeverity)} Seviye Hasar',
                    style:
                    textTheme.headlineSmall?.copyWith(
                      fontWeight: FontWeight.w800,
                    ),
                  ),
                  if (_inspection.confidenceScore !=
                      null) ...[
                    const SizedBox(height: 8),
                    Text(
                      'Model güveni: %${(_inspection.confidenceScore! * 100).toStringAsFixed(1)}',
                      style: TextStyle(
                        color: colorScheme
                            .onSurfaceVariant,
                      ),
                    ),
                  ],
                ],
              ),
            ),

            if (_inspection.damageTypes.isNotEmpty) ...[
              const SizedBox(height: 20),
              _SectionCard(
                title: 'Tespit Edilen Hasarlar',
                child: Wrap(
                  spacing: 8,
                  runSpacing: 8,
                  children: _inspection.damageTypes
                      .map(
                        (type) => Chip(
                      label: Text(
                        _damageTypeLabel(type),
                      ),
                    ),
                  )
                      .toList(),
                ),
              ),
            ],

            if (_inspection.affectedParts
                .isNotEmpty) ...[
              const SizedBox(height: 20),
              _SectionCard(
                title: 'Etkilenen Parçalar',
                child: Wrap(
                  spacing: 8,
                  runSpacing: 8,
                  children: _inspection.affectedParts
                      .map(
                        (part) => Chip(
                      avatar: const Icon(
                        Icons
                            .build_circle_outlined,
                        size: 18,
                      ),
                      label: Text(
                        _vehiclePartLabel(part),
                      ),
                    ),
                  )
                      .toList(),
                ),
              ),
            ],

            if (_inspection.repairRecommendations
                .isNotEmpty) ...[
              const SizedBox(height: 20),
              _SectionCard(
                title: 'Onarım Önerileri',
                child: Column(
                  children: _inspection
                      .repairRecommendations
                      .map(
                        (recommendation) {
                      final parts = recommendation
                          .affectedParts
                          .map(_vehiclePartLabel)
                          .join(', ');

                      return Padding(
                        padding:
                        const EdgeInsets.only(
                          bottom: 14,
                        ),
                        child: Row(
                          crossAxisAlignment:
                          CrossAxisAlignment.start,
                          children: [
                            Icon(
                              Icons
                                  .handyman_outlined,
                              color:
                              colorScheme.primary,
                            ),
                            const SizedBox(width: 12),
                            Expanded(
                              child: Column(
                                crossAxisAlignment:
                                CrossAxisAlignment
                                    .start,
                                children: [
                                  Text(
                                    _repairActionLabel(
                                      recommendation
                                          .recommendedAction,
                                    ),
                                    style: const TextStyle(
                                      fontWeight:
                                      FontWeight.w800,
                                    ),
                                  ),
                                  if (parts.isNotEmpty) ...[
                                    const SizedBox(
                                      height: 4,
                                    ),
                                    Text(
                                      parts,
                                      style: TextStyle(
                                        color: colorScheme
                                            .onSurfaceVariant,
                                      ),
                                    ),
                                  ],
                                ],
                              ),
                            ),
                          ],
                        ),
                      );
                    },
                  ).toList(),
                ),
              ),
            ],

            const SizedBox(height: 20),

            if (report != null) ...[
              AppCard(
                child: Column(
                  crossAxisAlignment:
                  CrossAxisAlignment.start,
                  children: [
                    Row(
                      children: [
                        Icon(
                          Icons
                              .auto_awesome_rounded,
                          color: colorScheme.primary,
                        ),
                        const SizedBox(width: 8),
                        Expanded(
                          child: Text(
                            'AI Raporu',
                            style: textTheme
                                .titleLarge
                                ?.copyWith(
                              fontWeight:
                              FontWeight.w800,
                            ),
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 18),
                    Text(
                      report.title,
                      style: textTheme
                          .titleMedium
                          ?.copyWith(
                        fontWeight: FontWeight.w800,
                      ),
                    ),
                    const SizedBox(height: 10),
                    Text(
                      report.summary,
                      style: const TextStyle(
                        height: 1.5,
                      ),
                    ),
                    const SizedBox(height: 20),
                    Text(
                      'Hasar Açıklaması',
                      style: textTheme
                          .titleSmall
                          ?.copyWith(
                        fontWeight: FontWeight.w800,
                      ),
                    ),
                    const SizedBox(height: 7),
                    Text(
                      report.damageDescription,
                      style: const TextStyle(
                        height: 1.5,
                      ),
                    ),
                    const SizedBox(height: 20),
                    Text(
                      'Onarım Önerisi',
                      style: textTheme
                          .titleSmall
                          ?.copyWith(
                        fontWeight: FontWeight.w800,
                      ),
                    ),
                    const SizedBox(height: 7),
                    Text(
                      report.repairRecommendation,
                      style: const TextStyle(
                        height: 1.5,
                      ),
                    ),
                  ],
                ),
              ),

              const SizedBox(height: 20),

              AppCard(
                child: Column(
                  crossAxisAlignment:
                  CrossAxisAlignment.start,
                  children: [
                    Text(
                      'Tahmini Onarım Maliyeti',
                      style: textTheme
                          .titleMedium
                          ?.copyWith(
                        fontWeight: FontWeight.w800,
                      ),
                    ),
                    const SizedBox(height: 12),
                    Text(
                      '${_formatPrice(report.estimatedMinimumPrice)} - '
                          '${_formatPrice(report.estimatedMaximumPrice)} '
                          '${report.currency}',
                      style: textTheme
                          .headlineSmall
                          ?.copyWith(
                        color: colorScheme.primary,
                        fontWeight: FontWeight.w900,
                      ),
                    ),
                    const SizedBox(height: 14),
                    Text(
                      report.priceInformation,
                      style: const TextStyle(
                        height: 1.5,
                      ),
                    ),
                    const SizedBox(height: 14),
                    Text(
                      report.disclaimer,
                      style:
                      textTheme.bodySmall?.copyWith(
                        color: colorScheme
                            .onSurfaceVariant,
                        height: 1.45,
                      ),
                    ),
                  ],
                ),
              ),
            ] else ...[
              AppCard(
                child: Column(
                  children: [
                    Icon(
                      Icons
                          .description_outlined,
                      size: 44,
                      color: _inspection
                          .isReportFailed
                          ? colorScheme.error
                          : colorScheme.primary,
                    ),
                    const SizedBox(height: 14),
                    Text(
                      _inspection.reportMessage ??
                          'AI raporu henüz oluşturulmadı.',
                      textAlign: TextAlign.center,
                      style: textTheme
                          .bodyLarge
                          ?.copyWith(
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                    const SizedBox(height: 18),
                    FilledButton.tonalIcon(
                      onPressed:
                      _isRegeneratingReport
                          ? null
                          : _regenerateReport,
                      icon: _isRegeneratingReport
                          ? const SizedBox(
                        width: 18,
                        height: 18,
                        child:
                        CircularProgressIndicator(
                          strokeWidth: 2,
                        ),
                      )
                          : const Icon(
                        Icons.refresh_rounded,
                      ),
                      label: const Text(
                        'AI Raporunu Tekrar Oluştur',
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }
}

class _SectionCard extends StatelessWidget {
  const _SectionCard({
    required this.title,
    required this.child,
  });

  final String title;
  final Widget child;

  @override
  Widget build(BuildContext context) {
    return AppCard(
      child: Column(
        crossAxisAlignment:
        CrossAxisAlignment.start,
        children: [
          Text(
            title,
            style: Theme.of(context)
                .textTheme
                .titleMedium
                ?.copyWith(
              fontWeight: FontWeight.w800,
            ),
          ),
          const SizedBox(height: 14),
          child,
        ],
      ),
    );
  }
}