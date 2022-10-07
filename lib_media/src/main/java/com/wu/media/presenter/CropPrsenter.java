package com.wu.media.presenter;

import android.os.Environment;
import android.text.TextUtils;

import com.wkq.base.frame.mosby.MvpBasePresenter;
import com.wu.media.R;
import com.wu.media.ui.activity.CropActivity;
import com.wu.media.ui.activity.ImageCropActivity;
import com.wu.media.utils.AndroidQUtil;
import com.wu.media.view.CropView;
import com.wu.media.view.ImageCropView;

import java.io.File;

/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/9/29 15:45
 * <p>
 * 名 字 : ImageCropPrsenter
 * <p>
 * 简 介 :
 */
public class CropPrsenter extends MvpBasePresenter<CropView> {

    public void initData(CropActivity mActibity) {
        if (mActibity.mOptions == null) {
            getView().showMessage(mActibity.getResources().getString(R.string.error_imagepicker_lack_params));
            mActibity.setResult(mActibity.RESULT_CANCELED);
            mActibity.finish();
            return;
        }
        if (TextUtils.isEmpty(mActibity.mOriginPath) || mActibity.mOriginPath.length() == 0) {
            getView().showMessage(mActibity.getResources().getString(R.string.imagepicker_crop_decode_fail));
            mActibity.setResult(mActibity.RESULT_CANCELED);
            mActibity.finish();
            return;
        }

        File file = new File(mActibity.mOriginPath);
        if (!file.exists()) {
            getView().showMessage(mActibity.getResources().getString(R.string.imagepicker_crop_decode_fail));
            mActibity.finish();
            return;
        }
        if (TextUtils.isEmpty(mActibity.mOptions.cachePath)){
            if (AndroidQUtil.isAndroidQ()) {
                mActibity.mOptions.cachePath = mActibity.getExternalFilesDir("").getPath() + File.separator + "MediaPickerPic";
            } else {
                mActibity.mOptions.cachePath = Environment.getExternalStorageDirectory().getPath() + File.separator + "MediaPickerPic";
            }
        }

        mActibity.cacheFile = new File(mActibity.mOptions.cachePath);
        if (!mActibity.cacheFile.exists())
            mActibity.cacheFile.mkdirs();

        mActibity.mCropParams = mActibity.mOptions.getCropParams();
        mActibity.binding.cropView.setFilePath(mActibity.mOriginPath);

//        mActibity.binding.cropView.load(mActibity.mOriginPath)
//                .setAspect(mActibity.mCropParams.getAspectX(), mActibity.mCropParams.getAspectY())
//                .setOutputSize(mActibity.mCropParams.getOutputX(), mActibity.mCropParams.getOutputY())
//                .start(mActibity);



    }
}
