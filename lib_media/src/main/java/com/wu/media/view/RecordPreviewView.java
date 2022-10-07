package com.wu.media.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.wkq.base.frame.mosby.delegate.MvpView;
import com.wkq.base.utils.AlertUtil;
import com.wu.media.PickerConfig;
import com.wu.media.media.entity.Media;
import com.wu.media.ui.activity.CropActivity;
import com.wu.media.ui.activity.CustomCameraActivity;
import com.wu.media.ui.activity.ImageCropActivity;
import com.wu.media.ui.activity.RecordActivity;
import com.wu.media.ui.fragment.RecordPreviewFragment;
import com.wu.media.utils.GlideCacheUtil;
import com.wu.media.utils.MediaUtils;
import com.wu.media.utils.album.AlbumProcessUtil;
import com.wu.media.utils.album.OnSaveCallback;
import com.wu.media.utils.observable.MediaAddObservable;
import com.wu.media.utils.observable.MeidaResultObservable;


import java.io.File;
import java.util.ArrayList;

/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/9/28 9:35
 * <p>
 * 名 字 : RecordPreviewView
 * <p>
 * 简 介 :
 */
public class RecordPreviewView implements MvpView {
    RecordPreviewFragment mFragment;

    public RecordPreviewView(RecordPreviewFragment mFragment) {
        this.mFragment = mFragment;
    }

    public void initView(CustomCameraActivity mActivity) {

        if (mFragment.type == 0) {
            mFragment.binding.ivPre.setVisibility(View.VISIBLE);
            mFragment.binding.rlVideo.setVisibility(View.GONE);
            GlideCacheUtil.intoItemImageBitmap(mActivity, mFragment.path, mFragment.binding.ivPre);
        } else if (mFragment.type == 1) {
            mFragment.binding.ivPre.setVisibility(View.GONE);
            mFragment.binding.rlVideo.setVisibility(View.VISIBLE);
            RequestOptions option = new RequestOptions();
            option.dontTransform()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .dontAnimate();
            Glide.with(mFragment).load(mFragment.path).apply(option).into(mFragment.binding.ivVideo);
        }

        ObjectAnimator animator_cancel = ObjectAnimator.ofFloat(mFragment.binding.tbOk, "translationX", 200, 0);
        ObjectAnimator animator_confirm = ObjectAnimator.ofFloat(mFragment.binding.tbCancel, "translationX", -200, 0);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(animator_cancel, animator_confirm);
        set.setInterpolator(new OvershootInterpolator());
        set.setDuration(200);
        set.start();

    }


    public void showMessage(String message) {
        if (mFragment == null || TextUtils.isEmpty(message)) return;
        AlertUtil.showDeftToast(mFragment.getActivity(), message);
    }

    public void showSuccessMessage(String message) {
        if (mFragment == null || TextUtils.isEmpty(message)) return;
        AlertUtil.showSuccessToast(mFragment.getActivity(), message);
    }

    public void savePic(String path) {

        AlbumProcessUtil.saveImageToGallery(mFragment.getActivity(), new File(path), new OnSaveCallback() {
            @Override
            public void onSuccess(String path) {
                File file = new File(path);
                if (file != null && file.exists() && file.canRead() && file.canWrite()) {

                    Uri uri = FileProvider.getUriForFile(mFragment.getActivity(), mFragment.getActivity().getApplicationContext().getPackageName() + ".fileprovider", file);
                    Media media = new Media(path, file.getName(), System.currentTimeMillis(), 1, file.length(), (int) System.currentTimeMillis(), file.getParent(), uri.toString());
                    media.setSelect(false);
                    media.setReturnUri(mFragment.mActivity.mOptions.isReturnUri());
                    if (mFragment.mActivity.preBitmap != null && mFragment.mActivity.preBitmap.getHeight() > 0 && mFragment.mActivity.preBitmap.getWidth() > 0) {
                        media.setImgHeight(mFragment.mActivity.preBitmap.getHeight());
                        media.setImgWidth(mFragment.mActivity.preBitmap.getWidth());
                        media.setLongImg(MediaUtils.isLongImg(mFragment.mActivity.preBitmap.getWidth(), mFragment.mActivity.preBitmap.getHeight()));
                    }
                    if (mFragment.mActivity.mOptions.needCrop) {
//                        ImageCropActivity.start(mFragment.mActivity, path, mFragment.mActivity.mOptions);
                        CropActivity.start(mFragment.mActivity, path, mFragment.mActivity.mOptions);
                    } else if (mFragment.mActivity.mOptions.isSinglePick() || mFragment.mActivity.mOptions.getMaxImageSize() == 1) {
                        MeidaResultObservable.getInstance().finishMedia(true, media);
                        mFragment.mActivity.finish();
                    } else if (mFragment.mActivity.isJumpCamera) {
                        if (mFragment.mActivity.selectMedia == null)
                            mFragment.mActivity.selectMedia = new ArrayList<Media>();
                        mFragment.mActivity.selectMedia.add(media);
                        Intent intent = new Intent();
                        intent.putParcelableArrayListExtra(PickerConfig.EXTRA_RESULT, mFragment.mActivity.selectMedia);
                        mFragment.mActivity.setResult(mFragment.mActivity.resultCode, intent);
                        mFragment.mActivity.finish();
                    } else {
                        MediaAddObservable.getInstance().addMedia(media);
                        mFragment.mActivity.finish();
                    }

                } else {
                    mFragment.requireActivity().getOnBackPressedDispatcher().onBackPressed();
                }
            }

            @Override
            public void onFail(String message) {
                showMessage("图片保存异常");
            }
        });
    }

    public void saveVideo(CustomCameraActivity mActivity) {
        if (mFragment.path == null || mActivity == null) return;
        File file = new File(mFragment.path);
        Uri uri = FileProvider.getUriForFile(mFragment.getActivity(), mFragment.getActivity().getApplicationContext().getPackageName() + ".fileprovider", file);
        Media media = new Media(mFragment.path, file.getName(), System.currentTimeMillis(), 3, file.length(), (int) System.currentTimeMillis(), file.getParent(), (int) 0, uri.toString());
        media.setSelect(false);
        media.setReturnUri(mActivity.mOptions.isReturnUri());
        if (mActivity.isJumpCamera) {
            MeidaResultObservable.getInstance().finishMedia(true, media);
        } else if (mActivity.mOptions.isSinglePick() || mActivity.mOptions.getMaxImageSize() == 1) {
            MeidaResultObservable.getInstance().finishMedia(true, media);
            mActivity.finish();
        } else if (mActivity.mOptions.needCrop) {
            CustomCameraActivity.newInstance(mActivity, mActivity.mOptions);
            mActivity.finish();
        } else {
            MediaAddObservable.getInstance().addMedia(media);
            mActivity.finish();
        }
    }
}
