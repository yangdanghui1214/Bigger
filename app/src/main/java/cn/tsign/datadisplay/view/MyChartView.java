package cn.tsign.datadisplay.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


import java.util.ArrayList;
import java.util.List;

import cn.tsign.datadisplay.R;


/**
 * Created by tangyangkai on 2016/12/2.
 */

public class MyChartView extends View {

    private int leftColor;//双柱左侧
    private int rightColor;//双柱右侧
    private int rightColor3;//三柱右侧
    private int lineColor;//横轴线
    private int selectLeftColor;//点击选中左侧
    private int selectRightColor;//点击选中右侧
    private int selectRightColor3;//点击选中3侧
    private int lefrColorBottom;//左侧底部
    private int rightColorBottom;//右侧底部
    private int rightColorBottom3;//3侧底部
    private Paint mPaint, mChartPaint, mShadowPaint;//横轴画笔、柱状图画笔、阴影画笔
    private int mWidth, mHeight, mStartWidth, mChartWidth, mSize;//屏幕宽度高度、柱状图起始位置、柱状图宽度
    private Rect mBound;
    private List<Float> list = new ArrayList<>();//柱状图高度占比
    private Rect rect;//柱状图矩形
    private getNumberListener listener;//点击接口
    private int number = 1000;//柱状图最大值
    private int selectIndex = -1;//点击选中柱状图索引
    private List<Integer> selectIndexRoles = new ArrayList<>();

    public void setList(List<Float> list) {
        this.list = list;
        mSize = getWidth() / 36;
        mStartWidth = getWidth() / 18;
        mChartWidth = getWidth() / 18 - mSize - 3;
        invalidate();
    }

    public void setListener(getNumberListener listener) {
        this.listener = listener;
    }

    public MyChartView(Context context) {
        this(context, null);
    }

    public MyChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取我们自定义的样式属性
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MyChartView, defStyleAttr, 0);
        int n = array.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = array.getIndex(i);
            switch (attr) {
                case R.styleable.MyChartView_leftColorBottom:
                    lefrColorBottom = array.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.MyChartView_leftColor:
                    // 默认颜色设置为黑色
                    leftColor = array.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.MyChartView_selectLeftColor:
                    // 默认颜色设置为黑色
                    selectLeftColor = array.getColor(attr, Color.BLACK);
                    break;

                case R.styleable.MyChartView_xyColor:
                    lineColor = array.getColor(attr, Color.BLACK);
                    break;

                case R.styleable.MyChartView_rightColorBottom:
                    rightColorBottom = array.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.MyChartView_rightColor:
                    rightColor = array.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.MyChartView_selectRightColor:
                    selectRightColor = array.getColor(attr, Color.BLACK);
                    break;

                case R.styleable.MyChartView_rightColorBottom3:
                    rightColorBottom3 = array.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.MyChartView_rightColor3:
                    rightColor3 = array.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.MyChartView_selectRightColor3:
                    // 默认颜色设置为黑色
                    selectRightColor3 = array.getColor(attr, Color.BLACK);
                    break;
            }
        }
        array.recycle();
        init();
    }

    //初始化画笔
    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mBound = new Rect();
        mChartPaint = new Paint();
        mChartPaint.setAntiAlias(true);
        mShadowPaint = new Paint();
        mShadowPaint.setAntiAlias(true);
        mShadowPaint.setColor(Color.WHITE);
    }

    //测量高宽度
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width;
        int height;
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = widthSize * 1 / 3;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = heightSize * 1 / 3;
        }

        setMeasuredDimension(width, height);
    }

    //计算高度宽度
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getWidth();
        mHeight = getHeight();
        mStartWidth = getWidth() / 18;
        mSize = getWidth() / 36;
        mChartWidth = getWidth() / 18 - mSize;
    }

    //重写onDraw绘制
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setColor(lineColor);
        //画坐标轴
        canvas.drawLine(0, mHeight - 80, mWidth, mHeight - 80, mPaint);
        mStartWidth += 9;
        for (int i = 0; i < 12; i++) {
            //画刻度线
            canvas.drawLine(mStartWidth, mHeight - 80, mStartWidth, mHeight - 80, mPaint);
            //画数字
            mPaint.setTextSize(15);
            mPaint.setTextAlign(Paint.Align.CENTER);
            mPaint.getTextBounds(String.valueOf(i + 1) + "", 0, String.valueOf(i).length(), mBound);
            canvas.drawText(String.valueOf(i + 1) + "月", mStartWidth - mBound.width() * 1 / 3,
                    mHeight - 60 + mBound.height() * 1 / 3, mPaint);
            mStartWidth += getWidth() / 7;
        }
        //画柱状图
        for (int i = 0; i < list.size(); i++) {
            int size = mHeight / 120;
            if (selectIndexRoles.contains(i)) {
                //偶数
                mChartPaint.setShader(null);
                switch (i % 3) {
                    case 0:
                        mChartPaint.setColor(selectLeftColor);
                        break;
                    case 1:
                        mChartPaint.setColor(selectRightColor);
                        break;
                    case 2:
                        mChartPaint.setColor(selectRightColor3);
                        break;
                }
            } else {
                LinearGradient lg;
                switch (i % 3) {
                    case 0:
                        lg = new LinearGradient(mChartWidth, mChartWidth + mSize, mHeight - 80,
                                (float) (mHeight - 80 - list.get(i) * size), lefrColorBottom, leftColor, Shader.TileMode.MIRROR);
                        mChartPaint.setShader(lg);
                        break;
                    case 1:
                        lg = new LinearGradient(mChartWidth, mChartWidth + mSize, mHeight - 80,
                                (float) (mHeight - 80 - list.get(i) * size), rightColorBottom, rightColor, Shader.TileMode.MIRROR);
                        mChartPaint.setShader(lg);
                        break;
                    case 2:
                        lg = new LinearGradient(mChartWidth, mChartWidth + mSize, mHeight - 80,
                                (float) (mHeight - 80 - list.get(i) * size), rightColorBottom3, rightColor3, Shader.TileMode.MIRROR);
                        mChartPaint.setShader(lg);
                        break;
                }
            }

            mChartPaint.setStyle(Paint.Style.FILL);
            //画阴影
            if (i == number * 3 || i == number * 3 + 1) {
                mShadowPaint.setColor(Color.BLUE);
            } else {
                mShadowPaint.setColor(Color.WHITE);
            }

            //画柱状图
            RectF rectF = new RectF();
            rectF.left = mChartWidth;
            rectF.right = mChartWidth + mSize;
            rectF.bottom = mHeight - 80;
            rectF.top = (float) (mHeight - 80 - list.get(i) * size);
            canvas.drawRoundRect(rectF, 5, 3, mChartPaint);
//            canvas.drawRect(mChartWidth, mHeight - 100 - list.get(i) * size, mChartWidth + mSize, mHeight - 100, mChartPaint);
            switch (i % 3) {
                case 0:
                    mChartWidth += (3 + getWidth() / 36);
                    break;
                case 1:
                    mChartWidth += (3 + getWidth() / 36);
                    break;
                case 2:
                    mChartWidth += (getWidth() / 10 - mSize);
                    break;
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {

        }
    }

    /**
     * 注意:
     * 当屏幕焦点变化时重新侧向起始位置,必须重写次方法,否则当焦点变化时柱状图会跑到屏幕外面
     */

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            mSize = getWidth() / 78;
            mStartWidth = getWidth() / 12;
            mChartWidth = getWidth() / 18 - mSize ;
        }
    }

    /**
     * 柱状图touch事件
     * 获取触摸位置计算属于哪个月份的
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {

//        int x = (int) ev.getX();
//        int y = (int) ev.getY();
//        int left = 0;
//        int top = 0;
//        int right = mWidth / 36;
//        int bottom = mHeight - 80;
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                for (int i = 0; i < 12; i++) {
//                    rect = new Rect(left, top, right, bottom);
//                    left += mWidth / 12;
//                    right += mWidth / 12;
//                    if (rect.contains(x, y)) {
//                        listener.getNumber(i, x, y);
//                        number = i;
//                        selectIndex = i;
//                        selectIndexRoles.clear();
//                        ;
//                        selectIndexRoles.add(selectIndex * 3 + 1);
//                        selectIndexRoles.add(selectIndex * 3);
//                        invalidate();
//                    }
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//
//                break;
//        }
        return true;
    }

    public interface getNumberListener {
        void getNumber(int number, int x, int y);
    }

    public int getRightColor3() {
        return rightColor3;
    }

    public void setRightColor3(int rightColor3) {
        this.rightColor3 = rightColor3;
    }

    public int getSelectRightColor3() {
        return selectRightColor3;
    }

    public void setSelectRightColor3(int selectRightColor3) {
        this.selectRightColor3 = selectRightColor3;
    }

    public int getRightColorBottom3() {
        return rightColorBottom3;
    }

    public void setRightColorBottom3(int rightColorBottom3) {
        this.rightColorBottom3 = rightColorBottom3;
    }

    public int getLeftColor() {
        return leftColor;
    }

    public void setLeftColor(int leftColor) {
        this.leftColor = leftColor;
    }

    public int getRightColor() {
        return rightColor;
    }

    public void setRightColor(int rightColor) {
        this.rightColor = rightColor;
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public int getSelectLeftColor() {
        return selectLeftColor;
    }

    public void setSelectLeftColor(int selectLeftColor) {
        this.selectLeftColor = selectLeftColor;
    }

    public int getSelectRightColor() {
        return selectRightColor;
    }

    public void setSelectRightColor(int selectRightColor) {
        this.selectRightColor = selectRightColor;
    }

    public int getLefrColorBottom() {
        return lefrColorBottom;
    }

    public void setLefrColorBottom(int lefrColorBottom) {
        this.lefrColorBottom = lefrColorBottom;
    }

    public int getRightColorBottom() {
        return rightColorBottom;
    }

    public void setRightColorBottom(int rightColorBottom) {
        this.rightColorBottom = rightColorBottom;
    }
}