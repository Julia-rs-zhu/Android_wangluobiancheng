package com.example.android_wangluobiancheng;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {
    private OkHttpClient client;
    private Button get;
    private Button post;
    private Button png;
    private TextView textView;
    private ImageView imageView;

    private Button sendfile;
    private Button downfile;

//    public static final MediaType MEDIA_TYPE_MARKDOWN
//            = MediaType.parse("text/x-markdown; charset=utf-8");        //type值是text，表示是文本这一大类；/后面的x-markdown是subtype，表示是文本这一大类下的markdown这一小类； charset=utf-8 则表示采用UTF-8编码。
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        get = (Button) this.findViewById(R.id.get);
        post = (Button) this.findViewById(R.id.post);
        png= (Button) this.findViewById(R.id.png);
//        sendfile=(Button) this.findViewById(R.id.sendfile);
        downfile=(Button) this.findViewById(R.id.downfile);
        textView=this.findViewById(R.id.textView);
        imageView=this.findViewById(R.id.imageView);
        get.setOnClickListener(this);
        post.setOnClickListener(this);
        png.setOnClickListener(this);
//        sendfile.setOnClickListener(this);
        downfile.setOnClickListener(this);
    }

    //Get异步请求
    private void getHttp() {

        client = new OkHttpClient();
        //创建Request对象
        Request.Builder requestBuilder = new Request.Builder().url("https://www.httpbin.org/json");
        requestBuilder.method("GET", null);
        Request request = requestBuilder.build();
        //执行request请求
        //异步请求
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //请求失败
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData=response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(responseData);
                    String slideshow = jsonObject.getString("slideshow");
                    JSONObject slideshowObject = new JSONObject(slideshow);
                    String date = slideshowObject.getString("date");
                    Log.d("zrs--TestGet", "web---" + date);
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("date",date);
                    message.setData(bundle);
                    message.what = 1;
                    handler.sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    Handler handler = new Handler(Looper.myLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 1:
                    String date = msg.getData().getString("date");
                    textView.setText(date);
                    break;
                case 2:
                    String origin = msg.getData().getString("origin");
                    textView.setText(origin);
                    break;
                case 3:
                    imageView.setImageDrawable((Drawable) msg.obj);
                    break;
                case 4:
                    String origin1 = msg.getData().getString("origin1");
                    textView.setText(origin1);
                    break;
                case 5:
                    imageView.setImageDrawable((Drawable) msg.obj);
                    break;
            }
            return true;
        }
    });

    //Post异步请求
    private void postHttp() {
        client = new OkHttpClient();
        //创建Request对象
        RequestBody formBody = new FormBody.Builder()
                .add("size", "10")
                .build();
        Request request = new Request.Builder()
                .url("https://www.httpbin.org/post")
                .post(formBody)
                .build();
        //执行request请求
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //请求失败
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(responseData);
                    String origin = jsonObject.getString("origin");
                    Log.d("zrs--TestPost", "web---" + origin);
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("origin",origin);
                    message.setData(bundle);
                    message.what = 2;
                    handler.sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    //Png请求
    public void pngHttp(){
        //1、创建OkHttpClient对象实例
        client = new OkHttpClient();
        //2、创建Request对象
        Request request = new Request.Builder()
                .url("https://www.httpbin.org/image/png")
                .get()
                .build();
        //3、执行Request请求
        client.newCall(request).enqueue(new Callback() {

            public void onFailure(Call call, IOException e) {
                //请求失败
            }

            public void onResponse(Call call, final Response response){
                Drawable drawable =
                        Drawable.createFromStream(response.body().byteStream(),
                                "image.png");
                Message message = new Message();
                message.obj = drawable;
                message.what = 3;
                handler.sendMessage(message);
                Log.d("zrs---downPost", "图片获取显示成功！！！" );
            }
        });
    }

//    /**
//     * 异步上传文件
//     */
//    private void postFile() {
//        client = new OkHttpClient();
//        File file = new File("/sdcard/Download/Android_wangluobiancheng/app/src/main/res/raw/xunm.png");
//        //创建Request对象
//        Request request = new Request.Builder()
//                .url("https://www.httpbin.org/")
//                .post(RequestBody.create(MEDIA_TYPE_PNG, file))
//                .build();
//        //执行request请求
//        Call call = client.newCall(request);
//        call.enqueue(new Callback()  {
//            @Override
//            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
//                String responseData = response.body().string();
//                try {
//                    JSONObject jsonObject = new JSONObject(responseData);
//                    String origin = jsonObject.getString("origin1");
//                    Log.d("zhuruisha--Testsendfile", "web---" + origin);
//                    Message message = new Message();
//                    Bundle bundle = new Bundle();
//                    bundle.putString("origin1",origin);
//                    message.setData(bundle);
//                    message.what = 2;
//                    handler.sendMessage(message);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//            @Override
//            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
//            }
//        });
//    }

    /**
     * 异步下载文件
     */
    private void downFile() {
        client = new OkHttpClient();
        String url = "https://bpic.588ku.com/art_water_pic/21/02/02/0f14abe5decb8310ef3e9a302f31219d.jpg";
        //创建Request对象
        Request request = new Request.Builder().url(url).build();
        //执行request请求
        Call call = client.newCall(request);
        call.enqueue(new Callback()  {
            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
                InputStream inputStream = response.body().byteStream();
                FileOutputStream fileOutputStream = null;
                try {
                    int REQUEST_EXTERNAL_STORAGE = 1;
                    String[] PERMISSIONS_STORAGE = {
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    };
                    int permission = ActivityCompat.checkSelfPermission(MainActivity2.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                    if (permission != PackageManager.PERMISSION_GRANTED) {
                        // We don't have permission so prompt the user
                        ActivityCompat.requestPermissions(
                                MainActivity2.this,
                                PERMISSIONS_STORAGE,
                                REQUEST_EXTERNAL_STORAGE
                        );
                    }
//                    Drawable drawable =
//                            Drawable.createFromStream(response.body().byteStream(),
//                                    "zrs2.jpg");
//                    Message message = new Message();
//                    message.obj = drawable;
//                    message.what = 5;
//                    handler.sendMessage(message);

                    fileOutputStream = new FileOutputStream(new File("/sdcard/Download/zrs2.jpg"));
                    byte[] buffer = new byte[2048];
                    int len = 0;
                    while ((len = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, len);
                    }
                    fileOutputStream.flush();

                    Log.d("zrs---downPost", "图片下载成功！！！" );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {

            }

        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.get:
                getHttp();
                break;
            case R.id.post:
                postHttp();
                break;
            case R.id.png:
                pngHttp();
                break;
//            case R.id.sendfile:
//                postFile();
//                break;
            case R.id.downfile:
                downFile();
                break;

        }
    }
}