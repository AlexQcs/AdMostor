package com.hc.admc.ui;

import com.hc.admc.bean.ProgramBean;
import com.hc.admc.request.ApiService;

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
    private static final String BASE_URL="";
    private Call<ProgramBean> mProgramBeanCall;

    public void requestProgram(String timestamp, String token, String signature, Callback<ProgramBean> callback){
        Retrofit retrofit=new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService=retrofit.create(ApiService.class);
        mProgramBeanCall=apiService.requestProgram(timestamp,token,signature);
        mProgramBeanCall.enqueue(callback);
    }

    public void interruptHttp(){
        if (mProgramBeanCall!=null&&!mProgramBeanCall.isCanceled()){
            mProgramBeanCall.cancel();
        }
    }
}
