package com.wu.media.view;

import android.text.TextUtils;

import androidx.activity.OnBackPressedCallback;
import androidx.viewpager2.widget.ViewPager2;

import com.wkq.base.frame.mosby.delegate.MvpView;
import com.wkq.base.utils.AlertUtil;
import com.wu.media.ui.activity.MediaActivity;
import com.wu.media.ui.adapter.MediaPreviewAdapter;
import com.wu.media.ui.fragment.MediaPreviewFragment;
import com.wu.media.utils.observable.MediaPageSelectStateObservable;


/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/9/17 14:30
 * <p>
 * 名 字 : MediaPreviewview
 * <p>
 * 简 介 :
 */
public class MediaPreviewView implements MvpView {
    MediaPreviewFragment mFragment;

    private int currentPosition = 0;

    public MediaPreviewView(MediaPreviewFragment mFragment) {
        this.mFragment = mFragment;
    }

    public void initView(MediaActivity mActivity) {
        if (mFragment.isPre) {
            mFragment.mediaPreviewAdapter = new MediaPreviewAdapter(mActivity, mActivity.selects);
            mFragment.binding.viewpager.setAdapter(mFragment.mediaPreviewAdapter);
            mFragment.binding.viewpager.setCurrentItem(0, false);
            mFragment.preMedias = mActivity.selects;
        } else {

            if (mFragment.preMedias.size() == 0) mFragment.preMedias.addAll(mActivity.allMedias);
            if (mFragment.preMedias != null && mFragment.preMedias.size() > 0 && mFragment.preMedias.get(0).mediaType == 0) {
                mFragment.preMedias.remove(0);
            }
            mFragment.mediaPreviewAdapter = new MediaPreviewAdapter(mActivity, mFragment.preMedias);
            if (mFragment.mMedia == null) {
                currentPosition = 0;
            } else {
                currentPosition = mFragment.preMedias.indexOf(mFragment.mMedia);

                mFragment.binding.viewpager.setAdapter(mFragment.mediaPreviewAdapter);
                mFragment.binding.viewpager.setCurrentItem(currentPosition, false);
            }
        }

        mFragment.binding.viewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mFragment.mMedia = mFragment.preMedias.get(position);
                MediaPageSelectStateObservable.getInstance().pageSelectState(mFragment.mMedia);
            }
        });
    }

    /**
     * 处理 fragment 返回的逻辑
     */
    public void initBack(MediaActivity mActivity) {
        mFragment.mback = mFragment.requireActivity().getOnBackPressedDispatcher();
        mFragment.callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                mFragment.callback.setEnabled(false);
                mFragment.mback.onBackPressed();
            }
        };
        mFragment.mback.addCallback(mFragment, mFragment.callback);
    }


    public void showMessage(String message) {
        if (mFragment == null || mFragment.getActivity() == null || TextUtils.isEmpty(message))
            return;
        AlertUtil.showDeftToast(mFragment.getActivity(), message);
    }
}
