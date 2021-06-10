package com.wu.media.utils;


import com.wu.media.media.entity.Media;

public class MediaUtils {
    private final static String MIME_TYPE_PNG = "image/png";
    public final static String MIME_TYPE_JPEG = "image/jpeg";
    private final static String MIME_TYPE_JPG = "image/jpg";
    private final static String MIME_TYPE_BMP = "image/bmp";
    private final static String MIME_TYPE_GIF = "image/gif";
    private final static String MIME_TYPE_WEBP = "image/webp";

    private final static String MIME_TYPE_3GP = "video/3gp";
    private final static String MIME_TYPE_MP4 = "video/mp4";
    private final static String MIME_TYPE_MPEG = "video/mpeg";
    private final static String MIME_TYPE_AVI = "video/avi";

    /**
     * isGif
     *
     * @param mimeType
     * @return
     */
    public static boolean isGif(String mimeType) {
        return mimeType != null && (mimeType.equals("image/gif") || mimeType.equals("image/GIF"));
    }

    /**
     * 是否是长图
     *
     * @param media
     * @return true 是 or false 不是
     */
    public static boolean isLongImg(Media media) {
        if (null != media) {
            int width = media.getImgWidth();
            int height = media.getImgHeight();
            float newHeight = (float) (width * 3.5);
            float newWidth = (float) (height * 3.5);
            return ((height > newHeight) || (width > newWidth) || (width > 15 * 1000) || (height > 15 * 1000));
        }
        return false;
    }

    /**
     * 是否是长图
     *
     * @return true 是 or false 不是
     */
    public static boolean isLongImg(int width,int height) {
        if (width >0&&height>0) {
            float newHeight = (float) (width * 3.5);
            float newWidth = (float) (height * 3.5);
            return ((height > newHeight) || (width > newWidth) || (width > 15 * 1000) || (height > 15 * 1000));
        }
        return false;
    }

//    /**
//     * 是否是长图
//     *
//     * @return true 是 or false 不是
//     */
//    public static boolean isLongImg(int width, int height) {
//        float newHeight = (float) (width * 3.5);
//        return height > newHeight;
//    }
}
