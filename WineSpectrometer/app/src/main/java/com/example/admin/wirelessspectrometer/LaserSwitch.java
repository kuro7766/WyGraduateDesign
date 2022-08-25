package com.example.admin.wirelessspectrometer;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import static java.lang.Thread.sleep;
/**激光亮灭控制
 * 亮 S1 = "$O1#" 返回O1
 * 灭 S0 = "$O0#" 返回O0
 * **/
public class LaserSwitch {
    private String S0 = "$O0#"; /**"0"+"\r"+"\n"**/
    private String S1 = "$O1#"; /**"1"+"\r"+"\n"**/
    public void laserswitch(boolean button,String url){
        boolean flage = true;
        int a =1;

        while (flage) {
            Socket socket = new Socket();
            SocketAddress socketAddress = new InetSocketAddress(url, 2001);

            try {
                long startTime1 = System.currentTimeMillis();
                socket.connect(socketAddress, 80);

                long endTime1 = System.currentTimeMillis(); //结束时间
                long runTime1 = endTime1 - startTime1;
                Log.i("test", String.format("第 %d 次发送命令 %d ms", a,runTime1));
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("test", String.format("第 %d 次socket建立出错",a));
                try {
                    sleep(10);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                a++;
                continue;
            }

            OutputStream socketwrite = null;
            try {
                socketwrite = socket.getOutputStream();
                PrintWriter printWriter = new PrintWriter(socketwrite);     //定义输出流

                if (button){
                    printWriter.write(S0);
                }else {
                    printWriter.write(S1);
                }

                printWriter.flush();
                printWriter.close();
                socket.close();
                try {
                    sleep(20);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("test", String.format("最后关闭socket出错"));
            }

            flage = false;

        }
    }
}
