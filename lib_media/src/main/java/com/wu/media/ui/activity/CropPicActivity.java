package com.wu.media.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.wkq.base.frame.activity.MvpBindingActivity;
import com.wkq.base.utils.StatusBarUtil2;
import com.wu.media.PickerConfig;
import com.wu.media.R;
import com.wu.media.databinding.ActivityPicCropBinding;
import com.wu.media.model.ImagePickerCropParams;
import com.wu.media.model.ImagePickerOptions;
import com.wu.media.presenter.CropPrsenter;
import com.wu.media.view.CropPicView;

import java.io.File;


/**
 * 裁剪界面
 */
public class CropPicActivity extends MvpBindingActivity<CropPicView, CropPrsenter, ActivityPicCropBinding> implements View.OnClickListener {
    /**
     * 跳转到该界面的公共方法
     *
     * @param activity   发起跳转的Activity
     * @param originPath 待裁剪图片路径
     */
    /**
     * 跳转到该界面的公共方法
     *
     * @param activity   发起跳转的Activity
     * @param originPath 待裁剪图片路径
     * @param options    参数
     */
    public static void start(Activity activity, String originPath, ImagePickerOptions options) {
        Intent intent = new Intent(activity, CropPicActivity.class);
        intent.putExtra(PickerConfig.INTENT_KEY_ORIGIN_PATH, originPath);
        intent.putExtra(PickerConfig.INTENT_KEY_OPTIONS, options);
        activity.startActivityForResult(intent, PickerConfig.REQUEST_CODE_CROP);
    }

    public ImagePickerOptions mOptions;
    public String mOriginPath;
    private Handler mHandler;
    private ProgressDialog mDialog;
    public ImagePickerCropParams mCropParams;
    public File cacheFile;


    @Override
    protected int getLayoutId() {

        return R.layout.activity_pic_crop;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil2.setTransparentForWindow(this);
        StatusBarUtil2.addTranslucentView(this, 0);
        mOriginPath = getIntent().getStringExtra(PickerConfig.INTENT_KEY_ORIGIN_PATH);
        mHandler = new Handler(getMainLooper());
        mOptions = getIntent().getParcelableExtra(PickerConfig.INTENT_KEY_OPTIONS);

        if (getMvpView() != null) getMvpView().initView();
        if (getPresenter() != null) getPresenter().initData(this);
    }


    @Override
    protected void onDestroy() {
        if (mHandler != null) mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.cancel_button) {
            finish();
        } else if (id == R.id.rotate_button) {
            // 旋转
            binding.cropView.rotate(90);
        } else if (id == R.id.confirm_button) {
            //裁剪
            if (getMvpView() != null) getMvpView().returnCropedImage();
        }
    }
}
