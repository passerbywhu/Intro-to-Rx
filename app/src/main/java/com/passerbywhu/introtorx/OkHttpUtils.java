package com.passerbywhu.introtorx;

import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by hzwuwenchao on 2015/12/28.
 */
public class OkHttpUtils {
    private static final OkHttpClient mOkHttpClient;
    private static final String CHARSET_NAME = "utf-8";
    private static final int CACHE_SIZE = 10 * 1024 * 1024; //10MB
    private static final int CONNECT_TIME_OUT = 30;
    private static final int WRITE_TIME_OUT = 30;
    private static final int READ_TIME_OUT = 30;
    public static final String MEDIA_TYPE_MARKDOWN = "text/x-markdown; charset=" + CHARSET_NAME;
    public static final String MEDIA_TYPE_PNG = "image/png";
    public static final String MEDIA_TYPE_BINARY = "application/octet-stream";

    public static OkHttpClient getRetrofitOkHttpClient() {
        return mOkHttpClient.newBuilder().build();
    }

    public static OkHttpClient getClient() {
        return mOkHttpClient;
    }

    static {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        mOkHttpClient = new OkHttpClient.Builder().  //还需要加拦截器
                connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS).
                writeTimeout(WRITE_TIME_OUT, TimeUnit. SECONDS).
                readTimeout(READ_TIME_OUT, TimeUnit.SECONDS).
                retryOnConnectionFailure(true).
                addInterceptor(loggingInterceptor).
//                cookieJar(IplayCookieJar.getInstance()).
                build();
    }

    public static Response doGetSync(String url, List<Map.Entry<String, String>> params) throws IOException {
        return doGetSync(url, params, null, false);
    }

    public static Response doGetSync(String url, List<Map.Entry<String, String>> params, boolean cache) throws IOException {
        return doGetSync(url, params, null, cache);
    }

    public static Response doGetSync(String url, List<Map.Entry<String, String>> params, List<Map.Entry<String, String>> headers, boolean cache) throws IOException {
        if (TextUtils.isEmpty(url)) {
            return null;
        }

        Response response = makeGetCall(url, params, headers, cache).execute();
        return response;
    }

    public static void doGetAsync(String url, List<Map.Entry<String, String>> params,Callback callback) {
        doGetAsync(url, params, null, false, callback);
    }

    public static void doGetAsync(String url, List<Map.Entry<String, String>> params, boolean cache, Callback callback) {
       doGetAsync(url, params, null, cache, callback);
    }

    public static void doGetAsync(String url, List<Map.Entry<String, String>> params, List<Map.Entry<String, String>> headers,boolean cache,  Callback callback) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Call call = makeGetCall(url, params, headers, cache);
        if (callback == null) {
            callback = new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                }
            };
        }
        call.enqueue(callback);
    }

    private static Call makeGetCall(String url, List<Map.Entry<String, String>> params, List<Map.Entry<String, String>> headers, boolean cache) {
        String finalUrl = attachParams(url, params);
        Request.Builder requestBuilder = new Request.Builder();
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> header : headers) {
                String name = header.getKey();
                String value = header.getValue();
                requestBuilder.addHeader(name, value);
            }
        }
        if (!cache) {
            requestBuilder.cacheControl(CacheControl.FORCE_NETWORK);
        } else {
            //TODO
        }
        Request request = requestBuilder.url(finalUrl).get().build();
        Call call = mOkHttpClient.newCall(request);
        return call;
    }

    public static Response doPostSync(String url, List<Map.Entry<String, String>> params) throws IOException {
        return doPostSync(url, params, null, false);
    }

    public static Response doPostSync(String url, List<Map.Entry<String, String>> params, boolean cache) throws IOException {
        return doPostSync(url, params, null, cache);
    }

    public static Response doPostSync(String url, List<Map.Entry<String, String>> params, List<Map.Entry<String, String>> headers, boolean cache) throws IOException {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        Response response = makePostCall(url, params, headers, cache).execute();
        return response;
    }

    public static void doPostAsync(String url, List<Map.Entry<String, String>> params, Callback callback) {
        doPostAsync(url, params, null, false, callback);
    }

    public static void doPostAsync(String url, List<Map.Entry<String, String>> params, boolean cache, Callback callback) {
        doPostAsync(url, params, null, cache, callback);
    }

    public static void doPostAsync(String url, List<Map.Entry<String, String>> params, List<Map.Entry<String, String>> headers, Callback callback) {
        doPostAsync(url, params, headers, false, callback);
    }
    public static void doPostAsync(String url, List<Map.Entry<String, String>> params, List<Map.Entry<String, String>> headers, boolean cache, Callback callback) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (callback == null) {
            callback = new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                }
            };
        }
        Call call = makePostCall(url, params, headers, cache);
        call.enqueue(callback);
    }

    private static Call makePostCall(String url, List<Map.Entry<String, String>> params, List<Map.Entry<String, String>> headers, boolean cache) {
        FormBody formBody = null;
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> param : params) {
                String key = param.getKey();
                String value = param.getValue();
                if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                    formBodyBuilder.add(param.getKey(), param.getValue());
                }
            }
        }
        formBody = formBodyBuilder.build();
        Request.Builder requestBuilder = new Request.Builder().url(url);
        if (!cache) {
            requestBuilder.cacheControl(CacheControl.FORCE_NETWORK);
        } else {
            //TODO
        }
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> header : headers) {
                String name = header.getKey();
                String value = header.getValue();
                requestBuilder.addHeader(name, value);
            }
        }
        //TODO agent放到interceptor中处理。
//        if (url.contains(Requests.comment_insert.getUrl())) {
            requestBuilder.addHeader("User-Agent", "iplay_app_android;");
//        }
        requestBuilder.post(formBody);
        Request request = requestBuilder.build();
        Call call = mOkHttpClient.newCall(request);
        return call;
    }

    public static Response uploadFile(String url, File file) throws IOException {
        if (TextUtils.isEmpty(url) || file == null || !file.exists() || file.isDirectory()) {
            return null;
        }
        Request.Builder requestBuilder = new Request.Builder();
        RequestBody requestBody = RequestBody.create(MediaType.parse(MEDIA_TYPE_BINARY), file);
        requestBuilder.url(url).post(requestBody);
        Request request = requestBuilder.build();
        Call call = mOkHttpClient.newCall(request);
        Response response = call.execute();
        if (response.isSuccessful()) {
            return response;
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    public static Response multiPart(String url, List<Map.Entry<String, String>> params, List<Map.Entry<String, File>> files, Callback callback) throws IOException {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder();
        multipartBuilder.setType(MultipartBody.FORM);
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> param : params) {
                String key = param.getKey();
                String value = param.getValue();
                if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                    multipartBuilder.addFormDataPart(key, value);
                }
            }
        }
        if (files != null && !files.isEmpty()) {
            for (Map.Entry<String, File> fileInfo : files) {
                String fileKey = fileInfo.getKey();
                File file = fileInfo.getValue();
                if (TextUtils.isEmpty(fileKey) || file == null || !file.exists() || file.isDirectory()) {
                    continue;
                }
                String fileName = file.getName();
                multipartBuilder.addFormDataPart(fileKey, fileName, RequestBody.create(guessMimeType(fileName), file));
            }
        }
        RequestBody body = multipartBuilder.build();
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url).post(body);
        Request request = requestBuilder.build();
        Call call = mOkHttpClient.newCall(request);
        if (callback == null) {
            callback = new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                }
            };
            return call.execute();
        } else {
            call.enqueue(callback);
        }
        return null;
    }

    public static String attachParams(String url, List<Map.Entry<String, String>> params) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        if (params == null || params.isEmpty()) {
            return url;
        }
        if (url.contains("?")) {
            url += "&";
        } else {
            url += "?";
        }
        for (Map.Entry<String, String> param : params) {
            String key = param.getKey();
            String value = param.getValue();
            if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                url += key + "=" + value + "&";
            }
        }
        if (url.endsWith("&")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

    private static MediaType guessMimeType(String fileName) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(fileName);
        if (TextUtils.isEmpty(contentTypeFor)) {
            contentTypeFor = MEDIA_TYPE_BINARY;
        }
        return MediaType.parse(contentTypeFor);
    }

}
