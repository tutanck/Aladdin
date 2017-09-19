package com.aj.aladdin.tools.components.fragments.autonomous;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aj.aladdin.R;
import com.aj.aladdin.tools.components.model.AutonomousFragment;
import com.aj.aladdin.tools.components.services.ComponentsServices;
import com.aj.aladdin.tools.components.services.IO;
import com.aj.aladdin.tools.oths.KeyboardServices;
import com.aj.aladdin.tools.oths.utils.__;
import com.aj.aladdin.tools.regina.Regina;

import org.json.JSONArray;
import org.json.JSONException;

import io.socket.client.Ack;


public class AutoFormField extends AutonomousFragment {

    private static final String SELECTABLE = "SELECTABLE";
    private static final String LABEL = "LABEL";
    private static final String INDIC = "INDIC";
    private static final String CONTENT = "CONTENT";

    private boolean openStatus = false;

    private TextView tvContent;
    private EditText etContent;
    private TextView tvDescription;


    //instance parameters

    public static AutoFormField newInstance(
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
        AutoFormField fragment = new AutoFormField();
        fragment.setArguments(args);
        fragment.init(IO.r, coll, _id, key, true);
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


        tvContent.setText(args.getString(CONTENT));
        tvDescription.setText(args.getString(LABEL));
        textInputLayout.setHint(args.getString(LABEL));

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
                                __.showShortToast(getContext(), "Enregistrez en cliquant sur l'icone à gauche");
                            } else try {
                                saveState(etContent.getText().toString());
                            } catch (InvalidStateException | JSONException | Regina.NullRequiredParameterException e) {
                                __.showLongToast(getContext(), "DebugMode : Une erreur s'est produite" + e);//todo prod mod
                            }
                        }
                    });

        return view;
    }


    @Override
    protected Ack saveStateAck() {
        return new Ack() {
            @Override
            public void call(Object... args) {
                getActivity().runOnUiThread(new Runnable() { //mandatory to modify an activity's ui view
                    @Override
                    public void run() {
                        __.showShortToast(self.getContext(), "Mise à jour réussie");
                    }
                });
                logObjectList(args); //debug // TODO: 19/09/2017 REM
            }
        };
    }

    @Override
    protected Ack loadStateAck() {
        return new Ack() {
            @Override
            public void call(Object... args) {
                if (args[0] != null)
                    __.showLongToast(getContext(), "Une erreur s'est produite");
                else
                    getActivity().runOnUiThread(new Runnable() { //mandatory to modify an activity's ui view
                        @Override
                        public void run() {
                            try {
                                etContent.setVisibility(View.GONE);
                                tvContent.setVisibility(View.VISIBLE);
                                tvDescription.setVisibility(View.VISIBLE);
                                openStatus = false;

                                tvContent.setText(((JSONArray) args[1]).getJSONObject(0).optString(getKey(), ""));

                                KeyboardServices.dismiss(getContext(), etContent);

                            } catch (JSONException e) {
                                fatalError(e); //SNO or means that DB is inconsistent if there is no profile found getJSONObject(0)
                            }
                        }
                    });
            }
        };
    }

}