import 'package:flutter/material.dart';
import 'package:flutter_wine/widgets/home_draw.dart';
import 'package:flutter_wine/util/db/my_db.dart';
import 'package:flutter_wine/util/dialog.dart';
import 'package:flutter_wine/util/files.dart';
import 'package:flutter_wine/util/getx_debug_tool.dart';
import 'package:flutter_wine/util/simple_http.dart';
import 'package:flutter_wine/util/simple_http_builder.dart';
import 'package:flutter_wine/widgets/cover_widget.dart';
import 'package:flutter_wine/widgets/curve_selector/view.dart';
import 'package:flutter_wine/widgets/plotter/view.dart';
import 'package:get/get.dart';

import 'logic.dart';

class CurveCompareComponent extends StatefulWidget {
  @override
  _CurveCompareComponentState createState() => _CurveCompareComponentState();
}

class _CurveCompareComponentState extends State<CurveCompareComponent> {
  final logic = Get.put(CurveCompareLogic());
  final state = Get.find<CurveCompareLogic>().state;

  @override
  Widget build(BuildContext context) {
    return Container(
      child: HiddenDrawerNav(
        menuWidget: SizedBox(width: 200, child: CurveSelectorComponent(
          onCurveIdTap: (id)async{
            Dbg.log('id $id');
            var items = await SpecDb.instance.queryWineCurveByWineId(id);
            Dbg.log('ite,$items');
            if(items.length==0){
              DialogUtil.tip('该白酒没有曲线!');
            }else{
              DialogUtil.widget(
                  SizedBox(
                    height: 200,
                    width: 500,
                    child: ListView(
                      // This next line does the trick.
                      scrollDirection: Axis.horizontal,
                      shrinkWrap: true,
                      children:
                      List.generate(
                          // 100
                          items.length
                          , (index){
                        return SizedBox(
                          width: 300,
                          height: 200,
                          child: SimpleFutureBuilder(
                            future: ()async{
                              Dbg.log('aaa'+await PersistentFiles(fileId: items[index]['curve_file']).readCurve().toString());
                              return ResponseContent.success(await PersistentFiles(fileId: items[index]['curve_file']).readCurve());
                            }(),
                            builder: (d) {
                              return GestureDetector(
                                onTap: (){
                                  // ;
                                  if(!state.curveIds.contains(items[index]['id'])){
                                    state.curveIds.add(items[index]['id']);

                                  }else{
                                    state.curveIds.remove(items[index]['id']);

                                  }
                                  setState(() {

                                  });
                                },
                                child: MaskWidget(
                                  child: PlotterComponent(data: {
                                    'data': d
                                  },),
                                ),
                              );
                            }
                          ),
                        );
                      }),),
                  )

              );
            }
          },
        )),
        mainWidget: SizedBox(
          width: MediaQuery.of(context).size.width-100,
          height: MediaQuery.of(context).size.height-130,
          child:
          state.curveIds.isEmpty?Center(child: Text('请添加曲线')):

          SimpleFutureBuilder(
              future: ()async{
                Map<String,List<int>> data = {};
                var curveIdsList = state.curveIds.toList();
                for(var idx=0;idx<curveIdsList.length;idx+=1){
                  var element = curveIdsList[idx];
                  var wineCurveEntry =await  SpecDb.instance.queryWineCurve(element);
                  data[(await SpecDb.instance.queryWine(wineCurveEntry['wine_id']))['name'] + (wineCurveEntry['curve_desc']??'curve')] = await PersistentFiles(fileId: wineCurveEntry['curve_file']).readCurve();
                }
                // Dbg.log('aaa'+await PersistentFiles(fileId: items[index]['curve_file']).readCurve().toString());
                return ResponseContent.success(data);
              }(),
              builder: (d) {
                return PlotterComponent(
                  diableShadow: true,
                  data: d
                );
              }
          )

          ,
        ),
      ),
    );
  }

  @override
  void dispose() {
    Get.delete<CurveCompareLogic>();
    super.dispose();
  }
}
