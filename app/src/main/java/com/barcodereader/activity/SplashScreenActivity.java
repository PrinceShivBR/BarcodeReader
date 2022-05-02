package com.barcodereader.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.barcodereader.R;
import com.barcodereader.utils.PermissionUtils;

/**
 * Created by shivappa.battur on 26/11/2018
 */
public class SplashScreenActivity extends AppCompatActivity {
    private String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA};
    private static final int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermission();
        } else {
            openHome();
        }
    }

    private void openHome() {
        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
        finish();
    }

    private void requestPermission() {
        if (PermissionUtils.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                PermissionUtils.hasPermission(this, Manifest.permission.RECORD_AUDIO) &&
                PermissionUtils.hasPermission(this, Manifest.permission.CAMERA)) {
            openHome();
        } else {
            PermissionUtils.requestPermissions(this, permissions, REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE: {
                if (grantResults.length > 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                    openHome();
                    System.out.println("SplashScreenActivity.onRequestPermissionsResult----------------");
                } else {
                    if (PermissionUtils.shouldShowRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                            PermissionUtils.shouldShowRationale(this, Manifest.permission.RECORD_AUDIO) ||
                            PermissionUtils.shouldShowRationale(this, Manifest.permission.CAMERA)) {
                        showAlertForPermissionRequired();
                    } else {
                        System.out.println("SplashScreenActivity.onRequestPermissionsResult----------------");
                        AlertDialog dialog;
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage("Permissions are needed to launch the application. Go to Settings and enable the permissions");
                        builder.setTitle("Permission Required");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                settingsIntent.setData(uri);
                                startActivity(settingsIntent);
                            }
                        });
                        dialog = builder.create();
                        dialog.show();
                    }
                }
            }
            break;
        }
    }

    private void showAlertForPermissionRequired() {
        System.out.println("SplashScreenActivity.showAlertForPermissionRequired-------------------");
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("To work well the application please enable the requested permissions");
        builder.setTitle("Permission Denied");
        builder.setCancelable(false);
        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestPermission();
            }
        });
        dialog = builder.create();
        dialog.show();
    }
}

