package com.wu.media.utils.observable;


import com.wu.media.media.entity.Media;

import java.util.Observable;

/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/9/30 9:52
 * <p>
 * 名 字 : MeidaResultObservable
 * <p>
 * 简 介 : 媒体库结果的观察者
 */
public class MeidaResultObservable extends Observable {

    static MeidaResultObservable instance;

    public static MeidaResultObservable getInstance() {
        synchronized (MeidaResultObservable.class) {
            if (instance == null) {
                instance = new MeidaResultObservable();
            }
        }
        return instance;
    }


    public void finishMedia(boolean isFinish, Media meida) {
        setChanged();
        notifyObservers(meida);
    }
}
