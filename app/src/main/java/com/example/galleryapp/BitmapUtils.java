package com.example.galleryapp;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

public class BitmapUtils {
    public static byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }
}
