package com.example.mumo.scara;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mumo.scara.fragments.SelectPicFramgment;

import java.io.File;

public class AskActivity extends AppCompatActivity implements SelectPicFramgment.OnSelectedImageListener {

    private static final String TAG = AskActivity.class.getSimpleName();
    private TextView mTvAddPhoto;
    private ImageView mPhotoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTvAddPhoto = findViewById(R.id.tv_add_photo);
        mTvAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectPicFramgment selectPicFramgment = new SelectPicFramgment();
                selectPicFramgment.show(getSupportFragmentManager(), selectPicFramgment.getTag());
            }
        });

        mPhotoImageView = findViewById(R.id.photo_image_view);


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
    public void onSelectImage(String path) {
        Log.i(TAG, "onSelectImage: " + path);
        Uri photoUri = Uri.fromFile(new File(path));
//        Glide.with(this)
//                .load(photoUri)
//                .into(mPhotoImageView);
        mPhotoImageView.setImageURI(photoUri);

    }
}
