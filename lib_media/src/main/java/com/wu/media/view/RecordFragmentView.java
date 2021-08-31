package com.wu.media.view;

import android.text.TextUtils;

import com.wkq.base.frame.mosby.delegate.MvpView;
import com.wkq.base.utils.AlertUtil;
import com.wu.media.ui.activity.RecordActivity;
import com.wu.media.ui.fragment.RecordFragment;

/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/10/29 10:05
 * <p>
 * 名 字 : RecordFragmentView
 * <p>
 * 简 介 :
 */
public class RecordFragmentView implements MvpView {

    RecordFragment mFragment;

    public RecordFragmentView(RecordFragment mFragment) {
        this.mFragment = mFragment;
    }

    public RecordActivity getActivity() {
        return (RecordActivity) mFragment.getActivity();
    }

    public void initView() {

    }

    /**
     * 异常
     *
     * @param message
     */
    public void showMesage(String message) {

        if (TextUtils.isEmpty(message) || mFragment == null || mFragment.getActivity().isFinishing())
            return;
        mFragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertUtil.showDeftToast(mFragment.getActivity(), message);
            }
        });


    }
}
