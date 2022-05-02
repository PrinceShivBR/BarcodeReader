package com.barcodereader.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.barcodereader.R;
import com.barcodereader.databinding.ActivityMainBinding;
import com.barcodereader.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding mBinding_;
    private String barcodeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        mBinding_ = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Intent intent = getIntent();
        if (intent != null) {
            barcodeText = intent.getStringExtra("result");
        }
        initViews();
        initListeners();
        mBinding_.getRoot();
    }

    private void initViews() {
        if (!TextUtils.isEmpty(barcodeText))
            mBinding_.etBarcodeText.setText(barcodeText);
    }

    private void initListeners() {
        mBinding_.btnScan.setOnClickListener(this);
        mBinding_.btnCaptureImage.setOnClickListener(this);
        mBinding_.btnSave.setOnClickListener(this);
        mBinding_.btnRefresh.setOnClickListener(this);
        mBinding_.btnExit.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.backup:
                copyDatabase("barcode.db");
                break;
        }
        return false;
    }

    public void copyDatabase(String databaseName) {
        try {
            String currentDBPath = "/data/data/" + getPackageName() + "/databases/" + databaseName;
            File file = new File(currentDBPath);
            if (file.exists()) {
                String rootFolder = Environment.getExternalStorageDirectory() + "/BarcodeReader/DBBackup/";
                FileUtils.getInstance().createDirectory(rootFolder);
                File dbFile = new File(rootFolder + databaseName);
                FileChannel src = new FileInputStream(currentDBPath).getChannel();
                FileChannel dst = new FileOutputStream(dbFile).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_scan:
                startActivity(new Intent(this, ZXingScannerActivity.class));
                finish();
                break;
            case R.id.btn_capture_image: {
                if (!TextUtils.isEmpty(barcodeText)) {
                    Intent cameraIntent = new Intent(this, CameraActivity.class);
                    cameraIntent.putExtra("result", barcodeText);
                    startActivity(cameraIntent);
                } else {
                    Toast.makeText(this, "Please scan the Barcode first", Toast.LENGTH_SHORT).show();
                }

            }
            break;
            case R.id.btn_save:
                break;
            case R.id.btn_refresh:
                finish();
                startActivity(getIntent());
                break;
            case R.id.btn_exit:
                showAlertDialogForExit();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        showAlertDialogForExit();
    }

    private void showAlertDialogForExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Confirm Exit");
        builder.setMessage("Do you really want to exit the app?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
