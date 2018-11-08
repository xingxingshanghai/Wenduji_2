package com.cn.jianshi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/11/8.
 */

public class TimeSetting extends Activity {
    private SeekBar seekBar;
    private TextView description;
    private Button queding;
    private int value = 1;
    private int a ;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settingtime_layout);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = 850;
        params.height = 650 ;
        params.y = 150;
        params.x = -120;
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION| View.SYSTEM_UI_FLAG_IMMERSIVE;
        window.setAttributes(params);
        getActionBar().setTitle("报警温度设置");
        seekBar=(SeekBar)findViewById(R.id.seekBar);
        queding = (Button) findViewById(R.id.queding);
        description=(TextView)findViewById(R.id.description);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             * 拖动条停止拖动的时候调用
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                description.setText(value+"");
            }
            /**
             * 拖动条开始拖动的时候调用
             */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                description.setText("开始拖动");
            }
            /**
             * 拖动条进度改变的时候调用
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //progress从1到100变化
                value = 1+(int)((progress/100.0)*29);
                a = progress;
                description.setText(value+"");
            }
        });
        queding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent();
                mIntent.putExtra("change01", description.getText());
                // 设置结果，并进行传送
                TimeSetting.this.setResult(2, mIntent);
                finish();
            }
        });
    }
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK )
        {
            // 创建退出对话框
            AlertDialog isExit = new AlertDialog.Builder(this).create();
            // 设置对话框标题
            isExit.setTitle("系统提示");
            // 设置对话框消息
            isExit.setMessage("确定要退出吗");
            // 添加选择按钮并注册监听
            isExit.setButton("确定", listener);
            isExit.setButton2("取消", listener);
            // 显示对话框
            isExit.show();
        }
        return false;
    }
    /**监听对话框里面的button点击事件*/
    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
    {
        public void onClick(DialogInterface dialog, int which)
        {
            switch (which)
            {
                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
                    Intent mIntent = new Intent();
                    mIntent.putExtra("change01", description.getText());
                    // 设置结果，并进行传送
                    TimeSetting.this.setResult(1, mIntent);
                    finish();
                    break;
                case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
