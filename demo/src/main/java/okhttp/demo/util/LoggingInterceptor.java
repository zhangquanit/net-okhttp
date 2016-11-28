package okhttp.demo.util;

import com.socks.library.KLog;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 日志拦截器
 */
public class LoggingInterceptor implements Interceptor {
  private static final String TAG="Okhttp";
  @Override public Response intercept(Interceptor.Chain chain) throws IOException {
    Request request = chain.request();
    System.out.println("LoggingInterceptor----------------start");

    long t1 = System.nanoTime();
   KLog.d(TAG,String.format("Sending request %s on %s%n%s",
        request.url(), chain.connection(), request.headers()));

    Response response = chain.proceed(request);
    System.out.println("LoggingInterceptor----------------end");
    long t2 = System.nanoTime();
    KLog.d(TAG,String.format("Received response for %s in %.1fms%n%s",
        response.request().url(), (t2 - t1) / 1e6d, response.headers()));

    MediaType mediaType = response.body().contentType();
    String content = response.body().string();
    KLog.d(TAG,content);

    response=response.newBuilder()
                    .body(ResponseBody.create(mediaType,content))
                    .build();
    return response;
  }
}