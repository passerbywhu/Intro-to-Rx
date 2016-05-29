package com.passerbywhu.introtorx;

/**
 * Created by hzwuwenchao on 2016/2/19.
 */
public class CustomException extends Exception {
    private int mExceptionCode;

    public CustomException() {
        super();
    }

    public CustomException(String msg) {
        super(msg);
    }

    public int getExceptionCode() {
        return mExceptionCode;
    }

    public CustomException(int code) {
        super();
        this.mExceptionCode = code;
    }

    public CustomException(Throwable t) {
        super(t);
    }
}
