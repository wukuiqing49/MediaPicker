package com.wu.media.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;


import com.wkq.base.frame.mosby.delegate.MvpView;
import com.wkq.base.utils.AlertUtil;
import com.wu.media.ui.activity.RecordActivity;
import com.wu.media.ui.fragment.RecordFragment;
import com.wu.media.utils.ScreenUtils;

import java.io.File;

/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/10/29 10:05
 * <p>
 * 名 字 : RecordFragmentView
 * <p>
 * 简 介 :
 */
public class RecordFragmentView implements MvpView {

    RecordFragment mFragment;
//    private PLRecordSetting recordSetting;
//    private PLAudioEncodeSetting audioEncodeSetting;
//    private PLMicrophoneSetting microphoneSetting;
//    private PLVideoEncodeSetting videoEncodeSetting;
//    private PLCameraSetting cameraSetting;
    String videoCaceDirPath;

    public RecordFragmentView(RecordFragment mFragment) {
        this.mFragment = mFragment;
    }

    public RecordActivity getActivity() {
        return (RecordActivity) mFragment.getActivity();
    }

    public void initView() {

//        if (getActivity().mOptions == null) {
//            //Activity 缓存数据失效，一般情况是App被系统回收
//            return;
//        }
//        // 摄像头采集选项
//        if (mFragment.plShortVideoRecorder == null)
//            mFragment.plShortVideoRecorder = new PLShortVideoRecorder();
//        if (cameraSetting == null)
//            cameraSetting = new PLCameraSetting();
//        //设置摄像头
//        cameraSetting.setCameraId(chooseCameraFacingId());
//        cameraSetting.setCameraPreviewSizeRatio(PLCameraSetting.CAMERA_PREVIEW_SIZE_RATIO.RATIO_16_9);
//        cameraSetting.setCameraPreviewSizeLevel(PLCameraSetting.CAMERA_PREVIEW_SIZE_LEVEL.PREVIEW_SIZE_LEVEL_720P);
//        // 麦克风采集选项
//        if (microphoneSetting == null)
//            microphoneSetting = new PLMicrophoneSetting();
//        // 视频编码选项
//        videoEncodeSetting = new PLVideoEncodeSetting(mFragment.getActivity());
//        videoEncodeSetting.setEncodingSizeLevel(PLVideoEncodeSetting.VIDEO_ENCODING_SIZE_LEVEL.VIDEO_ENCODING_SIZE_LEVEL_720P_3); // 480x480
//        videoEncodeSetting.setEncodingBitrate(4000 * 1024); // 1000kbps
//        videoEncodeSetting.setHWCodecEnabled(true); // true:硬编 false:软编
//        // 音频编码选项
//        audioEncodeSetting = new PLAudioEncodeSetting();
//        audioEncodeSetting.setHWCodecEnabled(true); // true:硬编 false:软编
//        // 美颜选项
//        // 录制选项
//        recordSetting = new PLRecordSetting();
////        最大录制时常
//        if (getActivity().mOptions != null && getActivity().mOptions.getMaxTime() > 0) {
//            recordSetting.setMaxRecordDuration(getActivity().mOptions.getMaxTime());
//        } else {
//            recordSetting.setMaxRecordDuration(PickerConfig.RECODE_MAX_TIME);
//        }
//
//        recordSetting.setRecordSpeedVariable(true);
//        recordSetting.setDisplayMode(PLDisplayMode.FULL);
//
//
//        if (getActivity().mOptions == null || TextUtils.isEmpty(getActivity().mOptions.getCachePath())) {
//            if (AndroidQUtil.isAndroidQ()) {
//                videoCaceDirPath = getActivity().getExternalFilesDir("").getPath() + File.separator + "MediaPickerVideo";
//            } else {
//                videoCaceDirPath = Environment.getExternalStorageDirectory().getPath() + File.separator + "MediaPickerVideo";
//            }
//        } else {
//            if (AndroidQUtil.isAndroidQ()) {
//                videoCaceDirPath = mFragment.getActivity().getExternalFilesDir("").getPath() + File.separator + "MediaPickerVideo";
//            } else {
//                videoCaceDirPath = getActivity().mOptions.getCachePath();
//            }
//        }
//        getActivity().mOptions.setCachePath(videoCaceDirPath);
//        recordSetting.setVideoCacheDir(videoCaceDirPath);
//
//        getActivity().videoPath = getActivity().mOptions.getCachePath() + System.currentTimeMillis() + ".mp4";
//        recordSetting.setVideoFilepath(getActivity().videoPath);
//        mFragment.binding.rcControl.setFlashState(false);
//        mFragment.binding.rcControl.setMaxTime(getActivity().mOptions.getMaxTime());
//
//        //设置闪光灯
//
//        // 设置录制速度 (默认为 1.0)
//        mFragment.plShortVideoRecorder.setRecordStateListener(this);
//
//        mFragment.plShortVideoRecorder.prepare(mFragment.binding.gSf, cameraSetting, microphoneSetting, videoEncodeSetting, audioEncodeSetting, null, recordSetting);
//        //处理手动对焦事件
//        mFragment.binding.gSf.requestRender();
//        mFragment.binding.gSf.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    moveFocus(event.getRawX(), event.getRawY());
//                }
//                return true;
//            }
//        });
//        //如果是裁剪类型只支持拍照
//        if (getActivity().isJumpCamera || getActivity().mOptions.getJumpCameraMode() >= 0) {
//            mFragment.binding.rcControl.setRecordType(getActivity().mOptions.getJumpCameraMode());
//        } else {
//            if (getActivity().mOptions.isNeedCrop()) {
//                mFragment.binding.rcControl.setRecordType(PickerConfig.CAMERA_MODE_PIC);
//            } else {
//                mFragment.binding.rcControl.setRecordType(PickerConfig.CAMERA_MODE_ALL);
//            }
//        }
    }

    /**
     * 选择相机
     *
     * @return
     */
//    private PLCameraSetting.CAMERA_FACING_ID chooseCameraFacingId() {
//        if (PLCameraSetting.hasCameraFacing(PLCameraSetting.CAMERA_FACING_ID.CAMERA_FACING_3RD)) {
//            return PLCameraSetting.CAMERA_FACING_ID.CAMERA_FACING_3RD;
//        } else if (PLCameraSetting.hasCameraFacing(PLCameraSetting.CAMERA_FACING_ID.CAMERA_FACING_BACK)) {
//            return PLCameraSetting.CAMERA_FACING_ID.CAMERA_FACING_BACK;
//        } else {
//            return PLCameraSetting.CAMERA_FACING_ID.CAMERA_FACING_FRONT;
//        }
//    }

    /**
     * 设置闪光灯
     *
     * @param isFlashingState
     */
//    public void processFlash(boolean isFlashingState) {
//        mFragment.binding.rcControl.setFlashState(isFlashingState);
//    }

    /**
     * 异常
     *
     * @param message
     */
    public void showMesage(String message) {

        if (TextUtils.isEmpty(message) || mFragment == null || mFragment.getActivity().isFinishing())
            return;
        mFragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertUtil.showDeftToast(mFragment.getActivity(), message);
            }
        });


    }

    //移动对焦的动画
    public void moveFocus(float x, float y) {

//        mFragment.plShortVideoRecorder.manualFocus(80, 80, (int) x, (int) y);
//        //缩小
//        float transX = x - ScreenUtils.getScreenWidth(getActivity()) / 2;
//
//        float transY = y - ScreenUtils.getScreenHeight(getActivity()) / 2;
//        ObjectAnimator translationX = ObjectAnimator.ofFloat(mFragment.binding.fv, "translationX", 0, transX);
//        ObjectAnimator translationY = ObjectAnimator.ofFloat(mFragment.binding.fv, "translationY", 0, transY);
//        AnimatorSet animSet = new AnimatorSet();
//        animSet.play(translationX).with(translationY);
//        animSet.start();
//        animSet.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                mFragment.binding.fv.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//        });
//        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mFragment.binding.fv, "scaleX", 1, 0.6f);
//        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mFragment.binding.fv, "scaleY", 1, 0.6f);
//
//        AnimatorSet animSet2 = new AnimatorSet();
//        animSet2.play(scaleY).with(scaleX);
//        animSet2.setInterpolator(new OvershootInterpolator());
//        animSet2.setDuration(800);
//        animSet2.start();
//        animSet2.addListener(new Animator.AnimatorListener() {
//
//            @Override
//            public void onAnimationStart(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                mFragment.binding.fv.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//        });
    }


//    @Override
//    public void onReady() {
//        //设置闪光灯
//        mFragment.isReady = true;
//    }
//
//    @Override
//    public void onError(int i) {
//        showMesage("相机初始化异常");
//        mFragment.isReady = false;
//        mFragment.isRecordIng = false;
//    }
//
//    @Override
//    public void onDurationTooShort() {
//    }
//
//    @Override
//    public void onRecordStarted() {
//    }
//
//    @Override
//    public void onSectionRecording(long l, long l1, int i) {
//    }
//
//    @Override
//    public void onRecordStopped() {
//        Log.e("", "");
//    }
//
//    @Override
//    public void onSectionIncreased(long l, long l1, int i) {
//        Log.e("", "");
//    }
//
//    @Override
//    public void onSectionDecreased(long l, long l1, int i) {
//    }
//
//    @Override
//    public void onRecordCompleted() {
//        mFragment.isReady = false;
//        mFragment.isRecordIng = false;
//    }
}
