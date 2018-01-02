package com.hc.admc.request;

/**
 * Created by Alex on 2017/12/21.
 * 备注:
 */

public class ApiException extends RuntimeException {
    public int code;
    public String message;

    public ApiException(Throwable throwable,int code){
        super(throwable);
        this.code=code;
    }
}
