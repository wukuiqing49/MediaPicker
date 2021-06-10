package com.wu.media.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.HashMap;

/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2021/1/25 15:18
 * <p>
 * 名 字 : ThumbnailsUtils
 * <p>
 * 简 介 :
 */
public class ThumbnailsUtils {
    static HashMap<String, String> thumbnails = new HashMap<String, String>();

    public static void getThumbnails(Context context) {
        String[] projection = {MediaStore.Video.Thumbnails._ID, MediaStore.Video.Thumbnails.VIDEO_ID,
                MediaStore.Video.Thumbnails.DATA};

        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, projection, null, null, null);
        if (cursor == null) return;
        while (cursor.moveToNext()) {
            int _id;
            int image_id;
            String image_path;
            int _idColumn = cursor.getColumnIndex(MediaStore.Video.Thumbnails._ID);
            int image_idColumn = cursor.getColumnIndex(MediaStore.Video.Thumbnails.VIDEO_ID);
            int dataColumn = cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA);
            _id = cursor.getInt(_idColumn);
            image_id = cursor.getInt(image_idColumn);
            image_path = cursor.getString(dataColumn);
            thumbnails.put(image_id + "", image_path);
        }
        getImageThumbnails(context);
    }

    public static void getImageThumbnails(Context context) {
        String[] projection = {MediaStore.Images.Thumbnails._ID, MediaStore.Images.Thumbnails.IMAGE_ID,
                MediaStore.Images.Thumbnails.DATA};

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projection, null, null, null);
        if (cursor == null) return;
        while (cursor.moveToNext()) {
            int _id;
            int image_id;
            String image_path;
            int _idColumn = cursor.getColumnIndex(MediaStore.Images.Thumbnails._ID);
            int image_idColumn = cursor.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID);
            int dataColumn = cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA);
            _id = cursor.getInt(_idColumn);
            image_id = cursor.getInt(image_idColumn);
            image_path = cursor.getString(dataColumn);
            thumbnails.put(image_id + "", image_path);
        }
    }


}
