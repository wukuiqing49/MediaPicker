package com.wu.media.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.wu.media.R;
import com.wu.media.media.entity.Media;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.File;
import java.math.BigDecimal;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Lynn on 2018/1/19.
 */

public class GlideCacheUtil {
    private static GlideCacheUtil inst;

    public static GlideCacheUtil getInstance() {
        if (inst == null) {
            inst = new GlideCacheUtil();
        }
        return inst;
    }

    /**
     * 清除图片磁盘缓存
     */
    public void clearImageDiskCache(final Context context) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.get(context).clearDiskCache();
                    }
                }).start();
            } else {
                Glide.get(context).clearDiskCache();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清除图片内存缓存
     */
    public void clearImageMemoryCache(Context context) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) { //只能在主线程执行
                Glide.get(context).clearMemory();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清除图片所有缓存
     */
    public void clearImageAllCache(Context context) {
        clearImageDiskCache(context);
        clearImageMemoryCache(context);
        String ImageExternalCatchDir = context.getExternalCacheDir() + ExternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR;
        deleteFolderFile(ImageExternalCatchDir, true);
    }

    /**
     * 获取Glide造成的缓存大小
     *
     * @return CacheSize
     */
    public String getCacheSize(Context context) {
        try {
            return getFormatSize(getFolderSize(new File(context.getCacheDir() + "/" + InternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取指定文件夹内所有文件大小的和
     *
     * @param file file
     * @return size
     * @throws Exception
     */
    private long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (File aFileList : fileList) {
                if (aFileList.isDirectory()) {
                    size = size + getFolderSize(aFileList);
                } else {
                    size = size + aFileList.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 删除指定目录下的文件，这里用于缓存的删除
     *
     * @param filePath       filePath
     * @param deleteThisPath deleteThisPath
     */
    private void deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {
                    File files[] = file.listFiles();
                    for (File file1 : files) {
                        deleteFolderFile(file1.getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) {
                        file.delete();
                    } else {
                        if (file.listFiles().length == 0) {
                            file.delete();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 格式化单位
     *
     * @param size size
     * @return size
     */
    private static String getFormatSize(double size) {

        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "Byte";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);

        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }


    @SuppressLint("CheckResult")
    public static void intoItemImage(Context context, Media media, ImageView view, ProgressBar loading) {
        if (context == null || media == null || view == null) return;

        RequestOptions option = new RequestOptions();
        try {
            Uri mediaUri = Uri.parse(media.fileUri);

            RequestManager rm = Glide.with(context);
            RequestBuilder rb = null;

            if (FileTypeUtil.isGif(media.path)) {
//                rb = rm.asGif();
            } else {
                option = option
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .centerCrop()
                        .dontAnimate();
            }

            if (media.mediaType == 1) {
                option = option
                        .error(R.drawable.iv_imgload_full_err);
            } else if (media.mediaType == 1) {
                option = option
                        .error(R.drawable.iv_imgload_full_err);
                view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }

            if (AndroidQUtil.isAndroidQ()) {
                rb = rb == null ? rm.load(mediaUri) : rb.load(mediaUri);
            } else {
                rb = rb == null ? rm.load(media.path) : rb.load(media.path);
            }

            rb.listener(new RequestListener() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                    view.setScaleType(ImageView.ScaleType.CENTER);
                    if (loading != null) loading.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                    if (loading != null) loading.setVisibility(View.GONE);
                    return false;
                }
            });

            rb.apply(option).into(view);
        } catch (Exception e) {
            if (loading != null) loading.setVisibility(View.GONE);
        }
    }


    @SuppressLint("CheckResult")
    public static void intoItemImageListener(Context context, Media media, ImageView view, RequestListener listener) {
        if (context == null || media == null || view == null) return;

        RequestOptions option = new RequestOptions();
        try {
            Uri mediaUri = Uri.parse(media.fileUri);

            RequestManager rm = Glide.with(context);
            RequestBuilder rb = null;

            if (FileTypeUtil.isGif(media.path)) {
//                rb = rm.asGif();
            } else {
                option = option
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .centerCrop()
                        .dontAnimate();
            }
            if (media.mediaType == 1) {
                option = option.error(R.drawable.iv_imgload_full_err);
                view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else if (media.mediaType == 3) {
                option = option.error(R.drawable.iv_imgload_full_err);
            }

            if (AndroidQUtil.isAndroidQ()) {
                rb = rb == null ? rm.load(mediaUri) : rb.load(mediaUri);
            } else {
                rb = rb == null ? rm.load(media.path) : rb.load(media.path);
            }
            rb.listener(listener).apply(option).into(view);
        } catch (Exception e) {
        }
    }


    @SuppressLint("CheckResult")
    public static void intoItemImageThumbnail(Context context, Media media, ImageView view, ProgressBar loading) {
        if (context == null || media == null || view == null) return;
        RequestOptions option = new RequestOptions();
        try {
            Uri mediaUri = Uri.parse(media.fileUri);
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (FileTypeUtil.isGif(media.path)) {
            } else {
                option = option
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .dontAnimate();
            }
            if (media.mediaType == 1) {
                option = option.error(R.drawable.iv_imgload_full_err).placeholder(R.drawable.iv_new_media_zw);
            } else {
                option = option.error(R.drawable.iv_imgload_full_err).placeholder(R.drawable.iv_new_media_zw);
            }
            if (AndroidQUtil.isAndroidQ()) {
                view.setTag(mediaUri.toString());
                showAndroidQ(context, media.mediaType, mediaUri, view, loading, option);
            } else {
                if (media.mediaType == 1) {
                    showNoAndroidQ(context, media.mediaType, media.fileUri, view, loading, option);
                } else if (media.mediaType == 3) {
                    if (ThumbnailsUtils.thumbnails.containsKey(media.id + "")) {
                        showVideoNoAndroidQ(context, media.mediaType, ThumbnailsUtils.thumbnails.get(media.id + ""), view, loading, option);
                    } else {
                        view.setTag(media.id);
                        showVideoThumbnail(context, media.mediaType, media.id, media.path, view, loading, option);
                    }
                }
            }


        } catch (Exception e) {
            if (loading != null) loading.setVisibility(View.GONE);
        }
    }

    private static void showAndroidQUri(Context context, Uri path, ImageView view, ProgressBar loading, RequestOptions option) {
        RequestBuilder rb = Glide.with(context).load(path);
        rb.listener(new RequestListener() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                view.setScaleType(ImageView.ScaleType.CENTER);
                if (loading != null) loading.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                if (loading != null) loading.setVisibility(View.GONE);
                return false;
            }
        });

        rb.apply(option).into(view);
    }
    private static void showAndroidQ(Context context, int mediaType, Uri mediaUri, ImageView view, ProgressBar loading, RequestOptions option) {

        Observable.create(new ObservableOnSubscribe<Bitmap>() {

            @Override
            public void subscribe(ObservableEmitter<Bitmap> emitter) throws Exception {
                ContentResolver contentResolver = context.getContentResolver();
                Bitmap bm = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    bm = contentResolver.loadThumbnail(mediaUri, new Size(100, 100), null);
                }
                if (bm != null) {
                    emitter.onNext(bm);
                } else {
                    emitter.onError(new Throwable(""));
                }
            }
        }).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Bitmap>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Bitmap bean) {
                if (view.getTag() != null && mediaUri.toString().equals(view.getTag())) {
                    showThumbnail(context, bean, view, loading, option);
                }
            }

            @Override
            public void onError(Throwable e) {
                if (view.getTag() != null && mediaUri.toString().equals(view.getTag())) {
                    showAndroidQUri(context, mediaUri, view, loading, option);
                }
                if (loading != null) loading.setVisibility(View.GONE);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private static void showVideoNoAndroidQ(Context context, int mediaType, String path, ImageView view, ProgressBar loading, RequestOptions option) {
        RequestBuilder rb = Glide.with(context).load(path);
        rb.listener(new RequestListener() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                view.setScaleType(ImageView.ScaleType.CENTER);
                if (loading != null) loading.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                if (loading != null) loading.setVisibility(View.GONE);
                return false;
            }
        });

        rb.thumbnail(0.05f).apply(option).into(view);
    }

    private static void showNoAndroidQ(Context context, int mediaType, String path, ImageView view, ProgressBar loading, RequestOptions option) {
        RequestBuilder rb = Glide.with(context).load(path);
        rb.listener(new RequestListener() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                view.setScaleType(ImageView.ScaleType.CENTER);
                if (loading != null) loading.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                if (loading != null) loading.setVisibility(View.GONE);
                return false;
            }
        });

        rb.thumbnail(0.3f).apply(option).into(view);
    }


    private static void showVideoThumbnail(Context context, int mediaType, long id, String path, ImageView view, ProgressBar loading, RequestOptions option) {
//        // 步骤1：创建被观察者 =  Flowable
        Flowable.create(new FlowableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(FlowableEmitter<Bitmap> emitter) throws Exception {
                Bitmap bm = getVideoThumbnail(context, id + "");
                if (bm != null) {
                    emitter.onNext(bm);
                } else {
                    emitter.onError(new Throwable(""));
                }
            }
        }, BackpressureStrategy.LATEST)
                .subscribe(new Subscriber<Bitmap>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                    }

                    @Override
                    public void onNext(Bitmap thumbnails) {
                        if (view.getTag() != null && view.getTag().toString().equals(id + ""))
                            showThumbnail(context, thumbnails, view, loading, option);
                    }

                    @Override
                    public void onError(Throwable t) {
                        showNoAndroidQ(context, mediaType, path, view, loading, option);
                        if (loading != null) loading.setVisibility(View.GONE);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }


    private static void showThumbnail(Context context, Bitmap bm, ImageView view, ProgressBar loading, RequestOptions option) {
        RequestBuilder rb = Glide.with(context).load(bm);
        rb.listener(new RequestListener() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                view.setScaleType(ImageView.ScaleType.CENTER);
                if (loading != null) loading.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                if (loading != null) loading.setVisibility(View.GONE);
                return false;
            }
        });

        rb.apply(option).into(view);
    }


    public static Bitmap getVideoThumbnail(Context context, String videoId) {
        Cursor thumbCursor = null;
        Bitmap bitmap = null;
        try {
            thumbCursor = context.getApplicationContext().getContentResolver().query(
                    MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Video.Thumbnails.VIDEO_ID
                            + "=" + videoId, null, null);
            if (thumbCursor.moveToFirst()) {
                String albumPath = thumbCursor.getString(thumbCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                Log.d("缩略图----------------->:", "id:" + videoId + "地址:" + albumPath);
                bitmap = BitmapFactory.decodeFile(albumPath);
                return bitmap;
            }
        } catch (Exception e) {
            return bitmap;
        } finally {
            if (thumbCursor != null)
                thumbCursor.close();
            return bitmap;
        }
    }

    public static Bitmap getImageThumbnail(Context context, String videoId) {
        Bitmap bitmap = null;
        Cursor thumbCursor = null;
        try {
            thumbCursor = context.getApplicationContext().getContentResolver().query(
                    MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Images.Thumbnails.IMAGE_ID
                            + "=" + videoId, null, null);
            if (thumbCursor.moveToFirst()) {
                String albumPath = thumbCursor.getString(thumbCursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
                bitmap = BitmapFactory.decodeFile(albumPath);
                return bitmap;
            }
        } catch (Exception e) {
            return bitmap;
        } finally {
            if (thumbCursor != null)
                thumbCursor.close();
            return bitmap;
        }
    }

    @SuppressLint("CheckResult")
    public static void intoItemImageOther(Context context, Media media, ImageView view, ProgressBar loading) {
        if (context == null || media == null || view == null) return;

        RequestOptions option = new RequestOptions();
        try {
            Uri mediaUri = Uri.parse(media.fileUri);

            RequestManager rm = Glide.with(context);
            RequestBuilder rb = null;
            if (FileTypeUtil.isGif(media.path)) {
//                rb = rm.asGif();
            } else {
                option = option
                        .dontTransform()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .dontAnimate();
            }

            if (media.mediaType == 1) {
                option = option.error(R.drawable.iv_imgload_full_err);
            }

            if (AndroidQUtil.isAndroidQ()) {
                rb = rb == null ? rm.load(mediaUri) : rb.load(mediaUri);
            } else {
                rb = rb == null ? rm.load(media.path) : rb.load(media.path);
            }


            rb.listener(new RequestListener() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                    view.setScaleType(ImageView.ScaleType.CENTER);
                    if (loading != null) loading.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                    if (loading != null) loading.setVisibility(View.GONE);
                    return false;
                }
            });
            rb.apply(option).into(view);
        } catch (Exception e) {
            if (loading != null) loading.setVisibility(View.GONE);
        }
    }


    @SuppressLint("CheckResult")
    public static void intoItemImageBitmap(Context context, Bitmap bitmap, ImageView view) {
        if (context == null || bitmap == null || view == null) return;

        RequestOptions option = new RequestOptions();
        try {

            RequestManager rm = Glide.with(context);
            RequestBuilder rb = null;
            option = option
                    .dontTransform()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .dontAnimate();
            option.error(R.drawable.iv_imgload_full_err);

            rb = rm.load(bitmap);

            rb.listener(new RequestListener() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                    view.setScaleType(ImageView.ScaleType.CENTER);
                    return false;
                }

                @Override
                public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            });
            rb.apply(option).into(view);
        } catch (Exception e) {

        }
    }

}
