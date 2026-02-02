import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

class StaysScreen extends StatefulWidget {
  const StaysScreen({super.key});

  @override
  State<StaysScreen> createState() => _StaysScreenState();
}

class _StaysScreenState extends State<StaysScreen> {
  List<dynamic> _stays = [];
  bool _loading = true;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    setState(() => _loading = true);
    final resp = await http.get(
      Uri.parse('http://localhost:8080/api/accommodations/public/search'),
    );
    if (resp.statusCode == 200) {
      setState(() {
        _stays = jsonDecode(resp.body) as List<dynamic>;
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
        itemCount: _stays.length,
        itemBuilder: (context, index) {
          final s = _stays[index] as Map<String, dynamic>;
          return ListTile(
            title: Text(s['title'] ?? ''),
            subtitle: Text('${s['city']}, ${s['country']}'),
            trailing: Text('${s['pricePerNight'] ?? ''}'),
          );
        },
      ),
    );
  }
}

