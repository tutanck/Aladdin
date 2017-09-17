package com.aj.aladdin.tools.components;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aj.aladdin.R;


public class FormFieldFragment extends Fragment {
    private static final String RANK = "RANK";
    private static final String LABEL = "LABEL";
    private static final String HINT = "HINT";
    private static final String CONTENT = "CONTENT";

    private Listener mListener;


    public static FormFieldFragment newInstance(
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
        FormFieldFragment fragment = new FormFieldFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(
            LayoutInflater inflater
            , ViewGroup container
            , Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_form_field, container, false);

        final Bundle args = getArguments();

        final ImageView ivIndication = (ImageView) view.findViewById(R.id.ivIndication);

        final TextView tvContent = (TextView) view.findViewById(R.id.tvContent);
        tvContent.setText(args.getString(CONTENT));
        if(args.getInt(RANK)==0)
            tvContent.setMaxLines(1);

        final TextView tvDescription = (TextView) view.findViewById(R.id.tvDescription);
        tvDescription.setText(args.getString(LABEL));

        ImageView ivToogleButton = (ImageView) view.findViewById(R.id.ivToogleButton);
        ivToogleButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText et = new EditText(getContext());
                        et.setText(tvContent.getText());
                        replaceView(tvContent,et);
                        ivIndication.setVisibility(View.GONE);
                        tvDescription.setVisibility(View.GONE);
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


    private void replaceView(
            View oldView
            ,View newView
    ){
        ViewGroup parent = (ViewGroup) oldView.getParent();
        parent.removeView(oldView);
        parent.addView(newView, parent.indexOfChild(oldView));
    }


    public interface Listener {
    }
}