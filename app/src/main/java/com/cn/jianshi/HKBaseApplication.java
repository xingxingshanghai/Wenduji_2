package com.cn.jianshi;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/12/26.
 */

public class HKBaseApplication extends Application {
    // activity对象列表,用于activity统一管理
    private List<Activity> activityList;
    // 异常捕获
    protected boolean isNeedCaughtExeption = true;// 是否捕获未知异常
    private PendingIntent restartIntent;
    private MyUncaughtExceptionHandler uncaughtExceptionHandler;
    private String packgeName;

    @Override
    public void onCreate() {
        super.onCreate();

        activityList = new ArrayList<Activity>();
        packgeName = getPackageName();

        if (isNeedCaughtExeption) {
            cauchException();
        }
    }

    // -------------------异常捕获-----捕获异常后重启系统-----------------//

    private void cauchException() {
        Intent intent = new Intent();
        // 参数1：包名，参数2：程序入口的activity
        intent.setClassName(packgeName, packgeName + ".Welcome");
        restartIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //restartIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
        // 程序崩溃时触发线程
        uncaughtExceptionHandler = new MyUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
    }

    // 创建服务用于捕获崩溃异常
    private class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            // 保存错误日志
            saveCatchInfo2File(ex);

            // 1秒钟后重启应用
            AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);

            // 关闭当前应用
            finishAllActivity();
            finishProgram();
        }
    };

    /**
     * 保存错误信息到文件中
     *
     * @return 返回文件名称
     */
    private String saveCatchInfo2File(Throwable ex) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String sb = writer.toString();
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            String time = formatter.format(new Date());
            String fileName = time + ".txt";
            System.out.println("fileName:" + fileName);
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String filePath = Environment.getExternalStorageDirectory() + "/HKDownload/" + packgeName
                        + "/crash/";
                File dir = new File(filePath);
                if (!dir.exists()) {
                    if (!dir.mkdirs()) {
                        // 创建目录失败: 一般是因为SD卡被拔出了
                        return "";
                    }
                }
                System.out.println("filePath + fileName:" + filePath + fileName);
                FileOutputStream fos = new FileOutputStream(filePath + fileName);
                fos.write(sb.getBytes());
                fos.close();
                //文件保存完了之后,在应用下次启动的时候去检查错误日志,发现新的错误日志,就发送给开发者
            }
            return fileName;
        } catch (Exception e) {
            System.out.println("an error occured while writing file..." + e.getMessage());
        }
        return null;
    }

    // ------------------------------activity管理-----------------------//

    // activity管理：从列表中移除activity
    public void removeActivity(Activity activity) {
        activityList.remove(activity);
    }

    // activity管理：添加activity到列表
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    // activity管理：结束所有activity
    public void finishAllActivity() {
        for (Activity activity : activityList) {
            if (null != activity) {
                activity.finish();
            }
        }
    }

    // 结束线程,一般与finishAllActivity()一起使用
    // 例如: finishAllActivity;finishProgram();
    public void finishProgram() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}