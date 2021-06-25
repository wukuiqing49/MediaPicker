package com.wu.media.media.loader;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;


import com.wu.media.R;
import com.wu.media.media.DataCallback;
import com.wu.media.media.entity.Folder;
import com.wu.media.media.entity.Media;
import com.wu.media.utils.MediaUtils;
import com.wu.media.utils.observable.MediaAddObservable;
import com.wu.media.utils.observable.MediaAddObservable2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by dmcBig on 2017/6/9.
 */

public class MediaLoader extends LoaderM implements LoaderManager.LoaderCallbacks {
    private final String TAG = this.getClass().getName();

    String[] MEDIA_PROJECTION = {
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.WIDTH,
            MediaStore.Files.FileColumns.HEIGHT,
            MediaStore.Files.FileColumns.PARENT,
            MediaStore.Video.VideoColumns.DURATION};

    Context mContext;
    DataCallback mLoader;
    private Uri photoUri;
    private static MediaLoader instance;
    private static ArrayList<Folder> folders = new ArrayList<>();
    private static long startTime;

    public synchronized static MediaLoader getInstance(Context context, DataCallback loader) {
        startTime=System.currentTimeMillis();
        if (instance == null) {
            instance = new MediaLoader(context);
            folders = new ArrayList<>();
        }
        instance.setmLoader(loader);
        return instance;
    }

    public MediaLoader(Context context) {
        if (context == null) return;
        this.mContext = context;
    }

    public void setmLoader(DataCallback mLoader) {
        this.mLoader = mLoader;
    }

    @Override
    public Loader onCreateLoader(int picker_type, Bundle bundle) {
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;


        Uri queryUri = MediaStore.Files.getContentUri("external");

        CursorLoader cursorLoader = new CursorLoader(
                mContext,
                queryUri,
                MEDIA_PROJECTION,
                selection,
                null, // Selection args (none).
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC" // Sort order.
        );
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader loader, Object o) {


        if (folders == null || mLoader == null || instance == null) return;
        if (folders.size() > 0) {
            mLoader.onData(folders);
            return;
        }

        Folder allFolder = new Folder(mContext.getResources().getString(R.string.all_dir_name));
        folders.add(allFolder);
        Folder allVideoDir = new Folder(mContext.getResources().getString(R.string.video_dir_name));
        folders.add(allVideoDir);
        Cursor cursor = (Cursor) o;

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));
                File file = new File(path);
                if (!file.exists()) {
                    continue;
                }
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME));
                long dateTime = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED));
                int mediaType = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE));
                long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE));
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID));
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DURATION));
                if (size < 512) continue;
                if (mediaType == 3) {
                    photoUri = Uri.parse(MediaStore.Video.Media.EXTERNAL_CONTENT_URI.toString() + File.separator + id);
                } else if (mediaType == 1) {
                    photoUri = Uri.parse(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() + File.separator + id);
                }

                //获取图片宽高
                int width = cursor.getInt
                        (cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.WIDTH));

                int height = cursor.getInt
                        (cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.HEIGHT));

                String fileUri = photoUri.toString();

                String dirName = getParent(path);
                if (TextUtils.isEmpty(dirName)) continue;
               Media  media = new Media(path, name, dateTime, mediaType, size, id, dirName, duration, fileUri);

                allFolder.addMedias(media);
                if (mediaType == 3) {
                    allVideoDir.addMedias(media);
                } else if (mediaType == 1) {
                }

                int index = hasDir(folders, dirName);
                if (index != -1) {
                    folders.get(index).addMedias(media);
                } else {
                    Folder folder = new Folder(dirName);
                    media.setImgWidth(width);
                    media.setImgHeight(height);
                    media.setLongImg(MediaUtils.isLongImg(media));
                    folder.addMedias(media);
                    folders.add(folder);
                }
            }
            Log.e("获取资源时间:",  System.currentTimeMillis()-startTime+"");
            startTime=0;
            cursor.close();
            if (mLoader != null) mLoader.onData(folders);
        }

    }


    @Override
    public void onLoaderReset(Loader loader) {

    }

    @Override
    public void onDestory() {
        if (null != instance && null != folders) {
            folders.clear();
            folders = null;
            instance = null;
        }
    }
}
