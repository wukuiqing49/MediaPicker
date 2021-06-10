package com.wu.media.ui.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListPopupWindow;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.wkq.base.frame.activity.MvpBindingActivity;
import com.wu.media.PickerConfig;
import com.wu.media.R;
import com.wu.media.databinding.ActivityMediaBinding;
import com.wu.media.media.DataCallback;
import com.wu.media.media.entity.Folder;
import com.wu.media.media.entity.Media;
import com.wu.media.media.loader.ImageLoader;
import com.wu.media.media.loader.LoaderM;
import com.wu.media.media.loader.MediaLoader;
import com.wu.media.media.loader.VideoLoader;
import com.wu.media.model.ImagePickerCropParams;
import com.wu.media.model.ImagePickerOptions;
import com.wu.media.presenter.MediaPresenter;
import com.wu.media.ui.adapter.FolderAdapter;
import com.wu.media.ui.fragment.MediaPickerFragment;
import com.wu.media.ui.fragment.MediaPreviewFragment;
import com.wu.media.utils.ThumbnailsUtils;
import com.wu.media.utils.observable.MediaAddObservable;
import com.wu.media.utils.observable.MediaOpenActivityObservable;
import com.wu.media.utils.observable.MediaOpenInfo;
import com.wu.media.utils.observable.MediaPageSelectStateObservable;
import com.wu.media.utils.observable.MediaSelectStateObservable;
import com.wu.media.utils.observable.MeidaResultObservable;
import com.wu.media.view.MediaView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static com.wu.media.PickerConfig.REQUEST_CODE_PERMISSION_CAMERA;
import static com.wu.media.PickerConfig.REQUEST_CODE_PERMISSION_CROP;
import static com.wu.media.PickerConfig.REQUEST_CODE_PERMISSION_READ;

/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/9/17 11:24
 * <p>
 * 名 字 : MediaActivity
 * <p>
 * 简 介 :
 */
public class MediaActivity extends MvpBindingActivity<MediaView, MediaPresenter,  ActivityMediaBinding> implements DataCallback, Observer, View.OnClickListener {

    /**
     * sdk23获取sd卡拍照权限的requestCode
     */
    //首页的tag
    public String MEDIA_HOME = "tag_media_home";
    public String MEDIA_PRE = "tag_media_pre";

    public int maxTime = PickerConfig.RECODE_MAX_TIME;

    //配置
    public ImagePickerOptions mOptions;
    //加载器
    public LoaderM loaderM;
    //最大选择个数
    public int maxSelect;
    //是否展示相机
    public boolean showCamera;
    //是否返回视频地址
    public boolean isFrendCircle;
    //临时文件
    public File tempFile;
    //拍照存储路径
    // 结果的code
    public int resultCode;
    //最大
    public int maxNum = 9;
    //视频的最大 的大小
    public long maxVideoSize = Integer.MAX_VALUE;
    //图片的最大大小
    public long maxImageSize = Integer.MAX_VALUE;
    //是否展示相机
    public boolean needCamera = false;
    //选择模式
    public int selectMode = PickerConfig.PICKER_IMAGE_VIDEO;
    //选中的集合
    public ArrayList<Media> selects = new ArrayList<>();
    //缓存地址
    public String cachePath;
    //剪辑视频存储路径
    public String videoTrimPath;
    //裁剪的数据
    public ImagePickerCropParams cropParams;
    //是否需要裁剪
    public boolean needCrop = false;
    //单选
    public boolean singlePick = false;
    //展示时长
    public boolean showTime = false;
    public int maxVideoTime = PickerConfig.DEFAULT_MAX_TIME;
    //是否选择Gif
    public boolean selectGift = true;
    //处理返回uri地址
    public boolean isReturnUri = false;
    //获取Media的数据
    //展示点击预览文字
    public boolean isPre;
    public List<Folder> mediasList = new ArrayList<>();
    public List<Media> allMedias = new ArrayList<>();

    public FolderAdapter mFolderAdapter = null;
    public ListPopupWindow mFolderPopupWindow = null;

    //权限申请弹窗
    public Dialog dialog;
    //是否没有申请权限
    public boolean isNeverAsk;
    //录制权限
    public static String[] permissionsRecord = {
            Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    // 存读取权限
    public static String[] permissionsRead = {
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private Media cropPath;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_media;
    }
    long time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化携带数据

        if (getPresenter() != null) getPresenter().initIntentData(this);
        if (getMvpView() != null) getMvpView().initView();
        getSupportFragmentManager().beginTransaction().add(R.id.frame, MediaPickerFragment.newInstanse(), MEDIA_HOME).addToBackStack("").commit();
         time= System.currentTimeMillis();
        getMediaData();
    }

    void getMediaData() {
        boolean hasPermissions = getPresenter()
                .checkPermissions(this, permissionsRead, REQUEST_CODE_PERMISSION_READ, getResources().getString(R.string.dialog_imagepicker_permission_sdcard_nerver_ask_message), true);

        if (hasPermissions) {
            if (needCrop && singlePick) {
//                ThumbnailsUtils.getThumbnails(this);
                selectMode = PickerConfig.PICKER_IMAGE;
            }
            int type = selectMode;
            if (type == PickerConfig.PICKER_IMAGE_VIDEO) {
                loaderM = MediaLoader.getInstance(this, this);
                getLoaderManager().initLoader(type, null, (MediaLoader) loaderM);
            } else if (type == PickerConfig.PICKER_ONLY_ONE_TYPE) {
                loaderM = MediaLoader.getInstance(this, this);
                getLoaderManager().initLoader(type, null, (MediaLoader) loaderM);
            } else if (type == PickerConfig.PICKER_IMAGE) {
                loaderM = ImageLoader.getInstance(this, this);
                getLoaderManager().initLoader(type, null, (ImageLoader) loaderM);
            } else if (type == PickerConfig.PICKER_VIDEO) {
                loaderM = VideoLoader.getInstance(this, this);
                getLoaderManager().initLoader(type, null, (VideoLoader) loaderM);
            }
        }
    }


    public void addPrePreviewFragment(Media media, boolean isShow) {
        isPre = isShow;
        if (getMvpView() != null) getMvpView().showPrew(media);
        if (getMvpView() != null) getMvpView().showPrewBottom(media);
        getSupportFragmentManager().beginTransaction().add(R.id.frame, MediaPreviewFragment.newInstanse(media, isShow), MEDIA_PRE).addToBackStack("isPre").commit();
    }


    @Override
    public void onData(ArrayList<Folder> list) {
        mediasList = list;
        if (list == null || list.size() == 0) return;
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame);
        if (currentFragment instanceof MediaPickerFragment) {
            ((MediaPickerFragment) currentFragment).setMediaData(list);
            Log.e("时间:", System.currentTimeMillis()-time+"");
            if (mFolderAdapter != null) mFolderAdapter.updateAdapter(list);
        }

        ThumbnailsUtils.getThumbnails(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaAddObservable.getInstance().deleteObserver(this);
        MeidaResultObservable.getInstance().deleteObserver(this);
        MediaSelectStateObservable.getInstance().deleteObserver(this);
        MediaPageSelectStateObservable.getInstance().deleteObserver(this);
        MediaOpenActivityObservable.getInstance().deleteObserver(this);
        if (mediasList != null) {
            mediasList.clear();
            mediasList = null;
        }
        if (allMedias != null) {
            allMedias.clear();
            allMedias = null;
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof MediaAddObservable) {
            Media media = (Media) arg;
            //拍照后添加的逻辑
            if (media != null) {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame);
                if (currentFragment instanceof MediaPickerFragment) {
                    MediaPickerFragment mediaPickerFragment = (MediaPickerFragment) currentFragment;
                    mediaPickerFragment.addMedia(media);
                    getMvpView().setSingleMode(media);
                    getMvpView().processFolderSize(media);
                    //拍摄后默认选中
                    mediaPickerFragment.getMvpView().processSelectMedias(this, false, media, mOptions.isNeedCamera() ? 1 : 0);
                }
            }
        } else if (o instanceof MeidaResultObservable) {
            //处理 回调
            Media media = (Media) arg;
            if (media != null) {
                selects.clear();
                selects.add(media);
                finishMedia();
            }
        } else if (o instanceof MediaSelectStateObservable) {
            Media media = (Media) arg;
            if (media != null) {
                if (getMvpView() != null) getMvpView().progressSelectMedia(media);
            }
        } else if (o instanceof MediaPageSelectStateObservable) {
            Media media = (Media) arg;
            if (media != null) {
                if (getMvpView() != null) getMvpView().showPrew(media);
            }
        } else if (o instanceof MediaOpenActivityObservable) {
            MediaOpenInfo info = (MediaOpenInfo) arg;
            if (info != null) {
                if (info.getType() == 1) {
                    //录制页面
                    getMvpView().openRecord();
                } else if (info.getType() == 2) {
                    //裁剪
                    cropPath = info.getMedia();
                    getMvpView().openCrop(cropPath);

                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.btn_back) {
            onBackPressed();
        } else if (viewId == R.id.done) {
            finishMedia();

        } else if (viewId == R.id.category_btn) {
            if (getMvpView().hasPermission())
                if (mFolderPopupWindow.isShowing()) {
                    mFolderPopupWindow.dismiss();
                } else {
                    mFolderPopupWindow.show();
                }
        } else if (viewId == R.id.preview) {
            if (selects.size() <= 0) {
                if (getMvpView() != null)
                    getMvpView().showMessage(getResources().getString(R.string.select_null));
            } else {
                addPrePreviewFragment(selects.get(0), true);
            }
        } else if (viewId == R.id.check_layout_top) {
            getMvpView().processCheckMeida();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean[] hasPermissions = getPresenter().onRequestPermissionsResult(this, requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSION_CAMERA) {
            if (hasPermissions[0]) {
                if (getMvpView() != null) { //权限正常
                    getMvpView().openRecord();
                }
            } else if (hasPermissions[1]) { //权限永久拒绝的处理
                getMvpView().showPermissionPerpetual(requestCode, getResources().getString(R.string.dialog_imagepicker_permission_camera_nerver_ask_message), false);
            } else { //权限没有全部同意
                getMvpView().showPermission(permissionsRecord, REQUEST_CODE_PERMISSION_CAMERA, this.getResources().getString(R.string.dialog_imagepicker_permission_camera_nerver_ask_message), false);
            }
        } else if (requestCode == REQUEST_CODE_PERMISSION_READ) {
            if (hasPermissions[0]) {
                if (getMvpView() != null) { //权限正常
                    getMediaData();
                }
            } else if (hasPermissions[1]) { //权限永久拒绝的处理
                getMvpView().showPermissionPerpetual(requestCode, getResources().getString(R.string.dialog_imagepicker_permission_sdcard_nerver_ask_message), true);
            } else { //权限没有全部同意
                getMvpView().showPermission(permissionsRecord, REQUEST_CODE_PERMISSION_READ, this.getResources().getString(R.string.dialog_imagepicker_permission_sdcard_nerver_ask_message), true);
            }
        } else if (requestCode == REQUEST_CODE_PERMISSION_CROP) {
            if (hasPermissions[0]) {
                if (getMvpView() != null) { //权限正常
                    getMvpView().openCrop(cropPath);
                }
            } else if (hasPermissions[1]) { //权限永久拒绝的处理
                getMvpView().showPermissionPerpetual(requestCode, getResources().getString(R.string.dialog_imagepicker_permission_sdcard_nerver_ask_message), false);
            } else { //权限没有全部同意
                getMvpView().showPermission(permissionsRecord, REQUEST_CODE_PERMISSION_READ, this.getResources().getString(R.string.dialog_imagepicker_permission_sdcard_nerver_ask_message), false);
            }
        }

    }

    /**
     * 完成
     */
    public void finishMedia() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(PickerConfig.EXTRA_RESULT, selects);
        setResult(resultCode, intent);
        finish();
    }

    //返回
    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame);
        if (currentFragment == null) super.onBackPressed();
        if (currentFragment instanceof MediaPickerFragment) {
            finish();
        } else if (currentFragment instanceof MediaPreviewFragment) {
            if (getMvpView() != null) getMvpView().processShowBack();
            getSupportFragmentManager().popBackStack();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }
}
