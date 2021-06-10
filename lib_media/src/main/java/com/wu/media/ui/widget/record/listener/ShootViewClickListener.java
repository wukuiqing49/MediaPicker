package com.wu.media.ui.widget.record.listener;

/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/9/27 13:58
 * <p>
 * 名 字 : ShootViewClickListener
 * <p>
 * 简 介 :
 */
public interface ShootViewClickListener {
    //拍照
    void onPhotograph();
    //录像完成
    void onFinishRecordVideo();
    //录像开始
    void onStartRecordVideo();
    //录像开始
    void onRecordVideoFail(String message);
}
