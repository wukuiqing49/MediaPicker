package com.wu.media.presenter;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;


import com.wkq.base.frame.mosby.MvpBasePresenter;
import com.wu.media.PickerConfig;
import com.wu.media.ui.activity.MediaActivity;
import com.wu.media.utils.AndroidQUtil;
import com.wu.media.view.MediaView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/9/15 11:30
 * <p>
 * 名 字 : MediaPickerPresenter
 * <p>
 * 简 介 :
 */
public class MediaPresenter extends MvpBasePresenter<MediaView> {

    public void initIntentData(MediaActivity mediaPickerActivity) {
        //获取配置的
        mediaPickerActivity.mOptions = mediaPickerActivity.getIntent().getParcelableExtra(PickerConfig.INTENT_KEY_OPTIONS);
        mediaPickerActivity.cachePath = mediaPickerActivity.mOptions.getCachePath();
        if (TextUtils.isEmpty(mediaPickerActivity.cachePath)) {
            if (AndroidQUtil.isAndroidQ()) {
                mediaPickerActivity.cachePath = mediaPickerActivity.getExternalFilesDir("").getPath() + File.separator + "MediaPickerPic";
            } else {
                mediaPickerActivity.cachePath = Environment.getExternalStorageDirectory().getPath() + File.separator + "MediaPickerPic";
            }
            mediaPickerActivity.mOptions.setCachePath(mediaPickerActivity.cachePath);
        } else {
            if (AndroidQUtil.isAndroidQ()) {
                mediaPickerActivity.cachePath = mediaPickerActivity.getExternalFilesDir("") + mediaPickerActivity.cachePath;
                mediaPickerActivity.mOptions.setCachePath(mediaPickerActivity.cachePath);
            }
        }

        mediaPickerActivity.videoTrimPath = mediaPickerActivity.mOptions.getVideoTrimPath();
        if (TextUtils.isEmpty(mediaPickerActivity.videoTrimPath)) {
            if (AndroidQUtil.isAndroidQ()) {
                mediaPickerActivity.videoTrimPath = mediaPickerActivity.getExternalFilesDir("").getPath() + File.separator + "MediaPickerVideo";

            } else {
                mediaPickerActivity.videoTrimPath = Environment.getExternalStorageDirectory().getPath() + File.separator + "MediaPickerVideo";
            }
            mediaPickerActivity.mOptions.setVideoTrimPath(mediaPickerActivity.videoTrimPath);

        } else {
            if (AndroidQUtil.isAndroidQ()) {
                mediaPickerActivity.videoTrimPath = mediaPickerActivity.getExternalFilesDir("") + mediaPickerActivity.videoTrimPath;
                mediaPickerActivity.mOptions.setVideoTrimPath(mediaPickerActivity.videoTrimPath);
            }
        }

        mediaPickerActivity.cropParams = mediaPickerActivity.mOptions.getCropParams();
        mediaPickerActivity.maxImageSize = mediaPickerActivity.mOptions.getMaxImageSize();
        mediaPickerActivity.maxNum = mediaPickerActivity.mOptions.getMaxNum();
        mediaPickerActivity.maxVideoSize = mediaPickerActivity.mOptions.getMaxVideoSize();
        mediaPickerActivity.selectMode = mediaPickerActivity.mOptions.getSelectMode();
        mediaPickerActivity.isReturnUri = mediaPickerActivity.mOptions.isReturnUri();
        mediaPickerActivity.isFrendCircle = mediaPickerActivity.mOptions.isFriendCircle();
        mediaPickerActivity.needCrop = mediaPickerActivity.mOptions.isNeedCrop();
        mediaPickerActivity.needCamera = mediaPickerActivity.mOptions.isNeedCamera();
        mediaPickerActivity.selectMode = mediaPickerActivity.mOptions.getSelectMode();

        if (mediaPickerActivity.needCamera) {
            if (mediaPickerActivity.selectMode < 0) {
                mediaPickerActivity.selectMode = PickerConfig.PICKER_IMAGE_VIDEO;
                mediaPickerActivity.mOptions.setSelectMode(mediaPickerActivity.selectMode);
                mediaPickerActivity.mOptions.setJumpCameraMode(PickerConfig.CAMERA_MODE_ALL);
            } else if (mediaPickerActivity.selectMode == PickerConfig.PICKER_IMAGE) {
                mediaPickerActivity.mOptions.setJumpCameraMode(PickerConfig.CAMERA_MODE_PIC);
            } else if (mediaPickerActivity.selectMode == PickerConfig.PICKER_VIDEO) {
                mediaPickerActivity.mOptions.setJumpCameraMode(PickerConfig.CAMERA_MODE_VIDEO);
            } else if (mediaPickerActivity.selectMode == PickerConfig.PICKER_ONLY_ONE_TYPE) {
                mediaPickerActivity.mOptions.setJumpCameraMode(PickerConfig.CAMERA_MODE_ALL);
            }
        }
        mediaPickerActivity.maxTime = mediaPickerActivity.mOptions.getMaxTime();
        mediaPickerActivity.showTime = mediaPickerActivity.mOptions.isShowTime();
        mediaPickerActivity.selectGift = mediaPickerActivity.mOptions.isSelectGift();
        mediaPickerActivity.singlePick = mediaPickerActivity.mOptions.isSinglePick();
        mediaPickerActivity.selects = mediaPickerActivity.mOptions.getSelects();
        mediaPickerActivity.resultCode = mediaPickerActivity.getIntent().getIntExtra(PickerConfig.RESULT_CODE, 0);
        if (mediaPickerActivity.selects == null) mediaPickerActivity.selects = new ArrayList<>();
    }

    public boolean checkPermissions(Activity activity, String[] permissions, int requestCode, String showMessage, boolean isFinish) {
        //Android6.0以下默认有权限
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;
        List<String> needList = new ArrayList<>();
        boolean needShowRationale = false;

        for (String permisson : permissions) {
            if (TextUtils.isEmpty(permisson)) continue;
            if (ActivityCompat.checkSelfPermission(activity, permisson)
                    != PackageManager.PERMISSION_GRANTED) {
                needList.add(permisson);
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permisson))
                    needShowRationale = true;
            }
        }

        if (needList.size() != 0) {
            final int count = needList.size();
            String[] needArray = needList.toArray(new String[count]);
            if (needShowRationale) {
                getView().showPermission(needArray, requestCode, showMessage, isFinish);
            } else {
                ActivityCompat.requestPermissions(activity, needArray, requestCode);
            }
            return false;
        } else {
            return true;
        }
    }

    public boolean[] onRequestPermissionsResult(Activity recordActivity, int requestCode, String[] permissions, int[] grantResults) {

        boolean result = true;
        boolean isNeverAsk = false;

        int length = grantResults.length;
        for (int i = 0; i < length; i++) {
            String permission = permissions[i];
            int grandResult = grantResults[i];
            if (grandResult == PackageManager.PERMISSION_DENIED) {
                result = false;
                if (!ActivityCompat.shouldShowRequestPermissionRationale(recordActivity, permission))
                    isNeverAsk = true;
            }
        }

        return new boolean[]{result, isNeverAsk};
    }

}
