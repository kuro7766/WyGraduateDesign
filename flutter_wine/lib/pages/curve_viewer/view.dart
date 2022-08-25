import 'package:fl_chart/fl_chart.dart';
import 'package:flutter/material.dart';
import 'package:flutter_wine/util/mixin/curve_fetch.dart';
import 'package:flutter_wine/widgets/plotter/view.dart';
import 'package:get/get.dart';

import 'logic.dart';
import 'dart:async';
import 'dart:math';

import 'package:fl_chart/fl_chart.dart';
import 'package:flutter/material.dart';
import 'package:flutter_wine/config.dart';
import 'package:flutter_wine/util/laser.dart';
import 'package:flutter_wine/util/laser_image.dart';
import 'package:flutter_wine/util/mixin/evnetbus_mixin.dart';
import 'package:flutter_wine/widgets/bottom_black_box.dart';
import 'package:get/get.dart';
import 'package:get/get_rx/src/rx_types/rx_types.dart';
import 'package:get_storage/get_storage.dart';
import 'package:numdart/numdart.dart';
import 'dart:math' as Math;
import 'logic.dart';

class CurveViewerComponent extends StatefulWidget {
  @override
  _CurveViewerComponentState createState() => _CurveViewerComponentState();
}

class _CurveViewerComponentState extends State<CurveViewerComponent>
    with CurveFetchMixin {
  final logic = Get.put(CurveViewerLogic());
  final state = Get.find<CurveViewerLogic>().state;

  // var paddingSize = 50.0;
  var paddingSize = 20.0;
  var i = 1;
  List<int> spots = [];
  List<Color> gradientColors = [
    const Color(0xff23b6e6),
    const Color(0xff02d39a),
  ];

  Color waterColor = Colors.deepPurpleAccent;

  num mathAbs(num n) {
    return n.abs();
  }

  void calibrate() async {
    await (() async {
      var result = await fetchCurve();
      // spots = List.generate(white.length,
      //     (index) => FlSpot(index.toDouble(), white[index].toDouble()));
      spots = result.curve;
      if (mounted) setState(() {});
    })();
    if(mounted) {
      calibrate();
    }
  }

  @override
  void initState() {
    super.initState();
    calibrate();
  }

  @override
  Widget build(BuildContext context) {
    return (spots.isEmpty
        ? Container()
        : PlotterComponent(
            data: {'spectrum': spots},
          ));
  }

  @override
  void dispose() {
    Get.delete<CurveViewerLogic>();
    super.dispose();
  }
}
