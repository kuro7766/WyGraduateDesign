package com.example.admin.wirelessspectrometer;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * 按钮最多的fragment，核心界面，重点理解，右侧有一排按钮，左侧是光谱曲线图
 */

public class MainShowData extends android.support.v4.app.Fragment {
    String TAG = MainShowData.class.getSimpleName();
    private Button btn_laser, btn_save, btn_contrast;
    //    private ImageButton btn_image;                      //
    private ToggleButton btn_pause, show_switch, btn_auto, btn_correct_power;
    PopupMenu popupMenu = null;

    private XYMultipleSeriesRenderer renderer;
    private XYMultipleSeriesDataset dataset;
    private LinearLayout graphView;
    private GraphicalView chart;
    private XYSeries series, series2;
    private WifiManager wifiManager;
    private TextView text_peak1, text_peak2, text_peak3, text_peak4;
    private ProgressBar progressBar;
    int jiaozhun_success_times = 0;
    /**
     * mhandler : 事件总线，各种事件，写的特别乱
     * handler: 只起到了显示激光强度的作用
     */
    private Handler mhandler, handler, shandler;
    /**
     * mThread: 手动测量socket线程
     * cthread:
     * sthread:
     */
    private Thread mThread, cthread, sthread;
    private boolean flage = true;
    private boolean flage1 = true;
    private boolean RUN = true, RUN1 = true;
    private int[] yy0;
    private int[] yy2;
    String pre_content = "激光强度:";

    private int leftEdge = 0, rightEdge = 1280; //截取有用部分

    int mBitmapWidth = 0;
    int mBitmapHeight = 0;

    int maxpixel_num, medium_num = 5, maxpixel_num_avg;       //szhy，初始1 为10时候激光能量太高
    int decimal = 79; //初始最高档位十进制数值
    int decimal9, decimal8, decimal7, decimal6, decimal5, decimal4, decimal3, decimal2;
    int modify_num = 0; //校准更改值
    int[] maxpixel_num_array = new int[5];
    int max_data;      //最高点值 校准判断
    int min_data;      //最小点值 校准判断
    int raman = 1;         //szhy校准时根据水拉曼峰判断
    int raman_10 = 1;     //校准最高档时拉曼值
    int power_num = 10; //校准时强度档位
    int toast_flag = 0; //校准失败，隔一次一显示
    int maxpixel1, maxpixel2, maxpixel_result;
    int peak1 = 1, peak2 = 1, peak3 = 1, peak4 = 1;    //测纸用的
    int pre_value, same_count;
    int pre_value1 = 100, pre_value2 = 200, pre_value3 = 300, third_same_flag;
    int p1 = 10, p2 = 20, p3 = 30;
    int p_flag = 1;

//    double[][] pixel1 = new double[5][1280 * 720];      //灰度 getpixel
//    double[][] pixels1 = new double[5][1280 * 720];     //列之和

    double max_axis;
    boolean pasue_flag = false;
    boolean correct_flag = false;
    boolean flag_switch, flag_correct;   //判断两个按钮是否执行

    /**
     * string5和{@link MydbHeper}中一致
     * {@link MainShowData#string10}在{@link MydbHeper}中是H0，不知道是不是作者写错了，另{@link LaserPower#String10}
     */
    static String start_flag = "$", end_flag = "#", string10 = "$MO#", string5 = "$M<#";

    /**
     * {@link MainShowData#string1}$M# \u0002是个无法显示的字符，并且在{@link MydbHeper}中也没有找到
     * {@link MainShowData#string55}莫名奇妙，5档应该是有特殊含义?
     * 此处声明只有string1有值，其他全为null，作者此处的意思是否为仅初始化{@link MainShowData#string1}?
     * <p>
     * 他们何时初始化?
     * <p>
     * 数据库里correct_power是动态变化的，这里也是吗
     */
    static String string9, string8, string7, string6, string55, string4, string3, string2, string1 = "$M\u0002#";//"$L"+"\t"+"#";


    int k = 0;
    int time = 0;
    private int LasPower = 3;
    int[] yy1 = new int[1280];
    //第二个曲线
    String str = null;
    int[] data2 = new int[2048];
    int data_time = 0;

    private int[] yy = new int[2048];//背景扣除之后
    int yy_temp[] = new int[2048];   // szhy
    private double[] xx;

    private final int MSG_LOW = 1;
    private final int MSG_MEDIUM = 2;
    private final int MSG_HIGH = 3;
    private final int ADJUST = 4;
    private String StrIP, auto_power;
    private Context mContext;
    private Activity mActivate;
    private int y_scale = 1;
    //    private boolean auto_save = false;
    private boolean auto_save = true;
    private final int MESSAGE_WHAT_AUTO_SAVE_CURVE = 10000;
    private String sampleName = "4";

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.fragment_main_show_data);
//
//    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View view = inflater.inflate(R.layout.fragment_main_show_data, container, false);
        graphView = view.findViewById(R.id.show_view);
        show_switch = view.findViewById(R.id.show_switch);
        btn_laser = view.findViewById(R.id.show_laser);
        btn_pause = view.findViewById(R.id.show_pause);
        btn_save = view.findViewById(R.id.show_save);
        btn_auto = view.findViewById(R.id.laser_correct); //自动测量按钮
        btn_correct_power = view.findViewById(R.id.correct_power);
        btn_save.setEnabled(false);
        btn_contrast = view.findViewById(R.id.show_contrast);
//        btn_contrast.setEnabled(false);
        btn_contrast.setEnabled(true);
        mContext = this.getContext();
        mActivate = this.getActivity();

//        text_peak1 = view.findViewById(R.id.peak1);
//        text_peak2 = view.findViewById(R.id.peak2);
//        text_peak3 = view.findViewById(R.id.peak3);
//        text_peak4 = view.findViewById(R.id.peak4);

        progressBar = view.findViewById(R.id.pb);

//        btn_image = view.findViewById(R.id.limage);             //

        XYMultipleSeriesRenderer RD = InitRenderer();
        XYMultipleSeriesDataset DA = InitDataset();

        chart = ChartFactory.getLineChartView(view.getContext(), DA, RD);              //折线图
//        chart = ChartFactory.getCubeLineChartView(view.getContext(),DA,RD,0.3f); //平滑曲线
        graphView.addView(chart);
        xx = new double[1280];
        for (int a = 0; a < 1280; a++) {
            xx[a] = a;
        }

        /**获取连接ip，设置网络图像url*/

        /**
         * 读取系统文件获取连接到本机热点的ip地址，后续改为扫描版
         */
        ArrayList<String> STRIP = getContectedIP();
        if (STRIP == null || STRIP.size() == 0) {
            Toast.makeText(view.getContext(), "android 版本过高，无法获取设备ip!", Toast.LENGTH_LONG).show();
        } else {
            StrIP = STRIP.toArray(new String[STRIP.size()])[0];
//            StrIP = "192.168.1.251";
            final String url = "http://" + StrIP + ":8083/?action=snapshot";

//            另一个为stream

            if (sthread != null) {
                Log.i("string_maxflag", flage1 + "");
                flage1 = false;//4.18 xiancheng wenti
                Log.i("string_maxflag", flage1 + "");
            }

            if (getIP(StrIP)) {
                Toast.makeText(view.getContext(), "无线已连接", Toast.LENGTH_LONG).show();
                new powerthread().start();      //激光强度更改
//            ModifyIntensity(10);
            } else {
                Toast.makeText(view.getContext(), "无线连接失败", Toast.LENGTH_LONG).show();
                btn_laser.setEnabled(false);
                show_switch.setEnabled(false);
            }

//        btn_laser.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onPopupButtonClick(view);       //下拉式菜单
//            }
//        });
            // 暂停
            btn_pause.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { //tButton设置监听器(通过实现OnCheckedChangeListener接口)
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {        //监听器方法
                    if (b || pasue_flag) {
                        btn_pause.setTextColor(Color.BLACK);
                        //手动测量
                        if (flag_switch) {
                            p_flag = 1;
                            RUN = false;
                            Log.i("flagbolean-pause-run", RUN + "");
                            //progressBar.setVisibility(View.GONE);//12.17night
                        }
                        //自动测量
                        if (!RUN1 && flag_correct) {
                            RUN1 = true;// 暂停后按键恢复
                            cthread.interrupt();
                            btn_pause.setTextOn("暂停1");
                            progressBar.setProgress(25);
                            progressBar.setVisibility(View.VISIBLE);
                            btn_pause.setTextOff("暂停1");
                            medium_num = 5;
                        } else {
                            RUN1 = false;
                        }
                        Log.i("flagbolean-pause-run1", RUN1 + "");
                        btn_save.setEnabled(true);
                        btn_contrast.setEnabled(true);
                    } else {
//                        btn_pause.setTextColor(Color.WHITE);
                        RUN = true;
                        if (flag_correct) RUN1 = !RUN1;
                        Log.i("flagbolean-pause-run1", RUN1 + "");
                        if (flag_switch) {
                            mThread.interrupt();
                            p_flag = 1;
                        }
                        if (flag_correct) {
                            cthread.interrupt();  //szhy 加上程序退出
                            btn_pause.setTextOff("恢复自动测量");
                            medium_num = 5;
                        }
                        btn_save.setEnabled(false);
                        btn_contrast.setEnabled(false);
                    }
                }
            });
            // 保存
            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkPermission();
                    EditInput(view);
                }
            });
            // 对比
            btn_contrast.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DataContrast dataContrast = new DataContrast();
                    Result result = dataContrast.Contrast(yy, medium_num, getActivity().getApplicationContext(), leftEdge, rightEdge, min_data);
                    int[] idList = result.getIdList();
                    double[] scoreList = result.getScoreList();
                    List<String> nameList = new ArrayList<>();
                    ContactinfoDao contactinfoDao = new ContactinfoDao(view.getContext());
                    int id = result.getId();
//                int intensity_return = contactinfoDao.IntensityJudge(id);
//                if(intensity_return>=medium_num-1&&intensity_return<=medium_num+1){
                    if (result.getMaxPs() != 0 && id != -1) {
                        for (int i = 0; i < idList.length; i++) {
                            nameList.add(contactinfoDao.alertNameData(idList[i]));
                        }
//                        String re = String.format("%.2f", result.getMaxPs() * 100);
//                    String similarity = String.valueOf (result.getMaxPs() * 100);
//                    ContactinfoDao contactinfoDao = new ContactinfoDao(view.getContext());
//                        String name = contactinfoDao.alertNameData(id);
//                        String finalresult = name + " 的相似度为" + re + "%";
                        String finalresult = "";
                        for (int i = 0; i < nameList.size(); i++) {
                            String re = String.format("%.2f", scoreList[i] * 100);
                            finalresult += nameList.get(i) + " 的相似度为" + re + "%\n";
                        }
                        DisplayResult(getContext(), finalresult, "比对结果");                        //AlertDialog
                        String backSpuctrum = contactinfoDao.alertData(id);
                        for (int i = 1; i < backSpuctrum.length(); i++) {
                            char charat = backSpuctrum.charAt(i);     //返回指定索引处字符
                            if ((charat != ',') && (charat != ']')) {
                                str = str + charat;
                            } else {
                                data2[data_time] = Integer.parseInt(str.substring(4, str.length()));
                                str = null;
                                data_time++;
                            }

                        }
                        data_time = 0;
                        for (int i = 30; i < 50; i++) {
                            Log.i("database", data2[i] + "");
                        }
                        // 绘制第二条曲线
                        Message msg = new Message();
                        msg.what = 12;
                        mhandler.sendMessage(msg);
//
//                    for(int k = 0; k<mBitmapWidth; k++){
//                        series2.add(xx[k],yy[k+500]);// two line
//                    }chart.postInvalidate();

                    } else {
                        String error = "无匹配项目相似";
                        DisplayResult(getContext(), error, "比对结果");
                    }
                }
            });
            // 强度指示

            /**
             * 激光强度handler
             */
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    btn_laser.setText(pre_content + msg.what);
                    LasPower = msg.what;
                    medium_num = msg.what;
//                    pre_content="Intensity:"
//                    switch (msg.what) {
//                        case 10:
//                            btn_laser.setText(pre_content+"10");
//                            LasPower = 10;
//                            medium_num = 10;
//                            break;
//                        case 9:
//                            btn_laser.setText("Intensity:9");
//                            LasPower = 9;
//                            medium_num = 9;
//                            break;
//                        case 8:
//                            btn_laser.setText("Intensity:8");
//                            LasPower = 8;
//                            medium_num = 8;
//                            break;
//                        case 7:
//                            btn_laser.setText("Intensity:7");
//                            LasPower = 7;
//                            medium_num = 7;
//                            break;
//                        case 6:
//                            btn_laser.setText("Intensity:6");
//                            LasPower = 6;
//                            medium_num = 6;
//                            break;
//                        case 5:
//                            btn_laser.setText("Intensity:5");
//                            LasPower = 5;
//                            medium_num = 5;
//                            break;
//                        case 4:
//                            btn_laser.setText("Intensity:4");
//                            LasPower = 4;
//                            medium_num = 4;
//                            break;
//                        case 3:
//                            btn_laser.setText("Intensity:3");
//                            LasPower = 3;
//                            medium_num = 3;
//                            break;
//                        case 2:
//                            btn_laser.setText("Intensity:2");
//                            LasPower = 2;
//                            medium_num = 2;
//                            break;
//                        case 1:
//                            btn_laser.setText("Intensity:1");
//                            LasPower = 1;
//                            medium_num = 1;
//                            break;


//                    }
                }
            };

            mhandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        /**
                         * 这里的事件应该是停止自动测量
                         */
                        case 1:
                            btn_pause.setText("恢复自动测量");
//                        btn_laser.setText("激光:5");

                            btn_contrast.setEnabled(true);
                            pasue_flag = true;
                            btn_save.setEnabled(true);
                            btn_contrast.setEnabled(true);
                            progressBar.setVisibility(View.GONE);
//                        RUN1 = true;
                            break;

                        /**
                         * 把曲线保存成文件
                         */
                        case 2:
                            if (auto_save) {
                                SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
                                sdf.applyPattern("yyyy-MM-dd=HH-mm-ss");// a为am/pm的标记
                                Date date = new Date();// 获取当前时间
                                System.out.println("现在时间：" + sdf.format(date)); // 输出已经格式化的现在时间（24小时制）
                                String NameStr = sampleName + "-" + sdf.format(date);
                                ContactinfoDao contactinfoDao = new ContactinfoDao(view.getContext());      //数据库增
                                boolean nameFlag = contactinfoDao.alertName(NameStr);                       //名字是否重复
                                if (!nameFlag) {
                                    long row = contactinfoDao.addData(NameStr, medium_num, yy, leftEdge, rightEdge, min_data);       //laspower
                                    if (row == -1) {
                                        Toast.makeText(view.getContext(), "添加失败", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(view.getContext(), "成功添加在" + row + "行", Toast.LENGTH_LONG).show();
                                    }
                                    //继续自动测量
                                    btn_auto.setChecked(false);
                                } else {
                                    Toast.makeText(view.getContext(), "名称重复，请重新输入！", Toast.LENGTH_LONG).show();
                                }

                                try {
                                    sleep(1000);
//                                btn_auto.setChecked(true);
//                                sleep(500);
//                                View listView = getActivity().findViewById(R.id.rb_show);
//                                listView.set
                                    MainActivity activity = (MainActivity) getActivity();
                                    /**
                                     * 自己覆盖自己？
                                     */
                                    activity.show();
                                    btn_pause.setChecked(!btn_pause.isChecked());
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                            }
                            break;
                        case 3:
//                        text_peak1.postInvalidate(104,100,0,0);
                            Bundle bundle1 = msg.getData();
                            int str1 = msg.getData().getInt("peak1");
                            text_peak1.setText(Integer.toString(str1));
                            int top_num = (int) (200 + (1 - bundle1.getDouble("cor1")) * 270);
                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                                    text_peak1.getLayoutParams());
                            lp.setMargins(300, top_num, 0, 0);
                            text_peak1.setLayoutParams(lp);
                            break;

                        case 4:
                            Bundle bundle2 = msg.getData();
                            int str2 = bundle2.getInt("peak2");
//                                msg.getData().getInt("peak2");
                            text_peak2.setText(Integer.toString(bundle2.getInt("peak2")));
                            int top_num2 = (int) (200 + (1 - bundle2.getDouble("cor2")) * 270);
                            RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(
                                    text_peak2.getLayoutParams());
                            lp2.setMargins(600, top_num2, 0, 0);
                            text_peak2.setLayoutParams(lp2);
                            break;

                        case 5:
                            Bundle bundle3 = msg.getData();
                            int str3 = msg.getData().getInt("peak3");
                            text_peak3.setText(Integer.toString(str3));
                            int top_num3 = (int) (200 + (1 - bundle3.getDouble("cor3")) * 270);
                            RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(
                                    text_peak3.getLayoutParams());
                            lp3.setMargins(1000, top_num3, 0, 0);
                            text_peak3.setLayoutParams(lp3);
                            break;
                        case 6:
                            Bundle bundle4 = msg.getData();
                            int str4 = msg.getData().getInt("peak4");
                            text_peak4.setText(Integer.toString(str4));
                            break;
                        case 7: //自动测量 进度条
                            progressBar.setVisibility(View.VISIBLE);
                            break;
                        case 8://自动测量 进度条
                            progressBar.setVisibility(View.GONE);
                            break;
                        case 9:
                            progressBar.setVisibility(View.GONE);
                            btn_pause.setTextOff("暂停");
                            btn_pause.setTextOn("恢复");
                            break;
                        case 10:   //校准失败

//                            AlertDialog alertDialog = new AlertDialog.Builder(view.getContext())
//                                    .setTitle("get")
//                                    .setMessage("强度达不到要求建议重试或更换激光器！")
//                                    .setIcon(R.drawable.ic_contrast_red)
//                                    .create();
//                            alertDialog.show();
                            DisplayResult(view.getContext(), "强度达不到要求建议重试或更换激光器！", "激光校准");
                            btn_correct_power.setChecked(false);

//                            Toast.makeText(view.getContext(), "强度达不到要求建议重试或更换激光器", Toast.LENGTH_SHORT).show();
                            break;
                        case 11:
//                             alertDialog = new AlertDialog.Builder(view.getContext())
//                                    .setTitle("激光校准")
//                                    .setMessage("校准成功！")
//                                    .setIcon(R.drawable.ic_contrast_red)
//                                    .create();
//                            alertDialog.show();
//                            DisplayResult(view.getContext(), "Calibration success！", "Intensity calibration");
                            DisplayResult(view.getContext(), "校准成功！", "激光校准");
                            btn_correct_power.setChecked(false);

//                            Toast.makeText(view.getContext(), "校准成功！", Toast.LENGTH_SHORT).show();

                            break;

                        case 12:  //绘制两条曲线
                            SetDataSeries2();
//                        for(int k = 0; k<mBitmapWidth; k++){
//                            series2.add(xx[k],yy[k]+500);// two line
//                        }
//                        case MESSAGE_WHAT_AUTO_SAVE_CURVE:  //自动保存曲线
//
//                            break;
                    }
                }
            };
            //自动测量
            btn_auto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean Flag) {
                    if (Flag) {
                        flag_correct = true;
                        btn_auto.setTextColor(Color.BLACK);
                        btn_pause.setEnabled(true);
                        show_switch.setEnabled(false);
//                        btn_correct_power.setEnabled(false);
                        flage1 = true;
                        time = 0;
                        Log.i("runnnnn1", RUN1 + "");
                        cthread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (flage1) {
                                    try {
                                        if (!RUN1) {
                                            sleep(Long.MAX_VALUE);
                                        }
                                        long startTime = System.currentTimeMillis();  //获取系统时间
                                        LaserSwitch laserSwitch = new LaserSwitch();
                                        laserSwitch.laserswitch(flage, StrIP);
                                        sleep(250);
                                        long startTime1 = System.currentTimeMillis();
                                        getPixels(url);
//                                    ModifyIntensity(10);
//                                    int decimal = 79 ; //初始最高档位十进制数值
                                        //TODO 进行判断更改指令
                                        Log.i("flagbolean-correct1", flag_correct + "");
                                        // Log.i("string0_anxis",max_data+"");
                                        Log.i("mmmmmmmmm_count", same_count + "");
                                        Log.i("mmmmmmmmm_falg", third_same_flag + "");

                                        if (same_count == 1 || pre_value3 == pre_value1)
                                            progressBar.setProgress(50);
                                        if (same_count == 2) progressBar.setProgress(75);
                                        /**
                                         * 三次强度相同或两次类似于1 2 1 2 循环 暂停测量，显示对比结果
                                         */
                                        if (same_count >= 3 || third_same_flag == 1) {

//                                        progressBar.setVisibility(View.GONE);
                                            btn_pause.setTextColor(Color.BLACK);
                                            progressBar.setProgress(100);// progressbar 100percent
                                            sleep(100);
                                            RUN1 = false;
                                            pre_value = 0;
                                            pre_value1 = pre_value2 = pre_value3 = 0;
                                            same_count = 0;
                                            third_same_flag = 0;
                                            //ModifyIntensity(5);
                                            Log.i("1234567", pre_value + "");
                                            Log.i("1234567", third_same_flag + "");
                                            Message msg = new Message();
                                            msg.what = 1;
                                            mhandler.sendMessage(msg);
                                            Log.i("123456", "5");
//                                        pasue_flag = true;
//                                        btn_save.setEnabled(true);
//                                        btn_contrast.setEnabled(true);
//                                        sleep(1000);
                                            Log.i("123456", "4");
                                            //int medium_temp = medium_num;
                                            DataContrast dataContrast = new DataContrast();
                                            Result result = dataContrast.Contrast(yy, medium_num, getContext(), leftEdge, rightEdge, min_data);

                                            int id = result.getId();
                                            ContactinfoDao contactinfoDao = new ContactinfoDao(getContext());
                                            Log.i("123456", "3");
                                            if (result.getMaxPs() != 0 && id != -1) {
                                                Log.i("123456", "1");
                                                String re = String.format("%.2f", result.getMaxPs() * 100);
                                                String name = contactinfoDao.alertNameData(id);
                                                String finalresult = name + " 的相似度为" + re + "%";
                                                Message msg1 = Message.obtain();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("result", finalresult);
                                                msg1.setData(bundle);
                                                msg1.what = 2;
                                                mhandler.sendMessage(msg1);
//                                            DisplayResult(getContext(),finalresult);                        //AlertDialog

                                                String backSpuctrum = contactinfoDao.alertData(id);
                                                for (int i = 1; i < backSpuctrum.length(); i++) {
                                                    char charat = backSpuctrum.charAt(i);     //返回指定索引处字符
                                                    if ((charat != ',') && (charat != ']')) {
                                                        str = str + charat;
                                                    } else {
                                                        data2[data_time] = Integer.parseInt(str.substring(4, str.length()));    //substring提取字符串中介于两个指定下标之间的字符
                                                        str = null;
                                                        data_time++;
                                                    }

                                                }
                                                data_time = 0;
                                                for (int i = 30; i < 50; i++) {
                                                    Log.i("database", data2[i] + "");
                                                }

                                                Message msg12 = new Message();
                                                msg12.what = 12;
                                                mhandler.sendMessage(msg12);

                                            } else {
                                                Log.i("123456", "2");
                                                String error = "无匹配项目相似";
                                                Message msg1 = Message.obtain();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("result", error);
                                                msg1.setData(bundle);
                                                msg1.what = 2;
                                                mhandler.sendMessage(msg1);
//                                            DisplayResult(getContext(),error);
                                            }
                                            same_count = 0;

//                                        medium_num = 5;  //自动测量结束，强度恢复为5

                                        }

//                                    pre_value=medium_num;
                                        Log.i("mmmmmmmmmmm0", medium_num + "");

                                        if (time == 1) {
                                            Log.i("mmmmmmmmmmmm3", pre_value + "");
                                            if (pre_value != medium_num) {
                                                same_count = 0;
                                            } else {
                                                ++same_count;
                                            }
                                            pre_value = medium_num;
                                            if (pre_value1 == pre_value3 && pre_value2 == medium_num) {
                                                third_same_flag = 1;
                                            } else {
                                                third_same_flag = 0;
                                            }
                                            pre_value1 = pre_value2;
                                            pre_value2 = pre_value3;
                                            pre_value3 = medium_num;

                                            Message msg_pb = new Message();
                                            msg_pb.what = 7;
                                            mhandler.sendMessage(msg_pb);
                                            /**@param maxpixel_result 图像像素饱和点个数，据此判断强度调节上限10000，下限3000
                                             * @param medium_num 强度值超过下调一，低于上调一
                                             * ModifyIntensity 强度更改函数
                                             * */
                                            if (maxpixel_result > 10000 && medium_num != 1) {//有时候初始化失败参数为0
                                                medium_num = medium_num - 1;
//                                            pre_value=medium_num;
//                                            ModifyIntensity(medium_num);

                                            }
//                                        Log.i("mmmmmmmmmmmflag",flag_judge+"");
                                            Log.i("mmmmmmmmmmm1", medium_num + "");
                                            if (maxpixel_result < 3000 && medium_num != 10) {
                                                medium_num = medium_num + 1;
//                                            pre_value=medium_num;
//                                            ModifyIntensity(medium_num);

                                            }
                                            ModifyIntensity(medium_num);
                                            Log.i("mmmmmmmmmmm2", medium_num + "");
//                                    }
                                        }
                                        Log.i("111111", 123 + "");
//                                    if (same_count>=4||third_same_flag==1){
//                                        btn_pause.setTextColor(Color.BLACK);
////                                        RUN1 = false;
//                                        pre_value=0;
//                                        pre_value1=pre_value2=pre_value3=0;
//                                        same_count=0;
//                                        third_same_flag=0;
//                                        Log.i("1234567",pre_value+"");
//                                        Log.i("1234567",third_same_flag+"");
//                                        Message msg = new Message();
//                                        msg.what = 1;
//                                        mhandler.sendMessage(msg);
//                                        Log.i("123456","5");
////                                        pasue_flag = true;
////                                        btn_save.setEnabled(true);
////                                        btn_contrast.setEnabled(true);
////                                        sleep(1000);
//                                        Log.i("123456","4");
//                                        DataContrast dataContrast = new DataContrast();
//                                        Result result = dataContrast.Contrast(yy,medium_num,getContext());
//
//                                        int id = result.getId();
//                                        ContactinfoDao contactinfoDao = new ContactinfoDao(getContext());
//                                        Log.i("123456","3");
//                                        if (result.getMaxPs() != 0&& id != -1) {
//                                            Log.i("123456","1");
//                                            String re = String.format("%.2f",result.getMaxPs() * 100);
//                                            String name = contactinfoDao.alertNameData(id);
//                                            String finalresult = name+" 的相似度为"+re+"%";
//                                            Message msg1 = Message.obtain();
//                                            Bundle bundle = new Bundle();
//                                            bundle.putString("result",finalresult);
//                                            msg1.setData(bundle);
//                                            msg1.what = 2;
//                                            mhandler.sendMessage(msg1);
////                                            DisplayResult(getContext(),finalresult);                        //AlertDialog
//                                        }else {
//                                            Log.i("123456","2");
//                                            String error = "无匹配项目相似";
//                                            Message msg1 = Message.obtain();
//                                            Bundle bundle = new Bundle();
//                                            bundle.putString("result",error);
//                                            msg1.setData(bundle);
//                                            msg1.what = 2;
//                                            mhandler.sendMessage(msg1);
////                                            DisplayResult(getContext(),error);
//                                        }
//                                        same_count=0;
//                                    }
//
//                                    pre_value=medium_num;

                                        time++;
                                        Log.i("num_timetest10/1/2", time + "");
                                        /**
                                         * time指示激光亮灭 0或1*
                                         */
                                        if (time == 2) {
                                            time = 0;
                                        }
                                        Log.i("num_timetest20/1/2", time + "");
                                        long endTime1 = System.currentTimeMillis(); //结束时间
                                        long runTime1 = endTime1 - startTime1;
                                        Log.i("test", String.format("方法使用时间 %d ms", runTime1));
                                        long endTime = System.currentTimeMillis(); //结束时间
                                        long runTime = endTime - startTime;
                                        Log.i("test", String.format("总时间 %d ms", runTime));
                                        //一个周期设置为2s，少于则延时
                                        if (runTime < 2000) {
                                            sleep(2000 - runTime);
                                        }
                                        if (flage) {
                                            flage = false;
                                        } else {
                                            flage = true;
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                        cthread.start();
                    } else {
                        Message msg_pb_gone = new Message();
                        msg_pb_gone.what = 8;
                        mhandler.sendMessage(msg_pb_gone);//12.18
                        flag_correct = false;
                        Log.i("flagbolean-correct2", flag_correct + "");
                        Log.i("flagbolean-RUN1", RUN1 + "");
                        btn_auto.setTextColor(Color.WHITE);
                        show_switch.setEnabled(true);
                        btn_correct_power.setEnabled(true);
                        if (btn_pause.isChecked()) {
                            btn_pause.setChecked(false);
                        }
                        btn_pause.setEnabled(false);
                        flage1 = false;
                        Log.i("flaggg", flage1 + "");
                        try {
                            sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //imageView.setImageDrawable(null);
                        ClearDataSeries();
                    }
                }
            });
            if (auto_save) {
                btn_auto.setChecked(true);

            }
            //手动测量按钮
            show_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(final CompoundButton compoundButton, boolean b) {
                    if (b) {
//                    ContactinfoDao contactinfoDao = new ContactinfoDao(graphView.getContext());
//                    //string1 = contactinfoDao.getinstru(1);
//                    string2 = contactinfoDao.getinstru(2);
//                    Log.i("database-str2",string2+"");
//                    string3 = contactinfoDao.getinstru(3);
//                    Log.i("database-str3",string3+"");
//                    string4 = contactinfoDao.getinstru(4);
//                    Log.i("database-str4",string4+"");
//                    string5 = contactinfoDao.getinstru(5);
//                    Log.i("database-str5",string5+"");
//                    string6 = contactinfoDao.getinstru(6);
//                    Log.i("database-str6",string6+"");
//                    string7 = contactinfoDao.getinstru(7);
//                    Log.i("database-str7",string7+"");
//                    string8 = contactinfoDao.getinstru(8);
//                    Log.i("database-str8",string8+"");
//                    string9 = contactinfoDao.getinstru(9);
//                    Log.i("database-str9",string9+"");
//                    string10 = contactinfoDao.getinstru(10);
//                    Log.i("database-str10",string10+"");
                        Message msg_pb_gone = new Message();
                        msg_pb_gone.what = 8;
                        mhandler.sendMessage(msg_pb_gone);
                        //12.18
                        flag_switch = true;
                        Log.i("flagbolean-runnnnn1", RUN1 + "");
                        Log.i("flagbolean-switch1", flag_switch + "");
                        show_switch.setTextColor(Color.BLACK);
                        btn_pause.setEnabled(true);
                        btn_auto.setEnabled(false);
                        btn_correct_power.setEnabled(false);
                        flage1 = true;
                        time = 0;
                        mThread = new Thread(new Runnable() {

                            @Override
                            public void run() {
                                while (flage1) {
                                    try {
//                                    ModifyIntensity(medium_num);
                                        if (!RUN) {
                                            sleep(Long.MAX_VALUE);
                                        }

                                        Log.i("qiangdu", "run:1 ");

                                        long startTime = System.currentTimeMillis();  //获取系统时间
                                        LaserSwitch laserSwitch = new LaserSwitch();

                                        Log.i("flagggg", flage + "");
                                        laserSwitch.laserswitch(flage, StrIP);
                                        sleep(250);
                                        long startTime1 = System.currentTimeMillis();
                                        getPixels(url);
                                        Log.i("qiangdu", "run:2 ");

                                        Log.i("runnnnn3", RUN1 + "");
                                        Log.i("maxnumber", maxpixel_num + "");
                                        Log.i("maxnumresult", maxpixel_result + "");
                                        Log.i("maxnu_medium_number", medium_num + "");
//                                            //==1 最低档无法进行下调，不调节while陷入死循环
                                        if (time == 1) {
                                            if (p1 == p3 && p2 == medium_num) {
                                                p_flag = 0;
                                                Log.i("biaozhiwei ", p_flag + "");
                                                p1 = 10;
                                                p2 = 20;
                                                p3 = 30;
                                            }
                                            p1 = p2;
                                            p2 = p3;
                                            p3 = medium_num;

                                            if (maxpixel_result > 10000 && medium_num != 1 && p_flag == 1) {//有时候初始化失败参数为0
                                                medium_num = medium_num - 1;
                                                ModifyIntensity(medium_num);
                                            } else if (maxpixel_result < 3000 && medium_num != 10 && p_flag == 1) {
                                                medium_num = medium_num + 1;
                                                ModifyIntensity(medium_num);
                                            } else {

                                            }
                                        }
//                                    if (medium_num == 3 || medium_num == 2) {
//                                        btn_pause.setTextColor(Color.BLACK);
//                                        RUN = false;
//                                        Message msg = new Message();
//                                        msg.what = 1;
//                                        mhandler.sendMessage(msg);
//                                        Log.i("123456","5");
////                                        pasue_flag = true;
////                                        btn_save.setEnabled(true);
////                                        btn_contrast.setEnabled(true);
////                                        sleep(1000);
//                                        Log.i("123456","4");
//                                        DataContrast dataContrast = new DataContrast();
//                                        Result result = dataContrast.Contrast(yy,medium_num,getContext());
//
//                                        int id = result.getId();
//                                        ContactinfoDao contactinfoDao = new ContactinfoDao(getContext());
//                                        Log.i("123456","3");
//                                        if (result.getMaxPs() != 0&& id != -1) {
//                                            Log.i("123456","1");
//                                            String re = String.format("%.2f",result.getMaxPs() * 100);
//                                            String name = contactinfoDao.alertNameData(id);
//                                            String finalresult = name+" 的相似度为"+re+"%";
//                                            Message msg1 = Message.obtain();
//                                            Bundle bundle = new Bundle();
//                                            bundle.putString("result",finalresult);
//                                            msg1.setData(bundle);
//                                            msg1.what = 2;
//                                            mhandler.sendMessage(msg1);
////                                            DisplayResult(getContext(),finalresult);                        //AlertDialog
//                                        }else {
//                                            Log.i("123456","2");
//                                            String error = "无匹配项目相似";
//                                            Message msg1 = Message.obtain();
//                                            Bundle bundle = new Bundle();
//                                            bundle.putString("result",error);
//                                            msg1.setData(bundle);
//                                            msg1.what = 2;
//                                            mhandler.sendMessage(msg1);
////                                            DisplayResult(getContext(),error);
//                                        }
//                                    }

                                        time++;
                                        Log.i("num_timetest10/1/2", time + "");
                                        if (time == 2) {
                                            time = 0;
                                        }
                                        Log.i("num_timetest20/1/2", time + "");
                                        long endTime1 = System.currentTimeMillis(); //结束时间
                                        long runTime1 = endTime1 - startTime1;
                                        Log.i("test", String.format("方法使用时间 %d ms", runTime1));
                                        long endTime = System.currentTimeMillis(); //结束时间
                                        long runTime = endTime - startTime;
                                        Log.i("test", String.format("总时间 %d ms", runTime));

                                        //imageView.setImageBitmap(bitmap);
                                        if (runTime < 2000) {
                                            sleep(2000 - runTime);
                                        }
                                        if (flage) {
                                            flage = false;
                                        } else {
                                            flage = true;
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();

                                    }
                                }
                            }
                        });
                        mThread.start();                                        //启动线程
                    } else {

//                    if (mThread!=null&&mThread.isAlive()){
//                        mThread.interrupt();
//                        mThread.stop();
//                    }
                        flag_switch = false;
                        Log.i("flagbolean", flag_switch + "");
                        Message msg_pb_gone = new Message();
                        msg_pb_gone.what = 8;
                        mhandler.sendMessage(msg_pb_gone);
                        show_switch.setTextColor(Color.WHITE);
                        btn_auto.setEnabled(true);
                        btn_correct_power.setEnabled(true);
                        if (btn_pause.isChecked()) {
                            btn_pause.setChecked(false);
                        }
                        btn_pause.setEnabled(false);
                        flage1 = false;
                        try {
                            sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //imageView.setImageDrawable(null);
                        ClearDataSeries();
                    }
                }
            });

//        btn_correct_power.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ContactinfoDao contactinfoDao = new ContactinfoDao(view.getContext());
//                contactinfoDao.Uppower(2,"$r1#");
//                Log.i("database",string2+"");
//            }
//        });
            //校准
            btn_correct_power.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        btn_correct_power.setTextColor(Color.BLACK);
                        btn_auto.setEnabled(false);
                        btn_pause.setEnabled(true);
                        show_switch.setEnabled(false);


                        // ModifyIntensity(5);

                        flage1 = true;
                        correct_flag = true;
                        time = 0;
                        sthread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (flage1) {
                                    try {
                                        if (!RUN) {
                                            sleep(Long.MAX_VALUE);
                                        }
                                        long startTime = System.currentTimeMillis();  //获取系统时间

//                                LaserPower laserPower = new LaserPower();
//                                Log.i("string_10", string10 + "");
//                                int ACCEPT = laserPower.LPower(string10,StrIP); //10.24gai power string10 11.19 初始5
//
//                                Message message = Message.obtain();
////            Message message1 = Message.obtain(message);
//                                message.what = ACCEPT;
//                                handler.sendMessage(message);
//                                Log.i("string_yes",  "yes");

                                        LaserSwitch laserSwitch = new LaserSwitch();
                                        laserSwitch.laserswitch(flage, StrIP);
                                        ModifyIntensity(power_num);
                                        sleep(250);
                                        long startTime1 = System.currentTimeMillis();
                                        getPixels(url);
//                                    ModifyIntensity(10);
//                                    int decimal = 79 ; //初始最高档位十进制数值
                                        //TODO 进行判断更改指令
//                                string5 = string5.replace(string5.charAt(0),'$');
//                                string4 = string4.replace(string4.charAt(3),'#');
                                        Log.i("string_55", string5 + "");
                                        Log.i("string_correctflag", correct_flag + "");

                                        Log.i("string0_anxis", max_data + "");

                                        boolean flag_judge = true;
                                        if (time == 1) {
                                            Log.i("string0", string10 + "");
                                            /**根据水拉曼值 raman 判断强度调整 范围14000-16000
                                             * decimal 强度对应ascii 十进制表示，一次调整大小为2，上下限66，99
                                             * power_num 强度档位
                                             * **/
                                            Log.i(TAG, "；校准raman=" + raman + ";decimal=" + decimal);
                                            if (raman <= 10000 && max_data != 0 && decimal < 99 && power_num == 10) {
//                                            if (raman <= 14500 && max_data != 0 && decimal < 99 && power_num == 10) {
                                                jiaozhun_success_times = 0;
//                                            if (raman <= 9500 && max_data != 0 && decimal < 99 && power_num == 10) {
                                                // && medium_num != 10
                                                //Toast.makeText()
                                                // Toast.makeText(view.getContext(), "qiangduxiugai", Toast.LENGTH_SHORT).show();
                                                Log.i("stringdecimal", decimal + "");
                                                int temp = 10;
//                                      ModifyIntensity(temp);
                                                Log.i("string_y/nn", "yes0");

                                                decimal = decimal + 2;
                                                modify_num = modify_num + 2;
                                                Log.i("string_decimal", decimal + "");
                                                string10 = string10.replace(string10, "");
                                                string10 = "$M" + String.valueOf((char) decimal) + "#";
//                                        string10 = string10.replace(string10.charAt(2),(char)decimal);
                                                // string10 = string10.replace(string10, "");
                                                //string10 = start_flag + "H" + ParametersIntensity.bytes2String(ParametersIntensity.hexString2Bytes(Integer.toHexString(decimal))) + end_flag;
                                                Log.i("string_100", string10 + "");
//                                        ModifyIntensity(10);/4.6

//                                        LaserPower laserpower = new LaserPower();
//                                        int return_num =laserpower.LPower(string10,StrIP);

                                                Log.i("string_yy/n", "yes0");
                                                flag_judge = false;
//
                                            }
//                                            else if (raman >= 16000 && decimal > 66 && power_num == 10) {
                                            else if (raman >= 12000 && decimal > 66 && power_num == 10) {
                                                jiaozhun_success_times = 0;
//                                            else if (raman >= 10500 && decimal > 66 && power_num == 10) {
//                                        if (decimal > 66) {
                                                //  Toast.makeText(view.getContext(), "qiangduxiugai", Toast.LENGTH_SHORT).show();
                                                decimal = decimal - 2;
                                                modify_num = modify_num - 2;
                                                Log.i("string_decimal", decimal + "");
                                                if (decimal != 77) {//77 M
                                                    string10 = string10.replace(string10, "");
                                                    string10 = "$M" + String.valueOf((char) decimal) + "#";
//                                            string10 = string10.replace(string10.charAt(2),(char)decimal);
                                                } else {
                                                    string10 = string10.replace(string10, "");
                                                    string10 = "$M" + String.valueOf((char) decimal) + "#";
                                                }
                                                // string10 = string10.replace(string10, "");
                                                // string10 = start_flag + "H" + ParametersIntensity.bytes2String(ParametersIntensity.hexString2Bytes(Integer.toHexString(decimal))) + end_flag;
                                                Log.i("string_101", string10 + "");
//                                        ModifyIntensity(10);//4.6
                                                Log.i("string_yy/n", "yes1");
                                                flag_judge = false;
//                                        }
                                                /*** 还要增加99时候达不到要求提示 校准失败*/
                                            }
//                                            else if ((decimal >= 99 || decimal <= 66) && (raman < 14000 || raman > 16000)) {
                                            else if ((decimal >= 99 || decimal <= 66) && (raman < 10000 || raman > 12000)) {
                                                //校准失败
                                                Log.i("string_error", "fail");
                                                Log.i("string_raman10", raman_10 + "");
                                                Log.i("string_elseif-num", power_num + "");
                                                if (toast_flag % 2 == 0) {
                                                    Message msg = new Message();
                                                    msg.what = 10;
                                                    mhandler.sendMessage(msg);
                                                }
                                                toast_flag++;
                                                jiaozhun_success_times = 0;
                                            }
                                            //raman_10恒等于1，power_num恒等于10，correct_flag在进入判断后才变为false
                                            else if (raman_10 == 1 && power_num == 10 && correct_flag) {
                                                jiaozhun_success_times += 1;
                                                Log.i(TAG, "run: jiaozhun_success_times=" + jiaozhun_success_times);
                                                if (jiaozhun_success_times >= 1) {
                                                    /**raman达到要求，调整剩余档位
                                                     * */
                                                    raman_10 = raman;
                                                    correct_flag = false;
//                                        power_num = 9;
                                                    // 剩余档位均分，最低档不变十进制表示为1
                                                    int tmp = decimal - 1;
                                                    int dec = Math.round(tmp / 9);
                                                    decimal9 = decimal - dec;
                                                    decimal8 = decimal9 - dec;
                                                    decimal7 = decimal8 - dec;
                                                    decimal6 = decimal7 - dec;
                                                    decimal5 = decimal6 - dec;
                                                    decimal4 = decimal5 - dec;
                                                    decimal3 = decimal4 - dec;
//                                        if (decimal3 == 19) {
//                                            decimal3 = 18;
//                                        }
//                                        if (decimal3 == 17) {
//                                            decimal3 = 16;
//                                        }
                                                    decimal2 = decimal3 - dec;
//                                        if (decimal2 == 19) {
//                                            decimal2 = 18;
//                                        }
//                                        if (decimal2 == 17) {
//                                            decimal2 = 16;
//                                        }
                                                    string9 = string9.replace(string9, "");
                                                    string9 = "$M" + String.valueOf((char) decimal9) + "#";
//                                        string9 = string9.replace(string9.charAt(1),'H');
//                                        string9 = string9.replace(string9.charAt(2),(char)decimal9);
                                                    Log.i("string_9", string9 + "");
                                                    string8 = string8.replace(string8, "");
                                                    string8 = "$M" + String.valueOf((char) decimal8) + "#";
                                                    Log.i("string_8", string8 + "");
                                                    string7 = string7.replace(string7, "");
                                                    string7 = "$M" + String.valueOf((char) decimal7) + "#";
                                                    Log.i("string_7", string7 + "");
                                                    string6 = string6.replace(string6, "");
                                                    string6 = "$M" + String.valueOf((char) decimal6) + "#";
                                                    Log.i("string_6", string6 + "");
//                                        string5 = string5.replace(string5.charAt(1),'H');
//                                        string5 = string5.replace(string5.charAt(2),(char)decimal5);
                                                    string5 = string5.replace(string5, "");
                                                    string5 = "$M" + String.valueOf((char) decimal5) + "#";
                                                    Log.i("string_5", string5 + "");
                                                    string4 = string4.replace(string4, "");
                                                    //string4 = start_flag + "H" + ParametersIntensity.bytes2String(ParametersIntensity.hexString2Bytes(Integer.toHexString(decimal4))) + end_flag;
                                                    string4 = "$M" + String.valueOf((char) decimal4) + "#";
//                                        string4 = string4.replace(string4.charAt(1),'H');
//                                        string4 = string4.replace(string4.charAt(2),(char)decimal4);
                                                    Log.i("string_4", string4 + "");
                                                    string3 = string3.replace(string3, "");
                                                    string3 = "$M" + String.valueOf((char) decimal3) + "#";
                                                    Log.i("string_3", string3 + "");
                                                    string2 = string2.replace(string2, "");
                                                    string2 = "$M" + String.valueOf((char) decimal2) + "#";
                                                    Log.i("string_2", string2 + "");
//                                        string1 = string1.replace(string1, "");
//                                        string1 = start_flag + "H" + ParametersIntensity.bytes2String(ParametersIntensity.hexString2Bytes(Integer.toHexString(01))) + end_flag;
                                                    Log.i("string_1", string1 + "");

                                                    //更改数据库内档位值
                                                    ContactinfoDao contactinfoDao = new ContactinfoDao(view.getContext());
                                                    contactinfoDao.Uppower(10, string10);
                                                    contactinfoDao.Uppower(9, string9);
                                                    contactinfoDao.Uppower(8, string8);
                                                    contactinfoDao.Uppower(7, string7);
                                                    contactinfoDao.Uppower(6, string6);
                                                    contactinfoDao.Uppower(5, string5);
                                                    contactinfoDao.Uppower(4, string4);
                                                    contactinfoDao.Uppower(3, string3);
                                                    contactinfoDao.Uppower(2, string2);
//                                        contactinfoDao.Uppower(1,string1);

                                                    Message msg = new Message();
                                                    msg.what = 11;
                                                    mhandler.sendMessage(msg);
                                                    Log.i("string_raman10", raman_10 + "");
                                                    Log.i("string_elseif-num", power_num + "");
                                                }
                                            }
                                    /*
                                    //9 第一次判断raman没有改变还是强度为10时
                                    if(power_num==9 && raman>0.94*raman_10 &&decimal9>30){

                                        Log.i("string_decimal9",decimal9+"");
                                        decimal9 = decimal9 -2;
                                        string9 = string9.replace(string9.charAt(2),(char)decimal9);
                                        //string9 = string9.replace(string9, "");
                                        //string9 = start_flag + "M" + ParametersIntensity.bytes2String(ParametersIntensity.hexString2Bytes(Integer.toHexString(decimal9))) + end_flag;
                                        Log.i("string_decimal9",decimal9+"");
                                    } else if (power_num==9 && raman<0.86*raman_10 &&decimal9<99) {
                                        decimal9 = decimal9 +2;
                                        string9 = string9.replace(string9.charAt(2),(char)decimal9);
                                       // string9 = string9.replace(string9, "");
                                       // string9 = start_flag + "M" + ParametersIntensity.bytes2String(ParametersIntensity.hexString2Bytes(Integer.toHexString(decimal9))) + end_flag;
                                        Log.i("string_decimal9",decimal9+"");
                                    } else if (power_num==9) {
                                        power_num = 8;
                                        Log.i("string_elseif-num",power_num+"");
                                    }
                                    //8
                                    if(power_num==8 && raman>0.84*raman_10 &&decimal8>30){

                                        Log.i("string_decimal8",decimal8+"");
                                        decimal8 = decimal8 -2;
                                        string8 = string8.replace(string8, "");
                                        string8 = start_flag + "M" + ParametersIntensity.bytes2String(ParametersIntensity.hexString2Bytes(Integer.toHexString(decimal8))) + end_flag;
                                        Log.i("string_decimal8",decimal8+"");
                                    } else if (power_num==8 && raman<0.76*raman_10 &&decimal8<99) {
                                        decimal8 = decimal8 +2;
                                        string8 = string8.replace(string8, "");
                                        string8 = start_flag + "M" + ParametersIntensity.bytes2String(ParametersIntensity.hexString2Bytes(Integer.toHexString(decimal8))) + end_flag;
                                        Log.i("string_decimal8",decimal8+"");
                                    }else if (power_num==8) {
                                        power_num = 7;
                                        Log.i("string_elseif-num",power_num+"");
                                    }
                                    //7
                                    if(power_num==7 && raman>0.74*raman_10 &&decimal7>30){

                                        Log.i("string_decimal7",decimal7+"");
                                        decimal7 = decimal7 -2;
                                        string7 = string7.replace(string7, "");
                                        string7 = start_flag + "M" + ParametersIntensity.bytes2String(ParametersIntensity.hexString2Bytes(Integer.toHexString(decimal7))) + end_flag;
                                        Log.i("string_decimal7",decimal7+"");
                                    } else if (power_num==7 && raman<0.66*raman_10 &&decimal7<99) {
                                        decimal7 = decimal7 +2;
                                        string7 = string7.replace(string7, "");
                                        string7 = start_flag + "M" + ParametersIntensity.bytes2String(ParametersIntensity.hexString2Bytes(Integer.toHexString(decimal7))) + end_flag;
                                        Log.i("string_decimal7",decimal7+"");
                                    }else if (power_num==7) {
                                        power_num = 6;
                                        Log.i("string_elseif-num",power_num+"");
                                    }
                                    //6
                                    if(power_num==6 && raman>0.64*raman_10 &&decimal6>30){

                                        Log.i("string_decimal6",decimal6+"");
                                        decimal6 = decimal6 -2;
                                        string6 = string6.replace(string6, "");
                                        string6 = start_flag + "M" + ParametersIntensity.bytes2String(ParametersIntensity.hexString2Bytes(Integer.toHexString(decimal6))) + end_flag;
                                        Log.i("string_decimal6",decimal6+"");
                                    } else if (power_num==6 && raman<0.56*raman_10 &&decimal6<99) {
                                        decimal6 = decimal6 +2;
                                        string6 = string6.replace(string6, "");
                                        string6 = start_flag + "M" + ParametersIntensity.bytes2String(ParametersIntensity.hexString2Bytes(Integer.toHexString(decimal6))) + end_flag;
                                        Log.i("string_decimal6",decimal6+"");
                                    }else if (power_num==6) {
                                        power_num = 5;
                                        Log.i("string_elseif-num",power_num+"");
                                    }
                                    //5
                                    if(power_num==5 && raman>0.54*raman_10 &&decimal5>30){

                                        Log.i("string_decimal5",decimal5+"");
                                        decimal5 = decimal5 -2;
                                        string5 = string5.replace(string5, "");
                                        string5 = start_flag + "M" + ParametersIntensity.bytes2String(ParametersIntensity.hexString2Bytes(Integer.toHexString(decimal5))) + end_flag;
                                        Log.i("string_decimal5",decimal5+"");
                                    } else if (power_num==5 && raman<0.46*raman_10 &&decimal5<99) {
                                        decimal5 = decimal5 +2;
                                        string5 = string5.replace(string5, "");
                                        string5 = start_flag + "M" + ParametersIntensity.bytes2String(ParametersIntensity.hexString2Bytes(Integer.toHexString(decimal5))) + end_flag;
                                        Log.i("string_decimal5",decimal5+"");
                                    }else if (power_num==5) {
                                        power_num = 4;
                                        Log.i("string_elseif-num",power_num+"");
                                    }
                                    //4
                                    if(power_num==4 && raman>0.44*raman_10 &&decimal4>30){

                                        Log.i("string_decimal4",decimal4+"");
                                        decimal4 = decimal4 -2;
                                        string4 = string4.replace(string4, "");
                                        string4 = start_flag + "M" + ParametersIntensity.bytes2String(ParametersIntensity.hexString2Bytes(Integer.toHexString(decimal4))) + end_flag;
                                        Log.i("string_decimal4",decimal4+"");
                                    } else if (power_num==4 && raman<0.36*raman_10 &&decimal4<99) {
                                        decimal4 = decimal4 +2;
                                        string4 = string4.replace(string4, "");
                                        string4 = start_flag + "M" + ParametersIntensity.bytes2String(ParametersIntensity.hexString2Bytes(Integer.toHexString(decimal4))) + end_flag;
                                        Log.i("string_decimal4",decimal4+"");
                                    }else if (power_num==4) {
                                        power_num = 3;
                                        Log.i("string_elseif-num",power_num+"");
                                    }
                                    //3
                                    if(power_num==3 && raman>0.34*raman_10 &&decimal3>30){

                                        Log.i("string_decimal3",decimal3+"");
                                        decimal3 = decimal3 -2;
                                        string3 = string3.replace(string3, "");
                                        string3 = start_flag + "M" + ParametersIntensity.bytes2String(ParametersIntensity.hexString2Bytes(Integer.toHexString(decimal3))) + end_flag;
                                        Log.i("string_decimal3",decimal3+"");
                                    } else if (power_num==3 && raman<0.26*raman_10 &&decimal3<99) {
                                        decimal3 = decimal3 +2;
                                        string3 = string3.replace(string3, "");
                                        string3 = start_flag + "M" + ParametersIntensity.bytes2String(ParametersIntensity.hexString2Bytes(Integer.toHexString(decimal3))) + end_flag;
                                        Log.i("string_decimal3",decimal3+"");
                                    }else if (power_num==3) {
                                        power_num = 2;
                                        Log.i("string_elseif-num",power_num+"");
                                    }
                                    */

                                            flag_judge = false;
                                        }

                                        time++;
                                        Log.i("num_timetest10/1/2", time + "");
                                        if (time == 2) {
                                            time = 0;
                                        }
                                        Log.i("num_timetest20/1/2", time + "");
                                        long endTime1 = System.currentTimeMillis(); //结束时间
                                        long runTime1 = endTime1 - startTime1;
                                        Log.i("test", String.format("方法使用时间 %d ms", runTime1));
                                        long endTime = System.currentTimeMillis(); //结束时间
                                        long runTime = endTime - startTime;
                                        Log.i("test", String.format("总时间 %d ms", runTime));

                                        if (runTime < 2000) {
                                            sleep(2000 - runTime);
                                        }
                                        if (flage) {
                                            flage = false;
                                        } else {
                                            flage = true;
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                        sthread.start();
                    } else {
                /*    try {
                    ContactinfoDao contactinfoDao = new ContactinfoDao(view.getContext());
                    contactinfoDao.Uppower(10,string10);
                    if(modify_num !=0) {
                        int num9 = 77 + modify_num;
                        string9 = string9.replace(string9, "");
                        string9 = start_flag + "M" + ParametersIntensity.bytes2String(ParametersIntensity.hexString2Bytes(Integer.toHexString(num9))) + end_flag;
                        contactinfoDao.Uppower(9,string9);
                        int num8 = 74 + modify_num;
                        string8 = string8.replace(string8, "");
                        string8 = start_flag + "M" + ParametersIntensity.bytes2String(ParametersIntensity.hexString2Bytes(Integer.toHexString(num8))) + end_flag;
                        contactinfoDao.Uppower(8,string8);
                        int num7 = 71 + modify_num;
                        string7 = string7.replace(string7, "");
                        string7 = start_flag + "M" + ParametersIntensity.bytes2String(ParametersIntensity.hexString2Bytes(Integer.toHexString(num7))) + end_flag;
                        contactinfoDao.Uppower(7,string7);
                        int num6 = 66 + modify_num;
                        string6 = string6.replace(string6, "");
                        string6 = start_flag + "M" + ParametersIntensity.bytes2String(ParametersIntensity.hexString2Bytes(Integer.toHexString(num6))) + end_flag;
                        contactinfoDao.Uppower(6,string6);
                        int num5 = 60 + modify_num;
                        string5 = string5.replace(string5, "");
                        string5 = start_flag + "M" + ParametersIntensity.bytes2String(ParametersIntensity.hexString2Bytes(Integer.toHexString(num5))) + end_flag;
                        contactinfoDao.Uppower(5,string5);
                        int num4 = 54 + modify_num;
                        string4 = string4.replace(string4, "");
                        string4 = start_flag + "M" + ParametersIntensity.bytes2String(ParametersIntensity.hexString2Bytes(Integer.toHexString(num4))) + end_flag;
                        contactinfoDao.Uppower(4,string4);
                        int num3 = 45 + modify_num;
                        string3 = string3.replace(string3, "");
                        string3 = start_flag + "M" + ParametersIntensity.bytes2String(ParametersIntensity.hexString2Bytes(Integer.toHexString(num3))) + end_flag;
                        contactinfoDao.Uppower(3,string3);
                        int num2 = 32 + modify_num;
                        string2 = string2.replace(string2, "");
                        string2 = start_flag + "M" + ParametersIntensity.bytes2String(ParametersIntensity.hexString2Bytes(Integer.toHexString(num2))) + end_flag;
                        contactinfoDao.Uppower(2, string2);
                    }

                } catch (Exception e) {
                        e.printStackTrace();
                    }    */
                        btn_correct_power.setTextColor(Color.WHITE);
                        show_switch.setEnabled(true);
                        btn_auto.setEnabled(true);
                        if (btn_pause.isChecked()) {
                            btn_pause.setChecked(false);
                        }
                        btn_pause.setEnabled(false);
                        flage1 = false;
                        try {
                            sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //imageView.setImageDrawable(null);
//                        ClearDataSeries();
                    }
                }
            });

//        handler = new Handler(){
//            @Override
//            public void handleMessage(Message msg) {
//                switch (msg.what){
////                    case 5:
////                        Toast.makeText(getApplicationContext(),MODE+DefinedStr,Toast.LENGTH_LONG).show();
////                        break;
////                    case 4:
////                        Toast.makeText(getApplicationContext(),LaserMode+DefinedStr,Toast.LENGTH_LONG).show();
////                        break;
//                    case 3:
//                        btn_laser.setText("激光:高");
//                        LasPower = 3;
//                        break;
//                    case 2:
//                        btn_laser.setText("激光:中");
//                        LasPower = 2;
//                        break;
//                    case 1:
//                        btn_laser.setText("激光:低");
//                        LasPower = 1;
//                        break;
//                }
//            }
//        };

        }
        return view;
    }

    /**
     * 获取像素值
     * yy0[] 五帧图像平均
     */
    public void getPixels(String bmurl) throws Exception {
        double[][] pixel1 = new double[5][1280 * 720];//灰度 getpixel
        double[][] pixels1 = new double[5][1280 * 720];//列之和
//        try {
//            pixel1=
//            pixels1 =
//        } catch (Exception e) {
////            Toast.makeText(MainShowData.this,"数据异常，请重试",Toast.LENGTH_LONG).show();
//            e.printStackTrace();
//            return;
//        }
//        if(pixel1==null){
//            pixel1 = new double[5][1280 * 720];
//        }
//        if(pixels1==null){
//            pixels1 = new double[5][1280 * 720];
//        }
        URL url = new URL(bmurl);
        // btn_image.setImageURI(url);
        for (k = 0; k < 5; k++) {  // k 五帧图像
            Log.i("timetime", k + "");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream is = conn.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(is);         //解码获取图片
            maxpixel_num = 0;
//            btn_image.setImageBitmap(bitmap);
            mBitmapWidth = bitmap.getWidth();
            mBitmapHeight = bitmap.getHeight();
            int[] pixel = new int[mBitmapWidth * mBitmapHeight];
            yy0 = new int[mBitmapWidth];
//            yy = new int[mBitmapWidth];
            yy2 = new int[mBitmapWidth];

            bitmap.getPixels(pixel, 0, mBitmapWidth, 0, 0, mBitmapWidth, mBitmapHeight);    // 一行行读取像素值
            //取出灰度值放入数组pixel1中
            for (int i = 0; i < mBitmapHeight; i++) {
                for (int j = 0; j < mBitmapWidth; j++) {
                    int grey = pixel[i * mBitmapWidth + j];
                    int R = ((grey & 0xFF0000) >> 16);
                    int G = ((grey & 0xFF00) >> 8);
                    int B = (grey & 0xFF);

                    double grey1 = (R * 38 + G * 75 + B * 15) >> 7;

                    pixel1[k][i * mBitmapWidth + j] = grey1;
                }
            }
            // 每列像素值叠加放入pixels1数组中
            for (int a = 0; a < mBitmapWidth; a++) {
                for (int b = 0; b < mBitmapHeight; b++) {
                    pixels1[k][a] = pixels1[k][a] + pixel1[k][b * mBitmapWidth + a];        //列像素之和
                }
            }
            // 饱和像素255 ，maxpixel_num饱和像素点个数
            for (int n = 0; n < mBitmapWidth * mBitmapHeight; n++) {         //szhy
                if (pixel1[k][n] == 255) {
                    maxpixel_num++;
                }
            }
            maxpixel_num_array[k] = maxpixel_num;
            Log.i("maxnum", maxpixel_num + "");

//            maxpixel_num=0;

        }
        for (int b = 0; b < mBitmapWidth; b++) {    //5帧图像平均
            yy0[b] = (int) ((pixels1[0][b] + pixels1[1][b] + pixels1[2][b] + pixels1[3][b] + pixels1[4][b]) / 5);
        }
        //五帧图像饱和像素点平均值
        maxpixel_num_avg = (maxpixel_num_array[0] + maxpixel_num_array[1] + maxpixel_num_array[2] + maxpixel_num_array[3] + maxpixel_num_array[4]) / 5;
        Log.i("maxnum_avg", maxpixel_num_avg + "");
//        maxpixel_num = (maxpixel[0]+maxpixel[1]+maxpixel[2]+maxpixel[3]+maxpixel[4])/5;
//        int[] gp = new int[1280];
//        for (int a=0;a<mBitmapWidth;a++){
//            if (a==0){
//                gp[0] = yy0[0];
//            }else if (a==1279){
//                gp[1279] = yy0[1279];
//            }else {
//                gp[a] = (yy0[a - 1] + yy0[a] + yy0[a + 1]) / 3;
//                gp[1279] = yy0[1279];
//            }
//        }
//        yy0 = gp.clone();
        Log.i("timetest", time + "");
        Log.i("maxtest0/1", String.format("time %d", time));
        // 激光亮灭各一组数据，再相减
        if (time == 0) {
            yy1 = yy0.clone();
            maxpixel1 = maxpixel_num_avg;
        } else if (time == 1) {
            maxpixel2 = maxpixel_num_avg;
            yy2 = yy0.clone();
            //强度自动调节判断
            maxpixel_result = Math.abs(maxpixel2 - maxpixel1);
            for (int b = 0; b < mBitmapWidth; b++) {
                yy_temp[b] = yy1[b] - yy2[b];
            }
            int max_temp = -1000000, min_temp = 0;
            int max_point = 0, min_point = 0;
            for (int b = 1; b < mBitmapWidth; b++) {
                if (yy_temp[b] < min_temp) {
                    min_temp = yy_temp[b];
                    min_point = b;
                }
                if (yy_temp[b] > max_temp) {
                    max_temp = yy_temp[b];
                    max_point = b;
                }
            }

//            if(Math.abs(min_temp) > Math.abs(max_temp)){
//                for (int b = 0; b < mBitmapWidth; b++) {
//                    yy[b] = yy_temp[b] - Math.abs(max_temp);
//                }
//                for (int b = 0; b < mBitmapWidth; b++) {
//                    yy[b] =  Math.abs(yy_temp[b]);
//                }
//            }
//            if(min_temp < 0 && max_temp > 0) {
//                for (int b = 0; b < mBitmapWidth; b++) {
//                    yy[b] = yy_temp[b] + Math.abs(max_temp);
//                }
//            }
            /**
             * 几种亮灭相减的可能结果
             */
            if (min_temp < 0 && max_temp < 0) {
                for (int b = 0; b < mBitmapWidth; b++) {
                    yy[b] = Math.abs(yy_temp[b]) - Math.abs(max_temp);
                }
            }
            if (min_temp > 0 && max_temp > 0) {
                for (int b = 0; b < mBitmapWidth; b++) {
                    yy[b] = yy_temp[b] - min_temp;
                }
            }
            if (min_temp < 0 && max_temp > 0 && Math.abs(min_temp) > Math.abs(max_temp)) {
                for (int b = 0; b < mBitmapWidth; b++) {
                    yy[b] = Math.abs(yy_temp[b] - max_temp);
                }
            }
            if (min_temp < 0 && max_temp > 0 && Math.abs(min_temp) < Math.abs(max_temp)) {
                for (int b = 0; b < mBitmapWidth; b++) {
                    yy[b] = yy_temp[b] + Math.abs(min_temp);
                }
            }
            // TODO szhy  移动平均滤波进行平滑处理 窗口取值windows根据需要调整
            int summ = 0;
            int window = 10;
            for (int i = 0; i < mBitmapWidth - window; i++) {
                for (int j = 0; j < window; j++) {
                    summ += yy[i + j];
                }
                yy[i] = summ / window;
                summ = 0;
            }
            for (int j = 0; j < window; j++) {
                summ += yy[mBitmapWidth - window + 1];
            }
            int avg = summ / window;
            for (int i = mBitmapWidth - window; i < mBitmapWidth; i++) {
                yy[i] = avg;
            }

            //int maxdata = 0;  //11.04 最大值点判断
            //int maxdata = 0;  //11.04 最小值点判断

            for (int i = 0; i < 1280; i++) {
                if (max_data < yy[i]) {
                    max_data = yy[i];   //校准调节判断
                }

            }

            Log.i("getData", "getPixels: minData=" + min_data + ";maxData=" + max_data);

            int tmp_ramam = 0;

            for (int i = 200; i < 400; i++) {     //水拉曼峰 3.28
                if (yy[i] > tmp_ramam) {
                    tmp_ramam = yy[i];
                    leftEdge = i - 200;
                    rightEdge = i + 800;
                }
            }
            min_data = 10000000;
            for (int i = leftEdge; i < rightEdge; i++) {
                if (min_data > yy[i]) {
                    min_data = yy[i];   //校准调节判断
                }
            }
            raman = tmp_ramam;
            Log.i(TAG, "getPixels: " + "raman=" + raman);
//            Log.i("raman", "raman="+raman);

            peak1 = peak2 = peak3 = peak4 = 1;
//
//            for(int i=200;i<300;i++){
//                if(yy[i]>peak1) peak1 = yy[i];
//            }
////            text_peak1.setText(Integer.toString(peak1));
//            double cor1 = peak1*0.7/max_data;
//            Message msg_1 = new Message();
//            Bundle bundle1 = new Bundle();
//            bundle1.putDouble("cor1",cor1);
//            bundle1.putInt("peak1",peak1);
//              Log.i("peak1",peak1+"");
//            msg_1.setData(bundle1);
//            msg_1.what = 3;
//            mhandler.sendMessage(msg_1);
//
//            for(int i=450;i<550;i++){
//                if(yy[i]>peak2) peak2 = yy[i];
//            }
////            text_peak2.setText(Integer.toString(peak2));
//            double cor2 = peak2*0.7/max_data;
//            Message msg_2 = new Message();
//            Bundle bundle2 = new Bundle();
//            bundle2.putDouble("cor2",cor2);
//            bundle2.putInt("peak2",peak2);
//            Log.i("peak2",peak2+"");
//            msg_2.setData(bundle2);
//            msg_2.what = 4;
//            mhandler.sendMessage(msg_2);
//
//            for(int i=850;i<950;i++){
//                if(yy[i]>peak3) peak3 = yy[i];
//            }
////            text_peak3.setText(Integer.toString(peak3));
//            double cor3 = peak3*0.7/max_data;
//            Message msg_3 = new Message();
//            Bundle bundle3 = new Bundle();
//            bundle3.putDouble("cor3",cor3);
//            bundle3.putInt("peak3",peak3);
//            Log.i("peak3",peak3+"");
//            msg_3.setData(bundle3);
//            msg_3.what = 5;
//            mhandler.sendMessage(msg_3);
//            int tmp_p4 ;
//            for(int i=900;i<990;i++){
//                tmp_p4=0;
//                for(int j=1;j<=6;j++){
//                    if(yy[i]>yy[i+j]&&yy[i]>yy[i-j]) tmp_p4++;
//                }
//                if(yy[i]>peak4&&yy[i]!=peak3&&tmp_p4==6)
//                    peak4 = yy[i];
//            }
//            if(peak4!=1){
//           Message msg_4 = new Message();
//            Bundle bundle4 = new Bundle();
//           bundle4.putInt("peak4",peak4);
//            Log.i("peak4",peak4+"");
//           msg_4.setData(bundle4);
//           msg_4.what = 6;
//           mhandler.sendMessage(msg_4);}
//            text_peak4.setText(peak4);}
            SetDataSeries();
        }

        k = 0;

    }

//    public void onPopupButtonClick(View view){
//        popupMenu = new PopupMenu(view.getContext(),view);//popupmenu 弹出式下拉菜单
//        popupMenu.getMenuInflater().inflate(R.menu.popup_laser,popupMenu.getMenu());
//        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            int pointer;
//            @Override
//            public boolean onMenuItemClick(MenuItem menuItem) {
//                switch (menuItem.getItemId()){
//                    case R.id.high:
//                        Log.i("test", String.format("开关高"));
//                        pointer = 3;
//                        break;
//                    case R.id.medium:
//                        Log.i("test", String.format("开关中"));
//                        pointer = 2;
//                        break;
//                    case R.id.low:
//                        Log.i("test", String.format("开关低"));
//                        pointer = 1;
//                        break;
//                    default:
//                        break;
//                }
//                // 向光谱仪发送更改强度
//                ModifyIntensity(pointer);
////                mhandler.obtainMessage(pointer).sendToTarget();
//                return true;
//            }
//
//        });
//
//        popupMenu.show();
//
//    }

    /***btn_sava 保存弹出对话框输入名称保存***/
    public void EditInput(final View view) {
        final EditText editText = new EditText(view.getContext());
        new AlertDialog.Builder(view.getContext()).setTitle("请输入：")
                .setIcon(R.drawable.ic_input)
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    String NameStr;

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NameStr = editText.getText().toString();
                        ContactinfoDao contactinfoDao = new ContactinfoDao(view.getContext());      //数据库增
                        boolean nameFlag = contactinfoDao.alertName(NameStr);                       //名字是否重复
                        if (!nameFlag) {
                            long row = contactinfoDao.addData(NameStr, medium_num, yy, leftEdge, rightEdge, min_data);       //laspower
                            if (row == -1) {
                                Toast.makeText(view.getContext(), "添加失败", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(view.getContext(), "成功添加在" + row + "行", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(view.getContext(), "名称重复，请重新输入！", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("取消", null).show();
    }

    public static ArrayList<String> getContectedIP() {
        ArrayList<String> connectedIP = new ArrayList<String>();
        try {
            //Android10不可用
            BufferedReader br = new BufferedReader(new FileReader(
                    "/proc/net/arp"));
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4) {
                    String ip = splitted[0];
                    connectedIP.add(ip);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.w("wifilog", connectedIP + "");
        return connectedIP;
    }

    /**
     * 判断IP
     ***/
    /**
     * 这里仅仅判断了是否是正常的局域网ip
     */
    private boolean getIP(String ip) {
        boolean flag;
        if (ip.substring(0, 7)
                .equals("192.168")) {
            flag = true;
        } else {
            flag = false;
        }
        return flag;
    }

    private String intTOIP(int i) {
        String openwrtIP;
        openwrtIP = (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                (i >> 24 & 0xFF);
        Log.i("test", String.format(openwrtIP));
        return openwrtIP;
    }

    /*** 初始强度为第5档
     * 根据返回值信息发送强度指令***/
    class powerthread extends Thread {
        /**
         * 这个指令强度是5，见{@link MydbHeper#onCreate}数据库创建过程
         */
        String power = "$M<#";  //初始化时发送的指令

        @Override
        public void run() {
            try {
                sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            ModifyIntensity(3); //闪退，socket连接失败
            //从数据库中获取激光强度指令 2020.1.8
            ContactinfoDao contactinfoDao = new ContactinfoDao(graphView.getContext());
            contactinfoDao.Uppower(1, string1);
//            string1 = contactinfoDao.getinstru(1);
            Log.i("database-str1", string1 + "");
            string2 = contactinfoDao.getinstru(2);
            Log.i("database-str2", string2 + "");
            decimal2 = Integer.valueOf(string2.charAt(2));
            string3 = contactinfoDao.getinstru(3);
            Log.i("database-str3", string3 + "");
            decimal3 = Integer.valueOf(string3.charAt(2));
            string4 = contactinfoDao.getinstru(4);
            Log.i("database-str4", string4 + "");
            decimal4 = Integer.valueOf(string4.charAt(2));
            string5 = contactinfoDao.getinstru(5);
            Log.i("database-str5", string5 + "");
            decimal5 = Integer.valueOf(string5.charAt(2));
            string6 = contactinfoDao.getinstru(6);
            Log.i("database-str6", string6 + "");
            decimal6 = Integer.valueOf(string6.charAt(2));
            string7 = contactinfoDao.getinstru(7);
            Log.i("database-str7", string7 + "");
            decimal7 = Integer.valueOf(string7.charAt(2));
            string8 = contactinfoDao.getinstru(8);
            Log.i("database-str8", string8 + "");
            decimal8 = Integer.valueOf(string8.charAt(2));
            string9 = contactinfoDao.getinstru(9);
            Log.i("database-str9", string9 + "");
            decimal9 = Integer.valueOf(string9.charAt(2));
            string10 = contactinfoDao.getinstru(10);
            Log.i("database-str10", string10 + "");
//         LaserPower laserPower = new LaserPower();
//                        laserPower.LPower("$Hc#",StrIP);//高档最高，中档可调为最高
//                        try {
//                            sleep(10);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }//加上初始变慢了

            LaserPower laserPower1 = new LaserPower();
            int ACCEPT = laserPower1.LPower(string5, StrIP); //10.24gai power string10 11.19 初始5

            // accept是光谱仪给出的新 (强度?挡位?) 返回值
            Message message = Message.obtain();
//            Message message1 = Message.obtain(message);
            message.what = ACCEPT;
            handler.sendMessage(message);


//            maxpixel_num=0
//            message1.what= ACCEPT;

//            shandler.sendMessage(message1);
//            Looper.prepare();               //启动looper
//            mhandler = new  Handler(){
//                @Override
//                public void handleMessage(Message msg) {
//                    switch (msg.what){
//                        case MSG_LOW:
//                            power = "$RL#";
//                            break;
//                        case MSG_MEDIUM:
//                            power = "$RM#";
//                            break;
//                        case MSG_HIGH:
//                            power = "$RH#";
//                            break;
////                        case ADJUST:
////                            power = BytesToString;
////                            break;
//                    }
//                    LaserPower laserPower = new LaserPower();
//                    int ACCEPT = laserPower.LPower(power,StrIP);
//                    Message message = Message.obtain();
//                    message.what = ACCEPT;
//                    handler.sendMessage(message);
////                    shandler.sendMessage(message);
//                }
//
//            };
//            Looper.loop();                  //Looper开始工作，从消息队列里取消息，处理消息
        }
    }

    private void DisplayResult(Context context, String message, String title) {

        AlertDialog alertDialog = new AlertDialog.Builder(context)
//                .setTitle("比对结果")
                .setTitle(title)
                .setMessage(message)
                .setIcon(R.drawable.ic_contrast_red)
                .create();
        alertDialog.show();
    }

    private XYMultipleSeriesRenderer InitRenderer() {
        renderer = new XYMultipleSeriesRenderer();
        renderer.setAntialiasing(false);//true:消除锯齿；false:不消除锯齿
//        renderer.setChartTitle("光谱采集");
        renderer.setChartTitle("Spectral Curve");
        renderer.setChartTitleTextSize(40);//设置图表标题的字体大小(图的最上面文字)
        renderer.setMargins(new int[]{50, 130, 50, 0});//设置外边距，顺序为：上左下右
        //坐标轴设置
        renderer.setShowLabels(true);
//        renderer.setXTitle("像素点");
        renderer.setXTitle("CurveLength");
        renderer.setYTitle("Intensity");
//        renderer.setYTitle("相对强度");
        renderer.setAxisTitleTextSize(40);//设置坐标轴标题字体的大小
        renderer.setAxesColor(Color.BLACK);     /***/
        renderer.setXAxisMin(0);
        renderer.setXAxisMax(1010);
        renderer.setYAxisMin(0);
        renderer.setYAxisMax((150000 / y_scale));
//        renderer.setYAxisMax(150);
        renderer.setLabelsTextSize(30);//设置坐标字号
        renderer.setXLabelsColor(Color.BLACK);  /*****/  // 设置X轴标签的显示颜色
        renderer.setYLabelsColor(0, Color.BLACK);    /***/
        renderer.setXLabelsAlign(Paint.Align.CENTER);// 设置X轴在标签哪边对齐方式
        renderer.setYLabelsAlign(Paint.Align.RIGHT);//设置Y轴在标签哪边对齐方式
//        renderer.setYLabelsAlign(Paint.Align.LEFT);
        renderer.setXLabels(30);
        renderer.setYLabels(30);
        //设置颜色
        renderer.setApplyBackgroundColor(true); //内部底色
        renderer.setBackgroundColor(0xffffffff);    /****/          // 内部底色
        renderer.setLabelsColor(Color.BLACK);   /***///设置标签颜色
//        renderer.setMarginsColor(0x000000); /***0xFFfffaf0*/    // 外部颜色，X轴坐标
        renderer.setMarginsColor(0xffffffff); /***0xFFfffaf0*/    // 外部颜色，X轴坐标
        //缩放设置
        renderer.setZoomButtonsVisible(false);//设置缩放按钮是否可见
        renderer.setZoomEnabled(false, false); //图表是否可以缩放设置

        /***
         * 设置网格
         */
        renderer.setShowGrid(true);
        renderer.setGridColor(0x00ff00ff);

        //legend(最下面的文字说明)设置
        renderer.isShowLegend();
        renderer.setShowLegend(true);
        renderer.setFitLegend(true);//是否适应于屏幕
        renderer.setLegendHeight(20);//设置说明的高度
        renderer.setLegendTextSize(16);

        renderer.setPointSize(5f);
        renderer.setShowGrid(false);
        renderer.setClickEnabled(false); //是否可以点击
        renderer.setPanEnabled(false);

        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setColor(Color.RED); /***black*/
        r.setPointStyle(PointStyle.POINT); // 点的形状
        r.setFillPoints(true); // 实心点
        r.setDisplayChartValues(false);// 设置显示数值
        r.setChartValuesTextSize(25);
        r.setLineWidth(3);
        /**
         * ：XYSeriesRenderer个数必须和series个数相同
         */
        XYSeriesRenderer r2 = new XYSeriesRenderer();
        r2.setColor(Color.BLACK); /***black*/
        r2.setPointStyle(PointStyle.POINT); // 点的形状
        r2.setFillPoints(true); // 实心点
        r2.setDisplayChartValues(false);// 设置显示数值
        r2.setChartValuesTextSize(25);
        r2.setLineWidth(3);

        renderer.addSeriesRenderer(r);
        renderer.addSeriesRenderer(r2); //

        return renderer;
    }

    private XYMultipleSeriesDataset InitDataset() {
        dataset = new XYMultipleSeriesDataset();
//        series = new XYSeries("像素--强度对照图");
        series = new XYSeries("");
        series2 = new XYSeries("");  //
        dataset.addSeries(series);
        dataset.addSeries(series2);   //
        return dataset;
    }

    private void SetDataSeries() {

        dataset.removeSeries(series);
        series.clear();
        dataset.removeSeries(series2);
        series2.clear();


        int maxdata1 = 0;
//        for (int i = 0; i < 1280; i++) {
        for (int i = leftEdge; i < rightEdge; i++) {
            if (maxdata1 < yy[i]) {
                maxdata1 = yy[i];
            }
        }


        max_data = maxdata1;
        max_axis = 10.0 * maxdata1 / 7;
        renderer.setYAxisMax((max_axis / y_scale));

//        for (int k = 0; k < mBitmapWidth; k++) {
        for (int k = leftEdge; k < rightEdge; k++) {
//            series.add(xx[k], yy[k]);
//            series.add(xx[k - leftEdge], yy[k] - min_data);
            series.add(xx[k - leftEdge], (yy[k] - min_data) / y_scale);
        }

        dataset.addSeries(series);
        // dataset.addSeries(series2);
        chart.repaint();

    }

    //绘制两条曲线
    private void SetDataSeries2() {
        dataset.removeSeries(series);
        series.clear();

        dataset.removeSeries(series2);
        series2.clear();

        int maxdata1 = 0;
        for (int i = 0; i < 1280; i++) {
            if (maxdata1 < yy[i]) {
                maxdata1 = yy[i];
            }
        }

        max_data = maxdata1;
        max_axis = 10.0 * maxdata1 / 7;
        renderer.setYAxisMax((max_axis / y_scale));

//        for (int k = 0; k < mBitmapWidth; k++) {
//
//            series.add(xx[k], yy[k]);
//        }
        for (int k = leftEdge; k < rightEdge; k++) {
            series.add(xx[k - leftEdge], (yy[k] - min_data) / y_scale);
        }
        /**
         *
         */
        for (int k = 0; k < mBitmapWidth; k++) {
            series2.add(xx[k], data2[k] / y_scale);// two line
        }

        dataset.addSeries(series);
        dataset.addSeries(series2);
        chart.repaint();

    }

    private void ClearDataSeries() {
        dataset.removeSeries(series);
        series.clear();
        for (int k = 0; k < mBitmapWidth; k++) {
            series.add(0, 0);
        }
        dataset.addSeries(series);
        chart.repaint();

    }

    /****激光强度更改***/
    private void ModifyIntensity(int power_num) {
        String power_instruction;
        if (power_num == 10) {
            power_instruction = string10;
            Log.i("string_mody10", "01");
        } else if (power_num == 9) {
            power_instruction = string9;//"$MM#"
        } else if (power_num == 8) {
            power_instruction = string8;//"$MJ#"
        } else if (power_num == 7) {
            power_instruction = string7;//"$MG#"
        } else if (power_num == 6) {
            power_instruction = string6;//"$MB#"
        } else if (power_num == 5) {
            power_instruction = string5;//"$M<#"
        } else if (power_num == 4) {
            power_instruction = string4;//"$M6#"
        } else if (power_num == 3) {
            power_instruction = string3;//"$M-#"
        } else if (power_num == 2) {
            power_instruction = string2;//"$M #"
        } else if (power_num == 1) {
            power_instruction = string1;//"$L"+"\t"+"#";
        } else {
            power_instruction = "";
            Toast.makeText(getContext(), "强度更改失败", Toast.LENGTH_SHORT).show();
        }

        LaserPower laserpower = new LaserPower();
        int return_num = laserpower.LPower(power_instruction, StrIP);
        Log.i("string_return", return_num + "");
        Message message = Message.obtain();
        message.what = return_num;
        handler.sendMessage(message);  //handler更新UI
        Log.i("string_mody10", "02");
    }

    public static ViewGroup.LayoutParams setViewMargin(View view, int left, int right, int top, int bottom) {
        if (view == null) {
            return null;
        }

        int leftPx = left;
        int rightPx = right;
        int topPx = top;
        int bottomPx = bottom;
        ViewGroup.LayoutParams params = view.getLayoutParams();
        ViewGroup.MarginLayoutParams marginParams = null;
        //获取view的margin设置参数
        if (params instanceof ViewGroup.MarginLayoutParams) {
            marginParams = (ViewGroup.MarginLayoutParams) params;
        } else {
            //不存在时创建一个新的参数
            marginParams = new ViewGroup.MarginLayoutParams(params);
        }
        //设置margin
        marginParams.setMargins(leftPx, topPx, rightPx, bottomPx);
        view.setLayoutParams(marginParams);
        return marginParams;
    }


    //    btn_correct.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//        @Override
//        public void onCheckedChanged(CompoundButton buttonView, boolean Flag) {
//            if(Flag) {
//                btn_correct.setTextColor(Color.BLACK);
//                btn_pause.setEnabled(true);
//                show_switch.setEnabled(false);
//                flage1 =true;
//                time = 0;
//                cthread = new  Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        while (flage1) {
//                            try {
//                                if (!RUN)  {sleep(Long.MAX_VALUE);}
//                                long startTime = System.currentTimeMillis();  //获取系统时间
//                                LaserSwitch laserSwitch = new LaserSwitch();
//                                laserSwitch.laserswitch(flage,StrIP);
//                                sleep(250);
//                                long startTime1 = System.currentTimeMillis();
//                                getPixels(url);
////                                    ModifyIntensity(10);
////                                    int decimal = 79 ; //初始最高档位十进制数值
//
//
//                                Log.i("string0_anxis",max_data+"");
//
//                                boolean flag_judge = true;
//                                if(flag_judge) {
//                                    Log.i("string0",string10+"");
//                                    if (max_data <= 10000 && max_data != 0 && decimal < 99) { // && medium_num != 10
//                                        Log.i("stringdecimal", decimal + "");
////                                        if (decimal < 99) {
//                                        decimal = decimal + 2;
//                                        modify_num = modify_num + 2;
//                                        Log.i("stringdecimal", decimal + "");
//                                        string10 = string10.replace(string10, "");
//                                        string10 = start_flag + "H" + ParametersIntensity.bytes2String(ParametersIntensity.hexString2Bytes(Integer.toHexString(decimal))) + end_flag;
//                                        Log.i("string01", string10 + "");
//                                        ModifyIntensity(10);
//                                        flag_judge = false;
////                                        }
//                                    } else if (max_data >= 12000 && decimal > 66) {
////                                        if (decimal > 66) {
//                                        decimal = decimal - 2;
//                                        modify_num = modify_num - 2;
//                                        Log.i("stringdecimal", decimal + "");
//                                        string10 = string10.replace(string10, "");
//                                        string10 = start_flag + "H" + ParametersIntensity.bytes2String(ParametersIntensity.hexString2Bytes(Integer.toHexString(decimal))) + end_flag;
//                                        Log.i("string00", string10 + "");
//                                        ModifyIntensity(10);
//                                        flag_judge = false;
////                                        }
//                                    }
//                                    flag_judge = false;
//                                }
//
//                                time++;
//                                Log.i("num_timetest10/1/2",time+"");
//                                if (time == 2){
//                                    time = 0;
//                                }
//                                Log.i("num_timetest20/1/2",time+"");
//                                long endTime1 = System.currentTimeMillis(); //结束时间
//                                long runTime1 = endTime1 - startTime1;
//                                Log.i("test", String.format("方法使用时间 %d ms", runTime1));
//                                long endTime = System.currentTimeMillis(); //结束时间
//                                long runTime = endTime - startTime;
//                                Log.i("test", String.format("总时间 %d ms", runTime));
//
//                                if(runTime<2000) {
//                                    sleep(2000 - runTime);
//                                }
//                                if (flage) {
//                                    flage = false;
//                                } else {
//                                    flage = true;
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                });
//                cthread.start();
//            } else {
//                if(modify_num !=0) {
//                    int num9 = 77 + modify_num;
//                    int num8 = 74 + modify_num;
//                    int num7 = 71 + modify_num;
//                    int num6 = 66 + modify_num;
//                    int num5 = 60 + modify_num;
//                    int num4 = 54 + modify_num;
//                    int num3 = 45 + modify_num;
//                    int num2 = 32 + modify_num;
//
//                }
//                btn_correct.setTextColor(Color.WHITE);
//                show_switch.setEnabled(true);
//                if (btn_pause.isChecked()){
//                    btn_pause.setChecked(false);
//                }
//                btn_pause.setEnabled(false);
//                flage1 = false;
//                try {
//                    sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                //imageView.setImageDrawable(null);
//                ClearDataSeries();
//            }
//        }
//    });
    private void checkPermission() {
        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivate, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(mContext, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
            }
            //申请权限
            ActivityCompat.requestPermissions(mActivate, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        } else {
            Toast.makeText(mContext, "授权成功！", Toast.LENGTH_SHORT).show();
        }
    }
}


