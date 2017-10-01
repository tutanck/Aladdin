package com.aj.aladdin.tools.components.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aj.aladdin.R;


public class ImageFragment extends Fragment {

    private static final String KEY = "KEY";
    private static final String EDITABLE = "EDITABLE";

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

        ImageView imageView = (ImageView) inflater.inflate(R.layout.fragment_image_view, container, false);

        if (args.getBoolean(EDITABLE))
            imageView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    }
            );
        return imageView;
    }
}