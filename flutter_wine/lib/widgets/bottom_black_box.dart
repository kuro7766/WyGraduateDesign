import 'dart:async';
import 'dart:ui';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_wine/config.dart';
import 'package:flutter_wine/util/getx_debug_tool.dart';
import 'package:flutter_wine/util/mixin/evnetbus_mixin.dart';

import 'debug_container.dart';

class LogBoxClient {
  bool __useUUidTag;
  static int __tagNum = 0;
  int __uuidTag;

  static const logBoxTextStyle = TextStyle(color: Colors.white);

//  constructor
  LogBoxClient({useUUidTag = false}) {
    __useUUidTag = useUUidTag;
    if (__useUUidTag) {
      __uuidTag = __tagNum++;
    }
  }

  String __logTagString(String log) {
    return (__useUUidTag ? '[任务编号 $__uuidTag] ' : '') + log;
  }

  static simple(String log) {
    return LogBoxClient().text(log);
  }

  static widget(Widget w) {
    fireEvent(LogBoxState, [1, w]);
  }

  void text(dynamic log) {
    fireEvent(LogBoxState, [0, __logTagString('$log')]);
  }

  void success(String log, bool success) {
    fireEvent(LogBoxState, [0, __logTagString(log) + (success ? '成功' : '失败')]);
  }
}

class LogBox extends StatefulWidget {
  // static int text = 0;
  // static int widget = 1;

  const LogBox({Key key}) : super(key: key);

  @override
  LogBoxState createState() => LogBoxState();
}

class LogBoxState extends State<LogBox>
    with
        SingleTickerProviderStateMixin,
        BaseEventBusMixin,
        StateEventBusReceiverMixin {
  ScrollController _scrollController;
  List<Widget> logs = [];
  var currentExpandRatio = Configuration.bottomScrollSheetSize;
  bool expanding = false;
  AnimationController controller;
  bool scrollBottomEnabled = true;

  void _scrollDown() {
    if (scrollBottomEnabled && _scrollController.hasClients) {
      Timer(Duration(milliseconds: 100), () {
        _scrollController.jumpTo(
          _scrollController.position.maxScrollExtent,
        );
      });
    }
  }

  void openScrollBottomMechanism() {
    scrollBottomEnabled = true;
    _scrollDown();
    if (mounted) setState(() {});
  }

  void closeScrollBottomMechanism() {
    scrollBottomEnabled = false;
    if (mounted) setState(() {});
  }

  @override
  void receiveEvent(message) {
    if (message is List) {
      if (message[0] == 0) {
        if (mounted)
          setState(() {
            addTextLog((message[1]));
          });
      }
      if (message[0] == 1) {
        if (mounted)
          setState(() {
            addLog(message[1]);
          });
      }
    }
  }

  @override
  void initState() {
    super.initState();
    _scrollController = ScrollController();
    // _scrollController = ScrollController();
    controller = AnimationController(
        duration: const Duration(milliseconds: 200), vsync: this);
    Animation expandAnim;
    expandAnim = Tween(
            begin: Configuration.bottomScrollSheetSize,
            end: Configuration.bottomScrollMaxSize)
        .animate(
            CurvedAnimation(parent: controller, curve: Curves.fastOutSlowIn))
      ..addListener(() {
        // Dbg.log('r');      if(mounted)
        setState(() {
          currentExpandRatio = expandAnim?.value;
        });
      });
    // addTextLog('Log:');
  }

  void addLog(Widget widget) {
    logs.add(widget);
    if (mounted) setState(() {});

    _scrollDown();
  }

  void addTextLog(String log) {
    logs.add(
      Text(
        '$log' ?? '',
        style: TextStyle(color: Colors.white),
      ),
    );
    if (mounted) setState(() {});

    _scrollDown();
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Expanded(
          child: Container(),
          flex: 100 - (currentExpandRatio * 100).toInt(),
        ),
        Expanded(
          flex: (currentExpandRatio * 100).toInt(),
          child: Padding(
            padding: EdgeInsets.only(
                left: 8.0, right: Configuration.rightSize + 8.0),
            child: Container(
              // color: Colors.red,
              child: Stack(
                children: [
                  Padding(
                    padding: EdgeInsets.only(top: 10, bottom: 5),
                    child: GestureDetector(
                      onPanDown: (d) {
                        Dbg.log('pd');
                        if (expanding) {
                          closeScrollBottomMechanism();
                        }
                      },
                      child: RawScrollbar(
                        controller: _scrollController,
                        thumbColor: Colors.grey,
                        radius: Radius.circular(20),
                        thickness: 20,
                        child: ListView.builder(
                          physics: BouncingScrollPhysics(),
                          controller: _scrollController,
                          itemCount: logs.length,
                          itemBuilder: (BuildContext context, int index) {
                            return Padding(
                                padding: const EdgeInsets.only(left: 24.0),
                                child: logs[index]
                                // Text(
                                //   // '光谱日志$index',
                                //   '${logs[index]}',
                                //   style: TextStyle(color: Colors.white),
                                // ),
                                );
                          },
                        ),
                      ),
                    ),
                  ),
                  Align(
                    alignment: Alignment.topRight,
                    child: Padding(
                      padding: const EdgeInsets.all(8.0),
                      child: DebugContainer(
                        debugMode: false,
                        child: InkWell(
                          splashColor: Colors.white,
                          highlightColor: Colors.white,
                          hoverColor: Colors.white,
                          onTap: () {
                            if (!expanding) {
                              controller.forward();
                              expanding = true;
                            } else {
                              controller.reverse();
                              expanding = false;
                              Timer(Duration(milliseconds: 500), () {
                                openScrollBottomMechanism();
                              });
                            }
                          },
                          child: Padding(
                            padding:
                                const EdgeInsets.only(left: 50, bottom: 20),
                            child: Icon(
                              expanding
                                  ? Icons.keyboard_arrow_down
                                  : Icons.keyboard_arrow_up,
                              color: Colors.white,
                              size: 30,
                            ),
                          ),
                        ),
                      ),
                    ),
                  ),
                  Visibility(
                    visible: expanding && !scrollBottomEnabled,
                    child: Align(
                      alignment: Alignment.bottomRight,
                      child: GestureDetector(
                        onTap: () {
                          openScrollBottomMechanism();
                        },
                        child: Padding(
                          padding: const EdgeInsets.only(right: 20, bottom: 20),
                          child: Icon(
                            Icons.arrow_drop_down_circle,
                            color: Colors.white,
                            size: 30,
                          ),
                        ),
                      ),
                    ),
                  )
                ],
              ),
              decoration: BoxDecoration(
                shape: BoxShape.rectangle,
                color: Colors.black,

                /// To set a shadow behind the parent container
                boxShadow: [
                  BoxShadow(
                    color: Colors.white,
                    offset: Offset(0.0, -2.0),
                    blurRadius: 4.0,
                  ),
                ],

                /// To set radius of top left and top right
                borderRadius: BorderRadius.only(
                  topLeft: Radius.circular(24.0),
                  topRight: Radius.circular(24.0),
                ),
              ),
            ),
          ),
        ),
      ],
    );
  }
}
