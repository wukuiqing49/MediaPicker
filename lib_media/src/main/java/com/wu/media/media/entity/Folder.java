package com.wu.media.media.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/9/15 11:26
 * <p>
 * 名 字 : Folder
 * <p>
 * 简 介 :  文件夹的数据模型
 */

public class Folder implements Parcelable {

    public String name;

    public int count;

    ArrayList<Media> medias = new ArrayList<>();

    public void addMedias(Media media) {
        medias.add(media);
    }

    public Folder(String name) {
        this.name = name;
    }

    public ArrayList<Media> getMedias() {
        return this.medias;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.count);
        dest.writeTypedList(this.medias);
    }


    protected Folder(Parcel in) {
        this.name = in.readString();
        this.count = in.readInt();
        this.medias = in.createTypedArrayList(Media.CREATOR);
    }

    public static final Creator<Folder> CREATOR = new Creator<Folder>() {
        @Override
        public Folder createFromParcel(Parcel source) {
            return new Folder(source);
        }

        @Override
        public Folder[] newArray(int size) {
            return new Folder[size];
        }
    };
}
