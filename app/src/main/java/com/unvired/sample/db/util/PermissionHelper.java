package com.unvired.sample.db.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import java.util.List;

/**
 * Created by nishchith on 21/07/17.
 */
public class PermissionHelper {

    public final static int GENERAL_PERMISSION = 110;

    public static boolean hasPhonePermission(Activity activity) {
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasStoragePermission(Activity activity) {
        return (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                && (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    public static void requestPermissions(Object activity, List<String> list) {
        String[] permissions = new String[list.size()];

        for (int i = 0; i < list.size(); i++) {
            permissions[i] = list.get(i);
        }

        if (activity instanceof Activity)
            ActivityCompat.requestPermissions((Activity) activity, permissions, GENERAL_PERMISSION);
        else
            ((Fragment) activity).requestPermissions(permissions, GENERAL_PERMISSION);
    }
}
