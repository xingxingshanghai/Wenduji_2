package com.cn.jianshi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class Welcome extends Activity implements Runnable {

    //判断是否第一次启动程序
    private boolean isFirstUse;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HideStatusBar();
        setContentView(R.layout.test);
        /**
         * 开启线程
         */
        new Thread(this).start();
    }
    /**
     * 隐藏状态栏（全屏）
     * 在Activity.setCurrentView()之前调用此方法
     */
    private void HideStatusBar() {
// TODO TODO TODO TODO Auto-generated method stub
// 隐藏标题
        requestWindowFeature(Window. FEATURE_NO_TITLE );
// 定义全屏参数
        int flag= WindowManager.LayoutParams. FLAG_FULLSCREEN ;
// 获得窗口对象
        Window myWindow= this.getWindow();
// 设置 Flag 标识
        myWindow.setFlags(flag,flag);
    }
    public void run() {
        try {
            /**
             * 线程休眠时间
             */
            Thread.sleep(1500);
            //SharedPreferences存储
            SharedPreferences preferences = getSharedPreferences("isFirstUse", Context.MODE_PRIVATE);
            isFirstUse = preferences.getBoolean("isFirstUse", true);
            /**
             *判断是否第一次启动程序
             *如果是第一次启动GuideActivity.class，如果不是这进入主界面
             */
            if (isFirstUse) {
                startActivity(new Intent(Welcome.this, GuideActivity.class));
            } else {
                startActivity(new Intent(Welcome.this, MainActivity.class));
            }
            finish();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isFirstUse", false);
            editor.commit();


        } catch (InterruptedException e) {

        }
    }
}
