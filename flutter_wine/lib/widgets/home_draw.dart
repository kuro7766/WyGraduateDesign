import 'package:flutter/material.dart';
import 'package:flutter_wine/widgets/curve_selector/view.dart';

/**
 * Author: Ambika Dulal
 * profile: https://github.com/ambikadulal
 */

class HiddenDrawerNav extends StatefulWidget {
  static final String path = "";
  final Widget menuWidget;
  final Widget mainWidget;

  const HiddenDrawerNav({Key key, this.menuWidget, this.mainWidget}) : super(key: key);
  @override
  _HiddenDrawerNavState createState() => _HiddenDrawerNavState();
}

class _HiddenDrawerNavState extends State<HiddenDrawerNav>
    with SingleTickerProviderStateMixin {
  bool isCollapsed = true;
  double screenWidth, screenHeight;
  final Duration duration = const Duration(milliseconds: 300);
   AnimationController _controller;
   Animation<double> _scaleAnimation;
   Animation<double> _menuScaleAnimation;
   Animation<Offset> _slideAnimation;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(vsync: this, duration: duration);
    _scaleAnimation = Tween<double>(begin: 1, end: 0.8).animate(_controller);
    _menuScaleAnimation =
        Tween<double>(begin: 0.5, end: 1).animate(_controller);
    _slideAnimation = Tween<Offset>(begin: Offset(-1, 0), end: Offset(0, 0))
        .animate(_controller);
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    Size size = MediaQuery.of(context).size;
    screenHeight = size.height;
    screenWidth = size.width;

    return Scaffold(
      // backgroundColor: Colors.blue,
      body: Stack(
        children: <Widget>[
          menu(context),
          mainContent(context),
        ],
      ),
    );
  }

  Widget menu(context) {
    return SlideTransition(
      position: _slideAnimation,
      child: ScaleTransition(
        scale: _menuScaleAnimation,
        child: Padding(
          padding: const EdgeInsets.only(left: 16.0),
          child: Align(
            alignment: Alignment.centerLeft,
            child: widget.menuWidget,
          ),
        ),
      ),
    );
  }

  Widget mainContent(context) {
    return AnimatedPositioned(
      duration: duration,
      top: 0,
      bottom: 0,
      left: isCollapsed ? 0 : 0.2 * screenWidth,
      right: isCollapsed ? 0 : -0.2 * screenWidth,
      child: ScaleTransition(
        scale: _scaleAnimation,
        child: Material(
          animationDuration: duration,

          elevation: 8,
          child: SingleChildScrollView(
            scrollDirection: Axis.vertical,
            physics: ClampingScrollPhysics(),
            child: Container(
              //主界面
              padding: const EdgeInsets.only(left: 16, right: 16, top: 16),
              child:
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: <Widget>[
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    mainAxisSize: MainAxisSize.max,
                    children: [
                      InkWell(
                        child: !isCollapsed?Icon(Icons.arrow_back_ios_rounded, color: Colors.grey):Icon(Icons.arrow_forward_ios_rounded, color: Colors.grey),
                        onTap: () {
                          setState(() {
                            if (isCollapsed)
                              _controller.forward();
                            else
                              _controller.reverse();

                            isCollapsed = !isCollapsed;
                          });
                        },
                      )],),
                widget.mainWidget]
            ),

              // Column(
              //   crossAxisAlignment: CrossAxisAlignment.start,
              //   children: <Widget>[
              //     Row(
              //       mainAxisAlignment: MainAxisAlignment.spaceBetween,
              //       mainAxisSize: MainAxisSize.max,
              //       children: [
              //         InkWell(
              //           child: Icon(Icons.menu, color: Colors.grey),
              //           onTap: () {
              //             setState(() {
              //               if (isCollapsed)
              //                 _controller.forward();
              //               else
              //                 _controller.reverse();
              //
              //               isCollapsed = !isCollapsed;
              //             });
              //           },
              //         ),
              //         Text("Mycurve",
              //             style: TextStyle(fontSize: 24, color: Colors.white)),
              //         Icon(Icons.settings, color: Colors.white),
              //       ],
              //     ),
              //     SizedBox(height: 50),
              //     Container(
              //       height: 200,
              //       child: PageView(
              //         controller: PageController(viewportFraction: 0.8),
              //         scrollDirection: Axis.horizontal,
              //         pageSnapping: true,
              //         children: <Widget>[
              //           Container(
              //             margin: const EdgeInsets.symmetric(horizontal: 8),
              //             color: Colors.redAccent,
              //             width: 100,
              //           ),
              //           Container(
              //             margin: const EdgeInsets.symmetric(horizontal: 8),
              //             color: Colors.blueAccent,
              //             width: 100,
              //           ),
              //           Container(
              //             margin: const EdgeInsets.symmetric(horizontal: 8),
              //             color: Colors.greenAccent,
              //             width: 100,
              //           ),
              //         ],
              //       ),
              //     ),
              //     SizedBox(height: 20),
              //     Text(
              //       "comp",
              //       style: TextStyle(color: Colors.white, fontSize: 20),
              //     ),
              //     ListView.separated(
              //         shrinkWrap: true,
              //         itemBuilder: (context, index) {
              //           return ListTile(
              //             title: Text(
              //               "Macbook",
              //               style: TextStyle(color: Colors.white, fontSize: 16),
              //             ),
              //             subtitle: Text(
              //               "A",
              //               style: TextStyle(color: Colors.white, fontSize: 16),
              //             ),
              //             trailing: Text(
              //               "a",
              //               style: TextStyle(color: Colors.white, fontSize: 16),
              //             ),
              //           );
              //         },
              //         separatorBuilder: (context, index) {
              //           return Divider(height: 16);
              //         },
              //         itemCount: 5)
              //   ],
              // ),
            ),
          ),
        ),
      ),
    );
  }
}