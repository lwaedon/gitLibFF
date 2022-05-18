package com.mabeijianxi.smallvideo2;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtil {
    /**
     * 获取edittext的内容
     *
     * @param et
     * @return
     */
    public static String getEtContent(EditText et) {
        return et.getText().toString().trim();
    }

    /**
     * 校验手机号码是否合格
     *
     * @param phoneNumber 手机号码
     * @return
     */

    public static boolean isPhoneNumberValid(String phoneNumber) {
        boolean isValid = false;
        String expression = "^[1][3-9][0-9]{9}$";
        CharSequence inputStr = phoneNumber;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static void createFile(File file, String fileName, String newName) {
        File sdCard = Environment.getExternalStorageDirectory();
        if (!file.exists()) {
            try {
                file.createNewFile();
                String oldPath = file.getAbsolutePath();
                String newPath = oldPath.replace(fileName, newName);
                renameFile(oldPath, newPath);
                //file is create
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            String oldPath = file.getAbsolutePath();
            if (!TextUtils.isEmpty(oldPath)) {
                String newPath = oldPath.replace(fileName, newName);
                renameFile(oldPath, newPath);
            }

        }
    }


    public static void renameFile(String oldPath, String newPath) {
        if (TextUtils.isEmpty(oldPath)) {
            return;
        }

        if (TextUtils.isEmpty(newPath)) {
            return;
        }

        File file = new File(oldPath);
        file.renameTo(new File(newPath));
    }


    public static void deleteFolderFile(String filePath, boolean dedeleteThisPath) {
        try {
            String rootPathSD = Environment.getExternalStorageDirectory().getPath();
            File file = new File(filePath);//获取SD卡指定路径
            File[] files = file.listFiles();//获取SD卡指定路径下的文件或者文件夹
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {//如果是文件直接删除
                    File photoFile = new File(files[i].getPath());
//                    Log.d("photoPath -->> ", photoFile.getPath());
                    photoFile.delete();
                } else {
                    if (dedeleteThisPath) {//如果是文件夹再次迭代进里面找到指定文件路径
                        File[] myfile = files[i].listFiles();
                        for (int d = 0; d < myfile.length; d++) {
                            File photoFile = new File(myfile[d].getPath());
                            photoFile.delete();
                        }
                    }

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static int getLocalVideoDuration(String filePath) {
        int duration = 0;
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(filePath);
            duration = Integer.parseInt(mmr.extractMetadata
                    (MediaMetadataRetriever.METADATA_KEY_DURATION)) / 1000;//除以 1000 返回是秒


            //时长(毫秒)
//            String duration = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
//            //宽
//            String width = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
//            //高
//            String height = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);

        } catch (Exception e) {
            e.printStackTrace();
            return duration;
        }
        return duration;
    }


    private static final String TAG = FileUtil.class.getSimpleName();

    public static final int SIZETYPE_B = 1;//获取文件大小单位为B的double值
    public static final int SIZETYPE_KB = 2;//获取文件大小单位为KB的double值
    public static final int SIZETYPE_MB = 3;//获取文件大小单位为MB的double值
    public static final int SIZETYPE_GB = 4;//获取文件大小单位为GB的double值

    /**
     * 获取文件指定文件的指定单位的大小
     *
     * @param filePath 文件路径
     * @param sizeType 获取大小的类型1为B、2为KB、3为MB、4为GB
     * @return double值的大小
     */
    public static double getFileOrFilesSize(String filePath, int sizeType) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "获取文件大小失败!");
        }
        return FormetFileSize(blockSize, sizeType);
    }

    /**
     * 调用此方法自动计算指定文件或指定文件夹的大小
     *
     * @param filePath 文件路径
     * @return 计算好的带B、KB、MB、GB的字符串
     */
    public static String getAutoFileOrFilesSize(String filePath) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "获取文件大小失败!");
        }
        return FormetFileSize(blockSize);
    }

    /**
     * 获取指定文件大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            Log.e(TAG, "获取文件大小不存在!");
        }
        return size;
    }

    /**
     * 获取指定文件夹
     *
     * @param f
     * @return
     * @throws Exception
     */
    private static long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    /**
     * 转换文件大小,指定转换的类型
     *
     * @param fileS
     * @param sizeType
     * @return
     */
    public static double FormetFileSize(long fileS, int sizeType) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        switch (sizeType) {
            case SIZETYPE_B:
                fileSizeLong = Double.valueOf(df.format((double) fileS));
                break;
            case SIZETYPE_KB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1024));
                break;
            case SIZETYPE_MB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1048576));
                break;
            case SIZETYPE_GB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1073741824));
                break;
            default:
                break;
        }
        return fileSizeLong;
    }


    public static String formatDateTime(long mss) {
        String DateTimes = null;
        long days = mss / (60 * 60 * 24);
        long hours = (mss % (60 * 60 * 24)) / (60 * 60);
        long minutes = (mss % (60 * 60)) / 60;
        long seconds = mss % 60;
        if (days > 0) {
            DateTimes = days + "天" + hours + "小时" + minutes + "分钟"
                    + seconds + "秒";
        } else if (hours > 0) {
            if (minutes >=10) {
                if (seconds >=10) {
                    DateTimes = "0"+hours+":"+minutes +":"+ seconds;
                } else {
                    DateTimes = "0"+hours+":"+minutes +":0"+ seconds;
                }

            } else {
                if (seconds >=10) {
                    DateTimes ="0"+hours+":"+ "0"+minutes +":"+ seconds;
                } else {
                    DateTimes = "0"+hours+":"+"0"+minutes +":0"+ seconds;
                }
            }
        } else if (minutes > 0) {
            if (minutes >=10) {
                if (seconds >=10) {
                    DateTimes = minutes +":"+ seconds;
                } else {
                    DateTimes = minutes +":0"+ seconds;
                }

            } else {
                if (seconds >=10) {
                    DateTimes = "0"+minutes +":"+ seconds;
                } else {
                    DateTimes = "0"+minutes +":0"+ seconds;
                }
            }

        } else {
            if (seconds >=10) {
                DateTimes = "00" +":"+ seconds;
            } else {
                DateTimes = "00"+":0"+ seconds;
            }

        }

        return DateTimes;
    }


    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }




}
