package com.example.admin.wirelessspectrometer;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Looper;
import android.text.method.ReplacementTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;


/**  强度参数更改 （可忽略）
 * A simple {@link Fragment} subclass.
 */
public class ParametersIntensity extends android.support.v4.app.Fragment implements View.OnClickListener {


    private EditText editText;
    private View view;
    private Button imageButton;
    private String TV_Str,BytesToString;
    private Handler mhandler;
    private String StrIP;

    public ParametersIntensity() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        view = inflater.inflate(R.layout.fragment_parameters_intensity,container,false);
        init(view);
        ArrayList<String> STRIP = getContectedIP();
        StrIP = "192.168.1.251";
//        StrIP = STRIP.toArray(new String[STRIP.size()])[0];
        return view;
    }

    private void init(View view){
        editText = view.findViewById(R.id.intensity_et);
        imageButton = view.findViewById(R.id.intensity_ib);
        imageButton.setOnClickListener(this);
        editText.setTransformationMethod(new UpperCaseTransform());
    }

    public static ArrayList<String> getContectedIP() {
        ArrayList<String> connectedIP = new ArrayList<String>();
        try {
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

    @Override
    public void onClick(View view) {
        TV_Str = editText.getText().toString();
        try {
            BytesToString = bytes2String(hexString2Bytes(TV_Str));  //16进制→二进制→字符
            new mthread().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class mthread extends Thread{
        @Override
        public void run() {
//            Looper.prepare();
//            mhandler = new Handler(){
//                @Override
//                public void handleMessage(Message msg) {
//                    switch (msg.what){
//                        case 1:
//                            break;
//                        default:
//                            break;
//                    }
//                    LaserPower laserPower = new LaserPower();
//                    int ACCEPT = laserPower.LPower(BytesToString);
//                    Message message = Message.obtain();
//                }
//            };
//            Looper.loop();
            LaserPower laserPower = new LaserPower();
            int ACCEPT = laserPower.LPower(BytesToString,StrIP);
//            Message message = Message.obtain();
//            message.what = ACCEPT;
//            mhandler.sendMessage(message);
            Looper.prepare();
            if (ACCEPT == 4){
                Toast.makeText(view.getContext(),"激光强度修改成功",Toast.LENGTH_LONG).show();
            }else if (ACCEPT == 5){
                Toast.makeText(view.getContext(),"模式修改成功",Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(view.getContext(),"修改失败",Toast.LENGTH_LONG).show();
            }
            Looper.loop();
        }
    }

     static String bytes2String(byte[] b) throws Exception {//private
        if (b==null) return null;
        String r = new String (b,"UTF-8");
        return r;
    }

     static byte[] hexString2Bytes(String hex) {//private
        if ((hex == null) || (hex.equals(""))){
            return null;
        }
        else if (hex.length()%2 != 0){
            return null;
        }
        else{
            hex = hex.toUpperCase();    //小写转大写
            int len = hex.length()/2;
            byte[] b = new byte[len];
            char[] hc = hex.toCharArray();   
            for (int i=0; i<len; i++){
                int p=2*i;
                b[i] = (byte) (charToByte(hc[p]) << 4 | charToByte(hc[p+1]));
            }
            return b;
        }
    }

     static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    } //private

    private class UpperCaseTransform extends ReplacementTransformationMethod {      //小写转换为大写

        @Override
        protected char[] getOriginal() {
            char[] aa = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
            return aa;
        }

        @Override
        protected char[] getReplacement() {
            char[] cc = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
            return cc;
        }
    }
}
