import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:get_storage/get_storage.dart';

class ConfigSwitchListTileItem extends StatefulWidget {
  final String title;
  final String subtitle;
  const ConfigSwitchListTileItem({Key key, this.title, this.subtitle}) : super(key: key);

  @override
  _ConfigSwitchListTileItemState createState() => _ConfigSwitchListTileItemState();
}

class _ConfigSwitchListTileItemState extends State<ConfigSwitchListTileItem> {
  @override
  Widget build(BuildContext context) {
    return ListTile(
      title: Text(widget.title),
      subtitle: Text(widget.subtitle),
      trailing: Switch(
        value: GetStorage().read(widget.title) ?? false,
        onChanged: (v) async {
          await GetStorage().write(widget.title, v);
          if(mounted)
            setState(() {

          });
        },
      ),
    );
  }
}
