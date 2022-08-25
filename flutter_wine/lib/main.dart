import 'dart:io';
import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_wine/pages/home/view.dart';
import 'package:flutter_wine/urls.dart';
import 'package:flutter_wine/util/async_mutex.dart';
import 'package:flutter_wine/util/block.dart';
import 'package:flutter_wine/util/getx_debug_tool.dart';
import 'package:flutter_wine/util/spectrometer_tool.dart';
import 'package:flutter_wine/widgets/spect_connection_widget.dart';
import 'package:get/get.dart';
import 'package:http/http.dart' as http;
import 'package:process_run/shell.dart';
import 'package:tflite_flutter/tflite_flutter.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key key}) : super(key: key);

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return GetMaterialApp(
      title: 'Flutter Demo',
      getPages: [
        GetPage(
            name: '/', page: () =>  HomePage()),
      ],
      initialRoute: '/',
      theme: ThemeData(
        primarySwatch: Colors.blue,
        // fontFamily: 'Songti'
      ),
      // showSemanticsDebugger: false,
      debugShowCheckedModeBanner: false,

    );
  }
}

// class MyHomePage extends StatefulWidget {
//   const MyHomePage({Key key, this.title}) : super(key: key);
//   final String title;
//
//   @override
//   State<MyHomePage> createState() => _MyHomePageState();
// }
//
// class _MyHomePageState extends State<MyHomePage> {
//   int _counter = 0;
//   AsyncMutex mutex = AsyncMutex();
//   Block block = Block(1000);
//   Widget image;
//
//
//
//   // testStream() async {
//   //   print('clicked');
//   //   var client = HttpClient();
//   //   HttpClientRequest request = await client
//   //       .getUrl(Uri.parse('http://192.168.137.161:8083/?action=snapshot'));
//   //   // .getUrl(Uri.parse('http://192.168.137.161:8083/?action=stream'));
//   //
//   //   // request.headers.contentType =
//   //   //     ContentType('application', 'json', charset: 'utf-8');
//   //   HttpClientResponse response = await request.close();
//   //   List<int> all = [];
//   //   await response.forEach((event) {
//   //     all.addAll(event);
//   //     print('foreach:${event.length}');
//   //     // block.run(() {
//   //     //   print('l:${all.length}');
//   //     //   image = Image.memory(Uint8List.fromList(all));
//   //     //   print('image:$image');
//   //     //   setState(() {});
//   //     // });
//   //   });
//   //
//   //   // image = Image.memory(Uint8List.fromList(all));
//   //   // setState(() {});
//   //   print('singleImage : ${all.sublist(103400)}');
//   //
//   //   // stream already listened
//   //   // response.forEach((element) {
//   //   //   print(element.length);
//   //   // });
//   // }
//
//   @override
//   void initState() {
//     super.initState();
//
//   }
//
//   @override
//   Widget build(BuildContext context) {
//     return SafeArea(
//       child: HomePage()
//     );
//   }
// }
