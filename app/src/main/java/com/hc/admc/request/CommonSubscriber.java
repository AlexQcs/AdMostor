package com.hc.admc.request;

import android.content.Context;
import android.util.Log;

/**
 * Created by Alex on 2017/12/21.
 * 备注:
 */

public abstract class CommonSubscriber<T> extends BaseSubcriber<T> {
    private Context mContext;
    public CommonSubscriber(Context context){
        this.mContext=context;
    }

    private static final String TAG="CommonSubscriber";

    @Override
    public void onStart() {
        if (!NetworkUtil.isNetworkAvailable(mContext)){
            Log.e(TAG,"网络不可用");
        }else {
            Log.e(TAG,"网络可用");
        }
    }

    @Override
    public void onError(ApiException e) {
        Log.e(TAG,"错误信息为"+"code:"+e.code+"  message"+e.message);
    }

    @Override
    public void onCompleted() {
        Log.e(TAG,"成功了");
    }
}
