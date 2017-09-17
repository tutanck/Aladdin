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
    private static final String RANK = "RANK";
    private static final String LABEL = "LABEL";
    private static final String HINT = "HINT";
    private static final String CONTENT = "CONTENT";

    private Listener mListener;


    public static ImageFragment newInstance(
            String label
            , String hint
            , int rank
            , String content
    ) {
        Bundle args = new Bundle();
        args.putInt(RANK, rank);
        args.putString(LABEL, label);
        args.putString(HINT, hint);
        args.putString(CONTENT, content);
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
        View view = inflater.inflate(R.layout.fragment_image, container, false);

        final Bundle args = getArguments();

        final ImageView image = (ImageView) view.findViewById(R.id.image);
        image.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                }
        );
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof Listener)
            mListener = (Listener) context;
        else
            throw new RuntimeException(context.toString()
                    + " must implement Listener");*/
    }


    public interface Listener {
    }
}