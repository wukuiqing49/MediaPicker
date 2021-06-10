package com.wu.media.utils;

import android.content.Context;


/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/10/29 9:46
 * <p>
 * 名 字 : QiniuVideoUtil
 * <p>
 * 简 介 : 七牛 初始化
 */
public class QiniuVideoUtil {
    /**
     * 初始化七牛sdk
     * @param context
     */
    public static void initVideo(Context context) {
//        PLShortVideoEnv.init(context.getApplicationContext());
    }

    /**
     * 检查授权状态
     * @param context
     * @param listener
     */
    public static void chcekInitState(Context context, QiniuVideoInitListener listener) {
//        PLShortVideoEnv.checkAuthentication(context, i -> {
//            if (i==1){
//                listener.isInitState(true);
//            }else {
//                listener.isInitState(false);
//            }
//
//        });
    }

   public interface  QiniuVideoInitListener{
        void isInitState(boolean isInit);
    }
}
