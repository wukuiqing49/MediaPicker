package com.wu.media.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.wu.media.R;
import com.wu.media.databinding.MediaPreviewBottomBarItemBinding;
import com.wu.media.media.entity.Media;
import com.wu.media.utils.GlideCacheUtil;

import java.util.List;

/**
 * 预览页面下面小图
 * Created by Lynn on 2018/1/17.
 */

public class PreviewBottomMediaAdapter extends BaseRecyclerViewAdapter<Media> {

    private Context context;
    private List<Media> mItems;
    boolean isPre;
    private int selectedPosition = -5;
    private OnBottomItemClickListener onBottomItemClickListener = null;

    public PreviewBottomMediaAdapter(Context context, int selectedPosition, boolean isPre) {
        super(context);
        this.context = context;
        this.selectedPosition = selectedPosition;
        this.isPre = isPre;
    }

    public void setOnItemClickListener(OnBottomItemClickListener listener) {
        this.onBottomItemClickListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        MediaPreviewBottomBarItemBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.media_preview_bottom_bar_item, parent, false);
        MediaViewHolder holder = new MediaViewHolder(viewDataBinding.getRoot());
        holder.setBinding(viewDataBinding);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MediaViewHolder mediaViewHolder = (MediaViewHolder) holder;
        MediaPreviewBottomBarItemBinding bottomBarItemBinding = (MediaPreviewBottomBarItemBinding) mediaViewHolder.getBinding();
        GlideCacheUtil.intoItemImage(context, getItem(position), bottomBarItemBinding.bottomItemImageView, null);
        if (selectedPosition == position) {
            bottomBarItemBinding.bottomItemImageView.setBackgroundResource(R.drawable.shape_green_square_bg);
        } else {
            bottomBarItemBinding.bottomItemImageView.setBackgroundResource(R.drawable.shape_gray_square_bg);
        }

        bottomBarItemBinding.bottomItemImageView.setOnClickListener(v -> {
            if (onBottomItemClickListener!=null)onBottomItemClickListener.onItemClick(position,getItem(position));
        });

    }


    public void updateBgState(int curPosition) {
//        if (curPosition < 0) return;
        selectedPosition = curPosition;
        notifyDataSetChanged();
    }


    public interface OnBottomItemClickListener {
        void onItemClick( int position, Media datas);
    }
}
