package com.wu.media.ui.widget.record.observable;

import java.util.Observable;

/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/9/23 16:39
 * <p>
 * 名 字 : RecordCameraViewObservable
 * <p>
 * 简 介 :
 */
public class RecordCameraViewObservable extends Observable {
    //闪光灯
    public static int CLICK_FLASH = 1;
    //横竖屏
    public static int CLICK_SWITCH = 2;
    //返回
    public static int CLICK_FINISH = 3;
    //拍照
    public static int CLICK_TAKE = 4;
    //录制视频开始
    public static int CLICK_RECODE_START = 5;
    //录制视频结束
    public static int CLICK_RECODE_FINISH = 6;
    //录制异常
    public static int CLICK_RECODE_ERR= 7;

    static RecordCameraViewObservable instance;

    public static RecordCameraViewObservable newInstance() {
        synchronized (RecordCameraViewObservable.class) {
            if (instance == null) instance = new RecordCameraViewObservable();
        }
        return instance;
    }

    public void onClick(int click) {
        setChanged();
        notifyObservers(click);
    }

}
