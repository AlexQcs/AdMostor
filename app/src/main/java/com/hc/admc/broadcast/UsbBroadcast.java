package com.hc.admc.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;

import com.hc.admc.MainActivity;

/**
 * Created by alex on 2016/8/9.
 * 监听挂载U盘插入 广播接收器
 */
public class UsbBroadcast extends BroadcastReceiver {

    MainActivity mExecActivity;

    public static final int USB_STATE_MSG = 0x00020;//U盘状态
    public static final int USB_STATE_ON = 0x00021;//U盘插入
    public static final int USB_STATE_OFF = 0x00022;//U盘拔出

    public IntentFilter mFilter=new IntentFilter(); //intent过滤器

    public UsbBroadcast(Context context){
        mExecActivity=(MainActivity)context;
        mFilter.addAction(Intent.ACTION_MEDIA_CHECKING);
        mFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        mFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        mFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        mFilter.addDataScheme("file");
    }

    public Intent registerReceiver(){
        return mExecActivity.registerReceiver(this,mFilter);
    }

    public void unregistReceiver(){
        mExecActivity.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Message msg=new Message();
        msg.what=USB_STATE_MSG;
//        ||intent.getAction().equals(Intent.ACTION_MEDIA_CHECKING
        if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)){
            msg.arg1=USB_STATE_ON;
            String path=intent.getDataString();
            Bundle b=new Bundle();
            b.putString("path",path);
            msg.setData(b);
        }else {
            msg.arg1=USB_STATE_OFF;
        }

//        mExecActivity.mCopyFileHandler.sendMessage(msg);
    }

}
