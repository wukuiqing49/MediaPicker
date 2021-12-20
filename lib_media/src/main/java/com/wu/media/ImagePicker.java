package com.wu.media;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;


import com.wu.media.ui.activity.CustomCameraActivity;
import com.wu.media.media.entity.Media;
import com.wu.media.model.ImagePickerCropParams;
import com.wu.media.model.ImagePickerOptions;
import com.wu.media.ui.activity.MediaActivity;
import com.wu.media.ui.activity.MediaPreviewActivity;

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

    public void startNewCam(Activity activity) {
        Intent intent = new Intent(activity, CustomCameraActivity.class);
        intent.putExtra(PickerConfig.INTENT_KEY_OPTIONS, mOptions);
        intent.putExtra(PickerConfig.WHERE_JUMP_CAMERA, true);
        activity.startActivityForResult(intent, mOptions.resultCode);
    }

    /**
     * 图片选择
     *
     * @param activity
     * @param requestCode 请求码
     * @param resultCode  结果码
     */
    public void startCamera(Activity activity, ArrayList<Media> selectMedia,int requestCode, int resultCode) {
        if (selectMedia==null)selectMedia=new ArrayList<>();
        Intent intent = new Intent(activity, CustomCameraActivity.class);
        intent.putExtra(PickerConfig.INTENT_KEY_OPTIONS, mOptions);
        intent.putExtra(PickerConfig.RESULT_CODE, resultCode);
        intent.putExtra(PickerConfig.WHERE_JUMP_CAMERA, true);
        intent.putParcelableArrayListExtra(PickerConfig.WHERE_JUMP_CAMERA_SELECTS, selectMedia);
        activity.startActivityForResult(intent, requestCode);
    }



    public static final class Builder {
        private ImagePickerOptions mOptions;

        public Builder() {
            mOptions = new ImagePickerOptions();
        }

        //选择模式
        public Builder selectMode(int selectMode) {
            mOptions.setSelectMode(selectMode);
            return this;
        }

        public Builder isReturnUri(boolean isReturnUri) {
            mOptions.setReturnUri(isReturnUri);
            return this;
        }
        //视频最大大小
        public Builder maxVideoSize(int maxSize) {
            mOptions.setMaxVideoSize(maxSize);
            return this;
        }
        //图片最大大小
        public Builder maxImageSize(int maxImageSize) {
            mOptions.setMaxImageSize(maxImageSize);
            return this;
        }

        public Builder showTime(boolean showTime) {
            mOptions.setShowTime(showTime);
            return this;
        }
        //选中gif
        public Builder setSelectGif(boolean selectGif) {
            mOptions.setSelectGif(selectGif);
            return this;
        }
        //是否跳转相机以及相机模式
        public Builder setJumpCameraMode(int cameraMode) {
            mOptions.setJumpCameraMode(cameraMode);
            return this;
        }
        //结果码
        public Builder setResultCode(int resultCode) {
            mOptions.setResultCode(resultCode);
            return this;
        }
        //最大时常
        public Builder maxTime(int maxTime) {
            mOptions.setMaxTime(maxTime);
            return this;
        }
        //最大选择个数
        public Builder maxNum(int maxNum) {
            mOptions.setMaxNum(maxNum);
            return this;
        }
        //是否需要相机
        public Builder needCamera(boolean needCamera) {
            mOptions.setNeedCamera(needCamera);
            return this;
        }
        //预览专用 默认展示的list
        public Builder defaultSelectList(ArrayList<Media> select) {
            mOptions.setSelects(select);
            return this;
        }
        //缓存路径
        public Builder cachePath(String cachePath) {
            mOptions.setCachePath(cachePath);
            return this;
        }

//        public Builder videoTrimPath(String videoTrimPath) {
//            mOptions.setVideoTrimPath(videoTrimPath);
//            return this;
//        }

        //裁剪属性
        public Builder doCrop(ImagePickerCropParams cropParams) {
            mOptions.setNeedCrop(cropParams != null);
            mOptions.setCropParams(cropParams);
            return this;
        }
        //裁剪格式 默认(0,0,400,400)
        public Builder doCrop(int aspectX, int aspectY, int outputX, int outputY) {
            mOptions.setNeedCrop(true);
            mOptions.setCropParams(new ImagePickerCropParams(aspectX, aspectY, outputX, outputY));
            return this;
        }
        //是否单选
        public Builder isSinglePick(boolean isSinglePick) {
            mOptions.setSinglePick(isSinglePick);
            return this;
        }

        public ImagePicker builder() {
            return new ImagePicker(mOptions);
        }
    }
}
