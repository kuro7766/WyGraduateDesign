import 'dart:convert';
import 'dart:io';
import 'dart:typed_data';

import 'package:fl_chart/fl_chart.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_wine/config.dart';
import 'package:flutter_wine/util/db/my_db.dart';
import 'package:flutter_wine/util/dialog.dart';
import 'package:flutter_wine/util/getx_debug_tool.dart';
import 'package:flutter_wine/widgets/bottom_black_box.dart';
import 'package:get/get.dart';
import 'package:local_hero/local_hero.dart';
import 'package:path_provider/path_provider.dart';
import 'package:screenshot/screenshot.dart';
import 'package:share_plus/share_plus.dart';

import 'logic.dart';

class WineReportComponent extends StatefulWidget {
  final List<dynamic> possibilities;
  final List<String> names;
  final double degree;
  final String reportId;
  final String wineType;
  final List<int> curve;

  WineReportComponent(
      {Key key,
      this.possibilities,
      this.degree,
      this.names,
      this.reportId,
      this.curve,
      this.wineType})
      : super(key: key);

  @override
  _WineReportComponentState createState() => _WineReportComponentState();
}

class _WineReportComponentState extends State<WineReportComponent> {
  final logic = Get.put(WineReportLogic());
  final state = Get.find<WineReportLogic>().state;
  var tooltipSize = 30.0;
  String wineCurveFileId;
  ScreenshotController screenshotController = ScreenshotController();

  @override
  Widget build(BuildContext context) {
    return Screenshot(
      controller: screenshotController,
      child: Stack(
        children: [
          Align(
            alignment: Alignment.bottomRight,
            child: Padding(
              padding: const EdgeInsets.all(28.0),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.end,
                children: [
                  Row(
                    mainAxisAlignment: MainAxisAlignment.end,
                    children: [
                      IconButton(
                        icon: Icon(Icons.save),
                        onPressed: () async {
                          var inputs =
                              (await DialogUtil.displayTextInputDialog(
                                  ['名称', '白酒描述', '分类','曲线备注'],
                                  defaults: {0:widget.names[0],2: widget.wineType,3: DateTime.now().toString().substring(0,19)}));
                          if (inputs[0] != null && inputs[2] != null) {

                            //插入白酒名字和描述
                            var wineId = await SpecDb.instance
                                .queryWineByName(inputs[0]);

                            //没有的话插入新的
                            wineId ??= await SpecDb.instance.insertWine({
                              'name': inputs[0],
                              'desc': inputs[1],
                              'img': ''
                            });


                            Dbg.log('wineId:$wineId', 'wdi');
                            var queryItem = (await SpecDb()
                                .queryWineTypeIdByName(inputs[2]));
                            Dbg.log('queryItem:$queryItem', '2222');

                            // int.parse(queryItem['id']);
                            wineCurveFileId = await SpecDb.instance
                                .saveCurveResult(
                                    wineId, queryItem==null?null:queryItem['id'], widget.degree, widget.names[0], widget.curve,inputs[3]);
                          }
                        },
                      ),
                      IconButton(
                        icon: Icon(Icons.error_outline),
                        onPressed: () {
                          showCupertinoDialog(
                              context: context,
                              builder: (ctx) {
                                return new CupertinoAlertDialog(
                                  title: new Text("我要报错"),
                                  content: new Text("认为检测结果错误？"),
                                  actions: <Widget>[
                                    CupertinoDialogAction(
                                      isDefaultAction: true,
                                      child: Text("是"),
                                      onPressed: () async {
                                        if (wineCurveFileId == null) {
                                          await Get.back();
                                          // Get.snackbar('提示', '请先保存结果',);
                                          LogBoxClient.simple('报错失败,请先保存结果');
                                        } else {
                                          var wineResultPre = await SpecDb
                                              .instance
                                              .queryWineCurveResult(
                                                  wineCurveFileId);
                                          Map wineResult = json.decode(
                                              json.encode(wineResultPre));

                                          wineResult['is_error'] = 1;
                                          SpecDb.instance.updateWineCurveResult(
                                              wineResult);
                                        }
                                        Get.back();
                                      },
                                    ),
                                    CupertinoDialogAction(
                                      child: Text("否"),
                                      onPressed: () {
                                        Get.back();
                                      },
                                    )
                                  ],
                                );
                              });
                        },
                      ),
                      IconButton(
                        icon: Icon(Icons.ios_share),
                        onPressed: () {
                          screenshotController
                              .capture()
                              .then((Uint8List image) async {
                            Dbg.log('${image.lengthInBytes} bytes');
                            //    save image to file tmp.png
                            Directory tempDir = await getTemporaryDirectory();
                            String tempPath = tempDir.path;
                            File file = File('$tempPath/tmp.png');

                            await file.writeAsBytes(image);
                            await Share.shareFiles(['$tempPath/tmp.png']);
                          });
                        },
                      ),
                    ],
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.end,
                    children: [
                      Hero(
                        tag:'0',
                        child: Text('以上信息仅供参考，请以实际为准',
                            style: TextStyle(
                                fontSize: 10,
                                color: Colors.black,
                                fontFamily: 'Songti')),
                      ),
                    ],
                  ),
                ],
              ),
            ),
          ),
          Column(
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.start,
                children: [
                  Padding(
                    padding: const EdgeInsets.all(18.0),
                    child: Text(
                      '白酒报告',
                      style: TextStyle(
                          fontSize: 40,
                          fontWeight: FontWeight.normal,
                          fontFamily: 'Songti'),
                    ),
                  )
                ],
              ),
              Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Padding(
                    padding: const EdgeInsets.only(left: 18.0),
                    child: SizedBox(
                      height: 200,
                      width: 250,
                      child: ListView.separated(
                        separatorBuilder: (context, index) => Divider(
                          color: Colors.transparent,
                        ),
                        physics: BouncingScrollPhysics(
                            parent: AlwaysScrollableScrollPhysics()),
                        itemBuilder: (c, i) {
                          return Container(
                            // color: Colors.blueAccent,
                            child: SizedBox(
                              height: 35,
                              child: Row(
                                children: [
                                  Text(
                                    widget.names[i],
                                    style: TextStyle(fontFamily: 'Songti'),
                                  ),
                                  SizedBox(width: 10),
                                  Expanded(
                                      flex: (widget.possibilities[i] * 10000)
                                          .toInt(),
                                      child: Container(
                                        height: 15,
                                        decoration: BoxDecoration(
                                          borderRadius:
                                              BorderRadius.circular(30),
                                          color: Configuration.colorTable[i %
                                              Configuration.colorTable.length],
                                        ),
                                      )),
                                  Expanded(
                                    child: Container(),
                                    flex: (10000 -
                                            widget.possibilities[i] * 10000)
                                        .toInt(),
                                  ),
                                  Text(
                                    '${(widget.possibilities[i] * 100.0).toStringAsFixed(2)}%',
                                    style: TextStyle(fontFamily: 'Songti'),
                                  ),
                                ],
                              ),
                            ),
                          );
                        },
                        itemCount: widget.possibilities.length,
                      ),
                    ),
                  ),
                  SizedBox(
                    width: 30,
                  ),
                  SizedBox(
                    height: 200,
                    width: 200,
                    child: Builder(builder: (context) {
                      var length = 0;
                      var threshold = 0.15;
                      widget.possibilities.firstWhere((element) {
                        length += 1;
                        return element < threshold;
                      });
                      length -= 1;

                      //length's right possibilities sum
                      var leftPossibilitiesSum = 0.0;
                      for (int i = 0; i < widget.possibilities.length; i++) {
                        if (widget.possibilities[i] < threshold) {
                          leftPossibilitiesSum += widget.possibilities[i];
                        }
                      }
                      return PieChart(
                        PieChartData(
                          // read about it in the PieChartData section
                          sections: [
                            PieChartSectionData(
                              color: Colors.grey,
                              value: leftPossibilitiesSum,
                              radius: 100,
                              title: '其他',
                              badgeWidget: Tooltip(
                                message: '其他 ${leftPossibilitiesSum * 100}%',
                                child: Container(
                                  color: Colors.transparent,
                                  width: tooltipSize,
                                  height: tooltipSize,
                                ),
                              ),
                              titleStyle: TextStyle(
                                fontSize: 20,
                                color: Colors.white,
                                fontFamily: 'Songti',
                              ),
                            ),
                            ...List.generate(
                              length,
                              (index) => PieChartSectionData(
                                color: Configuration.colorTable[
                                    index % Configuration.colorTable.length],
                                value: widget.possibilities[index],
                                radius: 100,
                                title: widget.names[index],
                                badgeWidget: Tooltip(
                                  message:
                                      '${widget.names[index]} ${widget.possibilities[index] * 100}%',
                                  child: Container(
                                    color: Colors.transparent,
                                    width: tooltipSize,
                                    height: tooltipSize,
                                  ),
                                ),
                                titleStyle: TextStyle(
                                  fontSize: 20,
                                  color: Colors.white,
                                  fontFamily: 'Songti',
                                ),
                              ),
                            )
                          ],
                        ),
                        swapAnimationDuration:
                            Duration(milliseconds: 500), // Optional
                      );
                    }),
                  ),
                  SizedBox(
                    width: 30,
                  ),
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        '白酒度数: ${widget.degree}°',
                        style: TextStyle(fontSize: 20, fontFamily: 'Songti'),
                      ),
                      SizedBox(height: 10),
                      Text(
                        '${widget.wineType}',
                        style: TextStyle(fontSize: 20, fontFamily: 'Songti'),
                      ),
                    ],
                  ),
                ],
              )
            ],
          ),
        ],
      ),
    );
  }

  @override
  void dispose() {
    Get.delete<WineReportLogic>();
    super.dispose();
  }
}
