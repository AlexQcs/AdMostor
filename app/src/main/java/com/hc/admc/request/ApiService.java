package com.hc.admc.request;

import com.hc.admc.bean.ProgramBean;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Alex on 2017/12/8.
 * 备注:请求服务器通讯操作
 */

public interface ApiService {
    /**
     * 作用:请求获取节目单信息
     *
     * @param timestamp
     *         时间戳
     * @param token
     *         约定的token
     * @param signature
     *         根据 时间戳 与 token 加密生成的签名
     * @return Call<ProgramBean>
     */
    @POST("4156")
    Call<ProgramBean> requestProgram(@Path("timestamp") String timestamp, @Path("token") String token, @Path("signature") String signature);
}
