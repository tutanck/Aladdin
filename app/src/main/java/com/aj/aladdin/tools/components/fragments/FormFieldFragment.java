package com.aj.aladdin.tools.components.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aj.aladdin.R;
import com.aj.aladdin.tools.components.model.AutonomousDBFragment;
import com.aj.aladdin.tools.components.services.ComponentsServices;
import com.aj.aladdin.tools.components.services.IO;
import com.aj.aladdin.tools.oths.KeyboardServices;
import com.aj.aladdin.tools.oths.utils.__;
import com.aj.aladdin.tools.regina.Regina;

import org.json.JSONException;

import io.socket.client.Ack;


public class FormFieldFragment extends AutonomousDBFragment {

    private static final String SELECTABLE = "SELECTABLE";
    private static final String LABEL = "LABEL";
    private static final String INDIC = "INDIC";
    private static final String CONTENT = "CONTENT";

    private boolean openStatus = false;

    private TextView tvContent;
    private EditText etContent;
    private TextView tvDescription;


    //instance parameters

    public static FormFieldFragment newInstance(
            String coll
            , String _id
            , String key
            , String label
            , boolean selectable
            , String content
    ) {
        Bundle args = new Bundle();
        args.putBoolean(SELECTABLE, selectable);
        args.putString(LABEL, label);
        args.putString(CONTENT, content);
        FormFieldFragment fragment = new FormFieldFragment();
        fragment.setArguments(args);
        fragment.init(IO.r, coll, _id, key);
        return fragment;
    }


    @Override
    public View onCreateView(
            LayoutInflater inflater
            , ViewGroup container
            , Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_form_field, container, false);

        tvContent = (TextView) view.findViewById(R.id.tvContent);

        etContent = (EditText) view.findViewById(R.id.etContent);

        tvDescription = (TextView) view.findViewById(R.id.tvDescription);

        final ImageView ivIndication = (ImageView) view.findViewById(R.id.ivIndication);

        final TextInputLayout textInputLayout = (TextInputLayout) view.findViewById(R.id.text_input_layout);


        final Bundle args = getArguments();

        String label = args.getString(LABEL);
        //String indic = args.getString(INDIC);


        tvContent.setText(args.getString(CONTENT));
        tvDescription.setText(label);
        textInputLayout.setHint(label);

        etContent.setVisibility(View.GONE);

        if (args.getBoolean(SELECTABLE))
            ComponentsServices.setSelectable(
                    getContext(), view.findViewById(R.id.form_field_layout), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!openStatus) { //open the selectable view as input
                                etContent.setText(tvContent.getText());
                                etContent.setVisibility(View.VISIBLE);
                                tvContent.setVisibility(View.GONE);
                                tvDescription.setVisibility(View.GONE);
                                openStatus = true;
                                __.showToast(getContext(), "Enregistrez en cliquant sur l'icone à gauche");
                            } else try {
                                saveState(tvContent.getText());
                            } catch (InvalidStateException | JSONException | Regina.NullRequiredParameterException e) {
                                __.showToast(getContext(), "DebugMode : Une erreur s'est produite" + e);//todo prod mod
                            }
                        }
                    });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            loadState();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Regina.NullRequiredParameterException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected Ack saveStateAck() { //close the selectable view
        return new Ack() {
            @Override
            public void call(Object... args) {
                if (args[0] != null)
                    __.showToast(getContext(), "Une erreur s'est produite");
                else
                    getActivity().runOnUiThread(new Runnable() { //mandatory to modify an activity's ui view
                        @Override
                        public void run() {
                            tvContent.setText(etContent.getText());
                            etContent.setVisibility(View.GONE);
                            tvContent.setVisibility(View.VISIBLE);
                            tvDescription.setVisibility(View.VISIBLE);
                            openStatus = false;
                            __.showToast(getContext(), "Mise à jour réussie");
                            KeyboardServices.dismiss(getContext(), etContent);
                        }
                    });
            }
        };
    }

    @Override
    protected Ack loadStateAck() {
        return null; //todo
    }

}