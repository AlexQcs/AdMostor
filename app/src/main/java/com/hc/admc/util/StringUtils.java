package com.hc.admc.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Alex on 2017/12/8.
 * 备注:字符串工具类
 */

public class StringUtils {
    // 将字符串覆盖写入到文本文件中
    public static void coverTxtToFile(String strcontent, String filePath) throws IOException {
        FileWriter fw = null;
        BufferedWriter bw = null;

        try {
            fw = new FileWriter(filePath, false);
            bw = new BufferedWriter(fw);
            bw.write(strcontent);
            bw.flush();
            bw.close();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                bw.close();
                fw.close();
            } catch (IOException e1) {

            }
        }


    }

    //读取txt文件内容
    public static String getStringFromTxT(String path) {
        String str = "";
        File urlFile = new File(path);
        if (!urlFile.exists()) return str;
        InputStreamReader isr = null;
        try {
            isr = new InputStreamReader(new FileInputStream(urlFile), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String mimeTypeLine = null;
            while ((mimeTypeLine = br.readLine()) != null) {
                str = str + mimeTypeLine;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }



}
