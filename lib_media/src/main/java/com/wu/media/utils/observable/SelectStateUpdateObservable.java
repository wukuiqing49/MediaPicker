package com.wu.media.utils.observable;


import com.wu.media.media.entity.Media;

import java.util.Observable;

/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/9/18 13:43
 * <p>
 * 名 字 : SelectStateUpdateObservable
 * <p>
 * 简 介 :
 */
public class SelectStateUpdateObservable extends Observable {
    static SelectStateUpdateObservable instance;

    public static SelectStateUpdateObservable getInstance() {
        synchronized (SelectStateUpdateObservable.class) {
            if (instance == null) {
                instance = new SelectStateUpdateObservable();
            }
        }
        return instance;
    }

    public void stateUpdate(Media media) {
        setChanged();
        notifyObservers(media);
    }
}
