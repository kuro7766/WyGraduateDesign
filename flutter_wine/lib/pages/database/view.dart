import 'package:flutter/material.dart';
import 'package:flutter_wine/util/getx_debug_tool.dart';
import 'package:flutter_wine/util/mixin/evnetbus_mixin.dart';
import 'package:flutter_wine/widgets/scroll_with_list/view.dart';
import 'package:flutter_wine/widgets/wine_card.dart';
import 'package:flutter_wine/widgets/wine_list/view.dart';
import 'package:get/get.dart';

import 'logic.dart';

class DatabaseComponent extends StatefulWidget {
  @override
  DatabaseComponentState createState() => DatabaseComponentState();
}

class DatabaseComponentState extends State<DatabaseComponent>
    with BaseEventBusMixin, StateEventBusReceiverMixin {
  final logic = Get.put(DatabaseLogic());
  final state = Get.find<DatabaseLogic>().state;

  @override
  void receiveEvent(message) {
    if (message == 'refresh') {
      Dbg.log('DatabaseComponentState receiveEvent refresh');
      setState(() {});
    }
  }

  @override
  Widget build(BuildContext context) {
    return WineListComponent();
  }

  @override
  void dispose() {
    Get.delete<DatabaseLogic>();
    super.dispose();
  }
}
