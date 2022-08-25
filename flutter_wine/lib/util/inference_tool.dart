import 'dart:typed_data';

import 'package:flutter/services.dart';
import 'package:flutter_wine/util/getx_debug_tool.dart';
import 'package:flutter_wine/util/speed_tester.dart';
import 'package:flutter_wine/widgets/bottom_black_box.dart';
import 'package:tflite_flutter/tflite_flutter.dart';
import 'package:tflite_flutter/tflite_flutter.dart' as tfl;

class RegressionPredict {
  static RegressionPredict _instance;

  static RegressionPredict get instance =>
      _instance ??= RegressionPredict._internal();

  factory RegressionPredict() => instance;

  RegressionPredict._internal();

  List<RegressionInterface> interfaces = [];

  Future<List<double>> predict(List<double> curve) async {
    List<double> sum = [];
    var count = 0;
    for (var i = 0; i < interfaces.length; i++) {
      if (interfaces[i].enabled) {
        sum.add(await interfaces[i].predict(curve));
        count++;
      }
    }
    return sum;
  }
}

abstract class RegressionInterface {
  bool get enabled;

  Future<double> predict(List<double> curve);
}

class ClassificationPredict {
  static ClassificationPredict _instance;

  static ClassificationPredict get instance =>
      _instance ??= ClassificationPredict._internal();

  factory ClassificationPredict() => instance;

  ClassificationPredict._internal();

  static List<int> logitsIndexSort(List<double> possibilities) {
    List<int> index = [];
    for (int i = 0; i < possibilities.length; i++) {
      index.add(i);
    }
    index.sort((a, b) => possibilities[a].compareTo(possibilities[b]));
    return index;
  }

  List<ClassificationInterface> interfaces = [];

  Future<List<List<double>>> predict(List<double> curve) async {
    List<List<double>> possibilities = [];
    for (var i = 0; i < interfaces.length; i++) {
      if (interfaces[i].enabled) {
        possibilities.add(await interfaces[i].predict(curve));
      }
    }
    return possibilities;
  }
}

abstract class ClassificationInterface {
  bool get enabled;

  Future<List<double>> predict(List<double> curve);
}
