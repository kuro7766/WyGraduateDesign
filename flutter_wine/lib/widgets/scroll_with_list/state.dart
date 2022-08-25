import 'package:flutter/cupertino.dart';
import 'package:flutter_wine/util/async_mutex.dart';

import 'package:get/get_rx/src/rx_types/rx_types.dart';
import 'package:scrollable_positioned_list/scrollable_positioned_list.dart';

class ScrollWithListState {
  var pageController = PageController(
    viewportFraction: 0.4,
    initialPage: 0,
  );
  var scrollController = ItemScrollController();
  final ItemPositionsListener itemPositionsListener =
      ItemPositionsListener.create();
  AsyncMutex asyncMutex=AsyncMutex();
  var currentListFirstIndex=0;
  final listenListScroll=true.obs;

  var currentPageViewIndex=0;

  List<dynamic> categoryFirstMap;
  List<dynamic> listCategoryMap;
  ScrollWithListState() {
    ///Initialize variables
    // pageController.addListener(() {
    //   Dbg.log('msg');
    //   scrollController.scrollTo(
    //       index: 50,
    //       duration: Duration(seconds: 2),
    //       curve: Curves.fastLinearToSlowEaseIn);
    // });
  }
}
