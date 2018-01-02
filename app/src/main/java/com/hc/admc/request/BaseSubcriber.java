package com.hc.admc.request;

import rx.Subscriber;

/**
 * Created by Alex on 2017/12/21.
 * 备注:
 */

public abstract class BaseSubcriber<T> extends Subscriber<T> {
    @Override
    public void onError(Throwable e) {
        ApiException apiException=(ApiException)e;
        onError(apiException);
    }

    /**
     * 错误的一个回调
     * @param e
     */
    protected abstract void onError(ApiException e);
}
