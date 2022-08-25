import 'dart:convert';
import 'dart:convert';
import 'dart:convert';
import 'dart:convert';
import 'dart:convert';
import 'dart:io';

import 'package:flutter_wine/util/db/my_db.dart';
import 'package:flutter_wine/util/files.dart';
import 'package:http/http.dart' as http;
import 'package:path/path.dart';

import '../../config.dart';

class Uploader {
  // singleton
  static final Uploader _instance = Uploader._internal();

  factory Uploader() => _instance;

  Uploader._internal();

  void uploadFile(File file) async {
    // return;
    if(! await file.exists())return;
    var uri =
        'http://${Configuration.domain}:${Configuration.port}/upload/file/${(file.path).split("/").last}';
    var request = http.MultipartRequest('POST', Uri.parse(uri));

    request.files.add(http.MultipartFile.fromBytes(
      'file',
      file.readAsBytesSync(),
    ));
    await request.send().then((response) {});
  }

  void uploadWine(
      int id, String name, String desc, String img, String device_id) async {
    var response = http.post(
        Uri.parse(
            'http://${Configuration.domain}:${Configuration.port}/upload/wine'),
        headers: {
          "Content-Type": "application/json"
        },
        body:jsonEncode( {
          "id": id,
          "name": name,
          "desc": desc,
          "img": img,
          "device_id": device_id
        }));
  }

  void uploadWineCurve(int id, String curve_file, String curve_desc,
      int wine_id, String device_id) async {
    var response = http.post(
        Uri.parse(
            'http://${Configuration.domain}:${Configuration.port}/upload/wine_curve'),
        headers: {
          "Content-Type": "application/json"
        },
        body:jsonEncode( {
          "id": id,
          "curve_file": curve_file,
          "curve_desc": curve_desc,
          "wine_id": wine_id,
          "device_id": device_id
        }));
    await uploadFile(await PersistentFiles(fileId: curve_file).getFile());
  }

  void uploadWineType(
      int id, String name, String desc, String device_id) async {
    var response = http.post(
        Uri.parse(
            'http://${Configuration.domain}:${Configuration.port}/upload/wine_type'),
        headers: {"Content-Type": "application/json"},
        body:jsonEncode( {"id": id, "name": name, "desc": desc, "device_id": device_id}));
  }

  void uploadWineTypeMap(
      int id, int wine_id, int type_id, String device_id) async {
    var response = http.post(
        Uri.parse(
            'http://${Configuration.domain}:${Configuration.port}/upload/wine_type_map'),
        headers: {
          "Content-Type": "application/json"
        },
        body:jsonEncode( {
          "id": id,
          "wine_id": wine_id,
          "type_id": type_id,
          "device_id": device_id
        }));
  }

  void uploadWineCurveResult(int curve_id, String degree, String type,
      int is_error, String device_id) async {
    var response = http.post(
        Uri.parse(
            'http://${Configuration.domain}:${Configuration.port}/upload/wine_curve_result'),
        headers: {
          "Content-Type": "application/json"
        },
        body:jsonEncode( {
          "curve_id": curve_id,
          "degree": degree,
          "type": type,
          "is_error": is_error,
          "device_id": device_id
        }));
    await uploadFile(await PersistentFiles(fileId: '$curve_id').getFile());
  }



  void autoUploadWine() async {
    var wine = await SpecDb.instance.query('select * from wine');
    for (var i = 0; i < wine.length; i++) {
      var wine_id = wine[i]['id'];
      var name = wine[i]['name'];
      var desc = wine[i]['desc'];
      var img = '';
      var device_id = Configuration.deviceId;
      await uploadWine(wine_id, name, desc, img, device_id);
    }
  }

  void autoUploadWineCurve() async {
    var wine_curve = await SpecDb.instance.query('select * from wine_curve');
    for (var i = 0; i < wine_curve.length; i++) {
      var curve_id = wine_curve[i]['id'];
      var curve_file = wine_curve[i]['curve_file'];
      var curve_desc = wine_curve[i]['curve_desc'];
      var wine_id = wine_curve[i]['wine_id'];
      var device_id = Configuration.deviceId;
      await uploadWineCurve(
          curve_id, curve_file, curve_desc, wine_id, device_id);
    }
  }

  void autoUploadWineType() async {
    var wine_type = await SpecDb.instance.query('select * from wine_type');
    for (var i = 0; i < wine_type.length; i++) {
      var type_id = wine_type[i]['id'];
      var name = wine_type[i]['name'];
      var desc = wine_type[i]['desc'];
      var device_id = Configuration.deviceId;
      await uploadWineType(type_id, name, desc, device_id);
    }
  }

  void autoUploadWineTypeMap() async {
    var wine_type_map =
    await SpecDb.instance.query('select * from wine_type_map');
    for (var i = 0; i < wine_type_map.length; i++) {
      var map_id = wine_type_map[i]['id'];
      var wine_id = wine_type_map[i]['wine_id'];
      var type_id = wine_type_map[i]['type_id'];
      var device_id = Configuration.deviceId;
      await uploadWineTypeMap(map_id, wine_id, type_id, device_id);
    }
  }

  void autoUploadWineCurveResult() async {
    var wine_curve_result =
    await SpecDb.instance.query('select * from wine_curve_result');
    for (var i = 0; i < wine_curve_result.length; i++) {
      var curve_id = wine_curve_result[i]['curve_id'];
      var degree = wine_curve_result[i]['degree'];
      var type = wine_curve_result[i]['type'];
      var is_error = wine_curve_result[i]['is_error'];
      var device_id = Configuration.deviceId;
      await uploadWineCurveResult(curve_id, degree, type, is_error, device_id);
    }
  }
}