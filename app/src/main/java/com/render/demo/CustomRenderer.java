package com.render.demo;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Debug;
import android.util.Log;
import android.view.Display;
import android.view.Surface;

import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

class CustomRenderer implements GLSurfaceView.Renderer {
    int glSurfaceTex;
    private final int GL_TEXTURE_EXTERNAL_OES = 0x8D65;
    long currentTime;
    long previousTime;
    boolean b = false;
    int frameCount = 0;
    DirectDrawer mDirectDrawer;
    ActivityManager activityManager;
    MemoryInfo _memoryInfo;


    // Fixed values
    private int TEXTURE_WIDTH = 1280;
    private int TEXTURE_HEIGHT = 720;

    Context context;

    private RendedWidget rendedView;

    private SurfaceTexture surfaceTexture = null;

    private Surface surface;

    float fps;

    public CustomRenderer(Context context, RendedWidget rendedView, Display mDisplay) {
        this.context = context;
        this.rendedView = rendedView;
        TEXTURE_WIDTH = rendedView.getWidth();
        TEXTURE_HEIGHT = rendedView.getHeight();
        _memoryInfo = new MemoryInfo();
        activityManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        synchronized (this) {
            surfaceTexture.updateTexImage();
        }

        activityManager.getMemoryInfo(_memoryInfo);
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        float[] mtx = new float[16];
//        TEXTURE_WIDTH = rendedView.getWidth();
//        TEXTURE_HEIGHT = rendedView.getHeight();
//        surfaceTexture.setDefaultBufferSize(TEXTURE_WIDTH,TEXTURE_HEIGHT);
        surfaceTexture.getTransformMatrix(mtx);
        mDirectDrawer.draw(mtx);

        calculateFps();
//		getAppMemorySize();
//		getRunningAppProcessInfo();
//		Log.v("onDrawFrame", "FPS: " + Math.round(fps) + ", availMem: " + Math.round(_memoryInfo.availMem / 1048576) + "MB");
    }

    private void getAppMemorySize() {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        android.os.Debug.MemoryInfo[] memoryInfos = mActivityManager.getProcessMemoryInfo(new int[]{android.os.Process.myPid()});
        int size = memoryInfos[0].dalvikPrivateDirty;
        Log.w("getAppMemorySize", size / 1024 + " MB");
    }


    private void getRunningAppProcessInfo() {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<RunningAppProcessInfo> runningAppProcessesList = mActivityManager.getRunningAppProcesses();

        for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcessesList) {
            int pid = runningAppProcessInfo.pid;
            int uid = runningAppProcessInfo.uid;
            String processName = runningAppProcessInfo.processName;
            int[] pids = new int[]{pid};
            Debug.MemoryInfo[] memoryInfo = mActivityManager.getProcessMemoryInfo(pids);
            int memorySize = memoryInfo[0].dalvikPrivateDirty;

            System.out.println("processName=" + processName + ",currentPid: " + "pid= " + android.os.Process.myPid() + "----------->" + pid + ",uid=" + uid + ",memorySize=" + memorySize + "kb");
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        surface = null;
        surfaceTexture = null;

        glSurfaceTex = Engine_CreateSurfaceTexture(TEXTURE_WIDTH, TEXTURE_HEIGHT);
        Log.d("GLES20Ext", "glSurfaceTex" + glSurfaceTex);
        if (glSurfaceTex > 0) {
            surfaceTexture = new SurfaceTexture(glSurfaceTex);
            surfaceTexture.setDefaultBufferSize(TEXTURE_WIDTH, TEXTURE_HEIGHT);
            surface = new Surface(surfaceTexture);
            rendedView.setSurface(surface);
            rendedView.setSurfaceTexture(surfaceTexture);
            //addedWidgetView.setSurfaceTexture(surfaceTexture);
            mDirectDrawer = new DirectDrawer(glSurfaceTex);
        }
    }

    float calculateFps() {

        frameCount++;
        if (!b) {
            b = true;
            previousTime = System.currentTimeMillis();
        }
        long intervalTime = System.currentTimeMillis() - previousTime;

        if (intervalTime >= 1000) {
            b = false;
            fps = frameCount / (intervalTime / 1000f);
            frameCount = 0;
            Log.w("calculateFps", "FPS: " + fps);
        }

        return fps;
    }

    int Engine_CreateSurfaceTexture(int width, int height) {
        /*
		 * Create our texture. This has to be done each time the surface is
		 * created.
		 */

        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);

        glSurfaceTex = textures[0];

        if (glSurfaceTex > 0) {
            GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, glSurfaceTex);

            // Notice the use of GL_TEXTURE_2D for texture creation
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, width, height, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, null);

            GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        }
        return glSurfaceTex;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }
}