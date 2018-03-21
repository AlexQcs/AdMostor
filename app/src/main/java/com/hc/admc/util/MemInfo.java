package com.hc.admc.util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.RequiresApi;

import com.hc.admc.application.MyApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by alex on 2017/7/22.
 */

public class MemInfo {

    //得到外部储存sdcard的状态
    public static String sdcard = Environment.getExternalStorageState();
    //外部储存sdcard存在的情况
    public static String state = Environment.MEDIA_MOUNTED;
    //获取Sdcard的路径

    //获得路径
    public static File file = Environment.getExternalStorageDirectory();

    public static StatFs statFs;

    /**
     * SDCard 总容量大小
     *
     * @return MB
     */
    public static long getTotalSize() {
        statFs = new StatFs(file.getPath());
        if (sdcard.equals(state)) {
            //获得sdcard上 block的总数
            long blockCount = statFs.getBlockCount();
            //获得sdcard上每个block 的大小
            long blockSize = statFs.getBlockSize();
            //计算标准大小使用：1024，当然使用1000也可以
            long bookTotalSize = blockCount * blockSize / 1000 / 1000;
            return bookTotalSize;

        } else {
            return -1;
        }

    }

    /**
     * 计算Sdcard的剩余大小
     * @return MB
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static long getAvailableSize()
    {
        statFs = new StatFs(file.getPath());
        if(sdcard.equals(state))
        {
            //获得Sdcard上每个block的size
            long blockSize=statFs.getBlockSizeLong();
            //获取可供程序使用的Block数量
            long blockavailable=statFs.getAvailableBlocksLong();
            //计算标准大小使用：1024，当然使用1000也可以
            long blockavailableTotal=blockSize*blockavailable/1024/1024;
            return blockavailableTotal;
        }else
        {
            return -1;
        }
    }


    // 获得可用的内存
    public static long getmem_UNUSED() {
        long MEM_UNUSED = 0;
        // 得到ActivityManager
        ActivityManager am = (ActivityManager) MyApplication.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        // 创建ActivityManager.MemoryInfo对象
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        // 取得剩余的内存空间
        MEM_UNUSED = mi.availMem / 1024 / 1024;
        return MEM_UNUSED;
    }


    // 获得总内存
    public static long getmem_TOLAL() {
        long mTotal;
        // /proc/meminfo读出的内核信息进行解释
        String path = "/proc/meminfo";
        String content = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(path), 8);
            String line;
            if ((line = br.readLine()) != null) {
                content = line;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // beginIndex
        int begin = content.indexOf(':');
        // endIndex
        int end = content.indexOf('k');
        // 截取字符串信息  content = content.substring(begin + 1, end).trim();
        mTotal = Integer.parseInt(content);
        return mTotal;
    }
}
