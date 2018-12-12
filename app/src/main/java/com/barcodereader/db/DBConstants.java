package com.barcodereader.db;

/**
 * Created by shivappa.battur on 10/12/2018
 */
final class DBConstants {
    static class BarCodeData {
        public static String TABLE_NAME = "barcode_data";
        public static String COLUMN_ID = "_id";
        public static String COLUMN_BARCODE_RESULT = "scan_result";
        public static String COLUMN_IMAGE_COUNT = "image_count";
        public static String COLUMN_IMAGE_PATH = "image_path";
        public static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
                + " ( " + COLUMN_ID + " INTEGER PRIMARY KEY ,"
                + COLUMN_IMAGE_COUNT + " INTEGER ,"
                + COLUMN_BARCODE_RESULT + " TEXT ,"
                + COLUMN_IMAGE_PATH + " TEXT);";

        private BarCodeData() {
        }

    }
}
