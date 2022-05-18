package com.mabeijianxi.smallvideo2;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;



public class WebGridActivity extends AppCompatActivity {
    private final static String TAG = "villa";

    private String url, titles;
    private boolean videoFlag = false;
    public static final String PARM = "parm";//原生头部的标题名
    private String mToken;
    private String mType;
    private WebView webView;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_activity_photo);
        webView = findViewById(R.id.webview);


        url = getIntent().getStringExtra("URL");


        webView.getSettings().setDomStorageEnabled(true);  //很关键！！！！
        initUrls();


    }


    /**
     * back=1                      固定取值，标识使用华为H5页面中的回退按钮；
     * tab=0                        固定取值，标识没有页签信息；
     * token=xx                  请求携带的token，后续用于校验请求合法性；
     * userId=xx                 随身厅用户内部ID；
     * username=xx          用户中文名称；
     * phone=xx                 用户手机号；
     * empNo=xx               用户从账号ID（CRM工号）；
     * channelCode=xx     用户组织编码；
     * source=vpclub        固定取值，标识请求来源
     * 楼宇 https://103.218.217.157/sdyd/h5/#/?position=2&back=1&tab=0&token=xx
     * &userId=xx&username=xx&phone=xx&empNo=xx&channelCode=xx&source=vpclub
     * <p>
     * https://103.218.217.157/sdyd2/html/busnessVolume/#/?token=xx&userId=xx
     * &username=xx&phone=xx&empNo=xx&channelCode=xx&source=vpclub
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void initUrls() {
        initWebView();

    }



    //初始化webView
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void initWebView() {
        //从布局文件中扩展webView  

        initWebViewSetting();
    }

    //初始化webViewSetting
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void initWebViewSetting() {
        WebSettings settings = webView.getSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDomStorageEnabled(true);
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setAllowContentAccess(true); // 是否可访问Content Provider的资源，默认值 true
        settings.setAllowFileAccess(true);    // 是否可访问本地文件，默认值 true
        // 是否允许通过file url加载的Javascript读取本地文件，默认值 false
        settings.setAllowFileAccessFromFileURLs(false);
        // 是否允许通过file url加载的Javascript读取全部资源(包括文件,http,https)，默认值 false
        settings.setAllowUniversalAccessFromFileURLs(false);
        //开启JavaScript支持
        settings.setJavaScriptEnabled(true);

        // 支持缩放
        settings.setSupportZoom(true);

        //启用数据库
        settings.setDatabaseEnabled(true);

        //设置定位的数据库路径
        String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        settings.setGeolocationDatabasePath(dir);

        //启用地理定位
        settings.setGeolocationEnabled(true);
        //辅助WebView设置处理关于页面跳转，页面请求等操作
        webView.setWebViewClient(new MyWebViewClient());
        //辅助WebView处理图片上传操作

        //加载地址
        webView.loadUrl(url);
    }

    //自定义 WebViewClient 辅助WebView设置处理关于页面跳转，页面请求等操作【处理tel协议和视频通讯请求url的拦截转发】
    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {

            // 不要使用super，否则有些手机访问不了，因为包含了一条 handler.cancel()
            // super.onReceivedSslError(view, handler, error);

            // 接受所有网站的证书，忽略SSL错误，执行访问网页
            handler.proceed();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (!TextUtils.isEmpty(url)) {
                videoFlag = url.contains("vedio");
            }
            if (url.trim().startsWith("tel")) {//特殊情况tel，调用系统的拨号软件拨号【<a href="tel:1111111111">1111111111</a>】
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            } else {
                String port = url.substring(url.lastIndexOf(":") + 1, url.lastIndexOf("/"));//尝试要拦截的视频通讯url格式(808端口)：【http://xxxx:808/?roomName】
                if (port.equals("808")) {//特殊情况【若打开的链接是视频通讯地址格式则调用系统浏览器打开】
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                } else {//其它非特殊情况全部放行
                    view.loadUrl(url);
                }
            }
            return true;
        }
    }





    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //如果按下的是回退键且历史记录里确实还有页面
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }




}  
