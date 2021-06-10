package com.wu.media.ui.widget.record;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.wu.media.R;
import com.wu.media.databinding.LayoutRecordCameraBinding;
import com.wu.media.ui.widget.record.listener.ShootViewClickListener;
import com.wu.media.ui.widget.record.observable.RecordCameraViewObservable;


/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/9/22 15:54
 * <p>
 * 名 字 : RecordCameraView
 * <p>
 * 简 介 :  视频录制的控制器
 */
public class RecordCameraView extends FrameLayout {
    Context mContext;
    private LayoutRecordCameraBinding binding;

    public RecordCameraView(@NonNull Context context) {
        this(context, null);
    }

    public RecordCameraView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordCameraView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        setMeasuredDimension(ScreenUtils.getScreenWidth(getContext()), ScreenUtils.getScreenHeight(getContext()));
    }

    private void initView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_record_camera, this, false);
        addView(binding.getRoot());
        binding.imageFlash.setOnClickListener(v -> {
            RecordCameraViewObservable.newInstance().onClick(RecordCameraViewObservable.CLICK_FLASH);
        });
        binding.imageSwitch.setOnClickListener(v -> {
            RecordCameraViewObservable.newInstance().onClick(RecordCameraViewObservable.CLICK_SWITCH);
        });
        binding.ivShootBack.setOnClickListener(v -> RecordCameraViewObservable.newInstance().onClick(RecordCameraViewObservable.CLICK_FINISH));
        binding.shootView.setOnShootListener(new ShootViewClickListener() {
            @Override
            public void onPhotograph() {
//                拍照
                RecordCameraViewObservable.newInstance().onClick(RecordCameraViewObservable.CLICK_TAKE);
            }

            @Override
            public void onFinishRecordVideo() {
                RecordCameraViewObservable.newInstance().onClick(RecordCameraViewObservable.CLICK_RECODE_FINISH);
            }

            @Override
            public void onStartRecordVideo() {
                RecordCameraViewObservable.newInstance().onClick(RecordCameraViewObservable.CLICK_RECODE_START);
            }

            @Override
            public void onRecordVideoFail(String message) {
                RecordCameraViewObservable.newInstance().onClick(RecordCameraViewObservable.CLICK_RECODE_ERR);

                processErrMessage(message);
            }
        });
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

    public void setFlashState(boolean isFlashIng) {
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
}
