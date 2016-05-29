package com.passerbywhu.introtorx;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by hzwuwenchao on 2015/12/28.
 */
public class API {
    //缓存有效期为两个星期
    public static volatile Set<Request> cacheRequests = new HashSet<>();  //有个问题是多个线程同时请求同一个接口会有问题

    public interface APIService {
        @GET("http://i.play.163.com/news/topicOrderSource/list")
        Observable<KaResponse<List<JingXuanEntity>>> getJingxuanCategory();
    }

    public static <T> void enqueue(Call<KaResponse<T>> call, final RetrofitCallback<T> callback) {
        call.enqueue(new Callback<KaResponse<T>>() {
            @Override
            public void onResponse(Call<KaResponse<T>> call, Response<KaResponse<T>> response) {
                try {
                    if (response.isSuccessful()) {
                        final KaResponse<T> kaResponse = response.body();
                        if (kaResponse == null) {
                            throw new CustomException();
                        }
                        int code = kaResponse.code;
                        if (code == KaResponse.SUCCESS) {   //网络请求成功
                            if (callback != null) {
                                MyApplication.postRunnable(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onSuccess(kaResponse.info);
                                    }
                                });
                            }
                        } else {  //网络请求成功，但是服务端返回something wrong
                            parseCode(code);
                            throw new CustomException();
                        }
                    } else {
                        throw new CustomException();
                    }
                } catch (CustomException e) {
                    e.printStackTrace();
                    onFailed(e);
                }
            }

            @Override
            public void onFailure(Call<KaResponse<T>> call, Throwable t) {
                onFailed(new CustomException(t));
            }

            private void onFailed(final CustomException e) {
                if (callback != null) {
                    MyApplication.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailed(e);
                        }
                    });
                }
            }
        });
    }

    public static <T> T execute(Call<KaResponse<T>> call, boolean fromCache) throws CustomException {
        if (call != null) {
            try {
                if (fromCache) {
                    synchronized (cacheRequests) {
                        cacheRequests.add(call.request());
                    }
                }
                Response<KaResponse<T>> response = call.execute();
                if (response.isSuccessful()) { // code is between 200 and 300
                    KaResponse<T> kaResponse = response.body();
                    if(kaResponse == null) {
                        throw new CustomException();
                    }
                    int code = kaResponse.code; //接口返回的code
                    if (code == KaResponse.SUCCESS) {
                        return kaResponse.info;
                    } else {
                        parseCode(code);
                    }
                } else { //请求不正常
                    throw new IOException("request error");
                }
            } catch (Exception e) {  //网络错误或者解析错误
                e.printStackTrace();
                throw new CustomException();
            }
        }
        return null;
    }

    public static <T> T execute(Call<KaResponse<T>> call) throws CustomException {
        return execute(call, false);
    }

    private static void parseCode(int code) {
        switch (code) {
            default:
                break;
        }
    }
}
