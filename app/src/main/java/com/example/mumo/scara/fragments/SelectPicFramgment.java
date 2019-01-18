package com.example.mumo.scara.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mumo.scara.R;

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class SelectPicFramgment extends BottomSheetDialogFragment {

    private static final String TAG = "SelectPicFramgment";
    private TextView mLauchCameraButton;
    private TextView mSelectPicfromGallery;
    public static final int REQUEST_GET_PHOTO = 12;

    OnSelectedImageListener dataPasser;

    public interface OnSelectedImageListener {
        void onSelectImage(String path);
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_pic_dialog, container, false);

        mLauchCameraButton = view.findViewById(R.id.tv_take_picture);
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

        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == REQUEST_GET_PHOTO) {

                    Uri selectedImageUri = data.getData();
                    //get the path from the uri;;
                    final String path = getPathFromUri(selectedImageUri);
                    if (path != null) {
                        File f = new File(path);
                        selectedImageUri = Uri.fromFile(f);
                        //pass the path of the image to Activity
                        dataPasser.onSelectImage(path);

                        dismiss();
                        Log.i(TAG, "file path is " + path);
                    }

                }
            } else {
                Toast.makeText(getContext(), "No Image Selected", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "onActivityResult: ", e);
        }
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


}
