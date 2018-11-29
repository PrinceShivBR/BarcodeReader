package com.barcodereader;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.barcodereader.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by shivappa.battur on 28/11/2018
 */
public class ZXingScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView scannerView;
    static final int REQUEST_IMAGE_CAPTURE = 1000;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register ourselves as a handler for scan results.
        scannerView.setResultHandler(this);
        // Start camera on resume
        scannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop camera on pause
        scannerView.stopCamera();
    }


    /**
     * method that handles the scanned result
     */
    @Override
    public void handleResult(final com.google.zxing.Result result) {
        if (result != null) {
            final Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Scan Result")
                        .setMessage(result.getText() + " - captured image will be saved by this result")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Create the File where the photo should go
                                File photoFile = null;
                                try {
                                    photoFile = createImageFile(result.getText());
                                } catch (IOException ex) {
                                    // Error occurred while creating the File
                                    Log.i("IMAGE CAPTURE", "IOException");
                                }
                                // Continue only if the File was successfully created
                                if (photoFile != null) {
                                    Uri photoUri;
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        photoUri = FileProvider.getUriForFile(ZXingScannerActivity.this,
                                                "com.barcodereader.fileprovider",
                                                photoFile);
                                    } else {
                                        photoUri = Uri.fromFile(photoFile);
                                    }
                                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                                    startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                                }
                            }
                        })
                        .create();
                dialog.show();
            }

        } else {
            Toast.makeText(this, "No information found", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method to create file for capturing the image from device camera
     *
     * @param text scanned result text
     * @return file
     */
    private File createImageFile(String text) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        String rootFolder = Environment.getExternalStorageDirectory() + "/BarcodeReader/";
        FileUtils.getInstance().createDirectory(rootFolder);
        String imageFolder = rootFolder + "images/";
        FileUtils.getInstance().createDirectory(imageFolder);
        File image = new File(imageFolder + text + "_" + timeStamp + ".jpg");

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }
}
