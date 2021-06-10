package com.wu.media.utils.observable;

import java.util.Observable;

/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/10/14 13:01
 * <p>
 * 名 字 : MediaOpenActivityObservable
 * <p>
 * 简 介 :  打开  裁剪和录制页面
 */
public class MediaOpenActivityObservable extends Observable {

    static MediaOpenActivityObservable instance;

    public static MediaOpenActivityObservable getInstance() {
        synchronized (MediaOpenActivityObservable.class) {
            if (instance == null) {
                instance = new MediaOpenActivityObservable();
            }
        }
        return instance;
    }

    public void openActivity(MediaOpenInfo info) {
        setChanged();
        notifyObservers(info);
    }
}
