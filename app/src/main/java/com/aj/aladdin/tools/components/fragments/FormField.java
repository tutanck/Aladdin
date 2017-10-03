package com.aj.aladdin.tools.components.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aj.aladdin.R;
import com.aj.aladdin.tools.components.services.Ic;
import com.aj.aladdin.tools.utils.KeyboardServices;


public class FormField extends Fragment {

    private static final String ID = "ID";
    private static final String LAYOUT_ID = "LAYOUT_ID";
    private static final String KEY = "KEY";
    private static final String LABEL = "LABEL";

    private boolean isOpen = false;

    private RelativeLayout formFieldLayout;
    private ImageView ivIndication;
    private TextView tvContent;
    private EditText etContent;
    private TextInputLayout textInputLayout;
    private TextView tvDescription;
    private View divider;

    private Listener mListener;

    private String key;

    private int id;


    //instance parameters

    public static FormField newInstance(
            int id
            , String label
            , String key
            , int layoutID
    ) {
        FormField fragment = new FormField();

        Bundle args = new Bundle();
        args.putInt(ID, id);
        args.putString(KEY, key);
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

        id = args.getInt(ID);

        key = args.getString(KEY);

        View view = inflater.inflate(args.getInt(LAYOUT_ID), container, false);

        formFieldLayout = (RelativeLayout) view.findViewById(R.id.form_field_layout);

        ivIndication = (ImageView) view.findViewById(R.id.ivIndication);
        ivIndication.setImageResource(Ic.icon(key));

        textInputLayout = (TextInputLayout) view.findViewById(R.id.text_input_layout);
        textInputLayout.setHint(args.getString(LABEL));

        tvDescription = (TextView) view.findViewById(R.id.tvDescription);
        tvDescription.setText(args.getString(LABEL));

        etContent = (EditText) view.findViewById(R.id.etContent);
        etContent.setVisibility(View.GONE);

        tvContent = (TextView) view.findViewById(R.id.tvContent);

        divider = view.findViewById(R.id.divider);
        divider.setVisibility(View.INVISIBLE);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListener.onFormFieldCreated(id, this);
    }

    public void open() {
        if (isOpen) return;
        etContent.setText(tvContent.getText());
        etContent.setVisibility(View.VISIBLE);
        tvContent.setVisibility(View.GONE);
        tvDescription.setVisibility(View.GONE);
        isOpen = true;
    }


    public void close() {
        if (!isOpen) return;
        tvContent.setText(etContent.getText());
        etContent.setVisibility(View.GONE);
        tvContent.setVisibility(View.VISIBLE);
        tvDescription.setVisibility(View.VISIBLE);
        isOpen = false;
        KeyboardServices.dismiss(getContext(), etContent);
    }


    public RelativeLayout getLayout() {
        return formFieldLayout;
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

    public String getKey() {
        return key;
    }

    public boolean isOpen() {
        return isOpen;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Listener)
            mListener = (Listener) context;
        else
            throw new RuntimeException(context.toString()
                    + " must implement FormField.Listener");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface Listener {
        void onFormFieldCreated(int id, FormField formField);
    }
}