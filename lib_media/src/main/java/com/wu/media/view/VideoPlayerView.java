package com.wu.media.view;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.wkq.base.frame.mosby.delegate.MvpView;
import com.wkq.base.utils.AlertUtil;
import com.wkq.base.utils.StatusBarUtil2;
import com.wu.media.ui.activity.VideoPlayerActivity;

/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/10/15 11:13
 * <p>
 * 名 字 : VideoPlayerView
 * <p>
 * 简 介 :
 */
public class VideoPlayerView implements MvpView {
    VideoPlayerActivity mActivity;

    public VideoPlayerView(VideoPlayerActivity mActivity) {
        this.mActivity = mActivity;
    }

    public void initView() {
        processFullScreen();
        StatusBarUtil2.setTransparentForWindow(mActivity);
        StatusBarUtil2.addTranslucentView(mActivity, 0);
        QMUIStatusBarHelper.setStatusBarLightMode(mActivity);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        mActivity.binding.ivPlay.setOnClickListener(v -> {
            setPlayState();
        });
        mActivity.binding.video.setOnClickListener(v -> {
            setPlayState();
        });


        Glide.with(mActivity).load(Uri.parse(mActivity.path)).into(mActivity.binding.ivThumbnail);
        mActivity.binding.video.setUrl(mActivity.path);
        mActivity.binding.video.start();
    }

    /**
     * 处理刘海屏全屏问题
     */
    public void processFullScreen() {

        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            lp.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        mActivity.getWindow().setAttributes(lp);
        // 设置页面全屏显示
        View decorView = mActivity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

    }

    public void showMessage(String message) {
        if (mActivity == null || TextUtils.isEmpty(message)) {
            return;
        }
        AlertUtil.showDeftToast(mActivity, message);
    }

    public void requestAudioFocus() {
        if (mActivity == null)
            return;
        AudioManager audioManager = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            int ret = audioManager.requestAudioFocus(mOnAudioFocusChangeListener,
                    AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            if (ret != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            }
        }
    }

    /**
     * 释放音频焦点
     */
    public void abandonAudioFocus() {
        if (mActivity == null)
            return;
        AudioManager audioManager = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        }
    }

    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS ||
                    focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                //失去焦点之后的操作
                onPause();
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN ||
                    focusChange == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT ||
                    focusChange == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE ||
                    focusChange == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK) {
                //获得焦点之后的操作
            }
        }
    };

    /**
     * 设置播放状态
     */
    public void setPlayState() {
        if (mActivity == null) return;
        if (mActivity.isCanPlay) {
            if (mActivity.isPlayIng) {
                mActivity.binding.video.pause();
                mActivity.binding.rlThum.setVisibility(View.VISIBLE);
                mActivity.binding.ivThumbnail.setVisibility(View.GONE);
                mActivity.isPlayIng = false;
            } else {
                mActivity.binding.video.start();
                mActivity.binding.rlThum.setVisibility(View.GONE);
                mActivity.isPlayIng = true;
            }
        }
    }


    public void onResume() {
        mActivity.isPlayIng = false;
        mActivity.binding.video.pause();
        mActivity.binding.rlThum.setVisibility(View.VISIBLE);
    }

    public void onPause() {
        mActivity.isPlayIng = false;
        mActivity.binding.video.pause();
        mActivity.binding.rlThum.setVisibility(View.VISIBLE);
    }
}
