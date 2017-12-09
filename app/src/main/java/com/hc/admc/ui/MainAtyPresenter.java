package com.hc.admc.ui;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.alex.mvp.presenter.BaseMvpPresenter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hc.admc.Constant;
import com.hc.admc.bean.ProgramBean;
import com.hc.admc.bean.ScreenItemBean;
import com.hc.admc.util.StringUtils;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.os.Looper.getMainLooper;

/**
 * Created by Alex on 2017/12/8.
 * 备注:
 */

public class MainAtyPresenter extends BaseMvpPresenter<MainView> {
    private final static String TAG = "MainAtyPresenter";
    private final MainAtyMode mMainAtyMode;

    public MainAtyPresenter() {
        this.mMainAtyMode = new MainAtyMode();
    }

    private Subscription mProgramSubscription;
    private Queue<ProgramBean.ProgramListBean> mProgramListBeens;

    /**
     * 作用:初始化友盟自定义消息
     *
     * @param context
     *         mainactivity的context
     */
    public void initUmeng(Context context) {
        PushAgent mPushAgent = PushAgent.getInstance(context);
        UmengMessageHandler messageHandler = new UmengMessageHandler() {
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        // 对于自定义消息，PushSDK默认只统计送达。若开发者需要统计点击和忽略，则需手动调用统计方法。
                        Log.i("友盟消息", msg.custom);
                        getMvpView().receiveUmengMsg(msg.custom);

                        Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
                    }
                });
            }
        };
        mPushAgent.setMessageHandler(messageHandler);
    }


    private void resolveProgramJson(String jsonStr) {
        Gson gson = new Gson();
        ProgramBean programBean = gson.fromJson(jsonStr, ProgramBean.class);

        if (programBean == null || programBean.getProgramList().size() == 0) {
            Log.e(TAG, "友盟消息节目单为空");
            return;
        } else {
            try {
                mProgramListBeens=new LinkedList<>();
                unsubscribeSub(mProgramSubscription);
                StringUtils.coverTxtToFile(jsonStr, Constant.LOCAL_PROGRAM_LIST_PATH);

            } catch (IOException e) {
                e.printStackTrace();
            }

            mProgramSubscription = Observable.interval(1, TimeUnit.SECONDS)
                    .onBackpressureDrop()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Long>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(Long aLong) {

                        }
                    });
        }


    }


    private void playProgram() {
        String programJson = StringUtils.getStringFromTxT(Constant.LOCAL_PROGRAM_LIST_PATH);
        if ("".equals(programJson)) {
            Log.e(TAG, "节目列表为空");
            return;
        }
        Gson gson = new Gson();
        ProgramBean programBean = gson.fromJson(programJson, ProgramBean.class);
        if (programBean == null || programBean.getProgramList().size() == 0) {
            Log.e(TAG, "友盟消息节目单为空");
            return;
        }
    }

    private void resolveScrItemJson(String jsonStr) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<ScreenItemBean>>() {
        }.getType();
        List<ScreenItemBean> screenItems = gson.fromJson(jsonStr, type);
        if (screenItems != null && screenItems.size() > 0) {
            getMvpView().layoutProgramView(screenItems);
        }

    }

    private void unsubscribeSub(Subscription subscription) {
        if (subscription != null && subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }


}
