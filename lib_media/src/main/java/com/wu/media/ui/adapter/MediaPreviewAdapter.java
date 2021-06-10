package com.wu.media.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;


import com.wu.media.media.entity.Media;
import com.wu.media.ui.fragment.MediaFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/9/17 16:12
 * <p>
 * 名 字 : MediaPreviewAdapter
 * <p>
 * 简 介 :
 */
public class MediaPreviewAdapter extends FragmentStateAdapter {
    List<Media> mediaList = new ArrayList<>();

    public MediaPreviewAdapter(@NonNull FragmentActivity fragmentActivity, List<Media> mediaList) {
        super(fragmentActivity);
        this.mediaList = mediaList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return MediaFragment.newInstance(mediaList.get(position));
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    public Media getMediaByPosition(int position) {
        if (mediaList.size() == 0 || position >= (mediaList.size())) return null;
        return mediaList.get(position);
    }

    public List<Media> getMedias() {
        return mediaList;
    }
}
