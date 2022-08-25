import 'package:flutter_wine/config.dart';
import 'package:flutter_wine/util/getx_debug_tool.dart';
import 'package:flutter_wine/util/inference_tool.dart';
import 'package:flutter_wine/util/tflite/tf.dart';
import 'package:get_storage/get_storage.dart';

class MlpClassifier extends ClassificationInterface {
  @override
  // bool get enabled => GetStorage().read(Configuration.models['mlpclass']) ?? true;
  bool get enabled => true;

  @override
  Future<List<double>> predict(List<double> curve) async {
    List d = await TfliteRunner('assets/model/converted_model.tflite')
        .runBatchOne(curve, Configuration.classifyCount);
    //  d to double

    // Dbg.log(d.map((e)=>e.toDouble()).toList().runtimeType);
    // List<double> result = d.map((e)=>e.toDouble()).toList();
    var result = List<double>.from(d);
    return result;
  }
}
