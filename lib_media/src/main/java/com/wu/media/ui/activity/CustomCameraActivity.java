package com.wu.media.ui.activity;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.wkq.base.frame.activity.MvpBindingActivity;
import com.wu.media.PickerConfig;
import com.wu.media.R;
import com.wu.media.camera.frame.presenter.CustomCameraCameraPresenter;
import com.wu.media.camera.frame.view.CustomCameraCameraView;
import com.wu.media.databinding.ActivityCustomCameraBinding;
import com.wu.media.media.entity.Media;
import com.wu.media.model.ImagePickerOptions;
import com.wu.media.ui.fragment.RecordPreviewFragment;
import com.wu.media.ui.widget.record.observable.RecordCameraViewObservable;
import com.wu.media.utils.observable.MediaShowObservable;
import com.wu.media.utils.observable.MeidaResultObservable;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * @author wkq
 * @date 2021年06月24日 15:14
 * @des 新版本自定义相机
 */

public class CustomCameraActivity extends MvpBindingActivity<CustomCameraCameraView, CustomCameraCameraPresenter, ActivityCustomCameraBinding> implements Observer {

    public ImagePickerOptions mOptions;
    public Bitmap preBitmap;
    public String videoPath;
    public int resultCode;
    //是否是直接跳转相机
    public boolean isJumpCamera;
    //权限申请弹窗
    public Dialog dialog;
    public int PERMISISSION_CODE_ASK = 10010;
    //是否没有申请权限
    public boolean isNeverAsk;
    public ArrayList<Media> selectMedia;
    // 是否需要录音权限
    public boolean isNeedAudio = true;
    public static String[] permissionsRecord = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static String[] permissionsRecordNoAudio = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static void newInstance(Context context, ImagePickerOptions mOptions) {
        Intent intent = new Intent();
        intent.putExtra(PickerConfig.INTENT_KEY_OPTIONS, mOptions);
        intent.setClass(context, CustomCameraActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_custom_camera;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (savedInstanceState != null) {
            mOptions = savedInstanceState.getParcelable(PickerConfig.INTENT_KEY_OPTIONS);
            resultCode = savedInstanceState.getInt(PickerConfig.RESULT_CODE, resultCode);
            isJumpCamera = savedInstanceState.getBoolean(PickerConfig.WHERE_JUMP_CAMERA, isJumpCamera);
        } else {
            resultCode = getIntent().getIntExtra(PickerConfig.RESULT_CODE, 0);
            isJumpCamera = getIntent().getBooleanExtra(PickerConfig.WHERE_JUMP_CAMERA, false);
            selectMedia = getIntent().getParcelableArrayListExtra(PickerConfig.WHERE_JUMP_CAMERA_SELECTS);
            mOptions = getIntent().getParcelableExtra(PickerConfig.INTENT_KEY_OPTIONS);
        }
        getMvpView().initView();
        MeidaResultObservable.getInstance().addObserver(this);
        if (mOptions != null) {
            if (mOptions.getJumpCameraMode() == PickerConfig.CAMERA_MODE_PIC) {
                isNeedAudio = false;
            } else {
                isNeedAudio = true;
            }
        } else {
            getMvpView().showMessage("初始化异常");
            onBackPressed();
        }
        MediaShowObservable.getInstance().addObserver(this);
        RecordCameraViewObservable.newInstance().addObserver(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isNeverAsk) {
            isNeverAsk = false;
            //永久拒绝后，返回页面时检测权限并初始化
            getMvpView().checkPermisssion();
        }
        binding.rcc.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        binding.rcc.setFlashState(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISISSION_CODE_ASK) {

            boolean[] hasPermissions = getPresenter().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
            if (hasPermissions[0]) {
                if (getMvpView() != null) { //权限正常
                    getMvpView().checkPermisssion();
                }
            } else if (hasPermissions[1]) { //权限永久拒绝的处理
                getMvpView().showPermissionPerpetual(requestCode);
            } else { //权限没有全部同意
                if (isNeedAudio) {
                    getMvpView().showPermission(permissionsRecord, PERMISISSION_CODE_ASK);
                } else {
                    getMvpView().showPermission(permissionsRecordNoAudio, PERMISISSION_CODE_ASK);
                }

            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaShowObservable.getInstance().deleteObserver(this);
        RecordCameraViewObservable.newInstance().deleteObserver(this);
        MeidaResultObservable.getInstance().deleteObserver(this);
        if (preBitmap != null) {
            preBitmap = null;
        }
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
        if (currentFragment == null) {
            finish();
        } else {
            if (currentFragment instanceof RecordPreviewFragment) {
                getSupportFragmentManager().popBackStack();
            } else {
                finish();
            }
        }

    }


    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof MeidaResultObservable) {
            Media media = (Media) arg;
            if (isJumpCamera) {
                if (selectMedia == null)
                    selectMedia = new ArrayList<Media>();
                selectMedia.add(media);
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra(PickerConfig.EXTRA_RESULT, selectMedia);
                setResult(resultCode, intent);
                finish();
            }
        } else if (o instanceof MediaShowObservable && arg != null) {
            MediaShowObservable.MediaShowInfo info = (MediaShowObservable.MediaShowInfo) arg;
            getMvpView().processFile(info);
        } else if (o instanceof RecordCameraViewObservable) {
            int type = (int) arg;
            if (type == RecordCameraViewObservable.CLICK_FINISH) finish();
        }
    }
}
