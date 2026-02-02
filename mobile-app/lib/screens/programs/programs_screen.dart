import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

class ProgramsScreen extends StatefulWidget {
  const ProgramsScreen({super.key});

  @override
  State<ProgramsScreen> createState() => _ProgramsScreenState();
}

class _ProgramsScreenState extends State<ProgramsScreen> {
  List<dynamic> _programs = [];
  bool _loading = true;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    setState(() => _loading = true);
    final resp = await http.get(
      Uri.parse('http://localhost:8080/api/programs/public'),
    );
    if (resp.statusCode == 200) {
      setState(() {
        _programs = jsonDecode(resp.body) as List<dynamic>;
      });
    }
    setState(() => _loading = false);
  }

  @override
  Widget build(BuildContext context) {
    if (_loading) {
      return const Center(child: CircularProgressIndicator());
    }
    return RefreshIndicator(
      onRefresh: _load,
      child: ListView.builder(
        itemCount: _programs.length,
        itemBuilder: (context, index) {
          final p = _programs[index] as Map<String, dynamic>;
          return ListTile(
            title: Text(p['title'] ?? ''),
            subtitle: Text(p['mainDestination'] ?? ''),
          );
        },
      ),
    );
  }
}

