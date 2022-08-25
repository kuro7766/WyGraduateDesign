import 'package:fl_chart/fl_chart.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

import 'logic.dart';

class PlotterComponent extends StatefulWidget {

  final Map<String,List<int>> data;
  final diableShadow;

  PlotterComponent({Key key, this.data, this.diableShadow=false}) : super(key: key);

  @override
  _PlotterComponentState createState() => _PlotterComponentState();
}

class _PlotterComponentState extends State<PlotterComponent> {
  final logic = Get.put(PlotterLogic());
  final state = Get.find<PlotterLogic>().state;

  var paddingSize = 20.0;

  List<Color> gradientColors = [
    const Color(0xff23b6e6),
    const Color(0xff02d39a),
    const Color(0xffff0000),
    const Color(0xffff0088),
    Color(0xff1abc9c),
    Color(0xff16a085),
    Color(0xff2ecc71),
    Color(0xff27ae60),
    Color(0xff3498db),
    Color(0xff2980b9),
    Color(0xff9b59b6),
    Color(0xff8e44ad),
    Color(0xff34495e),
    Color(0xff2c3e50),
    Color(0xfff1c40f),
    Color(0xfff39c12),
    Color(0xffe67e22),
    Color(0xffd35400),
    Color(0xffe74c3c),
    Color(0xffc0392b),
    Color(0xffecf0f1),
    Color(0xffbdc3c7),
    Color(0xff95a5a6),
    Color(0xff7f8c8d),
  ];
  @override
  Widget build(BuildContext context) {

    var listInfo=logic.convertToSpotsMap(widget.data);
    return ( Stack(
      children: [
        Padding(
          padding: EdgeInsets.only(bottom: paddingSize, top: paddingSize),
          child: LineChart(
            LineChartData(
              minY:0,
              maxY:logic.calculateMaxY(widget.data).toDouble(),
              // read about it in the LineChartData section
              lineBarsData: List.generate(listInfo.length, (index) => spotsCurve(index,logic.convertToSpots(listInfo[index][1]))),
            ),
            swapAnimationDuration:
            Duration(milliseconds: 150), // Optional
            swapAnimationCurve: Curves.linear, // Optional
          ),
        ),
        Align(
          alignment: Alignment.topRight,
          child: Container(
            color: Colors.white70,
            child: Padding(
              padding: const EdgeInsets.all(8.0),
              child: IntrinsicHeight(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: List.generate(listInfo.length , (index) => labelText(index,listInfo[index][0]))
                ),
              ),
            ),
          ),
        )
      ],
    ));
  }

  LineChartBarData spotsCurve(int index,List<FlSpot> spots) {
    return LineChartBarData(
      spots: spots,
      isCurved: true,
      colors: gradientColors.sublist(index*2,index*2+1),
      barWidth: 2,
      isStrokeCapRound: true,
      dotData: FlDotData(
        show: false,
      ),
      belowBarData: widget.diableShadow?null: BarAreaData(
        show: true,
        colors: gradientColors.sublist(index*2,index*2+2)
            .map((color) => color.withOpacity(0.3))
            .toList(),
      ),
    );
  }

  Widget labelText(index,String text){
    return IntrinsicWidth(
      child: Row(
        mainAxisAlignment: MainAxisAlignment.end,
        children: [
          Container(
            color: gradientColors[index*2],
            width: 20,
            height: 4,
          ),
          SizedBox(width: 5),
          Text(text)
        ],
      ),
    );
  }
  @override
  void dispose() {
    Get.delete<PlotterLogic>();
    super.dispose();
  }
}