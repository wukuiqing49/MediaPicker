package com.wu.media.utils.album;

/**
 * 作者: 吴奎庆
 * <p>
 * 时间: 2020/6/15
 * <p>
 * 简介:
 */
public interface OnSaveCallback {
    void onSuccess(String path);
    void onFail(String message);

}
