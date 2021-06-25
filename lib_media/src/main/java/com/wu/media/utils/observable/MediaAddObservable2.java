package com.wu.media.utils.observable;


import com.wu.media.media.entity.Media;

import java.util.List;
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
public class MediaAddObservable2 extends Observable {
    static MediaAddObservable2 instance;

    public static MediaAddObservable2 getInstance() {
        synchronized (MediaAddObservable2.class) {
            if (instance == null) {
                instance = new MediaAddObservable2();
            }
        }
        return instance;
    }

    public void addMedia(MediaAddInfo media) {
        setChanged();
        notifyObservers(media);
    }

    public static class MediaAddInfo {
        List<Media> datas;

        public List<Media> getDatas() {
            return datas;
        }

        public void setDatas(List<Media> datas) {
            this.datas = datas;
        }
    }
}
