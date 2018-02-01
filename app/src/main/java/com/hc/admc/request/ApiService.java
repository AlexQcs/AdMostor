package com.hc.admc.request;

import com.hc.admc.bean.program.ProgramBean;
import com.hc.admc.bean.program.PushBean;
import com.hc.admc.bean.program.RegistBean;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Alex on 2017/12/8.
 * 备注:请求服务器通讯操作
 */

public interface ApiService {


    /**
     * 作用:向服务器注册终端
     *
     * @param
     * @return
     */
    @POST("/api/register.do")
    Call<RegistBean> registToService(@Query("signature") String signature,
                                     @Query("timestamp") String timestamp,
                                     @Query("token") String token,
                                     @Query("deviceId") String deviceId);

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
    @GET
    Call<ProgramBean> requestProgram(@Url String url,
                                     @Query("signature") String signature,
                                     @Query("timestamp") String timestamp,
                                     @Query("token") String token,
                                     @Query("taskId") String taskId
    );

    /**
     * 作用:下载资源
     *
     * @param fileUrl
     *         资源地址
     */
    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String fileUrl);

    /**
     * 作用:时间同步
     */
    @GET("/api/get_now_time.do")
    Call<ResponseBody> sysncTime();

    /**
     * 作用:通知服务器资源同步完成
     *
     * @param mac
     *       终端mac地址
     */
    @POST("/api/sync_finish.do")
    Call<ResponseBody> sysncFinish(@Query("terminalId") String mac);

    /**
     * 作用:轮询获取节目单信息
     */
    @GET("/api/notification.do")
    Call<PushBean> pollingTask(@Query("signature") String signature,
                               @Query("timestamp") String timestamp,
                               @Query("token") String token,
                               @Query("deviceId") String deviceId);

    /**
     * 作用:通知服务器终端在线
     *
     * @param mac
     *         终端mac地址
     */
    @POST("/api/network_online.do")
    Call<ResponseBody> notifyOnLine(@Query("terminalId") String mac);


}
