package com.wu.media.camera.frame.presenter;

import com.wkq.base.frame.mosby.MvpBasePresenter;
import com.wu.media.PickerConfig;
import com.wu.media.camera.frame.view.CustomCameraCameraView;
import com.wu.media.ui.activity.CustomCameraActivity;

/**
 * @author wkq
 * @date 2021年06月24日 15:19
 * @des
 */

public class CustomCameraCameraPresenter extends MvpBasePresenter<CustomCameraCameraView> {


    public void initData(CustomCameraActivity customCameraActivity) {
        customCameraActivity.resultCode = customCameraActivity.getIntent().getIntExtra(PickerConfig.RESULT_CODE, PickerConfig.DEFAULT_RESULT_CODE);
        //类型
        customCameraActivity.shootMode = customCameraActivity.getIntent().getIntExtra(PickerConfig.CAMERA_TYPE, 0);
        //最长时间
        if (customCameraActivity.getIntent().hasExtra(PickerConfig.MAX_TIME)){
            customCameraActivity.maxTime = customCameraActivity.getIntent().getIntExtra(PickerConfig.MAX_TIME, 10*000) + 900;//录制时长增加900毫秒,解决部分手机只显示时间少一秒问题
        }else {
            customCameraActivity.maxTime = PickerConfig.RECODE_MAX_TIME + 900;//录制时长增加900毫秒,解决部分手机只显示时间少一秒问题
        }
    }
}
