package com.example.mumo.scara.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mumo.scara.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class SelectPicFramgment extends BottomSheetDialogFragment {

    private static final String TAG = "SelectPicFramgment";
    private TextView mLauchCameraButton;
    private TextView mSelectPicfromGallery;
    public static final int REQUEST_GET_PHOTO = 15;
    public static final int REQUEST_IMAGE_CAPTURE = 12;

    String mCurrentPhotoPath;


    OnSelectedImageListener dataPasser;


    public interface OnSelectedImageListener {
        void onSelectImage(String path, boolean fromCamera);
    }


    public SelectPicFramgment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSelectedImageListener) {
            dataPasser = (OnSelectedImageListener) context;
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_pic_dialog, container, false);

        mLauchCameraButton = view.findViewById(R.id.tv_take_picture);
        mLauchCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        mSelectPicfromGallery = view.findViewById(R.id.tv_choose_from_gallery);
        mSelectPicfromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GET_PHOTO);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            Toast.makeText(getActivity(), "No image was selected", Toast.LENGTH_LONG).show();
        } else {

            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Log.i(TAG, "file path is " + mCurrentPhotoPath);
                dataPasser.onSelectImage(mCurrentPhotoPath, true);
                dismiss();
            }

            if (REQUEST_GET_PHOTO == requestCode) {
                Uri selectedImageUri = data.getData();
                //get the path from the uri;;
                final String path = getPathFromUri(selectedImageUri);
                if (path != null) {
                    dataPasser.onSelectImage(path, false);
                    dismiss();
                    Log.i(TAG, "file path is " + path);
                }
            }
        }

        //TODO: better permission handling methods
        //TODO: chain multiple request code in this methods
        //TODO: make the image captured by camera displayed..


    }

    private String getPathFromUri(Uri selectedImageUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver()
                .query(selectedImageUri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                res = cursor.getString(column_index);
            }
            cursor.close();
        }

        return res;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            //create the file where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();

            } catch (IOException e) {
                //an error occured while creation the file
            }
            //continue only if the file was sucessfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.example.mumo.scara.fileprovider", photoFile);
                //com.example.mumo.scara.fileprovider
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);


                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }

        }
    }

    private File createImageFile() throws IOException {
        //create an image filename
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName, /*prefix*/
                ".jpg", /*suffix */
                storageDir /*directory*/
        );
        mCurrentPhotoPath = image.getAbsolutePath();
//        galleryAddPic();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }


}
