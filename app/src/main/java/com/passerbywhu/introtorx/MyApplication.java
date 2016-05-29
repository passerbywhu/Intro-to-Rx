package com.passerbywhu.introtorx;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

/**
 * Created by passe on 2016/5/28.
 */
public class MyApplication extends Application {
    public static volatile MyApplication instance;
    private static Handler mHandler;

    public static MyApplication getInstance() {
        return instance;
    }

    public static void postRunnable(Runnable runnable) {
        if (mHandler != null) {
            mHandler.post(runnable);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mHandler = new Handler(Looper.getMainLooper());
    }
}
