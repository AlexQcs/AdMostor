package com.hc.admc.util;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Alex on 2017/12/11.
 * 备注:
 */

public class DateFormatUtils {
    /**
     * 作用:获取时间格式化字符串
     *
     * @param date
     *         需要格式化的时间
     * @param format
     *         格式化标准
     * @return 格式化完成的时间字符串
     */
    public static String date2String(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINA);
        return sdf.format(date);
    }

    /**
     * 作用:将格式化时间字符串还原为时间
     *
     * @param dateStr
     *         格式化时间字符串
     * @param format
     *         格式化标准
     * @return 时间
     */
    public static Date string2Date(String dateStr, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format,Locale.CHINA);
        Date date = new Date();
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 判断time是否在from，to之内
     *
     * @param time
     *         指定时间
     * @param from
     *         开始时间
     * @param to
     *         结束时间
     * @return 是否在时间内
     */
    public static boolean belongCalendar(Date time, Date from, Date to) {
        Calendar date = Calendar.getInstance();
        date.setTime(time);

        Calendar after = Calendar.getInstance();
        after.setTime(from);

        Calendar before = Calendar.getInstance();
        before.setTime(to);

        return date.after(after) && date.before(before);
    }

    public static void syncTime(String date) {
        if (date == null || "".equals(date)) {
            Log.e("修改系统时间", "失败");
            return;
        }
        try {
            Process process = Runtime.getRuntime().exec("su");
            Date resDate = DateFormatUtils.string2Date(date, "yyyy-MM-dd HH:mm:ss");
//            Log.e("修改系统时间", "对象"+resDate);
            String formatStr = DateFormatUtils.date2String(resDate, "yyyyMMdd.HHmmss");
            Log.e("修改系统时间", "格式化"+formatStr);
//            String formatStr = "20170926.103020"; //测试的设置的时间【时间格式 yyyyMMdd.HHmmss】
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("setprop persist.sys.timezone GMT\n");
            os.writeBytes("/system/bin/date -s " + formatStr + "\n");
            os.writeBytes("clock -w\n");
            os.writeBytes("exit\n");
            os.flush();
            Log.e("修改系统时间", "成功"+date);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
