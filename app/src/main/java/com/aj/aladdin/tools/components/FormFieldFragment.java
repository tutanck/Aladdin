package com.aj.aladdin.tools.components;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aj.aladdin.R;

public class FormFieldFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;

    private Context ctx;

    public static FormFieldFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        FormFieldFragment fragment = new FormFieldFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater
            , ViewGroup container
            , Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_form_field, container, false);

        ImageView ivIndication = (ImageView)view.findViewById(R.id.ivIndication);

        TextView tvContent = (TextView) view.findViewById(R.id.tvContent);
        tvContent.setText("Fragment #" + mPage);

        TextView tvDescription = (TextView) view.findViewById(R.id.tvDescription);
        tvDescription.setText("Fragment #" + mPage);

        ImageView ivToogleButton = (ImageView)view.findViewById(R.id.ivToogleButton);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        this.ctx = context;
        super.onAttach(context);
        /*if (context instanceof Listener)
            mListener = (Listener) context;
        else
            throw new RuntimeException(context.toString()
                    + " must implement Listener");*/
    }
}