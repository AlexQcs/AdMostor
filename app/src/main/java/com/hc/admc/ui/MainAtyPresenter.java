package com.hc.admc.ui;

import android.util.Log;

import com.alex.mvp.presenter.BaseMvpPresenter;
import com.google.gson.Gson;
import com.hc.admc.Constant;
import com.hc.admc.application.MyApplication;
import com.hc.admc.bean.program.ProgramBean;
import com.hc.admc.bean.program.PushBean;
import com.hc.admc.bean.program.RegistBean;
import com.hc.admc.request.Api;
import com.hc.admc.request.ApiService;
import com.hc.admc.request.CommonSubscriber;
import com.hc.admc.request.Http;
import com.hc.admc.util.DateFormatUtils;
import com.hc.admc.util.FileUtils;
import com.hc.admc.util.MD5;
import com.hc.admc.util.SpUtils;
import com.hc.admc.util.StringUtils;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.hc.admc.Constant.LOCAL_PROGRAM_PATH;

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

    private Subscription mProgramSubscription;//播放节目单读秒器订阅
    private Subscription mRigisterSubscription;//注册轮询订阅
    private Subscription mConnSocketSubscription;//链接WebSocket订阅
    private Subscription mReconnSocketSubscription;
    private List<ProgramBean.ProgramListBean> mProgramListBeens;//播放节目单列表订阅
    private Subscription mDownLoadSubscription;//下载资源文件订阅
    private Subscription mMD5Subscription;//MD5校验订阅
    private int mProgramListBeenIdx = 0;//记录在播放时间内的下标

    private MyApplication mApplication = MyApplication.getInstance();
    private static ApiService apiService = Http.getApiService();

    private boolean mRegistered = false;

    private WebSocketClient mSocketClient;
    private HashMap<String, String> mItemMD5Map;//播放资源md5map

//    private volatile String mDeviceToken;

    /**
     * 作用:初始化友盟自定义消息
     */
    public void init() {
        //时间同步
        syncTime();
        //获取sp
        getShareprefrence();
        //初始化友盟


        //注册消息
        if (!mRegistered) {
            mRigisterSubscription = Observable.interval(10, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            if (mRegistered) {
                                mRigisterSubscription.unsubscribe();
                            } else {
                                registDevice();
                            }
                        }
                    });

        }
        //从内存中获取节目对象
        String programStr = FileUtils.getStringFromTxT(Constant.LOCAL_PROGRAM_LIST_PATH);
        ProgramBean programBean = new ProgramBean();
        if (programStr.equals("")) {
            Log.e(TAG, "未找到节目单");
        } else {
            Gson gson = new Gson();
            programBean = gson.fromJson(programStr, ProgramBean.class);
            //播放节目单
            playProgram(programBean.getProgramList());
        }

        //链接WebSocket
        connSocket();
        //轮询服务器接口
//        pollingGetTask();
        //通知终端在线
//        pollingOnLine();
    }

    /**
     * 作用:连接WebSocket用于接收节目单推送或者其他消息
     */
    public void connSocket() {
        if (mConnSocketSubscription != null && !mConnSocketSubscription.isUnsubscribed()) {
            mConnSocketSubscription.unsubscribe();
        }
        if (mReconnSocketSubscription != null && !mReconnSocketSubscription.isUnsubscribed()) {
            mReconnSocketSubscription.unsubscribe();
        }
        String deviceid = Constant.MAC;
        String socketUrl = "ws://" + SpUtils.get("base_url", Api.BASE_URL) + "webSocket/" + deviceid;
        Log.e(TAG, socketUrl);
        mConnSocketSubscription = Observable.just(socketUrl)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String url) {
                        try {
                            //实例化websocket
                            mSocketClient = new WebSocketClient(new URI(url), new Draft_17()) {
                                @Override
                                public void onOpen(ServerHandshake handshakedata) {
                                    Log.e("WebSocketClient", "打开通道" + handshakedata.getHttpStatus());
                                    getMvpView().online();
                                }

                                @Override
                                public void onMessage(String message) {
                                    Log.e("WebSocketClient", "接收消息" + message);
                                    Gson gson = new Gson();

                                    PushBean pushBean = gson.fromJson(message, PushBean.class);
                                    if (pushBean == null) {
                                        Log.e("推送消息解析", "接收到异常数据");
                                        return;
                                    }
                                    Log.e("友盟消息解析", pushBean.toString());
                                    switch (pushBean.getCode()) {

                                        case 1001:
                                            Log.e(TAG, "关机消息");
//                                            Intent intent=new Intent("android.intent.action.shotdown");
                                            getMvpView().shutdown();
                                            //关机
                                            break;
                                        case 1002:
                                            Log.e(TAG, "重启消息");
                                            getMvpView().reboot();
                                            break;

                                        case 10003:
                                            Log.e(TAG, "播放节目消息");
                                            Map<String, String> data = pushBean.getData();
                                            String url = data.get("url");
                                            String id = data.get("taskId");
                                            requestProgram(url, id);
                                            break;

                                        case 10004:
                                            Log.e(TAG, "设置时间");
                                            getMvpView().settime();
                                            break;
                                        case 10005:
                                            Log.e(TAG, "隐藏导航栏");
                                            getMvpView().hidebar();
                                            break;
                                        case 10006:
                                            Log.e(TAG, "显示导航栏");
                                            getMvpView().showbar();
                                            break;
                                        case 10007:
                                            Log.e(TAG, "恢复出厂");
                                            getMvpView().recovery();
                                            break;
                                        case 10008:
                                            Log.e(TAG, "定时开关机");
                                            Map<String, String> time = pushBean.getData();
                                            String timeon = time.get("timeon");
                                            String timeoff = time.get("timeoff");
                                            String enableStr=time.get("setTimeOffAndOn");
                                            boolean enable=false;
                                            if ("true".equals(enableStr))enable=true;
                                            getMvpView().setpoweronoff(timeon, timeoff,enable);
                                        case 1009:
                                            Log.e(TAG, "截屏");
                                            getMvpView().screenshot();
                                            break;
                                        case 1010:
                                            Log.e(TAG, "开关背光");
                                            Map<String, String> backLight = pushBean.getData();
                                            String isOnStr=backLight.get("isOffAndOn");
                                            boolean isOn=false;
                                            if ("true".equals(isOnStr))isOn=true;
                                            getMvpView().backlight(isOn);
                                            break;
                                        case 1011:
                                            Log.e(TAG, "设置音量");
                                            Map<String, String> volData = pushBean.getData();
                                            String volumeStr = volData.get("volume");
                                            int volume = Integer.parseInt(volumeStr);
                                            getMvpView().setvolume(volume);
                                            break;
                                    }
                                }

                                @Override
                                public void onClose(int code, String reason, boolean remote) {
                                    Log.e("WebSocketClient", "通道关闭" + reason);
                                    getMvpView().offline();
                                    mReconnSocketSubscription = Observable.timer(5, TimeUnit.SECONDS).subscribe(
                                            new Action1<Long>() {
                                                @Override
                                                public void call(Long aLong) {
                                                    connSocket();
                                                }
                                            }
                                    );
                                }

                                @Override
                                public void onError(Exception ex) {
                                    Log.e("WebSocketClient", "链接失败");
                                    getMvpView().errline();
                                    mReconnSocketSubscription = Observable.timer(5, TimeUnit.SECONDS).subscribe(
                                            new Action1<Long>() {
                                                @Override
                                                public void call(Long aLong) {
                                                    connSocket();
                                                }
                                            }
                                    );
                                }
                            };
                            mSocketClient.connect();
                        } catch (URISyntaxException e) {
                            getMvpView().errline();
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 作用:注册设备
     * <p>
     * 设备码
     */
    private void registDevice() {
        String time_s = System.currentTimeMillis() + "";
        String token = Constant.TOKEN;
        String signature = StringUtils.getSignature(time_s, token);
        String deviceid = Constant.MAC;

        mMainAtyMode.regist(signature, time_s, token, deviceid, new Callback<RegistBean>() {
            @Override
            public void onResponse(Call<RegistBean> call, Response<RegistBean> response) {

                RegistBean registBean = response.body();
                if (registBean == null) return;
                if ("注册成功".equals(registBean.getMsg())) {
                    mRegistered = true;
                    saveShareprefrence();
                }
                Log.e(TAG, registBean.getMsg());
            }

            @Override
            public void onFailure(Call<RegistBean> call, Throwable t) {
                t.printStackTrace();
                Log.e(TAG, "设备注册失败，请检查网络配置");
            }
        });
    }

    /**
     * 作用:同步时间
     *
     * @exception/throws IOException
     */
    private void syncTime() {
        mMainAtyMode.syncTime(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String date = response.body().string();
                    if (date == null || "".equals(date)) {
                        Log.e(TAG, "onResponse: 同步时间请求出错");
                    } else {
                        date = date.replace("\"", "");
                        //利用adb指令修改系统时间
                        DateFormatUtils.syncTime(date);
                        Log.e(TAG, "时间同步" + date);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                Log.e(TAG, "时间同步失败");
            }

        });
    }

    /**
     * 作用:通知服务器同步文件已完成
     */
    private void sysncFinish() {
        String deviceid = Constant.MAC;
        mMainAtyMode.sysncFinish(deviceid, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.e(TAG, "文件同步通知成功");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "文件同步通知失败");
            }
        });
    }

    /**
     * 作用:请求服务器获取对应节目单
     *
     * @param url
     *         请求地址
     * @param taskId
     *         任务id
     */
    private void requestProgram(String url, String taskId) {
        String timestamp = System.currentTimeMillis() + "";
        String token = Constant.TOKEN;
        String signature = StringUtils.getSignature(timestamp, token);
        mMainAtyMode.requestProgram(url, signature, timestamp, token, taskId, new Callback<ProgramBean>() {
            @Override
            public void onResponse(Call<ProgramBean> call, Response<ProgramBean> response) {
                resolveProgram(response.body());
            }

            @Override
            public void onFailure(Call<ProgramBean> call, Throwable t) {
                t.printStackTrace();
                Log.e(TAG, "处理节目单失败");
            }
        });
    }

    /**
     * 作用:解析节目单
     *
     * @param programBean
     *         节目
     * @return
     * @exception/throws
     */
    private void resolveProgram(ProgramBean programBean) {
        if (programBean == null || programBean.getProgramList().size() == 0) {
            Log.e(TAG, "友盟消息节目单为空");
            return;
        } else {

            try {
                mItemMD5Map = new HashMap<>();
                mProgramListBeens = new ArrayList<>();
                mProgramListBeens = programBean.getProgramList();
                unsubscribeSub(mProgramSubscription);
                //将节目保存到内存中
                Gson gson = new Gson();
                String jsonStr = gson.toJson(programBean);
                StringUtils.coverTxtToFile(jsonStr, Constant.LOCAL_PROGRAM_LIST_PATH);

                List<String> fileUrlList = new ArrayList<>();

                for (ProgramBean.ProgramListBean been : mProgramListBeens) {
                    List<String> fileList = been.getResource();
                    List<ProgramBean.ProgramListBean.MatItemBean> matItemBeens = been.getMatItem();
                    for (ProgramBean.ProgramListBean.MatItemBean itemBean : matItemBeens) {
                        //获取资源md5列表
                        String id = itemBean.getItemId();
                        String content = itemBean.getContent();
                        String[] tempArray = id.split("_");
                        //只有当节目类型为图片、视频、音乐时才下载文件
                        if ("image".equals(tempArray[0]) || "video".equals(tempArray[0]) || "audio".equals(tempArray[0])) {
                            mItemMD5Map.put(itemBean.getPath(), content);
                        }
                    }
                    for (String s : fileList) {
                        //获取资源下载列表
                        String[] array = s.split("/");
                        String folderPath = LOCAL_PROGRAM_PATH + File.separator + array[1];
                        FileUtils.folderCreate(folderPath);
                        fileUrlList.add("http://" + (String) SpUtils.get("base_url", Api.BASE_URL) + s);
                    }
                }

                //下载资源
                mDownLoadSubscription = Observable.just(fileUrlList)
                        .subscribeOn(Schedulers.newThread())
                        .flatMap(new Func1<List<String>, Observable<String>>() {
                            @Override
                            public Observable<String> call(List<String> strings) {
                                return Observable.from(strings);
                            }
                        }).subscribe(new Subscriber<String>() {
                            @Override
                            public void onCompleted() {
                                Log.e(TAG, "下载资源文件完成，准备开始播放节目");
                                mApplication.setProgramPlay(false);
                                mMD5Subscription = Observable.interval(1, TimeUnit.SECONDS)
                                        .subscribeOn(Schedulers.newThread())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Action1<Long>() {
                                            @Override
                                            public void call(Long aLong) {
                                                boolean isDone = false;
                                                Iterator iter = mItemMD5Map.entrySet().iterator();
                                                //验证MD5
                                                while (iter.hasNext()) {
                                                    Map.Entry entry = (Map.Entry) iter.next();
                                                    String path = (String) entry.getKey();
                                                    String md5 = (String) entry.getValue();
                                                    path = Constant.LOCAL_PROGRAM_PATH + File.separator + path;
                                                    File file = new File(path);
                                                    isDone = MD5.decode(file, md5);
                                                    if (!isDone) {
                                                        getMvpView().downloadNow();
                                                        Log.e(TAG, "下载节目未完成");
                                                        return;
                                                    }
                                                }
                                                if (mItemMD5Map.size() == 0) isDone = true;
                                                if (isDone) {
                                                    //当md5校验全部通过时通知服务器以及播放节目
                                                    getMvpView().downloadNot();
                                                    sysncFinish();
                                                    playProgram(mProgramListBeens);
                                                }
                                            }
                                        });

                            }

                            @Override
                            public void onError(Throwable e) {
                                getMvpView().downloadErr();
                                e.printStackTrace();
                            }

                            @Override
                            public void onNext(String url) {
                                String[] tempArray = url.split("/");
                                Log.e("下载地址", url);
                                final String path = tempArray[tempArray.length - 2] + File.separator + tempArray[tempArray.length - 1];
                                final File file = new File(LOCAL_PROGRAM_PATH + File.separator + path);
                                Log.e("文件路径", file.getPath());
                                if (file.exists()) return;
                                apiService.downloadFile(url)
                                        .subscribeOn(Schedulers.io())
                                        .subscribe(new CommonSubscriber<ResponseBody>(MyApplication.getContext()) {
                                            @Override
                                            public void onNext(ResponseBody body) {
                                                Log.e(TAG, "下载资源文件列表");
                                                FileUtils.downloadFile(body, file);
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                super.onError(e);
                                            }

                                            @Override
                                            public void onCompleted() {
                                                Log.e(TAG, path + "下载成功!");
                                            }
                                        });
                            }
                        });

            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

    /**
     * 作用:播放节目
     *
     * @param programListBeens
     *         节目列表
     */
    private void playProgram(final List<ProgramBean.ProgramListBean> programListBeens) {

        HashMap<String, String> itemMD5Map = new HashMap<>();

        for (ProgramBean.ProgramListBean been : programListBeens) {
            List<String> fileList = been.getResource();
            List<ProgramBean.ProgramListBean.MatItemBean> matItemBeens = been.getMatItem();
            for (ProgramBean.ProgramListBean.MatItemBean itemBean : matItemBeens) {
                //获取资源md5列表
                String id = itemBean.getItemId();
                String content = itemBean.getContent();
                String[] tempArray = id.split("_");
                //只有当节目类型为图片、视频、音乐时才记录MD5值
                if ("image".equals(tempArray[0]) || "video".equals(tempArray[0]) || "audio".equals(tempArray[0])) {
                    itemMD5Map.put(itemBean.getPath(), content);
                }
            }
        }

        Iterator iter = itemMD5Map.entrySet().iterator();
        //验证MD5
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String path = (String) entry.getKey();
            String md5 = (String) entry.getValue();
            path = Constant.LOCAL_PROGRAM_PATH + File.separator + path;
            File file = new File(path);
            boolean isCompleteness = MD5.decode(file, md5);
            if (!isCompleteness) {
                file.delete();
                Log.e(TAG, "下载节目未完成");
                return;
            }
        }

        Log.e(TAG, "播放新节目");
        if (mMD5Subscription != null && !mMD5Subscription.isUnsubscribed()) {
            mMD5Subscription.unsubscribe();
        }
        if (mDownLoadSubscription != null && !mDownLoadSubscription.isUnsubscribed()) {
            mDownLoadSubscription.unsubscribe();
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
                        Date curdate = new Date(System.currentTimeMillis());
                        //判断节目单是否在规定时间内
                        boolean isBelongCalendar = false;
                        for (int i = 0; i < programListBeens.size(); i++) {
                            ProgramBean.ProgramListBean programListBean = programListBeens.get(i);
                            ProgramBean.ProgramListBean.TimingBean timingBean = programListBean.getTiming();
                            isBelongCalendar = isTiming(curdate, timingBean);
                            if (isBelongCalendar) {
                                mProgramListBeenIdx = i;
                                break;
                            }
                        }

                        if (isBelongCalendar) {
                            //获取播放对象
                            ProgramBean.ProgramListBean programListBean = programListBeens.get(mProgramListBeenIdx);
                            if (programListBean == null) return;
                            if (mApplication.isProgramPlay) {

                            } else {
                                //删除列表中的播放对象，防止重新播放同一个节目
                                programListBeens.remove(mProgramListBeenIdx);
                                Log.e("播放节目单", mProgramSubscription.toString());
                                mApplication.setProgramPlay(true);
                                getMvpView().playProgram(programListBean);
                            }
                        }

                    }
                });
    }

    /**
     * 作用:解除订阅
     *
     * @param subscription
     *         监听者
     */
    private void unsubscribeSub(Subscription subscription) {
        if (subscription != null && subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    public void destrory() {
        //当activity被销毁时取消订阅
        unsubscribeSub(mProgramSubscription);
        unsubscribeSub(mRigisterSubscription);
        unsubscribeSub(mConnSocketSubscription);
        unsubscribeSub(mReconnSocketSubscription);
    }

    /**
     * 作用:判断是否规定时间内
     */
    private boolean isTiming(Date date, ProgramBean.ProgramListBean.TimingBean timingBean) {

        String beginDateStr = timingBean.getBeginDate() + " " + timingBean.getBeginTime();
        Date beginDate = DateFormatUtils.string2Date(beginDateStr, "yyyy-MM-dd HH:mm:ss");
        String endDateStr = timingBean.getEndDate() + " " + timingBean.getEndTime();
        Date endDate = DateFormatUtils.string2Date(endDateStr, "yyyy-MM-dd HH:mm:ss");
        boolean isInDate = DateFormatUtils.belongCalendar(date, beginDate, endDate);

        String curClockStr = DateFormatUtils.date2String(date, "HH:mm:ss");
        Date curClock = DateFormatUtils.string2Date(curClockStr, "HH:mm:ss");
        String beginClockStr = timingBean.getBeginTime();
        Date beginClock = DateFormatUtils.string2Date(beginClockStr, "HH:mm:ss");
        String endClockStr = timingBean.getEndTime();
        Date endClock = DateFormatUtils.string2Date(endClockStr, "HH:mm:ss");
        boolean isInClock = DateFormatUtils.belongCalendar(curClock, beginClock, endClock);

        return DateFormatUtils.belongCalendar(date, beginDate, endDate);
    }

    private void getShareprefrence() {
        mRegistered = (boolean) SpUtils.get(Constant.REGISTERED, false);
    }

    private void saveShareprefrence() {
        SpUtils.put(Constant.REGISTERED, mRegistered);
    }


    public String parseFramedata(Framedata framedata) {
        String result = "null";
        ByteBuffer buffer = framedata.getPayloadData();
        if (null == buffer) {
            return result;
        }
        byte[] data = buffer.array();
        if (null != data && data.length > 0) {
            return new String(data);
        }
        return result;
    }

}
