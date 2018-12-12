package com.barcodereader.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.barcodereader.BarcodeImageModel;

/**
 * Created by shivappa.battur on 10/12/2018
 */
public class DBHelper {
    private static DBHelper INSTANCE = null;
    private static final String DB_NAME = "barcode.db";
    private static final int DB_VERSION = 1;
    private static DBOpenHelper dbOpenHelper;
    private SQLiteDatabase database;

    private DBHelper() {

    }

    public static DBHelper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DBHelper();
            dbOpenHelper = new DBOpenHelper(context);
        }
        return INSTANCE;
    }

    static class DBOpenHelper extends SQLiteOpenHelper {

        DBOpenHelper(@Nullable Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DBConstants.BarCodeData.CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DBConstants.BarCodeData.TABLE_NAME);
            onCreate(db);
        }
    }

    public void insertImageDataForBarcodeScannedResult(BarcodeImageModel model) {
        database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.BarCodeData.COLUMN_BARCODE_RESULT, model.getBarcodeValue());
        contentValues.put(DBConstants.BarCodeData.COLUMN_IMAGE_COUNT, model.getImageCount());
        contentValues.put(DBConstants.BarCodeData.COLUMN_IMAGE_PATH, model.getImagePath());
        database.insert(DBConstants.BarCodeData.TABLE_NAME, null, contentValues);
    }

    public int getMediaCount(String resultText) {
        int imgCount = 1;
        String selectQuery = "SELECT MAX(" + DBConstants.BarCodeData.COLUMN_IMAGE_COUNT + ") FROM " + DBConstants.BarCodeData.TABLE_NAME + " WHERE "
                + DBConstants.BarCodeData.COLUMN_BARCODE_RESULT + " ='" + resultText + "'";
        Cursor cursor = null;
        database = getReadableDatabase();
        try {
//            cursor = database.query(DBConstants.BarCodeData.TABLE_NAME, new String[]{DBConstants.BarCodeData.COLUMN_IMAGE_COUNT}, null, null, null, null, null);
            cursor = database.rawQuery(selectQuery, null);
            if (cursor != null && cursor.moveToNext())
                imgCount = cursor.getInt(cursor.getColumnIndex(DBConstants.BarCodeData.COLUMN_IMAGE_COUNT));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return imgCount;
    }

    private SQLiteDatabase getReadableDatabase() {
        return dbOpenHelper.getReadableDatabase();
    }

    private SQLiteDatabase getWritableDatabase() {
        return dbOpenHelper.getWritableDatabase();
    }

}
