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

import java.io.File;
import java.util.ArrayList;


/**
 * Created by dmcBig on 2017/6/9.
 */

public class VideoLoader extends LoaderM implements LoaderManager.LoaderCallbacks {
    private final String TAG = this.getClass().getName();
    private static VideoLoader instance = null;
    private static ArrayList<Folder> folders=new ArrayList<>();
    String[] MEDIA_PROJECTION = {
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.PARENT,
            MediaStore.Video.VideoColumns._ID,
            MediaStore.Video.VideoColumns.DURATION};

    Context mContext;
    DataCallback mLoader;

    public synchronized static VideoLoader getInstance(Context context, DataCallback loader) {
        if (instance == null || folders == null) {
            instance = new VideoLoader(context);
            folders = new ArrayList<>();
        }
        instance.setmLoader(loader);
        return instance;
    }

    public VideoLoader(Context context) {
        this.mContext = context;
    }

    public void setmLoader(DataCallback mLoader) {
        this.mLoader = mLoader;
    }

    @Override
    public Loader onCreateLoader(int picker_type, Bundle bundle) {
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
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

        Folder allFolder = new Folder(mContext.getResources().getString(R.string.all_video));
        folders.add(allFolder);
        Cursor cursor = (Cursor) o;
        while (cursor.moveToNext()) {
            //获取的视频路径
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));
            File file = new File(path);
            if (!file.exists()) {
                continue;
            }

            long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE));
            int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DURATION));
            if (size < 512 || duration < 1) continue;
            String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME));
            long dateTime = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED));
            int mediaType = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE));

            int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID));


            Uri photoUri = Uri.parse(MediaStore.Video.Media.EXTERNAL_CONTENT_URI.toString() + File.separator + id);
            String fileUri = photoUri.toString();

            String dirName = getParent(path);
            if (TextUtils.isEmpty(dirName)) continue;
            Media media = new Media(path, name, dateTime, mediaType, size, id, dirName, duration, fileUri);
//            String thumbnails = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA));
//            media.thumbnails=thumbnails;
            allFolder.addMedias(media);

            int index = hasDir(folders, dirName);
            if (index != -1) {
                folders.get(index).addMedias(media);
            } else {
                Folder folder = new Folder(dirName);
                folder.addMedias(media);
                folders.add(folder);
            }
        }
//        Log.e(TAG, "数据为空: " + folders.size());

        mLoader.onData(folders);
        cursor.close();
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
