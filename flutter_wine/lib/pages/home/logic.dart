import 'dart:convert';

import 'package:flutter/services.dart';
import 'package:flutter_wine/config.dart';
import 'package:flutter_wine/pages/auto_measure/view.dart';
import 'package:flutter_wine/pages/config/view.dart';
import 'package:flutter_wine/pages/curve_compare/view.dart';
import 'package:flutter_wine/pages/curve_viewer/view.dart';
import 'package:flutter_wine/pages/database/logic.dart';
import 'package:flutter_wine/pages/database/view.dart';
import 'package:flutter_wine/pages/device_image/logic.dart';
import 'package:flutter_wine/pages/device_image/view.dart';
import 'package:flutter_wine/pages/laser_calibration/view.dart';
import 'package:flutter_wine/pages/spec_console/view.dart';
import 'package:flutter_wine/pages/welcome/view.dart';
import 'package:flutter_wine/pages/wine_type/model/classify.dart';
import 'package:flutter_wine/pages/wine_type/model/regression.dart';
import 'package:flutter_wine/pages/wine_type/view.dart';
import 'package:flutter_wine/util/db/db_uploader.dart';
import 'package:flutter_wine/util/db/my_db.dart';
import 'package:flutter_wine/util/dialog.dart';
import 'package:flutter_wine/util/getx_debug_tool.dart';
import 'package:flutter_wine/util/inference_tool.dart';
import 'package:flutter_wine/util/laser.dart';
import 'package:flutter_wine/util/mixin/evnetbus_mixin.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:sqflite/sqflite.dart';
import 'package:url_launcher/url_launcher.dart';

import 'state.dart';
import 'package:http/http.dart' as http;
import 'package:platform_device_id/platform_device_id.dart';

class HomeLogic extends GetxController {
  final HomeState state = HomeState();

  @override
  void onInit() {
    super.onInit();
    //异步初始化内容
    (() async {
      await GetStorage().initStorage;

      await SpecDb.instance.init();

      RegressionPredict.instance.interfaces = [
        MLPRegression(),
      ];

      ClassificationPredict.instance.interfaces = [
        MlpClassifier(),
      ];
      // LaserController.instance.setTunedIntensityFor10(66);

      String deviceId = await PlatformDeviceId.getDeviceId;
      Dbg.log(deviceId,'dvc');
      Configuration.dvc = deviceId;

      if (GetStorage().read('连接网络')??false){
        // Uploader().autoUploadWine();
        // Uploader().autoUploadWineCurve();
        // Uploader().autoUploadWineCurveResult();
        // Uploader().autoUploadWineType();
        // Uploader().autoUploadWineTypeMap();
      }
    }());

    SystemChrome.setPreferredOrientations([
      // DeviceOrientation.landscapeLeft,
      DeviceOrientation.landscapeRight,
    ]);

    state.rightMenus = [
      {
        'text': '首页',
        'action': () {
          state.currentComponent
              .update((val) => val.widget = WelcomeComponent());
        }
      },
      {
        'text': '光谱仪控制台',
        'action': () {
          state.currentComponent
              .update((val) => val.widget = SpecConsoleComponent());
        }
      },
      // {
      //   'text': '自动测量',
      //   'action': () {
      //     state.currentComponent
      //         .update((val) => val.widget = AutoMeasureComponent());
      //   }
      // },
      // {'text': '激光配置', 'action': () {}},

      {
        'text': '实时光栅',
        'action': () {
          state.currentComponent
              .update((val) => val.widget = DeviceImageComponent());
        }
      },
      {
        'text': '智能检测',
        'action': () {
          state.currentComponent
              .update((val) => val.widget = WineTypeComponent());
        }
      },
      {
        'text': '实时光谱',
        'action': () {
          state.currentComponent
              .update((val) => val.widget = CurveViewerComponent());
        }
      },
      {
        'text': '激光校准',
        'action': () {
          state.currentComponent
              .update((val) => val.widget = LaserCalibrationComponent());
        }
      },
      {
        'text': '数据库',
        'action': () {
          state.currentComponent
              .update((val) => val.widget = DatabaseComponent());
        }
      },
      {
        'text': '曲线对比',
        'action': () {
          state.currentComponent
              .update((val) => val.widget = CurveCompareComponent());
        }
      },
      // {'text': '光谱仪日志', 'action': () {}},

      {
        'text': '应用配置',
        'action': () {
          state.currentComponent
              .update((val) => val.widget = ConfigComponent());
        }
      },
      {
        'text': '应用更新',
        'action': () {
              () async {
            DialogUtil.tip('检查更新中');
            await Future.delayed(Duration(seconds: 1));
            if(true){
              await Get.back();
              await DialogUtil.tip('已是最新版本');
            }
          }();


          // () async {
          //   DialogUtil.tip('检查更新中');
          //   var url =
          //       'http://${Configuration.domain}:${Configuration.port}/update?version=${Configuration.apkVersion}';
          //   var req=await http.get(Uri.parse(url));
          //   Dbg.log(req.body);
          //   var json=jsonDecode(req.body);
          //   if(json['code']==false){
          //     await Get.back();
          //     await Future.delayed(Duration(seconds: 1));
          //     await DialogUtil.tip(json['msg']);
          //     await Future.delayed(Duration(seconds: 1));
          //
          //   }else{
          //     var url =
          //         'http://${Configuration.domain}:${Configuration.port}/appdownload';
          //     launch(url);
          //   }
          // }();
        },
      }
    ];
  }

  @override
  void onReady() {
    super.onReady();
  }

  @override
  void onClose() {
    super.onClose();
  }
}
