package com.zhang.floatwindow;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by zhanghui on 2019/3/29.
 */

public class FloatService extends Service {
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    public int statusHeight;
    public int daohangHeight;
    public int screenWidth;
    public int screenHeight;
    public int middleX;
    private LinearLayout linearLayout;
    private ImageView imageView;
    private GestureDetectorCompat mDetector;
    private OnGestureListener onGestureListener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        statusHeight = utils.getStatusBarHeight(this);
        daohangHeight = utils.getDaoHangHeight(this);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        middleX = screenWidth / 2;
        onGestureListener = new OnGestureListener();
        mDetector = new GestureDetectorCompat(this, onGestureListener);
        CreateFloatview();

    }

    /**
     * 创建悬浮窗
     */
    public void CreateFloatview() {
        layoutParams = new WindowManager.LayoutParams();
        windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        //如果是系统提示窗口，显示在应用程序之上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        //设置图片格式，效果为背景透明
        layoutParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //调整悬浮窗显示的停靠位置为左侧置顶
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
// 以屏幕左上角为原点，设置x、y初始值，相对于gravity(第一次默认在右下角)
        if (utils.getStringFromMainSP(this, CommParams.SP_FLOAT_BTN_FIRST).equals("first")) {
            layoutParams.x = screenWidth;
            layoutParams.y = (int) (screenHeight / 1.25);
            utils.saveStringToMainSP(this, CommParams.SP_FLOAT_BTN_FIRST, "notFirst");
        } else {
            layoutParams.x = utils.getIntFromMainSP(this, "x");
            layoutParams.y = utils.getIntFromMainSP(this, "y");
        }
        //设置悬浮窗口长宽数据
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        linearLayout = (LinearLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.service_floatwindow, null);
        windowManager.addView(linearLayout, layoutParams);
        imageView = (ImageView) linearLayout.findViewById(R.id.service_float);
        linearLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (layoutParams.x < middleX) {
                        layoutParams.x = 0;
                    } else {
                        layoutParams.x = screenWidth;
                    }
                    utils.saveIntToMainSP(getApplication(), "x", layoutParams.x);
                    utils.saveIntToMainSP(getApplication(), "y", layoutParams.y);
                    //刷新
                    windowManager.updateViewLayout(linearLayout, layoutParams);
                }
                return mDetector.onTouchEvent(motionEvent);//将事件交给手势处理
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //可以设置点两次关闭
//                stopSelf();
            }
        });

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (linearLayout!=null){
            windowManager.removeView(linearLayout);
        }
    }

    public class OnGestureListener implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return true;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            Toast.makeText(getApplicationContext(),"别点我",Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            layoutParams.x = (int) motionEvent1.getRawX() - imageView.getMeasuredWidth() / 2;
            //减为状态栏的高度
            layoutParams.y = (int) motionEvent1.getRawY() - imageView.getMeasuredHeight() / 2 - statusHeight;
            //减去导航栏跟状态栏高度
//            layoutParams.y = (int) motionEvent1.getRawY() - imageView.getMeasuredHeight() / 2 - statusHeight - daohangHeight;
            //刷新
            windowManager.updateViewLayout(linearLayout, layoutParams);
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return true;
        }
    }


}
