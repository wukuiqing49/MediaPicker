package com.wu.media.ui.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.wkq.base.frame.fragment.MvpBindingFragment;
import com.wu.media.R;
import com.wu.media.databinding.FragmentRecordBinding;
import com.wu.media.presenter.RecordFragmentPresenter;
import com.wu.media.ui.widget.record.observable.RecordCameraViewObservable;
import com.wu.media.view.RecordFragmentView;

import java.util.Observable;
import java.util.Observer;

/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/10/29 10:04
 * <p>
 * 名 字 : RecordFragment
 * <p>
 * 简 介 :  录制操作页面
 */
public class RecordFragment extends MvpBindingFragment<RecordFragmentView, RecordFragmentPresenter,  FragmentRecordBinding> implements Observer {


    public static RecordFragment newInstance() {

        RecordFragment fragment = new RecordFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;

    }

    // 处理 返回事件的监听
    public OnBackPressedCallback callback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            callback.setEnabled(false);
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        }
    };

//    public PLShortVideoRecorder plShortVideoRecorder;
    public boolean isFlash;
    //准备就绪
    public boolean isReady;
    //录制中
    public boolean isRecordIng;

    public boolean permission = false;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_record;
    }

    private boolean checkPermission() {
        if (getActivity() == null) return false;

        boolean hasPermisson = true;

        for (String permisson : new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}) {
            if (ActivityCompat.checkSelfPermission(getActivity(), permisson) != PackageManager.PERMISSION_GRANTED) {
                hasPermisson = false;
                break;
            }
        }
        return hasPermisson;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permission = checkPermission();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!permission) return;
        RecordCameraViewObservable.newInstance().addObserver(this);
        if (getMvpView() != null) getMvpView().initView();
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!permission) return;

//        if (isFlash && getMvpView() != null) getMvpView().processFlash(isFlash);
//        if (plShortVideoRecorder != null) {
//            plShortVideoRecorder.resume();
//            plShortVideoRecorder.deleteLastSection();
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!permission) return;

        isRecordIng = false;
//        if (plShortVideoRecorder != null) {
//            binding.rcControl.resetRecord();
//            plShortVideoRecorder.pause();
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!permission) return;

        RecordCameraViewObservable.newInstance().deleteObserver(this);
//        if (plShortVideoRecorder != null) {
//            plShortVideoRecorder.destroy();
//        }
    }


    @Override
    public void update(Observable o, Object arg) {
//        if (o instanceof RecordCameraViewObservable) {
//            int state = (int) arg;
////            if (plShortVideoRecorder == null || getMvpView() == null) return;
//            //闪光灯
//            if (state == RecordCameraViewObservable.CLICK_FLASH) {
//                if (isRecordIng) {
//                    getMvpView().showMesage("录制中...");
//                } else {
////                    if (plShortVideoRecorder.isFlashSupport()) {
////                        isFlash = (!isFlash);
////                        plShortVideoRecorder.setFlashEnabled(isFlash);
////                        getMvpView().processFlash(isFlash);
////                    } else {
////                        isFlash = false;
////                        getMvpView().processFlash(false);
////                        getMvpView().showMesage("暂不支持闪光灯");
////                    }
//                }
//
//            } else if (state == RecordCameraViewObservable.CLICK_SWITCH) {
//                if (isRecordIng) {
//                    //设置前后摄像头
//                    getMvpView().showMesage("录制中...");
//
//                } else {
////                    if (DoublePressed.onDoublePressed()) return;
////                    plShortVideoRecorder.switchCamera();
//                }
//            } else if (state == RecordCameraViewObservable.CLICK_FINISH) {
//                requireActivity().getOnBackPressedDispatcher().onBackPressed();
//            } else if (state == RecordCameraViewObservable.CLICK_TAKE) {
//                //拍照
//                if (isReady) {
////                    plShortVideoRecorder.captureFrame(new PLCaptureFrameListener() {
////                        @Override
////                        public void onFrameCaptured(PLVideoFrame plVideoFrame) {
////                            Bitmap plbitMap = plVideoFrame.toBitmap();
////
////                            if (plbitMap != null && plbitMap.getByteCount() > 0) {
////                                getMvpView().getActivity().showPreViewImg(plbitMap);
////                            } else {
////                                getMvpView().showMesage("图片拍摄异常");
////                            }
////                        }
////                    });
//                }
//
//
//            } else if (state == RecordCameraViewObservable.CLICK_RECODE_START) {
//                //视频录制开始
//                if (isReady) {
//                    isRecordIng = true;
////                    plShortVideoRecorder.deleteLastSection();
////                    plShortVideoRecorder.beginSection();
//                } else {
//                    isRecordIng = false;
//                    getMvpView().showMesage("相机还未准备完成,请稍后");
//                }
//            } else if (state == RecordCameraViewObservable.CLICK_RECODE_FINISH) {
//                //录制完成
//                plShortVideoRecorder.endSection();
//                plShortVideoRecorder.concatSections(new PLVideoSaveListener() {
//                    //视频保存完成
//                    @Override
//                    public void onSaveVideoSuccess(String s) {
//                        if (!TextUtils.isEmpty(s)) {
//                            isRecordIng = false;
//                            getMvpView().getActivity().showVideo(s);
//                        } else {
//                            getMvpView().showMesage("视频录制异常");
//                        }
//                    }
//
//                    @Override
//                    public void onSaveVideoFailed(int i) {
//                        isRecordIng = false;
//                        if (getMvpView() != null) getMvpView().showMesage("视频录制失败");
//                    }
//
//                    @Override
//                    public void onSaveVideoCanceled() {
//
//                    }
//
//                    @Override
//                    public void onProgressUpdate(float v) {
//
//                    }
//                });
//
//            } else if (state == RecordCameraViewObservable.CLICK_RECODE_ERR) {
//                isRecordIng = false;
//
//            }
//        }
    }

}
