package com.example.admin.wirelessspectrometer;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;


/**
 * 调用方式，在
 * @see com.example.admin.wirelessspectrometer.MainActivity#SendMessageValue(java.lang.String)
 * 里发送intent数据;
 * 实际的数据是在
 * @see com.example.admin.wirelessspectrometer.Database#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
 * 里面点击按钮事件生成的，生成函数为
 * @see com.example.admin.wirelessspectrometer.ContactinfoDao#alertData(int) 获得的
 */

/** 数据库点击 曲线显示
 * */
public class ShowData extends Activity {

    private XYMultipleSeriesRenderer renderer;
    private XYMultipleSeriesDataset dataset;
    private LinearLayout graphView;
    private GraphicalView chart;
    private XYSeries series;

    private int[] xx;

    private int[] spudata;
    private String str = null;
    private int time = 0;
    private int maxdata = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_show_data);
        graphView = findViewById(R.id.Dataview);

        xx = new int[1280];
        for (int i=0;i<1280;i++){
            xx[i] = i;
        }

        Intent intent = getIntent();
        String strspu = intent.getStringExtra("data");  //接收（取）MainActivity 数据

        spudata = new int[2048];
        for (int i=1;i<strspu.length();i++){
            char charat = strspu.charAt(i);     //返回指定索引处字符
            if ((charat != ',')&&(charat != ']')){
                str = str + charat;
            }else {
                spudata[time] = Integer.parseInt(str.substring(4,str.length()));    //substring提取字符串中介于两个指定下标之间的字符
                str = null;
                time++;
            }
        }
        for (int i=0;i<1280;i++){
            if (maxdata<spudata[i]){
                maxdata = spudata[i];
            }
        }

        XYMultipleSeriesRenderer RD = InitRenderer();
        XYMultipleSeriesDataset DA = InitDataset();
        chart = ChartFactory.getLineChartView(this,DA,RD);
        graphView.addView(chart);
        SetDataSeries();
    }

    private XYMultipleSeriesRenderer InitRenderer(){
        renderer = new XYMultipleSeriesRenderer();
        renderer.setAntialiasing(false);//true:消除锯齿；false:不消除锯齿
        renderer.setChartTitle("Spectral Curve");

        renderer.setChartTitleTextSize(60);//设置图表标题的字体大小(图的最上面文字)
        renderer.setMargins(new int[] { 50, 100, 50,50 });//设置外边距，顺序为：上左下右
        //坐标轴设置
        renderer.setShowLabels(true);
        renderer.setXTitle("CurveLength");
        renderer.setYTitle("Intensity");
        renderer.setAxisTitleTextSize(16);//设置坐标轴标题字体的大小
        renderer.setAxesColor(Color.BLACK);
        renderer.setXAxisMin(0);
        renderer.setXAxisMax(1300);
        renderer.setYAxisMin(0);
        renderer.setYAxisMax(10.0*maxdata/7);
        renderer.setLabelsTextSize(20);//设置坐标字号
        renderer.setXLabelsColor(Color.BLACK);// 设置X轴标签的显示颜色
        renderer.setYLabelsColor(0,Color.BLACK);
        renderer.setXLabelsAlign(Paint.Align.CENTER);// 设置X轴在标签哪边对齐方式
        renderer.setYLabelsAlign(Paint.Align.RIGHT);//设置Y轴在标签哪边对齐方式
        renderer.setXLabels(30);
        renderer.setYLabels(30);
        //设置颜色
        renderer.setApplyBackgroundColor(true); //内部底色
        renderer.setBackgroundColor(0xFFfffaf0); // 内部底色
        renderer.setLabelsColor(Color.BLACK);//设置标签颜色
        renderer.setMarginsColor(0xFFfffaf0); // 外部颜色，X轴坐标
        //缩放设置
        renderer.setZoomButtonsVisible(false);//设置缩放按钮是否可见
        renderer.setZoomEnabled(false,false); //图表是否可以缩放设置

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
        r.setColor(0xFF00C8FF);
        r.setPointStyle(PointStyle.POINT); // 点的形状
        r.setFillPoints(true); // 实心点
        r.setDisplayChartValues(false);// 设置显示数值
        r.setLineWidth(3);
        renderer.addSeriesRenderer(r);

        return renderer;
    }

    private XYMultipleSeriesDataset InitDataset(){
        dataset = new XYMultipleSeriesDataset();
        series = new XYSeries("像素--强度对照图");
        dataset.addSeries(series);
        return dataset;

    }

    private void SetDataSeries() {
        dataset.removeSeries(series);
        series.clear();
        for(int k = 0; k<1280; k++){
            series.add(xx[k], spudata[k]);
        }
        dataset.addSeries(series);
        chart.repaint();

    }
}