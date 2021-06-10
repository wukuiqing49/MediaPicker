package com.wu.media.ui.adapter;


import com.wu.media.media.entity.Media;

/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/9/16 13:08
 * <p>
 * 名 字 : OnItemClickListener
 * <p>
 * 简 介 : 条目点击事件
 */
public interface OnItemClickListener {
    /**
     * 点击事件
     *
     * @param media
     */
    void itemClick(Media media);

    /**
     * 选择事件
     *
     * @param isSelect
     * @param media
     */
    void itemSelect(boolean isSelect, Media media,int position);

    /**
     * 相机条目点击
     * @param media
     */
    void itemCameraClick( Media media);
}
