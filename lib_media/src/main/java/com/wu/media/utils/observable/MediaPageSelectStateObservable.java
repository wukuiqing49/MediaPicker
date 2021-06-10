package com.wu.media.utils.observable;


import com.wu.media.media.entity.Media;

import java.util.Observable;

/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/9/29 10:56
 * <p>
 * 名 字 : MediaAddObservable
 * <p>
 * 简 介 : 预览滑动的观察者
 */
public class MediaPageSelectStateObservable extends Observable {
    static MediaPageSelectStateObservable instance;

    public static MediaPageSelectStateObservable getInstance() {
        synchronized (MediaPageSelectStateObservable.class) {
            if (instance == null) {
                instance = new MediaPageSelectStateObservable();
            }
        }
        return instance;
    }

    public void pageSelectState(Media media) {
        setChanged();
        notifyObservers(media);
    }
}
