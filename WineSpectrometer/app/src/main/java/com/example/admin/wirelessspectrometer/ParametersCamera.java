package com.example.admin.wirelessspectrometer;


import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;


/** 探测器参数获取 更改
 * A simple {@link Fragment} subclass.
 */
public class ParametersCamera extends android.support.v4.app.Fragment {

    String etBrightness,etContrast,etSaturation,etHue,etGain,
            etSharpness,etBacklightCompensation,etExposure,etExposureSwitch;
    EditText Brightness,Contrast,Saturation,Hue,Gain,
            Sharpness,BacklightCompensation,Exposure;
    ToggleButton TBConnect;
    Button Ensure,Default;
    Switch ExposureSwitch;

    public final static int CONN_SUCCESS = 1;
    public final static int CHANGE_SUCCESS = 2;
    public final static int DISCONN_SUCCESS = 3;
    public final static int READ_BUF_SIZE = 1024;

    private int a;

    private static Connection sshConnection = null;

    private View view;

    public ParametersCamera() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_parameters_camera, container, false);
        InitCamera(view);

        Ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etBrightness = Brightness.getText().toString();
                etContrast = Contrast.getText().toString();
                etSaturation = Saturation.getText().toString();
                etHue = Hue.getText().toString();
                etGain = Gain.getText().toString();
                etSharpness = Sharpness.getText().toString();
                etBacklightCompensation = BacklightCompensation.getText().toString();
                etExposure = Exposure.getText().toString();
                if (etExposureSwitch.equals("1")){
                    ExposureSwitch.setChecked(false);
                }else {
                    ExposureSwitch.setChecked(true);
                }
                if (JudgeRanges() == 1) {
                    new SSHCmd2Openwrt().start();
                }
            }
        });

        Default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DefaultInt();
            }
        });

        TBConnect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    TBConnect.setText("连接中...");
                    TBConnect.setTextColor(Color.MAGENTA);
                    new Thread(runnable_connect).start();
                }else {
                    TBConnect.setText("断开连接中...");
                    TBConnect.setTextColor(Color.MAGENTA);
                    new Thread(runnable_disconnect).start();
                }
            }
        });

        ExposureSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Exposure.setEnabled(false);
                    etExposureSwitch = "3";
                }else {
                    Exposure.setEnabled(true);
                    etExposureSwitch = "1";
                }
            }
        });

        return view;
    }

    private final MyHandler mHandler = new MyHandler(this);

    private class MyHandler extends Handler {
        private final WeakReference<ParametersCamera> mActivity;        //弱引用
        MyHandler(ParametersCamera activity) {
            mActivity = new WeakReference<ParametersCamera>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            ParametersCamera activity = mActivity.get();
            if (activity != null) {
                switch (msg.what){
                    case CONN_SUCCESS:
                        TBConnect.setText("已连接");
                        TBConnect.setTextColor(Color.GREEN);
                        Ensure.setEnabled(true);
                        Default.setEnabled(true);
                        Toast.makeText(view.getContext(), "连接成功", Toast.LENGTH_LONG).show();
                        Brightness.setText(etBrightness);
                        Contrast.setText(etContrast);
                        Saturation.setText(etSaturation);
                        Hue.setText(etHue);
                        Gain.setText(etGain);
                        Sharpness.setText(etSharpness);
                        BacklightCompensation.setText(etBacklightCompensation);
                        Exposure.setText(etExposure);
                        if (etExposureSwitch.equals("1")){
                            Exposure.setEnabled(true);
                            ExposureSwitch.setChecked(false);
                        }else {
                            Exposure.setEnabled(false);
                            ExposureSwitch.setChecked(true);
                        }
                        break;
                    case DISCONN_SUCCESS:
                        TBConnect.setText("未连接");
                        TBConnect.setTextColor(Color.RED);
                        Ensure.setEnabled(false);
                        Default.setEnabled(false);
                        Toast.makeText(view.getContext(), "断开连接", Toast.LENGTH_LONG).show();
                        break;
                    case CHANGE_SUCCESS:
                        Toast.makeText(view.getContext(), "修改参数成功", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        TBConnect.setText("连接出错");
                        TBConnect.setTextColor(Color.RED);
                        Ensure.setEnabled(false);
                        Toast.makeText(view.getContext(), "连接出错", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        }
    }

    public void InitCamera(View view){
        TBConnect = view.findViewById(R.id.connect);
        Ensure = view.findViewById(R.id.ensure);
        Default = view.findViewById(R.id.Default);
        ExposureSwitch = view.findViewById(R.id.exposureswitch);

        Brightness = view.findViewById(R.id.brightness);
        Brightness.setText(etBrightness);
        Contrast = view.findViewById(R.id.contrast);
        Contrast.setText(etContrast);
        Saturation = view.findViewById(R.id.saturation);
        Saturation.setText(etSaturation);
        Hue = view.findViewById(R.id.hue);
        Hue.setText(etHue);
        Gain = view.findViewById(R.id.gain);
        Gain.setText(etGain);
        Sharpness = view.findViewById(R.id.sharpness);
        Sharpness.setText(etSharpness);
        BacklightCompensation = view.findViewById(R.id.BacklightCompensation);
        BacklightCompensation.setText(etBacklightCompensation);
        Exposure = view.findViewById(R.id.exposure);
        Exposure.setText(etExposure);
    }

    /******** ssh用户密码验证连接 ********
     ******** 连接成功后查询目前摄像头参数值 ********/
    final Runnable runnable_connect = new Runnable() {
        @Override
        public void run() {
            SSHConnectHost();
            try {
                etBrightness = CutStr(SSHExecuteCmd("Brightness","",2));
//                int len = etBrightness.length();
//                Log.i("test", String.format("etBrightness的字符串长度为 %d",len));
//                Log.i("test", String.format(etBrightness));
                etContrast = CutStr(SSHExecuteCmd("Contrast","",2));
                etSaturation = CutStr(SSHExecuteCmd("Saturation","",2));
                etHue = CutStr(SSHExecuteCmd("Hue","",2));
                etGain = CutStr(SSHExecuteCmd("Gain","",2));
                etSharpness = CutStr(SSHExecuteCmd("Sharpness","",2));
                etBacklightCompensation = CutStr(SSHExecuteCmd("Backlight Compensation","",2));
                etExposure = CutStr(SSHExecuteCmd("Exposure (Absolute)","",2));
                etExposureSwitch = CutStr(SSHExecuteCmd("Exposure, Auto","",2));
            } catch (IOException e) {
                e.printStackTrace();
            }
            mHandler.obtainMessage(CONN_SUCCESS).sendToTarget();
        }
    };
    Runnable runnable_disconnect = new Runnable() {
        @Override
        public void run() {
            SSHDisConnectHost();
            mHandler.obtainMessage(DISCONN_SUCCESS).sendToTarget();
        }
    };

    /******** 用户密码验证 ********/
    private void SSHConnectHost(){
        if (sshConnection != null){
            sshConnection.close();
        }
        try {
            String hostname ;
            ArrayList<String> STRIP = getContectedIP();
            hostname = "192.168.1.251";
            //hostname = STRIP.toArray(new String[STRIP.size()])[0];      //转换为数组
            sshConnection = new Connection(hostname);
            sshConnection.connect();
            String username = "root";
            String password = "chgd";
            boolean isAuthenticated =sshConnection.authenticateWithPassword(username, password);
            if (!isAuthenticated){
                Log.i("test", String.format("ssh用户密码验证失败"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("test", String.format("ssh连接失败"));
        }
    }

    public static ArrayList<String> getContectedIP() {
        ArrayList<String> connectedIP = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(
                    "/proc/net/arp"));
            String line = br.readLine();        //字符串形式返回这一行数据
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");       //分割
                if (splitted != null && splitted.length >= 4) {
                    String ip = splitted[0];
                    connectedIP.add(ip);            //添加一个新元素
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.w("wifilog", connectedIP + "");
        return connectedIP;
    }

    /******** 去除返回值结尾换行符 ********/
    private String CutStr(String Str){
        String replaceStr = "\n";
        int i = Str.indexOf(replaceStr);
        String BackStr;
        BackStr= Str.substring(0,i);        //返回子字符串
        return BackStr;
    }

    static void SSHDisConnectHost(){
        sshConnection.close();
        sshConnection = null;
    }

    /****** 判断输入框是否输入正常范围数据 ******/
    public int JudgeRanges(){
        if (etBrightness.equals("-") || etHue.equals("-")){
            Toast.makeText(view.getContext(), "请输入正确范围内数据", Toast.LENGTH_LONG).show();
            return 0;
        }
        int intBrightness = Integer.parseInt(etBrightness);
        int intContrast = Integer.parseInt(etContrast);
        int intSaturation = Integer.parseInt(etSaturation);
        int intHue = Integer.parseInt(etHue);
        int intGain = Integer.parseInt(etGain);
        int intSharpness = Integer.parseInt(etSharpness);
        int intBacklightCompensation = Integer.parseInt(etBacklightCompensation);
        int intExposure = Integer.parseInt(etExposure);
        if ((intBrightness < -64 || intBrightness > 64)||
                (intContrast < 0 || intContrast > 64)||
                (intSaturation < 0 || intSaturation > 128)||
                (intHue < -40 || intHue > 40)||
                (intGain < 0 || intGain > 100)||
                (intSharpness < 0 || intSharpness > 6)||
                (intBacklightCompensation < 0 || intBacklightCompensation > 121)||
                (intExposure < 1 || intExposure > 5000)){
            Toast.makeText(view.getContext(), "请输入正确范围内数据", Toast.LENGTH_LONG).show();
            return 0;
        }else {
            return 1;
        }

    }

    /******* 发送命令线程 *******
     ******** 根据PIS值判断发送修改摄像头参数指令和修改openwrt中uvcdynctrl文档指令 ********/
    class SSHCmd2Openwrt extends Thread{
        @Override
        public void run() {
            try {
                a = 1;
                SSHExecuteCmd("Brightness",etBrightness,0);
                sleep(10);
                SSHExecuteCmd("Contrast",etContrast,0);
                sleep(10);
                SSHExecuteCmd("Saturation",etSaturation,0);
                sleep(10);
                SSHExecuteCmd("Hue",etHue,0);
                sleep(10);
                SSHExecuteCmd("Gain",etGain,0);
                sleep(10);
                SSHExecuteCmd("Sharpness",etSharpness,0);
                sleep(10);
                SSHExecuteCmd("Backlight Compensation",etBacklightCompensation,0);
                sleep(10);
                SSHExecuteCmd("Exposure, Auto",etExposureSwitch,0);
                sleep(10);
                if (etExposureSwitch.equals("1")) {
                    SSHExecuteCmd("Exposure (Absolute)", etExposure, 0);
                    sleep(10);
                }
                SSHExecuteCmd("","",1);
                mHandler.obtainMessage(CHANGE_SUCCESS).sendToTarget();
                a = 0;
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /******** 输入框恢复默认值 ********/
    public void DefaultInt(){
        String defaultBrightness = "0";
        Brightness.setText(defaultBrightness);
        String defaultContrast = "40";
        Contrast.setText(defaultContrast);
        String defaultSaturation = "0";
        Saturation.setText(defaultSaturation);
        String defaultHue = "0";
        Hue.setText(defaultHue);
        String defaultGain = "100";
        Gain.setText(defaultGain);
        String defaultSharpness = "2";
        Sharpness.setText(defaultSharpness);
        String defaultBacklightCompensation = "121";
        BacklightCompensation.setText(defaultBacklightCompensation);
        String defaultExposure = "5000";
        Exposure.setText(defaultExposure);
        ExposureSwitch.setChecked(false);
    }

    /******* 三种不同命令的发送和接收返回值 *******/
    public String SSHExecuteCmd(String option,String cmd,int PIS) throws IOException {
        String CMD;
        if (PIS == 0){
            CMD = "uvcdynctrl -s \""+option+"\" -- "+cmd;
        }else if (PIS == 1){
            CMD = "/usr/bin/uvcdynctrl -W /etc/config/uvcdynctrl";  //执行脚本命令读取探测器目前参数并更改目录下文件
        }else {
            CMD = "uvcdynctrl -g \""+option+"\"";
        }
        if (sshConnection == null) {
            Log.i("test", String.format("发送指令时Connect关闭"));
        }

        Session sshSession = sshConnection.openSession();
        if (sshSession == null) {
            throw new IOException("发送指令时新建Session失败");
        }
        sshSession.execCommand(CMD);

        Log.i("test", String.format("第 %d 次已发送指令",a));

        InputStream stdout = new StreamGobbler(sshSession.getStdout());//stdout标准输出
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stdout));

        char[] buf = new char[READ_BUF_SIZE];
        int len = bufferedReader.read(buf,0,buf.length);
        if (len<1){
            a++;
            Log.i("test", String.format("第 %d 返回值为空",a));
            return null;
        }

        stdout.close();
        bufferedReader.close();
        sshSession.close();
        a++;
        return new String(buf);
    }

}
