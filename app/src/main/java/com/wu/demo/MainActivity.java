package com.wu.demo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.wu.demo.databinding.ActivityMainBinding;
import com.wu.media.ImagePicker;
import com.wu.media.PickerConfig;
import com.wu.media.media.entity.Media;
import com.wu.media.utils.AndroidQUtil;
import com.wu.media.utils.GlideCacheUtil;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMainBinding mainBinding;
    private ArrayList<Media> select;
    private String fileDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainBinding.setOnClick(this);


        boolean isAndroidQ = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
        File fileQ = getExternalFilesDir("");
        File file = Environment.getExternalStorageDirectory();
        String filePath = null;
        if (isAndroidQ) {
            if (fileQ != null) filePath = fileQ + "/strike/file/";
        } else {
            if (file != null) filePath = file + "/strike/file/";
        }
        fileDir = filePath == null ? "" : filePath;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {


            case R.id.tv_media:
                //打开相册
                new ImagePicker.Builder()
                        .maxNum(9)
                        .setSelectGif(true)
                        .maxImageSize(25 * 1024 * 1024)
                        .maxVideoSize(100 * 1024 * 1024)
                        .isReturnUri(AndroidQUtil.isAndroidQ())
                        .selectMode(PickerConfig.PICKER_IMAGE_VIDEO)
                        .defaultSelectList(new ArrayList<Media>())
                        .needCamera(true)
                        .builder()
                        .start(this, PickerConfig.PICKER_IMAGE, PickerConfig.DEFAULT_RESULT_CODE);
                break;

            case R.id.tv_camera:
                //打开相机
                new ImagePicker
                        .Builder()
                        .setJumpCameraMode(3)
                        .cachePath(fileDir)// 1 图片 3 视频
                        .doCrop(0,0,300,300)
                        .builder()
                        .startCamera(this, select, PickerConfig.PICKER_IMAGE, PickerConfig.DEFAULT_RESULT_CODE);
                break;

            case R.id.bt_preview:
                //打开预览
                new ImagePicker.Builder()
                        .builder()
                        .startPreview(this, 0, select, PickerConfig.PICKER_IMAGE, PickerConfig.DEFAULT_RESULT_CODE);
                break;
            case R.id.bt_qr:
                //打开预览
                ScanUtil.startScan(this, 10010, new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE).create());
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == PickerConfig.DEFAULT_RESULT_CODE) {
            select = data.getParcelableArrayListExtra(PickerConfig.EXTRA_RESULT);
            GlideCacheUtil.intoItemImage(this, select.get(0), mainBinding.ivPre, null);
        }

    }
}