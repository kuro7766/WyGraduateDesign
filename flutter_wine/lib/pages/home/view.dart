import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_wine/config.dart';
import 'package:flutter_wine/pages/auto_measure/view.dart';
import 'package:flutter_wine/widgets/obx_widget.dart';
import 'package:flutter_wine/widgets/bottom_black_box.dart';
import 'package:flutter_wine/widgets/debug_container.dart';
import 'package:flutter_wine/widgets/spect_connection_widget.dart';
import 'package:get/get.dart';

import 'logic.dart';
import 'package:matrix2d/matrix2d.dart';
import 'package:numdart/numdart.dart' as nd;

class HomePage extends StatelessWidget {
  final logic = Get.put(HomeLogic());
  final state = Get.find<HomeLogic>().state;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Configuration.background,
      body: SafeArea(
        child: Stack(
          children: [
            Obx(() {
              return Stack(
                children: [
                  Column(
                    children: [
                      Expanded(
                        flex: 100 -
                            (Configuration.bottomScrollSheetSize * 100).toInt(),
                        child: Padding(
                          padding: EdgeInsets.only(
                            right: Configuration.rightSize,
                            // bottom: Configuration.bottomSize
                          ),
                          child: state.currentComponent.value.widget,
                        ),
                      ),
                      Expanded(
                          flex: (Configuration.bottomScrollSheetSize * 100)
                              .toInt(),
                          child: Container()),
                    ],
                  ),
                  LogBox()
                ],
              );
            }),
            Align(
              alignment: Alignment.topRight,
              child: Padding(
                padding: EdgeInsets.only(right: 20),
                child: SpecConnectionWidget(),
              ),
            ),
            Align(
              alignment: Alignment.topRight,
              child: Padding(
                padding: EdgeInsets.only(top: 40),
                // padding: EdgeInsets.only(),
                child: SizedBox(
                  width: Configuration.rightSize,
                  child: Container(
                    color: Configuration.rightBarColor,
                    // padding: EdgeInsets.only(top:10,bottom: 10),
                    child: ObxObserveWidget([state.currentRightSelectionIndex],
                        () {
                      return ListView.builder(
                        controller: ScrollController(),
                        itemCount: state.rightMenus.length,
                        physics: BouncingScrollPhysics(
                            parent: AlwaysScrollableScrollPhysics()),
                        itemBuilder: (c, i) {
                          return GestureDetector(
                            onTap: () {
                              if (i == state.currentRightSelectionIndex.value) {
                                state.currentRightSelectionIndex.value = -1;
                                state.currentComponent
                                    .update((val) => val.widget = Container());

                                Timer(Duration(milliseconds: 300), () {
                                  state.currentRightSelectionIndex.value = i;
                                  state.rightMenus[i]['action']();
                                });
                              } else {
                                state.currentRightSelectionIndex.value = i;
                                state.rightMenus[i]['action']();
                              }
                            },
                            child: Container(
                              color: i == state.currentRightSelectionIndex.value
                                  ? Configuration.background
                                  : Configuration.rightBarColor,
                              child: Padding(
                                padding: const EdgeInsets.all(8.0),
                                child: Row(
                                  mainAxisAlignment: MainAxisAlignment.center,
                                  children: [
                                    Text(state.rightMenus[i]['text']),
                                  ],
                                ),
                              ),
                            ),
                          );
                        },
                      );
                    }),
                  ),
                ),
              ),
            )
          ],
        ),
      ),
    );
  }
}
