package com.wu.media.view;

import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import androidx.activity.OnBackPressedCallback;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.wkq.base.frame.mosby.delegate.MvpView;
import com.wkq.base.utils.AlertUtil;
import com.wu.media.R;
import com.wu.media.ui.activity.RecordActivity;
import com.wu.media.ui.fragment.RecordFragment;
import com.wu.media.ui.fragment.RecordPreviewFragment;
import com.wu.media.utils.AlertDialogUtils;
import com.wu.media.utils.PermissionChecker;


/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/9/22 13:32
 * <p>
 * 名 字 : RecordView
 * <p>
 * 简 介 :
 */
public class RecordView implements MvpView {

    RecordActivity mActivity;


    public RecordView(RecordActivity mActivity) {
        this.mActivity = mActivity;
    }

    public void initView() {
        processFullScreen();
        //处理fragment 回调
        mActivity.getOnBackPressedDispatcher().addCallback(mActivity, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Fragment currentFragment = mActivity.getSupportFragmentManager().findFragmentById(R.id.pre_frame);
                if (currentFragment instanceof RecordPreviewFragment) {
                    mActivity.getSupportFragmentManager().popBackStack();
                } else {
                    mActivity.finish();
                }
            }
        });


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        checkPermisssion();
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
            mActivity.getSupportFragmentManager().beginTransaction().replace(R.id.pre_frame, RecordFragment.newInstance()).addToBackStack("").commitAllowingStateLoss();
        }

    }
    /**
     *   处理刘海屏全屏问题
     */
    public void processFullScreen() {

        WindowManager.LayoutParams lp= mActivity.getWindow().getAttributes();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        mActivity.getWindow().setAttributes(lp);
        // 设置页面全屏显示
        View decorView  = mActivity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN| View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
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
}
