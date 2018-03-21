package com.hc.admc.util;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.hc.admc.application.MyApplication;


/**
 * Created by alex on 2017/7/22.
 */

public class VolumeUtils {
    public static void setVolum(double direction){
        direction=15*(direction/100);
        AudioManager am=(AudioManager) MyApplication.getContext().getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, (int) direction, AudioManager.STREAM_MUSIC);
        int max =am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);//得到媒体音量的最大值
        Log.e("系统最大音量",max+"");
        int current=am.getStreamVolume(AudioManager.STREAM_MUSIC);//得到媒体音量的当前值
        Log.e("当前系统音量",current+"");
    }
}
