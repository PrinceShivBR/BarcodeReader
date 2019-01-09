package com.barcodereader.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.barcodereader.R;
import com.barcodereader.model.BarcodeImageModel;
import com.barcodereader.helper.CameraPreview;
import com.barcodereader.helper.OrientationManager;
import com.barcodereader.db.DBHelper;
import com.barcodereader.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by shivappa.battur on 10/12/2018
 */
public class CameraActivity extends AppCompatActivity implements View.OnClickListener, OrientationManager.OrientationListener {
    private static final String TAG = CameraActivity.class.getSimpleName();
    private ImageView mIvSave, mIvCapture, mIvRecapture;
    private Button mBtnClose;
    private RelativeLayout mLayout;
    private Camera mCamera;
    private byte[] cameraData;
    private CameraPreview preview;
    private OrientationManager orientationManager;
    private OrientationManager.ScreenOrientation orientation;
    private String mCurrentPhotoPath;
    int captureAngle;
    private String scannedResult;
    private int imgCount = 1;
    private Camera.PictureCallback takePicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            captureAngle = getRotation();
            cameraData = data;
            releaseCamera();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_camera);
        mIvSave = findViewById(R.id.iv_save);
        mIvSave.setOnClickListener(this);
        mIvCapture = findViewById(R.id.iv_capture);
        mIvCapture.setOnClickListener(this);
        mIvRecapture = findViewById(R.id.iv_retake);
        mIvRecapture.setOnClickListener(this);
        mBtnClose = findViewById(R.id.btn_done);
        mBtnClose.setOnClickListener(this);
        mLayout = findViewById(R.id.rl);

        Intent intent = getIntent();
        scannedResult = intent.getStringExtra("result");
        if (isCameraAvailable()) {
            mCamera = getCameraInstance();
            setFocus();
            preview = new CameraPreview(this, mCamera);
            mLayout.addView(preview);
            orientationManager = new OrientationManager(CameraActivity.this, SensorManager.SENSOR_DELAY_NORMAL, this);
            if (orientationManager.canDetectOrientation()) {
                orientationManager.enable();
            }
        }
    }

    private boolean isCameraAvailable() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private static Camera getCameraInstance() {
        Camera camera;
        try {
            camera = Camera.open();
        } catch (Exception ex) {
            Log.d(TAG, "getCameraInstance: ");
            return null;
        }
        return camera;
    }

    public void setFocus() {
        Camera.Parameters params = mCamera.getParameters();
        params.setJpegQuality(50);
        if (params.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        } else {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }

        mCamera.setParameters(params);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_capture:
                mIvCapture.setEnabled(false);
                MediaPlayer player = MediaPlayer.create(this, R.raw.shutter);
                player.start();
                if (mCamera != null)
                    mCamera.takePicture(null, null, takePicture);
                mIvSave.setEnabled(true);
                mIvRecapture.setEnabled(true);
                break;
            case R.id.iv_retake:
                mIvCapture.setEnabled(true);
                mLayout.removeAllViews();
                if (isCameraAvailable()) {
                    mCamera = getCameraInstance();
                    setFocus();
                    preview = new CameraPreview(CameraActivity.this, mCamera);
                    // screen will stay on for this view
                    preview.setKeepScreenOn(true);
                    mLayout.addView(preview);
                }
                break;
            case R.id.iv_save:
                mIvSave.setEnabled(false);
                new SaveImageAsync().execute();
                break;
            case R.id.btn_done:
                finish();
                break;
        }
    }

    @Override
    public void onOrientationChange(OrientationManager.ScreenOrientation screenOrientation) {
        this.orientation = screenOrientation;
    }

    public int getRotation() {
        // TODO Auto-generated method stub
        if (orientation != null) {
            switch (orientation) {
                case PORTRAIT:
                    return 90;
                case REVERSED_LANDSCAPE:
                    return 180;
                case REVERSED_PORTRAIT:
                    return 270;
                case LANDSCAPE:
                    return 0;
                default:
                    return 0;
            }
        } else {
            return 0;
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }

    }

    class SaveImageAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            storeImageData();
            saveImageDataToDatabase();
            return null;
        }
    }

    private void storeImageData() {
        if (cameraData != null) {
            try {
                File outputFile = createImageFile();

                FileOutputStream outStream;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                Bitmap bitmap;
                try {
                    outStream = new FileOutputStream(outputFile);
                    outStream.write(cameraData);
                    outStream.flush();
                    outStream.close();
                    System.gc();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveImageDataToDatabase() {
        DBHelper dbHelper = DBHelper.getInstance(this);
        dbHelper.insertImageDataForBarcodeScannedResult(new BarcodeImageModel(scannedResult, mCurrentPhotoPath, imgCount));
        imgCount++;
    }

    /**
     * Method to create file for capturing the image from device camera
     *
     * @return file
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        String rootFolder = Environment.getExternalStorageDirectory() + "/BarcodeReader/";
        FileUtils.getInstance().createDirectory(rootFolder);
        String imageFolder = rootFolder + "images/";
        FileUtils.getInstance().createDirectory(imageFolder);
//        imgCount = DBHelper.getInstance(this).getMediaCount(scannedResult);
        File image = new File(imageFolder + scannedResult + "_" + imgCount + "_" + timeStamp + ".jpg");

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
