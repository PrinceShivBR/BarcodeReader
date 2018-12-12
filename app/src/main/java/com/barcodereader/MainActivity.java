package com.barcodereader;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.barcodereader.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ZXingScannerActivity.class));
            }
        });
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

        }
    }
}
