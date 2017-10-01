package com.aj.aladdin.tools.components.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aj.aladdin.R;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;


public class ImageFragment extends Fragment {
    public static final int PICK_IMAGE_REQUEST = 1;

    private static final String KEY = "KEY";
    private static final String EDITABLE = "EDITABLE";

    private ImageView imageView;

    public static ImageFragment newInstance(
            String key
            , boolean editable
    ) {
        Bundle args = new Bundle();
        args.putString(KEY, key);
        args.putBoolean(EDITABLE, editable);
        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(
            LayoutInflater inflater
            , ViewGroup container
            , Bundle savedInstanceState
    ) {
        final Bundle args = getArguments();

        imageView = (ImageView) inflater.inflate(R.layout.fragment_image_view, container, false);

        if (args.getBoolean(EDITABLE))
            imageView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                        }
                    }
            );
        return imageView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}