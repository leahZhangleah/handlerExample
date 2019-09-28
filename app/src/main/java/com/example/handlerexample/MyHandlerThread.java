package com.example.handlerexample;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import android.util.Log;

public class MyHandlerThread extends HandlerThread {
    private static final String TAG = "MyHandlerThread";
    private Handler handler;

    public MyHandlerThread() {
        super(TAG, Process.THREAD_PRIORITY_BACKGROUND);

    }

    /*
    just like what is mentioned in mainactivity, if we instantiate a class object directly(like Handler,Runnable etc.)
    These objects will have a reference to its outer class
    Here since our handler will as long as our thread, so it's ok to instantiate the handler like this, and suppress the warning
     */

    @SuppressLint("HandlerLeak")
    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        //we can also create a custom handler class and override handlemsg there
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };
    }

    public Handler getHandler() {
        return handler;
    }

    /*
    @Override
    public void run() {
        super.run();
        for (int i = 0; i <5 ; i++) {
            Log.d(TAG, "handler thread run: "+i);
            SystemClock.sleep(1000);
        }
    }*/
}
