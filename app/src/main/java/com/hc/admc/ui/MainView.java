package com.hc.admc.ui;

import com.alex.mvp.view.BaseMvpView;
import com.hc.admc.bean.ScreenItemBean;

import java.util.List;

/**
 * Created by Alex on 2017/12/8.
 * 备注:
 */

public interface MainView extends BaseMvpView {
    void receiveUmengMsg(String msg);
    void layoutProgramView(List<ScreenItemBean> items);
}
