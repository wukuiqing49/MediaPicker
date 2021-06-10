package com.wu.media.camera.state;

import android.content.Context;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.wu.media.camera.CameraInterface;


/**
 * =====================================
 * 作    者: 陈嘉桐
 * 版    本：1.1.4
 * 创建日期：2017/9/8
 * 描    述：
 * =====================================
 */
public interface State {

    boolean start(SurfaceHolder holder, float screenProp);

    void stop();

    boolean foucs(float x, float y, CameraInterface.FocusCallback callback);

    boolean swtich(SurfaceHolder holder, float screenProp);

    void restart();

    boolean capture();

    boolean record(Surface surface, float screenProp, Context context);

    void stopRecord(boolean isShort, long time);

    void cancle(SurfaceHolder holder, float screenProp);

    void confirm();

    void zoom(float zoom, int type);

    void flash(String mode);

    void onDestroy();
}
