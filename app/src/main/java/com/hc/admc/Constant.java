package com.hc.admc;

import android.os.Environment;

/**
 * Created by Alex on 2017/12/9.
 * 备注:
 */

public class Constant {
    //根目录app文件夹
    public static final String LOCAL_FILE_PATH = Environment.getExternalStorageDirectory() + "/HCAdSoft";
    //升级文件保存文件夹
    public static final String LOCAL_APK_PATH = LOCAL_FILE_PATH + "/apk";
    //保存生成的日志文件夹
    public static final String LOCAL_LOG_PATH = LOCAL_FILE_PATH + "/log";
    //播放资源文件夹
    public static final String LOCAL_PROGRAM_PATH = LOCAL_FILE_PATH + "/program";
    //保存错误日志文件夹
    public static final String LOCAL_ERROR_TXT = LOCAL_LOG_PATH + "/error.txt";
    //系统配置文件夹
    public static final String LOCAL_CONFIG_TXT = LOCAL_FILE_PATH + "/config.txt";
    //********
    public static final String LOCAL_PROGRAM_NORMAL_TXT = LOCAL_PROGRAM_PATH + "/normal.txt";
    //********
    public static final String LOCAL_PROGRAM_INTER_TXT = LOCAL_PROGRAM_PATH + "/inter.txt";
    //播放资源配置文件夹
    public static final String LOCAL_PROGRAM_CFG_PATH = LOCAL_FILE_PATH + "/programCfg";
    //播放节目单
    public static final String LOCAL_PROGRAM_LIST_PATH = LOCAL_PROGRAM_CFG_PATH + "/program.txt";
}
