package com.alex.mvp.factory;


import com.alex.mvp.presenter.BaseMvpPresenter;
import com.alex.mvp.view.BaseMvpView;

/**
 * @author alex
 * @date 2017/11/17
 * @description Presenter工厂接口
 */
public interface PresenterMvpFactory<V extends BaseMvpView,P extends BaseMvpPresenter<V>> {

    /**
     * 创建Presenter的接口方法
     * @return 需要创建的Presenter
     */
    P createMvpPresenter();
}
