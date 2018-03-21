package com.hc.admc.ui;

import com.alex.mvp.view.BaseMvpView;
import com.hc.admc.bean.program.ProgramBean;

/**
 * Created by Alex on 2017/12/8.
 * 备注:
 */

public interface MainView extends BaseMvpView {
    void receiveUmengMsg(String msg);
    //播放节目
    void playProgram(ProgramBean.ProgramListBean bean);
    //在线
    void online();
    //离线
    void offline();
    //状态异常
    void errline();
    void downloadNot();
    void downloadNow();
    void downloadErr();
    //设置开关机
    void setpoweronoff(String timeonStr,String timeoffStr,boolean enable);
    //设置系统时间
    void settime();
    //立即关机
    void shutdown();
    //重启板卡
    void reboot();
    //开关背光
    void backlight(boolean isOn);
    //设置音量
    void setvolume(int volume);
    //隐藏导航栏
    void hidebar();
    //显示导航栏
    void showbar();
    //恢复出厂系统
    void recovery();
    //截屏
    void screenshot();

}
