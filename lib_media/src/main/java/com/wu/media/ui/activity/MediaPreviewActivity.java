package com.wu.media.ui.activity;

import android.os.Bundle;


import com.wkq.base.frame.activity.MvpBindingActivity;
import com.wu.media.PickerConfig;
import com.wu.media.R;
import com.wu.media.databinding.ActivityPreviewImageBinding;
import com.wu.media.media.entity.Media;
import com.wu.media.presenter.MediaPreviewViewPresenter;
import com.wu.media.ui.adapter.MediaPreviewAdapter;
import com.wu.media.view.MediaPreviewViewView;

import java.util.ArrayList;

/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/10/23 9:27
 * <p>
 * 名 字 : MediaPreviewActivity
 * <p>
 * 简 介 : 外部预览页面
 */
public class MediaPreviewActivity extends MvpBindingActivity<MediaPreviewViewView, MediaPreviewViewPresenter,  ActivityPreviewImageBinding> {

    //选中的数据
    public ArrayList<Media> selectLists = new ArrayList<>();
    // 现在选中
    public ArrayList<Media> currentArray = new ArrayList<>();
    //位置
    public int position;

    public int resultCode = 200;

    public MediaPreviewAdapter mediaPreviewAdapter;

    public Media currentMedia;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_preview_image;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        position = getIntent().getIntExtra(PickerConfig.CURRENT_POSITION, -1);
        resultCode = getIntent().getIntExtra(PickerConfig.RESULT_CODE, -1);
        if (position < 0) position = 0;
        selectLists = getIntent().getParcelableArrayListExtra(PickerConfig.SELECTED_LIST);
        if (selectLists == null || selectLists.size() == 0) {
            if (getMvpView() != null) getMvpView().showMessage("数据异常");
            finish();
        } else {
            if (getMvpView() != null) getMvpView().initView();
        }


    }
}
