package com.barcodereader;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by shivappa.battur on 28/11/2018
 */
public class ZXingScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView scannerView;
    static final int REQUEST_IMAGE_CAPTURE = 1000;

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
            final Intent cameraIntent = new Intent(this, CameraActivity.class);
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Scan Result")
                        .setMessage(result.getText() + " - captured image will be saved by this result")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cameraIntent.putExtra("result", result.getText());
                                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                            }
                        })
                        .create();
                dialog.show();
            }

        } else {
            Toast.makeText(this, "No information found", Toast.LENGTH_SHORT).show();
        }
    }
}
