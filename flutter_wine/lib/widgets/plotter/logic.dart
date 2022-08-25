import 'package:fl_chart/fl_chart.dart';
import 'package:flutter_wine/util/getx_debug_tool.dart';
import 'package:get/get.dart';

import 'state.dart';

class PlotterLogic extends GetxController {
  final PlotterState state = PlotterState();

  int calculateMaxY(Map<String,List<int>> data){
    var scale=1.3;
    int maxY=-1;
    data.forEach((key,value){
      if(value.length>0){
        for (var value1 in value) {
          if(value1>maxY){
            maxY=value1;
          }
        }
      }
    });
    Dbg.log('$maxY','my');
    return (maxY*scale).toInt();
  }

  /// shape
  /// ['text',[0,1,2,3,4,5...]]
  /// ['text2',[0,1,2,3,4,5...]]
  List<dynamic> convertToSpotsMap(Map<String,List<int>> data){
    dynamic rt=[];
    data.forEach((key, value) {
      rt.add([key,value]);
    });
    return rt;
  }

  List<FlSpot> convertToSpots(List<int> data){
    List<FlSpot> rt=[];
    for (var i = 0; i < data.length; i++) {
      rt.add(FlSpot(i.toDouble(), data[i].toDouble()));
    }
    return rt;
  }
}
