package com.wu.media;

/**
 * Created by dmcBig on 2017/6/9.
 */

public class PickerConfig {


    //视频文件最大时长
    public static final String MAX_TIME = "max_time";
    //视频录制时长
    public static final int RECODE_MAX_TIME = 60*1000;
    // 默认视频时长
    public static final int DEFAULT_MAX_TIME = Integer.MAX_VALUE;
    //最大选择个数
    public static final int DEFAULT_MAX_NUM= Integer.MAX_VALUE;

    // 默认视频大小
    public static final int DEFAULT_MAX_VIDEO_SIZE = Integer.MAX_VALUE;
    public static final int DEFAULT_MAX_IMAGE_SIZE = Integer.MAX_VALUE;

    //界面跳转options的键值
    public static final String INTENT_KEY_OPTIONS = "options";
    //图片选择模式，默认选视频和图片
    public static final String SELECT_MODE = "select_mode";
    //选择结果，返回为 ArrayList&lt;String&gt; 图片路径集合
    public static final String EXTRA_RESULT = "select_result";
    //默认选择集
    public static final String DEFAULT_SELECTED_LIST = "default_list";
    public static final String MEDIA_TYPE = "mediaType";
    //已选择的媒体预览集
    public static final String SELECTED_LIST = "selected_list";
    //跳转到预览页面指定的viewpager页面
    public static final String CURRENT_POSITION = "current_position";
    //传递待裁剪图片路径的key
    public static final String INTENT_KEY_ORIGIN_PATH = "originPath";
    //结果的Code
    public static final String RESULT_CODE = "result_code";
    /**
     * 拍照存储位置
     */
    public static final String CACHE_PATH = "cache_path";

    public static final String CAMERA_SELECT_MODE = "select_mode";

    public static final String OLD_LIST = "old_list";
    public static final int DEFAULT_RESULT_CODE = 19901026;
    //图片类型
    public static final int PICKER_IMAGE = 100;
    //视频类型
    public static final int PICKER_VIDEO = 102;
    //图片视频类型
    public static final int PICKER_IMAGE_VIDEO = 101;
    //只能选择一种类型
    public static final int PICKER_ONLY_ONE_TYPE = 103;
    //视频选一个 图片选九个
    public static final int PICKER_ONE_VIDEO_TYPE = 103;
    //展示图片的列数行数
    public static int GridSpanCount = 4;
    public static int GridSpace = 4;

    //裁剪请求码
    public static final int REQUEST_CODE_CROP = 113;
    //打开相机的权限请求有吗
    public static final int REQUEST_CODE_PERMISSION_CAMERA = 111;
    public static final int REQUEST_CODE_PERMISSION_READ = 112;
    public static final int REQUEST_CODE_PERMISSION_CROP = 113;
    //相机类型 图片视频
    public static final int CAMERA_MODE_ALL = 0;
    //相机类型 只拍图片
    public static final int CAMERA_MODE_PIC = 1;
    //相机类型 只拍视频
    public static final int CAMERA_MODE_VIDEO = 2;

    //裁剪后图片名字前缀
    public static final String CROP_NAME_PREFIX = "CROP_";
    //图片文件名后缀
    public static final String IMG_NAME_POSTFIX = ".jpg";

    public static String WHERE_JUMP_CAMERA = "jump_camera";
    public static String WHERE_JUMP_CAMERA_SELECTS = "jump_camera_selects";
}
