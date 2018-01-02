package com.hc.admc.application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.squareup.leakcanary.LeakCanary;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;

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
        PushAgent mPushAgent = PushAgent.getInstance(this);
//注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                Log.e("umengtoken", deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {

            }
        });
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
