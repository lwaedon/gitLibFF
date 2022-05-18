package com.mabeijianxi.smallvideo2;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;

import com.mabeijianxi.smallvideo2.obj.FileUtils;
import com.mabeijianxi.smallvideorecord2.DeviceUtils;
import com.mabeijianxi.smallvideorecord2.JianXiCamera;
import com.mabeijianxi.smallvideorecord2.LocalMediaCompress;
import com.mabeijianxi.smallvideorecord2.MediaRecorderActivity;
import com.mabeijianxi.smallvideorecord2.StringUtils;
import com.mabeijianxi.smallvideorecord2.model.AutoVBRMode;
import com.mabeijianxi.smallvideorecord2.model.BaseMediaBitrateConfig;
import com.mabeijianxi.smallvideorecord2.model.LocalMediaConfig;
import com.mabeijianxi.smallvideorecord2.model.MediaRecorderConfig;
import com.mabeijianxi.smallvideorecord2.model.OnlyCompressOverBean;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.functions.Consumer;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.mabeijianxi.smallvideo2.OkManager.JSON;


public class LanchActivity extends AppCompatActivity {

    private YinxiaoSnWgAdapter yinxiaoSnWgAdapter;
    private RecyclerView recycleView;
    private TextView tv_show;
    private ImageView img_vedio, iv_jump;
    private LinearLayout ll_yasuo;
    private int CHOOSE_CODE = 0x000520;
    private String phone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lanch);
        initSmallVideo();
        recycleView = (RecyclerView) findViewById(R.id.rv_show);
        tv_show = (TextView) findViewById(R.id.tv_show);
        img_vedio = (ImageView) findViewById(R.id.img_vedio);
        iv_jump = (ImageView) findViewById(R.id.iv_jump);
        ll_yasuo = (LinearLayout) findViewById(R.id.ll_yasuo);
        phone = getIntent().getStringExtra("phone");
        yinxiaoSnWgAdapter = new YinxiaoSnWgAdapter();
        recycleView.setLayoutManager(new LinearLayoutManager(LanchActivity.this));
        recycleView.setAdapter(yinxiaoSnWgAdapter);


        yinxiaoSnWgAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                ImageView iv_share = view.findViewById(R.id.iv_share);
                MediaBean obj = (MediaBean) adapter.getData().get(position);
                if (view.getId() == R.id.ll_show) {
                    setShareWindown(iv_share, obj);
                } else if (view.getId() == R.id.ima_show) {
                    startActivity(new Intent(LanchActivity.this, VideoPlayerActivity.class).putExtra(
                            "path", obj.getPath()));
                }

            }
        });
        img_vedio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaRecorderConfig config = new MediaRecorderConfig.Buidler()
                        .fullScreen(false)
                        .recordTimeMax(1200000)
                        .recordTimeMin(3000)
                        .goHome(true)
                        .maxFrameRate(5)
                        .videoBitrate(580000)
                        .captureThumbnailsTime(1)
                        .build();
                MediaRecorderActivity.goSmallVideoRecorder(LanchActivity.this, LanchActivity.class.getName(), config);
            }
        });
        ll_yasuo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choose();
            }
        });
        iv_jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LanchActivity.this, WebGridActivity.class).putExtra("URL", "https://testsd.cicd.vpclub.cn/sxctt/meeting/video/check/#/meetingVideo/page?telNum=" + phone));
            }
        });
        updateLevel();
    }

    public static void initSmallVideo() {
        // 设置拍摄视频缓存路径
        File dcim = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (DeviceUtils.isZte()) {
            if (dcim.exists()) {
                JianXiCamera.setVideoCachePath(dcim + "/tietongshiping/");
            } else {
                JianXiCamera.setVideoCachePath(dcim.getPath().replace("/sdcard/",
                        "/sdcard-ext/")
                        + "/tietongshiping/");
            }
        } else {
            JianXiCamera.setVideoCachePath(dcim + "/tietongshiping/");
        }
        // 初始化拍摄
        JianXiCamera.initialize(false, null);
    }

    private void obtian() {
        ArrayList<MediaBean> allDataFileName = getAllDataFileName(JianXiCamera.getVideoCachePath());
        tv_show.setText("共" + allDataFileName.size() + "个视频");
        for (int i = 0; i < allDataFileName.size(); i++) {
            MediaBean mediaBean = allDataFileName.get(i);
            int localVideoDuration = FileUtil.getLocalVideoDuration(mediaBean.getPath());
            String autoFileOrFilesSize = FileUtil.getAutoFileOrFilesSize(mediaBean.getPath());
            mediaBean.setSize(autoFileOrFilesSize);
            mediaBean.setDuration(localVideoDuration);


        }
        yinxiaoSnWgAdapter.setNewData(allDataFileName);
    }


    public ArrayList<MediaBean> getAllDataFileName(String folderPath) {
        ArrayList<MediaBean> fileList = new ArrayList<>();
        File file = new File(folderPath);
        File[] tempList = file.listFiles();
        for (int i = 0; i < tempList.length; i++) {

            if (!tempList[i].isFile()) {

                File fileSingle = tempList[i];
                File[] files = fileSingle.listFiles();
                if (files.length >= 2) {
                    MediaBean mediaBean = new MediaBean();
                    for (int j = 0; j < files.length; j++) {
                        if (files[j].isFile()) {
                            String fileName = files[j].getName();

                            if (fileName.endsWith(".jpg")) {    //  根据自己的需要进行类型筛选
                                mediaBean.setThumbPath(folderPath + "/" + fileSingle.getName() + "/" + fileName);
                            }
                            if (fileName.endsWith(".mp4")) {    //  根据自己的需要进行类型筛选
                                mediaBean.setPath(folderPath + "/" + fileSingle.getName() + "/" + fileName);
                                mediaBean.setAbsolutePath(folderPath + "/" + fileSingle.getName());
                                mediaBean.setName(fileName);
                            }

                        }


                    }
                    if (!TextUtils.isEmpty(mediaBean.getPath())) {
                        fileList.add(mediaBean);
                    }
                }
            }


        }

        return fileList;
    }

    private PopupWindow popupWindow;

    private void setShareWindown(ImageView iv_share, final MediaBean obj) {
        View view = LayoutInflater.from(this).inflate(R.layout.popwindown_layout, null, false);
        LinearLayout ll_goup = view.findViewById(R.id.ll_goup);


        popupWindow = new PopupWindow(view, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAsDropDown(iv_share, 0, -10);
        view.findViewById(R.id.tv_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showgGrabSingleCompleteObj(obj);
                popupWindow.dismiss();
            }
        });
        view.findViewById(R.id.tv_rename).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showgGrabSingleComplete(obj);


            }
        });
        view.findViewById(R.id.ll_goup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                up(obj.getPath());
                popupWindow.dismiss();
            }
        });
        if (!isUp) {
            ll_goup.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideProgress();
    }

    private void up(String path) {
        File file = new File(path);
        OkManager manager = OkManager.getInstance();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
//                .addFormDataPart("convertTo", "mp4") // 提交内容字段
                .addFormDataPart("fileData", file.getName(), RequestBody.create(MediaType.parse("*/*"), new File(path))) // 第一个参数传到服务器的字段名，第二个你自己的文件名，第三个MediaType.parse("*/*")和我们之前说的那个type其实是一样的
                .build();
        showProgress("", "上传中...", -1);
        manager.postFile("https://testsd.cicd.vpclub.cn/sxctt/uploader/file/upload", requestBody, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseBody = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(responseBody);
                    String returnCode = jsonObject.getString("returnCode");
                    if (TextUtils.equals("1000", returnCode)) {
                        JSONObject dataInfo = jsonObject.getJSONObject("dataInfo");
                        updateUD(dataInfo.getString("url"), dataInfo.getString("fileId"));
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideProgress();

                                // 对返回结果进行操作
                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();


                }


            }

        });
    }

    private boolean isUp = false;


    private void showToast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }
    private void updateUD(String url, String fileId) {
        JSONObject json = new JSONObject();
        OkManager manager = OkManager.getInstance();
        try {
            json.put("loginUserId", phone);
            json.put("fileId", fileId);
            json.put("url", url);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(JSON, String.valueOf(json));
        manager.postJson("https://testsd.cicd.vpclub.cn/sxctt/check/violation/app/video/upload/info/add", requestBody, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        // 对返回结果进行操作
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        showToast("上传成功");
                        // 对返回结果进行操作
                    }
                });

            }
        });
    }


    private void updateLevel() {
        JSONObject json = new JSONObject();
        OkManager manager = OkManager.getInstance();
        try {
            json.put("loginUserId", phone);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(JSON, String.valueOf(json));
        manager.postJson("https://testsd.cicd.vpclub.cn/sxctt/check/violation/app/video/upload/info/checkAdd", requestBody, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String responseBody = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(responseBody);
                    String returnCode = jsonObject.getString("returnCode");
                    if (TextUtils.equals("1000", returnCode)) {
                        isUp = true;
                    } else {

                    }

                } catch (JSONException e) {
                    e.printStackTrace();


                }
            }
        });
    }

    public void choose() {
        Intent it = new Intent(Intent.ACTION_GET_CONTENT,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        it.setDataAndType(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "video/*");
        startActivityForResult(it, CHOOSE_CODE);

    }

    private AlertDialog mAlertDialog_loading, mAlertDialog;

    void showgGrabSingleComplete(final MediaBean obj) {
        View inflate = LayoutInflater.from(this).inflate(R.layout
                .dialog_grab_feedback, null, false);
        final TextView tv_name = (TextView) inflate.findViewById(R.id.tv_name);
        tv_name.setText(obj.getName());
        inflate.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAlertDialog_loading.dismiss();
            }
        });
        inflate.findViewById(R.id.btnConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileUtil.renameFile(obj.getPath(), obj.AbsolutePath + "/" + tv_name.getText());
                obtian();
                mAlertDialog_loading.dismiss();
            }
        });
        mAlertDialog_loading = new AlertDialog.Builder(this, R.style.dialog1).setView(inflate).setCancelable(true)
                .show();
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = mAlertDialog_loading.getWindow().getAttributes(); // 获取对话框当前的参数值
        p.width = FileUtil.dip2px(this, 290); // 宽度设置为屏幕的0.8
        mAlertDialog_loading.getWindow().setAttributes(p);

    }


    void showgGrabSingleCompleteObj(final MediaBean obj) {
        View inflate = LayoutInflater.from(this).inflate(R.layout
                .delet_dia, null, false);
        final TextView tv_name = (TextView) inflate.findViewById(R.id.tv_name);
        inflate.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAlertDialog.dismiss();
            }
        });
        inflate.findViewById(R.id.btnConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileUtil.deleteFolderFile(obj.getAbsolutePath(), true);
                obtian();
                mAlertDialog.dismiss();
            }
        });
        mAlertDialog = new AlertDialog.Builder(this, R.style.dialog1).setView(inflate).setCancelable(true)
                .show();
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = mAlertDialog.getWindow().getAttributes(); // 获取对话框当前的参数值
        p.width = FileUtil.dip2px(this, 290); // 宽度设置为屏幕的0.8
        mAlertDialog.getWindow().setAttributes(p);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSE_CODE) {
            //
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                Uri uri = data.getData();

                String path = "";
                if ("file".equalsIgnoreCase(uri.getScheme())) {//使用第三方应用打开
                    path = uri.getPath();
                } else {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                        path = FileUtils.getPath(this, uri);
                    } else {//4.4以下下系统调用方法
                        path = FileUtils.getRealPathFromURI(this, uri);
                    }
                }
//
//                String[] proj = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.MIME_TYPE};
//
//                Cursor cursor = getContentResolver().query(uri, proj, null,
//                        null, null);
//                if (cursor != null && cursor.moveToFirst()) {
//                    int _data_num = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
//                    int mime_type_num = cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE);
//
//                    String _data = cursor.getString(_data_num);
//                    String mime_type = cursor.getString(mime_type_num);
                if (!TextUtils.isEmpty(path) && path.endsWith(".mp4")) {
                    LocalMediaConfig.Buidler buidler = new LocalMediaConfig.Buidler();
                    final LocalMediaConfig config = buidler
                            .setVideoPath(path)
                            .captureThumbnailsTime(1)
                            .doH264Compress(new AutoVBRMode()
                                    .setVelocity(BaseMediaBitrateConfig.Velocity.ULTRAFAST))

                            .setFramerate(10)
                            .setScale(1.0f)
                            .build();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showProgress("", "压缩中...", -1);
                                }
                            });
                            OnlyCompressOverBean onlyCompressOverBean = new LocalMediaCompress(config).startCompress();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hideProgress();
                                    obtian();
                                }
                            });

//                                Intent intent = new Intent(LanchActivity.this, SendSmallVideoActivity.class);
//                                intent.putExtra(MediaRecorderActivity.VIDEO_URI, onlyCompressOverBean.getVideoPath());
//                                intent.putExtra(MediaRecorderActivity.VIDEO_SCREENSHOT, onlyCompressOverBean.getPicPath());
//                                startActivity(intent);
                        }
                    }).start();


                } else {
                    Toast.makeText(this, "选择的不是视频或者地址错误,也可能是这种方式定制神机取不到！", Toast.LENGTH_SHORT).show();
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private ProgressDialog mProgressDialog;

    private void showProgress(String title, String message, int theme) {
        if (mProgressDialog == null) {
            if (theme > 0)
                mProgressDialog = new ProgressDialog(this, theme);
            else
                mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setCanceledOnTouchOutside(false);// 不能取消
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(true);// 设置进度条是否不明确
        }

        if (!StringUtils.isEmpty(title))
            mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    private void hideProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        RxPermissions rxPermission = new RxPermissions(this);
        //请求权限全部结果
        rxPermission.request(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        if (!granted) {

                        } else { //获取权限成功
                            obtian();


                        }
                    }
                });
    }
}

