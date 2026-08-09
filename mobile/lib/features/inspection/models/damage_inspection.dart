import 'damage_detection.dart';
import 'damage_repair_recommendation.dart';
import 'inspection_report.dart';

class DamageInspection {
  const DamageInspection({
    required this.id,
    required this.vehicleId,
    required this.vehiclePlate,
    required this.userId,
    required this.imagePath,
    required this.status,
    required this.reportStatus,
    required this.reportMessage,
    required this.damageSeverity,
    required this.damageTypes,
    required this.affectedParts,
    required this.repairRecommendations,
    required this.confidenceScore,
    required this.analysisMessage,
    required this.createdAt,
    required this.completedAt,
    required this.detections,
    required this.report,
    required this.locationCity,
  });

  final int id;

  final int vehicleId;
  final String vehiclePlate;
  final int userId;

  final String? imagePath;

  final String status;

  final String? reportStatus;
  final String? reportMessage;

  final String? damageSeverity;

  final List<String> damageTypes;
  final List<String> affectedParts;

  final List<DamageRepairRecommendation>
  repairRecommendations;

  final double? confidenceScore;
  final String? analysisMessage;

  final DateTime? createdAt;
  final DateTime? completedAt;

  final List<DamageDetection> detections;

  final InspectionReport? report;

  final String locationCity;

  bool get isCompleted => status == 'COMPLETED';

  bool get isProcessing => status == 'PROCESSING';

  bool get isFailed => status == 'FAILED';

  bool get isReportCompleted =>
      reportStatus == 'COMPLETED';

  bool get isReportFailed =>
      reportStatus == 'FAILED';

  factory DamageInspection.fromJson(
      Map<String, dynamic> json,
      ) {
    final reportJson = json['report'];

    return DamageInspection(
      id: (json['id'] as num?)?.toInt() ?? 0,

      vehicleId:
      (json['vehicleId'] as num?)?.toInt() ?? 0,

      vehiclePlate:
      json['vehiclePlate'] as String? ?? '',

      userId:
      (json['userId'] as num?)?.toInt() ?? 0,

      imagePath:
      json['imagePath'] as String?,

      status:
      json['status'] as String? ?? 'PENDING',

      reportStatus:
      json['reportStatus'] as String?,

      reportMessage:
      json['reportMessage'] as String?,

      damageSeverity:
      json['damageSeverity'] as String?,

      damageTypes:
      (json['damageTypes'] as List?)
          ?.whereType<String>()
          .toList() ??
          const [],

      affectedParts:
      (json['affectedParts'] as List?)
          ?.whereType<String>()
          .toList() ??
          const [],

      repairRecommendations:
      (json['repairRecommendations'] as List?)
          ?.whereType<Map>()
          .map(
            (item) =>
            DamageRepairRecommendation
                .fromJson(
              Map<String, dynamic>.from(
                item,
              ),
            ),
      )
          .toList() ??
          const [],

      confidenceScore:
      (json['confidenceScore'] as num?)
          ?.toDouble(),

      analysisMessage:
      json['analysisMessage'] as String?,

      createdAt: DateTime.tryParse(
        json['createdAt'] as String? ?? '',
      ),

      completedAt: DateTime.tryParse(
        json['completedAt'] as String? ?? '',
      ),

      detections:
      (json['detections'] as List?)
          ?.whereType<Map>()
          .map(
            (item) =>
            DamageDetection.fromJson(
              Map<String, dynamic>.from(
                item,
              ),
            ),
      )
          .toList() ??
          const [],

      report:
      reportJson is Map
          ? InspectionReport.fromJson(
        Map<String, dynamic>.from(
          reportJson,
        ),
      )
          : null,

      locationCity:
      json['locationCity'] as String? ?? '',
    );
  }
}