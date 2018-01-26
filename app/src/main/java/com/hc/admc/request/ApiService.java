package com.hc.admc.request;

import com.hc.admc.bean.program.ProgramBean;
import com.hc.admc.bean.program.RegistBean;
import com.hc.admc.bean.program.UMengBean;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Alex on 2017/12/8.
 * 备注:请求服务器通讯操作
 */

public interface ApiService {


    @GET("/api/register.do")
    Call<RegistBean> registToService(@Query("signature") String signature,
                                     @Query("timestamp") String timestamp,
                                     @Query("token") String token,
                                     @Query("deviceId")String deviceId,
                                     @Query("deviceToken")String deviceToken);

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
                                     @Query("taskId")String taskId
                                     );

    @GET
    Observable<ResponseBody> downloadFile(@Url String fileUrl);

    @GET("/api/get_now_time.do")
    Call<ResponseBody> sysncTime();

    @GET("/api/sync_finish.do")
    Call<ResponseBody> sysncFinish(@Query("terminalId")String mac);

    /**
     * 作用:
     *
     * @param
     * @return
     * @exception/throws
     */
    @GET("/api/notification.do")
    Call<UMengBean> pollingTask(@Query("signature") String signature,
                                @Query("timestamp") String timestamp,
                                @Query("token") String token,
                                @Query("deviceId")String deviceId);

    @GET("/api/network_online.do")
    Call<ResponseBody> notifyOnLine(@Query("terminalId")String mac);




}
