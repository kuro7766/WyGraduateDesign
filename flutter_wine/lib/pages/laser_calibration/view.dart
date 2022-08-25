import 'dart:async';
import 'dart:math';

import 'package:fl_chart/fl_chart.dart';
import 'package:flutter/material.dart';
import 'package:flutter_wine/config.dart';
import 'package:flutter_wine/util/getx_debug_tool.dart';
import 'package:flutter_wine/util/laser.dart';
import 'package:flutter_wine/util/laser_image.dart';
import 'package:flutter_wine/util/mixin/curve_fetch.dart';
import 'package:flutter_wine/util/mixin/evnetbus_mixin.dart';
import 'package:flutter_wine/widgets/bottom_black_box.dart';
import 'package:flutter_wine/widgets/plotter/view.dart';
import 'package:get/get.dart';
import 'package:get/get_rx/src/rx_types/rx_types.dart';
import 'package:get_storage/get_storage.dart';
import 'package:numdart/numdart.dart';
import 'dart:math' as Math;
import 'logic.dart';

class LaserCalibrationComponent extends StatefulWidget {
  @override
  LaserCalibrationComponentState createState() =>
      LaserCalibrationComponentState();
}

class LaserCalibrationComponentState extends State<LaserCalibrationComponent>
    with BaseEventBusMixin, StateEventBusReceiverMixin, CurveFetchMixin {
  final logic = Get.put(LaserCalibrationLogic());
  final state = Get.find<LaserCalibrationLogic>().state;

  List<int> spots = [];
  Color waterColor = Colors.deepPurpleAccent;

  num mathAbs(num n) {
    return n.abs();
  }

  @override
  void receiveEvent(message) {
    if (message == 'calibrate') {
      calibrate();
    }
  }

  void calibrate() async {
    Dbg.log('waterPeak', 'sdfasf');

    await (() async {
      var tuned = false;

      // var step=100;
      var step = 2;

      var stepForwardEnabled = 0;

      var successTime = 0;

      LaserController.instance
          .setTunedIntensityFor10(Configuration.defaultIntensity);
      LaserController.instance
          .setRealIntensity(LaserController.instance.tunedIntensityFor10);

      while (!tuned) {
        var tmpMax = -1;

        var result = await fetchCurve();
        var subbedPixels = result.curve;
        int waterPeak = 0;
        int waterRamanIndex = 0;
        for (int i = 200; i < 400; i++) {
          //水拉曼峰 3.28
          if (subbedPixels[i] > waterPeak) {
            waterPeak = subbedPixels[i];
            waterRamanIndex = i;
          }
        }
        int min_data = 999999999;
        for (int i = waterRamanIndex - 200; i < waterRamanIndex + 800; i++) {
          if (min_data > subbedPixels[i]) {
            min_data = subbedPixels[i];
          }
        }
        // step = max(1,step~/2);
        await LaserController.instance.close();

        // step = ([0,0,0,0,0,2])[Math.Random().nextInt(([0,0,0,0,0,2]).length)];
        Dbg.log(waterPeak, 'slafdkjksa');

        if (waterPeak > Configuration.waterRaman[1]) {
          await LaserController().setTunedIntensityFor10(
              LaserController.instance.tunedIntensityFor10 - step);
          successTime = 0;
        } else if (waterPeak < Configuration.waterRaman[0]) {
          await LaserController().setTunedIntensityFor10(
              LaserController.instance.tunedIntensityFor10 + step);
          successTime = 0;
        } else {
          successTime++;
          LogBoxClient.simple(
              '校准成功，成功次数 $successTime/${Configuration.calibrateTimes}');
          await Future.delayed(Duration(seconds: 1));
          if (successTime >= Configuration.calibrateTimes) {
            tuned = true;
          }
        }

        // spots = List.generate(white.length,
        //     (index) => FlSpot(index.toDouble(), white[index].toDouble()));

        spots = List.generate(subbedPixels.length, (index) {
          if (subbedPixels[index] > tmpMax) {
            tmpMax = subbedPixels[index];
          }
          return (subbedPixels[index]);
        });
        if (mounted) {
          setState(() {});
        } else {
          return;
        }
      }

      LogBoxClient.widget(Row(
        children: [
          Text(
            '校准完成,激光强度 ${LaserController.instance.tunedIntensityFor10}，是否重新校准?',
            style: LogBoxClient.logBoxTextStyle,
          ),
          TextButton(
            style: TextButton.styleFrom(
              padding: EdgeInsets.zero,
              // alignment: Alignment.centerLeft
            ),
            onPressed: () {
              fireEvent(LaserCalibrationComponentState, 'calibrate');
            },
            child: Text(
              '确定',
              style: LogBoxClient.logBoxTextStyle,
            ),
          )
        ],
      ));
    })();
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
            data: {
              'curve': spots,
            },
          ));
  }

  @override
  void dispose() {
    Get.delete<LaserCalibrationLogic>();
    super.dispose();
  }

  List<LineChartBarData> getWaterRamanLines() {
    return [
      LineChartBarData(
        spots: [
          // FlSpot(0.0, 0.0),
          // FlSpot(1.0, 0.0),
          // ...List.generate(i, (index) => FlSpot(index.toDouble(), sin(index))),
          FlSpot(0,
              (Configuration.waterRaman[0] + Configuration.waterRaman[1]) / 2),
          FlSpot(LaserImage.IMG_WIDTH.toDouble(),
              (Configuration.waterRaman[0] + Configuration.waterRaman[1]) / 2),
          // FlSpot(6.8, 3.1),
          // FlSpot(8, 4),
          // FlSpot(9.5, 3),
          // FlSpot(11, 4),
        ],
        isCurved: true,
        colors: [waterColor],
        barWidth: 2,
        isStrokeCapRound: true,
        dotData: FlDotData(
          show: false,
        ),
        // belowBarData: BarAreaData(
        //   show: true,
        //   colors:
        //   gradientColors.map((color) => color.withOpacity(0.7)).toList(),
        // ),
      ),
      // LineChartBarData(
      //   spots: [
      //     // FlSpot(0.0, 0.0),
      //     // FlSpot(1.0, 0.0),
      //     // ...List.generate(i, (index) => FlSpot(index.toDouble(), sin(index))),
      //     FlSpot(0, 16000),
      //     FlSpot(LaserImage.IMG_WIDTH.toDouble(), 16000),
      //     // FlSpot(6.8, 3.1),
      //     // FlSpot(8, 4),
      //     // FlSpot(9.5, 3),
      //     // FlSpot(11, 4),
      //   ],
      //   isCurved: true,
      //   colors: [waterColor],
      //   barWidth: 2,
      //   isStrokeCapRound: true,
      //   dotData: FlDotData(
      //     show: false,
      //   ),
      //   // belowBarData: BarAreaData(
      //   //   show: true,
      //   //   colors:
      //   //   gradientColors.map((color) => color.withOpacity(0.7)).toList(),
      //   // ),
      // )
    ];
  }
}
