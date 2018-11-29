package com.barcodereader.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by shivappa.battur on 29/08/2018
 */
public class PermissionUtils {

    /**
     * checks whether the permission is enabled or not
     *
     * @param activity   Activity that requests permission
     * @param permission permission  list that needed to proceed
     */
    public static boolean hasPermission(Activity activity, String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * requests for the permissions
     *
     * @param activity    Activity that requests permission
     * @param permission  permission  list that needed to proceed
     * @param requestCode request code for permission
     */
    public static void requestPermissions(Activity activity, String[] permission, int requestCode) {
        ActivityCompat.requestPermissions(activity, permission, requestCode);
    }

    /**
     * checks whether the mandatory permission is skipped or not.
     * if permission is not enabled then dialog will be going to pop out
     *
     * @param activity   Activity that requests permission
     * @param permission permission that needed to proceed
     */
    public static boolean shouldShowRationale(Activity activity, String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }
}
