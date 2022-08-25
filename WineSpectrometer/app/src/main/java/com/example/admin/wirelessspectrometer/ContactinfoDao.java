package com.example.admin.wirelessspectrometer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * 数据(库)访问层
 */
public class ContactinfoDao {
    MydbHeper mMydbHeper;                       //自定义数据库
    public static String CURVE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "curve/";
    static String TAG = ContactinfoDao.class.getSimpleName();

    public ContactinfoDao(Context context) {
        mMydbHeper = new MydbHeper(context);
    }

    /**
     * 添加数据库
     *
     * @param name       名称
     * @param laserPower 激光强度
     * @param specturm   光谱数据
     * @return 都转化为字符存储
     */
    public long addData(String name, int laserPower, int[] specturm, int leftEdge, int rightEdge, int minData) {
        String laser;
        byte[] SPT = new byte[10240];
        byte[] TS = new byte[8];
        int newLen = rightEdge - leftEdge;
        int[] solveData = new int[newLen];
        int[] solveData2 = new int[specturm.length];
        for (int i = 0; i < specturm.length; i++) {
            solveData2[i] = specturm[i];
        }
        for (int i = 0; i < newLen; i++) {
            solveData[i] = specturm[i + leftEdge] - minData;
        }
        //写入文件
        boolean b = saveCurve2txt(name, solveData2);
        if (b) {
            Log.i(TAG, "addData: 写入文件成功！");
//            return row;
//            return 1;
        } else {
            Log.i(TAG, "addData: 写入文件失败！");
        }
        SQLiteDatabase writableDatabase = mMydbHeper.getWritableDatabase(); //传入数据库实例
        ContentValues values = new ContentValues();                         //存储机制、操作数据库
        laser = String.valueOf(laserPower);
        String lk = solveData.toString();
        Log.i("test", String.format(lk));
        Gson gson = new Gson();
        String StrSpt = gson.toJson(solveData);
        int a = StrSpt.length();
        Log.i("test", String.format(StrSpt));
        Log.i("test", String.format("a = %d", a));
        //增
        values.put("name", name);
        values.put("laser", laser);
        values.put("spectrum", StrSpt);
        int num = DataNum();    //总行数
        writableDatabase.execSQL("update sqlite_sequence set seq = " + num + " where name = 'contactinfo';");
        long row = writableDatabase.insert("contactinfo", null, values);

        return row;
    }

    /**
     * 保存曲线到文件
     *
     * @param name 文件名
     */
    public static boolean saveCurve2txt(String name, int[] curve) {
//        File file = new File(CURVE_PATH + name);
        String str = "";
        for (int i = 0; i < curve.length; i++) {
            str += i;
            str += " ";
            str += curve[i];
            if (i < curve.length - 1) {
                str += '\n';
            }
        }
//        str=str.trim();
        return writeTxtToFile(str, CURVE_PATH, name + ".txt");//对保存的设备ID加密保存
    }

    /**
     * 删除数据
     *
     * @param del_id 删除的id
     * @return
     */
    public int deleteData(int del_id) {
        SQLiteDatabase writableDatabase = mMydbHeper.getWritableDatabase();

        int result = writableDatabase.delete("contactinfo", "_id=?", new String[]{String.valueOf(del_id)});
        return result;
    }

    public int IntensityJudge(int judge_id) {
        SQLiteDatabase db = mMydbHeper.getWritableDatabase();
        Cursor cursor = db.query("contactubfo", new String[]{"laser"}, "_id=?", new String[]{String.valueOf(judge_id)}, null, null, null);
        int intensity = Integer.valueOf(cursor.getString(cursor.getColumnIndex("laser")));
        cursor.close();

        return intensity;
    }
    // 返回查找的光谱数据

    /**
     * 通过id 查找对应光谱数据
     *
     * @param alert_id 对应id
     * @return 光谱数据
     */
    public String alertData(int alert_id) {
        SQLiteDatabase writableDatabase = mMydbHeper.getWritableDatabase();

        Cursor cursor = writableDatabase.query("contactinfo", new String[]{"spectrum"}, "_id=?", new String[]{String.valueOf(alert_id)}, null, null, null);
        String spectrum = null;
        if (cursor.moveToNext()) {       //移动cursor到下一行
            spectrum = cursor.getString(0);
        }
        cursor.close();
        writableDatabase.close();
        return spectrum;
    }
    // 返回查找的名称

    /**
     * 通过id 查找对应名称
     *
     * @param alert_id
     * @return 对应名称
     */
    public String alertNameData(int alert_id) {
        SQLiteDatabase writableDatabase = mMydbHeper.getWritableDatabase();

        Cursor cursor = writableDatabase.query("contactinfo", new String[]{"name"}, "_id=?", new String[]{String.valueOf(alert_id)}, null, null, null);
        String name = null;
        if (cursor.moveToNext()) {
            name = cursor.getString(0);
        }
        cursor.close();
        writableDatabase.close();
        return name;
    }

    // 判断输入名称是否重复
    public boolean alertName(String alert_name) {      //MainShowData，名字是否重复
        SQLiteDatabase writableDatabase = mMydbHeper.getWritableDatabase();

        Cursor cursor = writableDatabase.query("contactinfo", new String[]{"name"}, "name=?", new String[]{alert_name}, null, null, null);
        String spectrum = null;
        boolean flag = false;
        if (cursor.moveToNext()) {
            spectrum = cursor.getString(0);
            flag = true;
        }
        cursor.close();
        writableDatabase.close();
        return flag;
    }


    public void sort(int cur_id) {       //id更新
        SQLiteDatabase writableDatabase = mMydbHeper.getWritableDatabase();
        writableDatabase.execSQL("update contactinfo set _id=_id-1 where _id>" + cur_id + ";");
    }

    public int DataNum() {       //总行数
        SQLiteDatabase writableDatabase = mMydbHeper.getWritableDatabase();
        Cursor cursor = writableDatabase.rawQuery("Select * from contactinfo", null);
        int num = cursor.getCount();            //返回cursor中的行数
        cursor.close();
        return num;
    }
    //返回符合强度条件的id

    /**
     * 返回筛选的对应强度的id
     *
     * @param p   激光强度
     * @param num 强度数量
     * @return 对应id
     */
    public int[] id(int p, int num) { //p 激光强度 num筛选数量
        int[] res = new int[num];
        int i = 0;
        String p1 = String.valueOf(p - 1);
        String p0 = String.valueOf(p);
        String p2 = String.valueOf(p + 1);
        SQLiteDatabase writableDatabase = mMydbHeper.getWritableDatabase();
        //暂时去除强度限制条件
        Cursor cursor = writableDatabase.rawQuery("select _id from contactinfo where laser =? or laser =? or laser =?", new String[]{p0, p1, p2});
//        Cursor cursor = writableDatabase.rawQuery("select _id from contactinfo",null);
        if (cursor.moveToFirst()) {
            do {
                res[i] = cursor.getInt(0);
                Log.i("contrast_source", res[i] + "");
                i++;
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }
    //计算符合强度条件的数量，p为强度档位值

    /**
     * 筛选符合强度的数量
     *
     * @param p 激光强度
     * @return 符合强度要求总的数量
     */
    public int number(int p) {//szhy 返回满足条件数量,传入强度值
        String p1 = String.valueOf(p - 1);
        String p0 = String.valueOf(p);
        String p2 = String.valueOf(p + 1);
        SQLiteDatabase writableDatabase = mMydbHeper.getWritableDatabase();
        //暂时去除强度限制条件
        Cursor cursor = writableDatabase.rawQuery("select * from contactinfo where laser =? or laser =? or laser =?", new String[]{p0, p1, p2});
//        Cursor cursor = writableDatabase.rawQuery("select * from contactinfo",null);
//        Cursor cursor = writableDatabase.query("contactinfo",new  String[]{"laser"}, "laser=?", new String[]{p0}, null, null, null);
//        Cursor cursor = writableDatabase.query("contactinfo",new String[]{"laser"},"laser =? or laser =? or laser =?",new String[]{p0,p1,p2},null,null,null);
        int num = cursor.getCount();
        cursor.close();
        return num;
    }
    //返回满足条件的光谱数据，传入数量num和强度值p

    /**
     * 返回对应的光谱数据
     *
     * @param num 符合条件的数量
     * @param p   激光强度
     * @return
     */
    public String[] table_num(int num, int p) {//szhy 返回满足条件的光谱数据，传入数量和强度值
        String p1 = String.valueOf(p - 1);
        String p0 = String.valueOf(p);
        String p2 = String.valueOf(p + 1);
        String[] data = new String[num];
        int a = 0;
        SQLiteDatabase writableDatabase = mMydbHeper.getWritableDatabase();
        //暂时去除强度限制条件
        Cursor cursor = writableDatabase.rawQuery("select * from contactinfo where laser =? or laser =? or laser =?", new String[]{p0, p1, p2});
//        Cursor cursor = writableDatabase.rawQuery("select * from contactinfo",null);

//       Cursor cursor = writableDatabase.query("contactinfo",new  String[]{"laser"}, "laser=?", new String[]{p0}, null, null, null);
//          Cursor cursor = writableDatabase.query("contactinfo",new String[]{"laser"},"laser =? or laser =? or laser =?",new String[]{p0,p1,p2},null,null,null);
        if (cursor.moveToFirst()) {
            do {
                /**
                 * 这种写法 data 不会越界崩溃，因为他在{@link DataContrast#Contrast}中先进行了一次
                 * spectrum检索个数，然后把 个数和spectrum传入到本函数中 ,所以是刚刚好
                 */
                data[a] = cursor.getString(cursor.getColumnIndex("spectrum"));
                a++;
            } while (cursor.moveToNext());
        }
        cursor.close();
        return data;
    }

    public String[] AllData(int num) {
        String[] Data = new String[num];
        int a = 0;
        SQLiteDatabase writableDatabase = mMydbHeper.getWritableDatabase();
        Cursor cursor = writableDatabase.rawQuery("Select * from contactinfo", null);
        if (cursor.moveToFirst()) {
            do {
                Data[a] = cursor.getString(cursor.getColumnIndex("spectrum"));          //返回列索引
                a++;
            } while (cursor.moveToNext());
        }
        cursor.close();
        return Data;
    }

    // table correct_power 2020.1.3 szhy

    /**
     * 从指令表中查询对应指令
     *
     * @param ins_id 档位
     * @return 对应档位指令
     */
    public String getinstru(int ins_id) {        //查找指令

        /**
         * 就十个字段，这里完全没必要存一个数据库
         */
        String instruct = null;
        SQLiteDatabase writableDatabase = mMydbHeper.getWritableDatabase();
        Cursor cursor = writableDatabase.rawQuery("select power_instru from correct_power where num_id =?", new String[]{String.valueOf(ins_id)});
        if (cursor.moveToNext()) {
            instruct = cursor.getString(0);
        }
        cursor.close();
        writableDatabase.close();
        return instruct;
    }

    /**
     * 更新指令
     *
     * @param num   档位
     * @param power 档位指令
     */
    public void Uppower(int num, String power) {     //更新指令
        ContentValues contentValues = new ContentValues();
        contentValues.put("power_instru", power);
        String whereid = "num_id =?";
        String[] whereargs = {String.valueOf(num)};
        SQLiteDatabase writableDatabase = mMydbHeper.getWritableDatabase();
        writableDatabase.update("correct_power", contentValues, whereid, whereargs);
    }

    /**
     * 字符串写入本地txt
     *
     * @param strcontent 文件内容
     * @param filePath   文件地址
     * @param fileName   文件名
     * @return 写入结果
     */
    private static boolean writeTxtToFile(String strcontent, String filePath, String fileName) {
        boolean isSavaFile = false;
        makeFilePath(filePath, fileName);
        String strFilePath = filePath + fileName;
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
            isSavaFile = true;
        } catch (Exception e) {
            isSavaFile = false;
            Log.e("TestFile", "Error on write File:" + e);
        }
        return isSavaFile;
    }

    /**
     * 生成文件
     *
     * @param filePath 文件地址
     * @param fileName 文件名
     */
    private static File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 生成文件夹
     */
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e + "");
        }
    }

//    /**
//     * 读取本地文件
//     */
//    public static String readDeviceId() {
//        String path = Constant.PATH_SAVE_DEVICE + fileName;
//        StringBuilder stringBuilder = new StringBuilder();
//        File file = new File(path);
//        if (!file.exists()) {
//            return "";
//        }
//        if (file.isDirectory()) {
//            Log.e("TestFile", "The File doesn't not exist.");
//            return "";
//        } else {
//            try {
//                InputStream instream = new FileInputStream(file);
//                if (instream != null) {
//                    InputStreamReader inputreader = new InputStreamReader(instream);
//                    BufferedReader buffreader = new BufferedReader(inputreader);
//                    String line;
//                    while ((line = buffreader.readLine()) != null) {
//                        stringBuilder.append(line);
//                    }
//                    instream.close();
//                }
//            } catch (java.io.FileNotFoundException e) {
//                Log.e("TestFile", "The File doesn't not exist.");
//                return "";
//            } catch (IOException e) {
//                Log.e("TestFile", e.getMessage());
//                return "";
//            }
//        }
//        return Base64Util.decode(stringBuilder.toString());//对读到的设备ID解密
//    }
//
//    /**
//     * 读取本地文件
//     */
//    public static String readRate() {
//        String path = Constant.PATH_RATE + rateName;
//        StringBuilder stringBuilder = new StringBuilder();
//        File file = new File(path);
//        if (!file.exists()) {
//            return "";
//        }
//        if (file.isDirectory()) {
//            Log.e("TestFile", "The File doesn't not exist.");
//            return "";
//        } else {
//            try {
//                InputStream instream = new FileInputStream(file);
//                if (instream != null) {
//                    InputStreamReader inputreader = new InputStreamReader(instream);
//                    BufferedReader buffreader = new BufferedReader(inputreader);
//                    String line;
//                    while ((line = buffreader.readLine()) != null) {
//                        stringBuilder.append(line);
//                    }
//                    instream.close();
//                }
//            } catch (java.io.FileNotFoundException e) {
//                Log.e("TestFile", "The File doesn't not exist.");
//                return "";
//            } catch (IOException e) {
//                Log.e("TestFile", e.getMessage());
//                return "";
//            }
//        }
//        return stringBuilder.toString();//对读到的设备ID解密
//    }
}
