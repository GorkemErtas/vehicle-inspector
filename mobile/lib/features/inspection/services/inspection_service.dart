import '../../../core/network/api_client.dart';
import '../models/damage_inspection.dart';

class InspectionService {
  const InspectionService({
    this.apiClient = const ApiClient(),
  });

  final ApiClient apiClient;

  Future<DamageInspection> createInspection({
    required int vehicleId,
    required String city,
  }) async {
    final response = await apiClient.post(
      '/inspections'
          '?vehicleId=$vehicleId'
          '&city=${Uri.encodeQueryComponent(city.trim())}',
    );

    return _parseInspection(
      response,
      'Hasar incelemesi oluşturulamadı.',
    );
  }

  Future<List<DamageInspection>>
  getInspections() async {
    final response = await apiClient.get(
      '/inspections',
    );

    if (response is! List) {
      return [];
    }

    return response
        .whereType<Map>()
        .map(
          (item) => DamageInspection.fromJson(
        Map<String, dynamic>.from(item),
      ),
    )
        .toList();
  }

  Future<DamageInspection> getInspectionById(
      int inspectionId,
      ) async {
    final response = await apiClient.get(
      '/inspections/$inspectionId',
    );

    return _parseInspection(
      response,
      'Hasar incelemesi alınamadı.',
    );
  }

  Future<DamageInspection> uploadImage({
    required int inspectionId,
    required String imagePath,
  }) async {
    final response = await apiClient.postMultipart(
      '/inspections/$inspectionId/image',
      filePath: imagePath,
      fileFieldName: 'image',
    );

    return _parseInspection(
      response,
      'Hasar fotoğrafı yüklenemedi.',
    );
  }

  Future<DamageInspection> analyzeInspection(
      int inspectionId,
      ) async {
    final response = await apiClient.post(
      '/inspections/$inspectionId/analyze',
    );

    return _parseInspection(
      response,
      'Hasar analizi tamamlanamadı.',
    );
  }

  Future<DamageInspection> regenerateReport(
      int inspectionId,
      ) async {
    final response = await apiClient.post(
      '/inspections/$inspectionId/report',
    );

    return _parseInspection(
      response,
      'AI raporu yeniden oluşturulamadı.',
    );
  }

  DamageInspection _parseInspection(
      dynamic response,
      String errorMessage,
      ) {
    if (response is! Map<String, dynamic>) {
      throw FormatException(errorMessage);
    }

    return DamageInspection.fromJson(
      response,
    );
  }
}