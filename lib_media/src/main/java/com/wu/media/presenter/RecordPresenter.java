package com.wu.media.presenter;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;


import com.wkq.base.frame.mosby.MvpBasePresenter;
import com.wu.media.ui.activity.RecordActivity;
import com.wu.media.view.RecordView;

import java.util.ArrayList;
import java.util.List;

/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/9/22 13:33
 * <p>
 * 名 字 : RecordPresenter
 * <p>
 * 简 介 :
 */
public class RecordPresenter extends MvpBasePresenter<RecordView> {

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

    public boolean[] onRequestPermissionsResult(RecordActivity recordActivity, int requestCode, String[] permissions, int[] grantResults) {

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
