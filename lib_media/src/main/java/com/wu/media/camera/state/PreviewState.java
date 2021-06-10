package com.wu.media.camera.state;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.wu.media.camera.CameraInterface;
import com.wu.media.camera.JCameraView;
import com.wu.media.camera.util.LogUtil;


/**
 * =====================================
 * 作    者: 陈嘉桐
 * 版    本：1.1.4
 * 创建日期：2017/9/8
 * 描    述：空闲状态
 * =====================================
 */
class PreviewState implements State {
    public static final String TAG = "PreviewState";

    private CameraMachine machine;

    PreviewState(CameraMachine machine) {
        this.machine = machine;
    }

    @Override
    public boolean start(SurfaceHolder holder, float screenProp) {
        return CameraInterface.getInstance().doStartPreview(holder, screenProp);
    }

    @Override
    public void stop() {
        CameraInterface.getInstance().doStopPreview();
    }


    @Override
    public boolean foucs(float x, float y, CameraInterface.FocusCallback callback) {
        LogUtil.i("preview state foucs");
        try {
            if (machine.getView().handlerFoucs(x, y)) {
                return CameraInterface.getInstance().handleFocus(machine.getContext(), x, y, callback);
            }
        } catch (Exception e) {
            Log.e("Camera", "", e);
        }

        return false;
    }

    @Override
    public boolean swtich(SurfaceHolder holder, float screenProp) {
        return CameraInterface.getInstance().switchCamera(holder, screenProp);
    }

    @Override
    public void restart() {

    }

    @Override
    public boolean capture() {
        return CameraInterface.getInstance().takePicture(new CameraInterface.TakePictureCallback() {
            @Override
            public void captureResult(Bitmap bitmap, boolean isVertical) {
                machine.getView().showPicture(bitmap, isVertical);
                machine.setState(machine.getBorrowPictureState());
                LogUtil.i("capture");
            }
        });
    }

    @Override
    public boolean record(Surface surface, float screenProp, Context context) {
        return CameraInterface.getInstance().startRecord(surface, screenProp, null,context);
    }

    @Override
    public void stopRecord(final boolean isShort, long time) {
        CameraInterface.getInstance().stopRecord(isShort, new CameraInterface.StopRecordCallback() {
            @Override
            public void recordResult(String url) {
                if (isShort) {
                    machine.getView().resetState(JCameraView.TYPE_SHORT);
                } else {
                    machine.getView().playVideo(url);
                    machine.setState(machine.getBorrowVideoState());
                }
            }
        });
    }

    @Override
    public void cancle(SurfaceHolder holder, float screenProp) {
        LogUtil.i("浏览状态下,没有 cancle 事件");
    }

    @Override
    public void confirm() {
        LogUtil.i("浏览状态下,没有 confirm 事件");
    }

    @Override
    public void zoom(float zoom, int type) {
        LogUtil.i(TAG, "zoom");
        CameraInterface.getInstance().setZoom(zoom, type);
    }

    @Override
    public void flash(String mode) {
         CameraInterface.getInstance().setFlashMode(mode);
    }

    @Override
    public void onDestroy() {
        CameraInterface.getInstance().onDestroy();
    }
}
