import 'dart:async';

import 'package:flutter_wine/config.dart';
import 'package:flutter_wine/urls.dart';
import 'package:flutter_wine/util/async_mutex.dart';
import 'dart:io';

import 'package:flutter_wine/util/getx_debug_tool.dart';
import 'package:flutter_wine/util/return_converter.dart';
import 'package:flutter_wine/widgets/bottom_black_box.dart';
import 'package:get_storage/get_storage.dart';

class LaserController {
//singleton
  static LaserController _instance;

  static LaserController get instance =>
      _instance ??= LaserController._internal();

  factory LaserController() => instance;

  LaserController._internal();

  LaserSocket __socketInstance;

  final AsyncMutex __socketMutex = AsyncMutex();

  // final __closeLaser = r"$O0#";
  // final __openLaser = r"$O1#";

  //gpyr1001上，两个指令是相反的
  final __openLaser = r"$O0#";
  final __closeLaser = r"$O1#";


  int get tunedIntensityFor10 => GetStorage().read('tunedIntensityFor10') ?? Configuration.defaultIntensity;

  setTunedIntensityFor10(int value)async {
    //upper bound check
    if(value>99) value = 99;
    if(value<0) value = 0;
    LogBoxClient.simple('激光强度从$tunedIntensityFor10设置为$value');
    await GetStorage().write('tunedIntensityFor10', value);
  }

  // Completer completer;
  //
  // Future<void> go() async {
  //   completer = Completer();
  //   return completer.future;
  // }
  //
  // void done() {
  //   if (completer != null) {
  //     completer.complete();
  //   }
  // }
  Function __onData;
  Function __onError;
  Function __onDone;

  // 单socket or 多socket
  get _connectAndSendCommandImplementation => _connectAndSendCommandMulSocket;
  // get _connectAndSendCommandImplementation => _connectAndSendCommandSingleSocket;

  Future<LaserSocket> getSocketInstance(Function() onErr, void onData(event),
      {Function onError, void onDone()}) async {
    this.__onData = onData;
    this.__onError = onError;
    this.__onDone = onDone;
    Dbg.log('in $__socketInstance', 'SOCK');
    Completer completer = Completer();

    if (__socketInstance == null) {
      Socket s = await Socket.connect(Urls.strIp, Urls.socketPort,
              timeout: Duration(milliseconds: 200))
          .catchError((e) {
        Dbg.log(
            "Unable to connect: $e, and __socketInstance set to $__socketInstance",
            'SOCK');
        completer.complete(null);
        onErr?.call();
      }).then((value) {
        value?.listen((data) {
          this.__onData(data);
          Dbg.log('onData [$data]', 'SOCK');
        }, onError: (error, StackTrace trace) {
          this.__onError?.call(error, trace);
          Dbg.log('onError', 'SOCK');
        }, onDone: () {
          this.__onDone?.call();
          Dbg.log('onDone', 'SOCK');
        }, cancelOnError: false);
        return value;
      });

      if (s == null) {
        return LaserSocket(null);
      } else {
        return __socketInstance = LaserSocket(s);
      }
    } else {
      return __socketInstance;
    }
  }

  //多socket请求，随用随关
  Future _connectAndSendCommandMulSocket(String command,
      {Function() onSuccess, Function() onFail}) async {
    Socket socket;
    __socketMutex.run(() async {
      Completer completer = Completer();
      Dbg.log('command to send $command','CMD');
      socket =
          await Socket.connect(Urls.strIp, Urls.socketPort,timeout: Duration(seconds: 2)).catchError((e) {
        Dbg.log("Unable to connect: $e");
        onFail?.call();
        if (!completer.isCompleted) completer.complete();
      });
      socket?.listen((data) {
        Dbg.log(String.fromCharCodes(data).trim(), 'dataHandler');
        socket?.destroy();
        onSuccess?.call();
        if (!completer.isCompleted) completer.complete();
      }, onError: (error, StackTrace trace) {
        Dbg.log(error, 'sock err');
        socket?.destroy();
        onFail?.call();
        if (!completer.isCompleted) completer.complete();
      }, onDone: () {
        socket?.destroy();
        if (!completer.isCompleted) completer.complete();
      }, cancelOnError: false);
      socket?.write(command);

      Dbg.log(socket.address,'des');
      return completer.future;
    });
  }

  //维护单个socket
  Future _connectAndSendCommandSingleSocket(String command,
      {Function() onSuccess, Function() onFail}) async {
    LogBoxClient logBoxClient = LogBoxClient(useUUidTag: true);
    // logBoxClient.text('指令$command进入请求队列中');
    __socketMutex.run(() async {
      Completer completer = Completer();

      Socket socket;

      // logBoxClient.text('Socket开始发送$command指令');
      // time record
      DateTime startTime = DateTime.now();

      Function() onSuccessWrapper = () {
        // logBoxClient.text(
        //     'Sokcet指令$command发送成功,耗时${DateTime.now().millisecondsSinceEpoch - startTime.millisecondsSinceEpoch}ms');
        onSuccess?.call();
        if (!completer.isCompleted) completer.complete();
      };
      Function() onFailWrapper = () {
        // logBoxClient.text('Sokcet指令$command发送失败');
        __socketInstance = null;
        onFail?.call();
        if (!completer.isCompleted) completer.complete();
      };
      // logBoxClient.text('Socket发送开关指令失败');

      socket = (await getSocketInstance(() {
        // Dbg.log("Unable to connect Socket");
        onFailWrapper?.call();
        if (!completer.isCompleted) completer.complete();
      }, (data) {
        Dbg.log(String.fromCharCodes(data).trim(), 'dataHandler');
        // socket?.destroy();
        // don't destroy, for later usage
        onSuccessWrapper?.call();
        if (!completer.isCompleted) completer.complete();
      }, onError: (error, StackTrace trace) {
        Dbg.log(error, 'sock err');
        // socket?.destroy();
        // don't destroy, for later usage
        onFailWrapper?.call();
        if (!completer.isCompleted) completer.complete();
      }, onDone: () {
        // socket?.destroy();
        // don't destroy, for later usage
        onFailWrapper?.call();
        if (!completer.isCompleted) completer.complete();
      }))
          .socket;
      //     await Socket.connect(Urls.strIp, Urls.socketPort).catchError((e) {
      //   Dbg.log("Unable to connect: $e");
      //   onFailWrapper?.call();
      //   if (!completer.isCompleted) completer.complete();
      // });
      Dbg.log('sending $command', 'SOCK');
      socket?.write(command);
      // Dbg.log('des');
      await completer.future;
      Dbg.log('finish one', 'SOCK');
      return;
    });
  }

  // socket.write(r'$O1#');
  //Connect standard in to the socket
  // stdin.listen(
  //     (data) => socket.write(new String.fromCharCodes(data).trim() + '\n'));
  // Future<bool> open() async =>true;
  Future<bool> open() async {
    Completer completer = Completer<bool>();
    await _connectAndSendCommandImplementation(__openLaser, onSuccess: () {
      Dbg.log('open laser success');
      completer.complete(true);
    }, onFail: () {
      Dbg.log('open laser fail');
      completer.complete(false);
    });
    LogBoxClient.simple('打开激光');
    return completer.future;
  }

  // Future<bool> close() async =>true;
  Future<bool> close() async {
    Completer completer = Completer<bool>();
    await _connectAndSendCommandImplementation(__closeLaser, onSuccess: () {
      Dbg.log('close laser success');
      completer.complete(true);
    }, onFail: () {
      Dbg.log('close laser fail');
      completer.complete(false);
    });
    LogBoxClient.simple('关闭激光');
    return completer.future;
  }
  // Future<bool> setRealIntensity(int intensity) async => true;
  Future<bool> setRealIntensity(int intensity) async {
    Completer completer = Completer<bool>();
    await _connectAndSendCommandImplementation(__getIntensityCommand(intensity),
        onSuccess: () {
      Dbg.log('set intensity $intensity success');
      completer.complete(true);
    }, onFail: () {
      Dbg.log('set intensity fail');
      completer.complete(false);
    });
    LogBoxClient.simple('设置激光强度为$intensity');

    return completer.future;
  }

  String __getIntensityCommand(int laserIntensity) {
    if (
    //upper bound check
    // laserIntensity > 99 ||
        laserIntensity < 0) {
      // 为了安全性，激光太强伤眼睛
      laserIntensity = 0;
    }
    return r'$M' + String.fromCharCode(laserIntensity) + '#';
  }
}

class LaserSocket {
  Socket socket;

  LaserSocket(this.socket);
}
