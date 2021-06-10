package com.wu.media.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.wu.media.R;
import com.wu.media.media.entity.Media;
import com.wu.media.utils.GlideCacheUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 预览页面下面小图
 * Created by Lynn on 2018/1/17.
 */

public class PreviewBottomAdapter extends RecyclerView.Adapter<PreviewBottomAdapter.ViewHolder> {

    private Context context;
    private List<Media> mItems;

    private int selectedPosition = -5;

    public PreviewBottomAdapter(Context context) {
        this.context = context;
        mItems = new ArrayList<>();
    }

    private OnBottomItemClickListener onBottomItemClickListener = null;

    public void setOnItemClickListener(OnBottomItemClickListener listener) {
        this.onBottomItemClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.media_preview_bottom_bar_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.rv_item_image = (ImageView) view.findViewById(R.id.bottom_item_image_view);
        holder.masking = view.findViewById(R.id.masking);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //填充图片
        GlideCacheUtil.intoItemImageThumbnail(context, mItems.get(position), holder.rv_item_image, null);

        if (selectedPosition == position) {
            holder.rv_item_image.setBackgroundResource(R.drawable.shape_green_square_bg);
        } else {
            holder.rv_item_image.setBackgroundResource(R.drawable.shape_gray_square_bg);
        }

        if (mItems.get(position).isDeleted) {
            holder.masking.setVisibility(View.VISIBLE);
        } else {
            holder.masking.setVisibility(View.GONE);
        }


        holder.rv_item_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBottomItemClickListener.onItemClick(view, position, mItems.get(position), (ArrayList<Media>) mItems);

                setItemBackground(position);

            }
        });
    }

    //更新item选中状态
    public void setItemBackground(int position) {
        int temp = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(temp,selectedPosition);
    }

    public void setItemMasking(int position, boolean visible) {
        if (visible) {
            mItems.get(position).isDeleted = true;
        } else {
            mItems.get(position).isDeleted = false;
        }
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

//    public int getIndex(int mediaId) {
//        for (int i = 0; i < mItems.size(); i++) {
//            if (mediaId == mItems.get(i).id) {
//                return mItems.indexOf(mItems.get(i));
//            }
//        }
//        return -1;
//    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }

        ImageView rv_item_image;
        ImageView masking;
    }

    public void addItem(@Nullable Media item) {
        if (item == null) return;
        mItems.add(mItems.size(), item);
        notifyItemInserted(mItems.size());
    }

    public void removeItem(Media item) {
        mItems.remove(item);
        notifyDataSetChanged();
    }

    public void removeItem(int i) {
        mItems.remove(i);
        notifyDataSetChanged();
    }

    public void addItems(@Nullable List<Media> items) {
        if (items == null) return;
        this.mItems.addAll(items);
        notifyDataSetChanged();

//        for (int i = 0; i < items.size(); i++) {
//            mItems.add(i, items.get(i));
//            notifyItemInserted(i);
//        }
    }

    public interface OnBottomItemClickListener {
        void onItemClick(View view, int position, Media data, ArrayList<Media> selectMedias);
    }
}
