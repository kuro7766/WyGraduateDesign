import 'dart:convert';
import 'dart:io';

import 'package:flutter_wine/util/getx_debug_tool.dart';
import 'package:path_provider/path_provider.dart';
import 'package:uuid/uuid.dart';

class PersistentFiles {
  String fileId;

  PersistentFiles({this.fileId}) {
    fileId ??= const Uuid().v4();
  }

  Future<File> getFile() async {
    final directory = await getApplicationDocumentsDirectory();
    return File('${directory.path}/$fileId');
  }

  Future<void> write(String data) async {
    final file = await getFile();
    file.writeAsStringSync(data);
  }

  Future<void> writeCurve(List<int> data) async {
    final file = await getFile();
    // file.writeAsBytesSync(data);
    file.writeAsStringSync(json.encode({'data':data}));
    Dbg.log(json.encode({'data':data}),'js');
  }

  Future<String> read() async {
    final file = await getFile();
    return file.readAsStringSync();
  }

  Future<List<int>> readCurve() async {
    final file = await getFile();
    // return file.readAsBytesSync();
    // Dbg.log(file.readAsStringSync().substring(file.readAsStringSync().length-200),'js2');
    Dbg.log(file.readAsStringSync(),'js2');
    List<int> ints=[];

    try{
      Dbg.log(json.decode(file.readAsStringSync()),'P');
      Dbg.log(json.decode(file.readAsStringSync())['data'],'P2');
      var datas=json.decode(file.readAsStringSync())['data'];
      for(int i=0;i<datas.length;i++){
        ints.add(datas[i]);
      }
      Dbg.log(ints,'P4');

      Dbg.log(ints.runtimeType,'P3');
    }catch(e){
      Dbg.log(e.toString(),'err');
    }
    // return json.decode(file.readAsStringSync())['data'].map((e)=>e.toInt());
    return ints;

  }

//  exist
  Future<bool> exists() async {
    final file = await getFile();
    return file.existsSync();
  }
}
