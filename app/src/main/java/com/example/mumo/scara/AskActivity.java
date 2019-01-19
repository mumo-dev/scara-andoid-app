package com.example.mumo.scara;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mumo.scara.fragments.SelectPicFramgment;

import java.io.File;

public class AskActivity extends AppCompatActivity implements SelectPicFramgment.OnSelectedImageListener{

    private static final String TAG = AskActivity.class.getSimpleName();
    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 56;
    private static final int PERMISSION_ACCESS_CAMERA = 60;
    private TextView mTvAddPhoto;
    private ImageView mPhotoImageView;
    private ImageView mPhotoRemove;
    private String uploadedImagePath;

    private boolean isCameraPermissionGranted = false;
    private boolean isWritePermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTvAddPhoto = findViewById(R.id.tv_add_photo);
        mTvAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //request camera and external storage write permission before proceeding
                if (ContextCompat.checkSelfPermission(AskActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    // Should we show an explanation?
                   /* if (ActivityCompat.shouldShowRequestPermissionRationale(AskActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {*/
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(AskActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_WRITE_EXTERNAL_STORAGE);
//                    }
                } else {

                    //write external storage permission  already granted
                    //check camera permission
                    if (ContextCompat.checkSelfPermission(AskActivity.this,
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        //camera permission denied
                       /* if(ActivityCompat.shouldShowRequestPermissionRationale(AskActivity.this,
                                Manifest.permission.CAMERA)){
                            // Show an explanation to the user *asynchronously* -- don't block
                            // this thread waiting for the user's response! After the user
                            // sees the explanation, try again to request the permission.
                        }else {*/
                        ActivityCompat.requestPermissions(AskActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                PERMISSION_ACCESS_CAMERA);
//                        }

                    } else {
                        //both permissions have been granted..proceed
                        SelectPicFramgment selectPicFramgment = new SelectPicFramgment();
                        selectPicFramgment.show(getSupportFragmentManager(), selectPicFramgment.getTag());
                    }

                }

            }
        });

        mPhotoImageView = findViewById(R.id.photo_image_view);
        mPhotoRemove = findViewById(R.id.photo_image_remove);
        mPhotoRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhotoImageView.setImageDrawable(null);
                mPhotoImageView.setVisibility(View.GONE);
                mPhotoRemove.setVisibility(View.GONE);
                uploadedImagePath = null;
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isWritePermissionGranted = true;
                } else {
                    Toast.makeText(AskActivity.this,
                            "Enable app read device storage permission", Toast.LENGTH_LONG).show();
                }
                break;
            case PERMISSION_ACCESS_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isCameraPermissionGranted = true;
                } else {
                    Toast.makeText(AskActivity.this,
                            "Enable app read device storage permission", Toast.LENGTH_LONG).show();
                }
                break;

        }

        if (isWritePermissionGranted && isCameraPermissionGranted) {
            SelectPicFramgment selectPicFramgment = new SelectPicFramgment();
            selectPicFramgment.show(getSupportFragmentManager(), selectPicFramgment.getTag());
        }
    }

    @Override
    public void onSelectImage(String path, boolean fromCamera) {
        if(!fromCamera) {
            uploadedImagePath = path;
            bindSelectedImageToView();
        }else {
            setPic(path);
        }

    }

    private void bindSelectedImageToView() {
        mPhotoImageView.setVisibility(View.VISIBLE);
        mPhotoRemove.setVisibility(View.VISIBLE);
        Uri photoUri = Uri.fromFile(new File(uploadedImagePath));
        Glide.with(this)
                .load(photoUri)
                .into(mPhotoImageView);

    }

    private void setPic(String photoPath) {
        mPhotoImageView.setVisibility(View.VISIBLE);
        mPhotoRemove.setVisibility(View.VISIBLE);
        // Get the dimensions of the View
//        int targetW = mPhotoImageView.getWidth();
//        int targetH = mPhotoImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/100, photoH/100);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
        mPhotoImageView.setImageBitmap(bitmap);
    }
}
