import 'package:flutter_wine/util/getx_debug_tool.dart';
import 'package:flutter_wine/util/laser.dart';
import 'package:flutter_wine/util/mixin/evnetbus_mixin.dart';
import 'package:flutter_wine/widgets/bottom_black_box.dart';
import 'package:get/get.dart';

import 'state.dart';

class SpecConsoleLogic extends GetxController {
  final SpecConsoleState state = SpecConsoleState();

  @override
  void onInit() {
    super.onInit();
  }

  setIntensity() async {
    if (state.intensitySendingState.value) {
      return;
    }

    state.intensitySendingState.value = true;
    var i = 0;
    var sum = 0;
    while (i < state.intensity.length) {
      sum = sum * 10 + state.intensity[i];
      i++;
    }
    Dbg.log(sum);
    LogBoxClient logBoxClient = LogBoxClient();
    var success = await LaserController.instance.setRealIntensity(sum);
    logBoxClient.success('发送激光强度$sum', success);
    state.intensitySendingState.value = false;
    if (success) LaserController.instance.setTunedIntensityFor10(sum);
  }

  void laserSwitch(bool target) async {
    if (target) {
      var result = await LaserController.instance.open();
      if (result) {
        state.openState.value = target;
      } else {}
    } else {
      var result = await LaserController.instance.close();
      if (result) {
        state.openState.value = target;
      } else {}
    }
    Dbg.log(state.openState.value);
  }
}
