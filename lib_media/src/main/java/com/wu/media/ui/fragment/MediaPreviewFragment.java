package com.wu.media.ui.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.Nullable;


import com.wkq.base.frame.fragment.MvpBindingFragment;
import com.wu.media.R;
import com.wu.media.databinding.FragmentPreviewBinding;
import com.wu.media.media.entity.Media;
import com.wu.media.presenter.MediaPreviewPresenter;
import com.wu.media.ui.activity.MediaActivity;
import com.wu.media.ui.adapter.MediaPreviewAdapter;
import com.wu.media.view.MediaPreviewView;

import java.util.ArrayList;
import java.util.List;

/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/9/17 14:19
 * <p>
 * 名 字 : MediaPreviewFragment
 * <p>
 * 简 介 :
 */
public class MediaPreviewFragment extends MvpBindingFragment<MediaPreviewView, MediaPreviewPresenter, FragmentPreviewBinding> {

    public OnBackPressedDispatcher mback;
    // 处理 返回事件的监听
    public OnBackPressedCallback callback = null;
    public Media mMedia;
    private MediaActivity mActivity;
    //是否是跳转选中的预览
    public boolean isPre;
    public MediaPreviewAdapter mediaPreviewAdapter;
    public List<Media> preMedias = new ArrayList<>();

    public static MediaPreviewFragment newInstanse(Media mMedia, boolean isPre) {
        MediaPreviewFragment fragment = new MediaPreviewFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("media", mMedia);
        //是否是选中的预览
        bundle.putBoolean("isPre", isPre);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_preview;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMedia = getArguments().getParcelable("media");
        isPre = getArguments().getBoolean("isPre");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity = (MediaActivity) getActivity();
        if (getMvpView() != null) getMvpView().initBack(mActivity);
        if (getMvpView() != null) getMvpView().initView(mActivity);
    }

    /**
     * 获取当前的Media
     *
     * @return
     */
    public Media getCurrentMedia() {
        return mMedia;
    }

    /**
     * 获取当前的Media
     *
     * @return
     */
    public void setMediaSelect(boolean isSelect) {
        if (mMedia != null) mMedia.setSelect(isSelect);
    }

    /**
     * 滑动到指定位置
     *
     * @param meida
     */
    public void showMediaPosition(Media meida) {
        int position = mediaPreviewAdapter.getMedias().indexOf(meida);
        if (position>=0)
        binding.viewpager.setCurrentItem(position);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (callback != null) callback.remove();
    }
}
