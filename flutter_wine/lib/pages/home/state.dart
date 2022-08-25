import 'package:flutter/cupertino.dart';
import 'package:flutter_wine/pages/auto_measure/view.dart';
import 'package:flutter_wine/pages/laser_calibration/view.dart';
import 'package:flutter_wine/pages/spec_console/view.dart';
import 'package:flutter_wine/pages/welcome/view.dart';
import 'package:flutter_wine/widgets/wine_report/view.dart';
import 'package:get/get_rx/src/rx_types/rx_types.dart';

class HomeState {
  var currentRightSelectionIndex = 0.obs;
  List rightMenus = [];
  // var currentComponent = WidgetHolder(LaserCalibrationComponent()).obs;
  var currentComponent = WidgetHolder(WelcomeComponent()).obs;
  // var currentComponent = WidgetHolder(WineReportComponent(possibilities: [0.3,0.2,0.1,0.4],names: ['A',"b",'c','d'],)).obs;
  HomeState() {
    ///Initialize variables
  }
}

class WidgetHolder{
  Widget widget;
  WidgetHolder(this.widget);

}
