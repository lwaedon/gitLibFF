package com.mabeijianxi.smallvideo2;//package com.mabeijianxi.smallvideo;
//
//import android.Manifest;
//import android.app.Activity;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import java.io.File;
//
//public class obj extends AppCompatActivity {
//
//    private static final String DOC = "application/msword";
//    private static final String DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
//    private static final String XLS = "application/vnd.ms-excel";
//    private static final String XLS1 = "application/x-excel";
//    private static final String XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
//    private static final String PPT = "application/vnd.ms-powerpoint";
//    private static final String PPTX = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
//    private static final String PDF = "application/pdf";
//    private static final String MP4 = "video/mp4";
//    private static final String M3U8 = "application/x-mpegURL";
//
//    private static final int REQUEST_CODE_FILE = 985 << 2;
//    private static final int REQUEST_PHONE_STATE = 211 << 2;
//
//    private final String[] fileSuffix = {".pptx", ".ppt", ".xlsx", ".docx", ".xls", ".doc", ".pdf"};
//    private final String[] videoSuffix = {".m3u8", ".mp4"};
//    TextView tv;
//    String type;
//    AlertDialog.Builder dialog;
//    private String path;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        tv = findViewById(R.id.text);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            int permissionRead = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            if (permissionRead == PackageManager.PERMISSION_GRANTED) {
//                grantSuccess();
//            } else {
//                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PHONE_STATE);
//            }
//        } else {
//            grantSuccess();
//        }
//        findViewById(R.id.btn_file).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                chooseFile(true);
//            }
//        });
//        findViewById(R.id.btn_video).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                chooseFile(false);
//            }
//        });
//        findViewById(R.id.btn_file_path).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                chooseFileWithPath();
//            }
//        });
//        findViewById(R.id.btn_pic).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                choosePic();
//            }
//
//        });
//    }
//
//    /**
//     * ??????????????????
//     */
//    private void choosePic() {
//        Intent intent;
//        intent = new Intent(Intent.ACTION_PICK, null);
//        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//        startActivityForResult(intent, REQUEST_CODE_FILE);
//    }
//
//    /**
//     * ????????????????????????????????????
//     */
//    private void chooseFileWithPath() {
//        //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????es???
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        String[] mimeTypes = {DOC, DOCX, PDF, PPT, PPTX, XLS, XLS1, XLSX};
//
//        //???????????????????????????????????????????????????sdcard
//        //???????????????
//        String path = getSDPath();
//        if (!TextUtils.isEmpty(path)) {
//            path = path + File.separator + "tencent/MicroMsg/Download";
//            File file = new File(path);
//            if (file.exists()) {
//                intent.setDataAndType(FileUtil.getUriFromFile(this, new File(path)), "application/*");
//            } else {
//                intent.setType("application/*");
//            }
//        } else {
//            intent.setType("application/*");
//        }
//
//        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
//        startActivityForResult(intent, REQUEST_CODE_FILE);
//    }
//
//    /**
//     * ????????????????????????????????????
//     */
//    private void chooseFile(boolean isFile) {
//        //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????es???
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        String[] mimeTypes = {DOC, DOCX, PDF, PPT, PPTX, XLS, XLS1, XLSX};
//        if (!isFile) {
//            mimeTypes = new String[]{MP4, M3U8};
//        }
//        intent.setType(isFile ? "application/*;*.xls" : "video/mp4;*.m3u8");
//        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
//        startActivityForResult(intent, REQUEST_CODE_FILE);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_FILE) {
//            Uri uri = data.getData();
//            tv.setText("");
//            String s = uri.toString() + "  \n " + uri.getPath() + " \n " + uri.getAuthority();
//            tv.setText(s);
//            Log.i("-----", s);
//
//            if ("file".equalsIgnoreCase(uri.getScheme())) {//???????????????????????????
//                path = uri.getPath();
//            } else {
//                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4??????
//                    path = FileUtil.getPath(this, uri);
//                } else {//4.4???????????????????????????
//                    path = FileUtil.getRealPathFromURI(this, uri);
//                }
//            }
//
//            //uri.getLastPathsegment()??????????????????????????????
//            //content://com.android.providers.media.documents/document/video:5186
//            //???????????????path?????????
//
//            String name =path.toLowerCase();
//            if (!checkFileType(name)) {
//                Toast.makeText(this, "????????????????????????", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//
//            doSomething();
//        }
//        //????????????????????????
//    }
//
//    /**
//     * ??????????????????
//     *
//     * @param fileName
//     *
//     * @return
//     */
//    private boolean checkFileType(String fileName) {
//        if ("video".equals(type)) {
//            for (String suffix : videoSuffix) {
//                if (fileName.endsWith(suffix)) {
//                    return true;
//                }
//            }
//        } else {
//            for (String suffix : fileSuffix) {
//                if (fileName.endsWith(suffix)) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
//    /**
//     * ???js????????????
//     */
//    private void doSomething() {
//        tv.setText(tv.getText() + "\n----dosomething----\n" + path);
//    }
//
//    /**
//     * @return
//     */
//    public String getSDPath() {
//        String path = "";
//        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);//??????sd???????????????
//        if (sdCardExist) {
//            File sdDir = Environment.getExternalStorageDirectory();//???????????????
//            path = sdDir.toString();
//        }
//        return path;
//    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        finish();
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (grantResults == null || grantResults.length == 0) {
//            return;
//        }
//        if (requestCode == REQUEST_PHONE_STATE) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                grantSuccess();
//            } else {
//                if (dialog == null) {
//                    dialog = new AlertDialog.Builder(MainActivity.this);
//                    dialog.setMessage("????????????????????????????????????????????????");
//                    dialog.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PHONE_STATE);
//                        }
//                    }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            grantSuccess();
//                        }
//                    }).show();
//                }
//            }
//        }
//    }
//
//    /**
//     * ????????????
//     */
//    private void grantSuccess() {
//
//    }
//
//}