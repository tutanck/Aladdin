package com.aj.aladdin.tools.components.fragments;

import android.os.Bundle;
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
import com.aj.aladdin.db.PROFILES;
import com.aj.aladdin.tools.components.services.ComponentsServices;
import com.aj.aladdin.tools.components.services.Ic;
import com.aj.aladdin.tools.regina.ack.UIAck;
import com.aj.aladdin.utils.KeyboardServices;

import org.json.JSONObject;


public class IDKeyFormField extends Fragment {

    private static final String ID = "ID";
    private static final String _ID = "ID";
    private static final String LAYOUT_ID = "LAYOUT_ID";
    private static final String KEY = "KEY";
    private static final String LABEL = "LABEL";
    private static final String EDITABLE = "EDITABLE";

    private boolean isOpen = false;

    private RelativeLayout formFieldLayout;
    private ImageView ivIndication;
    private TextView tvContent;
    private EditText etContent;
    private TextInputLayout textInputLayout;
    private TextView tvDescription;
    private View divider;

    private String key;

    private int id;

    private String _id;

    private boolean editable;


    //instance parameters

    public static IDKeyFormField newInstance(
            int id
            ,String _id
            , String label
            , String key
            , int layoutID
            , boolean editable
    ) {
        IDKeyFormField fragment = new IDKeyFormField();

        Bundle args = new Bundle();
        args.putInt(ID, id);
        args.putBoolean(EDITABLE, editable);
        args.putString(KEY, key);
        args.putString(_ID, _id);
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

        _id= args.getString(_ID);

        editable = args.getBoolean(EDITABLE);

        View view = inflater.inflate(args.getInt(LAYOUT_ID), container, false);

        divider = view.findViewById(R.id.divider);

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

        if (editable)
            ComponentsServices.setSelectable(
                    getActivity(), getLayout(), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!isOpen()) open();
                            else
                                PROFILES.setField(_id, getKey(), getETText(), new UIAck(getActivity()) {
                                    @Override
                                    protected void onRes(Object res, JSONObject ctx) {
                                        close();
                                    }
                                });
                        }
                    }
            );

        return view;
    }


    public void open() {
        if (isOpen) return;
        etContent.setText(tvContent.getText());
        etContent.setVisibility(View.VISIBLE);
        tvContent.setVisibility(View.GONE);
        tvDescription.setVisibility(View.GONE);
        ivIndication.setImageResource(R.drawable.ic_done_24dp);
        divider.setVisibility(View.GONE);
        isOpen = true;
    }


    public void close() {
        if (!isOpen) return;
        tvContent.setText(etContent.getText());
        etContent.setVisibility(View.GONE);
        tvContent.setVisibility(View.VISIBLE);
        tvDescription.setVisibility(View.VISIBLE);
        ivIndication.setImageResource(Ic.icon(key));
        divider.setVisibility(View.VISIBLE);
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

    private String getETText(){
        return getEtContent().getText().toString().trim();
    }
}