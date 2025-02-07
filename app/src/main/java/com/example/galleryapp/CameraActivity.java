package com.example.galleryapp;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CameraActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 102;
    private static final int VIDEO_CAPTURE_REQUEST_CODE = 200;

    private Uri imageUri;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        AppCompatButton captureImageButton = findViewById(R.id.captureImageButton);
        AppCompatButton captureVideoButton = findViewById(R.id.captureVideoButton);



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, STORAGE_PERMISSION_CODE);
        }


        captureImageButton.setOnClickListener(v -> openCamera());


        captureVideoButton.setOnClickListener(v -> openVideoCamera());


    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


        File photoFile = createImageFile();
        if (photoFile != null) {
            imageUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        }
    }

    private File createImageFile() {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            File image = File.createTempFile(imageFileName, ".jpg", storageDir);
            currentPhotoPath = image.getAbsolutePath();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void openVideoCamera() {
        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(videoIntent, VIDEO_CAPTURE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {

            Toast.makeText(this, "Rasmga saqlandi", Toast.LENGTH_SHORT).show();
            galleryAddPic();
        } else if (requestCode == VIDEO_CAPTURE_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri videoUri = data.getData();

            Toast.makeText(this, "Video saqlandi: " + videoUri, Toast.LENGTH_SHORT).show();
        }
    }

    private void galleryAddPic() {

        File f = new File(currentPhotoPath);
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);

        if (bitmap != null) {

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, f.getName());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            try {
                if (uri != null) {
                    getContentResolver().openOutputStream(uri).write(BitmapUtils.bitmapToBytes(bitmap));
                    Toast.makeText(this, "Rasm galereyaga saqlandi.", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Rasm saqlanmadi.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Rasm yuklanmadi.", Toast.LENGTH_SHORT).show();
        }
    }




    private void setWallpaper() {
        if (imageUri != null) {

            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            if (bitmap != null) {
                try {

                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
                    wallpaperManager.setBitmap(bitmap);
                    Toast.makeText(this, "Wallpaper set successfully!", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to set wallpaper.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Invalid image file.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
