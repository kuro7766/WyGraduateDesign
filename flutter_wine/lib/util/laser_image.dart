import 'dart:typed_data';

import 'package:flutter_wine/util/getx_debug_tool.dart';
import 'package:flutter_wine/util/speed_tester.dart';
import 'package:flutter_wine/widgets/bottom_black_box.dart';

import '../urls.dart';
import 'package:image/image.dart' as img;
import 'package:http/http.dart' as http;

class LaserImage {
  //singleton
  static final LaserImage _singleton = new LaserImage._internal();

  factory LaserImage() => _singleton;

  LaserImage._internal();

  //  instance
  LaserImage get instance => _singleton;

  static const int IMG_WIDTH = 1280;
  static const int IMG_HEIGHT = 720;

  // 获取光栅曲线，1280个列像素求和，1280是宽度
  Future<List<int>> fetchAvgCurve(int num) async {
    var K = num;

    // double[][] pixel_data = new double[K][IMG_WIDTH * IMG_HEIGHT];
    // List<List<int>> pixel_column_sum =
    //     List.generate(K, (_) => List.filled(IMG_WIDTH, 0));
    List<int> pixel_column_sum = List.filled(IMG_WIDTH, 0);
    // double[][] pixel_column_sum = new double[K][IMG_WIDTH];
    // List<int> avg_pixel_column_sum = List.filled(IMG_WIDTH, 0);

    for (var k = 0; k < K; k++) {
      LogBoxClient.simple('正在下载第${k + 1}/$K张图片...${((k+1)/K)*100.toInt()}%');
      http.Response response = await http.get(
        Uri.parse(Urls.imageUrl),
      );
      LogBoxClient.simple('${k + 1}/$K张图片下载完成');
      // ByteData imageBytes = await rootBundle.load('assets/images/test.png');
      // List<int> values = imageBytes.buffer.asUint8List();
      List<int> values = response.bodyBytes;
      img.Image photo = img.decodeImage(values);
      // photo.getBytes();

      // Dbg.log(photo
      //     .getBytes()
      //     .length / (1280 * 720));
      // Dbg.log(photo.getBytes());

      var rgba = photo.getBytes();

      List<int> singlePic = List.filled(IMG_WIDTH, 0);

      for (int i = 0; i < rgba.length; i += 4) {
        int r = rgba[i];
        int g = rgba[i + 1];
        int b = rgba[i + 2];
        // int a = rgba[i + 3];
        int gray = (r * 38 + g * 75 + b * 15) >> 7;
        int x = (i ~/ 4) % IMG_WIDTH;
        // int y = (i ~/ 4) ~/ IMG_WIDTH;
        // pixel_data[k][y * IMG_WIDTH + x] = gray;

        // pixel_column_sum[x] += gray;

        singlePic[x] += gray;

        // pixel_column_sum[k][x] += gray;
        // avg_pixel_column_sum[x] += gray;
      }
      // Dbg.log(avg_pixel_column_sum);

      // check singlePic
      // clusterCheck([singlePic]);

      for (int x = 0; x < singlePic.length; x++) {
        pixel_column_sum[x] += singlePic[x];
      }
    }
    for (int i = 0; i < IMG_WIDTH; i++) {
      pixel_column_sum[i] = pixel_column_sum[i] ~/ K;
    }
    return pixel_column_sum;
    // print(pixel_column_sum);

//     URL url = new URL(laserUrl);
//     for (int k = 0; k < K; k++) {  // k 五帧图像
//       HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//       InputStream is = conn.getInputStream();
//     BufferedImage bitmap = ImageIO.read(is);
//     int height = bitmap.getHeight();
//     int width = bitmap.getWidth();
// //            bitmap.getRGB()
//     Raster raster = bitmap.getData();
//     int[] temp = new int[raster.getWidth() * raster.getHeight() * raster.getNumBands()];
//     int[] pixels = raster.getPixels(0, 0, raster.getWidth(), raster.getHeight(), temp);
// //            System.out.println(pixels.length);
//     for (int i = 0; i < height; i++) {
//     for (int j = 0; j < width; j++) {
// //                    for (int m = 0; m < raster.getNumBands(); m++) {
//     double grey = (pixels[i * width * raster.getNumBands() + j * raster.getNumBands()] * 38 + pixels[i * width * raster.getNumBands() + j * raster.getNumBands() + 1] * 75 + pixels[i * width * raster.getNumBands() + j * raster.getNumBands() + 2] * 15) >> 7;
//     pixel_data[k][i * width + j] = grey;
// //                    }
//
//     }
//     }
//     for (int a = 0; a < width; a++) {
//     for (int b = 0; b < height; b++) {
//     pixel_column_sum[k][a] = pixel_column_sum[k][a] + pixel_data[k][b * width + a];        //列像素之和
//     }
//     }
//     // 饱和像素255 ，maxpixel_num饱和像素点个数
//     for (int n = 0; n < width * height; n++) {         //szhy
//     if (pixel_data[k][n] == 255) {
//     ++maxpixel_num;
//     }
//     }
//
//     }
//     for (int b = 0; b < IMG_WIDTH; b++) {    //5帧图像平均
//     avg_pixel_column_sum[b] =  ((pixel_column_sum[0][b] + pixel_column_sum[1][b] + pixel_column_sum[2][b] + pixel_column_sum[3][b] + pixel_column_sum[4][b]) / 5);
//     }
//
//     return avg_pixel_column_sum;
  }

  clusterCheck(List<List<int>> inputs) {}
}
