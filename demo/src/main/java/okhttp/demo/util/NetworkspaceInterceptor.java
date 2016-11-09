package okhttp.demo.util;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 张全
 */

public class NetworkspaceInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        System.out.println("NetworkspaceInterceptor----------------start");
        Response response = chain.proceed(request);
        System.out.println("NetworkspaceInterceptor----------------end");
        return response;
    }
}
