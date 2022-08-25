import 'dart:math';

import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_wine/config.dart';
import 'package:flutter_wine/util/getx_debug_tool.dart';
import 'package:flutter_wine/util/inference_tool.dart';
import 'package:flutter_wine/util/tflite/tf.dart';
import 'package:flutter_wine/widgets/bottom_black_box.dart';
import 'package:flutter_wine/widgets/wine_report/view.dart';
import 'package:get/get.dart';

import 'state.dart';

class WineTypeLogic extends GetxController {
  final WineTypeState state = WineTypeState();

  //预测模型

  // 输入曲线图并作UI展示，int类型是因为这样图像RGB运算更快，但是在输入模型的时候需要转成double
  void prediction(List<int> input) async {
    // List d = await TfliteRunner('assets/model/converted_model.tflite')
    //     .runBatchOne(input, 15);

    // input=input.sublist(0,1000);

    Dbg.log('classify ${input.length}', 'cls');
    //获取所有分类模型的输出结果,将不同的概率取平均值，合并到一个列表并展示
    List listOfPossibilities=await ClassificationPredict.instance.predict(input.map((e) => e.toDouble()).toList());

    Dbg.log(listOfPossibilities);

    //获取最大概率的分类
    dynamic typeResult =await  TfliteRunner(
      'assets/model/smell.tflite',
    ).runBatchOne(input.map((e) => e.toDouble()).toList(), Configuration.smell.length);
    Dbg.log('typeResult $typeResult', 'cls');
    var maxSmellIdx =0;
    var maxProb =0.0;
    for (var i = 0; i < (typeResult as List<dynamic>).length; i++) {
      if ((typeResult as List<dynamic>)[i] > maxProb) {
        maxSmellIdx = i;
        maxProb = (typeResult as List<dynamic>)[i];
      }
    }
    Dbg.log('maxSmellIdx $maxSmellIdx', 'cls');

    if(listOfPossibilities.isEmpty){
      LogBoxClient.simple('当前未选中任何模型');
      return;
    }

    //对多个模型结果如何处理？平均值还是voting stacking?
    List possibilities=listOfPossibilities[0];

    //将输出下标和概率排序,展示概率最大的前三个
    List<dynamic> collects = [];
    for (int i = 0; i < possibilities.length; i++) {
      collects.add([i, possibilities[i]]);
    }
    collects.sort((a, b) => b[1].compareTo(a[1]));
    var s = [];
    // for (int i = 0; i < collects.length; i++) {
    for (int i = 0; i < 3; i++) {
      s.add(
          '${Configuration.wines[collects[i][0]]}:${((collects[i][1]) * 100).toStringAsFixed(2)}%');
    }

    //以上为分类任务

    Dbg.log('before ${input.length}','reg');
    //回归任务,预测白酒的度数
    List<double> regressions=await RegressionPredict.instance.predict(input.map((e) => e.toDouble()).toList());

    //获取平均的输出
    double average=regressions.reduce((a,b)=>a+b)/regressions.length;

    //获取最大最小值
    double maxReg=regressions.reduce((a,b)=>max(a,b));
    double minReg=regressions.reduce((a,b)=>min(a,b));

    //获取标准差
    double variance =0 ;
    regressions.forEach((e){
      variance+=pow(e-average,2);
    });
    variance/=regressions.length;
    double standardDeviation=sqrt(variance);

    //打印上述
    Dbg.log('average:$average,max:$maxReg,min:$minReg,variance:$variance,standardDeviation:$standardDeviation','reg');



    LogBoxClient.simple('当前酒的预测结果为:');
    Dbg.log(s.join(", "));
    LogBoxClient.widget(Row(
      children: [
        Text(
          '- ${s.join(", ")}',
          style: LogBoxClient.logBoxTextStyle,
        ),
        TextButton(
            onPressed: () {
              state.builder = (context) {
                return WineReportComponent(
                  names: collects.map((e) => Configuration.wines[e[0]]).toList(),
                  possibilities: collects.map((e) => e[1]*1.0).toList(),
                  //,
                  degree: (((average)*10000).abs().toInt()/100.0),
                  curve: input,
                  wineType: Configuration.smell[maxSmellIdx],
                );
              };
              state.index.value = 1;
            },
            child: Text('查看详情'))
      ],
    ));
  }
}
