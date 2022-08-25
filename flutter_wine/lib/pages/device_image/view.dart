import 'dart:async';
import 'dart:io';
import 'dart:math';

import 'package:flutter/material.dart';
import 'package:flutter_wine/urls.dart';
import 'package:flutter_wine/util/getx_debug_tool.dart';
import 'package:get/get.dart';
import 'package:video_player/video_player.dart';
import 'package:webview_flutter/webview_flutter.dart';

import 'logic.dart';
import 'package:chewie/chewie.dart';

class DeviceImageComponent extends StatefulWidget {
  @override
  _DeviceImageComponentState createState() => _DeviceImageComponentState();
}

class _DeviceImageComponentState extends State<DeviceImageComponent> {
  final logic = Get.put(DeviceImageLogic());
  final state = Get
      .find<DeviceImageLogic>()
      .state;

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    //random

    Dbg.log(Urls.streamUrl);
    return Platform.isAndroid?WebView(
      initialUrl: Urls.streamUrl,
    ):Container();
  }

  @override
  void dispose() {
    Get.delete<DeviceImageLogic>();
    super.dispose();
  }
}