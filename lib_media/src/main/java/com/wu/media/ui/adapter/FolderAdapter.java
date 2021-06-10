package com.wu.media.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;


import com.wu.media.R;
import com.wu.media.media.entity.Folder;
import com.wu.media.media.entity.Media;
import com.wu.media.utils.GlideCacheUtil;

import java.util.ArrayList;

/**
 * Created by dmcBig on 2017/7/19.
 */

public class FolderAdapter extends BaseAdapter {
    private ArrayList<Folder> folders;
    int lastSelected = 0;

    public FolderAdapter(ArrayList<Folder> folders, Context context) {
        this.folders = folders;
    }

    @Override
    public int getCount() {
        return folders.size();
    }

    @Override
    public Folder getItem(int position) {
        return folders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void updateAdapter(ArrayList<Folder> list) {
        this.folders = list;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        Context context = viewGroup.getContext();
        if (view == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = mInflater == null ? new View(context) : mInflater.inflate(R.layout.folders_view_item, viewGroup, false);
            holder = new ViewHolder(view);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Folder folder = getItem(position);
        if (folder != null && folder.getMedias().size() > 0) {
            if (folder.getMedias().size() > 0) {
                Media media = folder.getMedias().get(0);
                //填充图片
                GlideCacheUtil.intoItemImageThumbnail(context, media, holder.cover, null);
            } else {
                holder.cover.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.picker_default_image));
            }
        }


        holder.name.setText(folder.name);

        holder.size.setText(folder.getMedias().size() + "" + context.getResources().getString(R.string.count_string));
        holder.indicator.setVisibility(lastSelected == position ? View.VISIBLE : View.INVISIBLE);
        return view;
    }


    public void setSelectIndex(int i) {
        if (lastSelected == i) return;
        lastSelected = i;
        notifyDataSetChanged();
    }

    public ArrayList<Media> getSelectMedias() {
        return folders.get(lastSelected).getMedias();
    }

    public int getSelectIndex() {
        return lastSelected;
    }

    class ViewHolder {
        ImageView cover, indicator;
        TextView name, path, size;

        ViewHolder(View view) {
            cover = (ImageView) view.findViewById(R.id.cover);
            name = (TextView) view.findViewById(R.id.name);
            path = (TextView) view.findViewById(R.id.path);
            size = (TextView) view.findViewById(R.id.size);
            indicator = (ImageView) view.findViewById(R.id.indicator);
            view.setTag(this);
        }
    }

    public ArrayList<Folder> getFolders() {
        return folders;
    }
}
