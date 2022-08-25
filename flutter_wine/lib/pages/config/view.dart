import 'package:flutter/material.dart';
import 'package:flutter_wine/widgets/cfg_switch.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

import 'logic.dart';

class ConfigComponent extends StatefulWidget {
  @override
  _ConfigComponentState createState() => _ConfigComponentState();
}

class _ConfigComponentState extends State<ConfigComponent> {

  //config switch widgets
  Widget _configList() {
    return ListView(
      children: [
        ConfigSwitchListTileItem(
          title:'手动切换',
          subtitle: '是否手动切换激光',
        ),
        ConfigSwitchListTileItem(
          title:'连接网络',
          subtitle: '联网时自动上传数据，检查更新',
        ),
        ConfigSwitchListTileItem(
          title:'自动连接',
          subtitle: '打开光谱以后自动连接',
        ),
      ],
    );
  }

  @override
  Widget build(BuildContext context) {
    return _configList();
  }

  @override
  void dispose() {
    Get.delete<ConfigLogic>();
    super.dispose();
  }
}
