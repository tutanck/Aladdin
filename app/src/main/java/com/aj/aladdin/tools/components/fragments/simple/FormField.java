package com.aj.aladdin.tools.components.fragments.simple;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aj.aladdin.R;
import com.aj.aladdin.tools.components.services.ComponentsServices;
import com.aj.aladdin.tools.oths.KeyboardServices;
import com.aj.aladdin.tools.oths.utils.__;


public class FormField extends Fragment {

    private static final String LAYOUT_ID = "LAYOUT_ID";
    private static final String LABEL = "LABEL";

    private boolean isOpen = false;

    private ImageView ivIndication;
    private TextView tvContent;
    private EditText etContent;
    private TextInputLayout textInputLayout;
    private TextView tvDescription;

    //instance parameters

    public static FormField newInstance(
            String label
            , int layoutID
    ) {
        FormField fragment = new FormField();

        Bundle args = new Bundle();
        args.putInt(LAYOUT_ID, layoutID);
        args.putString(LABEL, label);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public View onCreateView(
            LayoutInflater inflater
            , ViewGroup container
            , Bundle savedInstanceState
    ) {
        super.onCreateView(inflater, container, savedInstanceState);

        final Bundle args = getArguments();

        View view = inflater.inflate(args.getInt(LAYOUT_ID), container, false);

        ivIndication = (ImageView) view.findViewById(R.id.ivIndication);

        textInputLayout = (TextInputLayout) view.findViewById(R.id.text_input_layout);
        textInputLayout.setHint(args.getString(LABEL));

        tvDescription = (TextView) view.findViewById(R.id.tvDescription);
        tvDescription.setText(args.getString(LABEL));

        etContent = (EditText) view.findViewById(R.id.etContent);
        etContent.setVisibility(View.GONE);

        tvContent = (TextView) view.findViewById(R.id.tvContent);

        return view;
    }


    public void toggle(){
        if (!isOpen) { //open the selectable view as input
            etContent.setText(tvContent.getText());
            etContent.setVisibility(View.VISIBLE);
            tvContent.setVisibility(View.GONE);
            tvDescription.setVisibility(View.GONE);
            isOpen = true;
        } else {//close the selectable view
            tvContent.setText(etContent.getText());
            etContent.setVisibility(View.GONE);
            tvContent.setVisibility(View.VISIBLE);
            tvDescription.setVisibility(View.VISIBLE);
            isOpen = false;

            KeyboardServices.dismiss(getContext(), etContent);
        }
    }


    public ImageView getIvIndication() {
        return ivIndication;
    }

    public TextView getTvContent() {
        return tvContent;
    }

    public EditText getEtContent() {
        return etContent;
    }

    public TextInputLayout getTextInputLayout() {
        return textInputLayout;
    }

    public TextView getTvDescription() {
        return tvDescription;
    }
}