import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class DialogUtil {
  static Future iosConfirm(String title) async {
    var result = false;
    await showCupertinoDialog(
        context: Get.context,
        builder: (ctx) {
          return new CupertinoAlertDialog(
            title: new Text(title),
            // content: new Text("认为检测结果错误？"),
            actions: <Widget>[
              CupertinoDialogAction(
                isDefaultAction: true,
                child: Text("是"),
                onPressed: () {
                  result = true;
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
    return result;
  }

  static Future tip(String title) async {
    var result = false;
    return showCupertinoDialog(
        barrierDismissible: true,
        context: Get.context,
        builder: (ctx) {
          return new CupertinoAlertDialog(
            title: new Text(title),
          );
        });
  }


  static Future widget(Widget widget) async {
    var result = false;
    return showCupertinoDialog(
        barrierDismissible: true,
        context: Get.context,
        builder: (ctx) {
          return new AlertDialog(
            title: widget,
          );
        });
  }

  static Future<List<String>> displayTextInputDialog(List<String> forms,
      {Map<int, String> defaults}) async {
    var controllers =
        List.generate(forms.length, (index) => TextEditingController());
    var sure = false;
    if (defaults != null) {
      for (var i = 0; i < forms.length; i++) {
        controllers[i].text = defaults[i];
      }
    }
    await showCupertinoDialog(
      context: Get.context,
      // barrierDismissible: true,
      builder: (context) {
        return Scaffold(
          backgroundColor: Colors.transparent,
          resizeToAvoidBottomInset: true,
          body: CupertinoAlertDialog(
            actions: <Widget>[
              CupertinoDialogAction(
                isDefaultAction: true,
                child: Text("是"),
                onPressed: () {
                  sure = true;
                  Get.back();
                },
              ),
              CupertinoDialogAction(
                child: Hero(tag: '0',child: Text("否")),
                onPressed: () {
                  sure = false;
                  Get.back();
                },
              )
            ],
            title: Text('输入信息'),
            content: Column(
              children: List.generate(forms.length, (index) {
                return SizedBox(
                  height: 49,
                  child: CupertinoTextFormFieldRow(
                    controller: controllers[index],
                    placeholder: forms[index],
                    // decoration: InputDecoration(
                    //   labelText: forms[index],
                    // ),
                  ),
                );
              }),
            ),
          ),
        );
      },
    );

    if (sure) {
      return controllers.map((controller) => controller.text).toList();
    } else {
      return controllers.map((e) => null).toList();
    }
  }
}
