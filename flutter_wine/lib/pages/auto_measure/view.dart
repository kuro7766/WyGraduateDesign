import 'dart:async';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter_wine/util/getx_debug_tool.dart';
import 'package:flutter_wine/util/laser.dart';
import 'package:flutter_wine/util/laser_image.dart';
import 'package:flutter_wine/util/mixin/evnetbus_mixin.dart';
import 'package:flutter_wine/util/tflite/tf.dart';
import 'package:flutter_wine/widgets/bottom_black_box.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:process_run/shell.dart';

import 'logic.dart';
//debug页面，勿用
class AutoMeasureComponent extends StatefulWidget {
  @override
  AutoMeasureComponentState createState() => AutoMeasureComponentState();
}

class AutoMeasureComponentState extends State<AutoMeasureComponent>
    with BaseEventBusMixin, StateEventBusReceiverMixin {
  final logic = Get.put(AutoMeasureLogic());
  final state = Get.find<AutoMeasureLogic>().state;
  var i = 0;
  var isLoading = false;

  @override
  void receiveEvent(message) {
    Dbg.log('receive');
    // LaserController().connect();
    // LaserController.instance.open();
  }

  @override
  void initState() {
    super.initState();
    // Timer.periodic(Duration(milliseconds: 500), (timer) {
    //   if(mounted){
    //     i+=1;
    //     setState(() {
    //
    //     });
    //   }
    // });
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      child: Column(
        children: <Widget>[
          Text('测试页面'),
          // Text('$i'),
          // isLoading ? Text('加载模型中...') : CircularProgressIndicator(),
          RaisedButton(
            child: Text('start'),
            onPressed: () async {
              isLoading = true;
              if(mounted)

              setState(() {});
              await Future.delayed(Duration(milliseconds: 200));
              // await tfTest();
              cluster();
              if(mounted)

                setState(() {
                isLoading = false;
              });
              // await LaserController.instance.go();
              // Dbg.log('finish');
            },
          ),
          RaisedButton(
            child: Text('2'),
            onPressed: () async {
              // LaserController.instance.done();
            },
          ),
          RaisedButton(
            child: Text('swith2'),
            onPressed: () async {
              // await LaserController.instance.close();
              // Dbg.log('end');
              // Dbg.log();
              // ;
              // LogBoxClient().text((await Shell().run('dir')).map((e) => e.stdout));
              for (var i = 0; i < 10; i++) {
                LogBoxClient()
                    .text((await Process.run('cmd', ['/c', 'dir'])).stdout);
              }
            },
          ),
          RaisedButton(
            child: Text('s3'),
            onPressed: () async {
              // await LaserController.instance.close();
              // Dbg.log('end');
              // Dbg.log();
              // ;
              // LogBoxClient().text((await Shell().run('dir')).map((e) => e.stdout));
              Dbg.log(LaserController.instance.tunedIntensityFor10);

            },
          ),
        ],
      ),
    );
  }

  @override
  void dispose() {
    Get.delete<AutoMeasureLogic>();
    super.dispose();
  }
}
