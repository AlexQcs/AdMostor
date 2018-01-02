package com.hc.admc.ui;

import android.util.Log;

import com.hc.admc.bean.program.ProgramBean;
import com.hc.admc.bean.program.RegistBean;
import com.hc.admc.request.Api;
import com.hc.admc.request.ApiService;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Alex on 2017/12/8.
 * 备注:
 */

public class MainAtyMode {

    private Call<ProgramBean> mProgramBeanCall;
    private Call<ResponseBody> mResponseBodyCall;
    private Call<RegistBean> mRegistBeanCall;
    private static OkHttpClient client;

    public void regist(String signature, String timestamp, String token, String deviceId, String deviceToken, Callback<RegistBean> callback) {

//        String time_s=System.currentTimeMillis()+"";
//        String token= Constant.TOKEN;
        client = new OkHttpClient();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.e("regist",message);
            }
        });
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        client = new OkHttpClient.Builder().addInterceptor(logging).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        ApiService apiService = retrofit.create(ApiService.class);

        mRegistBeanCall = apiService.registToService(signature, timestamp, token, deviceId, deviceToken);
        mRegistBeanCall.enqueue(callback);
    }

    public void requestProgram(String url, String signature, String timestamp, String token, String taskId, Callback<ProgramBean> callback) {
        client = new OkHttpClient();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.e("requestProgram",message);
            }
        });
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        client = new OkHttpClient.Builder().addInterceptor(logging).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        mProgramBeanCall = apiService.requestProgram(url, signature, timestamp, token, taskId);
        mProgramBeanCall.enqueue(callback);
    }

    public void syncTime(Callback<ResponseBody> callback) {
        client = new OkHttpClient();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.e("syncTime",message);
            }
        });
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        client = new OkHttpClient.Builder().addInterceptor(logging).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        mResponseBodyCall = apiService.sysncTime();
        mResponseBodyCall.enqueue(callback);
    }

    public void interruptHttp() {
        if (mProgramBeanCall != null && !mProgramBeanCall.isCanceled()) {
            mProgramBeanCall.cancel();
        }
    }


}
