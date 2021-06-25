package com.wu.media.camera.frame.presenter;

import com.wkq.base.frame.mosby.MvpBasePresenter;
import com.wu.media.PickerConfig;
import com.wu.media.camera.frame.view.CustomCameraCameraView;
import com.wu.media.camera.ui.CustomCameraActivity;

/**
 * @author wkq
 * @date 2021年06月24日 15:19
 * @des
 */

public class CustomCameraCameraPresenter extends MvpBasePresenter<CustomCameraCameraView> {


    public void initData(CustomCameraActivity customCameraActivity) {
        customCameraActivity.shootMode = customCameraActivity.getIntent().getIntExtra(PickerConfig.CAMERA_SELECT_MODE, 0);
        customCameraActivity.cachePath = customCameraActivity.getIntent().getStringExtra(PickerConfig.CACHE_PATH);
        customCameraActivity.resultCode = customCameraActivity.getIntent().getIntExtra(PickerConfig.RESULT_CODE, PickerConfig.DEFAULT_RESULT_CODE);
        customCameraActivity.maxTime = customCameraActivity.getIntent().getIntExtra(PickerConfig.MAX_TIME, -1) + 900;//录制时长增加900毫秒,解决部分手机只显示时间少一秒问题
    }
}
