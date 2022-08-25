import 'dart:math';

class Urls {
  static String strIp = '192.168.137.62';
  static var socketPort = 2001;

  static String get imageUrl => imageUrlFromIp(strIp);

  static String imageUrlFromIp(ip) =>
      'http://$ip:8083/?action=snapshot' + '&random=${Random().nextInt(100)}';

  static String get streamUrl => 'http://$strIp:8083/?action=stream';

}
