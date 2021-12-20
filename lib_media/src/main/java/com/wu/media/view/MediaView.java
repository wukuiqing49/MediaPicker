package com.wu.media.view;

import android.Manifest;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wkq.base.frame.mosby.delegate.MvpView;
import com.wkq.base.utils.AlertUtil;
import com.wkq.base.utils.StatusBarUtil2;
import com.wu.media.PickerConfig;
import com.wu.media.R;
import com.wu.media.camera.ui.DiyCameraActivity;
import com.wu.media.media.entity.Folder;
import com.wu.media.media.entity.Media;
import com.wu.media.ui.activity.CustomCameraActivity;
import com.wu.media.ui.activity.ImageCropActivity;
import com.wu.media.ui.activity.MediaActivity;
import com.wu.media.ui.adapter.FolderAdapter;
import com.wu.media.ui.adapter.PreviewBottomMediaAdapter;
import com.wu.media.ui.fragment.MediaPickerFragment;
import com.wu.media.ui.fragment.MediaPreviewFragment;
import com.wu.media.utils.AlertDialogUtils;
import com.wu.media.utils.AndroidQUtil;
import com.wu.media.utils.FileTypeUtil;
import com.wu.media.utils.FileUtils;
import com.wu.media.utils.MediaStringUtils;
import com.wu.media.utils.PermissionChecker;
import com.wu.media.utils.ScreenUtils;
import com.wu.media.utils.observable.MediaAddObservable;
import com.wu.media.utils.observable.MediaOpenActivityObservable;
import com.wu.media.utils.observable.MediaPageSelectStateObservable;
import com.wu.media.utils.observable.MediaSelectStateObservable;
import com.wu.media.utils.observable.MeidaResultObservable;
import com.wu.media.utils.observable.SelectStateUpdateObservable;

import java.util.ArrayList;

import static com.wu.media.PickerConfig.REQUEST_CODE_PERMISSION_CAMERA;
import static com.wu.media.PickerConfig.REQUEST_CODE_PERMISSION_READ;


/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/9/15 11:29
 * <p>
 * 名 字 : MediaPickerView
 * <p>
 * 简 介 :
 */
public class MediaView implements MvpView {
    MediaActivity mActivity;
    Button bDone;
    Button preview;
    TextView barTitle;
    public LinearLayout checkLayout;
    public ImageView checkImage;
    public RecyclerView bottomRv;
    private PreviewBottomMediaAdapter previewBottomAdapter;

    public MediaView(MediaActivity mActivity) {
        this.mActivity = mActivity;
    }

    public void initView() {

        mActivity.binding.top.findViewById(R.id.btn_back).setOnClickListener(mActivity);
        bDone = mActivity.binding.top.findViewById(R.id.done);
        preview = mActivity.binding.footer.findViewById(R.id.preview);
        barTitle = mActivity.binding.top.findViewById(R.id.bar_title);
        checkLayout = mActivity.binding.top.findViewById(R.id.check_layout_top);
        checkImage = mActivity.binding.top.findViewById(R.id.check_image_top);
        bottomRv = mActivity.binding.bottom.findViewById(R.id.bottom_recycler_view);
        checkLayout.setOnClickListener(mActivity);
        bDone.setOnClickListener(mActivity);

        initState();
        setSelectText(null);
        setTitleBar();
        initFolder();
        if (mActivity.needCrop || mActivity.singlePick || mActivity.maxNum == 1) {
            preview.setVisibility(View.GONE);
        } else {
            preview.setVisibility(View.VISIBLE);
        }
    }

    private void initState() {
        StatusBarUtil2.setColor(mActivity, mActivity.getResources().getColor(R.color.status_bar_color), 0);
        MediaAddObservable.getInstance().addObserver(mActivity);
        MeidaResultObservable.getInstance().addObserver(mActivity);
        MediaSelectStateObservable.getInstance().addObserver(mActivity);
        MediaPageSelectStateObservable.getInstance().addObserver(mActivity);
        MediaOpenActivityObservable.getInstance().addObserver(mActivity);
        //处理fragment 回调
        mActivity.getOnBackPressedDispatcher().addCallback(mActivity, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Fragment currentFragment = mActivity.getSupportFragmentManager().findFragmentById(R.id.frame);
                if (currentFragment instanceof MediaPickerFragment) {
                    mActivity.finish();
                } else {
                    mActivity.getSupportFragmentManager().popBackStack();
                }
            }
        });
    }

    /**
     * 扎实顶部文字
     */
    private void setTitleBar() {
        int type = mActivity.selectMode;

        if (type == PickerConfig.PICKER_IMAGE_VIDEO) {
            barTitle.setText(mActivity.getResources().getString(R.string.select_title));
            ((Button) mActivity.binding.footer.findViewById(R.id.category_btn)).setText(mActivity.getResources().getString(R.string.select_title));
        } else if (type == PickerConfig.PICKER_ONLY_ONE_TYPE) {
            ((Button) mActivity.binding.footer.findViewById(R.id.category_btn)).setText(mActivity.getResources().getString(R.string.select_title));
            barTitle.setText(mActivity.getResources().getString(R.string.select_title));
        } else if (type == PickerConfig.PICKER_IMAGE) {
            barTitle.setText(mActivity.getResources().getString(R.string.select_image_title));
            ((Button) mActivity.binding.footer.findViewById(R.id.category_btn)).setText(mActivity.getResources().getString(R.string.select_image_title));
        } else if (type == PickerConfig.PICKER_VIDEO) {
            barTitle.setText(mActivity.getResources().getString(R.string.select_video_title));
            ((Button) mActivity.binding.footer.findViewById(R.id.category_btn)).setText(mActivity.getResources().getString(R.string.select_video_title));
        }


    }

    /**
     * 初始化文件夹
     */
    private void initFolder() {
        ArrayList<Folder> folders = new ArrayList<>();
        mActivity.binding.footer.findViewById(R.id.category_btn).setOnClickListener(mActivity);
        mActivity.mFolderAdapter = new FolderAdapter(folders, mActivity);
        mActivity.mFolderPopupWindow = new ListPopupWindow(mActivity);
        mActivity.mFolderPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        mActivity.mFolderPopupWindow.setAdapter(mActivity.mFolderAdapter);
        mActivity.mFolderPopupWindow.setHeight((int) (ScreenUtils.getScreenHeight(mActivity) * 0.6));
        mActivity.mFolderPopupWindow.setAnchorView(mActivity.binding.footer);
        mActivity.mFolderPopupWindow.setModal(true);
        mActivity.mFolderPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mActivity.mFolderAdapter.setSelectIndex(position);
                ((TextView) mActivity.binding.footer.findViewById(R.id.category_btn)).setText(mActivity.mFolderAdapter.getItem(position).name);
                Fragment currentFragment = mActivity.getSupportFragmentManager().findFragmentById(R.id.frame);
                if (currentFragment instanceof MediaPickerFragment) {
                    MediaPickerFragment mediaPickerFragment = (MediaPickerFragment) currentFragment;
                    mediaPickerFragment.mMediaAdapter.updateItems(mActivity.mFolderAdapter.getSelectMedias());
                }
                mActivity.mFolderPopupWindow.dismiss();
                mActivity.allMedias.clear();
                mActivity.allMedias.addAll(mActivity.mFolderAdapter.getSelectMedias());

            }
        });
    }

    public boolean hasPermission() {
        return PermissionChecker.checkPermissions(mActivity
                , new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                , REQUEST_CODE_PERMISSION_CAMERA, R.string.dialog_imagepicker_permission_sdcard_message);
    }


    public void showMessage(String message) {
        if (mActivity == null || mActivity.isFinishing() || TextUtils.isEmpty(message))
            return;
        AlertUtil.showDeftToast(mActivity, message);
    }

    /**
     * 设置单选模式的拍照类型
     */
    public void setSingleMode(Media media) {
        //设置单选模式
        if (mActivity.mOptions.getSelectMode() == PickerConfig.PICKER_ONLY_ONE_TYPE) {
            if (media.mediaType == 1) {
                mActivity.mOptions.setJumpCameraMode(PickerConfig.CAMERA_MODE_PIC);
            } else if (media.mediaType == 3) {
                mActivity.mOptions.setJumpCameraMode(PickerConfig.CAMERA_MODE_VIDEO);
            }
        }
    }

    /**
     * 处理选中的数据
     *
     * @param media
     */
    public void progressSelectMedia(Media media) {
        if (media == null) return;
        setSingleMode(media);
        if (isCheckMediaSupport(media)) {
            if (media.isSelect() && mActivity.selects.contains(media)) {
                media.setSelect(!media.isSelect());
                mActivity.selects.remove(media);
            } else {
                if (!mActivity.selects.contains(media)) {
                    media.setSelect(!media.isSelect());
                    mActivity.selects.add(media);
                }
            }
            setSelectText(media);
        }
    }


    /**
     * 展示底部预览
     *
     * @param mMedia
     */
    public void showPrewBottom(Media mMedia) {
        mActivity.binding.footer.setVisibility(View.GONE);
        bottomRv.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        if (mActivity.selects.indexOf(mMedia) < 0) {
            previewBottomAdapter = new PreviewBottomMediaAdapter(mActivity, 0, mActivity.isPre);
        } else {
            previewBottomAdapter = new PreviewBottomMediaAdapter(mActivity, mActivity.selects.indexOf(mMedia),true);
        }
        bottomRv.setAdapter(previewBottomAdapter);
        if (mActivity.selects != null && mActivity.selects.size() > 0) {
            bottomRv.setItemAnimator(null);
            if (mActivity.selects.size() > 0) {
                mActivity.binding.bottom.setVisibility(View.VISIBLE);
                previewBottomAdapter.addItems(mActivity.selects);
            } else {
                mActivity.binding.bottom.setVisibility(View.GONE);
            }
        }

        previewBottomAdapter.setOnItemClickListener(new PreviewBottomMediaAdapter.OnBottomItemClickListener() {
            @Override
            public void onItemClick(int position, Media media) {
                Fragment currentFragment = mActivity.getSupportFragmentManager().findFragmentById(R.id.frame);
                if (currentFragment instanceof MediaPreviewFragment) {
                    ((MediaPreviewFragment) currentFragment).showMediaPosition(media);
                    previewBottomAdapter.updateBgState(position);
                }
            }
        });
    }

    /**
     * 选中文字
     *
     * @param media
     */
    public void setSelectText(Media media) {
        if (mActivity.selects.size() > 0) {
            String finishText = String.format(mActivity.getResources().getString(R.string.btn_imagepicker_ok), mActivity.selects.size(), mActivity.maxNum);
            bDone.setText(finishText);
            bDone.setVisibility(View.VISIBLE);
        } else {
            bDone.setText("");
            bDone.setVisibility(View.GONE);
        }
        setBottomPreSize(mActivity.selects.size());
    }

    public void setBottomPreSize(int size) {
        preview.setOnClickListener(mActivity);
        if (mActivity.needCrop) {
            preview.setVisibility(View.GONE);
        } else {
            preview.setVisibility(View.VISIBLE);
            if (size <= 0) {
                preview.setText("预览");
            } else {
                String preText = String.format(mActivity.getResources().getString(R.string.btn_imagepicker_prev), size);
                preview.setText(preText);
            }
        }


    }

    public void processShowBack() {
        checkLayout.setVisibility(View.GONE);
        mActivity.binding.bottom.setVisibility(View.GONE);
        mActivity.binding.footer.setVisibility(View.VISIBLE);
        setTitleBar();
    }


    //判断 文件是否支持外部配置   1.文件从存在不存在 2.文件大小 3.文件格式 4. 特殊需求 5.是否支持gif 6.文件大小  7.单选 选择数量
    public boolean isCheckMediaSupport(Media media) {

        if (mActivity.selects.size() >= mActivity.maxNum && !media.isSelect()) {
            showMessage(mActivity.getResources().getString(R.string.msg_amount_limit));
            return false;
        }
        //文件是否存在
        //文件是否存在
        if (media.size <= 0) {
            showMessage(mActivity.getResources().getString(R.string.string_file_err));
            return false;
        }
        //文件大小判断  1 图片  3 视频
        if (media.mediaType == 1 && mActivity.maxImageSize < media.size) {
            String size = FileUtils.fileSize(mActivity.maxImageSize);
            showMessage(mActivity.getResources().getString(R.string.media_msg_size_limit) + size);
            return false;
        }
        if (media.mediaType == 3 && mActivity.maxVideoSize < media.size) {
            String size = FileUtils.fileSize(mActivity.maxVideoSize);
            showMessage(mActivity.getResources().getString(R.string.media_msg_size_limit) + size);
            return false;
        }
        //文件格式
        if (media.mediaType == 3 && FileTypeUtil.checkVideoType(media.path)) {
            showMessage(mActivity.getResources().getString(R.string.media_msg_video_limit));
            return false;
        }
        //视频时长限制
        if (media.mediaType == 3 && media.duration > mActivity.maxVideoTime) {
            showMessage(mActivity.getResources().getString(R.string.media_msg_time_limit) + (MediaStringUtils.gennerMinSec(mActivity.maxVideoTime / 1000)));
            return false;
        }
        //判断是否支持gif
        if (!mActivity.selectGift && FileTypeUtil.isGif(media.path)) {
            showMessage(mActivity.getResources().getString(R.string.msg_gif_limit));
            return false;
        }

        if (mActivity.selectMode == PickerConfig.PICKER_ONLY_ONE_TYPE && mActivity.selects != null && mActivity.selects.size() > 0 && mActivity.selects.get(0).mediaType != media.mediaType) {
            showMessage(mActivity.getResources().getString(R.string.msg_type_limit));
            return false;
        }
        return true;
    }

    /**
     * 处理选中和取消选中状态的逻辑
     */
    public void processCheckMeida() {
        Fragment currentFragment = mActivity.getSupportFragmentManager().findFragmentById(R.id.frame);
        if (currentFragment instanceof MediaPreviewFragment) {
            MediaPreviewFragment curF = (MediaPreviewFragment) currentFragment;
            Media curM = curF.getCurrentMedia();
            processCheckMeidaState(curM);
            progressCheckSelectMedia(curF, curM);
            SelectStateUpdateObservable.getInstance().stateUpdate(curM);
            if (mActivity.selects.size() > 0) {
                mActivity.binding.bottom.setVisibility(View.VISIBLE);
            } else {
                mActivity.binding.bottom.setVisibility(View.GONE);
            }
            if (previewBottomAdapter != null) previewBottomAdapter.notifyDataSetChanged();
        }


    }

    /**
     * 处理预览页面选中后数据的更新问题
     *
     * @param curF
     * @param curM
     */
    private void progressCheckSelectMedia(MediaPreviewFragment curF, Media curM) {

        if (curM == null || !isCheckMediaSupport(curM)) return;
        boolean isContains = mActivity.selects.contains(curM);
        if (curM.isSelect() && isContains) {
            int index = mActivity.selects.indexOf(curM);
            mActivity.selects.remove(curM);
            if (previewBottomAdapter != null && index >= 0) {
                previewBottomAdapter.removeItem(index);
                previewBottomAdapter.notifyItemRemoved(index);
            }
            curM.setSelect(!curM.isSelect());
        } else {
            if (!isContains) {
                curM.setSelect(!curM.isSelect());
                mActivity.selects.add(curM);
                if (previewBottomAdapter != null) previewBottomAdapter.addItem(curM);
            }
        }
        setSelectText(curM);
        curF.setMediaSelect(curM.isSelect());
    }


    public void processCheckMeidaState(Media media) {
        if (isCheckMediaSupport(media)) {
            if (!media.isSelect()) {
                checkImage.setImageResource(R.drawable.iv_media_checked);
            } else {
                checkImage.setImageResource(R.drawable.xc_weixuan);
            }
        }

    }

//    /**
//     * 预览页面滑动
//     *
//     * @param media
//     */
//    public void showSelectPrew(Media media) {
//        if (mActivity.isPre) {
//            String barTitleText = String.format(mActivity.getResources().getString(R.string.btn_bar_title), mActivity.selects.indexOf(media) + 1, mActivity.selects.size());
//            barTitle.setText(barTitleText);
//            checkLayout.setVisibility(View.GONE);
//        } else {
//            String barTitleText = String.format(mActivity.getResources().getString(R.string.btn_bar_title), mActivity.allMedias.indexOf(media) + 1, mActivity.allMedias.size());
//            barTitle.setText(barTitleText);
//            checkLayout.setVisibility(View.VISIBLE);
//        }
//
//        if (media.isSelect()) {
//            checkImage.setImageResource(R.drawable.xc_xuanzhong);
//        } else {
//            checkImage.setImageResource(R.drawable.xc_weixuan);
//        }
//
//        if (previewBottomAdapter != null) {
//            previewBottomAdapter.updateBgState(mActivity.selects.indexOf(media));
//        }
//    }

    /**
     * 展示选中状态
     *
     * @param media
     */
    public void showPrew(Media media) {
        if (mActivity.isPre) {
            String barTitleText = String.format(mActivity.getResources().getString(R.string.btn_bar_title), mActivity.selects.indexOf(media) + 1, mActivity.selects.size());
            barTitle.setText(barTitleText);
            checkLayout.setVisibility(View.GONE);
        } else {
            if (mActivity.allMedias.get(0).mediaType==0) {
                String barTitleText = String.format(mActivity.getResources().getString(R.string.btn_bar_title), mActivity.allMedias.indexOf(media), mActivity.allMedias.size() - 1);
                barTitle.setText(barTitleText);
                checkLayout.setVisibility(View.VISIBLE);
            } else {
                String barTitleText = String.format(mActivity.getResources().getString(R.string.btn_bar_title), mActivity.allMedias.indexOf(media) + 1, mActivity.allMedias.size());
                barTitle.setText(barTitleText);
                checkLayout.setVisibility(View.VISIBLE);
            }

        }

        if (media.isSelect()) {
            checkImage.setImageResource(R.drawable.iv_media_checked);
        } else {
            checkImage.setImageResource(R.drawable.xc_weixuan);
        }
        if (previewBottomAdapter != null) {
            previewBottomAdapter.updateBgState(mActivity.selects.indexOf(media));
        }
    }

    //打开 录制页面
    public void openRecord() {
        boolean hasPermissions = mActivity.getPresenter()
                .checkPermissions(mActivity, mActivity.permissionsRecord, REQUEST_CODE_PERMISSION_CAMERA, mActivity.getResources().getString(R.string.dialog_imagepicker_permission_camera_nerver_ask_message), false);
        if (hasPermissions) {
            CustomCameraActivity.newInstance(mActivity, mActivity.mOptions);
        }
    }

    //打开 裁剪页面
    public void openCrop(Media media) {
        boolean hasPermissions = mActivity.getPresenter()
                .checkPermissions(mActivity, mActivity.permissionsRead, REQUEST_CODE_PERMISSION_READ, mActivity.getResources().getString(R.string.dialog_imagepicker_permission_sdcard_nerver_ask_message), false);
        if (hasPermissions) {
            if (AndroidQUtil.isAndroidQ()) {
                //获取源数据的照片信息
                ExifInterface oldExif = AndroidQUtil.getExifInterface(mActivity, Uri.parse(media.fileUri));
                String filePath = AndroidQUtil.saveSignImageBox(mActivity, "tempCrop.png", AndroidQUtil.getBitmapFromUri(mActivity, Uri.parse(media.fileUri)));
                if (null != oldExif && !TextUtils.isEmpty(filePath)) {
                    try {
                        //将源数据的exif信息复制一份给新的
                        AndroidQUtil.saveExif(oldExif, filePath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                ImageCropActivity.start(mActivity, filePath, mActivity.mOptions);
            } else {
                ImageCropActivity.start(mActivity, media.path, mActivity.mOptions);
            }


        }


    }

    /**
     * 弹出拒绝后的弹框
     *
     * @param needList    权限列表
     * @param requestCode 请求吗
     * @param message     展示信息
     * @param isFinish    是否需要关闭页面
     */
    public void showPermission(String[] needList, int requestCode, String message, boolean isFinish) {
        if (mActivity.dialog != null) mActivity.dialog.dismiss();
        mActivity.dialog = AlertDialogUtils.showTwoButtonDialog(
                mActivity,
                mActivity.getString(R.string.dialog_imagepicker_permission_nerver_ask_cancel),
                mActivity.getString(R.string.dialog_imagepicker_permission_confirm),
                message,
                R.color.color_dialog_btn, R.color.color_ffa300, new AlertDialogUtils.DialogTwoListener() {
                    @Override
                    public void onClickLeft(Dialog dialog) {
                        dialog.dismiss();
                        showMessage(message);
                        if (isFinish) mActivity.finish();
                    }

                    @Override
                    public void onClickRight(Dialog dialog) {
                        dialog.dismiss();
                        ActivityCompat.requestPermissions(mActivity, needList, requestCode);
                    }
                });
    }

    /**
     * 权限拒绝后展示
     *
     * @param requestCode 请求吗
     * @param message
     * @param isFinish    是否销毁页面
     */
    public void showPermissionPerpetual(int requestCode, String message, boolean isFinish) {

        if (mActivity.dialog != null) mActivity.dialog.dismiss();
        mActivity.dialog = AlertDialogUtils.showTwoButtonDialog(
                mActivity,
                mActivity.getString(R.string.dialog_imagepicker_permission_nerver_ask_cancel),
                mActivity.getString(R.string.dialog_imagepicker_permission_confirm),
                message,
                R.color.color_dialog_btn, R.color.color_ffa300, new AlertDialogUtils.DialogTwoListener() {
                    @Override
                    public void onClickLeft(Dialog dialog) {
                        dialog.dismiss();
                        showMessage(message);
                        if (isFinish)
                            mActivity.finish();
                    }

                    @Override
                    public void onClickRight(Dialog dialog) {
                        dialog.dismiss();
                        PermissionChecker.settingPermissionActivity(mActivity, requestCode);
                        mActivity.isNeverAsk = true;
                    }
                });
    }

    public void processFolderSize(Media media) {
        if (media == null || mActivity.mediasList == null || mActivity.mediasList.size() == 0) {
            return;
        }
        mActivity.mediasList.get(0).getMedias().add(0, media);
    }
}
