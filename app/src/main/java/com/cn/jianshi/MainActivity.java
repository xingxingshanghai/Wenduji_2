package com.cn.jianshi;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import butterknife.ButterKnife;

import static com.cn.jianshi.EmptyUtil.isEmpty;
import static com.cn.jianshi.hexToBytes.hexStringToBytes;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    //AppCompatActivity
    private ImageView mImageView;
    private final static String TAG = MainActivity.class.getSimpleName();
    private TextView alertValueDis;
    private String  mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;
    private BluetoothGattCharacteristic mNotifyCharacteristic,gattCharacteristic;
    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 3;
    private boolean bindor = false;
    protected static boolean bluetoothstate = false;
    private boolean alertsign = false;
    private int batteryPecentCase = 4;
    private ImageView battery,alertValue,timesetting,lanyazhishi,lishiquxian;
    private int batterystate = 3;
    private Intent mIntent;
    private double maxWendu=0.00;
    private ToggleButton wenduType;
    Timer timer=new Timer();
    private BluetoothLeScanner scanner;
    Timer overtimer = new Timer();
    private int period = 65;
    private int period1 = 10;
    private int biaoji = 0;
    SimpleDateFormat formatter=new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
    File file = new File(Environment.getExternalStorageDirectory(), "test.txt");
    private int settingTime =1;
    BluetoothGattCharacteristic gattCharacteristic2;
    SharedPreferences preferences;
    String wenduke="0";
    private boolean times = false;
    private String thr = ""+(32.0+Integer.parseInt(wenduke)/10.0);
    private PieChart mPieChart;
    private boolean wdType = false;
    private Person person1;
    private long datasum;
    private DBManager mgr;
    SimpleDateFormat    sDateFormat    =   new SimpleDateFormat("yyyyMMdd HH:mm:ss");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences("isFirstUse",Context.MODE_PRIVATE);
        wenduke=preferences.getString("wenduke", "0");
        thr = ""+(32.0+Integer.parseInt(wenduke)/10.0);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        initlizeViews1();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        Log.d(TAG, "---onCreate()---");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //mTextView = (TextView) findViewById(R.id.textView);
        mImageView = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.ivAvatar);
        mImageView.setOnClickListener(this);
        ButterKnife.bind(this);
        mgr = new DBManager(this);
    }
    private void initlizeViews1() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);//这里与标准蓝牙略有不同
        //final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        // mPieChart = (PieChart) findViewById(R.id.mPieChart);
        mPieChart = (PieChart)findViewById(R.id.mPieChart);
        mPieChart.setData(getPieData("","",""));
        Legend mLegend = mPieChart.getLegend();
        mLegend.setEnabled(false);
        mPieChart.setDescription("");
        mPieChart.setDrawCenterText(true);
        mPieChart.setCenterTextSize(24f);
//        mPieChart.setCenterTextSizePixels(5f);
        // mPieChart.setCenterTextRadiusPercent(0.3f);
        mPieChart.setCenterText("搜索中...");
        mPieChart.setHoleRadius(60f);
        mPieChart.setTransparentCircleRadius(65f);
        //mPieChart.setRotationAngle(90);
        mPieChart.setRotationEnabled(false);
        //Color.parseColor("#C1FFC1")
        mPieChart.setCenterTextColor(ColorTemplate.JOYFUL_COLORS[1]);
       // mPieChart.animateXY(1000,1000);
        battery = (ImageView) findViewById(R.id.batterydis);
        alertValue= (ImageView) findViewById(R.id.alertValue);
       // lanyazhishi = (ImageView) findViewById(R.id.lanyazhishi);
        timesetting = (ImageView) findViewById(R.id.timesetting);
        alertValueDis = (TextView) findViewById(R.id.alertValueDis);
        lishiquxian = (ImageView) findViewById(R.id.lishiquxian);
        wenduType = (ToggleButton) findViewById(R.id.mTogBtn);
        alertValueDis.setText(thr);
        // getActionBar().setTitle(typename);
        mIntent = new Intent();
        mIntent.setClass(MainActivity.this, TemperatureSetting.class);
        final DecimalFormat df = new DecimalFormat("#%");
        battery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "剩余电量:"+df.format(batterystate/3.0), Toast.LENGTH_SHORT).show();
            }
        });
        lishiquxian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent curveintent = new Intent(MainActivity.this,RiliActivity.class);
                //mgr.closeDB();
                startActivity(curveintent);
                //Toast.makeText(MainActivity.this, "剩余电量:"+df.format(batterystate/3.0), Toast.LENGTH_SHORT).show();
            }
        });
        alertValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIntent.putExtra("testIntent", wenduke);
                startActivityForResult(mIntent, 1);
            }
        });
        timesetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivityForResult(mIntent1, 2);
                if(isEmpty(mBluetoothLeService))
                    Toast.makeText(MainActivity.this, "未连接", Toast.LENGTH_SHORT).show();
                else{
                    writeGattServices(mBluetoothLeService.getSupportedGattServices());
                Toast.makeText(MainActivity.this, "请求数据已发送", Toast.LENGTH_SHORT).show();
                }
            }
        });
        wenduType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    wdType = true;
                    if(isEmpty(mBluetoothLeService))
                        Toast.makeText(MainActivity.this, "未连接", Toast.LENGTH_SHORT).show();
                    else{
                        writeGattServices(mBluetoothLeService.getSupportedGattServices());
                    }
                }
                else{
                    wdType = false;
                }
            }
        });

    }
    public PieData getPieData(String max,String timeMoni,String fasaoCi) {
        ArrayList<Entry> list = new ArrayList<>();
        for (int i=0;i<1;i++){
            //一个Entry就是一个饼块
            Entry entry = new Entry(2,i);
            list.add(entry);
//            entry = new Entry(1,2);
//             list.add(entry);
//            entry = new Entry(1,3);
//        list.add(entry);
        }
        //创建一组饼块的数据
        PieDataSet pieDataSet = new PieDataSet(list,"标签");
        //设置饼块的间距
        //pieDataSet.setSliceSpace(1f);
        ArrayList<Integer> colors = new ArrayList<Integer>();
//        for(int c:ColorTemplate.VORDIPLOM_COLORS)
//            colors.add(c);
//        for(int c:ColorTemplate.JOYFUL_COLORS)
//            colors.add(c);
//        for(int c:ColorTemplate.COLORFUL_COLORS)
//            colors.add(c);
        for(int c:ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
//        for(int c:ColorTemplate.PASTEL_COLORS)
//            colors.add(c);
        //colors.add(ColorTemplate.getHoloBlue());
        //设置饼快颜色
        //pieDataSet.setColors(new int[]{Color.RED,Color.GREEN,Color.BLUE});
        pieDataSet.setColors(colors);
        //创建x轴
        ArrayList<String> xList = new ArrayList<>();
        //xList.add("时长:"+timeMoni);
        xList.add("最高温度:"+max);
        //xList.add("发烧次数:"+fasaoCi);
        PieData pieData = new PieData(xList,pieDataSet);
        pieData.setValueTextSize(12f);
        pieData.setDrawValues(false);
        pieData.setValueTextColor(ColorTemplate.JOYFUL_COLORS[1]);
        return pieData;
    }
    private SpannableString generateCeterSpannableText(String sbf){
        SpannableString s = new SpannableString(sbf);
        s.setSpan(new StyleSpan(Typeface.BOLD),0,sbf.length(),0);
        s.setSpan(new RelativeSizeSpan(2.0f),0,sbf.length()-4,0);
        //s.setSpan(new RelativeSizeSpan(1.5f),0,sbf.length()-2,0);
        if(sbf.substring(sbf.length()-2,sbf.length()).equals("偏高")){
            s.setSpan(new ForegroundColorSpan(Color.parseColor("#DDA0DD")),0,sbf.length(),0);
            s.setSpan(new RelativeSizeSpan(0.5f),sbf.length()-2,sbf.length(),0);
        }
        else{
            //s.setSpan(new ForegroundColorSpan(Color.GREEN),sbf.length()-2,sbf.length(),0);
            s.setSpan(new RelativeSizeSpan(0.5f),sbf.length()-2,sbf.length(),0);
        }
        return s;
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        String string = null;
        switch (id){
            case R.id.nav_me:
                string = "体温记录";
                startActivity(new Intent(this,RiliActivity.class));
                break;
            case R.id.nav_friend:
                string = "软件设置";
                break;
            case R.id.nav_message:
                string = "使用指南";
                startActivity(new Intent(this,UserGuideActivity.class));
                break;
            case R.id.nav_notification:
                string = "关于我们";
                startActivity(new Intent(this,AboutUs.class));
                break;
        }
//        if (!TextUtils.isEmpty(string)){
//            //mTextView.setText("你点击了"+string);
//            Toast.makeText(this,"你点击了"+string , Toast.LENGTH_SHORT).show();
////            Intent intent = new Intent(this,DeviceControlActivity.class);
////            startActivity(intent);
//        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }
    //用户注册登录
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.ivAvatar){
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        }
    }
    //新加入
    //    回调方法，从第二个页面回来的时候会执行这个方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        //用户取消打开蓝牙时，直接关闭程序
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            Toast.makeText(this, "本应用需要蓝牙服务，请允许打开蓝牙！", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(requestCode == 1){
            String change01 = data.getStringExtra("change01");
            wenduke =  data.getStringExtra("wendupercent");
            //保存温度数据
            //SharedPreferences preferences=getSharedPreferences("user",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putString("wenduke", wenduke);
            editor.commit();
            thr = ""+(32.0+Integer.parseInt(wenduke)/10.0);
            alertValueDis.setText(change01);
        }
    }
    //警报确定按钮事件
    public class okListener implements DialogInterface.OnClickListener{
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // TODO Auto-generated method stub
            unbindService(conn);
            alertsign = false;
        }
    }
    //alert取消事件
    public class noListener implements DialogInterface.OnClickListener{
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // TODO Auto-generated method stub

        }
    }
    //定义服务链接对象
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "MusicServiceActivity onSeviceDisconnected");
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "MusicServiceActivity onServiceConnected");
        }
    };
    //开始扫描
    private void startBleScan(){
        Log.e(TAG, "扫描函数开启");

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {//安卓6.0以下的方案
            List<ScanFilter> filters = new ArrayList<ScanFilter>();
            ScanSettings settings = (new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)).build();
            ScanFilter.Builder builder = new ScanFilter.Builder();
            //builder.setManufacturerData((int) 0x0118, new byte[]{(byte) 0xbe, (byte) 0xac}, new byte[]{(byte) 0xff, (byte)0xff});
            builder.setDeviceAddress("D5:3C:59:AF:31:F5");
            //builder.setServiceUuid(ParcelUuid.fromString("0000fee7-0000-1000-8000-00805f9b34fb"));
            ScanFilter scanFilter = builder.build();
            filters.add(scanFilter);
            mBluetoothAdapter.getBluetoothLeScanner().startScan(filters,settings,mScanCallback);
            //mBluetoothAdapter.getBluetoothLeScanner().
            Log.e(TAG, "扫描函数扫描中");
        } else {//安卓7.0及以上的方案
            //判断是否有权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                 //请求权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                 //判断是否需要 向用户解释，为什么要申请该权限
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                    Toast.makeText(this, "shouldShowRequestPermissionRationale", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                List<ScanFilter> filters = new ArrayList<ScanFilter>();
                ScanSettings settings = (new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)).build();
                ScanFilter.Builder builder = new ScanFilter.Builder();
                //builder.setManufacturerData((int) 0x0118, new byte[]{(byte) 0xbe, (byte) 0xac}, new byte[]{(byte) 0xff, (byte)0xff});
                builder.setDeviceAddress("D5:3C:59:AF:31:F5");
                //builder.setServiceUuid(ParcelUuid.fromString("0000fee7-0000-1000-8000-00805f9b34fb"));
                ScanFilter scanFilter = builder.build();
                filters.add(scanFilter);
                mBluetoothAdapter.getBluetoothLeScanner().startScan(filters,settings,mScanCallback);
                //mBluetoothAdapter.getBluetoothLeScanner().
                Log.e(TAG, "扫描函数扫描中");
            }
        }

//        List<ScanFilter> filters = new ArrayList<ScanFilter>();
//        ScanSettings settings = (new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)).build();
//        ScanFilter.Builder builder = new ScanFilter.Builder();
//        //builder.setManufacturerData((int) 0x0118, new byte[]{(byte) 0xbe, (byte) 0xac}, new byte[]{(byte) 0xff, (byte)0xff});
//        builder.setDeviceAddress("D5:3C:59:AF:31:F5");
//        ScanFilter scanFilter = builder.build();
//        filters.add(scanFilter);
//        mBluetoothAdapter.getBluetoothLeScanner().startScan(filters,settings,mScanCallback);
//        //mBluetoothAdapter.getBluetoothLeScanner().
//        Log.e(TAG, "扫描函数扫描中");

    }
    //停止扫描
    private void stopBleScan(){
        mBluetoothAdapter.getBluetoothLeScanner().stopScan(mScanCallback);
    }
    //ScanCallback 是蓝牙扫描返回结果的回调，可以通过回调获取扫描结果。
    private ScanCallback mScanCallback = new ScanCallback() {
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            //mPieChart.setCenterText("正在监控");
            //mPieChart.animateXY(500,500);
            //mPieChart.invalidate();
            Log.e(TAG, "扫描---");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                byte[] scanRecord = null;
                BluetoothDevice device = result.getDevice();
                //byte[] scanRecord = result.getScanRecord().getBytes();
                Log.e(TAG, ""+device.getAddress());
                if(device!=null) {
                    //26:21
                    //26:1F
                    //"82:EA:CA:10:26:1F"
                    //Ti硬件设备 D5:3C:59:AF:31:F5
                    if (device.getName() != null&&device.getAddress().equals("D5:3C:59:AF:31:F5")) {  //判断是否已经添加
                        scanRecord = result.getScanRecord().getBytes();//System.arraycopy(src, srcPos, dest, destPos, length)
                        scanRecord = Arrays.copyOfRange(scanRecord,11,27);
                        //mPieChart.setCenterText(bytesToHexString(scanRecord));//此处修改为监控的温度数据
                        //mPieChart.animateXY(500,500);
                        //mPieChart.invalidate();
                        Log.d(TAG, "---Receive Data---"+bytesToHexString(scanRecord));
//                        mDeviceAddress = device.getAddress().trim();
//                        Intent gattServiceIntent = new Intent(MainActivity.this, BluetoothLeService.class);
//                        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
//                        stopBleScan();
//                        //mBluetoothAdapter.stopLeScan(mLeScanCallback);
//                        bindor = true;

                   }
                }
            }
        }
        public String bytesToHexString(byte[] src){
            StringBuilder stringBuilder = new StringBuilder("");
            if (src == null || src.length <= 0) {
                return null;
            }
            for (int i = 0; i < src.length; i++) {
                int v = src[i] & 0xFF;
                String hv = Integer.toHexString(v);
                if (hv.length() < 2) {
                    stringBuilder.append(0);
                }
                stringBuilder.append(hv);
            }
            return stringBuilder.toString();
        }
        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            //在此返回一个包含所有扫描结果的列表集，包括以往扫描到的结果。
        }
        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            //扫描失败后的处理。
        }
    };
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            mBluetoothLeService.connect(mDeviceAddress);
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
            Log.e(TAG, "mBluetoothLeService is null");
        }
    };
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                //updateConnectionState(R.drawable.bluetooth_open);
                Log.e(TAG, "connected");
                mPieChart.setCenterText("正在监控");
                mPieChart.animateXY(500,500);
                mPieChart.invalidate();
                //updateConnectionState(true);
                //method2(file.getPath(),formatter.format(new Date(System.currentTimeMillis()))+" "+"connected"+"\n");
                bluetoothstate = true;
                //invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                //updateConnectionState(R.drawable.bluetooth_close);
                timer.cancel();
                timer = new Timer();
                Log.e(TAG, "disconnected");
                mPieChart.setCenterText("蓝牙断开");
                mPieChart.animateXY(500,500);
                mPieChart.invalidate();
                //method2(file.getPath(),formatter.format(new Date(System.currentTimeMillis()))+" "+"disconnected"+"\n");
                OverTime();
                bluetoothstate = false;
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                Bundle extras = intent.getExtras();
                String data = extras.getString(BluetoothLeService.EXTRA_DATA);
//                mPieChart.animateXY(500,500);
//                double a = Integer.parseInt(data.substring(9,11)+data.substring(12,14),16)/100.0;
//                if(a>maxWendu)
//                    maxWendu = a;
//                mPieChart.setData(getPieData(""+maxWendu+"度","0分钟","0次"));
//                mPieChart.invalidate();
//
//                //method2(file.getPath(),formatter.format(new Date(System.currentTimeMillis()))+" "+data+"\n");
//                datasum=mgr.allCaseNum();
//                String   date = sDateFormat.format(new Date());
//                person1 = new Person(datasum+1,date.substring(0,8),date.substring(9,date.length()),a);
//                mgr.save(person1,"");
//                Log.w(TAG, "onReceive: "+(datasum+1)+" "+date.substring(0,8)+date.substring(9,date.length()));
//                //Toast.makeText(MainActivity.this, ""+(datasum+1)+" "+date.substring(0,10)+date.substring(11,date.length()), Toast.LENGTH_SHORT).show();
//                displayData(data);
//                if(data.length()>=22)
//                    period = Integer.parseInt(data.substring(18,20),16)*60+5;
//                biaoji = biaoji == 0 ? 1 : 0;
//                calTime();
                Log.d(TAG, "---Receive Data---"+data);
            }
        }

    };
    private void calTime() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {      // UI thread
                    @Override
                    public void run() {
                        if(--period>0){
                            //Log.e(TAG, period+"");
                            //Log.e(TAG, biaoji+""+jiou);
                            //Toast.makeText(DeviceControlActivity.this, ""+biaoji, Toast.LENGTH_SHORT).show();
                            if(biaoji==0){
                                Log.e(TAG, "取消重发");
                                //Toast.makeText(DeviceControlActivity.this, "取消重发", Toast.LENGTH_SHORT).show();
                                period =settingTime*60+5;
                                timer.cancel();
                                timer = new Timer();
                                calTime1();
                            }
                        }
                        else{
                            Log.e(TAG, "重发");
                            period =settingTime*60+5;
                            //biaoji = 0;
                            if(isEmpty(mBluetoothLeService))
                            {}
                                //Toast.makeText(MainActivity.this, "蓝牙已断开", Toast.LENGTH_SHORT).show();
                            else
                                writeGattServices(mBluetoothLeService.getSupportedGattServices());
                            timer.cancel();
                            timer = new Timer();
                        }
                    }
                });
            }
        },1000,1000);
    }
    private void calTime1() {
        biaoji =1;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {      // UI thread
                    @Override
                    public void run() {
                        if(--period>0){
                            if(biaoji==0){
                                Log.e(TAG, "取消重发");
                                //Toast.makeText(DeviceControlActivity.this, "取消重发", Toast.LENGTH_SHORT).show();
                                period =65;
                                timer.cancel();
                                timer = new Timer();
                                calTime1();
                            }
                        }
                        else{
                            Log.e(TAG, "重发");
                            period =65;
                            if(isEmpty(mBluetoothLeService))
                            {}
                                //Toast.makeText(MainActivity.this, "蓝牙已断开", Toast.LENGTH_SHORT).show();
                            else
                                writeGattServices(mBluetoothLeService.getSupportedGattServices());
                            timer.cancel();
                            timer = new Timer();
                        }
                    }
                });
            }
        },1000,1000);
    }
    //追加文件：使用FileWriter
    public static void method2(String fileName, String content) {
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(fileName, true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //更新电池状态
    private void updateBatteryState(final boolean update,final int caseWhat) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateBattery(update,caseWhat);
            }
        });
    }
    //更新蓝牙连接的状态标识
    public void updateConnectionState(boolean biaoshi) {
//        bluetoothstate = biaoshi;
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
                if(biaoshi)
                {
                    lanyazhishi.setImageResource(R.drawable.bluetooth_open);
                    //bluetoothState.setText(R.string.connected);
                }
                else {
                    //mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    //bluetoothState.setText(R.string.disconnected);
                    lanyazhishi.setImageResource(R.drawable.bluetooth_close);
                    //bluetoothdis.setImageResource(R.drawable.bluetooth_close);
//                    if (bindor) {
//                        Log.e(TAG,"解绑服务");
//                        bindor = false;
//                        unbindService(mServiceConnection);
//                        mBluetoothLeService = null;
//                        mNotifyCharacteristic = null;
//                    }
//                    startBleScan();
//                    OverTime();
                    //mBluetoothAdapter.startLeScan(mLeScanCallback);
                }
//            }
//        });
    }
    private void OverTime() {
        overtimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {      // UI thread
                    @Override
                    public void run() {
                        if(--period1>0){
                            Log.e(TAG, ""+period1);
                            if(bluetoothstate){
                                Log.e(TAG, "取消断开预警");
                                overtimer.cancel();
                                period1 = 10;
                                overtimer = new Timer();
                            }
                        }
                        else{
                            Log.e(TAG, "断开预警");
                            AlertMethod1();
                            period1 = 3;
                            overtimer.cancel();
                            overtimer = new Timer();
                        }
                    }
                });
            }
        },1000,1000);
    }
    public void updateBattery(boolean update,int caseWhat){
        batteryPecentCase = caseWhat;
        if(update){
            switch(batteryPecentCase){
                case 4:
                    battery.setImageResource(R.drawable.battery_100c);
                    //batteryPercent.setText("100");
                    break;
                case 3:
                    battery.setImageResource(R.drawable.battery_100);
                    //batteryPercent.setText("75");
                    break;
                case 2:
                    battery.setImageResource(R.drawable.battery_60);
                    //batteryPercent.setText("50");
                    break;
                case 1:
                    battery.setImageResource(R.drawable.battery_20);
                    //batteryPercent.setText("25");
                    break;
            }
        }
    }
    //广播过滤
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
    @Override
    protected void onResume() {
        super.onResume();
        //updateConnectionState(false);
        //updateConnectionState(false);
        //new DrawThread().start();  //线程启动
        //registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        else{
            if(!bindor&&!times){
                startBleScan();
                times = true;
                //mBluetoothAdapter.startLeScan(mLeScanCallback);
            }
        }
//        if (mBluetoothLeService != null) {
//            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
//            Log.d(TAG, "Connect request result=" + result);
//        }
        Log.d(TAG, "---onResume()---");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        unregisterReceiver(mGattUpdateReceiver);
        //DeviceControlActivity.releaseWakeLock();
        if (bindor) {
            unbindService(mServiceConnection);
            mBluetoothLeService = null;
        }
        //mBluetoothAdapter.stopLeScan(mLeScanCallback);
        if (mBluetoothAdapter.isEnabled())
            mBluetoothAdapter.disable();
        Log.d(TAG, "---onDestroy()---");
    }
    //数据判断和显示
    private void displayData(String data) {
        //Toast.makeText(DeviceControlActivity.this, data, Toast.LENGTH_SHORT).show();
        if (data != null&&data.length()>=22) {
            //Toast.makeText(this, data+" "+data.length(), Toast.LENGTH_SHORT).show();
            String a = data.substring(9,11)+data.substring(12,14);
            String b = data.substring(15,17);
            if(!wdType){
            if(Integer.parseInt(a,16)/100.0>Double.parseDouble(thr))
                mPieChart.setCenterText(generateCeterSpannableText(String.valueOf(Integer.parseInt(a,16)/100.0)+"℃\n"+"偏高"));
            else
                mPieChart.setCenterText(generateCeterSpannableText(String.valueOf(Integer.parseInt(a,16)/100.0)+"℃\n"+"正常"));
            mPieChart.invalidate();
            }
            else
            {
                mPieChart.setCenterText(generateCeterSpannableText(String.valueOf(Integer.parseInt(a,16)*1.8/100.0+30)+"℉\n"+"正常"));
            }

            if(Integer.parseInt(b,16) != batterystate){
                batterystate = Integer.parseInt(b,16);
                updateBatteryState(true, Integer.parseInt(b,16));
            }
            if(Integer.parseInt(a,16)/100.0>Double.parseDouble(thr)&&!alertsign){
                //警报响起
                AlertMethod();
            }
        }
    }
    //蓝牙断开警报
    public void AlertMethod1() {
        alertsign = true;
        Intent intent = new Intent(MainActivity.this, MusicService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        okListener oklistener=new okListener();
        noListener nolistener =new noListener();
        Dialog alertDialog = new AlertDialog.Builder(MainActivity.this).setTitle("蓝牙断开警报")
                .setMessage("解除蓝牙断开警报预警？").setIcon(R.drawable.bluetooth_open)
                .setPositiveButton("确定", oklistener)
                .create();
        alertDialog.show();
    }
    //警报
    public void AlertMethod() {
        alertsign = true;
        Intent intent = new Intent(MainActivity.this, MusicService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        okListener oklistener=new okListener();
        noListener nolistener =new noListener();
        Dialog alertDialog = new AlertDialog.Builder(MainActivity.this).setTitle("温度警报")
                .setMessage("当前温度偏高，解除警报预警？").setIcon(R.drawable.alertlogo)
                .setPositiveButton("确定", oklistener)
                .setNegativeButton("取消", nolistener)
                .create();
        alertDialog.show();
//        try {
//            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
//            mAlert.setAccessible(true);
//            Object mAlertController = mAlert.get(alertDialog);
//            Field mMessage = mAlertController.getClass().getDeclaredField("mMessageView");
//            mMessage.setAccessible(true);
//            TextView mMessageView = (TextView) mMessage.get(mAlertController);
//            mMessageView.setTextColor(Color.RED);
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        }
    }
    //蓝牙服务
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        //修改部分
        //gattCharacteristic = gattServices.get(2).getCharacteristics().get(0);

        //gattCharacteristic = gattServices.get(2).getCharacteristics().get(0);
        gattCharacteristic = gattServices.get(2).getCharacteristics().get(0);
        Log.e(TAG,""+gattServices.get(2).getCharacteristics().get(0).getUuid());
        //get(2)
        //women  0000fee1-0000-1000-8000-00805f9b34fb
        //Toast.makeText(DeviceControlActivity.this, gattCharacteristic.getUuid().toString(), Toast.LENGTH_SHORT).show();
        //"0000ffe4-0000-1000-8000-00805f9b34fb"
        if(gattCharacteristic.getUuid().toString().equals("0000fec8-0000-1000-8000-00805f9b34fb"))//indicate
        {
            //mNotifyCharacteristic = gattCharacteristic;
            mBluetoothLeService.setCharacteristicNotification(gattCharacteristic, true);
//            BluetoothGattDescriptor descriptor = gattCharacteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));//获取修饰
//            descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);//修饰配置
//            gatt.writeDescriptor(descriptor);
        }

    }
    //数据请求重发
    private void writeGattServices(List<BluetoothGattService> gattServices){
        if (gattServices == null) {
            //Toast.makeText(this, "蓝牙服务还未初始化", Toast.LENGTH_SHORT).show();
        }
        else{
        final BluetoothGattCharacteristic gattCharacteristic1 = gattServices.get(2).getCharacteristics().get(0);
        gattCharacteristic2 = gattCharacteristic1;
        gattCharacteristic1.setValue(hexStringToBytes("aa0300fd"));
        mBluetoothLeService.wirteCharacteristic(gattCharacteristic1);
        }
        //Toast.makeText(DeviceControlActivity.this, "request data sended", Toast.LENGTH_SHORT).show();
    }
    //时间间隔设置
    private void writeGattServices(List<BluetoothGattService> gattServices, int value){
        if (gattServices == null)
        {
           // Toast.makeText(this, "蓝牙服务还未初始化", Toast.LENGTH_SHORT).show();

        }else{
        String setting;
        if(value<10)
            setting = "aa0201"+"0"+value+"f"+ Integer.toHexString(13-value);
        else
            setting = "aa02010af3";
        final BluetoothGattCharacteristic gattCharacteristic2 = gattServices.get(2).getCharacteristics().get(0);
        gattCharacteristic2.setValue(hexStringToBytes(setting));
        mBluetoothLeService.wirteCharacteristic(gattCharacteristic2);
        period = value*60+5;
        Toast.makeText(MainActivity.this, "Timesetting sended", Toast.LENGTH_SHORT).show();
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK )
        {
            // 创建退出对话框
            AlertDialog isExit = new AlertDialog.Builder(this).create();
            // 设置对话框标题
            isExit.setTitle("系统提示");
            // 设置对话框消息
            isExit.setMessage("确定要退出吗?");
            // 添加选择按钮并注册监听
            isExit.setButton(Dialog.BUTTON_POSITIVE,"确定",listener);
            isExit.setButton(Dialog.BUTTON_NEGATIVE,"取消",listener);
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
                    finish();
                    break;
                case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                    break;
                default:
                    break;
            }
        }
    };
}
