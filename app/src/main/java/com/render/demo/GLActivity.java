package com.render.demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Random;

public class GLActivity extends Activity {

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

    private LinearLayout root;

    private RendedWidget rendedWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the activity to display the glSurfaceView
        setContentView(R.layout.activity_main);

        root = (LinearLayout) findViewById(R.id.root);

        initScreenSize();
        rendedWidget = new RendedWidget(this);
        final ImageView view = new ImageView(this);
        view.setImageResource(R.mipmap.ic_launcher);
        rendedWidget.addView(view, -1, -1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (true) {
                    final int tmp = i++;
                    SystemClock.sleep(1000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            view.setBackgroundColor(randomColor(tmp % 255));
                            view.setVisibility(tmp % 2 == 0 ? View.VISIBLE : View.INVISIBLE);
                        }
                    });
                    if (tmp == Integer.MAX_VALUE - 1) {
                        break;
                    }
                }
            }
        }).start();

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
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isAdded)
                    addWidgetView(rendedWidget);
            }
        }, 500);
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
        GLSurfaceView glSurfaceView = new GLSurfaceView(getApplicationContext());
        // Setup the surface view for drawing to

        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        glSurfaceView.setRenderer(renderer);
        //glSurfaceView.setZOrderOnTop(true);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 300);
        // Add our WebView to the Android View hierarchy
        glSurfaceView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        root.addView(mHostView, lp);
        root.addView(glSurfaceView);
        //addContentView(glProgressBar, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        isAdded = true;
    }


}
