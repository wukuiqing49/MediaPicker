package com.wu.media.ui.widget.record.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import com.wu.media.ui.widget.record.FoucsView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;

import com.wkq.base.utils.AlertUtil;
import com.wkq.base.utils.toast.ToastUtil;
import com.wu.media.camera.CameraInterface;
import com.wu.media.camera.util.AngleUtil;
import com.wu.media.camera.util.CameraParamUtil;
import com.wu.media.camera.util.DeviceUtil;
import com.wu.media.ui.widget.record.listener.CameraPreviewErroListener;
import com.wu.media.ui.widget.record.listener.CameraPreviewListener;
import com.wu.media.utils.AndroidQUtil;
import com.wu.media.utils.ScreenUtils;
import com.wu.media.utils.album.AlbumProcessUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

/**
 * @author 吴奎庆
 * @date 2019/2/28 0028 17:06
 * 注释:相机预览视图
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    public Camera mCamera;
    private boolean isPreview;
    private Context context;
    private int SELECTED_CAMERA = -1;
    private int CAMERA_POST_POSITION = -1;
    private int CAMERA_FRONT_POSITION = -1;
    private int id = Camera.CameraInfo.CAMERA_FACING_BACK;
    //前后置摄像头
    private int backId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private int fontId = Camera.CameraInfo.CAMERA_FACING_FRONT;
    /**
     * 预览尺寸集合
     */
    private final SizeMap mPreviewSizes = new SizeMap();
    /**
     * 图片尺寸集合
     */
    private final SizeMap mPictureSizes = new SizeMap();
    /**
     * 屏幕旋转显示角度
     */
    private int mDisplayOrientation;
    /**
     * 设备屏宽比
     */
    private AspectRatio mAspectRatio;
    //视频录制
    private MediaRecorder mMediaRecorder;
    private String videoPath;
    CameraPreviewErroListener mOnErroListener;

    public void setOnErroListener(CameraPreviewErroListener listener) {
        mOnErroListener = listener;
    }

    public CameraPreview(Context context) {
        super(context);
        this.context = context;
        this.mHolder = getHolder();
        this.mHolder.addCallback(this);
        id = backId;
        openCamera(id);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mDisplayOrientation = ((Activity) context).getWindowManager().getDefaultDisplay().getRotation();
        mAspectRatio = AspectRatio.of(9, 16);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        previewCamera(holder);
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (holder.getSurface() == null) {
            return;
        }
        resume();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            if (isPreview) {
                //正在预览
                doDestroyCamera();
            }
        }
    }


    public void resume() {
        try {
            if (mCamera == null) {
                openCamera(id);
                previewCamera(getHolder());
            }
        } catch (Exception e) {
            if (mOnErroListener != null) mOnErroListener.onErro("相机异常");
        }

    }
    private float oldDist = 1f;
    private static float getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }


    /**
     * 初始化
     *
     * @param holder
     * @throws IOException
     */
    public void previewCamera(SurfaceHolder holder) {
        findAvailableCameras();
        //设置设备高宽比
        mAspectRatio = getDeviceAspectRatio((Activity) context);
        //设置预览方向
        mCamera.setDisplayOrientation(getDisplayOrientation());
        Camera.Parameters parameters = mCamera.getParameters();
        //获取所有支持的预览尺寸
        mPreviewSizes.clear();
        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            int width = Math.min(size.width, size.height);
            int heigth = Math.max(size.width, size.height);
            mPreviewSizes.add(new Size(width, heigth));
        }
        //获取所有支持的图片尺寸
        mPictureSizes.clear();
        for (Camera.Size size : parameters.getSupportedPictureSizes()) {
            int width = Math.min(size.width, size.height);
            int heigth = Math.max(size.width, size.height);
            mPictureSizes.add(new Size(width, heigth));
        }
        Size previewSize = chooseOptimalSize(mPreviewSizes.sizes(mAspectRatio));
        Size pictureSize = mPictureSizes.sizes(mAspectRatio).last();
        //设置相机参数
        parameters.setPreviewSize(Math.max(previewSize.getWidth(), previewSize.getHeight()), Math.min(previewSize.getWidth(), previewSize.getHeight()));
        parameters.setPictureSize(Math.max(pictureSize.getWidth(), pictureSize.getHeight()), Math.min(pictureSize.getWidth(), pictureSize.getHeight()));
        parameters.setPictureFormat(ImageFormat.JPEG);
        parameters.setRotation(getDisplayOrientation());
        mCamera.setParameters(parameters);
        //把这个预览效果展示在SurfaceView上面
        try {
            mCamera.setPreviewDisplay(holder);
        } catch (Exception e) {
            if (mOnErroListener != null) mOnErroListener.onErro("相机初始化异常");
        }
        //开启预览效果
        mCamera.startPreview();
        isPreview = true;
        doFocus(ScreenUtils.getScreenWidth(context) / 2, ScreenUtils.getScreenHeight(context) / 2);

    }

    /**
     * 注释：获取设备屏宽比
     */
    private AspectRatio getDeviceAspectRatio(Activity activity) {
        int width = activity.getWindow().getDecorView().getWidth();
        int height = activity.getWindow().getDecorView().getHeight();
        return AspectRatio.of(Math.min(width, height), Math.max(width, height));
    }

    /**
     * 注释：选择合适的预览尺寸
     *
     * @param sizes
     * @return
     */
    @SuppressWarnings("SuspiciousNameCombination")
    private Size chooseOptimalSize(SortedSet<Size> sizes) {
        int desiredWidth;
        int desiredHeight;
        final int surfaceWidth = getWidth();
        final int surfaceHeight = getHeight();
        if (isLandscape(mDisplayOrientation)) {
            desiredWidth = surfaceHeight;
            desiredHeight = surfaceWidth;
        } else {
            desiredWidth = surfaceWidth;
            desiredHeight = surfaceHeight;
        }
        Size result = new Size(desiredWidth, desiredHeight);
        if (sizes != null && !sizes.isEmpty()) {
            for (Size size : sizes) {
                if (desiredWidth <= size.getWidth() && desiredHeight <= size.getHeight()) {
                    return size;
                }
                result = size;
            }
        }
        return result;
    }


    private boolean isLandscape(int orientationDegrees) {
        return (orientationDegrees == Surface.ROTATION_90 ||
                orientationDegrees == Surface.ROTATION_270);
    }

    /**
     * 获取角度
     *
     * @return
     */
    public int getDisplayOrientation() {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        try {
            android.hardware.Camera.getCameraInfo(id, info);
        } catch (Exception e) {
            AlertUtil.showFailedToast(context, "像机ID识别错误" + id);
        }
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int rotation = wm.getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;   // compensate the mirror
        } else {
            // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    /**
     * 切换前后摄像头
     */
    public void updateFontOrBackCamera() {
        if (id == backId) {
            id = fontId;
        } else {
            id = backId;
        }
        if (SELECTED_CAMERA == CAMERA_POST_POSITION) {
            SELECTED_CAMERA = CAMERA_FRONT_POSITION;
        } else {
            SELECTED_CAMERA = CAMERA_POST_POSITION;
        }
        reset();
    }

    /**
     * 打开相机
     *
     * @param id 相机Id    0 后  1 前
     * @return
     */
    private synchronized boolean openCamera(int id) {
        try {
            mCamera = Camera.open(id);
        } catch (Exception e) {
            if (mOnErroListener != null) mOnErroListener.onErro("相机开启异常");
        }

        try {
            if (Build.VERSION.SDK_INT > 17 && this.mCamera != null) {
                this.mCamera.enableShutterSound(false);
            }
        } catch (Exception e) {
            if (mOnErroListener != null) mOnErroListener.onErro("相机开启异常");
        }

        return mCamera != null;
    }

    public void reset() {
        doDestroyCamera();
        openCamera(id);
        findAvailableCameras();
        previewCamera(getHolder());
    }

    /**
     * 销毁Camera
     */
    private void doDestroyCamera() {
        if (null != mCamera) {
            try {
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                //这句要在stopPreview后执行，不然会卡顿或者花屏
                mCamera.setPreviewDisplay(null);
                mHolder = null;
                mCamera.lock();
                mCamera.release();
                mCamera = null;
            } catch (Exception e) {

            }
        }
    }

    /**
     * 闪光灯
     *
     * @param isFlashing
     */
    public void openFlash(boolean isFlashing) {
        if (mCamera == null) return;
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setFlashMode(isFlashing ? Camera.Parameters.FLASH_MODE_TORCH : Camera.Parameters.FLASH_MODE_OFF);
        mCamera.setParameters(parameters);
    }

    /**
     * 自动对焦
     *
     * @param x 点击点X
     * @param y 点击点Y
     */
    public void doFocus(float x, float y) {
        try {
            //对焦光感区域
            if (mCamera == null) return;
            Rect touchFocusRect = new Rect((int) (x - 100), (int) (y - 100), (int) (x + 100), (int) (y + 100));

            int left = touchFocusRect.left * 2000 / ScreenUtils.getScreenWidth(context) - 1000;
            int top = touchFocusRect.top * 2000 / ScreenUtils.getScreenHeight(context) - 1000;
            int right = touchFocusRect.right * 2000 / ScreenUtils.getScreenWidth(context) - 1000;
            int bottom = touchFocusRect.bottom * 2000 / ScreenUtils.getScreenHeight(context) - 1000;
            // 如果超出了(-1000,1000)到(1000, 1000)的范围，则会导致相机崩溃
            left = left < -1000 ? -1000 : left;
            top = top < -1000 ? -1000 : top;
            right = right > 1000 ? 1000 : right;
            bottom = bottom > 1000 ? 1000 : bottom;
            Rect targetFocusRect = new Rect(left, top, right, bottom);
            List<Camera.Area> focusList = new ArrayList<>();
            Camera.Area focusArea = new Camera.Area(targetFocusRect, 1000);
            focusList.add(focusArea);
            Camera.Parameters para = mCamera.getParameters();
            para.setFocusAreas(focusList);
            para.setMeteringAreas(focusList);
            para.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            mCamera.cancelAutoFocus();
            mCamera.setParameters(para);
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {

                }
            });
        } catch (Exception e) {
            Log.e("设置相机参数异常", e.getMessage());
        }
    }

    /**
     * 拍照
     */
    public void takePhoto(CameraPreviewListener listener) {
        if (mCamera == null) return;
        mCamera.takePicture(null, null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                String filePath = AlbumProcessUtil.savePhoto(context, data);
                if (TextUtils.isEmpty(filePath)) return;
                File file = new File(filePath);
                if (AndroidQUtil.isAndroidQ()) {
                    AlbumProcessUtil.insertMediaImg(context, file);
                } else {
                    AlbumProcessUtil.insertMediaPic(context, file);
                    context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
                }
                listener.onResult(filePath);
                openCamera(id);
                previewCamera(getHolder());
            }
        });

    }


    private int angle = 0;
    Camera.Parameters mParams;
    private int cameraAngle = 90;//摄像头角度   默认为90度
    boolean isRecorder;

    public void registerSensorManager(Context context) {
        SensorManager sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sm == null) return;
        Sensor sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregisterSensorManager(Context context) {
        SensorManager sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sm == null) return;
        sm.unregisterListener(sensorEventListener);
    }

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            if (Sensor.TYPE_ACCELEROMETER != event.sensor.getType()) {
                return;
            }
            float[] values = event.values;
            angle = AngleUtil.getSensorAngle(values[0], values[1]);
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    /**
     * 视频录制
     *
     * @param screenProp
     */

    public void startRecord(float screenProp) {

        //启动录像
        if (mCamera == null) return;
        mCamera.setPreviewCallback(null);
        int nowAngle = (angle + 90) % 360;
        //获取第一帧图片

        if (mCamera == null) {
            openCamera(id);
        }
        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
        }
        if (mParams == null) {
            mParams = mCamera.getParameters();
        }
        List<String> focusModes = mParams.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
        try {
            mCamera.setParameters(mParams);
            mCamera.unlock();
            mMediaRecorder.reset();
            mMediaRecorder.setCamera(mCamera);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            Camera.Size videoSize;
            if (mParams.getSupportedVideoSizes() == null) {
                videoSize = CameraParamUtil.getInstance().getPreviewSize(mParams.getSupportedPreviewSizes(), 600,
                        screenProp);
            } else {
                videoSize = CameraParamUtil.getInstance().getPreviewSize(mParams.getSupportedVideoSizes(), 600,
                        screenProp);
            }
            mMediaRecorder.setVideoSize(videoSize.width, videoSize.height);

            if (SELECTED_CAMERA == CAMERA_FRONT_POSITION) {
                //手机预览倒立的处理
                if (cameraAngle == 270) {
                    //横屏
                    if (nowAngle == 0) {
                        mMediaRecorder.setOrientationHint(180);
                    } else if (nowAngle == 270) {
                        mMediaRecorder.setOrientationHint(270);
                    } else {
                        mMediaRecorder.setOrientationHint(90);
                    }
                } else {
                    if (nowAngle == 90) {
                        mMediaRecorder.setOrientationHint(270);
                    } else if (nowAngle == 270) {
                        mMediaRecorder.setOrientationHint(90);
                    } else {
                        mMediaRecorder.setOrientationHint(nowAngle);
                    }
                }
            } else {
                mMediaRecorder.setOrientationHint(nowAngle);
            }


            if (DeviceUtil.isHuaWeiRongyao()) {
                mMediaRecorder.setVideoEncodingBitRate(4 * 100000);
            } else {
                mMediaRecorder.setVideoEncodingBitRate(20 * 100000);
            }
            mMediaRecorder.setPreviewDisplay(getHolder().getSurface());
            videoPath = AlbumProcessUtil.getPath(context) + System.currentTimeMillis() + ".mp4";
            mMediaRecorder.setOutputFile(videoPath);

            mMediaRecorder.prepare();
            mMediaRecorder.start();
            isRecorder = true;
        } catch (Exception e) {
            if (mOnErroListener != null) mOnErroListener.onErro("视频录制异常");
        }
    }


    /**
     * 处理 视频录制方向问题
     */
    private void findAvailableCameras() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        int cameraNum = Camera.getNumberOfCameras();
        for (int i = 0; i < cameraNum; i++) {
            try {
                Camera.getCameraInfo(i, info);
            } catch (Exception e) {
                if (mOnErroListener != null) mOnErroListener.onErro("获取相机信息异常");
                continue;
            }
            switch (info.facing) {
                case Camera.CameraInfo.CAMERA_FACING_FRONT:
                    CAMERA_FRONT_POSITION = info.facing;
                    break;
                case Camera.CameraInfo.CAMERA_FACING_BACK:
                    CAMERA_POST_POSITION = info.facing;
                    break;
            }
        }
        SELECTED_CAMERA = CAMERA_POST_POSITION;
    }

    //停止录像
    public void stopRecord(CameraInterface.StopRecordCallback callback) {
        if (!isRecorder) {
            return;
        }
        if (mMediaRecorder != null) {
            mMediaRecorder.setOnErrorListener(null);
            mMediaRecorder.setOnInfoListener(null);
            mMediaRecorder.setPreviewDisplay(null);
            try {
                mMediaRecorder.stop();
            } catch (RuntimeException e) {
                e.printStackTrace();
                mMediaRecorder = null;
                mMediaRecorder = new MediaRecorder();
            } finally {
                if (mMediaRecorder != null) {
                    mMediaRecorder.release();
                }
                mMediaRecorder = null;
                isRecorder = false;
            }
            doDestroyCamera();
            callback.recordResult(videoPath);
            if (mCamera == null) {
                openCamera(id);
                findAvailableCameras();
            }
            try {
                previewCamera(getHolder());
            } catch (Exception e) {
                if (mOnErroListener != null) mOnErroListener.onErro("相机异常");
            }
        }
    }
    public void handleZoom(boolean isZoomIn) {
        Camera.Parameters params = mCamera.getParameters();
        if (params.isZoomSupported()) {
            int maxZoom = params.getMaxZoom();
            int zoom = params.getZoom();
            Log.e("放大缩小:",zoom+":最大:"+maxZoom);
            if (isZoomIn && zoom < maxZoom) {
                zoom++;
            } else if (zoom > 0) {
                zoom--;
            }
            params.setZoom(zoom);
            mCamera.setParameters(params);
        } else {
        }
    }

    public void pause() {
        if (mCamera!=null){
            Camera.Parameters params = mCamera.getParameters();
        }
    }
}
