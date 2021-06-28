package com.wu.media.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.wu.media.PickerConfig;
import com.wu.media.media.entity.Media;

import java.util.ArrayList;

/**
 *
 */
public class ImagePickerOptions implements Parcelable {
    public ImagePickerOptions() {
    }

    //最大个数
    public int maxNum = PickerConfig.DEFAULT_MAX_NUM;
    //视频的最大 的大小
    public long maxVideoSize = PickerConfig.DEFAULT_MAX_VIDEO_SIZE;
    //图片的最大大小
    public long maxImageSize = PickerConfig.DEFAULT_MAX_IMAGE_SIZE;
    //是否展示相机
    public boolean needCamera = false;
    //选择模式
    public int selectMode = PickerConfig.PICKER_IMAGE_VIDEO;
    //选中的集合
    public ArrayList<Media> selects;
    //缓存地址
    public String cachePath;
    //剪辑视频存储路径
    public String videoTrimPath;
    //是否是朋友圈
    public boolean friendCircle = false;
    //裁剪的数据
    public ImagePickerCropParams cropParams;
    //是否需要裁剪
    public boolean needCrop = false;
    //单选
    public boolean singlePick = false;
    //展示时长
    public boolean showTime = false;
    public int maxTime = PickerConfig.RECODE_MAX_TIME;
    //是否选择Gif
    public boolean selectGift = true;
    //处理返回uri地址
    public boolean isReturnUri = false;
    //直接跳转相机 控制相机的模式 0 全支持  1 照片 2 视频
    public int jumpCameraMode = -1;
    public int resultCode = 10086;
    public boolean  isPreview = false;
    public boolean isPreview() {
        return isPreview;
    }

    public void setPreview(boolean preview) {
        isPreview = preview;
    }



    public int getJumpCameraMode() {
        return jumpCameraMode;
    }

    public void setJumpCameraMode(int jumpCameraMode) {
        this.jumpCameraMode = jumpCameraMode;
    }

    public boolean isSelectGift() {
        return selectGift;
    }

    public void setSelectGif(boolean selectGift) {
        this.selectGift = selectGift;
    }


    public int getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }

    public boolean isShowTime() {
        return showTime;
    }

    public void setShowTime(boolean showTime) {
        this.showTime = showTime;
    }

    public int getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }

    public long getMaxVideoSize() {
        return maxVideoSize;
    }

    public void setMaxVideoSize(long maxVideoSize) {
        this.maxVideoSize = maxVideoSize;
    }

    public long getMaxImageSize() {
        return maxImageSize;
    }

    public void setMaxImageSize(long maxImageSize) {
        this.maxImageSize = maxImageSize;
    }

    public boolean isNeedCamera() {
        return needCamera;
    }

    public void setNeedCamera(boolean needCamera) {
        this.needCamera = needCamera;
    }

    public int getSelectMode() {
        return selectMode;
    }

    public void setSelectMode(int selectMode) {
        this.selectMode = selectMode;
    }

    public ArrayList<Media> getSelects() {
        return selects;
    }

    public void setSelects(ArrayList<Media> selects) {
        this.selects = selects;
    }

    public String getCachePath() {
        return cachePath;
    }

    public void setCachePath(String cachePath) {
        this.cachePath = cachePath;
    }

    public boolean isFriendCircle() {
        return friendCircle;
    }

    public void setFriendCircle(boolean friendCircle) {
        this.friendCircle = friendCircle;
    }

    public boolean isNeedCrop() {
        return needCrop;
    }

    public void setNeedCrop(boolean needCrop) {
        this.needCrop = needCrop;
    }

    public ImagePickerCropParams getCropParams() {
        return cropParams;
    }

    public void setCropParams(ImagePickerCropParams cropParams) {
        this.cropParams = cropParams;
    }

    public boolean isSinglePick() {
        return singlePick;
    }

    public void setSinglePick(boolean singlePick) {
        this.singlePick = singlePick;
    }

    public String getVideoTrimPath() {
        return videoTrimPath;
    }

    public void setVideoTrimPath(String videoTrimPath) {
        this.videoTrimPath = videoTrimPath;
    }

    public boolean isReturnUri() {
        return isReturnUri;
    }

    public void setReturnUri(boolean isReturnUri) {
        this.isReturnUri = isReturnUri;
    }
    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    protected ImagePickerOptions(Parcel in) {
        maxNum = in.readInt();
        maxVideoSize = in.readLong();
        maxImageSize = in.readLong();
        needCamera = in.readByte() != 0;
        selectMode = in.readInt();
        resultCode = in.readInt();
        selects = in.createTypedArrayList(Media.CREATOR);
        cachePath = in.readString();
        videoTrimPath = in.readString();
        friendCircle = in.readByte() != 0;
        cropParams = in.readParcelable(ImagePickerCropParams.class.getClassLoader());
        needCrop = in.readByte() != 0;
        singlePick = in.readByte() != 0;
        showTime = in.readByte() != 0;
        maxTime = in.readInt();
        selectGift = in.readByte() != 0;
        isReturnUri = in.readByte() != 0;
        jumpCameraMode = in.readInt();
        isPreview = in.readByte() != 0;
    }

    public static final Creator<ImagePickerOptions> CREATOR = new Creator<ImagePickerOptions>() {
        @Override
        public ImagePickerOptions createFromParcel(Parcel in) {
            return new ImagePickerOptions(in);
        }

        @Override
        public ImagePickerOptions[] newArray(int size) {
            return new ImagePickerOptions[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(maxNum);
        dest.writeLong(maxVideoSize);
        dest.writeLong(maxImageSize);
        dest.writeByte((byte) (needCamera ? 1 : 0));
        dest.writeInt(selectMode);
        dest.writeInt(resultCode);
        dest.writeTypedList(selects);
        dest.writeString(cachePath);
        dest.writeString(videoTrimPath);
        dest.writeByte((byte) (friendCircle ? 1 : 0));
        dest.writeParcelable(cropParams, flags);
        dest.writeByte((byte) (needCrop ? 1 : 0));
        dest.writeByte((byte) (singlePick ? 1 : 0));
        dest.writeByte((byte) (showTime ? 1 : 0));
        dest.writeInt(maxTime);
        dest.writeByte((byte) (selectGift ? 1 : 0));
        dest.writeByte((byte) (isReturnUri ? 1 : 0));
        dest.writeInt(jumpCameraMode);
        dest.writeByte((byte)(isPreview ? 1 : 0));;
    }
}
