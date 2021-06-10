package com.wu.media.view;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.viewpager2.widget.ViewPager2;

import com.wkq.base.frame.mosby.delegate.MvpView;
import com.wkq.base.utils.AlertUtil;
import com.wkq.base.utils.StatusBarUtil2;
import com.wu.media.PickerConfig;
import com.wu.media.R;
import com.wu.media.media.entity.Media;
import com.wu.media.ui.activity.MediaPreviewActivity;
import com.wu.media.ui.adapter.MediaPreviewAdapter;


/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/10/23 9:28
 * <p>
 * 名 字 : MediaPreviewViewView
 * <p>
 * 简 介 :
 */
public class MediaPreviewViewView implements MvpView {
    MediaPreviewActivity mActivity;

    public MediaPreviewViewView(MediaPreviewActivity mediaPreviewActivity) {
        mActivity = mediaPreviewActivity;
    }

    /**
     * 完成
     */
    public void finishMedia() {

        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(PickerConfig.EXTRA_RESULT, mActivity.currentArray);
        mActivity.setResult(mActivity.resultCode, intent);
        mActivity.finish();
    }

    public void initView() {
        StatusBarUtil2.setColor(mActivity, mActivity.getResources().getColor(R.color.status_bar_color), 0);

        mActivity.binding.preTop.findViewById(R.id.btn_back).setOnClickListener(v -> {
            mActivity.finish();
        });
        mActivity.currentMedia = mActivity.selectLists.get(mActivity.position);
        mActivity.binding.preTop.findViewById(R.id.check_layout_top).setVisibility(View.GONE);
//        if (mActivity.currentMedia.isSelect()) {
//            ((ImageView) mActivity.binding.preTop.findViewById(R.id.check_image_top)).setImageResource(R.drawable.xc_xuanzhong);
//        } else {
//            ((ImageView) mActivity.binding.preTop.findViewById(R.id.check_image_top)).setImageResource(R.drawable.xc_weixuan);
//        }
//        mActivity.currentArray.addAll(mActivity.selectLists);
//        mActivity.binding.preTop.findViewById(R.id.check_layout_top).setOnClickListener(v -> {
//            if (mActivity.currentMedia != null) {
//                int pos = mActivity.selectLists.indexOf(mActivity.currentMedia);
//                if (mActivity.currentMedia.isSelect()) {
//                    if (mActivity.currentArray.contains(mActivity.currentMedia)) {
//                        mActivity.currentArray.remove(mActivity.currentMedia);
//                    }
//                    mActivity.currentMedia.setSelect(false);
//                    if (mActivity.currentArray.contains(mActivity.currentMedia))
//                        mActivity.selectLists.get(pos).setSelect(false);
//                } else {
//                    mActivity.currentMedia.setSelect(true);
//                    mActivity.selectLists.get(pos).setSelect(true);
//                    if (!mActivity.currentArray.contains(mActivity.currentMedia)) {
//                        mActivity.currentArray.add(mActivity.currentMedia);
//                    }
//
//                }
//                if (mActivity.currentMedia.isSelect()) {
//                    ((ImageView) mActivity.binding.preTop.findViewById(R.id.check_image_top)).setImageResource(R.drawable.xc_xuanzhong);
//                } else {
//                    ((ImageView) mActivity.binding.preTop.findViewById(R.id.check_image_top)).setImageResource(R.drawable.xc_weixuan);
//                }
//            }
//        });
        mActivity.binding.preTop.findViewById(R.id.done).setVisibility(View.GONE);
        mActivity.binding.preTop.findViewById(R.id.done).setOnClickListener(v -> {
            //返回
            finishMedia();
        });
        mActivity.mediaPreviewAdapter = new MediaPreviewAdapter(mActivity, mActivity.selectLists);
        mActivity.binding.vpPreview.setAdapter(mActivity.mediaPreviewAdapter);
        mActivity.binding.vpPreview.setCurrentItem(mActivity.position, false);
        String barTitleText = String.format(mActivity.getResources().getString(R.string.btn_bar_title), mActivity.position + 1, mActivity.selectLists.size());
        ((TextView) mActivity.binding.preTop.findViewById(R.id.bar_title)).setText(barTitleText);


        mActivity.binding.vpPreview.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
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
                Media media = mActivity.selectLists.get(position);
                mActivity.currentMedia = media;
                showSelectPrew(media);
            }
        });

    }


    public void showSelectPrew(Media media) {
        String barTitleText = String.format(mActivity.getResources().getString(R.string.btn_bar_title), mActivity.selectLists.indexOf(media) + 1, mActivity.selectLists.size());
        ((TextView) mActivity.binding.preTop.findViewById(R.id.bar_title)).setText(barTitleText);
//        mActivity.binding.preTop.findViewById(R.id.check_layout_top).setVisibility(View.VISIBLE);
//
//        if (media.isSelect()) {
//            ((ImageView) mActivity.binding.preTop.findViewById(R.id.check_image_top)).setImageResource(R.drawable.xc_xuanzhong);
//        } else {
//            ((ImageView) mActivity.binding.preTop.findViewById(R.id.check_image_top)).setImageResource(R.drawable.xc_weixuan);
//        }

    }

    public void showMessage(String message) {
        if (mActivity == null || TextUtils.isEmpty(message)) {
            return;
        }
        AlertUtil.showDeftToast(mActivity, message);
    }

}
