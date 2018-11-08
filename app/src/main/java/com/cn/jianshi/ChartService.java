package com.cn.jianshi;

/**
 * Created by Administrator on 2017/11/3.
 */

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.List;

public class ChartService {
    private GraphicalView mGraphicalView;
    private XYMultipleSeriesDataset multipleSeriesDataset;// 数据集容器
    private XYMultipleSeriesRenderer multipleSeriesRenderer;// 渲染器容器
    private XYSeries mSeries;// 单条曲线数据集
    private XYSeriesRenderer mRenderer;// 单条曲线渲染器
    private Context context;
    public ChartService(Context context) {
        this.context = context;
    }
    /**
     * 获取图表
     *
     * @return
     */
    public GraphicalView getGraphicalView() {
        mGraphicalView = ChartFactory.getCubeLineChartView(context, multipleSeriesDataset, multipleSeriesRenderer, 0.3f);
        return mGraphicalView;
    }

    /**
     * 获取数据集，及xy坐标的集合
     *
     * @param curveTitle
     */
    public void setXYMultipleSeriesDataset(String curveTitle) {
        multipleSeriesDataset = new XYMultipleSeriesDataset();
        mSeries = new XYSeries("信号实时传输");
        multipleSeriesDataset.addSeries(mSeries);
    }

    /**
     * 获取渲染器
     *
     * @param maxX
     *            x轴最大值
     * @param maxY
     *            y轴最大值
     * @param chartTitle
     *            曲线的标题
     * @param xTitle
     *            x轴标题
     * @param yTitle
     *            y轴标题
     * @param axeColor
     *            坐标轴颜色
     * @param labelColor
     *            标题颜色
     * @param curveColor
     *            曲线颜色
     * @param gridColor
     *            网格颜色
     */
    public void setXYMultipleSeriesRenderer(double maxX, double maxY,
                                            String chartTitle, String xTitle, String yTitle, int axeColor,
                                            int labelColor, int curveColor, int gridColor) {
        multipleSeriesRenderer = new XYMultipleSeriesRenderer();
        if (chartTitle != null) {
            multipleSeriesRenderer.setChartTitle(chartTitle);
        }
        multipleSeriesRenderer.setXTitle(xTitle);
        multipleSeriesRenderer.setYTitle(yTitle);
        multipleSeriesRenderer.setRange(new double[] { 0, maxX, 21, maxY });//xy轴的范围
        multipleSeriesRenderer.setLabelsColor(labelColor);
        multipleSeriesRenderer.setXLabels(30);
        multipleSeriesRenderer.setYLabels(30);
        multipleSeriesRenderer.setXLabelsAlign(Align.RIGHT);
        multipleSeriesRenderer.setYLabelsAlign(Align.RIGHT);
        multipleSeriesRenderer.setAxisTitleTextSize(30);
        multipleSeriesRenderer.setChartTitleTextSize(40);
        multipleSeriesRenderer.setLabelsTextSize(20);
        //multipleSeriesRenderer.setLegendTextSize(20);
        multipleSeriesRenderer.setPointSize(7f);//曲线描点尺寸
        //multipleSeriesRenderer.setFitLegend(true);
       // multipleSeriesRenderer.setMargins(new int[] { 20（上）, 30(左), 15(下), 20(右) });
        multipleSeriesRenderer.setMargins(new int[] { 60, 60, 25, 35 });
        multipleSeriesRenderer.setShowGrid(true);
        //multipleSeriesRenderer.setShowGridY(true);
        //multipleSeriesRenderer.setZoomButtonsVisible(true);
        //设置x轴缩放，y轴不缩放
        multipleSeriesRenderer.setZoomEnabled(false, false);
        //multipleSeriesRenderer.setZoomEnabled(false);
        //设置坐标轴的颜色
        multipleSeriesRenderer.setAxesColor(axeColor);
        multipleSeriesRenderer.setXLabelsColor(Color.WHITE);
        multipleSeriesRenderer.setYLabelsColor(0, Color.WHITE);
        //设置移动
       // multipleSeriesRenderer.setPanEnabled(false);
        //设置x轴可移动，y轴不移动
        multipleSeriesRenderer.setPanEnabled(false,false);
//        //设置缩放范围
//        multipleSeriesRenderer.setZoomLimits(new double[] { 0, 10000, 0, 100 });//设置缩放的范围
//        multipleSeriesRenderer.setPanLimits(new double[] { 0, 10000, 0, 100 });//设置拉动的范围
        //设置网格的颜色
        multipleSeriesRenderer.setGridColor(gridColor);
        multipleSeriesRenderer.setApplyBackgroundColor(true);
        //multipleSeriesRenderer.setBackgroundColor(Color.GRAY);//背景色
        multipleSeriesRenderer.setBackgroundColor(Color.TRANSPARENT);

        //multipleSeriesRenderer.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));
        multipleSeriesRenderer.setMarginsColor(Color.parseColor("#ff4bb2f5"));//边距背景色，默认背景色为黑色，这里修改为白色
        //multipleSeriesRenderer.setMarginsColor(Color.WHITE);//边距背景色，默认背景色为黑色，这里修改为白色

        //曲线参数配置
        //legend显示与否
        multipleSeriesRenderer.setShowLegend(false);
        mRenderer = new XYSeriesRenderer();
        mRenderer.setColor(curveColor);
        mRenderer.setHighlighted(true);
        //mRenderer.setFillPoints(true);
        mRenderer.setLineWidth(5f);
        mRenderer.setPointStyle(PointStyle.CIRCLE);//描点风格，可以为圆点，方形点等等
        mRenderer.setFillPoints(true);
        mRenderer.setFillBelowLine(true);
        mRenderer.setFillBelowLineColor(Color.GREEN);
//        mRenderer.setGradientEnabled(true);
//        mRenderer.setGradientStart(0, Color.rgb(0x04, 0xa3, 0xff));
//        mRenderer.setGradientStop(0.8, Color.rgb(0x00, 0x89, 0xd8));
       // mRenderer.setDisplayChartValues(true);
        //追加曲线
        multipleSeriesRenderer.addSeriesRenderer(mRenderer);
    }
    /**
     * 根据新加的数据，更新曲线，只能运行在主线程
     *
     * @param x
     *            新加点的x坐标
     * @param y
     *            新加点的y坐标
     */
    public void updateChart(double x, double y) {
        if((x-1)%26 == 0){
            mSeries.clear();
        }

        mSeries.add((x-1)%26, y);
        mGraphicalView.repaint();//此处也可以调用invalidate()
    }

    /**
     * 添加新的数据，多组，更新曲线，只能运行在主线程
     * @param xList
     * @param yList
     */
    public void updateChart(List<Double> xList, List<Double> yList) {
        for (int i = 0; i < xList.size(); i++) {
            mSeries.add(xList.get(i), yList.get(i));
        }
        //mGraphicalView.repaint();//此处也可以调用invalidate()
        mGraphicalView.invalidate();
    }
}