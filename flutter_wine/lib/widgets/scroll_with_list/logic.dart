import 'dart:async';
import 'dart:math';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_wine/util/getx_debug_tool.dart';

import 'package:flutter_wine/widgets/scroll_with_list/view.dart';
import 'package:get/get.dart';
import 'package:modal_bottom_sheet/modal_bottom_sheet.dart';
import 'package:scrollable_positioned_list/scrollable_positioned_list.dart';

import 'state.dart';

class ScrollWithListLogic extends GetxController {
  final ScrollWithListState state = ScrollWithListState();

  ItemPositionsListener get itemPositionsListener =>
      state.itemPositionsListener;

  void calculateList(List<CategoryScrollBinder> bindScroll) {
    dynamic listMap = [];
    dynamic pageViewMap = [];
    var lastClassHasItems = false;
    for (var i = 0; i < bindScroll.length; i++) {
      lastClassHasItems = bindScroll[i].items.isNotEmpty;
      pageViewMap.add([i, lastClassHasItems?listMap.length:max<num>(listMap.length-1,0)]);
      // listMap.add([ bindScroll[i].items,i]);

      for (var j = 0; j < bindScroll[i].items.length; j++) {
        listMap.add([bindScroll[i].items[j], i]);
      }
    }
    state.categoryFirstMap = pageViewMap;
    state.listCategoryMap = listMap;
    Dbg.log(pageViewMap,'pageViewMap');
    Dbg.log(listMap,'listMap');
  }

  int getListFirstIndex() {
    int firstIndex = itemPositionsListener.itemPositions.value.first.index;
    // Dbg.log(itemPositionsListener.itemPositions.value);
    itemPositionsListener.itemPositions.value.forEach((element) {
      firstIndex = min(firstIndex, element.index);
    });
    return firstIndex;
  }

  listenFun() async {
    int firstIndex = getListFirstIndex();

    if(firstIndex==state.listCategoryMap.length-1){
      return;
      // var bouncingCount=1;
      // await state.scrollController.scrollTo(
      //     index: max(state.listCategoryMap.length-1-bouncingCount,0),
      //     duration: Duration(milliseconds: 500),
      //     curve: Curves.fastLinearToSlowEaseIn);
    }


    // Dbg.log(firstIndex, 'scroll');
    if (state.currentListFirstIndex != firstIndex &&
        state.listenListScroll.value) {
      state.listenListScroll.value = false;
      itemPositionsListener.itemPositions.removeListener(listenFun);
      await state.pageController.animateToPage(
          state.listCategoryMap[firstIndex][1],
          duration: const Duration(milliseconds: 200),
          curve: Curves.ease);
      // state.pageController.jumpToPage(firstIndex ~/ 50);
      // await Future.delayed(Duration(milliseconds: 100));
      state.listenListScroll.value = true;
      itemPositionsListener.itemPositions.addListener(listenFun);
    }
    state.currentListFirstIndex = firstIndex;
  }

  @override
  void onInit() {
    super.onInit();
    itemPositionsListener.itemPositions.addListener(listenFun);
  }
}
