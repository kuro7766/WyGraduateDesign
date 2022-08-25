package com.example.admin.wirelessspectrometer;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import static java.lang.Thread.sleep;

/**
 * 控制光谱仪激光强度的类
 * $M*# *是控制激光强度
 * 调节激光强度指令中*代表的是 ASCII 码中十六进制0x01到0x63所对应的字符，即1-99的强度
 */
public class LaserPower {

    /**
     * 作者的flage(flag)全部都和线程、while一起用，表示线程中的网络重试，直到发送成功为止
     */
    boolean flage = true;
    boolean flage1 = true;

    private final int MSG_LOW = 1;
    private final int MSG_MEDIUM = 2;
    private final int MSG_HIGH = 3;

    private final String LOW = "$RL#";
    private final String MEDIUM = "$RM#";
    private final String HIGH = "$RH#";

    /**
     * H0 M0哪个是对的? 还是说光谱仪后台程序写错了?
     */
    private final String String10 = "HO";

    /**
     * {@link LaserPower}每次new 都会跟随{@link MainShowData}变化
     */
    private final String String11 = MainShowData.string10.substring(1, 3);
    private final String String9 = MainShowData.string9.substring(1, 3);//"M"+"M";
    private final String String8 = MainShowData.string8.substring(1, 3);//"M"+"J";
    private final String String7 = MainShowData.string7.substring(1, 3);//"M"+"G";
    private final String String6 = MainShowData.string6.substring(1, 3);//"MB";
    private final String String5 = MainShowData.string5.substring(1, 3);//"M<";
    private final String String4 = MainShowData.string4.substring(1, 3);//"M6";
    private final String String3 = MainShowData.string3.substring(1, 3);//"M-";
    private final String String2 = MainShowData.string2.substring(1, 3);//"M ";
    private final String String1 = MainShowData.string1.substring(1, 3);//"L"+"\t";
    private final String String0 = "H" + "\t";


    int BACK;
    int LoopTime = 0;

    /**
     * 激光强度更改
     * power 强度指令 url 目标ip
     * 返回值为int，对应返回指令对应的强度档位
     * <p>
     * 这里可能是激光强度自动测量相关的代码
     * 首先函数传参发送一个power指令，然后通过socket发送出去，之后读取返回字符
     *
     * 发送指令、并将光谱仪socket返回的文字指令转换成强度数值
     */
    public int LPower(String power, String url) {
        Log.i("database-S4", String4 + "");
        while (flage) {
            Socket socket = new Socket();
            SocketAddress socketAddress = new InetSocketAddress(url, 2001);     //根据IP地址端口号创建套接字地址
            try {
                socket.connect(socketAddress, 80);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            //发送指令
            OutputStream socketwrite = null;
            try {
                socketwrite = socket.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(
                        new OutputStreamWriter(socketwrite));
                bufferedWriter.write(power);
                bufferedWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 读取返回字符
            InputStream inputStream = null;
            try {
                while (flage1) {
                    inputStream = socket.getInputStream();
                    int BackSize = inputStream.available(); //数据流可读取字节数
                    byte[] buffer = new byte[BackSize];
                    inputStream.read(buffer);
                    Log.i("string_return_byte", buffer + "");
                    sleep(10);

                    // 就是socket返回值，起名getLine就离谱，不会有新的line的
                    // java的低效性 stream->buffer->String
                    String getLine = new String(buffer);
                    Log.i("string_return_string", getLine + "");
                    Log.i("string_return_looptime", LoopTime + "");
//                    String OneGetline = (String) getLine.subSequence(0,1);

                    // 正常情况下，光谱仪会返回两个byte，否则就是错误
                    if (BackSize == 2) {
                        // 作者debug用，没有意义
                        int str = (int) getLine.charAt(1);
                        Log.i("string_return_decimal", str + "");
                        if (getLine.equals(String11)) {
                            BACK = 10;
                            Log.i("string0_11", String11 + "");
                            flage1 = false;
                        } else if (getLine.equals(String9)) {
                            BACK = 9;
                            flage1 = false;
                        } else if (getLine.equals(String8)) {
                            BACK = 8;
                            flage1 = false;
                        } else if (getLine.equals(String7)) {
                            BACK = 7;
                            flage1 = false;
                        } else if (getLine.equals(String6)) {
                            BACK = 6;
                            flage1 = false;
                        } else if (getLine.equals(String5)) {
                            BACK = 5;
                            flage1 = false;
                        } else if (getLine.equals(String4)) {

                            BACK = 4;
                            Log.i("string_return_judge", "y");
                            flage1 = false;
                            Log.i("string_return_back", BACK + "");
                        } else if (getLine.equals(String3)) {
                            BACK = 3;
                            flage1 = false;
                        } else if (getLine.equals(String2)) {
                            BACK = 2;
                            flage1 = false;
                        } else if (getLine.equals(String1)) {
                            BACK = 1;
                            flage1 = false;
                        }
                    }

                    // 出错重试的计数器
                    LoopTime++;
                    if (LoopTime == 100) {
                        flage1 = false;
                        BACK = 0;
                    }
//                    Log.i("test", String.format(getLine));
                }
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            flage = false;
        }
        Log.i("string_return_back", String.format("BACK = %d", BACK));
        return BACK;
    }
}
