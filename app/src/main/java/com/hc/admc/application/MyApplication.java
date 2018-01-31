package com.hc.admc.application;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by Alex on 2017/12/7.
 * 备注:
 */

public class MyApplication extends Application {

    private static MyApplication instance;

    public static MyApplication getInstance() {
        return instance;
    }

    public volatile boolean isProgramPlay;

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
        instance = this;
        mContext=this;
    }

    public boolean isProgramPlay() {
        return isProgramPlay;
    }

    public void setProgramPlay(boolean programPlay) {
        isProgramPlay = programPlay;
    }

    public static Context getContext() {
        return mContext;
    }
}
