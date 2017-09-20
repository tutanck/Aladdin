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

    private static final String TYPE = "TYPE";
    private static final String SELECTABLE = "SELECTABLE";
    private static final String LABEL = "LABEL";

    private boolean isOpen = false;

    private TextView tvContent;
    private EditText etContent;
    private TextView tvDescription;


    //instance parameters

    public static AutoFormField newInstance(
            String coll
            , String _id
            , String key
            , String label
            , int type
            , boolean selectable
    ) {
        Bundle args = new Bundle();
        args.putInt(TYPE, type);
        args.putBoolean(SELECTABLE, selectable);
        args.putString(LABEL, label);
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
        final Bundle args = getArguments();

        View view;
        switch (args.getInt(TYPE)) {
            case 0:
                view = inflater.inflate(R.layout.fragment_form_field_multiline, container, false);
                break;
            case 1:
                view = inflater.inflate(R.layout.fragment_form_field_oneliner, container, false);
                break;
            default:
                view = inflater.inflate(R.layout.fragment_form_field_multiline, container, false);
                break;
        }


        final ImageView ivIndication = (ImageView) view.findViewById(R.id.ivIndication);


        final TextInputLayout textInputLayout = (TextInputLayout) view.findViewById(R.id.text_input_layout);
        textInputLayout.setHint(args.getString(LABEL));


        tvDescription = (TextView) view.findViewById(R.id.tvDescription);
        tvDescription.setText(args.getString(LABEL));


        etContent = (EditText) view.findViewById(R.id.etContent);
        etContent.setVisibility(View.GONE);


        tvContent = (TextView) view.findViewById(R.id.tvContent);

        if (args.getBoolean(SELECTABLE))
            ComponentsServices.setSelectable(
                    getContext(), view.findViewById(R.id.form_field_layout), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!isOpen) { //open the selectable view as input
                                etContent.setText(tvContent.getText());
                                etContent.setVisibility(View.VISIBLE);
                                tvContent.setVisibility(View.GONE);
                                tvDescription.setVisibility(View.GONE);
                                isOpen = true;
                                __.showShortToast(getContext(), "Enregistrez en cliquant sur l'icone à gauche");
                            } else try {
                                saveState(etContent.getText().toString());
                            } catch (InvalidStateException | JSONException | Regina.NullRequiredParameterException e) {
                                __.showLongToast(getContext(), "DebugMode : Une erreur s'est produite" + e);//todo prod mode
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
                        __.showShortToast(getContext(),
                                args[0] != null ? "Une erreur s'est produite" : "Mise à jour réussie");
                    }
                });
                logObjectList(args); //debug
            }
        };
    }

    @Override
    protected Ack loadStateAck() {
        return new Ack() {
            @Override
            public void call(Object... args) {
                getActivity().runOnUiThread(new Runnable() { //mandatory to modify an activity's ui view
                    @Override
                    public void run() {
                        if (args[0] != null)
                            __.showLongToast(getContext(), "Une erreur s'est produite");
                        else try {//close the selectable view
                            etContent.setVisibility(View.GONE);
                            tvContent.setVisibility(View.VISIBLE);
                            tvDescription.setVisibility(View.VISIBLE);
                            isOpen = false;

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