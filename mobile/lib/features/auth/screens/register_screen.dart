import 'package:flutter/material.dart';
import '../services/auth_service.dart';

class RegisterScreen extends StatefulWidget {
  const RegisterScreen({super.key});

  @override
  State<RegisterScreen> createState() => _RegisterScreenState();
}

class _RegisterScreenState extends State<RegisterScreen> {
  final _formKey = GlobalKey<FormState>();

  final _fullNameController = TextEditingController();
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  final _passwordConfirmController = TextEditingController();
  final AuthService _authService = const AuthService();

  bool _obscurePassword = true;
  bool _obscurePasswordConfirm = true;
  bool _isLoading = false;

  @override
  void dispose() {
    _fullNameController.dispose();
    _emailController.dispose();
    _passwordController.dispose();
    _passwordConfirmController.dispose();
    super.dispose();
  }

  Future<void> _register() async {
    FocusScope.of(context).unfocus();

    if (!_formKey.currentState!.validate()) {
      return;
    }

    setState(() {
      _isLoading = true;
    });

    try {
      await _authService.register(
        fullName: _fullNameController.text,
        email: _emailController.text,
        password: _passwordController.text,
      );

      if (!mounted) {
        return;
      }

      ScaffoldMessenger.of(context)
        ..hideCurrentSnackBar()
        ..showSnackBar(
          const SnackBar(
            content: Text(
              'Hesabınız oluşturuldu. Şimdi giriş yapabilirsiniz.',
            ),
            behavior: SnackBarBehavior.floating,
          ),
        );

      Navigator.of(context).pop();

    } catch (exception) {
      if (!mounted) {
        return;
      }

      final message = exception
          .toString()
          .replaceFirst('Exception: ', '');

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
          _isLoading = false;
        });
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    final textTheme = Theme.of(context).textTheme;

    return Scaffold(
      body: Stack(
        children: [
          Positioned(
            top: -130,
            right: -100,
            child: Container(
              width: 290,
              height: 290,
              decoration: BoxDecoration(
                shape: BoxShape.circle,
                color: colorScheme.primaryContainer.withValues(
                  alpha: 0.55,
                ),
              ),
            ),
          ),
          Positioned(
            bottom: -150,
            left: -130,
            child: Container(
              width: 300,
              height: 300,
              decoration: BoxDecoration(
                shape: BoxShape.circle,
                color: colorScheme.secondaryContainer.withValues(
                  alpha: 0.35,
                ),
              ),
            ),
          ),
          SafeArea(
            child: SingleChildScrollView(
              padding: const EdgeInsets.symmetric(
                horizontal: 24,
                vertical: 18,
              ),
              child: ConstrainedBox(
                constraints: BoxConstraints(
                  minHeight:
                  MediaQuery.sizeOf(context).height - 72,
                ),
                child: Form(
                  key: _formKey,
                  child: Column(
                    crossAxisAlignment:
                    CrossAxisAlignment.stretch,
                    children: [
                      Align(
                        alignment: Alignment.centerLeft,
                        child: IconButton.filledTonal(
                          onPressed: () {
                            Navigator.of(context).pop();
                          },
                          icon: const Icon(
                            Icons.arrow_back_rounded,
                          ),
                        ),
                      ),
                      const SizedBox(height: 12),
                      Text(
                        'Hesabınızı oluşturun',
                        style: textTheme.headlineMedium?.copyWith(
                          fontWeight: FontWeight.w800,
                          letterSpacing: -0.7,
                        ),
                      ),
                      const SizedBox(height: 8),
                      Text(
                        'Araçlarınızı kaydedin ve hasar analizlerinizi tek yerden yönetin.',
                        style: textTheme.bodyLarge?.copyWith(
                          color: colorScheme.onSurfaceVariant,
                          height: 1.45,
                        ),
                      ),
                      const SizedBox(height: 28),
                      Text(
                        'Ad Soyad',
                        style: textTheme.labelLarge?.copyWith(
                          fontWeight: FontWeight.w700,
                        ),
                      ),
                      const SizedBox(height: 8),
                      TextFormField(
                        controller: _fullNameController,
                        textInputAction: TextInputAction.next,
                        autofillHints: const [
                          AutofillHints.name,
                        ],
                        decoration: const InputDecoration(
                          hintText: 'Adınız ve soyadınız',
                          prefixIcon: Icon(
                            Icons.person_outline_rounded,
                          ),
                        ),
                        validator: (value) {
                          final fullName =
                              value?.trim() ?? '';

                          if (fullName.length < 2) {
                            return 'Ad soyad en az 2 karakter olmalıdır.';
                          }

                          return null;
                        },
                      ),
                      const SizedBox(height: 18),
                      Text(
                        'E-posta',
                        style: textTheme.labelLarge?.copyWith(
                          fontWeight: FontWeight.w700,
                        ),
                      ),
                      const SizedBox(height: 8),
                      TextFormField(
                        controller: _emailController,
                        keyboardType:
                        TextInputType.emailAddress,
                        textInputAction: TextInputAction.next,
                        autofillHints: const [
                          AutofillHints.email,
                        ],
                        decoration: const InputDecoration(
                          hintText: 'ornek@email.com',
                          prefixIcon: Icon(
                            Icons.mail_outline_rounded,
                          ),
                        ),
                        validator: (value) {
                          final email = value?.trim() ?? '';

                          if (email.isEmpty) {
                            return 'E-posta adresinizi girin.';
                          }

                          if (!email.contains('@')) {
                            return 'Geçerli bir e-posta adresi girin.';
                          }

                          return null;
                        },
                      ),
                      const SizedBox(height: 18),
                      Text(
                        'Şifre',
                        style: textTheme.labelLarge?.copyWith(
                          fontWeight: FontWeight.w700,
                        ),
                      ),
                      const SizedBox(height: 8),
                      TextFormField(
                        controller: _passwordController,
                        obscureText: _obscurePassword,
                        textInputAction: TextInputAction.next,
                        autofillHints: const [
                          AutofillHints.newPassword,
                        ],
                        decoration: InputDecoration(
                          hintText: 'En az 8 karakter',
                          prefixIcon: const Icon(
                            Icons.lock_outline_rounded,
                          ),
                          suffixIcon: IconButton(
                            onPressed: () {
                              setState(() {
                                _obscurePassword =
                                !_obscurePassword;
                              });
                            },
                            icon: Icon(
                              _obscurePassword
                                  ? Icons
                                  .visibility_off_outlined
                                  : Icons.visibility_outlined,
                            ),
                          ),
                        ),
                        validator: (value) {
                          if (value == null ||
                              value.length < 8) {
                            return 'Şifre en az 8 karakter olmalıdır.';
                          }

                          return null;
                        },
                      ),
                      const SizedBox(height: 18),
                      Text(
                        'Şifre Tekrar',
                        style: textTheme.labelLarge?.copyWith(
                          fontWeight: FontWeight.w700,
                        ),
                      ),
                      const SizedBox(height: 8),
                      TextFormField(
                        controller:
                        _passwordConfirmController,
                        obscureText:
                        _obscurePasswordConfirm,
                        textInputAction: TextInputAction.done,
                        onFieldSubmitted: (_) => _register(),
                        decoration: InputDecoration(
                          hintText: 'Şifrenizi tekrar girin',
                          prefixIcon: const Icon(
                            Icons.lock_reset_rounded,
                          ),
                          suffixIcon: IconButton(
                            onPressed: () {
                              setState(() {
                                _obscurePasswordConfirm =
                                !_obscurePasswordConfirm;
                              });
                            },
                            icon: Icon(
                              _obscurePasswordConfirm
                                  ? Icons
                                  .visibility_off_outlined
                                  : Icons.visibility_outlined,
                            ),
                          ),
                        ),
                        validator: (value) {
                          if (value !=
                              _passwordController.text) {
                            return 'Şifreler eşleşmiyor.';
                          }

                          return null;
                        },
                      ),
                      const SizedBox(height: 28),
                      FilledButton(
                        onPressed:
                        _isLoading ? null : _register,
                        child: _isLoading
                            ? const SizedBox(
                          width: 22,
                          height: 22,
                          child:
                          CircularProgressIndicator(
                            strokeWidth: 2.4,
                          ),
                        )
                            : const Text(
                          'Hesap Oluştur',
                        ),
                      ),
                      const SizedBox(height: 18),
                      Row(
                        mainAxisAlignment:
                        MainAxisAlignment.center,
                        children: [
                          Text(
                            'Zaten hesabınız var mı?',
                            style: TextStyle(
                              color: colorScheme
                                  .onSurfaceVariant,
                            ),
                          ),
                          TextButton(
                            onPressed: () {
                              Navigator.of(context).pop();
                            },
                            child: const Text(
                              'Giriş Yap',
                            ),
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}