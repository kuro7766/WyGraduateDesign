import 'dart:async';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_wine/pages/auto_measure/view.dart';
import 'package:flutter_wine/util/getx_debug_tool.dart';
import 'package:flutter_wine/util/mixin/evnetbus_mixin.dart';
import 'package:flutter_wine/util/spectrometer_tool.dart';
import 'package:flutter_wine/widgets/bottom_black_box.dart';
import 'package:flutter_wine/widgets/debug_container.dart';
import 'package:get/get.dart';
import 'package:get/get_navigation/src/snackbar/snackbar.dart';
import 'package:get_storage/get_storage.dart';

import '../urls.dart';

class SpecConnectionWidget extends StatefulWidget {
  const SpecConnectionWidget({Key key}) : super(key: key);

  @override
  _SpecConnectionWidgetState createState() => _SpecConnectionWidgetState();
}

class _SpecConnectionWidgetState extends State<SpecConnectionWidget>
    with WidgetsBindingObserver {
  var loading = false;
  var connected = false;

  @override
  void initState() {
    super.initState();
    Dbg.log('init ');
    WidgetsBinding.instance.addPostFrameCallback((_) {
      // executes after build
      if(GetStorage().read('自动连接')??true){
        _tryConnect();
      }
    });
  }

  _tryConnect() {
    loading = !loading;
    if (mounted) setState(() {});

    if (loading) {
      var log=LogBoxClient(useUUidTag: true);
      log.text('开始扫描光谱仪ip');
      SpectrometerTool().getSpectrumIp((ip) {
        Urls.strIp = ip;
        Dbg.log('ip:$ip');
        // Get.snackbar('ip扫描结果✅', '光谱仪:$ip', snackPosition: SnackPosition.BOTTOM);
        connected = true;
        loading = false;
        if (mounted) setState(() {});
        fireEventToMany([AutoMeasureComponentState], 0);
        log.text('连接光谱仪成功');
      }, onNotFoundCallback: () {
        // Get.snackbar('ip扫描结果❌', '没有找到光谱仪', snackPosition: SnackPosition.BOTTOM);
        connected = false;
        loading = false;
        if (mounted) setState(() {});

        log.text('没有扫描到光谱仪ip');

      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return DebugContainer(
      debugMode: false,
      child: IntrinsicWidth(
        child: Row(
          children: [
            Icon(
              Icons.circle,
              color: connected ? Colors.green : Colors.red,
              size: 15,
            ),
            SizedBox(
              width: 10,
            ),
            SizedBox(
              width: 30,
              height: 40,
              child: Center(
                child: loading
                    ? SizedBox(
                        width: 20,
                        height: 20,
                        child: CircularProgressIndicator())
                    : IconButton(
                        onPressed: _tryConnect,
                        icon: Icon(Icons.wifi),
                      ),
              ),
            )
          ],
        ),
      ),
    );
  }
}
