package com.wu.media.ui.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.wkq.base.frame.fragment.MvpBindingFragment;
import com.wu.media.R;
import com.wu.media.databinding.FragmentMediaBinding;
import com.wu.media.media.entity.Media;
import com.wu.media.presenter.MediaFPresenter;
import com.wu.media.view.MediaFView;


/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/9/17 16:14
 * <p>
 * 名 字 : MediaFragment
 * <p>
 * 简 介 :
 */
public class MediaFragment extends MvpBindingFragment<MediaFView, MediaFPresenter, FragmentMediaBinding> {

    public Media mMedia;

    public static MediaFragment newInstance(Media mMedia) {

        MediaFragment fragment = new MediaFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("media", mMedia);
        fragment.setArguments(bundle);
        return fragment;

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_media;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMedia = getArguments().getParcelable("media");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       if (getMvpView()!=null)getMvpView().initView();
    }
}
