import 'dart:typed_data';

import 'package:flutter/services.dart';
import 'package:flutter_wine/util/getx_debug_tool.dart';
import 'package:flutter_wine/util/speed_tester.dart';
import 'package:flutter_wine/widgets/bottom_black_box.dart';
import 'package:tflite_flutter/tflite_flutter.dart';
import 'package:tflite_flutter/tflite_flutter.dart' as tfl;
import 'package:k_means_cluster/k_means_cluster.dart';

class TfliteRunner {
  String modelPath = 'assets/model/converted_model.tflite';

  //construc
  TfliteRunner(this.modelPath) ;

  Future<dynamic> runBatchOne(List input,int outputDim) async{
    return (await runBatch([input], outputDim))[0];
  }

  Future<dynamic> runBatch(List inputs, int outputDim) async {
    // ByteData imageBytes = await rootBundle.load('assets/images/test.png');
    // List<int> values = imageBytes.buffer.asUint8List();
    // img.Image photo;
    // photo = img.decodeImage(values);
    // photo.getBytes()

    // ()async{
    //   var shell = Shell();
    //   await shell.run('''su''');
    // }();

    // () async {
    //   var response = await http.get(Uri.parse(UrlConstants.imageUrl));
    //   response.
    //   response.bodyBytes;
    //   obj = response.bodyBytes;
    //   setState(() {});
    // }();

    // final gpuDelegateV2 = GpuDelegateV2(options: GpuDelegateOptionsV2());
    // var interpreterOptions = InterpreterOptions()..addDelegate(gpuDelegateV2);
    // var interpreterOptions = InterpreterOptions();
    LogBoxClient.simple('加载模型中...');

    var interpreterOptions = InterpreterOptions()..threads = 4;
    // var interpreterOptions = InterpreterOptions()..useNnApiForAndroid = true;
    Dbg.log('start loading model');
    ByteData data =
        await rootBundle.load(this.modelPath);
    var buffer = data.buffer;
    var list = buffer.asUint8List(data.offsetInBytes, data.lengthInBytes);
    final interpreter =
        await tfl.Interpreter.fromBuffer(list, options: interpreterOptions);

    LogBoxClient.simple('模型加载完成...');

    var start = DateTime.now();
    // for (var i = 0; i < 1280 * 720 * 10; i++) {
    //   var c = i + 1;
    // }
    var input = inputs;

    var output = List.filled(input.length * outputDim, 0.0)
        .reshape([input.length, outputDim]);
    var end = DateTime.now();
    print('generate time:${end.difference(start).inMilliseconds}');

    start = DateTime.now();
    // if output tensor shape [1,2] and type is float32
    // var output = List.filled(input.length * 1, 0).reshape([input.length]);
    interpreter.run(input, output);
    // interpreter.run(input, output);
    // print the output
    Dbg.log(output, 'TF-Output');
    // reocord end
    end = DateTime.now();
    Dbg.log('predict time:${end.difference(start).inMilliseconds}', 'TF-Time');
    interpreter.close();

    return output;
  }
}

// void cluster() {
//   SpeedTester(() {
//     List<List<double>> l=[
//       // [0.0, 0.0], [1.1, 1.1], [-.5, -0.50],
//       [.0,.0],
//       // [0.1, 5.0],[0.1, 1.0], [0.1, 2.0],
//       [...List.generate(2, (index) => index.toDouble())],
//       [...List.generate(2, (index) => index.toDouble()*2)],
//       [...List.generate(2, (index) => index.toDouble()*5)],
//     ];
//     var kmeans = KMeans(l);
//     Dbg.log(l);
//     var k = 2;
//     var clusters = kmeans.fit(k);
//     var silhouette = clusters.silhouette;
//     print('The clusters have silhouette $silhouette');
//     for (int i = 0; i < kmeans.points.length; i++) {
//       var point = kmeans.points[i];
//       var cluster = clusters.clusters[i];
//       var mean = clusters.means[cluster];
//       print('$point is in cluster $cluster with mean $mean.');
//     }
//   }, name: 'km');
// }



void cluster() async {
  // Load the data from iris.csv; ignore header-line;
  // each line representes an instance of an iris.
  SpeedTester(() {
    List<List<int>> lines = [
      [...List.generate(1280, (index) => index)],
      [...List.generate(1280, (index) => index * 2)],
      [...List.generate(1280, (index) => index * 5)],

    ];

    // Set the distance measure; this can be any function of the form
    // num f(List<num> a, List<num> b): a and b contain the coordinates
    // of two instances; f returns a numerical distance between the
    // points.
    distanceMeasure = DistanceType.squaredEuclidian;

    // Create the list of instances.
    List<Instance> instances = lines.map((List<int> line) {
      List<int> datum = line;

      // The first four columns contain the coordinates.
      List<int> location = datum;

      // The fifth column contains the species.
      // String id = datum[4];

      return Instance(location);
    }).toList();

    // Randomly create the initial clusters.
    List<Cluster> clusters = initialClusters(2, instances, seed: 0);

    // Run the algorithm.
    var info = kmeans(clusters: clusters, instances: instances);
    print(info);

    // See the final cluster results.
    clusters.forEach((cluster) {
      print(cluster);
      cluster.instances.forEach((iris) {
        print("  - $iris");
      });
    });
  }, name: 'C');
}
