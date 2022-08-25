package com.example.admin.wirelessspectrometer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 数据库创建相关的代码
 * 创建的数据库有
 * correct_power : 1-10 每个挡位对应的socket指令，重要，挡位越高，强度越大，真正的强度不是1-10
 * 强度调节见{@link LaserPower}的注释
 * correct_power不是始终不变的，它会在{@link ContactinfoDao#Uppower}里发生更新
 * contactinfo ：
 */
public class MydbHeper extends SQLiteOpenHelper {
    public MydbHeper(Context context) {
        super(context, "Sky.db", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) { //创建时的回调
        Log.d("MydbHelper", "创建数据库onCreate");
        // 光谱数据表
        sqLiteDatabase.execSQL("create table contactinfo("
                + " _id INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + " name varchar not null , "
                + " laser varchar not null , "
                + " spectrum varchar not null )");
        // 强度指令表
        sqLiteDatabase.execSQL("create table if not exists correct_power("
                + " num_id INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + " power_instru varchar )");

        sqLiteDatabase.execSQL("insert into correct_power Values(1,\"$L\t#\")");
        sqLiteDatabase.execSQL("insert into correct_power Values(2,\"$M #\")");
        sqLiteDatabase.execSQL("insert into correct_power Values(3,\"$M-#\")");
        sqLiteDatabase.execSQL("insert into correct_power Values(4,\"$M6#\")");
        sqLiteDatabase.execSQL("insert into correct_power Values(5,\"$M<#\")");
        sqLiteDatabase.execSQL("insert into correct_power Values(6,\"$MB#\")");
        sqLiteDatabase.execSQL("insert into correct_power Values(7,\"$MG#\")");
        sqLiteDatabase.execSQL("insert into correct_power Values(8,\"$MJ#\")");
        sqLiteDatabase.execSQL("insert into correct_power Values(9,\"$MM#\")");
        sqLiteDatabase.execSQL("insert into correct_power Values(10,\"$HO#\")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {//升级时的回调
        sqLiteDatabase.execSQL("alter table contactinfo add account varchar(20)");
    }
}
