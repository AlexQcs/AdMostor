package com.hc.admc;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alex.mvp.factory.CreatePresenter;
import com.alex.mvp.view.AbstractMvpActivitiy;
import com.bumptech.glide.Glide;
import com.hc.admc.application.MyApplication;
import com.hc.admc.bean.program.ProgramBean;
import com.hc.admc.ui.MainAtyPresenter;
import com.hc.admc.ui.MainView;
import com.hc.admc.util.FileUtils;
import com.hc.admc.util.GlideCacheUtil;
import com.hc.admc.util.ViewFind;
import com.hc.admc.view.CustomVideoView;
import com.umeng.message.PushAgent;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private static final String VIDEOLAYOUT = "video";
    private static final String IMAGELAYOUT = "image";

//    @FieldView(R.id.webview)
//    private WebView mWebView;

    private Context mContext = MainActivity.this;

    private FrameLayout mAtyLayout;//根layout
    private FrameLayout.LayoutParams mAtyLayoutParams;//根layout的参数

    private int mScreenWidth;//屏幕宽度
    private int mScreenHeight;//屏幕高度

    private List<String> mItemIDList;//播放节目子控件的箱子控件列表
    private HashMap<String, List<String>> mItemResList;//控件对应播放资源列表

    private HashMap<String, Integer> mItemIndexMap;//资源下标map

    private Subscription mSubscriImageTimer;

    private ImageHandler mImageHandler;

    private Timer mTimer;
    private TimerTask mTask;
    private boolean mIsDestroy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewFind.bind(this);
        PushAgent.getInstance(this).onAppStart();
        initData();
        initView();
        initEvent();
    }

    void initData() {
        FileUtils.checkAppFile();
        mItemIDList = new ArrayList<>();
        mItemIndexMap = new HashMap<>();
        mItemResList = new HashMap<>();

        mImageHandler = new ImageHandler(this);
    }

    void initView() {
////        mWebView.loadUrl("http://www.baidu.com");
//        // 开启 localStorage
//        mWebView.getSettings().setDomStorageEnabled(true);
//        // 设置支持javascript
//        mWebView.getSettings().setJavaScriptEnabled(true);
//        // 启动缓存
//        mWebView.getSettings().setAppCacheEnabled(true);
//        // 设置缓存模式
//        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
//        //使用自定义的WebViewClient
////        mWebView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.134 Safari/537.36");
//        mWebView.setWebViewClient(new WebViewClient()
//        {
//
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
////                view.loadUrl(requ);
//                return true;
//            }
//
//
//        });
//        mWebView.loadUrl("http://www.baidu.com");
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mScreenWidth = size.x;
        mScreenHeight = size.y;

        mAtyLayout = (FrameLayout) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        mAtyLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        mAtyLayout.setLayoutParams(mAtyLayoutParams);

        int width = 500;
        int height = 500;
        int marginLeft = 200;
        int marginTop = 300;
        RelativeLayout.LayoutParams itemParams = new RelativeLayout.LayoutParams(width, height);
        itemParams.setMargins(marginLeft, marginTop, 0, 0);
    }

    void initEvent() {
        getMvpPresenter().init(this);
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
        mAtyLayout.removeAllViews();
        mItemIDList.clear();
        mItemIndexMap.clear();
        mItemResList.clear();
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
                    CustomVideoView videoView = new CustomVideoView(mContext);
//                    videoView.setZOrderOnTop(true);
//                    videoView.se
//                    videoView.bringToFront();
                    videoView.setLayoutParams(itemParams);
                    mAtyLayout.addView(videoView);

                    break;
                case IMAGELAYOUT:
                    ImageView imageView = new ImageView(mContext);
//                    imageView.bringToFront();
                    imageView.setLayoutParams(itemParams);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    mAtyLayout.addView(imageView);
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
        if (itemsBeanList != null && itemsBeanList.size() > 0) {
            layoutProgramView(itemsBeanList);
        }

        List<ProgramBean.ProgramListBean.MatItemBean> matItemBeens = bean.getMatItem();
        for (String itemid : mItemIDList) {
            List<String> pathList = new ArrayList<>();
            for (ProgramBean.ProgramListBean.MatItemBean matItem : matItemBeens) {
                if (itemid.equals(matItem.getItemId())) {
                    pathList.add(matItem.getPath());
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
                            message.setData(bundle);
                            message.what = 0;
                            mImageHandler.sendMessage(message);
                            break;
                    }
                }
            }
        }
    }

    private void playVideo(final ArrayList<String> pathList, final String itemid, final int childViewIndex) {
//        final CustomVideoView videoView = (CustomVideoView) mAtyLayout.getChildAt(childViewIndex);
        if (pathList == null || pathList.size() == 0) {
            Log.e(TAG, "视频列表为空");
        } else {
            Log.e(TAG, itemid + "播放视频");
            ((CustomVideoView) mAtyLayout.getChildAt(childViewIndex)).setVisibility(View.VISIBLE);
            ((CustomVideoView) mAtyLayout.getChildAt(childViewIndex)).setVideoPath(Constant.LOCAL_PROGRAM_PATH + File.separator + pathList.get(mItemIndexMap.get(itemid)));
            ((CustomVideoView) mAtyLayout.getChildAt(childViewIndex)).getHolder().setFormat(PixelFormat.TRANSPARENT);
//            ((CustomVideoView) mAtyLayout.getChildAt(childViewIndex)).setZOrderOnTop(true);
//            ((CustomVideoView) mAtyLayout.getChildAt(childViewIndex)).setZOrderMediaOverlay(true);
            ((CustomVideoView) mAtyLayout.getChildAt(childViewIndex)).setOnPreparedListener(
                    new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            ((CustomVideoView) mAtyLayout.getChildAt(childViewIndex)).seekTo(0);
                            ((CustomVideoView) mAtyLayout.getChildAt(childViewIndex)).start();
                        }
                    }
            );

            ((CustomVideoView) mAtyLayout.getChildAt(childViewIndex)).setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    ((CustomVideoView) mAtyLayout.getChildAt(childViewIndex)).stopPlayback();
                    mp.release();
                    int currentIdx = mItemIndexMap.get(itemid) + 1;
                    if (currentIdx == pathList.size()) {
                        currentIdx = 0;
                    }
                    mItemIndexMap.put(itemid, currentIdx);
                    playVideo(pathList, itemid, childViewIndex);
                }
            });
        }
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
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
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

                if (pathList == null || pathList.size() == 0) {
                    Log.e(TAG, "图片列表为空");
                } else {
                    Log.e(TAG, itemid + "播放图片");
                    String picpath = Constant.LOCAL_PROGRAM_PATH + File.separator + pathList.get(mItemIndexMap.get(itemid));
                    GlideCacheUtil.getInstance().clearImageAllCache(MainActivity.this);
                    File file = new File(picpath);
                    Glide.with(MyApplication.getContext())
                            .load(file)
                            .override(650, 595)
                            .into((ImageView) mAtyLayout.getChildAt(childViewIndex));
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
                mIsDestroy = true;
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

    @Override
    protected void onStop() {
        super.onStop();
        Message message = new Message();
        message.what = 1;
        mImageHandler.sendMessage(message);
        MainActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Message message = new Message();
        message.what = 1;
        mImageHandler.sendMessage(message);
        MainActivity.this.finish();
    }


}
