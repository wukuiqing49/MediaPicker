package com.wu.media.camera.frame.presenter;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;

import com.wkq.base.frame.mosby.MvpBasePresenter;
import com.wu.media.camera.frame.view.CustomCameraCameraView;
import com.wu.media.ui.activity.CustomCameraActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wkq
 * @date 2021年06月24日 15:19
 * @des
 */

public class CustomCameraCameraPresenter extends MvpBasePresenter<CustomCameraCameraView> {

    public boolean checkPermissions(Activity activity, String[] permissions, int requestCode, boolean request) {
        //Android6.0以下默认有权限
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;

        List<String> needList = new ArrayList<>();
        boolean needShowRationale = false;

        for (String permisson : permissions) {
            if (TextUtils.isEmpty(permisson)) continue;
            if (ActivityCompat.checkSelfPermission(activity, permisson)
                    != PackageManager.PERMISSION_GRANTED) {
                needList.add(permisson);
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permisson))
                    needShowRationale = true;
            }
        }

        if (needList.size() != 0) {
            final int count = needList.size();
            String[] needArray = needList.toArray(new String[count]);
            if (needShowRationale) {
                getView().showPermission(needArray, requestCode);
            } else if (request) {
                ActivityCompat.requestPermissions(activity, needArray, requestCode);
            }
            return false;
        } else {
            return true;
        }
    }

    public boolean[] onRequestPermissionsResult(CustomCameraActivity recordActivity, int requestCode, String[] permissions, int[] grantResults) {

        boolean result = true;
        boolean isNeverAsk = false;

        int length = grantResults.length;
        for (int i = 0; i < length; i++) {
            String permission = permissions[i];
            int grandResult = grantResults[i];
            if (grandResult == PackageManager.PERMISSION_DENIED) {
                result = false;
                if (!ActivityCompat.shouldShowRequestPermissionRationale(recordActivity, permission))
                    isNeverAsk = true;
            }
        }

        return new boolean[]{result, isNeverAsk};
    }


}
