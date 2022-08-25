import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_wine/config.dart';
import 'package:flutter_wine/entity/wine_entity_entity.dart';
import 'package:flutter_wine/entity/wine_type_entity.dart';
import 'package:flutter_wine/generated/json/base/json_convert_content.dart';
import 'package:flutter_wine/pages/database/view.dart';
import 'package:flutter_wine/util/db/my_db.dart';
import 'package:flutter_wine/util/dialog.dart';
import 'package:flutter_wine/util/files.dart';
import 'package:flutter_wine/util/getx_debug_tool.dart';
import 'package:flutter_wine/util/mixin/evnetbus_mixin.dart';
import 'package:flutter_wine/util/simple_http.dart';
import 'package:flutter_wine/util/simple_http_builder.dart';
import 'package:flutter_wine/widgets/cover_widget.dart';
import 'package:flutter_wine/widgets/plotter/view.dart';
import 'package:flutter_wine/widgets/scroll_with_list/view.dart';
import 'package:get/get.dart';
import 'package:modal_bottom_sheet/modal_bottom_sheet.dart';

import '../shareble_widget.dart';
import '../wine_card.dart';
import 'logic.dart';

typedef OnCurveIdTap=Function(int curveId);
class CurveSelectorComponent extends StatefulWidget {

  final OnCurveIdTap onCurveIdTap;

  const CurveSelectorComponent({Key key, this.onCurveIdTap}) : super(key: key);
  @override
  _CurveSelectorComponentState createState() => _CurveSelectorComponentState();
}

class _CurveSelectorComponentState extends State<CurveSelectorComponent> {
  final logic = Get.put(CurveSelectorLogic());
  final state = Get.find<CurveSelectorLogic>().state;

  void onlistTap(WineEntity wine, Color color) {
    var radius = 10.0;
    widget.onCurveIdTap(wine.id);
    // showMaterialModalBottomSheet(
    //   context: context,
    //   backgroundColor: Colors.transparent,
    //   // elevation: 500,
    //   builder: (context) => Sharable(
    //     child: Padding(
    //       padding: const EdgeInsets.all(8.0),
    //       child: Container(
    //         decoration: BoxDecoration(
    //           color: Colors.white,
    //           borderRadius: BorderRadius.circular(radius),
    //         ),
    //         height: 300,
    //         // color: Colors.white,
    //         child: Row(
    //           children: [
    //             Expanded(
    //               child: Column(
    //                 crossAxisAlignment: CrossAxisAlignment.start,
    //                 children: [
    //                   Container(
    //                     child: Row(
    //                       // mainAxisAlignment: MainAxisAlignment.start,
    //                       mainAxisAlignment: MainAxisAlignment.center,
    //                       children: [
    //                         Padding(
    //                             padding: const EdgeInsets.all(8.0),
    //                             child: Center(
    //                               child: Text(
    //                                 wine.name,
    //                                 style: TextStyle(
    //                                     fontFamily: 'Songti', fontSize: 30),
    //                               ),
    //                             )
    //                           // Column(
    //                           //   mainAxisAlignment: MainAxisAlignment.start,
    //                           //   crossAxisAlignment: CrossAxisAlignment.start,
    //                           //   children: [
    //                           //     Text('类别',style: TextStyle(fontFamily: 'Songti',fontSize: 30),)
    //                           //   ],
    //                           // ),
    //                         ),
    //                       ],
    //                     ),
    //                     decoration: BoxDecoration(
    //                       // color: Colors.red,
    //                       color: color,
    //                       borderRadius: BorderRadius.only(
    //                         topLeft: Radius.circular(radius),
    //                         topRight: Radius.circular(radius),
    //                       ),
    //                     ),
    //                     height: 100,
    //                   ),
    //                   Expanded(
    //                       child: Column(
    //                         crossAxisAlignment: CrossAxisAlignment.start,
    //                         children: [
    //                           Text('描述:'),
    //                           SimpleFutureBuilder(future: () async {
    //                             Dbg.log(
    //                                 await SpecDb.instance.queryWineTypeMapList(),
    //                                 '1');
    //                             Dbg.log(wine.id, '2');
    //
    //                             var data = await SpecDb.instance
    //                                 .queryWineTypeMapListByWineId(wine.id);
    //                             Dbg.log('data:$data', 'x');
    //                             return ResponseContent.success(data);
    //                           }(), builder: (data) {
    //                             return Wrap(
    //                               children: [
    //                                 Text(wine.desc ?? ''),
    //                               ],
    //                             );
    //                           }),
    //                         ],
    //                       )),
    //                   Stack(
    //                     children: [
    //                       Row(
    //                         children: [
    //                           Expanded(
    //                             child: Padding(
    //                                 padding: const EdgeInsets.all(14.0),
    //                                 child: InkWell(
    //                                   child: Row(
    //                                     mainAxisAlignment:
    //                                     MainAxisAlignment.start,
    //                                     children: [Icon(Icons.delete_forever)],
    //                                   ),
    //                                   onTap: () {
    //                                     DialogUtil.iosConfirm('是否删除?')
    //                                         .then((value) async {
    //                                       if (value) {
    //                                         await SpecDb.instance.deleteWine(wine.id);
    //                                         var results=await SpecDb.instance.deleteWineTypeMapByWineId(wine.id);
    //                                         Dbg.log(results,'results');
    //                                         Get.back();
    //                                         fireEvent(DatabaseComponentState, 'refresh');
    //                                       }
    //                                     });
    //                                   },
    //                                 )),
    //                           ),
    //                           Expanded(
    //                             child: Padding(
    //                                 padding: const EdgeInsets.all(14.0),
    //                                 child: Sharable.shareButton()),
    //                           ),
    //                         ],
    //                       ),
    //                       // GesstureDetector(
    //                       //   onTap: () {},
    //                       //   child: Padding(
    //                       //       padding: const EdgeInsets.all(14.0),
    //                       //       child: InkWell(
    //                       //         child:  Row(
    //                       //           mainAxisAlignment: MainAxisAlignment.start,
    //                       //           children: [Icon(Icons.delete_forever)],
    //                       //         ),
    //                       //         onTap: () {
    //                       //         },
    //                       //       )
    //                       //   ),
    //                       // ),
    //                     ],
    //                   )
    //                 ],
    //               ),
    //             ),
    //             Expanded(
    //                 child: Container(
    //                     child:SimpleFutureBuilder(
    //                       future: ()async{
    //                         var data=await SpecDb.instance.queryWineCurveByWineId(wine.id);
    //                         Dbg.log(data,'rr');
    //                         return ResponseContent.success(data);
    //                       }(),
    //                       builder: (data){
    //                         if(data==null){
    //                           return Container();
    //                         }
    //                         if(data?.length==0){
    //                           return Container();
    //                         }
    //                         return PageView(
    //                           children: [
    //                             ...List.generate(data.length, (idx) => MaskWidget(
    //                               child: SimpleFutureBuilder(
    //                                   future: ()async{
    //                                     Dbg.log(data[idx]);
    //                                     var curve=await PersistentFiles(fileId: data[idx]['curve_file']).readCurve();
    //                                     Dbg.log(curve,'www');
    //                                     return ResponseContent.success(curve);
    //                                     // return ResponseContent.success(await PersistentFiles(fileId: 'asdfs').readBytes());
    //                                   }(),
    //                                   builder: (cur) {
    //                                     Dbg.log(cur,'c');
    //                                     return PlotterComponent(
    //                                       data: {'data': cur},
    //                                     );
    //                                   }
    //                               ),
    //                             ),)
    //                             // MaskWidget(
    //                             //   child: PlotterComponent(
    //                             //     data: {
    //                             //       'data': List.generate(1000, (index) => 2000 + index)
    //                             //     },
    //                             //   ),
    //                             // )
    //                           ],
    //                         );
    //                       },
    //                     )
    //                 )),
    //           ],
    //         ),
    //       ),
    //     ),
    //   ),
    // );
  }

  @override
  Widget build(BuildContext context) {
    return SimpleFutureBuilder(
      future: () async {
        // await Future.delayed(Duration(seconds: 1));
        var wineTypes = await SpecDb.instance.queryWineTypeList();
        Dbg.log('wineTypes:$wineTypes', 'wt');
        var wineTypeEntities =
        JsonConvert.fromJsonAsT<List<WineTypeEntity>>(wineTypes);
        List<List<WineEntity>> wineTypeArrays = [];
        for (var wineType in wineTypeEntities) {
          List<WineEntity> wineEntities = [];
          var wineMaps =
          await SpecDb.instance.queryWineTypeMapListByTypeId(wineType.id);
          Dbg.log('wineMaps:$wineMaps', 'wt2');
          // var wine=await SpecDb.instance.queryWine(wineMaps[0]['wine_id']);
          // Dbg.log('wine:$wine','wt3');
          for (var wineMap in wineMaps) {
            var wine = await SpecDb.instance.queryWine(wineMap['wine_id']);
            Dbg.log('wine:$wine for cls ${wineType.name}', 'wt4');
            wineEntities.add(JsonConvert.fromJsonAsT<WineEntity>(wine));
          }
          wineTypeArrays.add(wineEntities);
        }
        Dbg.log('wineTypeEntities:$wineTypeEntities', 'wt5');

        var defaultTypeWines = await SpecDb.instance.queryWineListNoTypeMap();
        Dbg.log('defaultTypeWines:$defaultTypeWines', 'wt6');
        var defaultTypeWineEntities =
        JsonConvert.fromJsonAsT<List<WineEntity>>(defaultTypeWines);
        return ResponseContent.success(
            [wineTypeEntities, wineTypeArrays, defaultTypeWineEntities]);
      }(),
      builder: (datas) {
        List<WineTypeEntity> data = datas[0];
        List<List<WineEntity>> wineTypeArrays = datas[1];
        List<WineEntity> defaultTypeWineEntities = datas[2];
        return CategoryBindScrollComponent(
            enableHeader: false,
            categories: [
          CategoryScrollBinder(
              title: WineCard(
                title: '默认分类',
                color: Colors.grey,
                wineTypeId: -1,
              ),
              items: [
                ...List.generate(
                    defaultTypeWineEntities.isEmpty ? 0 : 1,
                        (_index) => Padding(
                      padding: const EdgeInsets.only(left: 12.0, top: 10),
                      child: Row(
                        children: [
                          Icon(
                            Icons.circle,
                            size: 15,
                            color: Colors.grey,
                          )
                        ],
                      ),
                    )),
                ...List.generate(
                    defaultTypeWineEntities.length,
                        (index) => ListTile(
                      title: Text(defaultTypeWineEntities[index].name),
                      onTap: () => onlistTap(
                          defaultTypeWineEntities[index], Colors.grey),
                    ))
                // SizedBox(
                //   height: 200,
                //   child: Container(
                //     color: Colors.blueAccent,
                //   ),
                // )
              ]),
          ...List.generate(
              data.length,
                  (index) => CategoryScrollBinder(
                  title: WineCard(
                    wineTypeId: data[index].id,
                    title: data[index].name,
                    color: Configuration.colorTable[
                    index % Configuration.colorTable.length],
                  ),
                  items: [
                    ...List.generate(
                        wineTypeArrays[index].isEmpty ? 0 : 1,
                            (_index) => Padding(
                          padding: const EdgeInsets.only(
                              left: 12.0, top: 10),
                          child: Row(
                            children: [
                              Icon(
                                Icons.circle,
                                size: 15,
                                color: Configuration.colorTable[index %
                                    Configuration.colorTable.length],
                              )
                            ],
                          ),
                        )),
                    ...List.generate(
                        wineTypeArrays[index].length,
                            (index2) => ListTile(
                          title:
                          Text(wineTypeArrays[index][index2].name),
                          onTap: () => onlistTap(
                              wineTypeArrays[index][index2],
                              Configuration.colorTable[index %
                                  Configuration.colorTable.length]),
                        ))
                  ])),
          CategoryScrollBinder(
              title: WineCard(
                title: '更多分类待添加...',
                color: Colors.grey,
                onTap: () async {
                  // var results = await DialogUtil.displayTextInputDialog(
                  //     ['输入分类名称', '输入备注']);
                  // if (results[0] != null) {
                  //   SpecDb.instance.insertWineType({
                  //     'name': results[0],
                  //     'desc': results[1],
                  //   });
                  //   setState(() {
                  //
                  //   });
                  // }
                },
                // enableBottomSheet: false,
              ),
              items: [
                SizedBox(
                  height: 200,
                  child: Container(
                    color: Colors.transparent,
                  ),
                )
              ]),
        ]);
      },
    );
  }

  @override
  void dispose() {
    Get.delete<CurveSelectorLogic>();
    super.dispose();
  }
}
