package com.wu.media.view;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import com.wkq.base.frame.mosby.delegate.MvpView;
import com.wkq.base.utils.AlertUtil;
import com.wkq.base.utils.FileUtils;
import com.wu.media.R;
import com.wu.media.media.entity.Media;
import com.wu.media.ui.activity.ImageCropActivity;
import com.wu.media.utils.AndroidQUtil;
import com.wu.media.utils.observable.MeidaResultObservable;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/9/29 15:44
 * <p>
 * 名 字 : ImageCropView
 * <p>
 * 简 介 :
 */
public class ImageCropView implements MvpView {
    ImageCropActivity mActivity;
    private ProgressDialog mDialog;

    public ImageCropView(ImageCropActivity mActivity) {
        this.mActivity = mActivity;
    }

    public void initView() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        mActivity.binding.setOnClick(mActivity);
    }

    public void returnCropedImage() {

        showDialog();
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                Bitmap bitmap = mActivity.binding.cvCrop.getOutput();
                if (bitmap == null || bitmap.getWidth() == 0 || bitmap.getHeight() == 0 || bitmap.getByteCount() <= 0) {
                    emitter.onError(new Throwable("图片裁剪异常"));
                } else {
                    String filePath = AndroidQUtil.saveSignImageBox(mActivity, System.currentTimeMillis() + ".png", bitmap);
                    if (!TextUtils.isEmpty(filePath) && FileUtils.isFileExists(filePath)) {
                        emitter.onNext(filePath);
                    } else {
                        emitter.onError(new Throwable("图片裁剪异常"));
                    }
                }

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String path) {
                dismissDialog();
                processPath(path);
            }

            @Override
            public void onError(Throwable e) {
                dismissDialog();
                showMessage("文件格式不支持");
            }

            @Override
            public void onComplete() {

            }
        });
    }

    //
    private void processPath(String path) {
        File file = new File(path);
        if (file != null && file.exists() && file.canRead() && file.canWrite()) {
            Media media = new Media(path, file.getName(), System.currentTimeMillis(), 1, file.length(), (int) System.currentTimeMillis(), file.getParent(), Uri.fromFile(file).toString());
            media.setSelect(false);
            MeidaResultObservable.getInstance().finishMedia(true, media);
            mActivity.finish();
        }
    }

    public void showDialog() {
        if (mDialog == null) mDialog = new ProgressDialog(mActivity);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setMessage(mActivity.getResources().getString(R.string.imagepicker_crop_dialog));
        mDialog.show();
    }

    public void dismissDialog() {
        if (mDialog != null && mDialog.isShowing()) mDialog.dismiss();
    }

    public void showMessage(String message) {
        if (TextUtils.isEmpty(message) || mActivity.isFinishing()) return;
        AlertUtil.showDeftToast(mActivity, message);
    }

}
