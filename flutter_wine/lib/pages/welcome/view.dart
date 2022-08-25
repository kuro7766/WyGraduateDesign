import 'package:flutter/material.dart';
import 'package:flutter_wine/util/db/my_db.dart';
import 'package:flutter_wine/util/getx_debug_tool.dart';
import 'package:get/get.dart';

import 'logic.dart';

class WelcomeComponent extends StatefulWidget {
  @override
  _WelcomeComponentState createState() => _WelcomeComponentState();
}

class _WelcomeComponentState extends State<WelcomeComponent> {
  final logic = Get.put(WelcomeLogic());
  final state = Get.find<WelcomeLogic>().state;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      //测试区
      onTap: () async{
        Dbg.log('WelcomeComponent onTap');
        SpecDb.instance.insert('insert into wine values (null,7,?)',args: [999]);
        Dbg.log(await SpecDb.instance.query('select * from wine where k>?',args: [3]),'sele');
      },
      child: Stack(
        children: [
          Opacity(opacity: 0.5,child: Image.asset('assets/image/bg.png')),

          Center(child: Column(
            children: [
              Expanded(
                flex: 10,
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Text('光谱仪APP',style: TextStyle(fontWeight: FontWeight.bold,fontSize: 30),),

                    Text('欢迎使用光谱仪APP',style: TextStyle(fontSize: 20),),
                  ],
                ),
              ),
              // Expanded(child: Container(),flex: 7,),
            ],
          ),),

        ],
      ),
    );
  }

  @override
  void dispose() {
    Get.delete<WelcomeLogic>();
    super.dispose();
  }
}