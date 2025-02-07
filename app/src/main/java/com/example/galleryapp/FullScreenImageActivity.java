package com.example.galleryapp;

import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.FileProvider;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.widget.ImageView;

public class FullScreenImageActivity extends AppCompatActivity {

    private ImageView fullScreenImageView;
    private List<String> imagePaths;
    private int currentPosition;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        fullScreenImageView = findViewById(R.id.fullScreenImageView);
        AppCompatButton deleteButton = findViewById(R.id.deleteButton);
        AppCompatButton shareButton = findViewById(R.id.shareButton);
        AppCompatButton prevButton = findViewById(R.id.prevButton);
        AppCompatButton nextButton = findViewById(R.id.nextButton);
        AppCompatButton setWallpaperButton = findViewById(R.id.setWallpaperButton);
        AppCompatButton infoButton = findViewById(R.id.infoButton); // Yangi tugma

        Intent intent = getIntent();
        imagePaths = intent.getStringArrayListExtra("imagePaths");
        currentPosition = intent.getIntExtra("currentPosition", 0);

        loadImage();

        deleteButton.setOnClickListener(v -> deleteImage());
        shareButton.setOnClickListener(v -> shareImage());
        prevButton.setOnClickListener(v -> moveToPreviousImage());
        nextButton.setOnClickListener(v -> moveToNextImage());
        setWallpaperButton.setOnClickListener(v -> setWallpaper());


        infoButton.setOnClickListener(v -> showImageInfo());

        gestureDetector = new GestureDetector(this, new GestureListener());
        fullScreenImageView.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
    }

    private void loadImage() {
        Picasso.get()
                .load("file://" + imagePaths.get(currentPosition))
                .into(fullScreenImageView);
    }

    private void moveToNextImage() {
        if (currentPosition < imagePaths.size() - 1) {
            currentPosition++;
            loadImage();
        } else {
            Toast.makeText(this, "Bu oxirgi rasm.", Toast.LENGTH_SHORT).show();
        }
    }

    private void moveToPreviousImage() {
        if (currentPosition > 0) {
            currentPosition--;
            loadImage();
        } else {
            Toast.makeText(this, "Bu birinchi rasm.", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteImage() {
        String imagePath = imagePaths.get(currentPosition);
        File file = new File(imagePath);

        Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver resolver = getContentResolver();
        int rowsDeleted = resolver.delete(imageUri, MediaStore.MediaColumns.DATA + "=?", new String[]{file.getAbsolutePath()});

        if (rowsDeleted > 0) {
            Toast.makeText(this, "Rasm oʻchirildi", Toast.LENGTH_SHORT).show();
            imagePaths.remove(currentPosition);

            if (imagePaths.isEmpty()) {
                finish();
            } else {
                currentPosition = Math.min(currentPosition, imagePaths.size() - 1);
                loadImage();
            }
        } else {
            Toast.makeText(this, "Rasmni oʻchirib boʻlmadi", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareImage() {
        File file = new File(imagePaths.get(currentPosition));
        Uri imageUri = FileProvider.getUriForFile(
                this,
                getApplicationContext().getPackageName() + ".provider",
                file
        );

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share Image"));
    }

    private void setWallpaper() {
        String imagePath = imagePaths.get(currentPosition);
        File file = new File(imagePath);

        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

        if (bitmap != null) {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
            try {
                wallpaperManager.setBitmap(bitmap);
                Toast.makeText(this, "Fon rasmi oʻrnatildi!", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Fon rasmi sozlanmadi.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Rasm fayli yaroqsiz.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showImageInfo() {
        String imagePath = imagePaths.get(currentPosition);
        File file = new File(imagePath);

        long fileSize = file.length() / 1024; 
        String fileExtension = imagePath.substring(imagePath.lastIndexOf(".") + 1).toUpperCase();
        String fileLocation = file.getAbsolutePath();

        new AlertDialog.Builder(this)
                .setTitle("Rasm ma'lumotlari")
                .setMessage("Hajmi: " + fileSize + " KB\n" +
                        "Format: " + fileExtension + "\n" +
                        "Manzil: " + fileLocation)
                .setPositiveButton("OK", null)
                .show();
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float deltaX = e2.getX() - e1.getX();
            if (Math.abs(deltaX) > Math.abs(e2.getY() - e1.getY())) {
                if (deltaX > 0) {
                    moveToPreviousImage();
                } else {
                    moveToNextImage();
                }
            }
            return true;
        }
    }
}
