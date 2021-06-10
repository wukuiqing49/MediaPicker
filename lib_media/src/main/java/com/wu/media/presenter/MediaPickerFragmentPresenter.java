package com.wu.media.presenter;

import android.os.Environment;
import android.text.TextUtils;


import com.wkq.base.frame.mosby.MvpBasePresenter;
import com.wu.media.PickerConfig;
import com.wu.media.ui.activity.MediaActivity;
import com.wu.media.utils.AndroidQUtil;
import com.wu.media.view.MediaPickerFragmentView;

import java.io.File;
import java.util.ArrayList;

/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/9/15 11:30
 * <p>
 * 名 字 : MediaPickerPresenter
 * <p>
 * 简 介 :
 */
public class MediaPickerFragmentPresenter extends MvpBasePresenter<MediaPickerFragmentView> {

    public void initIntentData(MediaActivity mediaPickerActivity) {
        //获取配置的
        mediaPickerActivity.mOptions  = mediaPickerActivity.getIntent().getParcelableExtra(PickerConfig.INTENT_KEY_OPTIONS);
        mediaPickerActivity.cachePath =  mediaPickerActivity.mOptions .getCachePath();
        if (TextUtils.isEmpty(mediaPickerActivity.cachePath)) {
            if (AndroidQUtil.isAndroidQ()) {
                mediaPickerActivity.cachePath = mediaPickerActivity.getExternalFilesDir("").getPath() + File.separator + "MediaPickerPic";
            } else {
                mediaPickerActivity.cachePath = Environment.getExternalStorageDirectory().getPath() + File.separator + "MediaPickerPic";
            }

        } else {
            if (AndroidQUtil.isAndroidQ()) {
                mediaPickerActivity.cachePath = mediaPickerActivity.getExternalFilesDir("") + mediaPickerActivity.cachePath;
            }
        }

        mediaPickerActivity.videoTrimPath =  mediaPickerActivity.mOptions .getVideoTrimPath();
        if (TextUtils.isEmpty(mediaPickerActivity.videoTrimPath)) {
            if (AndroidQUtil.isAndroidQ()) {
                mediaPickerActivity.videoTrimPath = mediaPickerActivity.getExternalFilesDir("").getPath() + File.separator + "MediaPickerVideo";
            } else {
                mediaPickerActivity.videoTrimPath = Environment.getExternalStorageDirectory().getPath() + File.separator + "MediaPickerVideo";
            }

        } else {
            if (AndroidQUtil.isAndroidQ()) {
                mediaPickerActivity.videoTrimPath = mediaPickerActivity.getExternalFilesDir("") + mediaPickerActivity.videoTrimPath;
            }
        }

        mediaPickerActivity.cropParams =  mediaPickerActivity.mOptions .getCropParams();
        mediaPickerActivity.maxImageSize =  mediaPickerActivity.mOptions .getMaxImageSize();
        mediaPickerActivity.maxNum =  mediaPickerActivity.mOptions .getMaxNum();
        mediaPickerActivity.maxVideoSize =  mediaPickerActivity.mOptions .getMaxVideoSize();
        mediaPickerActivity.selectMode =  mediaPickerActivity.mOptions .getSelectMode();
        mediaPickerActivity.isReturnUri =  mediaPickerActivity.mOptions .isReturnUri();
        mediaPickerActivity.isFrendCircle =  mediaPickerActivity.mOptions .isFriendCircle();
        mediaPickerActivity.needCrop =  mediaPickerActivity.mOptions .isNeedCrop();
        mediaPickerActivity.needCamera =  mediaPickerActivity.mOptions .isNeedCamera();
        mediaPickerActivity.showTime =  mediaPickerActivity.mOptions .isShowTime();
        mediaPickerActivity.selectGift =  mediaPickerActivity.mOptions .isSelectGift();
        mediaPickerActivity.singlePick =  mediaPickerActivity.mOptions .isSinglePick();
        mediaPickerActivity.selects =  mediaPickerActivity.mOptions .getSelects();
        if (mediaPickerActivity.selects==null)mediaPickerActivity.selects=new ArrayList<>();
    }
}
