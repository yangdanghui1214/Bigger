package cn.tsign.datadisplay;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import cn.tsign.datadisplay.databinding.ActivityMainBinding;
import cn.tsign.datadisplay.lib.region.utils.CommonUtil;
import cn.tsign.datadisplay.lib.region.widget.RegionDetectSurfaceView;

import static cn.tsign.datadisplay.lib.region.widget.RegionDetectSurfaceView.SCALE_ZOOMIN;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "RegionDetectSViewAty";
    /**
     * 时钟
     */
    private static final int msgKey1 = 1;
    private static String mYear;
    private static String mMonth;
    private static String mDay;
    /*设置可点击区域*/
    int[] areaRes = new int[]{
            R.string.china_jilin, R.string.china_jiangsu,
            R.string.china_zhejiang, R.string.china_jiangxi,
            R.string.china_henan, R.string.china_hubei,
            R.string.china_hunan, R.string.china_guizhou,
            R.string.china_hainan, R.string.china_shaanxi,
            R.string.china_sichuan, R.string.china_yunnan,
            R.string.china_gansu, R.string.china_qinghai,
            R.string.china_taiwan, R.string.china_neimenggu,
            R.string.china_guangxi,
            R.string.china_ningxia, R.string.china_xianggang,
            R.string.china_shanxi, R.string.china_hebei,
            R.string.china_shanghai, R.string.china_beijing,
            R.string.china_tianjin, R.string.china_anhui,
            R.string.china_fujian, R.string.china_shandong,
            R.string.china_chongqing, R.string.china_heilongjiang,
            R.string.china_xinjiang, R.string.china_xizang,
            R.string.china_hainan,R.string.china_guangdong,
            R.string.china_liaoning

    };

    int[] areaRes2 = new int[]{
            R.string.china_anhui, R.string.china_beijing, R.string.china_guangdong
            , R.string.china_chongqing, R.string.china_shanxi, R.string.china_neimenggu
            , R.string.china_fujian, R.string.china_gansu, R.string.china_zhejiang
            , R.string.china_yunnan, R.string.china_liaoning, R.string.china_tianjin
            , R.string.china_shandong, R.string.china_heilongjiang, R.string.china_hainan
    };


    ActivityMainBinding binding;

    Random random = new Random();
    /*饼状图*/
    String[] name = new String[]{"单元", "机构", "用户"};
    List<int[]> listBing = new ArrayList<>();
    int[] color;

    int size=21216;
    List<int[]> listLinear = new ArrayList<>();
    private final Timer timer = new Timer();
    private TimerTask task;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 想要执行的事件
            super.handleMessage(msg);
            a += 1;
            if (a < areaRes.length) {
                /*地图*/
                binding.rdvDetect.setSelectedAreaOnlyCloseCenterLocation(areaRes[a]);
                /*饼状图*/
                binding.pieView.setData(listBing.get(a), name, color);
                /*线性进度条*/
                binding.mainBar1.setProgress(listLinear.get(a)[0]);
                binding.mainBar2.setProgress(listLinear.get(a)[1]);
                binding.mainBar3.setProgress(listLinear.get(a)[2]);
                binding.tvBaySize.setText((size/100)*listLinear.get(a)[0]+"");
                binding.tvMonthSize.setText((size/100)*listLinear.get(a)[1]+"");
                binding.tvSize.setText(size+"");
                /*圆形进度条*/
                binding.barCircle1.setProgress(listLinear.get(a)[0], true);
                binding.barCircle2.setProgress(listLinear.get(a)[1], true);
                binding.barCircle3.setProgress(listLinear.get(a)[2], true);
                size+=random.nextInt(10);
            } else {
                System.gc();
                a = 0;
            }
        }
    };
    int a = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //得到当前界面的装饰视图
        View decorView = getWindow().getDecorView();
        //设置系统UI元素的可见性
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        //隐藏标题栏
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        new TimeThread().start();//启动时钟线程

        binding.textYear.setText(StringYear());
        binding.rdvDetect.setOnActivateRegionDetectListener(new RegionDetectSurfaceView.OnActivateRegionDetectListener() {
            @Override
            public void onActivateRegionDetect(String name) {
                binding.tvActivate.setText("高亮区域：" + name);
                binding.rdvDetect.setSelectedAreaOnlyCloseCenterLocation(name);
            }
        });
        binding.rdvDetect.setOnRegionDetectListener(new RegionDetectSurfaceView.OnRegionDetectListener() {
            @Override
            public void onRegionDetect(String name) {
                String[] arr = getResources().getStringArray(R.array.china_shandong_list);
                if (name.equals(arr[0])) {
                    StringBuffer sb = new StringBuffer();
                    for (String s : arr) {
                        sb.append(s).append("\n");
                    }
                    binding.tvDetect.setText("当前区域：" + sb.toString());
                } else {
                    binding.tvDetect.setText("当前区域：" + name);
                }

            }
        });

        binding.rdvDetect.setOnDoubleClickListener(new RegionDetectSurfaceView.OnDoubleClickListener() {
            @Override
            public void onDoubleClick(@RegionDetectSurfaceView.ScaleMode int scaleMode) {
                binding.tvZoom.setText("双击操作:" + (scaleMode == SCALE_ZOOMIN ? "放大" : "缩小"));
            }
        });

        //binding.rdvDetect.setCenterIcon(CommonUtil.getBitmapFromVectorDrawable(this,R.mipmap.ic_launcher));
        //binding.rdvDetect.setAllAreaActivateStatus(true);
        binding.rdvDetect.setAreaActivateStatus(areaRes, true);
        binding.rdvDetect.setRegionDetectMode(RegionDetectSurfaceView.REGION_DETECT_MODE_CLICK);
        binding.rdvDetect.isOpenCenterLocation(false);

        binding.rdvDetect.setAreaColor(R.string.china_jilin, -1, -1, -1);
        binding.rdvDetect.setAreaColor(R.string.china_jiangsu, -1, -1, -1);
        binding.rdvDetect.setAreaColor(R.string.china_zhejiang, -1, -1, -1);
        binding.rdvDetect.setAreaColor(R.string.china_jiangxi, -1, -1, -1);
        binding.rdvDetect.setAreaColor(R.string.china_henan, -1, -1, -1);
        binding.rdvDetect.setAreaColor(R.string.china_hubei, -1, -1, -1);
        binding.rdvDetect.setAreaColor(R.string.china_hunan, -1, -1, -1);
        binding.rdvDetect.setAreaColor(R.string.china_guizhou, -1, -1, -1);
        binding.rdvDetect.setAreaColor(R.string.china_hainan, -1, -1, -1);
        binding.rdvDetect.setAreaColor(R.string.china_sichuan, -1, -1, -1);
        binding.rdvDetect.setAreaColor(R.string.china_yunnan, -1, -1, -1);
        binding.rdvDetect.setAreaColor(R.string.china_shaanxi, -1, -1, -1);
        binding.rdvDetect.setAreaColor(R.string.china_gansu, -1, -1, -1);
        binding.rdvDetect.setAreaColor(R.string.china_qinghai, -1, -1, -1);
        binding.rdvDetect.setAreaColor(R.string.china_taiwan, -1, -1, -1);
        binding.rdvDetect.setAreaColor(R.string.china_neimenggu, -1, -1, -1);
        binding.rdvDetect.setAreaColor(R.string.china_guangxi, -1, -1, -1);
        binding.rdvDetect.setAreaColor(R.string.china_ningxia, -1, -1, -1);
        binding.rdvDetect.setAreaColor(R.string.china_xianggang, -1, -1, -1);
        binding.rdvDetect.setAreaColor(R.string.china_liaoning, -1, -1, -1);
        binding.rdvDetect.setAreaColor(R.string.china_shanxi, -1, -1, -1);
        binding.rdvDetect.setAreaColor(R.string.china_hebei, -1, -1, -1);
        binding.rdvDetect.setAreaColor(R.string.china_guangdong, -1, -1, -1);
        binding.rdvDetect.setAreaColor(R.string.china_shanghai, -1, -1, -1);
        binding.rdvDetect.setAreaColor(R.string.china_beijing, -1, -1, -1);
        binding.rdvDetect.setAreaColor(R.string.china_tianjin, -1, -1, -1);
        binding.rdvDetect.setAreaColor(R.string.china_anhui, -1, -1, -1);
        binding.rdvDetect.setAreaColor(R.string.china_fujian, -1, -1, -1);
        binding.rdvDetect.setAreaColor(R.string.china_shandong, -1, -1, -1);
        binding.rdvDetect.setAreaColor(R.string.china_chongqing, -1, -1, -1);
        binding.rdvDetect.setCenterIconVisibility(true);
        //binding.rdvDetect.setCenterIconLocationType(RegionDetectSurfaceView.CENTER_ICON_POSITION_CENTER);
        binding.rdvDetect.setDefaultNormalColor(0x305ffbf8);
        binding.rdvDetect.setDefaultActivateColor(0x305ffbf8);
        binding.rdvDetect.setDefaultHighlightColor(0x905ffbf8);

        /*柱状图*/
        binding.myChartView.setLeftColor(getResources().getColor(R.color.leftColor));
        binding.myChartView.setLefrColorBottom(getResources().getColor(R.color.leftColorBottom));
        binding.myChartView.setSelectLeftColor(getResources().getColor(R.color.selectLeftColor));
        binding.myChartView.setRightColor(getResources().getColor(R.color.rightColor));
        binding.myChartView.setRightColorBottom(getResources().getColor(R.color.rightBottomColor));
        binding.myChartView.setSelectRightColor(getResources().getColor(R.color.selectRightColor));
        binding.myChartView.setRightColor3(getResources().getColor(R.color.rightColor3));
        binding.myChartView.setRightColorBottom3(getResources().getColor(R.color.rightBottomColor3));
        binding.myChartView.setSelectRightColor3(getResources().getColor(R.color.selectRightColor3));
        binding.myChartView.setLineColor(getResources().getColor(R.color.xyColor));

        //柱状图
        List chartList = new ArrayList<>();
        while (chartList.size() < 36) {
            int randomInt = chartList.size() * 5 + random.nextInt(8);
            chartList.add((float) randomInt);
        }
        binding.myChartView.setList(chartList);

        for (int i = 0; i < 34; i++) {
            listBing.add(new int[]{100 * (i + 1), 200 * i, 50 * (i + 2)});
            listLinear.add(new int[]{random.nextInt(45) + 50, random.nextInt(45) + 50, random.nextInt(45) + 50});
        }
        /*饼状图*/
        color = new int[]{
                getResources().getColor(R.color.chart_unit),
                getResources().getColor(R.color.chart_agent),
                getResources().getColor(R.color.chart_user),
        };
        binding.pieView.setData(listBing.get(0), name, color);

        binding.mainBar1.setProgress(listLinear.get(0)[0]);
        binding.mainBar2.setProgress(listLinear.get(0)[1]);
        binding.mainBar3.setProgress(listLinear.get(0)[2]);
        binding.barCircle1.setProgress(listLinear.get(0)[0], true);
        binding.barCircle2.setProgress(listLinear.get(0)[1], true);
        binding.barCircle3.setProgress(listLinear.get(0)[2], true);
        task = new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        };
        timer.schedule(task, 1000, 10000);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.i(TAG, "onContextItemSelected: ");
        switch (id) {
            case R.id.m_fitcenter:
                binding.rdvDetect.fitCenter();
                break;
            case R.id.m_change_icon:
                binding.rdvDetect.setCenterIcon(CommonUtil.getBitmapFromVectorDrawable(this, R.mipmap.ic_launcher));
                break;
            case R.id.m_toggle:
                binding.rdvDetect.setAreaMap(R.drawable.ic_china);
                binding.rdvDetect.setAreaActivateStatus(areaRes2, true);
                binding.rdvDetect.setAreaColor(R.string.china_guangdong, Color.YELLOW, -1, -1);
                binding.rdvDetect.setDefaultNormalColor(0x8069BBA8);
                binding.rdvDetect.setDefaultActivateColor(0x802F8BBB);
                binding.rdvDetect.setDefaultHighlightColor(0x80BB945A);
                break;
            case R.id.m_mode_center:
                binding.rdvDetect.setRegionDetectMode(RegionDetectSurfaceView.REGION_DETECT_MODE_CENTER);
                break;
            case R.id.m_mode_click:
                binding.rdvDetect.setRegionDetectMode(RegionDetectSurfaceView.REGION_DETECT_MODE_CLICK);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 显示系统时间（秒针变化）
     */
    public class TimeThread extends Thread {
        @Override
        public void run() {
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = msgKey1;
                    mHandler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case msgKey1:
                    long sysTime = System.currentTimeMillis();
                    CharSequence sysTimeStr = DateFormat.format("HH:mm:ss", sysTime);
                    binding.textClock.setText(sysTimeStr);
                    break;

                default:
                    break;
            }
        }
    };

    /*日期*/
    public static String StringYear() {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
        mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        return mYear + "年" + mMonth + "月" + mDay + "日";
    }
}
