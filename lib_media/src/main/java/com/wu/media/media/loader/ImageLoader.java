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


import com.wu.media.R;
import com.wu.media.media.DataCallback;
import com.wu.media.media.entity.Folder;
import com.wu.media.media.entity.Media;
import com.wu.media.utils.MediaUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by dmcBig on 2017/7/3.
 */

public class ImageLoader extends LoaderM implements LoaderManager.LoaderCallbacks {
    private static ArrayList<Folder> folders=new ArrayList<>();
    private final String TAG = this.getClass().getName();
    String[] IMAGE_PROJECTION = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
            MediaStore.Images.Media._ID};

    Context mContext;
    DataCallback mLoader;

    private static ImageLoader instance = null;

    public synchronized static ImageLoader getInstance(Context context, DataCallback loader) {
        if (instance == null || folders == null) {
            instance = new ImageLoader(context);
            folders = new ArrayList<>();
        }
        instance.setmLoader(loader);
        return instance;
    }

    public ImageLoader(Context context) {
        this.mContext = context;
    }

    public void setmLoader(DataCallback mLoader) {
        this.mLoader = mLoader;
    }

    @Override
    public Loader onCreateLoader(int picker_type, Bundle bundle) {
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
        Uri queryUri = MediaStore.Files.getContentUri("external");
        CursorLoader cursorLoader = new CursorLoader(
                mContext,
                queryUri,
                IMAGE_PROJECTION,
                selection,
                null, // Selection args (none).
                MediaStore.Images.Media.DATE_ADDED + " DESC" // Sort order.
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

        Folder allFolder = new Folder(mContext.getResources().getString(R.string.all_image));
        folders.add(allFolder);
        Cursor cursor = null;
        if (o != null) cursor = (Cursor) o;
        while (cursor != null && cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
            File file = new File(path);
            if (!file.exists()) {
                continue;
            }

            //判断路径的长度是否大于后缀名
            if (path.length() > ".wbmp".length()) {
                //进行判断
                if (path.lastIndexOf(".wbmp") != -1) {
                    continue;
                }
            }
            String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
            long dateTime = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED));
            int mediaType = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE));
            long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));
            if (size < 512) continue;
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
            Uri photoUri = Uri.parse(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() + File.separator + id);
            String fileUri = photoUri.toString();
            if (TextUtils.isEmpty(path)) continue;
            String dirName = getParent(path);
            if (TextUtils.isEmpty(dirName)) continue;
            Media media = new Media(path, name, dateTime, mediaType, size, id, dirName, fileUri);
            //获取图片宽高
            int width = cursor.getInt
                    (cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.WIDTH));

            int height = cursor.getInt
                    (cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.HEIGHT));
            //设置图片大小
            media.setImgWidth(width);
            media.setImgHeight(height);
            media.setLongImg(MediaUtils.isLongImg(media));
            allFolder.addMedias(media);
//            String thumbnails = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA));
//            media.thumbnails=thumbnails;
            int index = hasDir(folders, dirName);
            if (index != -1) {
                folders.get(index).addMedias(media);
            } else {
                Folder folder = new Folder(dirName);
                folder.addMedias(media);
                folders.add(folder);
            }
        }
        if (mLoader != null) mLoader.onData(folders);
        if (cursor != null) cursor.close();
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