package okhttp.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import okhttp.demo.util.HttpHeadInterceptor;
import okhttp.demo.util.LoggingInterceptor;
import okhttp.demo.util.NetworkspaceInterceptor;
import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;

/**
 * @author 张全
 */
public class MainActivity extends Activity implements View.OnClickListener {
    private OkHttpClient okHttpClient;
    private Call call;
    private TextView tv_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_get).setOnClickListener(this);
        findViewById(R.id.btn_post).setOnClickListener(this);
        findViewById(R.id.btn_postForm).setOnClickListener(this);
        findViewById(R.id.btn_postFormData).setOnClickListener(this);
        findViewById(R.id.netrequest).setOnClickListener(this);
        tv_result = (TextView) findViewById(R.id.result);

        prepareHttpClient();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get: //Get请求
//                doGet();
                test();
                break;
            case R.id.btn_post: //Post请求
                doPost();
                break;
            case R.id.btn_postForm: //form表单请求
                doPostForm();
                break;
            case R.id.btn_postFormData: //Form表单上传文件
                doPostFormData();
                break;
            case R.id.netrequest:
                startActivity(new Intent(this, NetRequestDemo.class));
                break;
        }
    }

    private void test(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String path="http://mubu.io/";
                try {
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    Source source = Okio.source(conn.getInputStream());
                    source.timeout().timeout(10,TimeUnit.MILLISECONDS);
                    BufferedSource buffer = Okio.buffer(source);
                    String data = buffer.readUtf8();
                    System.out.println("data="+data);
                    buffer.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void prepareHttpClient(){
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5*1000, TimeUnit.MILLISECONDS) //链接超时
                .readTimeout(10*1000,TimeUnit.MILLISECONDS) //读取超时
                .writeTimeout(10*1000,TimeUnit.MILLISECONDS) //写入超时
                .addInterceptor(new HttpHeadInterceptor()) //拦截器
                .addNetworkInterceptor(new NetworkspaceInterceptor())//网络拦截器
                .addInterceptor(new LoggingInterceptor())//日志拦截器
                .authenticator(new Authenticator() {
                    @Override
                    public Request authenticate(Route route, Response response) throws IOException {
                        String auth = Credentials.basic("zhangquan", "123456");
                        Request request = response.request().newBuilder().addHeader("Authorization", auth).build();
                        return request;
                    }
                })
                .build();

//        try {
//            okHttpClient= okHttpClient.newBuilder()
//                    .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("www.baidu.com",80)))
//                    .proxyAuthenticator(new Authenticator() {
//                        @Override
//                        public Request authenticate(Route route, Response response) throws IOException {
//                            String auth = Credentials.basic("zhangquan", "123456");
//                            Request request = response.request().newBuilder().addHeader("Proxy-Authorization", auth).build();
//                            return request;
//                        }
//                    }).build();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
/*
HttpHeadInterceptor----------------start
LoggingInterceptor----------------start
NetworkspaceInterceptor----------------start
NetworkspaceInterceptor----------------end
LoggingInterceptor----------------end
HttpHeadInterceptor----------------end
 */
/**
 * 拦截器执行顺序规则
 * 1、addInterceptor比addNetworkInterceptor先执行
 * 2、同一种Interceptor倒序返回
 * HttpHeadInterceptor start----LoggingInterceptor start----LoggingInterceptor end---HttpHeadInterceptor end
  */
    }

    private void doGet() {
        String url = "http://my.csdn.net";

        //构造一个url，比如http://www.baidu.com/user/login?username=zhangsan&password=123456
/*        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme("http")
                .host("www.baidu.com")
                .addPathSegment("user")
                .addPathSegment("login")
                .addQueryParameter("username", "zhangsan")
                .addQueryParameter("password","123456")
                .build();*/

        Request request = new Request.Builder().url(url).build();
        call = okHttpClient.newCall(request);
        //-------------同步执行无法在主线程中执行,  抛出android.os.NetworkOnMainThreadException
//                try {
//                    Response response = call.execute();
//                    String result = response.body().string();
//                    System.out.println(result);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

        //------------异步执行
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("当前线程:" + Thread.currentThread().getName());
                handleResponse(response.body().string());
            }
        });
    }

    private void doPost() {
        String url = "http://my.csdn.net";
        //发送Json数据
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", 1);
            jsonObject.put("username", "zhangsan");
            jsonObject.put("password", "123456");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Request request = new Request.Builder().url(url)
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handleResponse(response.body().string());
            }
        });
/*
POST http://my.csdn.net/ HTTP/1.1
Content-Type: application/json; charset=utf-8
Content-Length: 50
Host: my.csdn.net
Connection: Keep-Alive
Accept-Encoding: gzip
User-Agent: okhttp/3.4.1

{"id":1,"username":"zhangsan","password":"123456"}
 */
    }

    /**
     * 默认的表单Post提交，Content-Type: application/x-www-form-urlencoded
     */
    private void doPostForm() {
        String url = "http://my.csdn.net";

        //构造请求体RequestBody
        RequestBody requestBody =
                new FormBody.Builder()
                        .add("username", "zhangsan")
                        .add("password", "1111111")
                        .build();
        Request req = new Request.Builder().url(url).post(requestBody).build();
        call = okHttpClient.newCall(req);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handleResponse(response.body().string());
            }
        });

        /*
            POST http://my.csdn.net/ HTTP/1.1
            Content-Type: application/x-www-form-urlencoded
            Content-Length: 34
            Host: blog.csdn.net
            Connection: Keep-Alive
            Accept-Encoding: gzip
            User-Agent: okhttp/3.4.1

            username=zhangsan&password=1111111
         */
    }

    private void doPostFormData() {
//        String url = "http://192.168.1.105:8080/springmvc/user/login.do";
        String url = "http://my.csdn.net";

        String fileName = "zhangquan.jpg";
        File file = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            file = new File(Environment.getExternalStorageDirectory(), fileName);
        }
        RequestBody uploadImg = RequestBody.create(MediaType.parse("image/jpeg"), file);

        MultipartBody uploadBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", "zhangsan")
                .addFormDataPart("password", "11111")
                .addFormDataPart("img", fileName, uploadImg)
                .build();
        Request req = new Request.Builder().url(url).post(uploadBody).build();
        call = okHttpClient.newCall(req);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handleResponse(response.body().string());
            }
        });

/*
    POST http://my.csdn.net/ HTTP/1.1
    Content-Type: multipart/form-data; boundary=85bc6cfc-ed93-42eb-b2ff-a65d1119b85d
    Content-Length: 36284
    Host: my.csdn.net
    Connection: Keep-Alive
    Accept-Encoding: gzip
    User-Agent: okhttp/3.4.1

    --85bc6cfc-ed93-42eb-b2ff-a65d1119b85d
    Content-Disposition: form-data; name="username"
    Content-Length: 8

    zhangsan
    --85bc6cfc-ed93-42eb-b2ff-a65d1119b85d
    Content-Disposition: form-data; name="password"
    Content-Length: 5

    11111
    --85bc6cfc-ed93-42eb-b2ff-a65d1119b85d
    Content-Disposition: form-data; name="img"; filename="zhangquan.jpg"
    Content-Type: image/jpeg
    Content-Length: 35842

    图片内容字节
    --85bc6cfc-ed93-42eb-b2ff-a65d1119b85d--
*/
    }


    private void handleResponse(final String response) {
        //更新到主线程
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_result.setText(response);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != call) call.cancel();
    }
}
