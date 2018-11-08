package com.cn.jianshi;

import android.animation.ObjectAnimator;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

public class MusicService extends Service {
    //为日志工具设置标签
    private static String TAG = "MusicService";
    //定义音乐播放器变量
    public static MediaPlayer mPlayer = new MediaPlayer();
    //private MediaPlayer mPlayer;
    public static ObjectAnimator animator;
//    //声明键盘管理器
//    KeyguardManager mKeyguardManager = null;
//    //声明键盘锁
//    private KeyguardManager.KeyguardLock mKeyguardLock = null;
//    //声明电源管理器
//    private PowerManager pm;
//    private PowerManager.WakeLock wakeLock;
    //String path = MusicServiceActivity.sd_path;
    //该服务不存在需要被创建时被调用，不管startService()还是bindService()都会启动时调用该方法
    @Override
    public void onCreate() {
//        //获取电源的服务
//        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
////获取系统服务
//        mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        //Toast.makeText(this, "MusicSevice onCreate()"
        //        , Toast.LENGTH_SHORT).show();
        //Log.e(TAG, "MusicSerice onCreate()");
        mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.heat_alert);
//        //mPlayer.setDataSource(path);
//        //设置可以重复播放
        mPlayer.setLooping(true);
        //AnimatorAction();
        super.onCreate();
    }


    @Override
    public void onStart(Intent intent, int startId) {
        //Toast.makeText(this, "MusicSevice onStart()"
        //       , Toast.LENGTH_SHORT).show();
        Log.e(TAG, "MusicSerice onStart()");
//        Uri playUri = Uri.parse(intent.getStringExtra("something"));
//        mPlayer = MediaPlayer.create(getApplicationContext(), playUri);
//        mPlayer.setLooping(true);
//        //点亮亮屏
//        wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
//        wakeLock.acquire();
//        Log.i("Log : ", "------>mKeyguardLock");
////初始化键盘锁，可以锁定或解开键盘锁
//        mKeyguardLock = mKeyguardManager.newKeyguardLock("");
////禁用显示键盘锁定
//        if (mKeyguardManager .inKeyguardRestrictedInputMode()) {
//            // 解锁键盘
//            mKeyguardLock.disableKeyguard();
//        }
////        mKeyguardLock.disableKeyguard();

        mPlayer.start();
        super.onStart(intent, startId);
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        Log.i(TAG, "Service------->onStartCommand");
        //这里的返回有三种类型，可以自己手动返回。return XXXXX；
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // Toast.makeText(this, "MusicSevice onDestroy()"
        //  , Toast.LENGTH_SHORT).show();
        Log.e(TAG, "MusicSerice onDestroy()");
        //wakeLock.release();
        mPlayer.stop();
        super.onDestroy();
    }

    //其他对象通过bindService 方法通知该Service时该方法被调用
    @Override
    public IBinder onBind(Intent intent) {
        // Toast.makeText(this, "MusicSevice onBind()"
        //  , Toast.LENGTH_SHORT).show();
        Log.e(TAG, "MusicSerice onBind()");
        mPlayer.start();
        return null;
    }

    //其它对象通过unbindService方法通知该Service时该方法被调用
    @Override
    public boolean onUnbind(Intent intent) {
        //Toast.makeText(this, "MusicSevice onUnbind()"
        //, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "MusicSerice onUnbind()");
        mPlayer.stop();
        return super.onUnbind(intent);
    }

}