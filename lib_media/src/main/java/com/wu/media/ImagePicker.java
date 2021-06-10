package com.wu.media;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;


import com.wu.media.camera.ui.DiyCameraActivity;
import com.wu.media.media.entity.Media;
import com.wu.media.model.ImagePickerCropParams;
import com.wu.media.model.ImagePickerOptions;
import com.wu.media.ui.activity.MediaActivity;
import com.wu.media.ui.activity.MediaPreviewActivity;
import com.wu.media.ui.activity.RecordActivity;

import java.util.ArrayList;

/**
 *
 */

public class ImagePicker {

    /**
     * 作 者 : wkq
     * <p>
     * 时 间 : 2020/9/15 11:26
     * <p>
     * 名 字 : ImagePicker
     * <p>
     * 简 介 :  媒体选择的启动器
     */
    public static final int DEF_RESULT_CODE = 136;

    private ImagePickerOptions mOptions;

    public ImagePickerOptions getOptions() {
        return mOptions;
    }

    private ImagePicker() {

    }

    private ImagePicker(ImagePickerOptions options) {
        this.mOptions = options;
    }

    /**
     * 图片选择
     *
     * @param activity
     * @param requestCode 请求码
     * @param resultCode  结果码
     */
    public void start(Activity activity, int requestCode, int resultCode) {
        Intent intent = new Intent(activity, MediaActivity.class);
        intent.putExtra(PickerConfig.INTENT_KEY_OPTIONS, mOptions);
        intent.putParcelableArrayListExtra(PickerConfig.DEFAULT_SELECTED_LIST, mOptions.selects);
        intent.putExtra(PickerConfig.RESULT_CODE, resultCode);
        activity.startActivityForResult(intent, requestCode);
    }


    /**
     * 图片选择
     *
     * @param activity
     * @param requestCode 请求码
     * @param resultCode  结果码
     */
    public void startCamera(Activity activity, int requestCode, int resultCode) {
        Intent intent = new Intent(activity, RecordActivity.class);
        intent.putExtra(PickerConfig.INTENT_KEY_OPTIONS, mOptions);
        intent.putExtra(PickerConfig.RESULT_CODE, resultCode);
        intent.putExtra(PickerConfig.WHERE_JUMP_CAMERA, true);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 图片选择
     *
     * @param activity
     * @param requestCode 请求码
     * @param resultCode  结果码
     */
    public void startCamera(Activity activity, ArrayList<Media> selectMedia, int requestCode, int resultCode) {
        if (selectMedia==null)selectMedia=new ArrayList<>();
        DiyCameraActivity.start( activity, mOptions.cachePath,selectMedia,requestCode,resultCode,259,"");
    }

    /**
     * 图片选择
     *
     * @param fragment
     * @param requestCode 请求码
     * @param resultCode  结果码
     */
    public void start(Fragment fragment, int requestCode, int resultCode) {
        Intent intent = new Intent(fragment.getActivity(), MediaActivity.class);
        intent.putExtra(PickerConfig.INTENT_KEY_OPTIONS, mOptions);
        intent.putParcelableArrayListExtra(PickerConfig.DEFAULT_SELECTED_LIST, mOptions.selects); // (Optional)
        intent.putExtra(PickerConfig.RESULT_CODE, resultCode);
        fragment.getActivity().startActivityForResult(intent, requestCode);
    }

    public void startPreview(Activity activity, int position, ArrayList<Media> selects, int requestCode, int resultCode) {

        Intent intent = new Intent(activity, MediaPreviewActivity.class);
        intent.putParcelableArrayListExtra(PickerConfig.SELECTED_LIST, selects);
        intent.putExtra(PickerConfig.CURRENT_POSITION, position);
        intent.putExtra(PickerConfig.RESULT_CODE, resultCode);
        activity.startActivityForResult(intent, requestCode);
    }

    public static final class Builder {
        private ImagePickerOptions mOptions;

        public Builder() {
            mOptions = new ImagePickerOptions();
        }


        public Builder selectMode(int selectMode) {
            mOptions.setSelectMode(selectMode);
            return this;
        }

        public Builder isReturnUri(boolean isReturnUri) {
            mOptions.setReturnUri(isReturnUri);
            return this;
        }

        public Builder maxVideoSize(int maxSize) {
            mOptions.setMaxVideoSize(maxSize);
            return this;
        }

        public Builder maxImageSize(int maxImageSize) {
            mOptions.setMaxImageSize(maxImageSize);
            return this;
        }

        public Builder showTime(boolean showTime) {
            mOptions.setShowTime(showTime);
            return this;
        }

        public Builder setSelectGif(boolean selectGif) {
            mOptions.setSelectGif(selectGif);
            return this;
        }

        public Builder setJumpCameraMode(int cameraMode) {
            mOptions.setJumpCameraMode(cameraMode);
            return this;
        }

        public Builder maxTime(int maxTime) {
            mOptions.setMaxTime(maxTime);
            return this;
        }

        public Builder maxNum(int maxNum) {
            mOptions.setMaxNum(maxNum);
            return this;
        }

        public Builder needCamera(boolean needCamera) {
            mOptions.setNeedCamera(needCamera);
            return this;
        }

        public Builder defaultSelectList(ArrayList<Media> select) {
            mOptions.setSelects(select);
            return this;
        }

        public Builder cachePath(String cachePath) {
            mOptions.setCachePath(cachePath);
            return this;
        }

//        public Builder videoTrimPath(String videoTrimPath) {
//            mOptions.setVideoTrimPath(videoTrimPath);
//            return this;
//        }

        public Builder isFriendCircle(boolean isFriendCircle) {
            mOptions.setFriendCircle(isFriendCircle);
            return this;
        }

        public Builder doCrop(ImagePickerCropParams cropParams) {
            mOptions.setNeedCrop(cropParams != null);
            mOptions.setCropParams(cropParams);
            return this;
        }

        public Builder doCrop(int aspectX, int aspectY, int outputX, int outputY) {
            mOptions.setNeedCrop(true);
            mOptions.setCropParams(new ImagePickerCropParams(aspectX, aspectY, outputX, outputY));
            return this;
        }

        public Builder isSinglePick(boolean isSinglePick) {
            mOptions.setSinglePick(isSinglePick);
            return this;
        }

        public ImagePicker builder() {
            return new ImagePicker(mOptions);
        }
    }
}
