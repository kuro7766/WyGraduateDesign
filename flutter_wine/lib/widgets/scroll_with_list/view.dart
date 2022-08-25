import 'dart:async';

import 'package:flutter/material.dart';

import 'package:flutter_wine/util/getx_debug_tool.dart';
import 'package:get/get.dart';
import 'package:scrollable_positioned_list/scrollable_positioned_list.dart';


import 'logic.dart';

// typedef WidgetBuilder = Widget Function(int index);
class CategoryScrollBinder {
  Widget title;
  List<Widget> items;

  CategoryScrollBinder({this.title, this.items});
}

class CategoryBindScrollComponent extends StatefulWidget {
  final List<CategoryScrollBinder> categories;
  final enableHeader;

  // WidgetBuilder pageBuilder;
  // WidgetBuilder itemBuilder;
  //constructor
  CategoryBindScrollComponent({Key key, @required this.categories, this.enableHeader=true
      // @required this.pages,
      // @required this.items,
      })
      : super(key: key);

  @override
  _CategoryBindScrollComponentState createState() =>
      _CategoryBindScrollComponentState();
}

class _CategoryBindScrollComponentState extends State<CategoryBindScrollComponent> {
  final logic = Get.put(ScrollWithListLogic());
  final state = Get.find<ScrollWithListLogic>().state;

  @override
  Widget build(BuildContext context) {
    logic.calculateList(widget.categories);
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Container(
          child: Visibility(
            visible: widget.enableHeader,
            child: SizedBox(
              height: 130,
              child: PageView(
                  padEnds: false,
                  onPageChanged: (index) async {
                    if (state.listenListScroll.value == false) {
                      return;
                    }
                    state.listenListScroll.value = false;
                    state.itemPositionsListener.itemPositions
                        .removeListener(logic.listenFun);
                    Dbg.log(index);

                    await state.scrollController.scrollTo(
                        index: state.categoryFirstMap[index][1],
                        duration: Duration(milliseconds: 500),
                        curve: Curves.fastLinearToSlowEaseIn);

                    // await Future.delayed(Duration(milliseconds: 50));
                    state.listenListScroll.value = true;
                    state.itemPositionsListener.itemPositions
                        .addListener(logic.listenFun);
                  },
                  children: widget.categories.map((e) => e.title).toList(),
                  controller: state.pageController),
            ),
          ),
        ),
        Expanded(
          child: Scrollbar(
            child: ScrollablePositionedList.builder(
              itemCount: state.listCategoryMap.map((e) => e[0]).length,
              itemScrollController: state.scrollController,
              physics: BouncingScrollPhysics(),
              itemPositionsListener: state.itemPositionsListener,
              itemBuilder: (context, index) {
                return state.listCategoryMap[index][0];
                // return widget.items[index];
              },
            ),
          ),
        )
      ],
    );
  }

  @override
  void dispose() {
    Get.delete<ScrollWithListLogic>();
    super.dispose();
  }
}
