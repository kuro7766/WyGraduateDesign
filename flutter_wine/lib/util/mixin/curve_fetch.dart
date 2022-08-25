
import 'package:fl_chart/fl_chart.dart';
import 'package:flutter/material.dart';
import 'package:flutter_wine/pages/laser_calibration/view.dart';
import 'package:flutter_wine/util/app_tool.dart';
import 'package:flutter_wine/util/async_mutex.dart';
import 'package:flutter_wine/widgets/bottom_black_box.dart';

import '../../config.dart';
import '../laser.dart';
import '../laser_image.dart';
import 'evnetbus_mixin.dart';

class CurveData {
  List<int> curve;
  dynamic extra;
  int max;
  int min;
  int waterRamanIndex;

  CurveData({this.curve, this.extra, this.max, this.min,this.waterRamanIndex});
}

class CurveFetchMixin {
  num mathAbs(num n) {
    return n.abs();
  }

  Future<CurveData> fetchCurve() async {
    return await AppTool.instance.fetchCurveMutex.run(() async {
      var breakFlag = false;

      // var step=100;
      var step = 2;

      while (!breakFlag) {
        breakFlag = true;
        var tmpMax = -1;

        int n = 5;
        await LaserController.instance.close();

        await Future.delayed(
            Duration(seconds: Configuration.toggleLaserTimeGap));
        var black = await LaserImage().fetchAvgCurve(n);
        await LaserController.instance.open();

        await Future.delayed(
            Duration(seconds: Configuration.toggleLaserTimeGap));
        var white = await LaserImage().fetchAvgCurve(n);
        for (var i = 0; i < LaserImage.IMG_WIDTH; i++) {
          white[i] = white[i] - black[i];
        }

        var subbedPixels = white;

        int min_temp = 999999999, max_temp = -999999999;
        for (int b = 1; b < LaserImage.IMG_WIDTH; b++) {
          if (subbedPixels[b] < min_temp) {
            min_temp = subbedPixels[b];
          }
          if (subbedPixels[b] > max_temp) {
            max_temp = subbedPixels[b];
          }
        }

        /**
         * 几种亮灭相减的可能结果
         */

        if (min_temp < 0 && max_temp < 0) {
          for (int b = 0; b < LaserImage.IMG_WIDTH; b++) {
            subbedPixels[b] = mathAbs(subbedPixels[b]) - mathAbs(max_temp);
          }
        }
        if (min_temp > 0 && max_temp > 0) {
          for (int b = 0; b < LaserImage.IMG_WIDTH; b++) {
            subbedPixels[b] = subbedPixels[b] - min_temp;
          }
        }
        if (min_temp < 0 &&
            max_temp > 0 &&
            mathAbs(min_temp) > mathAbs(max_temp)) {
          for (int b = 0; b < LaserImage.IMG_WIDTH; b++) {
            subbedPixels[b] = mathAbs(subbedPixels[b] - max_temp);
          }
        }
        if (min_temp < 0 &&
            max_temp > 0 &&
            mathAbs(min_temp) < mathAbs(max_temp)) {
          for (int b = 0; b < LaserImage.IMG_WIDTH; b++) {
            subbedPixels[b] = subbedPixels[b] + mathAbs(min_temp);
          }
        }

        int windowSum = 0;
        int window = 10;
        for (int i = 0; i < LaserImage.IMG_WIDTH - window; i++) {
          for (int j = 0; j < window; j++) {
            windowSum += subbedPixels[i + j];
          }
          subbedPixels[i] = windowSum ~/ window;
          windowSum = 0;
        }
        for (int j = 0; j < window; j++) {
          windowSum += subbedPixels[LaserImage.IMG_WIDTH - window + 1];
        }
        int avg = windowSum ~/ window;
        for (int i = LaserImage.IMG_WIDTH - window;
            i < LaserImage.IMG_WIDTH;
            i++) {
          subbedPixels[i] = avg;
        }
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
        int max_data = -999999999;
        for (int i = waterRamanIndex - 200; i < waterRamanIndex + 800; i++) {
          if (min_data > subbedPixels[i]) {
            min_data = subbedPixels[i];
          }
          if (max_data < subbedPixels[i]) {
            max_data = subbedPixels[i];
          }
        }
        // step = max(1,step~/2);
        await LaserController.instance.close();

        return CurveData(
            curve: subbedPixels, extra: null, max: max_data, min: min_data,waterRamanIndex: waterRamanIndex);
      }
    });
  }
}
