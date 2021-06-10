package com.wu.media.view;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.wkq.base.frame.mosby.delegate.MvpView;
import com.wkq.base.utils.AlertUtil;
import com.wu.media.media.entity.Media;
import com.wu.media.ui.fragment.MediaFragment;
import com.wu.media.ui.widget.large.ImageSource;
import com.wu.media.ui.widget.large.SubsamplingScaleImageView;
import com.wu.media.utils.AndroidQUtil;
import com.wu.media.utils.FileTypeUtil;
import com.wu.media.utils.GlideCacheUtil;

/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/9/17 16:16
 * <p>
 * 名 字 : MediaFView
 * <p>
 * 简 介 :
 */
public class MediaFView implements MvpView {
    MediaFragment mFragment;

    public MediaFView(MediaFragment mFragment) {
        this.mFragment = mFragment;
    }

    public void initView() {
        //是视频
        mFragment.binding.lvLoading.setVisibility(View.VISIBLE);
        if (mFragment.mMedia.mediaType == 3) {
            mFragment.binding.photoview.setVisibility(View.GONE);
            mFragment.binding.largePhoto.setVisibility(View.GONE);
            mFragment.binding.gifView.setVisibility(View.VISIBLE);
//            GlideCacheUtil.intoItemImage(mFragment.getActivity(), mFragment.mMedia, mFragment.binding.gifView, mFragment.binding.lvLoading);
            GlideCacheUtil.intoItemImageListener(mFragment.getActivity(), mFragment.mMedia, mFragment.binding.gifView, new RequestListener() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                    if (mFragment.binding.lvLoading != null) mFragment.binding.lvLoading .setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                    mFragment.binding.playView.setVisibility(View.VISIBLE);
                    if (mFragment.binding.lvLoading != null) mFragment.binding.lvLoading.setVisibility(View.GONE);
                    return false;
                }
            });

            mFragment.binding.playView.setOnClickListener(v -> {

                Uri uri = Uri.parse(mFragment.mMedia.fileUri);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "video/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                if (mFragment.getActivity().getPackageManager().queryIntentActivities(intent, 0).isEmpty()) {
                    AlertUtil.showDeftToast(mFragment.getActivity(), "未检测到播放器");
                } else {
                    mFragment.startActivity(intent);
                }

//                VideoPlayerActivity.newInstance(mFragment.getActivity(), mFragment.mMedia.fileUri);
            });
        } else {
            if (FileTypeUtil.isGif(mFragment.mMedia.path)) {
                mFragment.binding.largePhoto.setVisibility(View.GONE);
                mFragment.binding.photoview.setVisibility(View.GONE);
                //填充图片
                GlideCacheUtil.intoItemImage(mFragment.getActivity(), mFragment.mMedia, mFragment.binding.gifView, mFragment.binding.lvLoading);
            }
            if (FileTypeUtil.isNoSupport(mFragment.mMedia.path)) {
                mFragment.binding.largePhoto.setZoomEnabled(false);
                mFragment.binding.largePhoto.setVisibility(View.GONE);
                mFragment.binding.photoview.setVisibility(View.VISIBLE);
                //填充图片
                GlideCacheUtil.intoItemImageOther(mFragment.getActivity(), mFragment.mMedia, mFragment.binding.gifView, mFragment.binding.lvLoading);
            } else {
                mFragment.binding.largePhoto.setVisibility(View.GONE);
                mFragment.binding.photoview.setVisibility(View.VISIBLE);
                showPicView(mFragment.mMedia);
            }
        }
    }

    private void showPicView(Media media) {
        if (null == mFragment.getActivity() || null == mFragment.getContext()) {
            return;
        }
        //先判断是不是超长图
        if (!media.isLongImg && media.getImgHeight() != 0 && media.getImgWidth() != 0) {
            //填充图片
            GlideCacheUtil.intoItemImageOther(mFragment.getActivity(), media, mFragment.binding.photoview, mFragment.binding.lvLoading);
            return;
        }
        mFragment.binding.largePhoto.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM);
        mFragment.binding.largePhoto.setMaxScale(10.0F);//最大显示比例（太大了图片显示会失真，因为一般微博长图的宽度不会太宽）
        mFragment.binding.largePhoto.setZoomEnabled(false);
        mFragment.binding.largePhoto.setVisibility(View.VISIBLE);

        if (AndroidQUtil.isAndroidQ()) {
            mFragment.binding.largePhoto.setImage(ImageSource.uri(media.fileUri));
        } else {
            mFragment.binding.largePhoto.setImage(ImageSource.uri(media.path));
        }

        mFragment.binding.largePhoto.setOnImageEventListener(new SubsamplingScaleImageView.OnImageEventListener() {
            @Override
            public void onReady() {
                mFragment.binding.lvLoading.setVisibility(View.GONE);
                if (mFragment.binding.largePhoto != null)
                    mFragment.binding.largePhoto.setZoomEnabled(false);
            }

            @Override
            public void onImageLoaded() {
                mFragment.binding.lvLoading.setVisibility(View.GONE);
                if (mFragment.binding.largePhoto != null)
                    mFragment.binding.largePhoto.setZoomEnabled(true);
            }

            @Override
            public void onPreviewLoadError(Exception e) {
                mFragment.binding.lvLoading.setVisibility(View.GONE);
                if (mFragment.binding.largePhoto != null)
                    mFragment.binding.largePhoto.setZoomEnabled(false);
            }

            @Override
            public void onImageLoadError(Exception e) {
                if (mFragment.getActivity() == null && mFragment.getContext() == null) return;
                mFragment.binding.lvLoading.setVisibility(View.GONE);
                if (mFragment.binding.largePhoto != null)
                    mFragment.binding.largePhoto.setZoomEnabled(false);
                if (mFragment.binding.largePhoto != null)
                    mFragment.binding.largePhoto.setVisibility(View.GONE);
                mFragment.binding.photoview.setVisibility(View.VISIBLE);
                //填充图片
                GlideCacheUtil.intoItemImageOther(mFragment.getContext(), media, mFragment.binding.photoview, mFragment.binding.lvLoading);
            }

            @Override
            public void onTileLoadError(Exception e) {
                mFragment.binding.lvLoading.setVisibility(View.GONE);
                if (mFragment.binding.largePhoto != null)
                    mFragment.binding.largePhoto.setZoomEnabled(false);
            }

            @Override
            public void onPreviewReleased() {
            }
        });
    }


    public void showMessage(String message) {
        if (mFragment == null || mFragment.getActivity() == null || TextUtils.isEmpty(message))
            return;
        AlertUtil.showDeftToast(mFragment.getActivity(), message);
    }
}
