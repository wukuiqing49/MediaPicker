package com.wu.media.ui.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wkq.base.frame.fragment.MvpBindingFragment;
import com.wu.media.R;
import com.wu.media.databinding.FragmentPickerBinding;
import com.wu.media.media.entity.Folder;
import com.wu.media.media.entity.Media;
import com.wu.media.presenter.MediaPickerFragmentPresenter;
import com.wu.media.ui.activity.MediaActivity;
import com.wu.media.ui.adapter.MediaAdapter;
import com.wu.media.utils.observable.SelectStateUpdateObservable;
import com.wu.media.view.MediaPickerFragmentView;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;


/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/9/17 12:34
 * <p>
 * 名 字 : MediaPickerFragment
 * <p>
 * 简 介 :
 */
public class MediaPickerFragment extends MvpBindingFragment<MediaPickerFragmentView, MediaPickerFragmentPresenter, FragmentPickerBinding>
        implements View.OnClickListener, Observer {

    public MediaActivity mActivity;
    public MediaAdapter mMediaAdapter = null;

    public static MediaPickerFragment newInstanse() {

        MediaPickerFragment fragment = new MediaPickerFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;

    }

    // 处理 返回事件的监听
    public OnBackPressedCallback callback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            callback.setEnabled(false);
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_picker;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SelectStateUpdateObservable.getInstance().addObserver(this);
        mActivity = (MediaActivity) getActivity();
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        //初始化控件
        if (getMvpView() != null) getMvpView().initView(mActivity);
    }

    public void setMediaData(ArrayList<Folder> medias) {
        if (getMvpView() != null) getMvpView().initMediaData(mActivity, medias);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.btn_back) {
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        } else if (viewId == R.id.done) {
            mActivity.finishMedia();

        } else if (viewId == R.id.preview) {
            if (getMvpView() != null) getMvpView().showPrew(mActivity);
        }
    }

    public void addMedia(Media media) {
        if (media != null && mMediaAdapter != null) {
            if (mActivity.mOptions.isNeedCamera()) {
                mActivity.allMedias.add(1, media);
                mMediaAdapter.addItem(1, media);
            } else {
                mActivity.allMedias.add(0, media);
                mMediaAdapter.addItem(0, media);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (callback != null) callback.remove();
        SelectStateUpdateObservable.getInstance().deleteObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof SelectStateUpdateObservable) {
            Media isUpdateMedia = (Media) arg;
            if (getMvpView() != null && isUpdateMedia != null)
                getMvpView().selectDateUpdate(mActivity, isUpdateMedia);
        }
    }

    public void notifySelect(){

    }
}
