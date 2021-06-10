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
 * 简 介 : 选中状态的观察者
 */
public class MediaSelectStateObservable extends Observable {
    static MediaSelectStateObservable instance;

    public static MediaSelectStateObservable getInstance() {
        synchronized (MediaSelectStateObservable.class) {
            if (instance == null) {
                instance = new MediaSelectStateObservable();
            }
        }
        return instance;
    }

    public void selectStateUpdateMedia(Media media) {
        setChanged();
        notifyObservers(media);
    }
}
