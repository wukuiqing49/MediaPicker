package com.wu.media.camera.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.wkq.base.utils.StatusBarUtil2;
import com.wu.media.PickerConfig;
import com.wu.media.R;
import com.wu.media.camera.JCameraView;
import com.wu.media.camera.listener.JCameraListener;
import com.wu.media.media.entity.Media;
import com.wu.media.ui.widget.record.listener.ClickListener;
import com.wu.media.utils.AndroidQUtil;
import com.wu.media.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * 类介绍（必填）：
 *
 * @author Lynn
 */

public class DiyCameraActivity extends AppCompatActivity  {

    private final int GET_PERMISSION_REQUEST = 100; //权限申请自定义码
    private JCameraView jCameraView;
    private boolean granted = false;

    private String cachePath;
    private int resultCode;
    private int maxTime;
    private ArrayList<Media> oldlist;
    private int shootMode = 0;
    // private RecyclerView circleview;
    private String endurl = "";
    private int enddutain = 0;
    private ImageView cameraback;
    private String sendcircle;

    // private List<CircleData.ReleaseVideoTypeListBean> releaseVideoTypeListBeans;

    public static void start(Activity context, String path, int maxTime, int resultCode) {
        Intent starter = new Intent(context, DiyCameraActivity.class);
        starter.putExtra(PickerConfig.CACHE_PATH, path);
        starter.putExtra(PickerConfig.RESULT_CODE, resultCode);
        starter.putExtra(PickerConfig.MAX_TIME, maxTime);
        starter.putParcelableArrayListExtra(PickerConfig.OLD_LIST, new ArrayList<Media>());
        context.startActivityForResult(starter, 200);
    }

    public static void start(Activity context, String path, ArrayList<Media> list, int maxTime, int resultCode, int type, String sendcircle) {
        Intent starter = new Intent(context, DiyCameraActivity.class);
        starter.putExtra(PickerConfig.CACHE_PATH, path);
        starter.putExtra(PickerConfig.RESULT_CODE, resultCode);
        starter.putExtra(PickerConfig.MAX_TIME, maxTime);
        starter.putExtra(PickerConfig.CAMERA_SELECT_MODE, type);
        starter.putParcelableArrayListExtra(PickerConfig.OLD_LIST, list);
        starter.putExtra("sendcircle", sendcircle);
        context.startActivityForResult(starter, 200);
    }

    /**
     *   处理刘海屏全屏问题
     */
    public void processFullScreen() {

        WindowManager.LayoutParams lp= this.getWindow().getAttributes();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        this.getWindow().setAttributes(lp);
        // 设置页面全屏显示
        View decorView  = this.getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN| View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);   StatusBarUtil2.setTransparentForWindow(this);
        StatusBarUtil2.addTranslucentView(this, 0);
        processFullScreen();

        setContentView(R.layout.camera_intent);

        shootMode = getIntent().getIntExtra(PickerConfig.CAMERA_SELECT_MODE, 0);
        cachePath = getIntent().getStringExtra(PickerConfig.CACHE_PATH);
        resultCode = getIntent().getIntExtra(PickerConfig.RESULT_CODE, PickerConfig.DEFAULT_RESULT_CODE);
        maxTime = getIntent().getIntExtra(PickerConfig.MAX_TIME, -1) + 900;//录制时长增加900毫秒,解决部分手机只显示时间少一秒问题
        oldlist = getIntent().getParcelableArrayListExtra(PickerConfig.OLD_LIST);
        sendcircle = getIntent().getStringExtra("sendcircle");
        jCameraView = (JCameraView) findViewById(R.id.jcameraview);
        cameraback = findViewById(R.id.cameraback);
        cameraback.setVisibility(View.GONE);
        if (maxTime > 5_000) jCameraView.setDuration(maxTime);
        //设置视频保存路径
        jCameraView.setSaveVideoPath(cachePath);

        if (shootMode == JCameraView.BUTTON_STATE_ONLY_RECORDER) {
            jCameraView.setFeatures(JCameraView.BUTTON_STATE_ONLY_RECORDER);
        } else if (shootMode == JCameraView.BUTTON_STATE_ONLY_CAPTURE || oldlist.size() > 0) {
            jCameraView.setFeatures(JCameraView.BUTTON_STATE_ONLY_CAPTURE);
        } else {
            jCameraView.setFeatures(JCameraView.BUTTON_STATE_BOTH);
        }


        //JCameraView监听
        jCameraView.setJCameraLisenter(new JCameraListener() {
            @Override
            public void captureSuccess(Bitmap bitmap) {
                //获取图片bitmap
                Media media;
                Intent intent = new Intent();
                if (AndroidQUtil.isAndroidQ()) {
                    String name = System.currentTimeMillis() + ".png";
                    String uri= AndroidQUtil.saveBitmapToFile(DiyCameraActivity.this, bitmap, name, "拍照");
                    media = new Media("", name, 0, 1, bitmap.getByteCount(), 0, "",uri);
                } else {
                    String path = FileUtils.saveBitmap(DiyCameraActivity.this,cachePath, bitmap);
                    File file = new File(path);
                    media = new Media(file.getPath(), file.getName(), 0, 1, file.length(), 0, "","");
                }
                oldlist.add(media);
                intent.putParcelableArrayListExtra(PickerConfig.EXTRA_RESULT, oldlist);
                intent.putExtra("isCircleVideo", "nocircle");
                setResult(resultCode, intent);
                Log.i("JCameraView", "bitmap = " + bitmap.getWidth());
                finish();
            }

            @Override
            public void recordSuccess(String url, int duration) {

                Intent intent = new Intent();

                File file = new File(url);
                Media media;
                ArrayList<Media> medias = new ArrayList<>();
                if (AndroidQUtil.isAndroidQ()){
                    media = new Media(file.getPath(), file.getName(), duration, 3, file.length(), 0, "", duration ,"");
                }else {
                    media = new Media(file.getPath(), file.getName(), duration, 3, file.length(), 0, "", duration ,"");
                }

                medias.add(media);
                oldlist.add(media);
                intent.putParcelableArrayListExtra(PickerConfig.EXTRA_RESULT, oldlist);
                // intent.putExtra("isCircleVideo","nocircle");
                intent.putExtra("isCircleVideo", "cancircle");
                setResult(resultCode, intent);
                finish();
            }

            @Override
            public void recordEnd(String url, int duration) {
                endurl = url;
                enddutain = duration;
            }
        });
        cameraback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        jCameraView.setLeftClickListener(new ClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });

        jCameraView.setRightClickListener(new ClickListener() {
            @Override
            public void onClick() {
                Media media ;
                Intent intent = new Intent();
                if (!"".equals(endurl)) {
                    File file = new File(endurl);
                    ArrayList<Media> medias = new ArrayList<>();
                    if (AndroidQUtil.isAndroidQ()){
                        Uri fileUri=   AndroidQUtil.getImageContentUri(DiyCameraActivity.this,file);
                        String fileUriStr="";
                        if (fileUri!=null){
                            fileUriStr= fileUri.toString();
                        }else {
                            fileUriStr= "";
                        }
                        media = new Media(file.getPath(), file.getName(), enddutain, 3, file.length(), 0, "", enddutain,fileUriStr);
                    }else {
                        media = new Media(file.getPath(), file.getName(), enddutain, 3, file.length(), 0, "", enddutain,"");
                    }


                    medias.add(media);
                    oldlist.add(media);
                    //  CircleData.ReleaseVideoTypeListBean releaseVideoTypeListBean = releaseVideoTypeListBeans.get(0);
                    intent.putParcelableArrayListExtra(PickerConfig.EXTRA_RESULT, oldlist);
                    intent.putExtra("isCircleVideo", "cancircle");
                    setResult(resultCode, intent);
                    finish();
                }
            }
        });

        //6.0动态权限获取
        getPermissions();
        if (granted) {
            jCameraView.onResume();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //全屏显示
//        if (Build.VERSION.SDK_INT >= 19) {
//            View decorView = getWindow().getDecorView();
//            decorView.setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        } else {
//            View decorView = getWindow().getDecorView();
//            int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
//            decorView.setSystemUiVisibility(option);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        jCameraView.onStop();
        jCameraView.onDestroy();
        super.onDestroy();
    }

    /**
     * 获取权限
     */
    private void getPermissions() {

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA}, GET_PERMISSION_REQUEST);
        granted = false;
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GET_PERMISSION_REQUEST) {
            int size = 0;
            if (grantResults.length >= 1) {
                int writeResult = grantResults[0];
                //读写内存权限
                boolean writeGranted = writeResult == PackageManager.PERMISSION_GRANTED;//读写内存权限
                if (!writeGranted) {
                    size++;
                }
                //录音权限
                int recordPermissionResult = grantResults[1];
                boolean recordPermissionGranted = recordPermissionResult == PackageManager.PERMISSION_GRANTED;
                if (!recordPermissionGranted) {
                    size++;
                }
                //相机权限
                int cameraPermissionResult = grantResults[2];
                boolean cameraPermissionGranted = cameraPermissionResult == PackageManager.PERMISSION_GRANTED;
                if (!cameraPermissionGranted) {
                    size++;
                }
                if (size == 0) {
                    granted = true;
                    jCameraView.onResume();
                } else {
                    Toast.makeText(this, "请到设置-权限管理中开启", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

}
