class ApiConstants {
  ApiConstants._();

  static const String baseUrl =
      'http://10.0.2.2:8080/api/v1';

  static const String loginEndpoint =
      '$baseUrl/auth/login';

  static const String registerEndpoint =
      '$baseUrl/auth/register';

  static const String currentUserEndpoint =
      '$baseUrl/auth/me';
}