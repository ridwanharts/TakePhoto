package com.example.takephoto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private Button btnTakePhoto;
    private TextView tvUri;
    private ImageView ivImageResult;
    private String currentPhotoPath;
    private static final int PERMISSION_REQUEST_CODE = 1001;
    private static final int REQUEST_IMAGE_CAPTURE = 1002;
    private String[] listPermission = {
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA
    };
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnTakePhoto = findViewById(R.id.btn_take_photo);
        tvUri = findViewById(R.id.tv_uri);
        ivImageResult = findViewById(R.id.iv_image_result);

        checkPermission();

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                return;
        }
    }

    private void takePicture() {
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        uri  = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", photoFile);
        photo.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(photo,REQUEST_IMAGE_CAPTURE);

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File myDir = new File(Environment.getExternalStorageDirectory() + "/SalesMate");
        myDir.mkdirs();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                myDir      /* directory */
        );

        currentPhotoPath = image.getAbsolutePath();
        tvUri.setText(currentPhotoPath);
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("cek", resultCode + " " + requestCode);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap bitmap;
            File file = new File(currentPhotoPath);
            uri = Uri.fromFile(file);
            Log.e("cek", uri.toString());
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ivImageResult.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            tvUri.setText(uri.toString());
            Glide.with(this).load(currentPhotoPath).into(ivImageResult);
            galleryAddPic();
        }
    }


    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, listPermission, PERMISSION_REQUEST_CODE);

        }
    }

}