import 'dart:convert';

import 'package:http/http.dart' as http;

import '../constants/api_constants.dart';
import '../storage/token_storage.dart';
import 'api_exception.dart';

class ApiClient {
  const ApiClient();

  Future<dynamic> get(
      String path,
      ) async {
    final response = await http.get(
      _buildUri(path),
      headers: await _buildHeaders(),
    );

    return _handleResponse(response);
  }

  Future<dynamic> post(
      String path, {
        Map<String, dynamic>? body,
      }) async {
    final response = await http.post(
      _buildUri(path),
      headers: await _buildHeaders(),
      body: body == null ? null : jsonEncode(body),
    );

    return _handleResponse(response);
  }

  Future<dynamic> put(
      String path, {
        required Map<String, dynamic> body,
      }) async {
    final response = await http.put(
      _buildUri(path),
      headers: await _buildHeaders(),
      body: jsonEncode(body),
    );

    return _handleResponse(response);
  }

  Future<void> delete(
      String path,
      ) async {
    final response = await http.delete(
      _buildUri(path),
      headers: await _buildHeaders(),
    );

    _handleResponse(response);
  }

  Uri _buildUri(
      String path,
      ) {
    final normalizedPath = path.startsWith('/')
        ? path
        : '/$path';

    return Uri.parse(
      '${ApiConstants.baseUrl}$normalizedPath',
    );
  }

  Future<Map<String, String>> _buildHeaders() async {
    final token = await TokenStorage.getAccessToken();

    return {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
      if (token != null && token.isNotEmpty)
        'Authorization': 'Bearer $token',
    };
  }

  dynamic _handleResponse(
      http.Response response,
      ) {
    final hasBody = response.bodyBytes.isNotEmpty;

    dynamic decodedBody;

    if (hasBody) {
      try {
        decodedBody = jsonDecode(
          utf8.decode(response.bodyBytes),
        );
      } catch (_) {
        decodedBody = null;
      }
    }

    if (response.statusCode >= 200 &&
        response.statusCode < 300) {
      return decodedBody;
    }

    throw ApiException(
      statusCode: response.statusCode,
      message: _extractErrorMessage(
        response.statusCode,
        decodedBody,
      ),
    );
  }

  String _extractErrorMessage(
      int statusCode,
      dynamic body,
      ) {
    if (body is Map<String, dynamic>) {
      final message =
          body['message'] ??
              body['detail'] ??
              body['error'];

      if (message is String && message.isNotEmpty) {
        return message;
      }
    }

    return switch (statusCode) {
      400 => 'Gönderilen bilgiler geçersiz.',
      401 => 'Oturum süreniz dolmuş olabilir.',
      403 => 'Bu işlem için yetkiniz bulunmuyor.',
      404 => 'İstenen kayıt bulunamadı.',
      409 => 'Bu kayıt zaten mevcut.',
      _ => 'Sunucu işlemi tamamlayamadı.',
    };
  }
}