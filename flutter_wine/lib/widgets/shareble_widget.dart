import 'dart:io';
import 'dart:typed_data';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:path_provider/path_provider.dart';
import 'package:screenshot/screenshot.dart';
import 'package:share_plus/share_plus.dart';

class Sharable extends StatefulWidget {
   Sharable({Key key,this.child}) : super(key: key);
  Widget child;

  @override
  _SharableState createState() => _SharableState();

  static Widget shareButton(){
    return InkWell(
      child:  Row(
        mainAxisAlignment: MainAxisAlignment.end,
        children: [Icon(Icons.ios_share), Text('分享')],
      ),
      onTap: () {
        Get.find<ScreenshotController>()
            .capture()
            .then((Uint8List image) async {
          // Dbg.log('${image.lengthInBytes} bytes');
          //    save image to file tmp.png
          Directory tempDir = await getTemporaryDirectory();
          String tempPath = tempDir.path;
          File file = File('$tempPath/tmp.png');

          await file.writeAsBytes(image);
          await Share.shareFiles(['$tempPath/tmp.png']);
        });
      },
    );
  }
}

class _SharableState extends State<Sharable> {
  ScreenshotController screenshotController = Get.put(ScreenshotController());
  @override
  Widget build(BuildContext context) {
    return Stack(
      children: [
        Screenshot(
          child: widget.child,
          controller: screenshotController,
        ),
      ],
    );
  }
}
