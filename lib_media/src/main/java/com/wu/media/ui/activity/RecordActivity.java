package com.wu.media.ui.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.wkq.base.frame.activity.MvpBindingActivity;
import com.wkq.base.utils.StatusBarUtil2;
import com.wu.media.PickerConfig;
import com.wu.media.R;
import com.wu.media.databinding.ActivityRecordBinding;
import com.wu.media.media.entity.Media;
import com.wu.media.model.ImagePickerOptions;
import com.wu.media.presenter.RecordPresenter;
import com.wu.media.ui.fragment.RecordPreviewFragment;
import com.wu.media.utils.observable.MeidaResultObservable;
import com.wu.media.view.RecordView;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;


/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/9/22 13:31
 * <p>
 * 名 字 : RecordActivity
 * <p>
 * 简 介 :录像 拍照页面
 */
public class RecordActivity extends MvpBindingActivity<RecordView, RecordPresenter,  ActivityRecordBinding> implements Observer {


    public ImagePickerOptions mOptions;
    public boolean isInit = false;
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
        intent.setClass(context, RecordActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_record;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        StatusBarUtil2.setTransparentForWindow(this);
        StatusBarUtil2.addTranslucentView(this, 0);
//        QiniuVideoUtil.chcekInitState(this, isInit -> {
//            if (isInit) {
//
//                if (savedInstanceState != null) {
//                    mOptions = savedInstanceState.getParcelable(PickerConfig.INTENT_KEY_OPTIONS);
//                    resultCode = savedInstanceState.getInt(PickerConfig.RESULT_CODE, resultCode);
//                    isJumpCamera = savedInstanceState.getBoolean(PickerConfig.WHERE_JUMP_CAMERA, isJumpCamera);
//                } else {
//                    resultCode = getIntent().getIntExtra(PickerConfig.RESULT_CODE, 0);
//                    isJumpCamera = getIntent().getBooleanExtra(PickerConfig.WHERE_JUMP_CAMERA, false);
//                    selectMedia = getIntent().getParcelableArrayListExtra(PickerConfig.WHERE_JUMP_CAMERA_SELECTS);
//                    mOptions = getIntent().getParcelableExtra(PickerConfig.INTENT_KEY_OPTIONS);
//                }
//
//                MeidaResultObservable.getInstance().addObserver(this);
//                if (mOptions != null) {
//                    if (mOptions.getJumpCameraMode() == PickerConfig.CAMERA_MODE_PIC) {
//                        isNeedAudio = false;
//                    } else {
//                        isNeedAudio = true;
//                    }
//                } else {
//                    getMvpView().showMessage("初始化异常");
//                    onBackPressed();
//                }
//
//                if (getMvpView() != null) getMvpView().initView();
//            } else {
//                getMvpView().showMessage("初始化异常");
//                onBackPressed();
//            }
//
//        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mOptions != null) outState.putParcelable(PickerConfig.INTENT_KEY_OPTIONS, mOptions);
        outState.putInt(PickerConfig.RESULT_CODE, resultCode);
        outState.putBoolean(PickerConfig.WHERE_JUMP_CAMERA, isJumpCamera);
        if (selectMedia != null)
            outState.putParcelableArrayList(PickerConfig.WHERE_JUMP_CAMERA_SELECTS, selectMedia);
        super.onSaveInstanceState(outState);
    }

    public void showPreViewImg(Bitmap plbitMap) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                preBitmap = plbitMap;
                getSupportFragmentManager().beginTransaction().add(R.id.pre_frame, RecordPreviewFragment.newInstance(0, "")).addToBackStack("").commitAllowingStateLoss();
            }
        });

    }

    public void showVideo(String path) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getSupportFragmentManager().beginTransaction().replace(R.id.pre_frame, RecordPreviewFragment.newInstance(1, path)).addToBackStack("").commitAllowingStateLoss();
            }
        });

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
    protected void onResume() {
        super.onResume();
        if (isNeverAsk) {
            isNeverAsk = false;
            //永久拒绝后，返回页面时检测权限并初始化
            getMvpView().checkPermisssion();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MeidaResultObservable.getInstance().deleteObserver(this);
        if (preBitmap != null) {
            //  glide 加载维护了 bitmap 回收
//            if (!preBitmap.isRecycled()){
//                preBitmap.recycle();
//            }
            preBitmap = null;
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
            } else {
                finish();
            }
        }

    }


    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.pre_frame);
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


}
