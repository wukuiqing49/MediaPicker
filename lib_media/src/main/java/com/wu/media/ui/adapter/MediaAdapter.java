package com.wu.media.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.wkq.base.utils.StringUtils;
import com.wu.media.PickerConfig;
import com.wu.media.R;
import com.wu.media.databinding.MediaImageItemBinding;
import com.wu.media.media.entity.Media;
import com.wu.media.model.ImagePickerOptions;
import com.wu.media.utils.AndroidQUtil;
import com.wu.media.utils.FileUtils;
import com.wu.media.utils.GlideCacheUtil;
import com.wu.media.utils.ScreenUtils;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/9/16 9:32
 * <p>
 * 名 字 : MeidaAdapter
 * <p>
 * 简 介 :
 */
public class MediaAdapter extends BaseRecyclerViewAdapter<Media> {

    public static final int TYPE_CAMERA = 0;
    public static final int TYPE_NORMAL = 1;
    Context mContxt;
    boolean showTime;
    OnItemClickListener listener;
    ImagePickerOptions mOptions;
    private MediaImageItemBinding binding;
    int itemHight = 0;

    public MediaAdapter(Context context, boolean showTime, ImagePickerOptions mOptions) {
        super(context);
        mContxt = context;
        this.showTime = showTime;
        this.mOptions = mOptions;
        itemHight = getItemWidth();
    }

    /**
     * 条目点击事件
     *
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewDataBinding viewDataBinding = null;
        switch (viewType) {
            case TYPE_CAMERA:
                viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContxt), R.layout.media_camera_item, parent, false);
                break;
            case TYPE_NORMAL:
            default:
                viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContxt), R.layout.media_image_item, parent, false);
                break;
        }
        //让图片是个正方形
        viewDataBinding.getRoot().setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHight));
        MediaViewHolder holder = new MediaViewHolder(viewDataBinding.getRoot());
        holder.setBinding(viewDataBinding);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MediaViewHolder mediaViewHolder = (MediaViewHolder) holder;
        switch (getItemViewType(position)) {
            case MediaAdapter.TYPE_CAMERA:
                showCamera(mediaViewHolder, position);
                break;
            case MediaAdapter.TYPE_NORMAL:
            default:
                showMedia(mediaViewHolder, position);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Media meida = getItem(position);
        if (meida.mediaType == 0) {
            //相机
            return TYPE_CAMERA;
        } else {
            //默认
            return TYPE_NORMAL;
        }
    }

    // 获取动态条目的宽度
    int getItemWidth() {
        return (ScreenUtils.getScreenWidth(mContxt) / PickerConfig.GridSpanCount) - PickerConfig.GridSpanCount;
    }

    /**
     * 处理图像展示的逻辑
     *
     * @param mediaViewHolder
     */
    private void showMedia(MediaViewHolder mediaViewHolder, int position) {
        Media mMedia = getItem(position);
        binding = (MediaImageItemBinding) mediaViewHolder.getBinding();
        GlideCacheUtil.intoItemImageThumbnail(mContext, mMedia, binding.mediaImage, null);
//        processQr(binding, mMedia);
        if (mMedia.mediaType == 1) {
            binding.videoInfo.setVisibility(View.GONE);
        } else if (mMedia.mediaType == 3) {
            binding.videoInfo.setVisibility(View.VISIBLE);
            binding.textViewSize.setText(showTime ? StringUtils.gennerTime(mMedia.duration / 1000) : FileUtils.getSizeByUnit(mMedia.size));
        }
        //单选
        if (mOptions.singlePick || mOptions.isNeedCrop() || mOptions.maxNum == 1 || (mOptions.getSelectMode() == PickerConfig.PICKER_ONLY_ONE_TYPE && mMedia.mediaType == 3)) {
            binding.checkImage.setVisibility(View.GONE);
        } else {
            binding.checkImage.setVisibility(View.VISIBLE);
            binding.checkImage.setImageDrawable(mMedia.isSelect() ? ContextCompat.getDrawable(mContext, R.drawable.iv_media_checked) : ContextCompat.getDrawable(mContext, R.drawable.xc_weixuan));
        }

        binding.checkImage.setOnClickListener(v -> {
            progressSelectItem(binding, position);
        });
        binding.mediaImage.setOnClickListener(v -> {
            if (listener != null) listener.itemClick(mMedia);
        });

    }

    private void processQr(MediaImageItemBinding binding, Media mMedia) {

        if (mMedia == null || mMedia.mediaType == 3 || binding == null) return;

        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {

                HmsScanAnalyzerOptions options = new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE, HmsScan.DATAMATRIX_SCAN_TYPE).setPhotoMode(true).create();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(),  Uri.parse(mMedia.fileUri));
                HmsScan[] hmsScans = ScanUtil.decodeWithBitmap(mContext, bitmap, options);
                if (hmsScans.length > 0) {
                    emitter.onNext(true);
                } else {
                    emitter.onNext(false);
                }
            }


        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Boolean s) {
                if (s) {
                    binding.ivQr.setVisibility(View.VISIBLE);
                } else {
                    binding.ivQr.setVisibility(View.GONE);
                }

            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * 处理选中的事件
     *
     * @param binding
     */
    private void progressSelectItem(MediaImageItemBinding binding, int position) {
        if (listener != null)
            listener.itemSelect(getItem(position).isSelect(), getItem(position), position);
    }

    /**
     * 处理相机点击事件
     *
     * @param mediaViewHolder
     */
    private void showCamera(MediaViewHolder mediaViewHolder, int position) {
        mediaViewHolder.getBinding().getRoot().setOnClickListener(v -> {
            if (listener != null) listener.itemCameraClick(getItem(position));
        });
    }


}
