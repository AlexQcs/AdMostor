package com.hc.admc.util;

import com.hc.admc.Constant;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 2017/12/9.
 * 备注:
 */

public class FileUtils {
    /**
     * 用于获取文件夹下面的子文件列表
     *
     * @param dirPath
     *         目标文件夹
     * @return List<String>
     */
    public static List<String> getPathOfDirectory(String dirPath) {
        List<String> pathList = null;
        File file = new File(dirPath);
        if (file.exists()) {
            pathList = new ArrayList<String>();
            File[] tempList = file.listFiles();
            for (int i = 0; i < tempList.length; i++) {
                if (tempList[i].isFile()) {
                    pathList.add(tempList[i].getPath());
                }
            }
        }
        return pathList;
    }

    /**
     * 此方法用于:创建新文件夹
     *
     * @param path
     *         文件路径
     * @author.Alex.on.2017年6月10日
     */
    public static void folderCreate(String path) {
        File dirFirstFolder = new File(path);//方法二：通过变量文件来获取需要创建的文件夹名字
        if (!dirFirstFolder.exists()) { //如果该文件夹不存在，则进行创建
            dirFirstFolder.mkdirs();//创建文件夹]
        }
    }

    /**
     * 此方法用于:创建新文件
     *
     * @param path
     *         文件路径
     * @author.Alex.on.2017年6月10日
     */
    public static void fileCreate(String path) {
        File file = new File(path);

        if (!file.exists()) {
            try {
                file.createNewFile();
                //file is create
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static void fileDelete(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    public static void checkAppFile() {

        try {
            folderCreate(Constant.LOCAL_FILE_PATH);
            folderCreate(Constant.LOCAL_LOG_PATH);
            folderCreate(Constant.LOCAL_PROGRAM_PATH);
            folderCreate(Constant.LOCAL_PROGRAM_CFG_PATH);
            fileCreate(Constant.LOCAL_ERROR_TXT);
            fileCreate(Constant.LOCAL_CONFIG_TXT);
            fileCreate(Constant.LOCAL_PROGRAM_LIST_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
