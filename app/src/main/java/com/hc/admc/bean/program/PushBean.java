package com.hc.admc.bean.program;

import java.util.HashMap;

/**
 * Created by Alex on 2017/12/21.
 * 备注:前期接入了友盟的推送，后面移除了，现在是默认推送的类
 */

public class PushBean {

    /**
     * data : {"taskId":1,"url":"http://192.168.0.90:8080/api/get_task_config_json.do"}
     * code : 10003
     */

    private HashMap<String, String> data;
    private int code;

    public HashMap<String, String> getData() {
        return data;
    }

    public void setData(HashMap<String, String> data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "test{" +
                "data=" + data +
                ", code=" + code +
                '}';
    }
}
