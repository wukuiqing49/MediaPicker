package com.wu.media.camera.state;

import android.content.Context;
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
 * 描    述：
 * =====================================
 */
public class BorrowVideoState implements State {
    private final String TAG = "BorrowVideoState";
    private CameraMachine machine;

    public BorrowVideoState(CameraMachine machine) {
        this.machine = machine;
    }

    @Override
    public boolean start(SurfaceHolder holder, float screenProp) {
        if (CameraInterface.getInstance().doStartPreview(holder, screenProp)) {
            machine.setState(machine.getPreviewState());
            return true;
        }
        return false;
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean foucs(float x, float y, CameraInterface.FocusCallback callback) {
        return false;
    }


    @Override
    public boolean swtich(SurfaceHolder holder, float screenProp) {
        return false;
    }

    @Override
    public void restart() {

    }

    @Override
    public boolean capture() {
        return false;
    }

    @Override
    public boolean record(Surface surface, float screenProp, Context context) {
        return false;
    }

    @Override
    public void stopRecord(boolean isShort, long time) {

    }

    @Override
    public void cancle(SurfaceHolder holder, float screenProp) {
        machine.getView().resetState(JCameraView.TYPE_VIDEO);
        machine.setState(machine.getPreviewState());
    }

    @Override
    public void confirm() {
        machine.getView().confirmState(JCameraView.TYPE_VIDEO);
        machine.setState(machine.getPreviewState());
    }

    @Override
    public void zoom(float zoom, int type) {
        LogUtil.i(TAG, "zoom");
    }

    @Override
    public void flash(String mode) {
        return ;
    }

    @Override
    public void onDestroy() {
        CameraInterface.getInstance().onDestroy();
    }
}
