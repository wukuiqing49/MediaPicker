package com.wu.media.camera.frame.view;

import android.os.Build;
import android.view.View;
import android.view.WindowManager;

import com.wkq.base.frame.mosby.delegate.MvpView;
import com.wkq.base.utils.StatusBarUtil2;
import com.wu.media.camera.ui.CustomCameraActivity;

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

    }

}
