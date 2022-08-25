import 'package:flutter_wine/config.dart';
import 'package:flutter_wine/util/getx_debug_tool.dart';
import 'package:flutter_wine/util/inference_tool.dart';
import 'package:flutter_wine/util/tflite/tf.dart';
import 'package:get_storage/get_storage.dart';

class MLPRegression extends RegressionInterface {
  @override
  bool get enabled => GetStorage().read(Configuration.models['mlpregress']) ?? true;

  @override
  Future<double> predict(List<double> curve)async {
      List d = await TfliteRunner('assets/model/flutter_reg_model.tflite')
          .runBatchOne(curve, 1);
      //  d to double

      Dbg.log(d,'mlpregress');
      // Dbg.log(d.map((e)=>e.toDouble()).toList().runtimeType);
      // List<double> result = d.map((e)=>e.toDouble()).toList();
      var result = List<double>.from(d);
      return result[0];
  }

  // @override
  // Future<List<double>> predict(List<double> curve) async {
  //   List d = await TfliteRunner('assets/model/converted_model.tflite')
  //       .runBatchOne(curve, 15);
  //   //  d to double
  //
  //   // Dbg.log(d.map((e)=>e.toDouble()).toList().runtimeType);
  //   // List<double> result = d.map((e)=>e.toDouble()).toList();
  //   var result = List<double>.from(d);
  //   return result;
  // }
}
