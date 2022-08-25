import 'dart:ui';

class Configuration {
//  background : white
  static const Color background = Color(0xFFFFFFFF);

//  rightBarColor : grey
  static const Color rightBarColor = Color(0x20E0E0E0);

  // rightSize
  static const double rightSize = 100.0;

//  bottomSize
  static const double bottomSize = 50.0;

  // bottomScrollSheetSize
  static const double bottomScrollSheetSize = 0.15;

  // static const double bottomScrollSheetSize = .0;

  // bottomScrollMaxSize
  static const double bottomScrollMaxSize = 0.8;

  // water ramans
  // static const waterRaman = [14000.0, 16000.0];
  static const waterRaman = [0, 99999.0];

//  default intensity
  static const int defaultIntensity = 79;

  // calibrate times
  static const int calibrateTimes = 3;

//  toggleLaserTimeGap
//  两次开关激光的时间间隔
  static const int toggleLaserTimeGap = 1;

  static const colorTable = [
    Color(0xFFf72585),
    Color(0xFF76c893),
    Color(0xFF4cc9f0),
    Color(0xFF168aad),
    Color(0xFF52b69a),
    Color(0xFF34a0a4),
    Color(0xFFb5e48c),
    Color(0xFF3a0ca3),
    Color(0xFF1a759f),
    Color(0xFF7209b7),
    Color(0xFF3f37c9),
    Color(0xFF4895ef),
    Color(0xFFd9ed92),
    Color(0xFF4361ee),
    Color(0xFF560bad),
    Color(0xFF480ca8),
    Color(0xFF99d98c),
    Color(0xFFb5179e),
    Color(0xFF1e6091),
    Color(0xFF184e77),
  ];

  static const wines = [
    '未知',
    '北京二锅头',
    '威海卫烧锅',
    '丁香情',
    '原浆',
    '北大仓',
    '苦芥',
    '景芝白干',
    '衡水老白干',
    '老酒壶',
    '牛栏山陈酿',
    '杜二酒',
    '小郎酒',
    '五粮情',
    '闷倒驴',
    '牛栏山二锅头'
  ];

  static const smell = [
    '酱香型',
    '浓香型',
    '清香型'
  ];

  static const models= {
    'mlpclass': 'MLPClassifier',
    'mlpregress': 'MLPRegressor',
  };

  static const domain='39.100.158.75';
  static const port=9998;
  static const apkVersion='1.0.0';

  static const classifyCount = 15;

  static get deviceId => dvc;
  static String dvc = "";
}
