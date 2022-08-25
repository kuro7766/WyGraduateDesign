package com.example.admin.wirelessspectrometer;

import android.content.Context;
import android.util.Log;
import android.view.View;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import java.util.Arrays;

public class DataContrast {

    private String[] DataBase;
    private ContactinfoDao contactinfoDao;
    private String[] AllData;
    private double[][] DouData, Fdouble;
    private double[] scroreList;
    private int[] scoreIdList;
    private String str = null;
    private int time = 0;
    private int num, _id;
    private double[][] normalization;
    static String TAG = DataContrast.class.getSimpleName();

    /**
     * 对比
     *
     * @param RawDataR 光谱数据
     * @param power    强度档位
     * @param context
     * @return 对比结果
     */
    public Result Contrast2(int[] RawDataR, int power, Context context, int leftEdge, int rightEdge, int mindata) {
        // 原来是处理 0 - 2048
        int newLen = rightEdge - leftEdge;
        double[] Rawdata = new double[newLen];
        for (int i = 0; i < newLen; i++) {
            Rawdata[i] = RawDataR[i + leftEdge] - mindata;
        }
        power=10;
        int id=119;
        contactinfoDao = new ContactinfoDao(context);
        String backSpuctrum = contactinfoDao.alertData(id);
        int data_time=0;
        Result result = new Result();
        if (backSpuctrum==null){
            result.setId(-1);
            result.setMaxPs(0);
            result.setScoreList(null);
            result.setIdList(null);
            return result;
        }
        for (int i = 1; i < backSpuctrum.length(); i++) {
            char charat = backSpuctrum.charAt(i);     //返回指定索引处字符
            if ((charat != ',') && (charat != ']')) {
                str = str + charat;
            } else {
                Rawdata[data_time] = Integer.parseInt(str.substring(4, str.length()));
                str = null;
                data_time++;
            }

        }
        //筛选符合强度的数量
        num = contactinfoDao.number(power);
        //返回对应强度的id
        int[] res = contactinfoDao.id(power, num);
        //  对应光谱数据
        AllData = contactinfoDao.table_num(num, power);
//        num = contactinfoDao.DataNum();
//        AllData = contactinfoDao.AllData(num);
        DouData = new double[num][newLen];
        // String 转为 double类型
        for (int i = 0; i < num; i++) {
            for (int j = 1; j < AllData[i].length(); j++) {
                char charat = AllData[i].charAt(j);
                if ((charat != ',') && (charat != ']')) {
                    str = str + charat;
                } else {
                    DouData[i][time] = Float.parseFloat(str.substring(4, str.length())); //szhy 4,str.length() →0,str.length()-1
                    str = null;
                    time++;
                }
            }
            time = 0;
        }
        double[] OriginalData = FastFourierTransformer(NorDouData(Rawdata));
        double[][] FFTdatabase = FFT(NorData(DouData));
        int[] number = FFTContrast(OriginalData, FFTdatabase);
        Log.i("contrast", number + "");
        for (int i = 0; i < num; i++) {
            Log.i("test", String.format("numer = %d", number[i]));
        }
        Log.i("test", String.format("num = %d", num));
        //计算相关系数
        double maxPearson = SelectData(number, DouData, Rawdata);
        for (int i = 0; i <res.length; i++) {
            Log.i(TAG, "Contrast: res_"+i+"="+res[i]);
        }
        // 没有匹配项id为-1
        if (_id != -1) {
            result.setId(res[_id]);
        } else {
            result.setId(-1);
        }
        if (scroreList!=null) {
            for (int i = 0; i < scroreList.length; i++) {
                scoreIdList[i] = res[i];
            }
        }
        result.setMaxPs(maxPearson);
        result.setScoreList(scroreList);
        result.setIdList(scoreIdList);
        return result;
    }
    /**
     *  对比
     * @param RawDataR 光谱数据
     * @param power 强度档位
     * @param context
     * @return 对比结果
     */
    public Result Contrast(int[] RawDataR, int power, Context context,int leftEdge, int rightEdge,int mindata){
        // 原来是处理 0 - 2048
        int newLen = rightEdge - leftEdge;
        double[] Rawdata = new double[newLen];
        for (int i=0;i<newLen;i++){
            Rawdata[i] = RawDataR[i+leftEdge]-mindata;
        }

        contactinfoDao = new ContactinfoDao(context);
        //筛选符合强度的数量
        num = contactinfoDao.number(power);
        //返回对应强度的id
        int[] res = contactinfoDao.id(power,num);
        //  对应光谱数据
        AllData = contactinfoDao.table_num(num,power);
//        num = contactinfoDao.DataNum();
//        AllData = contactinfoDao.AllData(num);
        DouData = new double[num][newLen];
        // String 转为 double类型
        for (int i=0;i<num;i++){
            for (int j=1;j<AllData[i].length();j++){
                char charat = AllData[i].charAt(j);
                if ((charat != ',')&&(charat != ']')){
                    str = str + charat;
                }else {
                    DouData[i][time] = Float.parseFloat(str.substring(4,str.length())); //szhy 4,str.length() →0,str.length()-1
                    str = null;
                    time++;
                }
            }
            time = 0;
        }
        double[] OriginalData = FastFourierTransformer(NorDouData(Rawdata));
        double[][] FFTdatabase = FFT(NorData(DouData));
        int[] number = FFTContrast(OriginalData,FFTdatabase);
        Log.i("contrast",number+"");
        for (int i=0;i<num;i++){
            Log.i("test", String.format("numer = %d",number[i]));
        }
        Log.i("test", String.format("num = %d",num));
        //计算相关系数
        double maxPearson = SelectData(number,DouData,Rawdata);
        Result result = new Result();
        // 没有匹配项id为-1
        if(_id != -1) {
            result.setId(res[_id]);
        } else {result.setId(-1);}
        result.setMaxPs(maxPearson);
        if (scroreList!=null) {
            for (int i = 0; i < scroreList.length; i++) {
                scoreIdList[i] = res[i];
            }
        }
        result.setMaxPs(maxPearson);
        result.setScoreList(scroreList);
        result.setIdList(scoreIdList);
        return result;
    }


    private double[][] NorData(double[][] data) {  //二维数组归一化
        normalization = new double[num][2048];
        for (int i = 0; i < num; i++) {
            normalization[i] = NorDouData(data[i]);
        }
        return normalization;
    }

    private double[] NorDouData(double[] data) {  //一维数组归一化
//        double[] reDouble = new double[2048];
        double[] reDouble = new double[2048];
        double max = 0;
//        for (int j=0;j<1280;j++){
        for (int j = 0; j < data.length; j++) {
            if (max < data[j]) {
                max = data[j];
            }
        }
        for (int j = 0; j < data.length; j++) {
            reDouble[j] = data[j] / max;
        }
        return reDouble;
    }

    private double[][] FFT(double[][] data) {    //返回傅立叶变换前四个值
        Fdouble = new double[num][4];
        for (int i = 0; i < num; i++) {
            Fdouble[i] = FastFourierTransformer(data[i]);
        }
        return Fdouble;
    }

    private double[] FastFourierTransformer(double[] data) {
        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
        Complex[] result = fft.transform(data, TransformType.FORWARD);
        double[] FFTFour = new double[4];
        for (int j = 0; j < 4; j++) {
            FFTFour[j] = Count(result[j].getReal(), result[j].getImaginary());
        }
        return FFTFour;
    }

    private double Count(double real, double imaginary) {
        return Math.sqrt(real * real + imaginary * imaginary);
    }

    private int[] FFTContrast(double[] OriginalData, double[][] FFTdatabase) {
        int[] number = new int[num];
        Arrays.fill(number, -1);
        int b = 0;
        for (int i = 0; i < num; i++) {
            int a = 0;
//            if (Math.abs(FFTdatabase[i][0]-OriginalData[0])/FFTdatabase[i][0]<0.5){
//                if (Math.abs(FFTdatabase[i][1]-OriginalData[1])/FFTdatabase[i][1]<0.5){
//                    if (Math.abs(FFTdatabase[i][2]-OriginalData[2])/FFTdatabase[i][2]<0.5 ||
//                            Math.abs(FFTdatabase[i][3]-OriginalData[3])/FFTdatabase[i][3]<0.5){
            number[b++] = i;
//                    }
//                }
//            }
            Log.i("contrast", number[i] + "");
        }
        return number;
    }

    /****************上为FFT粗筛，下为相关系数细筛****************/

//    public static double getPearsonCorrelationScore(List x, List y) {
//        if (x.size() != y.size())
//            throw new RuntimeException("数据不正确！");
//        double[] xData = new double[x.size()];
//        double[] yData = new double[x.size()];
//        for (int i = 0; i < x.size(); i++) {
//            xData[i] = x.get(i);
//            yData[i] = y.get(i);
//        }
//        return getPearsonCorrelationScore(xData,yData);
//    }

    /**
     * 归一化
     **/
    private static double[] GYHData(double[] data) {
        double[] reDouble = new double[data.length];
        double max = 0;
        for (int j = 0; j < data.length; j++) {
            if (max < data[j]) {
                max = data[j];
            }
        }
        for (int j = 0; j < data.length; j++) {
            reDouble[j] = data[j] / max;
        }
        return reDouble;
    }

    private double SelectData(int[] number, double[][] DataBase, double[] NowData) {
        int length = 0;
        _id = -1;  //不存在id返回-1
        double maxP = 0;
        for (int i = 0; i < number.length; i++) {
            if (number[i] != -1) {
                length++;
            }
        }
        Log.i("contrast_length", length + "");
        // 如果匹配则number中有不为-1，length不等0 ，有length个
        if (length != 0) {
            double[] PearsonData = new double[length];
            double[][] FFTDataBase = new double[length][number.length];
            double[] NowDataCut;
            //去掉归一化
            NowDataCut = CutData(NowData);
            for (int i = 0; i < length; i++) {
                //去掉归一化
                FFTDataBase[i] = GYHData(CutData(DataBase[number[i]])); //筛选出来符合条件的数组归一化
//                FFTDataBase[i] = CutData(DataBase[number[i]]);
                // TODO: 2021/3/22 在这修改 就行
                PearsonData[i] = getPearsonCorrelationScore(FFTDataBase[i], NowDataCut);
//                PearsonData[i] = lamanNormanDistanceScore(FFTDataBase[i], NowDataCut, 200);

            }
            scoreIdList= new int[length];
            scroreList= new double[length];
            for (int i = 0; i < length; i++) {
                scroreList[i]=PearsonData[i];
                Log.i(TAG, "SelectData:匹配结果：id="+i+";score="+PearsonData[i]);
                if (PearsonData[i] > maxP) {
                    maxP = PearsonData[i];
                    _id = i;
                }
            }
            Log.i("contrast_id", _id + "");
            Log.i("contrast_id", PearsonData.toString());
            Log.i(TAG, "SelectData:最大匹配结果：id="+_id+";score="+maxP);
            return maxP;
        } else {
            return 0;
        }
    }

    //数组长度 2048 转 1280
    private double[] CutData(double[] data2048) {
//        double[] returndata = new double[1280];
//        for (int i=0;i<1280;i++){
//            returndata[i] = data2048[i];
//        }
        return data2048;
    }

    // 最终计算的相关系数
    public static double getPearsonCorrelationScore(double[] xData, double[] yData) {
        Log.i("syd", "getPearsonCorrelationScore: " + xData.length + ";" + yData.length);
        if (xData.length != yData.length)
            throw new RuntimeException("数据不正确！");
        double xMeans;
        double yMeans;
        double numerator = 0;
        // 求解皮尔逊的分子
        double denominator = 0;
        // 求解皮尔逊系数的分母
        double result = 0;
        // 拿到两个数据的平均值
        xMeans = getMeans(xData);
        yMeans = getMeans(yData);
        // 计算皮尔逊系数的分子
        numerator = generateNumerator(xData, xMeans, yData, yMeans);
        // 计算皮尔逊系数的分母
        denominator = generateDenomiator(xData, xMeans, yData, yMeans);
        // 计算皮尔逊系数
        result = numerator / denominator;
        return result;
    }
    // 拉曼峰归一化差值相关系数 2021-

    /**
     * 拉曼峰归一化差值相关系数
     *
     * @param xData      数据库曲线
     * @param yData      当前曲线
     * @param lamanIndex 拉曼峰位置，例如200
     * @return
     */
    public static double lamanNormanDistanceScore(double[] xData, double[] yData, int lamanIndex) {
//        Log.i(DataContrast.class.getSimpleName(), "lamanNormanDistanceScore: " + xData.length + ";" + yData.length);
        //计算拉曼值倍数
        double lamanScale = xData[lamanIndex] / yData[lamanIndex];
//        Log.i(TAG, "lamanNormanDistanceScore: lamanScale=" + lamanScale);
        //进行拉曼倍数缩放
        double[] newxData = new double[xData.length];
        double scaledValue = 0;
        double distance = 0;
        double distance_sum = 0;
        for (int i = 0; i < xData.length; i++) {
            newxData[i] = xData[i] / lamanScale;
//            if (i == lamanIndex) {
//                Log.i(TAG, "lamanNormanDistanceScore: value at lamanIndex:" + newxData[i] + ";" + yData[i]);
//            }
        }
//        newxData=GYHData(newxData);
//        yData=GYHData(yData);
        int filter_nums = 0;
        for (int i = 0; i < xData.length; i++) {
//            Log.i(DataContrast.class.getSimpleName(), "lamanNormanDistanceScore: " + newxData[i] + ";" + yData[i]);
            if (yData[i] < 1500) {
                filter_nums += 1;
                continue;
            }
            distance = Math.abs(newxData[i] - yData[i]) / (yData[i]);

            distance_sum += distance;
        }
        distance = distance_sum / (xData.length - filter_nums);
        double score = 1 - distance;
//        Log.i(TAG, "lamanNormanDistanceScore: score=" + score);
        return score;
    }

    /**
     * 皮尔逊系数分子
     **/
    private static double generateNumerator(double[] xData, double xMeans, double[] yData, double yMeans) {
        double numerator = 0.0;
        for (int i = 0; i < xData.length; i++) {
            numerator += (xData[i] - xMeans) * (yData[i] - yMeans);
        }
        return numerator;
    }

    /**
     * 皮尔逊系数分母
     **/
    private static double generateDenomiator(double[] xData, double xMeans, double[] yData, double yMeans) {
        double xSum = 0.0;
        for (int i = 0; i < xData.length; i++) {
            xSum += (xData[i] - xMeans) * (xData[i] - xMeans);
        }
        double ySum = 0.0;
        for (int i = 0; i < yData.length; i++) {
            ySum += (yData[i] - yMeans) * (yData[i] - yMeans);
        }
        return Math.sqrt(xSum) * Math.sqrt(ySum);
    }

    /**
     * 平均值
     **/
    private static double getMeans(double[] datas) {
        double sum = 0.0;
        for (int i = 0; i < datas.length; i++) {
            sum += datas[i];
        }
        return sum / datas.length;
    }
}

class Result {
    int id;
    double maxPs;
    double[] scoreList;
    int[] idList;

    public double[] getScoreList() {
        return scoreList;
    }

    public void setScoreList(double[] scoreList) {
        this.scoreList = scoreList;
    }

    public int[] getIdList() {
        return idList;
    }

    public void setIdList(int[] idList) {
        this.idList = idList;
    }

    public Result() {
        super();
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMaxPs(double maxPs) {
        this.maxPs = maxPs;
    }

    public int getId() {
        return id;
    }

    public double getMaxPs() {
        return maxPs;
    }
}
