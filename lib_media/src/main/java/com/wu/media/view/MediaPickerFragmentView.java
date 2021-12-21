package com.wu.media.view;

import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;


import com.bumptech.glide.Glide;
import com.wkq.base.frame.mosby.delegate.MvpView;
import com.wkq.base.utils.AlertUtil;
import com.wkq.base.utils.DoublePressed;
import com.wu.media.PickerConfig;
import com.wu.media.R;
import com.wu.media.media.entity.Folder;
import com.wu.media.media.entity.Media;
import com.wu.media.ui.activity.MediaActivity;
import com.wu.media.ui.adapter.MediaAdapter;
import com.wu.media.ui.adapter.OnItemClickListener;
import com.wu.media.ui.fragment.MediaPickerFragment;
import com.wu.media.ui.widget.SpacingDecoration;
import com.wu.media.utils.FileTypeUtil;
import com.wu.media.utils.FileUtils;
import com.wu.media.utils.MediaStringUtils;
import com.wu.media.utils.observable.MediaOpenActivityObservable;
import com.wu.media.utils.observable.MediaOpenInfo;
import com.wu.media.utils.observable.MediaSelectStateObservable;
import com.wu.media.utils.observable.MeidaResultObservable;

import java.util.ArrayList;


/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/9/15 11:29
 * <p>
 * 名 字 : MediaPickerView
 * <p>
 * 简 介 :
 */
public class MediaPickerFragmentView implements MvpView {
    MediaPickerFragment mFragment;


    public MediaPickerFragmentView(MediaPickerFragment mFragment) {
        this.mFragment = mFragment;
    }

    public void initView(MediaActivity mActivity) {
        initMedia(mActivity);
    }


    private void initMedia(MediaActivity mActivity) {

        GridLayoutManager mLayoutManager = new GridLayoutManager(mFragment.getActivity(), PickerConfig.GridSpanCount);
        mFragment.binding.rv.setLayoutManager(mLayoutManager);
        mFragment.binding.rv.addItemDecoration(new SpacingDecoration(PickerConfig.GridSpanCount, PickerConfig.GridSpace));
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        mFragment.binding.rv.setHasFixedSize(true);
        // 处理 选中闪烁问题


        ((SimpleItemAnimator)   mFragment.binding.rv.getItemAnimator()).setSupportsChangeAnimations(false);
        mFragment.binding.rv.setItemAnimator(null);
//        mFragment.binding.rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (RecyclerView.SCROLL_STATE_IDLE == newState) {
//                    Glide.with(recyclerView).resumeRequests();
//                } else {
//                    Glide.with(recyclerView).pauseRequests();
//                }
//            }
//        });
        mFragment.mMediaAdapter = new MediaAdapter(mFragment.getActivity(), mActivity.showTime, mActivity.mOptions);
        mFragment.binding.rv.setAdapter(mFragment.mMediaAdapter);

        mFragment.mMediaAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void itemClick(Media media) {
                if (DoublePressed.onDoublePressed()) return;
                if (PickerConfig.PICKER_ONLY_ONE_TYPE == mActivity.selectMode && media.mediaType == 3) {
                    if (mActivity.selects.size() > 0) {
                        showMessage(mFragment.getResources().getString(R.string.msg_type_limit));
                    } else {
                        if (isCheckMediaSupport(mActivity, media)) {
                            MeidaResultObservable.getInstance().finishMedia(true, media);
                        }
                    }

                } else if (mActivity.mOptions.isSinglePick() || mActivity.mOptions.maxNum == 1) {

                    if (mActivity.mOptions.isNeedCrop()) {
                        if (media.mediaType != 1) {
                            showMessage("请选择图片");
                        } else {
                            MediaOpenActivityObservable.getInstance().openActivity(new MediaOpenInfo(2, media));
                        }
                    } else {
                        if (isCheckMediaSupport(mActivity, media)) {
                            MeidaResultObservable.getInstance().finishMedia(true, media);
                        }
                    }
                } else {
                    if (isCheckMediaSupport(mActivity, media)) {
                        if (mActivity.mOptions.isNeedCrop()) {
                            if (media.mediaType != 1) {
                                showMessage("请选择图片");
                            } else {
                                MediaOpenActivityObservable.getInstance().openActivity(new MediaOpenInfo(2, media));
                            }
                        } else {
                            mActivity.addPrePreviewFragment(media, false);
                        }
                    }
                }
            }

            @Override
            public void itemSelect(boolean isSelect, Media media, int position) {
                processSelectMedias(mActivity, isSelect, media, position);
            }

            @Override
            public void itemCameraClick(Media media) {
                processRecord(media);
            }
        });
    }

    private void processRecord(Media media) {
        MediaOpenActivityObservable.getInstance().openActivity(new MediaOpenInfo(1, null));
    }

    //处理数据选中的状态
    public void processSelectMedias(MediaActivity mActivity, boolean isSelect, Media media, int position) {
        if (isCheckMediaSupport(mActivity, media)) {
            MediaSelectStateObservable.getInstance().selectStateUpdateMedia(media);
            media.setSelect(!isSelect);
//            mFragment.mMediaAdapter.notifyItemChanged(position);
//            //处理 选中闪烁问题
            mFragment.mMediaAdapter.notifyItemChanged(position,  "1");
        }
    }

    //判断 文件是否支持外部配置   1.文件从存在不存在 2.文件大小 3.文件格式 4. 特殊需求 5.是否支持gif 6.文件大小  7.单选 选择数量
    public boolean isCheckMediaSupport(MediaActivity mActivity, Media media) {
        if (mActivity.selects.size() >= mActivity.maxNum && !media.isSelect()) {
            showMessage(mActivity.getResources().getString(R.string.msg_amount_limit));
            return false;
        }

        //文件是否存在
        if (media.size <= 0) {
            showMessage(mFragment.getResources().getString(R.string.string_file_err));
            return false;
        }
        //文件大小判断  1 图片  3 视频
        if (media.mediaType == 1 && mActivity.maxImageSize < media.size) {
            String size =FileUtils.fileSize(mActivity.maxImageSize);
            showMessage(mActivity.getResources().getString(R.string.media_msg_size_limit) + size);
            return false;
        }
        if (media.mediaType == 3 && mActivity.maxVideoSize < media.size) {
            String size = FileUtils.fileSize(mActivity.maxVideoSize);
            showMessage(mFragment.getResources().getString(R.string.media_msg_size_limit) + size);
            return false;
        }
        //文件格式
        if (media.mediaType == 1 && FileTypeUtil.checkImageType(media.path)) {
            showMessage(mFragment.getResources().getString(R.string.media_msg_img_limit));
            return false;
        }
        //文件格式
        if (media.mediaType == 3 && FileTypeUtil.checkVideoType(media.path)) {
            showMessage(mFragment.getResources().getString(R.string.media_msg_video_limit));
            return false;
        }
        //视频时长限制
        if (media.mediaType == 3 && media.duration > mActivity.maxVideoTime) {
            showMessage(mFragment.getResources().getString(R.string.media_msg_time_limit) + (MediaStringUtils.gennerMinSec(mActivity.maxVideoTime / 1000)));
            return false;
        }
        //判断是否支持gif
        if (!mActivity.selectGift && FileTypeUtil.isGif(media.path)) {
            showMessage(mFragment.getResources().getString(R.string.msg_gif_limit));
            return false;
        }

        if (mActivity.selectMode == PickerConfig.PICKER_ONLY_ONE_TYPE && mActivity.selects != null && mActivity.selects.size() > 0 && mActivity.selects.get(0).mediaType != media.mediaType) {
            showMessage(mFragment.getResources().getString(R.string.msg_type_limit));
            return false;
        }
        return true;
    }


    public void showMessage(String message) {
        if (mFragment == null || mFragment.getActivity() == null || TextUtils.isEmpty(message)) {
            return;
        }
        AlertUtil.showDeftToast(mFragment.getActivity(), message);
    }

    public void initMediaData(MediaActivity mediaActivity, ArrayList<Folder> medias) {
        if (medias == null || medias.size() == 0 || medias.get(0) == null || medias.get(0).getMedias() == null || medias.get(0).getMedias().size() == 0 || medias.get(0).getMedias().get(0) == null) {
            return;
        }
        mediaActivity.allMedias.addAll(medias.get(0).getMedias());
        for (Media select : mediaActivity.selects) {
            if (mediaActivity.allMedias.contains(select)) {
                mediaActivity.allMedias.get(mediaActivity.allMedias.indexOf(select)).setSelect(true);
            }
        }
        if (mediaActivity.needCamera && medias.get(0) != null && medias.get(0).getMedias() != null
                && medias.get(0).getMedias().get(0) != null && medias.get(0).getMedias().get(0).mediaType != 0) {
            mediaActivity.allMedias.add(0, new Media(0));
        }
        if (mFragment.mMediaAdapter == null)
            mFragment.mMediaAdapter = new MediaAdapter(mFragment.getActivity(), mediaActivity.showTime, mediaActivity.mOptions);
        mFragment.mMediaAdapter.updateItems(mediaActivity.allMedias);
    }

    public void showPrew(MediaActivity mActivity) {
        if (mActivity.selects.size() <= 0) {
            showMessage(mFragment.getResources().getString(R.string.select_null));
        } else {
            mActivity.addPrePreviewFragment(mActivity.selects.get(0), true);
        }
    }

    public void selectDateUpdate(MediaActivity mActivity, Media isUpdateMedia) {

        if (isUpdateMedia != null) {
//            int index = mActivity.allMedias.indexOf(isUpdateMedia);
            mFragment.mMediaAdapter.notifyDataSetChanged();
        }

    }
}
