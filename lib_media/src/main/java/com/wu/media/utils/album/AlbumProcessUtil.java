package com.wu.media.utils.album;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;


import com.wkq.base.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 作者: 吴奎庆
 * <p>
 * 时间: 2020/6/15
 * <p>
 * 简介:  相册操作工具类
 */
public class AlbumProcessUtil {

    public static void insertImg(Context context, File file) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, System.currentTimeMillis() + "");
        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//         其次把文件插入到系统图库
        try {
            String uri = MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), file.getName(), null);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 插入图片 部分机型适配(小米)
     *
     * @param context
     * @param file
     * @return
     */
    private static boolean insertMediaImg(Context context, File file) {
        if (file != null && !file.exists()) {
            return false;
        } else {
            try {
                MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), file.getName(), null);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

    }

    /**
     * 插入图片 部分机型适配
     *
     * @param context
     * @param file
     * @return
     */
    private static void insertMediaPic(Context context, File file) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, System.currentTimeMillis() + "");
        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    /**
     * 判断android Q  (10 ) 版本
     *
     * @return
     */
    private static boolean isAdndroidQ() {
        return Build.VERSION.SDK_INT >= 29;
    }
    /*
     * 保存bitmap到本地
     * */
    public static String saveBitmapNoInsert(Context context, Bitmap mBitmap) {
        String savePath;
        File filePic;
        savePath = getPath(context);
        try {
            filePic = new File(savePath + UUID.randomUUID().toString() + ".jpg");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return filePic.getAbsolutePath();
    }

    /**
     * 保存bitmap
     * @param context
     * @param bitmap
     * @param callback
     */
    public static void saveBitmapToGallery(Context context, final Bitmap bitmap, OnSaveCallback callback) {
        try {
            Observable.create(new ObservableOnSubscribe<String>() {
                @Override
                public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                    if (bitmap == null || bitmap.getByteCount() < 1) {
                        emitter.onError(new Throwable("文件地址为空"));
                    } else {

                        String path = saveBitmapNoInsert(context, bitmap);

                        if (FileUtils.isFileExists(path)) {
                            if (isAdndroidQ()) {
                                insertMediaImg(context, new File(path));
                                emitter.onNext(path);
                            } else {
                                insertMediaPic(context, new File(path));
                                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
                                emitter.onNext(path);
                            }
                        } else {
                            emitter.onError(new Throwable("图片保存失败"));
                        }
                    }
                }


            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(String s) {
                    callback.onSuccess(s);
                    if (bitmap!=null){
                        bitmap.recycle();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    callback.onFail("文件保存失败");
                    if (bitmap!=null){
                        bitmap.recycle();

                    }
                }

                @Override
                public void onComplete() {

                }
            });

        } catch (Exception e) {
            if (e != null && e.getMessage() != null) {
                callback.onFail("文件保存失败");
            }
        }

    }

    /**
     * 保存图片   小米  华为 vivo oppo  联想 锤子 三星 (部分机型)
     *
     * @param context
     * @param oldFile
     * @param callback
     */
    public static void saveImageToGallery(Context context, File oldFile, OnSaveCallback callback) {
        try {
            Observable.create(new ObservableOnSubscribe<String>() {
                @Override
                public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                    if (oldFile == null || !oldFile.exists() || oldFile.length() < 1) {
                        emitter.onError(new Throwable("文件地址为空"));
                    } else {
                        String type = AlbumFileUtli.getFileType(oldFile.getAbsolutePath(), true);
                        String extension;//初始文件扩展名
                        if (TextUtils.isEmpty(type)) {
                            extension = ".jpg";
                        } else {
                            extension = "." + type;
                        }
                        File file = new File(getPath(context), System.currentTimeMillis() + extension);
                        if (copyFile(oldFile.getAbsolutePath(), file.getAbsolutePath())) {
                            if (isAdndroidQ()) {
                                insertMediaImg(context, file);
                                emitter.onNext(file.getAbsolutePath());
                            } else {
                                insertMediaPic(context, file);
                                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
                                emitter.onNext(file.getAbsolutePath());
                            }
                        } else {
                            emitter.onError(new Throwable("图片保存失败"));
                        }
                    }
                }


            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(String s) {
                    callback.onSuccess(s);
                }

                @Override
                public void onError(Throwable e) {
                    callback.onFail("文件保存失败");
                }

                @Override
                public void onComplete() {

                }
            });

        } catch (Exception e) {
            if (e != null && e.getMessage() != null) {
                callback.onFail("文件保存失败");
            }
        }

    }

    /**
     * 保存视频
     *
     * @param filePath
     * @param callback
     */
    public static void saveVideo(Context mContext, String filePath, OnSaveCallback callback) {
        try {
            Observable.create(new ObservableOnSubscribe<String>() {
                @Override
                public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                    if (TextUtils.isEmpty(filePath) || !new File(filePath).exists() || new File(filePath).length() < 1) {
                        emitter.onError(new Throwable("文件地址为空"));
                    } else {
                        File file = new File(filePath);
                        try {
                            if (file != null && file.exists()) {
//                                String md5 = MD5Util.getMD5(file);
                                String type = AlbumFileUtli.getFileType(file.getAbsolutePath(), true);
                                String extension;//初始文件扩展名
                                if (TextUtils.isEmpty(type)) {
                                    extension = "";
                                } else {
                                    extension = "." + type;
                                }
                                File newFile = new File(getPath(mContext), System.currentTimeMillis() + extension);
                                if (copyFile(file.getAbsolutePath(), newFile.getAbsolutePath())) {
                                    if (isAdndroidQ()) {
                                        String uri = AlbumProcessUtil.insertVideoMediaFile(mContext, "video/mp4", System.currentTimeMillis() + "", "网家家视频", file.getAbsolutePath());
                                        emitter.onNext(newFile.getAbsolutePath());
                                    } else {
                                        insertMediaVideo(mContext, newFile);
                                        mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + newFile)));
                                        emitter.onNext(newFile.getAbsolutePath());
                                    }
                                } else {
                                    emitter.onError(new Throwable("文件异常"));
                                }
                            } else {
                                emitter.onError(new Throwable("文件异常"));
                            }
                        } catch (Exception e) {
                            emitter.onError(new Throwable("文件异常"));
                        }
                    }
                }


            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(String path) {
                    callback.onSuccess(path);
                }

                @Override
                public void onError(Throwable e) {
                    callback.onFail("文件保存失败");
                }

                @Override
                public void onComplete() {

                }
            });

        } catch (Exception e) {
            if (e != null && e.getMessage() != null) {
                callback.onFail("文件保存失败");
            }
        }
    }


    private static String getPath(Context context) {
        String fileName = Environment.getExternalStorageDirectory() + "/strike/current/";
        if (Build.VERSION.SDK_INT >= 29) {
            fileName = context.getExternalFilesDir("").getAbsolutePath();
        } else {
            if ("Xiaomi".equalsIgnoreCase(Build.BRAND)) { // 小米手机
                fileName = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/";
            } else if ("HUAWEI".equalsIgnoreCase(Build.BRAND) || "HONOR".equalsIgnoreCase(Build.BRAND)) {
                fileName = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/";
            } else if ("OPPO".equalsIgnoreCase(Build.BRAND)) {
                fileName = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/";
            } else if ("vivo".equalsIgnoreCase(Build.BRAND)) {
                fileName = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/";
            } else if ("samsung".equalsIgnoreCase(Build.BRAND)) {
                fileName = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/";
            } else {
                fileName = Environment.getExternalStorageDirectory().getPath() + "/DCIM/";
            }
        }
        File file = new File(fileName);
        if (file.mkdirs()) {
            return fileName;
        }
        return fileName;
    }


    /**
     * 复制单个文件
     */
    private static boolean copyFile(String oldPath$Name, String newPath$Name) {
        try {
            File oldFile = new File(oldPath$Name);
            if (!oldFile.exists()) {
                return false;
            } else if (!oldFile.isFile()) {
                return false;
            } else if (!oldFile.canRead()) {
                return false;
            }

            FileInputStream fileInputStream = new FileInputStream(oldPath$Name);
            FileOutputStream fileOutputStream = new FileOutputStream(newPath$Name);
            byte[] buffer = new byte[1024];
            int byteRead;
            while (-1 != (byteRead = fileInputStream.read(buffer))) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
     * 保存bitmap到本地
     * */
    public static String saveBitmap(Context context, Bitmap mBitmap) {
        String savePath;
        File filePic;
        savePath = getPath(context);
        try {
            filePic = new File(savePath + UUID.randomUUID().toString() + ".jpg");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

            saveImageToGallery(context ,filePic,null);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return filePic.getAbsolutePath();
    }

    /*
     * 保存bitmap到本地
     * */
    public static void saveBitmap(Context context, Bitmap mBitmap, OnSaveCallback callback) {
        String savePath;
        File filePic;
        savePath = getPath(context);
        try {
            filePic = new File(savePath + UUID.randomUUID().toString() + ".jpg");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

            saveImageToGallery(context ,filePic,callback);
        } catch (IOException e) {
            e.printStackTrace();
            callback.onFail(e.getMessage());
        }

    }

    public static File saveBitmap(Context context, Bitmap mBitmap, String fileName) {
        String savePath;
        File filePic;
        savePath = getPath(context);
        try {
            filePic = new File(savePath + fileName + ".jpg");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return filePic;
    }

    public static String saveBitmapToFile(Context context, Bitmap bitmap, String title, String discription) {
        return MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, title, discription);
    }


    private static String insertVideoMediaFile(Context context, String mimeType, String displayName, String description, String saveFileName) {
        ContentValues values = new ContentValues();
        values.put("_display_name", displayName);
        values.put("description", description);
        values.put("mime_type", mimeType);
        values.put("datetaken", System.currentTimeMillis() + "");
        Uri url = null;
        String stringUrl = null;
        ContentResolver cr = context.getContentResolver();
        FileOutputStream fileOutputStream = null;
        FileInputStream inputStream = null;
        try {
            url = cr.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
            if (url == null) {
                return null;
            }
            byte[] buffer = new byte[1044];
            ParcelFileDescriptor parcelFileDescriptor = cr.openFileDescriptor(url, "w");
            fileOutputStream = new FileOutputStream(parcelFileDescriptor.getFileDescriptor());
            inputStream = new FileInputStream(saveFileName);
            while (true) {
                int numRead = inputStream.read(buffer);
                if (numRead == -1) {
                    fileOutputStream.flush();
                    break;
                }
                fileOutputStream.write(buffer, 0, numRead);
            }
        } catch (Exception var14) {
            if (url != null) {
                cr.delete(url, (String) null, (String[]) null);
                url = null;
            }
        } finally {
            try {
                if (fileOutputStream != null)
                    fileOutputStream.close();
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (url != null) {
            stringUrl = url.toString();
        }
        return stringUrl;
    }

    private static void insertMediaVideo(Context context, File file) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Video.Media.DATA, file.getAbsolutePath());
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/*");
        values.put(MediaStore.Video.VideoColumns.DATE_TAKEN, System.currentTimeMillis() + "");
        context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
    }


}
