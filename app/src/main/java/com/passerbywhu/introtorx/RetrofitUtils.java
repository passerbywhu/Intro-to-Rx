package com.passerbywhu.introtorx;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hzwuwenchao on 2015/12/31.
 */
public class RetrofitUtils {
    private static final Retrofit mKaRetrofit;
    private static final API.APIService mApiService;

    static {
        mKaRetrofit = new Retrofit.Builder().client(OkHttpUtils.getRetrofitOkHttpClient()).
                baseUrl("http://i.play.163.com").
                addCallAdapterFactory(RxJavaCallAdapterFactory.create()).
                addConverterFactory(GsonConverterFactory.create()).build();
        mApiService = mKaRetrofit.create(API.APIService.class);
    }

    public static Retrofit getRetrofit() {
        return mKaRetrofit;
    }

    public static API.APIService getAPIService() {
        return mApiService;
    }
}
