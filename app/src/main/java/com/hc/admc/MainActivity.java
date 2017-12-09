package com.hc.admc;

import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alex.mvp.factory.CreatePresenter;
import com.alex.mvp.view.AbstractMvpActivitiy;
import com.hc.admc.bean.ScreenItemBean;
import com.hc.admc.ui.MainAtyPresenter;
import com.hc.admc.ui.MainView;
import com.hc.admc.util.FieldView;
import com.hc.admc.util.ViewFind;
import com.umeng.message.PushAgent;

import java.util.ArrayList;
import java.util.List;

@CreatePresenter(MainAtyPresenter.class)
public class MainActivity extends AbstractMvpActivitiy<MainView, MainAtyPresenter> implements MainView {

    private static final String TAG = "MainActivity";
    @FieldView(R.id.textview)
    private TextView mTextView;

    private RelativeLayout mAtyLayout;//根layout
    private RelativeLayout.LayoutParams mAtyLayoutParams;//根layout的参数

    private int mScreenWidth;//屏幕宽度
    private int mScreenHeight;//屏幕高度

    private List<View> mChildViewList;//播放节目子控件的箱子控件列表

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
        mChildViewList = new ArrayList<>();
    }

    void initView() {

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mScreenWidth = size.x;
        mScreenHeight = size.y;

        mAtyLayout = (RelativeLayout) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        mAtyLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mAtyLayout.setLayoutParams(mAtyLayoutParams);
    }

    void initEvent() {
        getMvpPresenter().initUmeng(this);
    }

    @Override
    public void receiveUmengMsg(String msg) {
        Log.e(TAG, "主页面收到友盟推送消息:" + msg);
        mTextView.setText(msg);
    }

    @Override
    public void layoutProgramView(List<ScreenItemBean> items) {
        if (items == null || items.size() == 0) {
            Log.e(TAG, "布局模板为空");
            return;
        }
        mAtyLayout.removeAllViews();
        mChildViewList.clear();
        for (ScreenItemBean item : items) {
            int width = Integer.parseInt(item.getReality_width());
            int height = Integer.parseInt(item.getReality_height());
            int marginLeft = item.getX() * mScreenWidth / 64;
            int marginTop = item.getY() * mScreenHeight / 64;
            RelativeLayout.LayoutParams itemParams = new RelativeLayout.LayoutParams(width, height);
            itemParams.setMargins(marginLeft, marginTop, 0, 0);
            FrameLayout frameLayout = new FrameLayout(this);
            frameLayout.setLayoutParams(itemParams);
            mChildViewList.add(frameLayout);
        }
    }


}
