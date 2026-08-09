import 'dart:convert';

import 'package:http/http.dart' as http;

import '../../../core/constants/api_constants.dart';
import '../../../core/storage/token_storage.dart';
import '../models/auth_response.dart';
import '../../../core/network/api_client.dart';
import '../models/user_profile.dart';

class AuthService {
  const AuthService();

  Future<AuthResponse> login({
    required String email,
    required String password,
  }) async {
    final response = await http.post(
      Uri.parse(ApiConstants.loginEndpoint),
      headers: const {
        'Content-Type': 'application/json',
      },
      body: jsonEncode({
        'email': email.trim(),
        'password': password,
      }),
    );

    if (response.statusCode < 200 ||
        response.statusCode >= 300) {
      throw Exception(
        _extractErrorMessage(response),
      );
    }

    final json = jsonDecode(
      utf8.decode(response.bodyBytes),
    ) as Map<String, dynamic>;

    final authResponse =
    AuthResponse.fromJson(json);

    if (authResponse.accessToken.isEmpty) {
      throw Exception(
        'Sunucudan geçerli bir erişim anahtarı alınamadı.',
      );
    }

    await TokenStorage.saveAccessToken(
      authResponse.accessToken,
    );

    return authResponse;
  }

  Future<void> logout() async {
    await TokenStorage.clearAll();
  }

  String _extractErrorMessage(
      http.Response response,
      ) {
    try {
      final body = jsonDecode(
        utf8.decode(response.bodyBytes),
      );

      if (body is Map<String, dynamic>) {
        final message =
            body['message'] ??
                body['detail'] ??
                body['error'];

        if (message is String &&
            message.isNotEmpty) {
          return message;
        }
      }
    } catch (_) {
    }

    if (response.statusCode == 401) {
      return 'E-posta veya şifre hatalı.';
    }

    if (response.statusCode == 403) {
      return 'Bu işlem için yetkiniz bulunmuyor.';
    }

    return 'Giriş işlemi tamamlanamadı.';
  }

  Future<void> register({
    required String fullName,
    required String email,
    required String password,
  }) async {
    final response = await http.post(
      Uri.parse(ApiConstants.registerEndpoint),
      headers: const {
        'Content-Type': 'application/json',
      },
      body: jsonEncode({
        'fullName': fullName.trim(),
        'email': email.trim().toLowerCase(),
        'password': password,
      }),
    );

    if (response.statusCode < 200 ||
        response.statusCode >= 300) {
      throw Exception(
        _extractErrorMessage(response),
      );
    }
  }

  Future<UserProfile> getCurrentUser() async {
    const apiClient = ApiClient();

    final response = await apiClient.get(
      '/auth/me',
    );

    if (response is! Map<String, dynamic>) {
      throw const FormatException(
        'Kullanıcı bilgileri alınamadı.',
      );
    }

    return UserProfile.fromJson(response);
  }
}