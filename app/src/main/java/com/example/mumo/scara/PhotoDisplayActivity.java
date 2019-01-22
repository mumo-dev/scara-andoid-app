package com.example.mumo.scara;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PhotoDisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_display);

        String imageRef = getIntent().getStringExtra("imageRef");

        ImageView imageView = findViewById(R.id.img_photo);

        final StorageReference storageReference = FirebaseStorage.getInstance()
                .getReference(imageRef);

        GlideApp.with(this)
                .load(storageReference)
                .into(imageView);

        ImageView closeIcon = findViewById(R.id.close_imageview);
        closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
}
