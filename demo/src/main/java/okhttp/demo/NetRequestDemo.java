package okhttp.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.util.http.NetClient;
import com.android.util.http.callback.ICallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.request.NetRequest;
import com.android.util.http.task.Task;

import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * 张全
 */

public class NetRequestDemo extends Activity implements View.OnClickListener {
    private Task task;
    private TextView tv_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_get).setOnClickListener(this);
        findViewById(R.id.btn_post).setOnClickListener(this);
        findViewById(R.id.btn_postForm).setOnClickListener(this);
        findViewById(R.id.btn_postFormData).setOnClickListener(this);
        findViewById(R.id.netrequest).setVisibility(View.GONE);
        tv_result = (TextView) findViewById(R.id.result);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get:
                break;
            case R.id.btn_post:
                doPost(1);
                break;
            case R.id.btn_postForm:
                break;
            case R.id.btn_postFormData:
                break;
        }
    }

    private void doPost(final int num) {
        NetRequest netRequest = new NetRequest.Builder()
                .url("https://www.baidu.com")
                .cacheControl(CacheControl.FORCE_NETWORK)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .post(RequestBody.create(MediaType.parse("application/json"), "{}"))
                .callback(new ICallback() {
                    @Override
                    public void start() {
//                        System.out.println("---------------start "+num);
                    }

                    @Override
                    public void success(Object data) {
//                        System.out.println("---------------success "+num);
                        tv_result.setText((String) data);
                    }

                    @Override
                    public void failure(NetException e) {
                        System.out.println("---------------failure " + num);
                        e.printStackTrace();
                    }

                    @Override
                    public void end() {
                        System.out.println("---------------end");
                    }
                })
//                .dataParser(new StringParser())
                .build();
        task = NetClient.getInstance().execute(netRequest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null!=task)task.stop();
    }
}
