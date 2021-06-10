package com.wu.media.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;


import com.wkq.base.frame.fragment.MvpBindingFragment;
import com.wkq.base.utils.DoublePressed;
import com.wu.media.R;
import com.wu.media.databinding.FragmentRecordPreviewBinding;
import com.wu.media.presenter.RecordPreviewPresenter;
import com.wu.media.ui.activity.RecordActivity;
import com.wu.media.ui.activity.VideoPlayerActivity;
import com.wu.media.view.RecordPreviewView;

import java.io.File;

/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/9/28 9:34
 * <p>
 * 名 字 : RecordPreviewFragment
 * <p>
 * 简 介 :
 */
public class RecordPreviewFragment extends MvpBindingFragment<RecordPreviewView, RecordPreviewPresenter, FragmentRecordPreviewBinding> implements View.OnClickListener {

    //类型 0 是图片  1 是视频
    public int type;
    //路径
    public String path;
    // 处理 返回事件的监听
    public OnBackPressedCallback callback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            callback.setEnabled(false);
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        }
    };
    private RecordActivity mActivity;


    public static RecordPreviewFragment newInstance(int type, String path) {

        Bundle args = new Bundle();

        RecordPreviewFragment fragment = new RecordPreviewFragment();
        args.putInt("type", type);
        args.putString("path", path);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_record_preview;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = (int) getArguments().get("type");
        path = (String) getArguments().get("path");
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity = (RecordActivity) getActivity();
        binding.setOnClick(this);
        if (getMvpView() != null) getMvpView().initView(mActivity);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (type == 1 && getMvpView() != null) {
            getMvpView().initPre();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (callback != null) callback.remove();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.play_control_iv) {
            try {
                Uri uri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".fileprovider", new File(path));
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setDataAndType(uri, "video/*");
//                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                if (getActivity().getPackageManager().queryIntentActivities(intent, 0).isEmpty()) {
//                    getMvpView().showMessage("未检测到播放器");
//                } else {
//                    startActivity(intent);
//                }
                VideoPlayerActivity.newInstance(getActivity(),path);

            } catch (Exception e) {
            }

        } else if (v.getId() == R.id.tb_cancel) {
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        } else if (v.getId() == R.id.tb_ok) {
            if (getMvpView() == null || DoublePressed.onDoublePressed()) return;
            if (type == 0) {
                getMvpView().savePic(mActivity);
            } else if (type == 1) {
                getMvpView().saveVideo(mActivity);
            }


        }

    }
}
