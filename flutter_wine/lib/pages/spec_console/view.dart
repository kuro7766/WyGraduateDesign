import 'package:flutter/material.dart';
import 'package:flutter_verification_box/verification_box.dart';
import 'package:flutter_wine/pages/device_image/view.dart';
import 'package:flutter_wine/util/getx_debug_tool.dart';
import 'package:flutter_wine/util/laser.dart';
import 'package:flutter_wine/widgets/debug_container.dart';
import 'package:get/get.dart';

import 'logic.dart';

class SpecConsoleComponent extends StatefulWidget {
  @override
  _SpecConsoleComponentState createState() => _SpecConsoleComponentState();
}

class _SpecConsoleComponentState extends State<SpecConsoleComponent> {
  final logic = Get.put(SpecConsoleLogic());
  final state = Get.find<SpecConsoleLogic>().state;

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Row(
        children: [
          Expanded(
            child: Obx(() {
              Dbg.log('rebuild${state.openState.value}');
              return InkWell(
                splashColor: Colors.red,
                onTap: () {
                  var target = !state.openState.value;
                  logic.laserSwitch(target);
                },
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Switch(
                      value: state.openState.value,
                      activeColor: Colors.red,
                      onChanged: (value) {
                        logic.laserSwitch(value);
                      },
                    ),
                    Text(
                      '光谱开关',
                      style: TextStyle(color: Colors.black),
                    ),
                    Text(
                      '小心激光!',
                      style: TextStyle(
                          color: Colors.red,
                          fontSize: 20,
                          fontWeight: FontWeight.bold),
                    )
                  ],
                ),
              );
            }),
            flex: 1,
          ),
          Expanded(
            flex: 1,
            child: DebugContainer(
              debugMode: false,
              child: LayoutBuilder(
                  builder: (BuildContext context, BoxConstraints constraints) {
                return Stack(
                  children: [
                    Obx(() {
                      return SizedBox(
                          width: constraints.maxWidth,
                          height: 100,
                          child: Center(
                              child: Text(
                            state.intensity.join(''),
                            style: TextStyle(fontSize: 25),
                          )));
                    }),
                    Padding(
                      padding: const EdgeInsets.only(top: 100),
                      child: LayoutBuilder(builder: (context, c) {
                        var width = .0, height = .0;
                        if (c.maxHeight * 0.75 > c.maxWidth) {
                          width = c.maxWidth;
                          height = c.maxWidth * 1.33;
                        } else {
                          width = c.maxHeight * 0.75;
                          height = c.maxHeight;
                        }
                        return Center(
                          child: Column(
                            mainAxisAlignment: MainAxisAlignment.end,
                            children: [
                              SizedBox(
                                width: c.maxHeight * 0.75,
                                height: c.maxHeight,
                                child: GridView.builder(
                                  shrinkWrap: true,
                                  itemBuilder: (c, i) {
                                    bool num = i < 9 || i == 10;
                                    if (i == 10) i = -1;
                                    return InkWell(
                                      onTap: () {
                                        if (num) {
                                          state.intensity.addIf(
                                              state.intensity.length <= 1,
                                              i + 1);
                                        } else if (i == 9) {
                                          if (state.intensity.isNotEmpty) {
                                            state.intensity.removeLast();
                                          }
                                        } else {
                                          logic.setIntensity();
                                        }
                                      },
                                      child: Padding(
                                        padding: const EdgeInsets.all(8.0),
                                        child: SizedBox(
                                          height: 10,
                                          child: Center(
                                            child: num
                                                ? Text(
                                              '${i + 1}',
                                              style:
                                              TextStyle(fontSize: 20),
                                            )
                                                : i == 9
                                                ? Icon(
                                              Icons.backspace,
                                            )
                                                : Obx(() {
                                              return state
                                                  .intensitySendingState
                                                  .value
                                                  ? SizedBox.square(
                                                dimension: 10,
                                                child:
                                                CircularProgressIndicator(
                                                  color:
                                                  Colors.red,
                                                ),
                                              )
                                                  : Icon(
                                                Icons.send,
                                              );
                                            }),
                                          ),
                                        ),
                                      ),
                                    );
                                  },
                                  itemCount: 12,
                                  gridDelegate:
                                      const SliverGridDelegateWithFixedCrossAxisCount(
                                          crossAxisCount: 3),
                                ),
                              ),
                            ],
                          ),
                        );
                      }),
                    ),
                  ],
                );
              }),
            ),
          ),
          Expanded(
            flex: 1,
            child: DeviceImageComponent(),
          )
        ],
      ),
    );
  }

  @override
  void dispose() {
    Get.delete<SpecConsoleLogic>();
    super.dispose();
  }
}
