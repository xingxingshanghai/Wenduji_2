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

public class TemperatureSetting extends Activity {
    private SeekBar seekBar;
    private TextView description;
    private Button queding;
    private double value = 32.0;
    private String a="0" ;
    private boolean biaoshi = false;
    private String value1 ="0";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);
        Intent intent = getIntent();
        //从Intent当中根据key取得value
        value1 = intent.getStringExtra("testIntent");
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = 850;
        params.height = 650 ;
        params.y = 50;
        params.x = 10;
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION| View.SYSTEM_UI_FLAG_IMMERSIVE;
        window.setAttributes(params);
        getActionBar().setTitle("报警温度设置");
        getActionBar().setBackgroundDrawable(getDrawable(R.color.colorPrimaryDark));
        seekBar=(SeekBar)findViewById(R.id.seekBar);
        queding = (Button) findViewById(R.id.queding);
        description=(TextView)findViewById(R.id.description);
        seekBar.setProgress(Integer.parseInt(value1));
        description.setText((32.0+Integer.parseInt(value1)/10.0)+"");
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             * 拖动条停止拖动的时候调用
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(!biaoshi)
                    description.setText(32.0+Integer.parseInt(value1)/10.0+"");
                else
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
                value = 32.0+progress/10.0;
                biaoshi = true;
                description.setText((32.0+progress/10.0)+"");
                a = ""+progress;
            }
        });
        queding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent();
                mIntent.putExtra("change01", description.getText());
                if(!biaoshi)
                    mIntent.putExtra("wendupercent",value1);
                else
                mIntent.putExtra("wendupercent",a);
                // 设置结果，并进行传送
                TemperatureSetting.this.setResult(1, mIntent);
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
                    TemperatureSetting.this.setResult(1, mIntent);
                    if (a.equals(""))
                    mIntent.putExtra("wendupercent",a);
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

//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
////        // User chose not to enable Bluetooth.
//        if (resultCode == Activity.RESULT_CANCELED) {
//            Intent mIntent = new Intent();
//            mIntent.putExtra("change01", description.getText());
//            // 设置结果，并进行传送
//            TemperatureSetting.this.setResult(1, mIntent);
//            finish();
//            return;
//        }
//        //super.onActivityResult(requestCode, resultCode, data);
//    }
    }
