package com.wu.media.ui.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.wkq.base.frame.activity.MvpBindingActivity;
import com.wu.media.PickerConfig;
import com.wu.media.R;
import com.wu.media.camera.frame.presenter.CustomCameraCameraPresenter;
import com.wu.media.camera.frame.view.CustomCameraCameraView;
import com.wu.media.databinding.ActivityCustomCameraBinding;
import com.wu.media.model.ImagePickerOptions;
import com.wu.media.ui.fragment.RecordPreviewFragment;
import com.wu.media.utils.observable.MediaShowObservable;

import java.util.Observable;
import java.util.Observer;

/**
 * @author wkq
 * @date 2021年06月24日 15:14
 * @des 自定义相机
 */

public class CustomCameraActivity extends MvpBindingActivity<CustomCameraCameraView, CustomCameraCameraPresenter, ActivityCustomCameraBinding> implements Observer {

    //模式
    public int shootMode;
    //结果码
    public int resultCode;
    //最长录制时间
    public int maxTime;

    /**
     * 跳转到该界面的公共方法
     *
     * @param activity 发起跳转的Activity
     */
    public static void start(Activity activity, int cameraType, int maxTime, int resultCode) {
        Intent intent = new Intent(activity, ImageCropActivity.class);
        intent.putExtra(PickerConfig.RESULT_CODE, resultCode);
        intent.putExtra(PickerConfig.CAMERA_TYPE, cameraType);
        intent.putExtra(PickerConfig.MAX_TIME, maxTime);
        activity.startActivityForResult(intent, PickerConfig.REQUEST_CODE_CROP);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_custom_camera;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPresenter().initData(this);
        MediaShowObservable.getInstance().addObserver(this);
        getMvpView().initView();

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
        MediaShowObservable.getInstance().deleteObserver(this);
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
        if (currentFragment == null) {
            finish();
        } else {
            if (currentFragment instanceof RecordPreviewFragment) {
              getMvpView().processShowView();
            } else {
                finish();
            }
        }

    }


    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof MediaShowObservable && arg != null) {
            MediaShowObservable.MediaShowInfo info = (MediaShowObservable.MediaShowInfo) arg;
            getMvpView().processFile(info);
        }
    }
}
