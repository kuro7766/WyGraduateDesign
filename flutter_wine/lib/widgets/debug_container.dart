import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class DebugContainer extends StatelessWidget {
  final bool debugMode ;
  final Widget child;

  const DebugContainer({Key key, this.child,this.debugMode=true}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    if (debugMode) {
      return Container(
        color: Colors.purple,
        child: child,
      );
    }
    return child;
  }
}
