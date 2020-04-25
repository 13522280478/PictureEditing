package com.biotech.drawlessons;

import android.Manifest;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

/**
 * Created by TuXin on 2018/5/8 上午10:33.
 * <p>
 * Email : tuxin@pupupula.com
 */

/**
 * todo:modularization need rewrite permission utils.
 */
public class PermissionUtil {
    public static final int REQUEST_CODE_READ_EXTRA_STORAGE = 500;
    public static final int REQUEST_CODE_CAMERA = 501;
    public static final int REQUEST_CODE_WIFI_LOCATION = 502;

    public static boolean hasExSdCardPermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasExSdCardWritePermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasCameraPermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean shouldShowWifiPermissionRationale(Activity activity) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION) ||
                ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    public static boolean hasWifiLocationPermission(Activity activity) {
        boolean hasCoarseLocation = hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
        boolean hasFineLocation = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        boolean hasOpsCoarseLocation = checkOpsPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
        boolean hasOpsFineLocation = checkOpsPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        return hasCoarseLocation && hasFineLocation && hasOpsCoarseLocation && hasOpsFineLocation;
    }

    public static boolean checkOpsPermission(Context context, String permission) {
        try {
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            if (appOpsManager == null) {
                return true;
            }

            String opsName = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                opsName = AppOpsManager.permissionToOp(permission);
                if (opsName == null) {
                    return true;
                }
                int opsMode = appOpsManager.checkOpNoThrow(opsName, android.os.Process.myPid(), context.getPackageName());
                return opsMode == AppOpsManager.MODE_ALLOWED;
            }
            return true;
        } catch (Exception ex) {
            return true;
        }
    }

    public static void requestWifiLocationPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_WIFI_LOCATION);
    }

    public static void requestWriteSDCardPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WIFI_LOCATION);
    }

    public static void requestBlueScanPermission(Activity activity, int requestCode) {
        ActivityCompat.requestPermissions(
                activity,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                requestCode);
    }

    public static boolean hasBlueScanPermission(Activity activity) {
        boolean hasCoarseLocation = hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        boolean hasFineLocation = hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        boolean hasOpsCoarseLocation = hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
        boolean hasOpsFineLocation = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        return hasCoarseLocation && hasFineLocation && hasOpsCoarseLocation && hasOpsFineLocation;
    }

    private static boolean hasPermission(String permission) {
        return ActivityCompat.checkSelfPermission(BaseApplication.getInstance(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean userhasRefuseExSdCardPermission(Activity activity) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    public static void requestExSdCardPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_EXTRA_STORAGE);
    }

    public static void requestExSdCardWritePermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_READ_EXTRA_STORAGE);
    }

    public static void requestCameraPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
    }

    // todo: dialog to show denied permissions, and use callback to call all grantResult
    public static boolean dealOnRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_READ_EXTRA_STORAGE:
                return verifyPermissions(grantResults);
        }

        return false;
    }

    public static boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if (grantResults == null || grantResults.length < 1) {
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
