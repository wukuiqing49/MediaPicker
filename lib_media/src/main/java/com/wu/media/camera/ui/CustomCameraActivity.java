package com.wu.media.camera.ui;


import android.os.Bundle;

import com.wkq.base.frame.activity.MvpBindingActivity;
import com.wu.media.R;
import com.wu.media.camera.frame.presenter.CustomCameraCameraPresenter;
import com.wu.media.camera.frame.view.CustomCameraCameraView;
import com.wu.media.databinding.ActivityCustomCameraBinding;

/**
 * @author wkq
 * @date 2021年06月24日 15:14
 * @des 自定义相机
 */

public class CustomCameraActivity extends MvpBindingActivity<CustomCameraCameraView, CustomCameraCameraPresenter, ActivityCustomCameraBinding> {

    //模式
    public int shootMode;
    //缓存地址
    public String cachePath;
    //结果码
    public int resultCode;
    //最长录制时间
    public int maxTime;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_custom_camera;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMvpView().initView();
        getPresenter().initData(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.rcc.preview.resume();
    }



    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



}
