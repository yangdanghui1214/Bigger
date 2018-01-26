package cn.tsign.datadisplay;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.don.pieviewlibrary.LinePieView;
import com.don.pieviewlibrary.PercentPieView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

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
    int[] areaRes = new int[]{
            R.string.china_anhui, R.string.china_beijing, R.string.china_guangdong
            , R.string.china_chongqing, R.string.china_xinjiang, R.string.china_fujian
            , R.string.china_gansu, R.string.china_zhejiang, R.string.china_yunnan
            , R.string.china_xizang, R.string.china_tianjin
            , R.string.china_shandong, R.string.china_heilongjiang, R.string.china_hainan
    };

    int[] areaRes2 = new int[]{
            R.string.china_anhui, R.string.china_beijing, R.string.china_guangdong
            , R.string.china_chongqing, R.string.china_shanxi, R.string.china_neimenggu
            , R.string.china_fujian, R.string.china_gansu, R.string.china_zhejiang
            , R.string.china_yunnan, R.string.china_liaoning, R.string.china_tianjin
            , R.string.china_shandong, R.string.china_heilongjiang, R.string.china_hainan
    };


    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//得到当前界面的装饰视图
        View decorView = getWindow().getDecorView();
//        SYSTEM_UI_FLAG_FULLSCREEN表示全屏的意思，也就是会将状态栏隐藏
        //设置系统UI元素的可见性
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        //隐藏标题栏
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        new TimeThread().start();//启动时钟线程

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
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
        binding.rdvDetect.setAreaColor(R.string.china_guangdong, Color.YELLOW, -1, -1);
        binding.rdvDetect.setAreaColor(R.string.china_anhui, Color.RED, -1, -1);
        binding.rdvDetect.setAreaColor(R.string.china_fujian, Color.RED, -1, -1);
        binding.rdvDetect.setAreaColor(R.string.china_shandong, Color.RED, -1, -1);
        binding.rdvDetect.setAreaColor(R.string.china_chongqing, Color.GREEN, -1, -1);
        binding.rdvDetect.setCenterIconVisibility(true);
        //binding.rdvDetect.setCenterIconLocationType(RegionDetectSurfaceView.CENTER_ICON_POSITION_CENTER);
        binding.rdvDetect.setDefaultNormalColor(0x8069BBA8);
        binding.rdvDetect.setDefaultActivateColor(0x802F8BBB);
        binding.rdvDetect.setDefaultHighlightColor(0x80BB945A);
        binding.rdvDetect.setBackgroundColor(0x00000000);

        binding.barCircle1.setProgress(70, true);

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
        List chartList = new ArrayList<>();

        Random random = new Random();
        while (chartList.size() < 36) {
            int randomInt = random.nextInt(80);
            chartList.add((float) randomInt);
        }
        binding.myChartView.setList(chartList);

        /*饼状图*/
        int[] data = new int[]{100, 200, 50};
        String[] name = new String[]{"单元", "机构", "用户"};
        int[] color = new int[]{
                getResources().getColor(R.color.chart_unit),
                getResources().getColor(R.color.chart_agent),
                getResources().getColor(R.color.chart_user),
        };

        //使用随机颜色
        binding.pieView.setData(data, name, color);
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
                binding.rdvDetect.setBackgroundColor(0xFF000000);
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
