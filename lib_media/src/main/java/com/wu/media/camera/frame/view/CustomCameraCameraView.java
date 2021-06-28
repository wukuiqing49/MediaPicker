package com.wu.media.camera.frame.view;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import com.wkq.base.frame.mosby.delegate.MvpView;
import com.wkq.base.utils.StatusBarUtil2;
import com.wu.media.R;
import com.wu.media.ui.activity.CustomCameraActivity;
import com.wu.media.ui.fragment.RecordPreviewFragment;
import com.wu.media.utils.observable.MediaShowObservable;

/**
 * @author wkq
 * @date 2021年06月24日 15:16
 * @des
 */

public class CustomCameraCameraView implements MvpView {

    CustomCameraActivity mActivity;

    public CustomCameraCameraView(CustomCameraActivity activity) {
        mActivity = activity;
    }

    //全屏设置
    private void initFull() {
        StatusBarUtil2.addTranslucentView(mActivity, 0);
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        mActivity.getWindow().setAttributes(lp);
        // 设置页面全屏显示
        View decorView = mActivity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    public void initView() {
        initFull();
        mActivity.binding.rcc.setRecordType(0);
        mActivity.binding.rcc.setMaxTime(mActivity.maxTime);
        //处理fragment 回调
        mActivity.getOnBackPressedDispatcher().addCallback(mActivity, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Fragment currentFragment = mActivity.getSupportFragmentManager().findFragmentById(R.id.pre_frame);
                if (currentFragment instanceof RecordPreviewFragment) {
                    mActivity.getSupportFragmentManager().popBackStack();
                } else {
//                    mActivity.finish();
                    mActivity.binding.fragment.setVisibility(View.GONE);
                }
            }
        });
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public void processFile(MediaShowObservable.MediaShowInfo info) {
        if (info.getType() == 0) {
            mActivity.getSupportFragmentManager().beginTransaction().add(R.id.fragment, RecordPreviewFragment.newInstance(0, info.getFilePath())).addToBackStack("").commitAllowingStateLoss();
        } else {
            mActivity.getSupportFragmentManager().beginTransaction().add(R.id.fragment, RecordPreviewFragment.newInstance(1, info.getFilePath())).addToBackStack("").commitAllowingStateLoss();
        }
        mActivity.binding.fragment.setVisibility(View.VISIBLE);

    }

    public void processShowView() {
        mActivity.binding.fragment.setVisibility(View.GONE);
    }
}
