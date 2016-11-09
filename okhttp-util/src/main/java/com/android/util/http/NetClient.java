package com.android.util.http;

import android.os.Handler;
import android.os.Looper;

import com.android.util.http.callback.ICallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.request.NetRequest;
import com.android.util.http.task.Task;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 张全
 */

public class NetClient {
    private static NetClient mInstance = null;
    private OkHttpClient mHttpClient;
    private Handler mDelivery;
    public static boolean LOGABLE=true;

    /**
     * 日志拦截器
     */
    static class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Request request = chain.request();
            long t1 = System.nanoTime();
            System.out.println(String.format("Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()));

            Response response = chain.proceed(request);

            long t2 = System.nanoTime();
            System.out.println(String.format("Received response for %s in %.1fms%n%s",
                    response.request().url(), (t2 - t1) / 1e6d, response.headers()));

            return response;
        }
    }
    private NetClient() {
        OkHttpClient.Builder builder= new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS);
        if(LOGABLE){
            builder.addInterceptor(new LoggingInterceptor());
        }
        mHttpClient = builder.build();
        mDelivery = new Handler(Looper.getMainLooper());
    }

    public static NetClient getInstance() {
        if (null == mInstance) {
            synchronized (NetClient.class) {
                if (null == mInstance) {
                    mInstance = new NetClient();
                }
            }
        }
        return mInstance;
    }


    public Task execute(final NetRequest request) {
        OkHttpClient httpClient=mHttpClient;
        OkHttpClient.Builder builder = null;

        //连接超时
        long connectTimeout = request.connectTimeout();
        if (connectTimeout > 0) {
            if (null == builder) builder = httpClient.newBuilder();
            builder.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
        }
        //读取数据超时
        long readTimeout = request.readTimeout();
        if (readTimeout > 0) {
            if (null == builder) builder = httpClient.newBuilder();
            builder.readTimeout(readTimeout,TimeUnit.MILLISECONDS);
        }
        //发送数据超时
        long writeTimeout = request.writeTimeout();
        if(writeTimeout>0){
            if (null == builder) builder = httpClient.newBuilder();
            builder.writeTimeout(writeTimeout,TimeUnit.MILLISECONDS);
        }
        if(null!=builder){
            httpClient=builder.build();
        }
        Call call = httpClient.newCall(request.getRealRequest());
        request.getCallback().start();
        call.enqueue(new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                if(call.isCanceled()){
                    //call.cancel也会回调onFailure()
                    return;
                }
                postFailResult(request.getCallback(), call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(call.isCanceled()){
                    return;
                }

                if(response.isSuccessful()){
                    try {
                        Response cloneResponse = response.newBuilder().build();
                        request.setResponse(cloneResponse);
                        Object o = request.parseResponse(response);
                        postSuccessResult(request.getCallback(), call, o);
                    } catch (Exception e) {
                        postFailResult(request.getCallback(), call, e);
                    }
                }else{
                    try {
                        postFailResult(request.getCallback(), call, new RuntimeException(response.body().string()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return new Task(call, request);
    }

    private void postFailResult(final ICallback callback, final Call call, final Exception e) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (null != call) callback.failure(new NetException(e));
                if (null != call) callback.end();
            }
        });
    }

    private void postSuccessResult(final ICallback callback, final Call call, final Object o) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (null != call) callback.success(o);
                if (null != call) callback.end();
            }
        });
    }
}
