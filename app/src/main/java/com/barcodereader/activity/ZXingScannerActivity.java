package com.barcodereader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


/**
 * Created by shivappa.battur on 28/11/2018
 */
public class ZXingScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView scannerView;

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
            final Intent cameraIntent = new Intent(this, MainActivity.class);
            cameraIntent.putExtra("result", result.getText());
            startActivity(cameraIntent);
            finish();

        } else {
            Toast.makeText(this, "No information found", Toast.LENGTH_SHORT).show();
        }
    }
}
