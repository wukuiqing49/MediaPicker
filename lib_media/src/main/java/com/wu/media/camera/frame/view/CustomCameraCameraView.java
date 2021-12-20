package com.wu.media.camera.frame.view;

import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import androidx.activity.OnBackPressedCallback;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.wkq.base.frame.mosby.delegate.MvpView;
import com.wkq.base.utils.AlertUtil;
import com.wkq.base.utils.StatusBarUtil2;
import com.wu.media.R;
import com.wu.media.ui.activity.CustomCameraActivity;
import com.wu.media.ui.fragment.RecordPreviewFragment;
import com.wu.media.utils.AlertDialogUtils;
import com.wu.media.utils.PermissionChecker;
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
        mActivity.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        //设置全屏，不延伸到刘海屏
        mActivity. getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (mActivity.getSupportActionBar() != null) mActivity.getSupportActionBar().hide();
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

    //检测权限
    public void checkPermisssion() {
        boolean hasPermissions;
        if (mActivity.isNeedAudio){
            hasPermissions = mActivity.getPresenter().checkPermissions(mActivity, mActivity.permissionsRecord, mActivity.PERMISISSION_CODE_ASK, true);
        }else {
            hasPermissions = mActivity.getPresenter().checkPermissions(mActivity, mActivity.permissionsRecordNoAudio, mActivity.PERMISISSION_CODE_ASK, true);
        }
        if (hasPermissions) {
            mActivity.binding.rcc.setRecordType(mActivity.mOptions.jumpCameraMode);
            mActivity.binding.rcc.setMaxTime(mActivity.mOptions.maxTime);        }

    }




    public void showPermission(String[] needList, int requestCode) {
        if (mActivity.dialog != null) mActivity.dialog.dismiss();
        mActivity.dialog = AlertDialogUtils.showTwoButtonDialog(
                mActivity,
                mActivity.getString(R.string.dialog_imagepicker_permission_nerver_ask_cancel),
                mActivity.getString(R.string.dialog_imagepicker_permission_confirm),
                mActivity.getString(R.string.dialog_imagepicker_permission_camera_nerver_ask_message),
                R.color.color_dialog_btn, R.color.color_ffa300, new AlertDialogUtils.DialogTwoListener() {
                    @Override
                    public void onClickLeft(Dialog dialog) {
                        dialog.dismiss();
                        showMessage("无法获取存读取权限,您的app将无法正常使用");
                        mActivity.finish();
                    }

                    @Override
                    public void onClickRight(Dialog dialog) {
                        dialog.dismiss();
                        ActivityCompat.requestPermissions(mActivity, needList, requestCode);
                    }
                });
    }

    public void showPermissionPerpetual(int requestCode) {

        if (mActivity.dialog != null) mActivity.dialog.dismiss();
        mActivity.dialog = AlertDialogUtils.showTwoButtonDialog(
                mActivity,
                mActivity.getString(R.string.dialog_imagepicker_permission_nerver_ask_cancel),
                mActivity.getString(R.string.dialog_imagepicker_permission_confirm),
                mActivity.getString(R.string.dialog_imagepicker_permission_camera_nerver_ask_message),
                R.color.color_dialog_btn, R.color.color_ffa300, new AlertDialogUtils.DialogTwoListener() {
                    @Override
                    public void onClickLeft(Dialog dialog) {
                        dialog.dismiss();
                        showMessage("无法获取存读取权限,您的app将无法正常使用");
                        mActivity.finish();
                    }

                    @Override
                    public void onClickRight(Dialog dialog) {
                        dialog.dismiss();
                        PermissionChecker.settingPermissionActivity(mActivity, requestCode);
                        mActivity.isNeverAsk = true;
                    }
                });

    }
    public void showMessage(String message) {

        if (TextUtils.isEmpty(message) || mActivity == null || mActivity.isFinishing()) return;
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertUtil.showDeftToast(mActivity, message);
            }
        });


    }

    public void initView() {
        initFull();

        //处理fragment 回调
        mActivity.getOnBackPressedDispatcher().addCallback(mActivity, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Fragment currentFragment = mActivity.getSupportFragmentManager().findFragmentById(R.id.pre_frame);
                if (currentFragment instanceof RecordPreviewFragment) {
                    mActivity.getSupportFragmentManager().popBackStack();
                    mActivity.binding.rcc.setBottomVisibility(true);
                } else {
                    mActivity.binding.rcc.setBottomVisibility(true);
                    mActivity.binding.fragment.setVisibility(View.GONE);
                }
            }
        });
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        checkPermisssion();
    }

    public void processFile(MediaShowObservable.MediaShowInfo info) {
        if (info.getType() == 0) {
            mActivity.getSupportFragmentManager().beginTransaction().add(R.id.fragment, RecordPreviewFragment.newInstance(0, info.getFilePath())).addToBackStack("").commitAllowingStateLoss();
        } else {
            mActivity.getSupportFragmentManager().beginTransaction().add(R.id.fragment, RecordPreviewFragment.newInstance(1, info.getFilePath())).addToBackStack("").commitAllowingStateLoss();
        }
        mActivity.binding.fragment.setVisibility(View.VISIBLE);

    }

}
