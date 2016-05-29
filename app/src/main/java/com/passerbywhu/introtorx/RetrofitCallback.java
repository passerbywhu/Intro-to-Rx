package com.passerbywhu.introtorx;

/**
 * Created by hzwuwenchao on 2016/2/19.
 */
public abstract class RetrofitCallback<T> {
    public abstract void onSuccess(T t);
    public abstract void onFailed(Exception e);

}
