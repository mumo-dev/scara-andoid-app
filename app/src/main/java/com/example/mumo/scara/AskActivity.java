package com.example.mumo.scara;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mumo.scara.fragments.SelectPicFramgment;
import com.example.mumo.scara.model.Question;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;
import java.util.UUID;

public class AskActivity extends AppCompatActivity implements SelectPicFramgment.OnSelectedImageListener {

    private static final String TAG = AskActivity.class.getSimpleName();
    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 56;
    private static final int PERMISSION_ACCESS_CAMERA = 60;
    private static final int ALL_PERMISSIONS = 50;
    private TextView mTvAddPhoto;
    private ImageView mPhotoImageView;
    private ImageView mPhotoRemove;
    private EditText editTextQuestion;
    private Spinner mCategorySpinner;
    private Button mSubmitButton;

    private String uploadUrl;

    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private CollectionReference questionCollectionRef = mFirestore.collection("questions");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //ask for the necessary permission or quit
        checkPermissions();

        TextView textView = findViewById(R.id.tv_username_label);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String username = user != null ? user.getDisplayName() : "Anonymous";
        textView.setText(username);

        editTextQuestion = findViewById(R.id.et_question);
        mCategorySpinner = findViewById(R.id.sp_category);
        mSubmitButton = findViewById(R.id.btn_submit);
        mTvAddPhoto = findViewById(R.id.tv_add_photo);
        mTvAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //both permissions have been granted..proceed
                SelectPicFramgment selectPicFramgment = new SelectPicFramgment();
                selectPicFramgment.show(getSupportFragmentManager(), selectPicFramgment.getTag());

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
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question = editTextQuestion.getText().toString().trim();
                String category = mCategorySpinner.getSelectedItem().toString();
                if (question.length() <= 0) {
                    Toast.makeText(AskActivity.this, "Question cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                if (category.equals("Choose")) {
                    Toast.makeText(AskActivity.this, "Please choose a category", Toast.LENGTH_LONG).show();
                    return;
                }

                String imageUrl = "";
                //check if any image was uploaded..
                if (mPhotoImageView.getDrawable() != null) {
                    //upload image to server
                    String imageRef = UUID.randomUUID().toString();
                    uploadImageToServer(imageRef);
                    imageUrl = imageRef;
                    /* StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images.jpg");
                        ImageView image = (ImageView)findViewById(R.id.imageView);
                        Glide.with(this).using(new FirebaseImageLoader()).load(storageReference).into(image );
                    */
                }
                //create a Question Object

                Question q = new Question(question, username, category, 0, imageUrl, 0, new Date().getTime());
                uploadDataToFirebase(q);
            }
        });


    }

    private void uploadDataToFirebase(Question q) {
        mSubmitButton.setEnabled(false);
        questionCollectionRef.add(q)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        Toast.makeText(AskActivity.this, "Question Uploaded successfully", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                e.printStackTrace();
                Toast.makeText(AskActivity.this, "uploadTask failed " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void uploadImageToServer(String name) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        final StorageReference ref = storageReference.child("images/" + name + ".jpg");


//        final String urlImage;
        Bitmap bitmap = ((BitmapDrawable) mPhotoImageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = ref.putBytes(data);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {


                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    if (downloadUri != null) {
                        uploadUrl = downloadUri.toString();
//                        Toast.makeText(AskActivity.this, uploadUrl, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(AskActivity.this, "Image upload Failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void checkPermissions() {
        if (!hasStorageWritePermission() && !hasCameraAccessPermission()) {
            //request both permissions
            requestPermission(new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, ALL_PERMISSIONS);
        } else if (!hasCameraAccessPermission()) {
            requestPermission(new String[]{Manifest.permission.CAMERA}, PERMISSION_ACCESS_CAMERA);
        } else if (!hasStorageWritePermission()) {
            requestPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_STORAGE);
        }
    }

    private void requestPermission(String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(this, permissions, requestCode);
    }

    private boolean hasCameraAccessPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasStorageWritePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
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
        if (requestCode == ALL_PERMISSIONS) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //write permission granted
                    //check camera permission
                    if (grantResults.length > 1) {
                        if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                        } else {
                            //camerA permission denied
                            finish();
                        }
                    }
                } else {
                    //write permission denied, exit
                    finish();
                }
            }
        }
        if (requestCode == PERMISSION_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permission granted

            } else {
                //permission denied
                finish();
            }
        }
        if (requestCode == PERMISSION_ACCESS_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permission granted
                checkPermissions();
            } else {
                //permission denied
                finish();
            }
        }

    }

    @Override
    public void onSelectImage(String path, boolean fromCamera) {
        if (!fromCamera) {
            bindSelectedImageToView(path);
        } else {
            setPic(path);
        }

    }

    private void bindSelectedImageToView(String uploadedImagePath) {
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
        int scaleFactor = Math.min(photoW / 100, photoH / 100);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
        mPhotoImageView.setImageBitmap(bitmap);
    }
}
