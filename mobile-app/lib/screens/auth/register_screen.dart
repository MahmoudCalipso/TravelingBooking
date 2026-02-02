import 'dart:convert';

import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

enum UserRole { traveler, supplier }

class RegisterScreen extends StatefulWidget {
  const RegisterScreen({super.key});

  @override
  State<RegisterScreen> createState() => _RegisterScreenState();
}

class _RegisterScreenState extends State<RegisterScreen> {
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  final _displayNameController = TextEditingController();
  UserRole _role = UserRole.traveler;
  bool _loading = false;
  String? _error;

  Future<void> _register() async {
    setState(() {
      _loading = true;
      _error = null;
    });
    try {
      final cred = await FirebaseAuth.instance.createUserWithEmailAndPassword(
        email: _emailController.text.trim(),
        password: _passwordController.text.trim(),
      );
      await cred.user?.updateDisplayName(_displayNameController.text.trim());

      final token = await cred.user!.getIdToken();

      final role = _role == UserRole.traveler ? 'TRAVELER' : 'SUPPLIER';

      // Call backend registration to store role & profile
      final resp = await http.post(
        Uri.parse('http://localhost:8080/api/users/register'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $token',
        },
        body: jsonEncode({
          'firebaseUid': cred.user!.uid,
          'email': _emailController.text.trim(),
          'displayName': _displayNameController.text.trim(),
          'role': role
        }),
      );

      if (resp.statusCode >= 400) {
        setState(() {
          _error = 'Backend registration failed: ${resp.body}';
        });
      } else {
        if (!mounted) return;
        Navigator.of(context).pop();
      }
    } on FirebaseAuthException catch (e) {
      setState(() {
        _error = e.message;
      });
    } finally {
      setState(() {
        _loading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Register')),
      body: Center(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(24),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(
                controller: _displayNameController,
                decoration: const InputDecoration(labelText: 'Full name'),
              ),
              const SizedBox(height: 12),
              TextField(
                controller: _emailController,
                decoration: const InputDecoration(labelText: 'Email'),
              ),
              const SizedBox(height: 12),
              TextField(
                controller: _passwordController,
                obscureText: true,
                decoration: const InputDecoration(labelText: 'Password'),
              ),
              const SizedBox(height: 20),
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Radio<UserRole>(
                    value: UserRole.traveler,
                    groupValue: _role,
                    onChanged: (v) => setState(() => _role = v!),
                  ),
                  const Text('Traveler'),
                  const SizedBox(width: 16),
                  Radio<UserRole>(
                    value: UserRole.supplier,
                    groupValue: _role,
                    onChanged: (v) => setState(() => _role = v!),
                  ),
                  const Text('Supplier'),
                ],
              ),
              const SizedBox(height: 20),
              if (_error != null)
                Text(
                  _error!,
                  style: const TextStyle(color: Colors.red),
                ),
              const SizedBox(height: 12),
              ElevatedButton(
                onPressed: _loading ? null : _register,
                child: _loading
                    ? const CircularProgressIndicator()
                    : const Text('Create account'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

