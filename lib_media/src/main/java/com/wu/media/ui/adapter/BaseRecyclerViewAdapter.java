package com.wu.media.ui.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/9/15 17:07
 * <p>
 * 名 字 : BaseRecyclerViewAdapter
 * <p>
 * 简 介 :
 */



public abstract class BaseRecyclerViewAdapter<T> extends Adapter<ViewHolder> {
    protected List<T> mItems;
    protected Context mContext;

    public BaseRecyclerViewAdapter(Context context) {
        this.mContext = context;
        this.mItems = new ArrayList();
    }

    public List<T> getList() {
        return this.mItems;
    }

    public void addItem(T item) {
        this.addItem(this.mItems.size(), item);
    }

    public void addItem(int index, T item) {
        if (item != null) {
            this.mItems.add(index, item);
            notifyDataSetChanged();
//            try {
//                this.notifyItemChanged(index,item);
//            } catch (Exception var4) {
//            }

        }
    }

    public void addItems(List<T> items) {
        if (items != null) {
            int position = mItems.size();
            this.mItems.addAll(items);
            notifyDataSetChanged();
            notifyItemRangeInserted(position, items.size());
            notifyItemRangeChanged(position, items.size());
        }
    }

    public void addItems(int indext, List<T> items) {
        if (items != null) {
            this.mItems.addAll(indext, items);
            this.notifyItemRangeInserted(indext, items.size());
        }
    }

    public boolean containsAll(List<T> items) {
        return this.mItems.containsAll(items);
    }

    public void updateItem(T tasks, int position) {
        if (tasks != null) {
            this.mItems.set(position, tasks);

            try {
                this.notifyItemChanged(position);
            } catch (Exception var4) {
            }

        }
    }

    public void updateItems(List<T> items) {
        if (items != null) {
            this.mItems.clear();
            this.mItems.addAll(items);
            this.notifyDataSetChanged();
        }
    }

    public int indexOf(T item) {
        return item != null && this.mItems != null && this.mItems.size() > 0 ? this.mItems.indexOf(item) : -1;
    }

    public void removeItem(int index) {
        this.mItems.remove(index);
        this.notifyItemRemoved(index);
    }

    public void removeAllItems() {
        this.mItems.clear();
        this.notifyDataSetChanged();
    }

    public void moveItem(T item, int fromPosition, int toPosition) {
        if (fromPosition > toPosition) {
            Collections.rotate(this.mItems.subList(toPosition, fromPosition + 1), 1);
        } else {
            Collections.rotate(this.mItems.subList(fromPosition, toPosition + 1), -1);
        }

        this.notifyItemMoved(fromPosition, toPosition);
        this.mItems.set(toPosition, item);
        this.notifyItemChanged(toPosition);
    }

    public void getView(int position, ViewHolder viewHolder, int type, T item) {
    }

    public T getItem(int location) {
        return this.mItems.get(location);
    }

    public int getItemCount() {
        return this.mItems == null ? 0 : this.mItems.size();
    }
}
