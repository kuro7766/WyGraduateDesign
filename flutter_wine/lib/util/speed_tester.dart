import 'getx_debug_tool.dart';

class SpeedTester{
  final Function() testFunction;
  final int iterations;
  // constructor
  final String name;
  SpeedTester(this.testFunction, {this.iterations=3,this.name}){
    run();
  }
  void run(){
    var start = DateTime.now();
    for (int i = 0; i < iterations; i++) {
      testFunction();
    }
    var end = DateTime.now();
    Dbg.log("${iterations} iterations took ${end.difference(start).inMilliseconds} ms , ${end.difference(start).inMilliseconds/iterations} ms/iteration",name??'SpeedTester');
  }
}