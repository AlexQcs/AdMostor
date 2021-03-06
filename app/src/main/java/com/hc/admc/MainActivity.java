package com.hc.admc;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alex.mvp.factory.CreatePresenter;
import com.alex.mvp.view.AbstractMvpActivitiy;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hc.admc.application.MyApplication;
import com.hc.admc.base.ActivityCollector;
import com.hc.admc.base.OnPermissionCallbackListener;
import com.hc.admc.bean.program.ProgramBean;
import com.hc.admc.bean.program.TextBean;
import com.hc.admc.service.PlayingMusicServices;
import com.hc.admc.ui.MainAtyPresenter;
import com.hc.admc.ui.MainView;
import com.hc.admc.util.DateFormatUtils;
import com.hc.admc.util.FieldView;
import com.hc.admc.util.FileUtils;
import com.hc.admc.util.GlideCacheUtil;
import com.hc.admc.util.MemInfo;
import com.hc.admc.util.SpUtils;
import com.hc.admc.util.ViewFind;
import com.hc.admc.util.VolumeUtils;
import com.hc.admc.view.CustomTextView;
import com.hc.admc.view.LEDView;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import rx.Subscription;

@CreatePresenter(MainAtyPresenter.class)
public class MainActivity extends AbstractMvpActivitiy<MainView, MainAtyPresenter> implements MainView {


    private static final String TAG = "MainActivity";
    /**
     * 节目类型的标识
     */
    private static final String VIDEOLAYOUT = "video";
    private static final String IMAGELAYOUT = "image";
    private static final String WEBLAYOUT = "url";
    private static final String TEXTLAYOUT = "text";
    private static final String CLOCKLAYOUT = "clock";
    private static final String MUSICLAYOUT = "audio";

    /**
     * 规定开始音乐、暂停音乐、结束音乐的标志
     */
    public static final int PLAY_MUSIC = 1;
    public static final int PAUSE_MUSIC = 2;
    public static final int STOP_MUSIC = 3;


//    @FieldView(R.id.webview)
//    private WebView mWebView;

    @FieldView(R.id.tv_skip_setip)
    private TextView mTvSkipSetip;//跳出设置ip的dialog
    @FieldView(R.id.tv_serialnum)
    private TextView mTvSerialNum;
    @FieldView(R.id.img_online)
    private ImageView mImgOnline;
    @FieldView(R.id.img_download)
    private ImageView mImgDownload;
    @FieldView(R.id.tv_memory_remind)
    private TextView mTvMemoryRemind;


    private EditText mEtvUrl;
    private EditText mEtvPort;
    private TextView mTvDialogSerial;

    private Context mContext = MainActivity.this;

    private OnPermissionCallbackListener mListener;
    private AlertDialog dialog;
    private ActivityCollector mActivityCollector;

    private FrameLayout mProgramLayout;//节目layout
    private FrameLayout.LayoutParams mAtyLayoutParams;//根layout的参数

    private int mScreenWidth;//屏幕宽度
    private int mScreenHeight;//屏幕高度

    private List<String> mItemIDList;//播放节目子控件的箱子控件列表
    private HashMap<String, String> mItemMD5;//播放资源md5map
    private HashMap<String, List<String>> mItemResList;//控件对应播放资源列表

    private HashMap<String, Integer> mItemIndexMap;//资源下标map

    private Subscription mSubscriImageTimer;

    private MediaPlayer mMediaPlayer;
    private int mMusicListIdx = 0;
    private PlayingMusicServices mMusicServices;
    private BroadcastReceiver mMusicBroadcast;
    private ArrayList<String> mMusicPathList;
    private String mMusicItemId;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMusicServices = ((PlayingMusicServices.MusicBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMusicServices = null;
        }
    };

    private ImageHandler mImageHandler;

    private Timer mTimer;//图片定时器
    private TimerTask mTask;
    private boolean mIsDestroy;
    private Map<String, MediaPlayer> mPlayerMap;

    private boolean isMusicBind;//判断音乐服务是否开启

    private final Handler mLineStatusHandler = new LineStatusHandler(this);

    private final Handler mDownloadHandler = new DownLoadStatusHandler(this);

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        ViewFind.bind(this);
        initData();
        initView();
        initEvent();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    void initData() {
        if (MemInfo.getAvailableSize() < 500) {
            FileUtils.delAllFile(Constant.LOCAL_FILE_PATH);
            mTvMemoryRemind.setVisibility(View.VISIBLE);
        } else {
            mTvMemoryRemind.setVisibility(View.GONE);
        }
        mActivityCollector = ActivityCollector.getInstance();
        mActivityCollector.addActivity(this);
        if (Build.VERSION.SDK_INT >= 23) {
            initPermission();
        }
        FileUtils.checkAppFile();
        mItemIDList = new ArrayList<>();
        mItemIndexMap = new HashMap<>();
        mItemMD5 = new HashMap<>();
        mItemResList = new HashMap<>();
        mPlayerMap = new HashMap<>();
        mMusicPathList = new ArrayList<>();

        mImageHandler = new ImageHandler(this);

    }

    void initView() {

        mTvSerialNum.setText(Constant.getSerialNumber());
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mScreenWidth = size.x;
        mScreenHeight = size.y;

        mProgramLayout = (FrameLayout) findViewById(R.id.frame_program);
        mAtyLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        mProgramLayout.setLayoutParams(mAtyLayoutParams);
        mImgOnline.setImageResource(R.mipmap.offline);

        int width = 500;
        int height = 500;
        int marginLeft = 200;
        int marginTop = 300;
        RelativeLayout.LayoutParams itemParams = new RelativeLayout.LayoutParams(width, height);
        itemParams.setMargins(marginLeft, marginTop, 0, 0);

    }

    void initEvent() {
        getMvpPresenter().init();
        mTvSkipSetip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.dialog_baseurl, null);
                mEtvUrl = (EditText) view.findViewById(R.id.edit_url);
                mEtvPort = (EditText) view.findViewById(R.id.edit_port);
                mTvDialogSerial = (TextView) view.findViewById(R.id.tv_serialnum_dialog);
                mTvDialogSerial.setText("设备标识码:" + Constant.getSerialNumber());
                mEtvUrl.setHintTextColor(Color.GRAY);
                mEtvPort.setHintTextColor(Color.GRAY);
                builder.setView(view)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String url = mEtvUrl.getText().toString();
                                String port = mEtvPort.getText().toString();
                                //设置BaseUrl
                                if ("".equals(url)) {
                                    Toast.makeText(mContext, "地址不能为空", Toast.LENGTH_SHORT).show();
                                } else {
                                    String baseurl = "";
                                    if ("".equals(port)) {
                                        baseurl = url + "/";
                                    } else {
                                        baseurl = url + ":" + port + "/";
                                    }
                                    SpUtils.put("base_url", baseurl);
                                    SpUtils.put(Constant.REGISTERED, false);
                                    getMvpPresenter().init();
                                }
                            }
                        })
                        .setNegativeButton("取消", null);
                builder.create().show();
            }
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PlayingMusicServices.ACT_MUSIC_COMPLETE);

        mMusicBroadcast = new PlayMusicBroadCast();
        registerReceiver(mMusicBroadcast, intentFilter);
    }

    @Override
    public void receiveUmengMsg(String msg) {
        Log.e(TAG, "主页面收到友盟推送消息:" + msg);
    }

    private void layoutProgramView(List<ProgramBean.ProgramListBean.LayoutBean.ItemsBean> items) {
        if (items == null || items.size() == 0) {
            Log.e(TAG, "布局模板为空");
            return;
        }
        mProgramLayout.removeAllViews();
        mItemIDList.clear();
        mItemIndexMap.clear();
        mItemResList.clear();
        mPlayerMap.clear();
        playingmusic(STOP_MUSIC);
        Message message = new Message();
        message.what = 1;
        mImageHandler.sendMessage(message);
        for (ProgramBean.ProgramListBean.LayoutBean.ItemsBean item : items) {
            int width = Integer.parseInt(item.getReality_width());
            int height = Integer.parseInt(item.getReality_height());
            int marginLeft = item.getReality_x();
            int marginTop = item.getReality_y();
            FrameLayout.LayoutParams itemParams = new FrameLayout.LayoutParams(width, height);
            itemParams.setMargins(marginLeft, marginTop, 0, 0);
            String[] itemIdArray = item.getId().split("_");
            switch (itemIdArray[0]) {
                case VIDEOLAYOUT:
                    FrameLayout frameLayout = new FrameLayout(mContext);
                    frameLayout.setLayoutParams(itemParams);
                    mProgramLayout.addView(frameLayout);
                    mPlayerMap.put(item.getId(), new MediaPlayer());
                    break;
                case IMAGELAYOUT:
                    ImageView imageView = new ImageView(mContext);
//                    imageView.bringToFront();
                    imageView.setLayoutParams(itemParams);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    mProgramLayout.addView(imageView);
                    break;
                case WEBLAYOUT:
                    WebView webView = new WebView(getApplicationContext());
                    webView.setLayoutParams(itemParams);
                    mProgramLayout.addView(webView);
                    break;
                case TEXTLAYOUT:
                    CustomTextView textView = new CustomTextView(mContext);
                    textView.setTextSize(height);
                    textView.setLayoutParams(itemParams);
                    mProgramLayout.addView(textView);
                    break;
                case CLOCKLAYOUT:
                    LEDView ledView = new LEDView(mContext);
                    ledView.bringToFront();
                    ledView.setLayoutParams(itemParams);
                    mProgramLayout.addView(ledView);
                    break;
                case MUSICLAYOUT:
                    View musicView = new View(mContext);
                    musicView.setLayoutParams(itemParams);
                    mProgramLayout.addView(musicView);
                    break;
            }
            mItemIndexMap.put(item.getId(), 0);
            mItemIDList.add(item.getId());
        }
    }

    @Override
    public void playProgram(ProgramBean.ProgramListBean bean) {
        //1.布局节目模块
        List<ProgramBean.ProgramListBean.LayoutBean.ItemsBean> itemsBeanList = bean.getLayout().getItems();
        if (itemsBeanList == null || itemsBeanList.size() == 0) {
            Toast.makeText(mContext, "播放控件为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (itemsBeanList.size() > 0) {
            layoutProgramView(itemsBeanList);
        }

        List<ProgramBean.ProgramListBean.MatItemBean> matItemBeens = bean.getMatItem();


        for (String itemid : mItemIDList) {
            List<String> pathList = new ArrayList<>();
            for (ProgramBean.ProgramListBean.MatItemBean matItem : matItemBeens) {
                if (itemid.equals(matItem.getItemId())) {
                    String[] tempArray = itemid.split("_");
                    if ("video".equals(tempArray[0]) || "image".equals(tempArray[0]) || "audio".equals(tempArray[0])) {
                        pathList.add(matItem.getPath());
                    } else {
                        pathList.add(matItem.getContent());
                    }
                }
            }
            mItemResList.put(itemid, pathList);
        }

        Iterator iter = mItemResList.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String itemid = (String) entry.getKey();
            ArrayList<String> pathList = (ArrayList<String>) entry.getValue();
            for (int i = 0; i < mItemIDList.size(); i++) {
                if (itemid.equals(mItemIDList.get(i))) {
                    String[] tempArray = itemid.split("_");
                    switch (tempArray[0]) {
                        case "video":
                            playVideo(pathList, itemid, i);
                            break;
                        case "image":
                            Message message = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("itemid", itemid);
                            bundle.putStringArrayList("pathList", pathList);
                            bundle.putInt("childViewIndex", i);
                            mIsDestroy = false;
                            message.setData(bundle);
                            message.what = 0;
                            mImageHandler.sendMessage(message);
                            break;
                        case "url":
                            playWeb(pathList, itemid, i);
                            break;
                        case "text":
                            playText(pathList, itemid, i);
                            break;
                        case "clock":
                            playClock(i);
                            break;
                        case "audio":
                            mMusicPathList.clear();
                            mMusicPathList = (ArrayList<String>) pathList.clone();
                            mMusicItemId = itemid;
//                            playingmusic(STOP_MUSIC);
//                            playMusic();
                            playingmusic(PLAY_MUSIC);
                            break;
                    }
                }
            }
        }
    }

    @Override
    public void online() {
        Message message = new Message();
        message.what = 1;
        mLineStatusHandler.sendMessage(message);
    }

    @Override
    public void offline() {
        Message message = new Message();
        message.what = 0;
        mLineStatusHandler.sendMessage(message);
    }

    @Override
    public void errline() {
        Message message = new Message();
        message.what = 2;
        mLineStatusHandler.sendMessage(message);
    }

    @Override
    public void downloadNot() {
        Message message = new Message();
        message.what = 0;
        mDownloadHandler.sendMessage(message);
    }

    @Override
    public void downloadNow() {
        Message message = new Message();
        message.what = 1;
        mDownloadHandler.sendMessage(message);
    }

    @Override
    public void downloadErr() {
        Message message = new Message();
        message.what = 2;
        mDownloadHandler.sendMessage(message);
    }

    @Override
    public void setpoweronoff(String timeonStr, String timeoffStr, boolean enable) {
        Intent intent = new Intent("android.intent.setpoweronoff");

        Date dateOn = DateFormatUtils.string2Date(timeonStr, "yyyy-MM-dd hh:mm:ss");
        Date dateOff = DateFormatUtils.string2Date(timeoffStr, "yyyy-MM-dd hh:mm:ss");

        Calendar calendarOn = Calendar.getInstance();
        calendarOn.setTime(dateOn);
        Calendar calendarOff = Calendar.getInstance();
        calendarOff.setTime(dateOff);

        int yearOn=calendarOn.get(Calendar.YEAR);
        int monthOn=calendarOn.get(Calendar.MONTH);
        int dayOn=calendarOn.get(Calendar.DATE);
        int hourOn=calendarOn.get(Calendar.HOUR_OF_DAY);
        int minuteOn=calendarOn.get(Calendar.MINUTE);
        int secondOn=calendarOn.get(Calendar.SECOND);

        int yearOff=calendarOff.get(Calendar.YEAR);
        int monthOff=calendarOff.get(Calendar.MONTH);
        int dayOff=calendarOff.get(Calendar.DATE);
        int hourOff=calendarOff.get(Calendar.HOUR_OF_DAY);
        int minuteOff=calendarOff.get(Calendar.MINUTE);
        int secondOff=calendarOff.get(Calendar.SECOND);

        int[] timeon = new int[]{2015, 07, 14, 8, 30, 0};//开机时间
        intent.putExtra("timeon", timeon);
        int[] timeoff = new int[]{2015, 07, 14, 18, 0, 0};//关机时间
        intent.putExtra("timeoff", timeoff);
        intent.putExtra("enable", enable);//true为启用,false为取消此功能
        sendBroadcast(intent);
    }

    @Override
    public void settime() {
//        Intent intent = new Intent("android.intent.action.settime");
//        int[] settime = new int[]{2015, 01, 14, 18, 0, 0};
//        intent.putExtra("settime", settime);
//        sendBroadcast(intent);
    }

    @Override
    public void shutdown() {
        Intent intent = new Intent("android.intent.action.shutdown");
        sendBroadcast(intent);
    }

    @Override
    public void reboot() {
        Intent intent = new Intent("android.intent.action.reboot");
        sendBroadcast(intent);
    }

    @Override
    public void backlight(boolean isOn) {
        Intent intent = new Intent("android.yide.ointent.action.backlight");
        intent.putExtra("enable", isOn);
        sendBroadcast(intent);
    }

    @Override
    public void setvolume(int volume) {
        VolumeUtils.setVolum(volume);
    }


    @Override
    public void hidebar() {
        Intent intent = new Intent("android.intent.action.hidebar");
        sendBroadcast(intent);
    }

    @Override
    public void showbar() {
        Intent intent = new Intent("android.intent.action.showbar");
        sendBroadcast(intent);
    }

    @Override
    public void recovery() {
        Intent intent = new Intent("android.yide.intent.action.recovery");
        intent.putExtra("enable", false);//确认恢复true 取消恢复false
        sendBroadcast(intent);
    }

    @Override
    public void screenshot() {
        Intent intent = new Intent("rk.android.screenshot.action");
        //图片保存路径,/mnt/sdcard/Screenshots,文件名按时间保存例如Screenshot_2011-01-01-20-00-58.png
        sendBroadcast(intent);
    }

    static class LineStatusHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        public LineStatusHandler(MainActivity mainActivity) {
            mActivity = new WeakReference<MainActivity>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mActivity.get() == null) {
                return;
            }
            mActivity.get().setLineStatus(msg);
        }
    }

    static class DownLoadStatusHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        public DownLoadStatusHandler(MainActivity mainActivity) {
            mActivity = new WeakReference<MainActivity>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mActivity.get() == null) {
                return;
            }
            mActivity.get().setDownloadStatus(msg);
        }
    }

    public void setLineStatus(Message message) {
        switch (message.what) {
            case 0:
                mImgOnline.setImageResource(R.mipmap.offline);
                break;
            case 1:
                mImgOnline.setImageResource(R.mipmap.online);
                break;
            case 2:
                mImgOnline.setImageResource(R.mipmap.errline);
                break;
        }
    }

    public void setDownloadStatus(Message message) {
        switch (message.what) {
            case 0:
                mImgDownload.setImageResource(R.mipmap.download_not);
                break;
            case 1:
                mImgDownload.setImageResource(R.mipmap.download_now);
                break;
            case 2:
                mImgDownload.setImageResource(R.mipmap.download_err);
                break;
        }
    }

    private void playVideo(final ArrayList<String> pathList, final String itemid, final int childViewIndex) {

        FrameLayout frameLayout = (FrameLayout) mProgramLayout.getChildAt(childViewIndex);
        if (pathList == null || pathList.size() == 0) {
            Log.e(TAG, "视频列表为空");
        } else {
            playVideo(frameLayout, pathList, itemid);
        }
    }

    private void playVideo(FrameLayout frameLayout, final ArrayList<String> pathList, final String itemid) {
        Log.e(TAG, itemid + "播放视频");
        final TextureView textureView = new TextureView(mContext);
        FrameLayout.LayoutParams viewParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        textureView.setLayoutParams(viewParams);
        frameLayout.addView(textureView);
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(final SurfaceTexture surface, final int width, final int height) {

                Surface s = new Surface(surface);
                try {
                    mPlayerMap.put(itemid, new MediaPlayer());
                    String path = Constant.LOCAL_PROGRAM_PATH + File.separator + pathList.get(mItemIndexMap.get(itemid));
                    mPlayerMap.get(itemid).setDataSource(path);
                    mPlayerMap.get(itemid).setSurface(s);

                    mPlayerMap.get(itemid).setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mPlayerMap.get(itemid).start();
                        }
                    });
                    mPlayerMap.get(itemid).setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mPlayerMap.get(itemid).release();
                            System.gc();
                            int currentIdx = mItemIndexMap.get(itemid) + 1;
                            if (currentIdx == pathList.size()) {
                                currentIdx = 0;
                            }
                            mItemIndexMap.put(itemid, currentIdx);
                            if (textureView.isAvailable()) {
                                onSurfaceTextureAvailable(surface, width, height);
                            }
                        }
                    });
                    mPlayerMap.get(itemid).setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mPlayerMap.get(itemid).prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                mPlayerMap.get(itemid).stop();
                mPlayerMap.get(itemid).release();
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
    }

    private static class ImageHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        public ImageHandler(MainActivity mainActivity) {
            mActivity = new WeakReference<MainActivity>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mActivity.get() == null) {
                return;
            }
            mActivity.get().playImage(msg);
        }
    }

    public void playImage(Message msg) {
        switch (msg.what) {
            case 0:
                if (mTimer != null) {
                    mTimer.cancel();
                    mTimer = null;
                }
                if (mTask != null) {
                    mTask.cancel();
                    mTask = null;
                }
                if (mIsDestroy) return;
                Bundle bundle = msg.getData();
                final ArrayList<String> pathList = bundle.getStringArrayList("pathList");
                final int childViewIndex = bundle.getInt("childViewIndex");
                final String itemid = bundle.getString("itemid");

                if (pathList == null || pathList.size() == 0 || mItemIndexMap == null) {
                    Log.e(TAG, "图片列表为空");
                } else {
                    Log.e(TAG, itemid + "播放图片");
                    if (mItemIndexMap.get(itemid) == null) {
                        Log.e(TAG, "控件不存在：" + mItemIndexMap.get(itemid));
                        return;
                    } else if (pathList.get(mItemIndexMap.get(itemid)) == null) {
                        Log.e(TAG, "图片路径不存在：" + pathList.get(mItemIndexMap.get(itemid)));
                        return;
                    }
                    String picpath = Constant.LOCAL_PROGRAM_PATH + File.separator + pathList.get(mItemIndexMap.get(itemid));

                    GlideCacheUtil.getInstance().clearImageAllCache(MainActivity.this);
                    File file = new File(picpath);
                    if (!file.exists()) {
                        Log.e("图片文件不存在", picpath);
                        return;
                    }
                    if (mProgramLayout.getChildAt(childViewIndex) == null) {
                        Log.e("图片控件不存在", picpath);
                        return;
                    }
                    Glide.with(MyApplication.getContext())
                            .load(file)
                            .override(650, 595)
                            .into((ImageView) mProgramLayout.getChildAt(childViewIndex));
                    int currentIdx = mItemIndexMap.get(itemid) + 1;
                    if (currentIdx == pathList.size()) {
                        currentIdx = 0;
                    }
                    mItemIndexMap.put(itemid, currentIdx);

                    mTimer = new Timer();
                    mTask = new TimerTask() {
                        @Override
                        public void run() {
//                        mImgViewPic.stopAutoCycle();
                            Message message = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("itemid", itemid);
                            bundle.putStringArrayList("pathList", pathList);
                            bundle.putInt("childViewIndex", childViewIndex);

                            message.setData(bundle);

                            if (mIsDestroy) {
                                message.what = 1;
                            } else {
                                message.what = 0;
                            }
                            mImageHandler.sendMessage(message);
                        }
                    };
                    mTimer.schedule(mTask, 10000);
                    break;
                }
            case 1:
                if (mTimer != null) {
                    mTimer.cancel();
                    mTimer = null;
                }
                if (mTask != null) {
                    mTask.cancel();
                    mTask = null;
                }
                break;
        }
    }

    public void playWeb(final ArrayList<String> pathList, final String itemid, final int childViewIndex) {
        WebView webView = (WebView) mProgramLayout.getChildAt(childViewIndex);
        if (pathList == null || pathList.size() == 0) {
            Log.e(TAG, "网页列表为空");
        } else {
            //        mWebView.loadUrl("http://www.baidu.com");
            // 开启 localStorage
            webView.getSettings().setDomStorageEnabled(true);
            // 设置支持javascript
            webView.getSettings().setJavaScriptEnabled(true);
            // 启动缓存
            webView.getSettings().setAppCacheEnabled(true);
            // 设置缓存模式
            webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            //使用自定义的WebViewClient
            webView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.134 Safari/537.36");
            webView.setWebViewClient(new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                view.loadUrl(requ);
                    return true;
                }

                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                    handler.proceed(); // 接受网站证书
                }
            });
            webView.loadUrl(pathList.get(mItemIndexMap.get(itemid)));
        }
    }

    /**
     * 作用:文字内容
     *
     * @param contentList
     *         播放的文字内容集合
     * @param itemid
     *         控件id
     * @param childViewIndex
     *         控件在framlayout中的下标
     * @return
     * @exception/throws
     */
    public void playText(final ArrayList<String> contentList, final String itemid, final int childViewIndex) {
        final CustomTextView textView = (CustomTextView) mProgramLayout.getChildAt(childViewIndex);
        if (contentList == null || contentList.size() == 0) {
            Log.e(TAG, "网页列表为空");
        } else {
            Gson gson = new Gson();
            TextBean textBean = gson.fromJson(contentList.get(mItemIndexMap.get(itemid)), TextBean.class);
            int textsize = textBean.getFontSize();
            String content = textBean.getContent();
            textView.setTextSize(textsize);
            textView.setFocusable(true);

            textView.setText(content);
            textView.startMove();
            textView.setOnMoveOver(new CustomTextView.onMoveOver() {
                @Override
                public void onOver() {
                    int currentIdx = mItemIndexMap.get(itemid) + 1;
                    if (currentIdx == contentList.size()) {
                        currentIdx = 0;
                    }
                    mItemIndexMap.put(itemid, currentIdx);
                    textView.stopMove();
                    playText(contentList, itemid, childViewIndex);
                }
            });
//            textView.setBackgroundColor(Color.BLUE);
        }
    }

    /**
     * 作用:时钟控件
     *
     * @param childViewIndex
     *         子view的下标
     */
    public void playClock(final int childViewIndex) {
        final LEDView ledView = (LEDView) mProgramLayout.getChildAt(childViewIndex);
        ledView.start();
    }


    private void playingmusic(int type) {
        //启动服务，播放音乐

//        ArrayList<String> pathList=new ArrayList<>();
//        String musicPath = Environment.getExternalStorageDirectory() + "/Pictures/wujiu.mp3";

        if (mMusicPathList == null || mMusicPathList.size() == 0) {
            Log.e(TAG, "音乐数组为空");
            return;
        }

        Intent intent = new Intent(this, PlayingMusicServices.class);
        intent.putExtra("type", type);
        if (type == PLAY_MUSIC) {
            mMusicListIdx++;
            if (mMusicListIdx >= mMusicPathList.size()) {
                mMusicListIdx = 0;
            }
            String musicPath = Constant.LOCAL_PROGRAM_PATH + File.separator + mMusicPathList.get(mItemIndexMap.get(mMusicItemId));
            intent.putExtra("path", musicPath);
            mItemIndexMap.put(mMusicItemId, mMusicListIdx);
        }

        startService(intent);
        isMusicBind = bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private class PlayMusicBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(PlayingMusicServices.ACT_MUSIC_COMPLETE)) {
                playingmusic(PLAY_MUSIC);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Message message = new Message();
        message.what = 1;
        mImageHandler.sendMessage(message);
        getMvpPresenter().destrory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mConnection != null && isMusicBind) {
            unbindService(mConnection);
        }
        Intent intent = new Intent(this, PlayingMusicServices.class);
        stopService(intent);
        unregisterReceiver(mMusicBroadcast);

        Message message = new Message();
        message.what = 1;
        mImageHandler.sendMessage(message);
        getMvpPresenter().destrory();
        MainActivity.this.finish();
        System.exit(0);
    }

    /**
     * 申请权限
     *
     * @param permissions
     */
    public void onRequestPermission(String[] permissions, OnPermissionCallbackListener listener) {
        mListener = listener;
        List<String> permissionList = new ArrayList<>();
        //1、 哪些权限需要申请
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(mActivityCollector.getTopActivity()
                    , permission) != PackageManager.PERMISSION_GRANTED) {
                //权限没有申请 添加到要申请的权限列表中
                permissionList.add(permission);
            }
        }
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(mActivityCollector.getTopActivity(),
                    permissionList.toArray(new String[permissionList.size()]), 1);
        } else {
            //所有权限都同意了
            mListener.onGranted();
        }
    }

    /**
     * 权限申请结果
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            List<String> deniedPermissions = new ArrayList<>();
            if (grantResults.length > 0) {
                for (int i = 0; i < grantResults.length; i++) {
                    int grantResult = grantResults[i];
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        deniedPermissions.add(permissions[i]);
                    }
                }
            }
            if (!deniedPermissions.isEmpty()) {
                mListener.onDenied(deniedPermissions);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                    boolean b = shouldShowRequestPermissionRationale(permissions[0]);
                    if (!b) {
                        // 用户还是想用我的 APP 的
                        // 提示用户去应用设置界面手动开启权限
                        showTipsDialog();
                    }
                }
            }
        } else {
            //所有的权限都被接受了
            mListener.onGranted();
        }
    }

    /**
     * 作用:权限申请
     */
    private void showTipsDialog() {
        // 跳转到应用设置界面
        dialog = new AlertDialog.Builder(this)
                .setTitle("权限不可用")
                .setMessage("请在-应用设置-权限-中，允许使用权限")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e("BaseActivity", "要开启进行权限设置");
                        // 跳转到应用设置界面
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setCancelable(false).show();
    }

    /**
     * 作用:权限列表
     */
    private void initPermission() {
        onRequestPermission(new String[]{
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE
        }, new OnPermissionCallbackListener() {
            @Override
            public void onGranted() {
            }

            @Override
            public void onDenied(List<String> deniedPermissions) {
            }
        });
    }

}
