package com.example.comodovid.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public interface PermissionUtils {
    static boolean checkPermission(Context context, String permission) {
        return (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED);
    }

    static void requestPermission(Activity activity, String[] permissions, int PERMISSION_REQUEST_CODE) {
        for (String permission : permissions) {
            if (!checkPermission(activity, permission)) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{permission}, PERMISSION_REQUEST_CODE);

            }
        }
    }

}
