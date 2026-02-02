import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

class EventsScreen extends StatefulWidget {
  const EventsScreen({super.key});

  @override
  State<EventsScreen> createState() => _EventsScreenState();
}

class _EventsScreenState extends State<EventsScreen> {
  List<dynamic> _events = [];
  bool _loading = true;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    setState(() => _loading = true);
    final resp = await http.get(
      Uri.parse('http://localhost:8080/api/events/public'),
    );
    if (resp.statusCode == 200) {
      setState(() {
        _events = jsonDecode(resp.body) as List<dynamic>;
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
        itemCount: _events.length,
        itemBuilder: (context, index) {
          final e = _events[index] as Map<String, dynamic>;
          return ListTile(
            title: Text(e['title'] ?? ''),
            subtitle: Text(e['locationName'] ?? ''),
          );
        },
      ),
    );
  }
}

