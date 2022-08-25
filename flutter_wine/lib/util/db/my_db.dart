import 'package:flutter_wine/util/async_mutex.dart';
import 'package:flutter_wine/util/files.dart';
import 'package:flutter_wine/util/getx_debug_tool.dart';
import 'package:sqflite/sqflite.dart';
import 'package:uuid/uuid.dart';

class SpecDb {
  static SpecDb _instance;

  static SpecDb get instance => _instance ??= SpecDb._internal();

  factory SpecDb() => instance;

  SpecDb._internal();

  AsyncMutex dbMutex = AsyncMutex();

  static String dbPath = 'wine2.db3';

  Future init() async {
    // await deleteDatabase(dbPath);

    // var db = await openDatabase(dbPath);
    Dbg.log(dbPath, 'db');


    // open the database
    // language=sql 这个不能用，在线编辑器 https://sqliteonline.com/
    Database db = await openDatabase(dbPath, version: 1,
        onCreate: (Database db, int version) async {
      Dbg.log(dbPath, 'g');

    });

    await db.execute('CREATE TABLE if not exists logs (t integer,msg text)');
    //白酒基本信息（id，名称，描述，图片），这里其实名称就是ID，否则会造成混淆
    await db.execute(
        'CREATE TABLE if not exists wine (id integer primary key,name text,desc text,img text)');
    // 白酒曲线（id，曲线文件 , 曲线id），同一种白酒在多次测量会有不同的曲线
    await db.execute(
        'CREATE TABLE if not exists wine_curve (id integer primary key,curve_file text,curve_desc text,wine_id integer)');
    // 白酒分类 （id，类别名称，类别备注）为了便于白酒归档整理
    await db.execute(
        'CREATE TABLE if not exists wine_type (id integer primary key,name text,desc text)');
    // 白酒和分类关联表 （两个ID）
    await db.execute(
        'CREATE TABLE if not exists wine_type_map (id integer primary key,wine_id integer,type_id integer)');
    // 白酒测量结果 （曲线ID，*曲线测量结果(「度数，类别」)，是否错误），因为不同模型，不同时间测量的结果是不一样的
    await db.execute(
        'CREATE TABLE if not exists wine_curve_result (curve_id integer,degree text,type text,is_error integer)');

    await db.execute('delete from wine_type');
    await db.execute(
        'insert into wine_type (id,name,desc) values (1,"酱香型","酱香为主，略有焦香但不能出头，香味细腻、复杂、柔顺含泸,泸香不突出，酯香柔雅协调，先酯后酱，酱香悠长，杯中香气经久不变 空杯留香经久不散茅台酒有扣杯隔日香的说法，味大于香，苦度适中，酒度低而不变。酱香型白酒的标准评语是：无色或微黄透明，无悬浮物，无沉淀，酱香突出、幽雅细腻，空杯留香幽雅持久，入口柔绵醇厚，回味悠长，风格突出、明显、尚可")');
    await db.execute(
        'insert into wine_type (id,name,desc) values (2,"浓香型","中国产白酒的香型之一。采用老窖位发酵生香基地，窖愈老，窖泥中的酿酒微生物愈多，生产的酒愈好。浓香型白酒其酒味芳香浓郁，绵柔甘烈，香味协调，绵甘适口， ...")');
    await db.execute(
        'insert into wine_type (id,name,desc) values (3,"清香型","中国产白酒的香型之一。 清香型白酒的特点为清香纯正，酸甜柔和，诸味协调，后味很甜。乙酸与适量的乳酸乙 ...")');

    // await initFakeData();
  }

  Future initFakeData() async {
    await dbMutex.run(() async {
      var db = await openDatabase(dbPath);


      await db.execute('delete from wine');
      await db.execute('delete from wine_curve');
      await db.execute('delete from wine_type');
      await db.execute('delete from wine_type_map');
      await db.execute('delete from wine_curve_result');
      await db.execute('delete from logs');


      await db.execute(
          'insert into wine (id,name,desc,img) values (1,"原浆白酒","贵州酱香型53度原浆白酒送礼高度纯粮食老酒礼盒装整箱特价6瓶装","bg.png")');
      await db.execute(
          'insert into wine (id,name,desc,img) values (2,"浓香型原浆","冠润白酒纯粮食浓香型原浆酒整箱52度桶装高粱自酿散装酒泡药专用","bg.png")');
      await db.execute(
          'insert into wine (id,name,desc,img) values (3,"散装53度白酒","自酿10斤桶装贵州酱香型原浆老酒纯粮食高粱散装53度白酒整箱泡酒","bg.png")');
      await db.execute(
          'insert into wine (id,name,desc,img) values (4,"北京红星二锅头","北京红星二锅头56度大二绿瓶500ml清香型高度纯粮食口粮酒白酒","bg.png")');
      await db.execute(
          'insert into wine (id,name,desc,img) values (5,"闷倒驴酒二锅头","闷倒驴酒二锅头白酒高度纯粮食68度5L约十斤桶装泡酒专用散装白酒","bg.png")');
      await db.execute(
          'insert into wine (id,name,desc,img) values (6,"白酒1","白酒","bg.png")');
      await db.execute(
          'insert into wine (id,name,desc,img) values (7,"白酒2","白酒","bg.png")');
      await db.execute(
          'insert into wine (id,name,desc,img) values (8,"白酒3","白酒","bg.png")');

      await db.execute(
          'insert into wine_curve (id,curve_file,wine_id) values (1,"bg.png",1)');
      await db.execute(
          'insert into wine_curve (id,curve_file,wine_id) values (2,"bg.png",2)');
      await db.execute(
          'insert into wine_curve (id,curve_file,wine_id) values (3,"bg.png",3)');


      await db.execute(
          'insert into wine_type_map (id,wine_id,type_id) values (1,1,1)');
      await db.execute(
          'insert into wine_type_map (id,wine_id,type_id) values (2,2,1)');
      await db.execute(
          'insert into wine_type_map (id,wine_id,type_id) values (3,3,2)');
      await db.execute(
          'insert into wine_type_map (id,wine_id,type_id) values (4,4,2)');
      await db.execute(
          'insert into wine_type_map (id,wine_id,type_id) values (5,5,2)');
      await db.execute(
          'insert into wine_type_map (id,wine_id,type_id) values (6,6,2)');

      await db.execute(
          'insert into wine_curve_result (curve_id,degree,type,is_error) values (1,"0.5","红葡萄酒",0)');
      await db.execute(
          'insert into wine_curve_result (curve_id,degree,type,is_error) values (2,"0.5","红葡萄酒",0)');
      await db.execute(
          'insert into wine_curve_result (curve_id,degree,type,is_error) values (3,"0.5","红葡萄酒",0)');
      await db.execute(
          'insert into wine_curve_result (curve_id,degree,type,is_error) values (4,"0.5","白葡萄酒",0)');
      await db.execute(
          'insert into wine_curve_result (curve_id,degree,type,is_error) values (5,"0.5","白葡萄酒",0)');
    });
  }

  Future<int> insert(String sql, {List<dynamic> args}) async {
    return await dbMutex.run(() async {
      var db = await openDatabase(dbPath);
      return await db.rawInsert(sql, args);
    });
  }

  Future<List<Map<String, dynamic>>> query(String sql,
      {List<dynamic> args}) async {
    return await dbMutex.run(() async {
      var db = await openDatabase(dbPath);
      return await db.rawQuery(sql, args);
    });
  }

  Future<int> update(String sql, {List<dynamic> args}) async {
    return await dbMutex.run(() async {
      var db = await openDatabase(dbPath);
      return await db.rawUpdate(sql, args);
    });
  }

  Future<int> delete(String sql, {List<dynamic> args}) async {
    return await dbMutex.run(() async {
      var db = await openDatabase(dbPath);
      return await db.rawDelete(sql, args);
    });
  }

  Future insertLog(String msg) async {
    await insert('insert into logs(t,msg) values(?,?)',
        args: [DateTime.now().millisecondsSinceEpoch, msg]);
  }

  Future<List<Map<String, dynamic>>> queryLogs() async {
    return await query('select * from logs order by t desc');
  }

  Future<num> insertWine(Map<String, dynamic> wine) async {
    var id = await insert('insert into wine(name,desc,img) values(?,?,?)',
        args: [wine['name'], wine['desc'], wine['img']]);
    return id;
  }

  Future<Map<String, dynamic>> queryWine(num id) async {
    var wine = await query('select * from wine where id=?', args: [id]);
    return wine.length > 0 ? wine[0] : null;
  }

  Future<List<Map<String, dynamic>>> queryWineList() async {
    return await query('select * from wine');
  }

  Future<int> updateWine(Map<String, dynamic> wine) async {
    return await update('update wine set name=?,desc=?,img=? where id=?',
        args: [wine['name'], wine['desc'], wine['img'], wine['id']]);
  }

  Future<int> queryWineByName(String name) async {
    var wine = await query('select * from wine where name=?', args: [name]);
    return wine.length > 0 ? wine[0]['id'] : null;
  }

  Future<int> deleteWine(num id) async {
    return await delete('delete from wine where id=?', args: [id]);
  }

  Future<num> insertWineCurve(Map<String, dynamic> wineCurve) async {
    var id = await insert(
        'insert into wine_curve(curve_file,wine_id,curve_desc) values(?,?,?)',
        args: [wineCurve['curve_file'], wineCurve['wine_id'],wineCurve['curve_desc']]);
    return id;
  }

  Future<Map<String, dynamic>> queryWineCurve(num id) async {
    var wineCurve =
        await query('select * from wine_curve where id=?', args: [id]);
    return wineCurve.length > 0 ? wineCurve[0] : null;
  }

  Future<List<dynamic>> queryWineCurveByWineId(num id) async {
    var wineCurve =
    await query('select * from wine_curve where wine_id=?', args: [id]);
    return wineCurve;
  }

  Future<int> updateWineCurve(Map<String, dynamic> wineCurve) async {
    return await update(
        'update wine_curve set curve_file=?,wine_id=? where id=?',
        args: [wineCurve['curve_file'], wineCurve['wine_id'], wineCurve['id']]);
  }

  Future<int> deleteWineCurve(num id) async {
    return await delete('delete from wine_curve where id=?', args: [id]);
  }

  Future<int> insertWineType(Map<String, dynamic> wineType) async {
    var id = await insert('insert into wine_type(name,desc) values(?,?)',
        args: [wineType['name'], wineType['desc']]);
    return id;
  }

  Future<Map<String, dynamic>> queryWineType(num id) async {
    var wineType =
        await query('select * from wine_type where id=?', args: [id]);
    return wineType.length > 0 ? wineType[0] : null;
  }

  Future<Map<String, dynamic>> queryWineTypeIdByName(String name) async {
    var wineType =
        await query('select * from wine_type where name=?', args: [name]);
    return wineType.length > 0 ? wineType[0] : null;
  }

  Future<List<Map<String, dynamic>>> queryWineTypeList() async {
    return await query('select * from wine_type');
  }

  Future<int> updateWineType(Map<String, dynamic> wineType) async {
    return await update('update wine_type set name=?,desc=? where id=?',
        args: [wineType['name'], wineType['desc'], wineType['id']]);
  }

  Future<int> deleteWineType(num id) async {
    return await delete('delete from wine_type where id=?', args: [id]);
  }

  Future<int> deleteWineTypeMapByWineTypeId(num id) async {
    return await delete('delete from wine_type_map where type_id=?',
        args: [id]);
  }

  Future<int> insertWineCurveResult(
      Map<String, dynamic> wineCurveResult) async {
    var id = await insert(
        'insert into wine_curve_result(curve_id,degree,type,is_error) values(?,?,?,?)',
        args: [
          wineCurveResult['curve_id'],
          wineCurveResult['degree'],
          wineCurveResult['type'],
          wineCurveResult['is_error']
        ]);
    return id;
  }

  Future<Map<String, dynamic>> queryWineCurveResult(String id) async {
    var wineCurveResult =
        await query('select * from wine_curve_result where curve_id=?', args: [id]);
    return wineCurveResult.length > 0 ? wineCurveResult[0] : null;
  }

  Future<List<Map<String, dynamic>>> queryWineCurveResultList() async {
    return await query('select * from wine_curve_result');
  }

  Future<int> updateWineCurveResult(
      Map<String, dynamic> wineCurveResult) async {
    return await update(
        'update wine_curve_result set curve_id=?,degree=?,type=?,is_error=? where curve_id=?',
        args: [
          wineCurveResult['curve_id'],
          wineCurveResult['degree'],
          wineCurveResult['type'],
          wineCurveResult['is_error'],
          wineCurveResult['curve_id']
        ]);
  }

  Future<int> deleteWineCurveResult(num id) async {
    return await delete('delete from wine_curve_result where id=?', args: [id]);
  }

  Future<int> insertWineTypeMap(Map<String, dynamic> wineTypeMap) async {
    var id = await insert(
        'insert into wine_type_map(wine_id,type_id) values(?,?)',
        args: [wineTypeMap['wine_id'], wineTypeMap['type_id']]);
    return id;
  }

  Future<Map<String, dynamic>> queryWineTypeMap(num id) async {
    var wineTypeMap =
        await query('select * from wine_type_map where id=?', args: [id]);
    return wineTypeMap.length > 0 ? wineTypeMap[0] : null;
  }

  //query by type_id
  Future<List<Map<String, dynamic>>> queryWineTypeMapListByTypeId(
      num typeId) async {
    return await query('select * from wine_type_map where type_id=?',
        args: [typeId]);
  }

  //query by wine_id
  Future<List<Map<String, dynamic>>> queryWineTypeMapListByWineId(
      num wineId) async {
    return await query('select * from wine_type_map where wine_id=?',
        args: [wineId]);
  }

  //查询所有没有wine_type_map的wine
  Future<List<Map<String, dynamic>>> queryWineListNoTypeMap() async {
    return await query(
        'select * from wine where id not in (select wine_id from wine_type_map)');
  }

  Future<List<Map<String, dynamic>>> queryWineTypeMapList() async {
    return await query('select * from wine_type_map');
  }

  Future<int> updateWineTypeMap(Map<String, dynamic> wineTypeMap) async {
    return await update(
        'update wine_type_map set wine_id=?,type_id=? where id=?',
        args: [
          wineTypeMap['wine_id'],
          wineTypeMap['type_id'],
          wineTypeMap['id']
        ]);
  }

  Future<int> deleteWineTypeMap(num id) async {
    return await delete('delete from wine_type_map where id=?', args: [id]);
  }

  Future<int> deleteWineTypeMapByWineId(num id) async {
    return await delete('delete from wine_type_map where wine_id=?', args: [id]);
  }

  //保存一个文件到数据库里需要几个操作
  //新建白酒，或者用之前的
  //给白酒一个类别
  //关联白酒和类别
  //保存曲线为文件，返回路径
  //把文件存入curve result中
  Future<String> saveCurveResult(
      int wineId,
      int typeId, //type 只能在数据库添加类别的地方新建
      double resultDegree,
      String resultType,  // 品牌分类
      List<int> curveData,
      String wineCurveDescribe,
      [String curveFile]) async {
    curveFile ??= Uuid().v4();

    if(typeId!=null) {
      var types = await queryWineTypeMapListByWineId(wineId);
      if (types.length > 0) {
      //  跳过，不要插入
      }else{
        await insertWineTypeMap({'wine_id': wineId, 'type_id': typeId});
      }
    }
    
    
    await PersistentFiles(fileId: curveFile).writeCurve(curveData);
    await insertWineCurve({'curve_file':curveFile,'wine_id':wineId,'curve_desc':wineCurveDescribe});

    var curveResultId = await insertWineCurveResult(
        {'curve_id': curveFile, 'degree': resultDegree, 'type': resultType,'is_error':0});
    return curveFile;
  }
}
