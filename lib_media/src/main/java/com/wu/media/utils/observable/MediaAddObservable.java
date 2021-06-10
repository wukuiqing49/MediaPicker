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
 * 简 介 : 媒体增加 的观察者
 */
public class MediaAddObservable extends Observable {
    static MediaAddObservable instance;

    public static MediaAddObservable getInstance() {
        synchronized (MediaAddObservable.class) {
            if (instance == null) {
                instance = new MediaAddObservable();
            }
        }
        return instance;
    }

    public void addMedia(Media media) {
        setChanged();
        notifyObservers(media);
    }
}
