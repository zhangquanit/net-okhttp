package okhttp.demo.util;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 统一设置Http Head
 * 张全
 */
public class HttpHeadInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        System.out.println("HttpHeadInterceptor----------------start");
        //统一添加Header
        request=request.newBuilder()
                .addHeader("header1","value1")
                .addHeader("header2","value2")
                .build();

        Response response = chain.proceed(request);

        System.out.println("HttpHeadInterceptor----------------end");
        return response;
    }
}
