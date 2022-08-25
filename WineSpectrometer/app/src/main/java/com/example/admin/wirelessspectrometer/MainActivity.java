package com.example.admin.wirelessspectrometer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;

/**
 * 程序入口，左侧部分为 {@link MainShowData}，右侧有三个固定按钮，他们的子按钮在
 * {@link MainShowData}中的右侧显示
 * 三个按钮{@link MainActivity#onCreate} 中替换fragment实现，绝大部分按钮在{@link MainActivity#show()}函数中
 */
public class MainActivity extends FragmentActivity implements Database.CallBackValue {

    private FragmentManager manager;
    private FragmentTransaction transaction;
    private RadioButton rb_show, rb_parameters, rb_database;      //显示、参数、数据库
    private View statusBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {        //创建Fragment
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);          //启用窗体扩展特性-无标题
        setContentView(R.layout.activity_main);
        initView();
        initStatusBar();
        StatusBarUtil.setStatusBarLightMode(getWindow());


        rb_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show();
            }
        });

        rb_parameters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transaction = manager.beginTransaction();
                transaction.replace(R.id.content_layout, new Parameters());
                transaction.commit();
                rb_parameters.setTextColor(Color.BLACK);
                rb_show.setTextColor(Color.WHITE);
                rb_database.setTextColor(Color.WHITE);
            }
        });

        rb_database.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transaction = manager.beginTransaction();
                transaction.replace(R.id.content_layout, new Database());
                transaction.commit();
                rb_database.setTextColor(Color.BLACK);
                rb_parameters.setTextColor(Color.WHITE);
                rb_show.setTextColor(Color.WHITE);
            }
        });

//        manager = getFragmentManager();
//        transaction = manager.beginTransaction();
//        transaction.add(R.id.content_layout,new Show());
//        transaction.commit();
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        transaction.add(R.id.content_layout, new MainShowData());
        transaction.commit();
        rb_show.setTextColor(Color.BLACK);

    }

    public void show() {
        transaction = manager.beginTransaction();
        transaction.replace(R.id.content_layout, new MainShowData());  //类
        transaction.commit();
        rb_show.setTextColor(Color.BLACK);
        rb_parameters.setTextColor(Color.WHITE);
        rb_database.setTextColor(Color.WHITE);
    }

    public void initView() {
        rb_show = findViewById(R.id.rb_show);
        rb_parameters = findViewById(R.id.rb_search);
        rb_database = findViewById(R.id.rb_database);
    }

    @Override
    public void SendMessageValue(String strValue) {
        Intent intent = new Intent(MainActivity.this, ShowData.class); //第一个参数Context要求提供一个启动活动的上下文，第二个Class指定要启动的目标活动
        intent.putExtra("data", strValue);                                       // 存数据，发送到showdata中
        startActivity(intent);
    }


    private void initStatusBar() {
        if (statusBarView == null) {
            int identifier = getResources().getIdentifier("statusBarBackground", "id", "android");
            statusBarView = getWindow().findViewById(identifier);
        }
        if (statusBarView != null) {
            statusBarView.setBackgroundDrawable(null);//在设置前将背景设置为null;
            statusBarView.setBackgroundResource(0); //这样也可以  statusBarView.setBackgroundResource(MVPConfig.statusDrawable); } }


        }
    }
}
