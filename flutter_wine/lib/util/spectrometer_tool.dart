import 'dart:io';

import 'package:dart_ipify/dart_ipify.dart';
import 'package:dio/dio.dart';
import 'package:flutter_wine/urls.dart';
import 'package:flutter_wine/util/db/my_db.dart';
import 'package:flutter_wine/util/getx_debug_tool.dart';
import 'package:http/http.dart' as http;
import 'package:network_info_plus/network_info_plus.dart';
import 'package:uuid/uuid.dart';
import 'package:uuid/uuid_util.dart';
class SpectrometerTool {
//  singleton
  static SpectrometerTool _instance;

  static SpectrometerTool get instance =>
      _instance ??= SpectrometerTool._internal();

  factory SpectrometerTool() => instance;

  SpecDb get database=>SpecDb();

  SpectrometerTool._internal();
  String _ipToSubnet(String ip) {
    return ip.substring(0, ip.lastIndexOf('.'));
  }
  getSpectrumIp(Function(String ip) onFindCallBack,
      {Function() onNotFoundCallback}) async {
    Dbg.log('go');
    var wifiIP = await (NetworkInfo().getWifiIP());
    // Dbg.log(await (NetworkInfo().getWifiGatewayIP()));
    // Dbg.log(await (NetworkInfo().getWifiBroadcast()));

    // 找到ip前缀
    var subnet = _ipToSubnet(wifiIP??'192.168.1.1');
    // subnet='192.168.255';
    Dbg.log(subnet,'subnet');
    var dio = Dio();
    dio.options.connectTimeout = 2000; //5s
    var count = 0;
    Function() innerFailureCallback = () {
      count++;
      if(count==255){
        onNotFoundCallback?.call();
      }
    };
    for (var i = 0; i < 255; i++) {
      // 低端机可能爆炸
      // Dbg.log(i);
      () async {
        try {
          var targetUrl = Urls.imageUrlFromIp('$subnet.$i');
          // Dbg.log(targetUrl);
          Response a = await dio.get(targetUrl);
          print(a.statusCode);
          onFindCallBack?.call('$subnet.$i');
        } catch (e) {
          innerFailureCallback();
        }
      }();
    }

    // final subnet = "192.168.0";
    // final timeout = Duration(seconds: 5);
    //
    // final scanner = LanScanner();
    //
    // final stream = scanner.preciseScan(
    //   subnet,
    //   progressCallback: (ProgressModel progress) {
    //     print('${progress.percent * 100}% 192.168.0.${progress.currIP}');
    //   },
    // );
    //
    // stream.listen((DeviceModel device) {
    //   if (device.exists) {
    //     print("Found device on ${device.ip}:${device.port}");
    //   }
    // });
    // 192.168.1.1 to 192.168.
    // http.get(Uri.parse(Urls.imageUrlFromIp()));
  }

  Future<String> saveCurveToFile(List<int> curve) async {
    String file=Uuid().v4();
    File(file).writeAsBytesSync(curve);
    return file;
  }
     

}
