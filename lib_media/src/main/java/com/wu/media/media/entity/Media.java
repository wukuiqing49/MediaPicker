package com.wu.media.media.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/9/15 11:26
 * <p>
 * 名 字 : Media
 * <p>
 * 简 介 :  媒体数据的数据模型
 */
public class Media  implements Parcelable {
    public String path;
    public String name;
    public long time;
    // mediaType 3:视频  1:图片 0: 相机
    public int mediaType;
    public long size;
    public int id;
    public String parentDir;
    public boolean isDeleted;
    public int duration;
    public boolean compressed;
    //
    public boolean isReturnUri;
    public String fileUri;
    public String thumbnails;

    //是否选中
    public boolean isSelect;

    public int imgWidth;
    public int imgHeight;

    public boolean isLongImg;
    //损坏图片
    public boolean isFail;

    public int getImgWidth() {
        return imgWidth;
    }

    public void setImgWidth(int imgWidth) {
        this.imgWidth = imgWidth;
    }

    public int getImgHeight() {
        return imgHeight;
    }

    public void setImgHeight(int imgHeight) {
        this.imgHeight = imgHeight;
    }

    public boolean isLongImg() {
        return isLongImg;
    }

    public void setLongImg(boolean longImg) {
        isLongImg = longImg;
    }

    public void setFail(boolean fail) {
        isFail = fail;
    }

    public Media() {

    }

    public Media(int mediaType) {
        this.mediaType = mediaType;
    }

    public Media(String path, String name, long time, int mediaType, long size, int id, String parentDir, int duration, String fileUri) {
        this.path = path;
        this.name = name;
        this.time = time;
        this.mediaType = mediaType;
        this.size = size;
        this.id = id;
        this.parentDir = parentDir;
        this.duration = duration;
        this.compressed = false;
        this.fileUri = fileUri;
    }


    public Media(String path, String name, long time, int mediaType, long size, int id, String parentDir, int duration, boolean compressed) {
        this.path = path;
        this.name = name;
        this.time = time;
        this.mediaType = mediaType;
        this.size = size;
        this.id = id;
        this.parentDir = parentDir;
        this.duration = duration;
        this.compressed = compressed;
        this.fileUri = fileUri;
    }

    public Media(String path, String name, long time, int mediaType, long size, int id, String parentDir, String fileUri) {
        this.path = path;
        this.name = name;
        this.time = time;
        this.mediaType = mediaType;
        this.size = size;
        this.id = id;
        this.parentDir = parentDir;
        this.fileUri = fileUri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeString(this.name);
        dest.writeLong(this.time);
        dest.writeInt(this.mediaType);
        dest.writeLong(this.size);
        dest.writeInt(this.id);
        dest.writeString(this.parentDir);
        dest.writeByte((byte) (isDeleted ? 1 : 0));
        dest.writeByte((byte) (isSelect ? 1 : 0));
        dest.writeByte((byte) (isReturnUri ? 1 : 0));
        dest.writeInt(duration);
        dest.writeByte((byte) (compressed ? 1 : 0));
        dest.writeString(this.fileUri);
    }

    protected Media(Parcel in) {
        this.path = in.readString();
        this.name = in.readString();
        this.time = in.readLong();
        this.mediaType = in.readInt();
        this.size = in.readLong();
        this.id = in.readInt();
        this.parentDir = in.readString();
        this.isDeleted = in.readByte() != 0;
        this.isSelect = in.readByte() != 0;
        this.isReturnUri = in.readByte() != 0;
        this.duration = in.readInt();
        this.compressed = in.readByte() != 0;
        this.fileUri = in.readString();
    }

    public static final Creator<Media> CREATOR = new Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel source) {
            return new Media(source);
        }

        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };

    public boolean isReturnUri() {
        return isReturnUri;
    }

    public void setReturnUri(boolean isReturnUri) {
        this.isReturnUri = isReturnUri;
    }
    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Media media = (Media) o;
        return
                mediaType == media.mediaType &&
                size == media.size &&
                id == media.id &&
                duration == media.duration &&
                Objects.equals(path, media.path) &&
                Objects.equals(name, media.name) &&
                Objects.equals(parentDir, media.parentDir) &&
                Objects.equals(fileUri, media.fileUri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, name, mediaType, size, id, parentDir, duration, fileUri);
    }
}
