package com.aj.aladdin.tools.components;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aj.aladdin.R;


public class RatingControlFragment extends Fragment {
    private static final String RANK = "RANK";
    private static final String LABEL = "LABEL";
    private static final String HINT = "HINT";
    private static final String CONTENT = "CONTENT";

    private Listener mListener;


    public static RatingControlFragment newInstance(
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
        RatingControlFragment fragment = new RatingControlFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(
            LayoutInflater inflater
            , ViewGroup container
            , Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_rating_control, container, false);

        final Bundle args = getArguments();

        final RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rating_control);
        ratingBar.setOnRatingBarChangeListener(
                new RatingBar.OnRatingBarChangeListener() {
                    public void onRatingChanged(
                            RatingBar ratingBar
                            , float rating
                            , boolean fromUser
                    ) {
                        Toast.makeText(getContext(),"LOL",Toast.LENGTH_LONG).show();
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