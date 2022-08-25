import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_wine/pages/database/view.dart';
import 'package:flutter_wine/util/db/my_db.dart';
import 'package:flutter_wine/util/dialog.dart';
import 'package:flutter_wine/util/getx_debug_tool.dart';
import 'package:flutter_wine/util/mixin/evnetbus_mixin.dart';
import 'package:flutter_wine/util/simple_http.dart';
import 'package:flutter_wine/util/simple_http_builder.dart';
import 'package:flutter_wine/widgets/shareble_widget.dart';
import 'package:get/get.dart';
import 'package:modal_bottom_sheet/modal_bottom_sheet.dart';

class WineCard extends StatelessWidget {
  // final String img;
  final String title;
  final Color color;
  final int wineTypeId;
  //enable bottom sheet
  final bool enableBottomSheet;
  //on tap function
  final Function onTap;

  const WineCard({Key key, this.title, this.color,@required this.wineTypeId, this.enableBottomSheet=true, this.onTap})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      child: Center(
        child: Stack(
          children: [
            // Center(child: Image.asset(img)),
            GestureDetector(
              onTap: onTap??() {
                // if(onTap != null) onTap();
                if(!enableBottomSheet) return;
                var radius = 10.0;
                showMaterialModalBottomSheet(
                  context: context,
                  backgroundColor: Colors.transparent,
                  // elevation: 500,
                  builder: (context) => Sharable(
                    child: Padding(
                      padding: const EdgeInsets.all(8.0),
                      child: Container(
                        decoration: BoxDecoration(
                          color: Colors.white,
                          borderRadius: BorderRadius.circular(radius),
                        ),
                        height: 300,
                        // color: Colors.white,
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Container(
                              child: Row(
                                // mainAxisAlignment: MainAxisAlignment.start,
                                mainAxisAlignment: MainAxisAlignment.center,
                                children: [
                                  Padding(
                                      padding: const EdgeInsets.all(8.0),
                                      child: Center(
                                        child: Text(
                                          title,
                                          style: TextStyle(
                                              fontFamily: 'Songti',
                                              fontSize: 30),
                                        ),
                                      )
                                      // Column(
                                      //   mainAxisAlignment: MainAxisAlignment.start,
                                      //   crossAxisAlignment: CrossAxisAlignment.start,
                                      //   children: [
                                      //     Text('类别',style: TextStyle(fontFamily: 'Songti',fontSize: 30),)
                                      //   ],
                                      // ),
                                      ),
                                ],
                              ),
                              decoration: BoxDecoration(
                                color: color,
                                borderRadius: BorderRadius.only(
                                  topLeft: Radius.circular(radius),
                                  topRight: Radius.circular(radius),
                                ),
                              ),
                              height: 100,
                            ),
                            Expanded(
                                child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text('类别描述:'),
                                SimpleFutureBuilder(
                                  future: ()async{
                                    if (wineTypeId == -1) return ResponseContent.success('默认分类，没有类别的白酒会自动归入本目录下');
                                    Dbg.log('get wine type${(wineTypeId)}','x');
                                    var data=await SpecDb().queryWineType((wineTypeId));
                                    Dbg.log('data:$data','r');
                                    return ResponseContent.success(data['desc']);
                                  }(),
                                  builder: (data) {
                                    return Wrap(
                                      children: [
                                        Text(data),
                                      ],
                                    );
                                  },
                                ),
                              ],
                            )),
                            Stack(
                              children: [
                                Row(
                                  children: [
                                    Expanded(
                                      child: Padding(
                                          padding: const EdgeInsets.all(14.0),
                                          child: InkWell(
                                            child: Row(
                                              mainAxisAlignment:
                                              MainAxisAlignment.start,
                                              children: [
                                                Icon(Icons.delete_forever)
                                              ],
                                            ),
                                            onTap: () {
                                              DialogUtil.iosConfirm('是否删除?').then((value)async{
                                                if (value) {
                                                  await SpecDb.instance.deleteWineType(wineTypeId);
                                                  await SpecDb.instance.deleteWineTypeMapByWineTypeId(wineTypeId);
                                                  Get.back();
                                                  fireEvent(DatabaseComponentState, 'refresh');
                                                }
                                              });
                                            },
                                          )),
                                    ),
                                    Expanded(
                                      child: Padding(
                                          padding: const EdgeInsets.all(14.0),
                                          child: Sharable.shareButton()),
                                    ),

                                  ],
                                ),
                                // GesstureDetector(
                                //   onTap: () {},
                                //   child: Padding(
                                //       padding: const EdgeInsets.all(14.0),
                                //       child: InkWell(
                                //         child:  Row(
                                //           mainAxisAlignment: MainAxisAlignment.start,
                                //           children: [Icon(Icons.delete_forever)],
                                //         ),
                                //         onTap: () {
                                //         },
                                //       )
                                //   ),
                                // ),
                              ],
                            )
                          ],
                        ),
                      ),
                    ),
                  ),
                );
              },
              child: Padding(
                padding: const EdgeInsets.only(left: 3.0, right: 3.0, top: 10),
                child: Container(
                  decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(20),
                    color: color,
                    boxShadow: [
                      BoxShadow(
                          // color: Colors.grey.withOpacity(0.5),
                          // spreadRadius: 5,
                          // blurRadius: 7,
                          // offset: Offset(0, 3), // changes position of shadow
                          ),
                    ],
                  ),
                ),
              ),
            ),
            Padding(
              padding: const EdgeInsets.all(18.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    '$title',
                    style: TextStyle(color: Colors.white, fontSize: 20),
                  ),
                  // Text('$title',style: TextStyle(color: Colors.white,),),
                  // Text('$title',style: TextStyle(color: Colors.white,),),
                ],
              ),
            )
          ],
        ),
      ),
    );
  }
}
