package com.wu.media.ui.adapter;

import android.view.View;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/9/15 17:12
 * <p>
 * 名 字 : MediaViewHolder
 * <p>
 * 简 介 :
 */
public class MediaViewHolder extends RecyclerView.ViewHolder {
    private ViewDataBinding binding;

    public MediaViewHolder(View itemView) {
        super(itemView);
    }

    public void setBinding(ViewDataBinding binding) {
        this.binding = binding;
    }

    public ViewDataBinding getBinding() {
        return this.binding;
    }
}
