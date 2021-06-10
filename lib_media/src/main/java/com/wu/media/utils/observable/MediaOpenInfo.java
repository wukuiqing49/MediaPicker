package com.wu.media.utils.observable;


import com.wu.media.media.entity.Media;

/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/10/14 13:08
 * <p>
 * 名 字 : MediaOpenInfo
 * <p>
 * 简 介 :
 */
public class MediaOpenInfo {
    // 1: 录制页面 2: 裁剪页面
    int type;
    Media media;

    public MediaOpenInfo(int type, Media media) {
        this.type = type;
        this.media = media;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }
}
