package com.wu.media.ui.widget.record;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.wkq.base.utils.toast.ToastUtil;
import com.wu.media.R;
import com.wu.media.camera.CameraInterface;
import com.wu.media.camera.state.CameraMachine;
import com.wu.media.databinding.LayoutRecordCameraBinding;
import com.wu.media.databinding.LayoutRecordCustomCameraBinding;
import com.wu.media.ui.widget.record.listener.CameraPreviewErroListener;
import com.wu.media.ui.widget.record.listener.CameraPreviewListener;
import com.wu.media.ui.widget.record.listener.RecordCustomCameraViewListener;
import com.wu.media.ui.widget.record.listener.ShootViewClickListener;
import com.wu.media.ui.widget.record.observable.RecordCameraViewObservable;
import com.wu.media.ui.widget.record.view.CameraPreview;
import com.wu.media.utils.ScreenUtils;


/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/9/22 15:54
 * <p>
 * 名 字 : RecordCameraView
 * <p>
 * 简 介 :  视频录制的控制器
 */
public class RecordCustomCameraView extends FrameLayout {
    Context mContext;
    float screenProp;
    private LayoutRecordCustomCameraBinding binding;
    /**
     * 相机类
     */
    public CameraPreview preview;
    boolean showFlash = false;

    public RecordCustomCameraView(@NonNull Context context) {
        this(context, null);
    }

    public RecordCustomCameraView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    RecordCustomCameraViewListener mListener;

    public void setRecordListener(RecordCustomCameraViewListener listener) {
        mListener = listener;
    }

    public RecordCustomCameraView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initCamera();
        initView();
    }

    /**
     * 初始化相机
     */
    private void initCamera() {
        preview = new CameraPreview(mContext);
        addView(preview);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    /**
     * 是否多点
     */
    private boolean isMultiTouch = false;
    private float oldDist = 1f;

    private static float getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }
    private void initView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_record_custom_camera, this, false);
        addView(binding.getRoot());

        binding.imageFlash.setOnClickListener(v -> {
            showFlash = !showFlash;
            setFlashState(showFlash);
        });
        binding.imageSwitch.setOnClickListener(v -> {
            if (preview != null) preview.updateFontOrBackCamera();
        });
        binding.ivShootBack.setOnClickListener(v -> RecordCameraViewObservable.newInstance().onClick(RecordCameraViewObservable.CLICK_FINISH));
        binding.shootView.setOnShootListener(new ShootViewClickListener() {
            @Override
            public void onPhotograph() {
                if (preview != null) preview.takePhoto(new CameraPreviewListener() {
                    @Override
                    public void onResult(String filePath) {
                        ToastUtil.newBuild().build(mContext).showToast(filePath);
                    }
                });
            }

            @Override
            public void onFinishRecordVideo() {
                if (preview != null) preview.stopRecord(new CameraInterface.StopRecordCallback() {
                    @Override
                    public void recordResult(String url) {
                        ToastUtil.newBuild().build(mContext).showToast(url);
                    }
                });
            }

            @Override
            public void onStartRecordVideo() {
                if (preview != null) preview.startRecord((float) 16 / 9);
            }

            @Override
            public void onRecordVideoFail(String message) {
                RecordCameraViewObservable.newInstance().onClick(RecordCameraViewObservable.CLICK_RECODE_ERR);
                processErrMessage(message);
                if (preview != null) preview.reset();
            }
        });

        binding.parent.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getPointerCount() == 1) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_POINTER_UP:
                            isMultiTouch = true;
                            break;
                        case MotionEvent.ACTION_UP:
                            if (!isMultiTouch) {
                                moveFocus(event.getRawX(), event.getRawY());
                            }
                            isMultiTouch = false;
                            break;
                    }
                }else {
                    isMultiTouch = true;
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_POINTER_DOWN:
                            oldDist = getFingerSpacing(event);
                            break;
                        case MotionEvent.ACTION_MOVE:
                            float newDist = getFingerSpacing(event);
                            if (newDist > oldDist) {
                                preview.handleZoom(true);
                            } else if (newDist < oldDist) {
                                preview.handleZoom(false);
                            }
                            oldDist = newDist;
                            break;
                    }
                }

                return true;
            }
        });

        if (preview != null) preview.setOnErroListener(new CameraPreviewErroListener() {
            @Override
            public void onErro(String message) {
                if (!TextUtils.isEmpty(message))
                    ToastUtil.newBuild().build(mContext).showToast(message);
            }
        });
        if (preview != null) preview.registerSensorManager(mContext);
    }


    private void processErrMessage(String message) {
        binding.tvMessage.setText(message);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(binding.tvMessage, "alpha", 0f, 1f, 0f);
        objectAnimator.setDuration(1500);
        objectAnimator.start();
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                binding.tvMessage.setText("");
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * 设置闪关灯
     *
     * @param isFlashIng
     */
    public void setFlashState(boolean isFlashIng) {
        if (preview != null) preview.openFlash(isFlashIng);
        if (isFlashIng) {
            binding.imageFlash.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_on));
        } else {
            binding.imageFlash.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_off));
        }
    }

    /**
     * 设置最大时常
     *
     * @param maxTime
     */
    public void setMaxTime(int maxTime) {
        if (binding != null && maxTime > 500) {
            binding.shootView.setMaxTime(maxTime);
        }
    }

    /**
     * 设置录制类型
     *
     * @param type 1 拍照  2 视频  0 支持所有
     */
    public void setRecordType(int type) {
        binding.shootView.setRecodeType(type);
    }

    public void resetRecord() {
        binding.shootView.reset();
    }

    //移动对焦的动画
    public void moveFocus(float x, float y) {

        //缩小
        float transX = x - ScreenUtils.getScreenWidth(mContext) / 2;

        float transY = y - ScreenUtils.getScreenHeight(mContext) / 2;
        ObjectAnimator translationX = ObjectAnimator.ofFloat(binding.fv, "translationX", 0, transX);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(binding.fv, "translationY", 0, transY);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(translationX).with(translationY);
        animSet.start();
        animSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                binding.fv.setVisibility(View.VISIBLE);
                if (preview != null)
                    preview.doFocus(x, y);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(binding.fv, "scaleX", 1, 0.6f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(binding.fv, "scaleY", 1, 0.6f);

        AnimatorSet animSet2 = new AnimatorSet();
        animSet2.play(scaleY).with(scaleX);
        animSet2.setInterpolator(new OvershootInterpolator());
        animSet2.setDuration(800);
        animSet2.start();
        animSet2.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                binding.fv.setVisibility(View.GONE);


            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }


}
