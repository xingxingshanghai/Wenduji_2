package com.cn.jianshi;

/**
 * Created by Administrator on 2018/1/5.
 */

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

public class MyChartUtil {
    /**
     * 显示图表
     * @param context
     *            上下文
     * @param lineChart
     *            图表对象
     * @param xDataList
     *            X轴数据
     * @param yDataList
     *            Y轴数据
     * @param title
     *            图表标题（如：XXX趋势图）
     * @param curveLable
     *            曲线图例名称（如：--用电量/时间）
     * @param unitName
     *            坐标点击弹出提示框中数字单位（如：KWH）
     */
    public static void showChart(Context context, LineChart lineChart, List<String> xDataList,
                                 List<Entry> yDataList, String title, String curveLable,String unitName) {
        // 设置数据
        lineChart.setData(setLineData(context, xDataList, yDataList, curveLable));
        CustomMarkerView mv = new CustomMarkerView(context, R.layout.chart_marker_view,unitName);
        // set the marker to the chart
        lineChart.setMarkerView(mv);
        //lineChart.setDrawMarkerViews(false);
        // 是否在折线图上添加边框
        lineChart.setDrawBorders(true);
        // 曲线描述 -标题
        lineChart.setDescription(title);
        // 标题字体大小
        lineChart.setDescriptionTextSize(12f);
        lineChart.setDescriptionPosition(650,30);
        // 标题字体颜色
        lineChart.setDescriptionColor(Color.parseColor("#ff808080"));
        // 如果没有数据的时候，会显示这个，类似文本框的placeholder
        lineChart.setNoDataTextDescription("暂无数据");
        // 是否显示表格颜色
        lineChart.setDrawGridBackground(false);
        // 禁止绘制图表边框的线
        lineChart.setDrawBorders(false);
        // 表格的的颜色，在这里是是给颜色设置一个透明度
        // lineChart.setGridBackgroundColor(Color.WHITE & 0x70FFFFFF);
        // 设置是否启动触摸响应
        lineChart.setTouchEnabled(true);
        // 是否可以拖拽
        lineChart.setDragEnabled(true);
        // 是否可以缩放
        lineChart.setScaleEnabled(true);
        // 如果禁用，可以在x和y轴上分别进行缩放
        lineChart.setPinchZoom(true);
        //lineChart.setMaxVisibleValueCount(6);
        // lineChart.setMarkerView(mv);
        // 设置背景色
        // lineChart.setBackgroundColor(getResources().getColor(R.color.bg_white));
        // 图例对象
        Legend mLegend = lineChart.getLegend();
        // mLegend.setPosition(LegendPosition.BELOW_CHART_CENTER);
        // 图例样式 (CIRCLE圆形；LINE线性；SQUARE是方块）
        mLegend.setForm(LegendForm.LINE);
        // 图例大小
        mLegend.setFormSize(8f);
        // 图例上的字体颜色
        mLegend.setTextColor(Color.parseColor("#ff808080"));
        mLegend.setTextSize(10f);
        // 图例字体
        // mLegend.setTypeface(mTf);
        // 图例的显示和隐藏
        mLegend.setEnabled(true);
        // 隐藏右侧Y轴（只在左侧的Y轴显示刻度）
        lineChart.getAxisRight().setEnabled(false);
        XAxis xAxis = lineChart.getXAxis();

        // 设置x轴的LimitLine
        LimitLine yLimitLine = new LimitLine(37.0f,"高温警戒线");
        yLimitLine.setLineColor(Color.rgb(244, 117, 117));
        yLimitLine.setTextSize(10f);
        yLimitLine.setTextColor(Color.rgb(244, 117, 117));
        yLimitLine.setLineWidth(2f);
        // 获得左侧侧坐标轴
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.addLimitLine(yLimitLine);
        //YAxis yAxis = lineChart;
        // 显示X轴上的刻度值
        xAxis.setDrawLabels(true);
        // 设置X轴的数据显示在报表的下方
        xAxis.setPosition(XAxisPosition.BOTTOM);
        //xAxis.setAxisMaxValue(yDataList.size()/6);
        // 轴线
        // xAxis.setDrawAxisLine(false);
        // 设置不从X轴发出纵向直线
        xAxis.setDrawGridLines(false);

        xAxis.setLabelsToSkip(yDataList.size()/5>=2?(yDataList.size()/5-1):0);
        //xAxis.resetLabelsToSkip();
        // 执行的动画,x轴（动画持续时间）
        //lineChart.animateX(1000);
        // lineChart.notifyDataSetChanged();


    }
    /**
     * 曲线赋值与设置
     *
     * @param context
     *            上下文
     * @param xDataList
     *            x轴数据
     * @param yDataList
     *            y轴数据
     * @return LineData
     */
    private static LineData setLineData(Context context, List<String> xDataList, List<Entry> yDataList,
                                        String curveLable) {
        // LineDataSet表示一条曲线数据对象
        ArrayList<LineDataSet> lineDataSets = new ArrayList<LineDataSet>();
        // y轴的数据集合
        LineDataSet lineDataSet = new LineDataSet(yDataList, curveLable);
        // mLineDataSet.setFillAlpha(110);
        // mLineDataSet.setFillColor(Color.RED);
        // 用y轴的集合来设置参数
        // 不显示坐标点的数据
        lineDataSet.setDrawValues(false);
        // 显示坐标点的小圆点
        lineDataSet.setDrawCircles(false);
        // 定位线
        lineDataSet.setHighlightEnabled(true);
        // 线宽
        lineDataSet.setLineWidth(2.0f);
        // 显示的圆形大小
        lineDataSet.setCircleSize(3f);
        // 显示颜色
        lineDataSet.setColor(Color.parseColor("#ffffffff"));
        //lineDataSet.setColor(Color.rgb(244, 117, 117));
        // 圆形的颜色
        lineDataSet.setCircleColor(Color.rgb(244, 117, 117));
        lineDataSet.setHighLightColor(Color.rgb(244, 117, 117));
        // 高亮的线的颜色
       // lineDataSet.setHighLightColor(Color.rgb(244, 117, 117));
        //lineDataSet.setHighlightLineWidth(0.5f);
        // 设置坐标点的颜色
        //lineDataSet.setFillColor(context.getApplicationContext().getResources().getColor(R.color.bg_gray));
        lineDataSet.setFillColor(Color.rgb(244, 117, 117));
        // 设置坐标点为空心环状
        //lineDataSet.setDrawCircleHole(false);
        // lineDataSet.setValueTextSize(9f);
        lineDataSet.setFillAlpha(85);
        // 设置显示曲线和X轴围成的区域阴影
        lineDataSet.setDrawFilled(true);
        // 坐标轴在左侧
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        // 设置每条曲线图例标签名
        // lineDataSet.setLabel("标签");
        lineDataSet.setValueTextSize(14f);
        // 曲线弧度（区间0.05f-1f，默认0.2f）
        lineDataSet.setCubicIntensity(0.2f);
        // 设置为曲线显示,false为折线
        lineDataSet.setDrawCubic(true);
        //lineDataSets.add(lineDataSet);
        // y轴的数据
        //LineData lineData = new LineData(xDataList,lineDataSets);
        LineData lineData =new LineData(xDataList,lineDataSet);
        return lineData;
    }
}

