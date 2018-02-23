package com.render.demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.alivc.player.AliVcMediaPlayer;
import com.render.demo.photo.PhotoViewPager;

import java.util.ArrayList;
import java.util.Random;

import tv.lycam.player.StandardPlayer;

public class GL2Activity extends Activity {
    String[] url = {"http://sbkt.oss-cn-shenzhen.aliyuncs.com/thumbnails/dev-8c4e5181-a43e-11e7-b0e2-cb9be4bc3d20/1514187164876.jpg",
            "http://sbkt.oss-cn-shenzhen.aliyuncs.com/thumbnails/dev-253e1511-a43f-11e7-aee5-25404b4da7c9/1514187074103.jpg",
            "http://sbkt.oss-cn-shenzhen.aliyuncs.com/thumbnails/dev-2ce41a01-ebaf-11e7-8f89-0562bbfed67e/1514452327228.jpg",
            "http://sbkt.oss-cn-shenzhen.aliyuncs.com/thumbnails/dev-4477cb81-9dc1-11e7-aee5-25404b4da7c9/1511347476196.jpg",
            "http://sbkt.oss-cn-shenzhen.aliyuncs.com/thumbnails/dev-039e3241-c362-11e7-8270-61eef27bc007/1510021139718.jpg",};
    public static final int APPWIDGET_HOST_ID = 1001;
    public static final int REQUEST_BIND_APPWIDGET = 1003;
    private static final int REQUEST_CREATE_SHORTCUT = 1;
    private static final int REQUEST_CREATE_APPWIDGET = 5;

    private int[] padding = new int[]{0, 0, 0};
    private ArrayList<Object> mWidgets;
    private BitmapDrawable previewDrawable;

    private int screenHeight;
    private int screenWidth;
    private int maxWidth;
    private int maxHeight;

    private ViewGroup root;

    private RendedWidget rendedWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AliVcMediaPlayer.init(getApplicationContext());
        // Set the activity to display the glSurfaceView
        setContentView(R.layout.activity_main2);

        root =  findViewById(R.id.root);

        initScreenSize();
        rendedWidget = findViewById(R.id.render);

        StandardPlayer mPlayer = findViewById(R.id.player_view);
//        mPlayer.setForbidAdjustBrightness(true);
//        mPlayer.setForbidAdjustVolumn(true);
//        mPlayer.setForbidChangePosition(true);
//        mPlayer.setAutoKeepScreen(false);
        mPlayer.setVideoPath("rtmp://live.hkstv.hk.lxdns.com/live/hks");
        mPlayer.start();
        mPlayer.getMediaPlayer().getSurfaceTexture();
        final PhotoViewPager view = findViewById(R.id.photo);
//        rendedWidget = new RendedWidget(this);
//        final PhotoViewPager view = new PhotoViewPager(this);
        view.initAdapter(url);
        rendedWidget.setBackgroundColor(Color.RED);
//        rendedWidget.addView(view, -1, -1);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                int i = 0;
//                while (true) {
//                    final int tmp = i++;
//                    SystemClock.sleep(1000);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            view.setBackgroundColor(randomColor(tmp % 255));
//                            view.setVisibility(tmp % 2 == 0 ? View.VISIBLE : View.INVISIBLE);
//                        }
//                    });
//                    if (tmp == Integer.MAX_VALUE - 1) {
//                        break;
//                    }
//                }
//            }
//        }).start();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
//        root.addView(rendedWidget, lp);

        rendedWidget.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (rendedWidget.getWidth() != 0) {
                    if (!isAdded)
                        addWidgetView(rendedWidget);
                    rendedWidget.getViewTreeObserver().removeOnPreDrawListener(this);
                }
                return true;
            }
        });

    }

    static int randomColor(int alpha) {
        Random rnd = new Random();
        alpha = Math.min(Math.max(1, alpha), 255);
        return Color.argb(alpha, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    private void initScreenSize() {
        Display screenSize = getWindowManager().getDefaultDisplay();
        screenWidth = screenSize.getWidth();
        screenHeight = screenSize.getHeight();
        maxWidth = maxHeight = screenWidth >= screenHeight ? screenHeight >> 1 : screenWidth >> 1;
    }

    private boolean isAdded = false;

    @Override
    protected void onResume() {
        super.onResume();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (!isAdded)
//                    addWidgetView(rendedWidget);
//            }
//        }, 500);
    }

    public final static int FINSISH_LOAD_WIDGET = 1;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FINSISH_LOAD_WIDGET:
                    break;
            }
        }
    };

    /**
     * @param mHostView
     */
    @SuppressLint("NewApi")
    private void addWidgetView(final RendedWidget mHostView) {

        Display mDisplay = getWindowManager().getDefaultDisplay();


        CustomRenderer renderer = new CustomRenderer(getApplicationContext(), mHostView, mDisplay);

//        GLSurfaceView glSurfaceView = new GLSurfaceView(getApplicationContext());
        GLSurfaceView glSurfaceView = findViewById(R.id.surface);
        // Setup the surface view for drawing to

        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        glSurfaceView.setRenderer(renderer);
        //glSurfaceView.setZOrderOnTop(true);
        // Add our WebView to the Android View hierarchy
//        glSurfaceView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
//        root.addView(glSurfaceView);
        //addContentView(glProgressBar, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        isAdded = true;

    }


}
