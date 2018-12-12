package com.barcodereader;

/**
 * Created by shivappa.battur on 10/12/2018
 */
public class BarcodeImageModel {
    private String barcodeValue;
    private String imagePath;
    private int imageCount;

    BarcodeImageModel(String barcodeValue, String imagePath, int imageCount) {
        this.barcodeValue = barcodeValue;
        this.imagePath = imagePath;
        this.imageCount = imageCount;
    }

    public String getBarcodeValue() {
        return barcodeValue;
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getImageCount() {
        return imageCount;
    }
}
