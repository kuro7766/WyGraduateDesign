import 'dart:math';
//白酒智能检测页面
import 'package:flutter/material.dart';
import 'package:flutter_wine/util/getx_debug_tool.dart';
import 'package:flutter_wine/util/mixin/curve_fetch.dart';
import 'package:flutter_wine/util/tflite/tf.dart';
import 'package:flutter_wine/widgets/plotter/view.dart';
import 'package:flutter_wine/widgets/wine_report/view.dart';
import 'package:get/get.dart';
import 'package:path_provider/path_provider.dart';

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

class WineTypeComponent extends StatefulWidget {
  @override
  _WineTypeComponentState createState() => _WineTypeComponentState();
}

class _WineTypeComponentState extends State<WineTypeComponent>
    with CurveFetchMixin {
  final logic = Get.put(WineTypeLogic());
  final state = Get
      .find<WineTypeLogic>()
      .state;


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
      var subbedPixels = result.curve;
      logic.prediction(subbedPixels.sublist(result.waterRamanIndex-200,result.waterRamanIndex+800));

      await LaserController.instance.close();

      spots = subbedPixels;
      if (mounted)
        setState(() {});
    })();
  }

  @override
  void initState() {
    super.initState();
    calibrate();
  }

  curvePageBuilder() =>
      (spots.isEmpty
          ? Container()
          : PlotterComponent(data: {
        'curve': spots,
      },));

  @override
  Widget build(BuildContext context) {
    return Obx(() {
      return IndexedStack(
        index: state.index.value,
        children: [
          curvePageBuilder(),
          Builder(
            builder: (context) {
              return state.builder==null?Container():state.builder(context);
            }
          )
        ],
      );
    });
  }

  @override
  void dispose() {
    Get.delete<WineTypeLogic>();
    super.dispose();
  }
}
