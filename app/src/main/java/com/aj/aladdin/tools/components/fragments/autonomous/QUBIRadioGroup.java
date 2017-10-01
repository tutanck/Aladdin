package com.aj.aladdin.tools.components.fragments.autonomous;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.aj.aladdin.R;
import com.aj.aladdin.tools.components.model.AutonomousQueryUpdateByIDFragment;
import com.aj.aladdin.tools.oths.db.IO;
import com.aj.aladdin.tools.oths.utils.__;
import com.aj.aladdin.tools.regina.Regina;
import com.aj.aladdin.tools.utils.UIAck;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Ack;


public class QUBIRadioGroup extends AutonomousQueryUpdateByIDFragment {

    private static final String SELECTABLE = "SELECTABLE";
    private static final String LABELS = "LABELS";


    private RadioGroup radioGroup;


    public static QUBIRadioGroup newInstance(
            String coll
            , String _id
            , String key
            , String[] labels
            , boolean selectable
    ) {
        Bundle args = new Bundle();
        args.putStringArray(LABELS, labels);
        args.putBoolean(SELECTABLE, selectable);

        QUBIRadioGroup fragment = new QUBIRadioGroup();
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
        super.onCreateView(inflater, container, savedInstanceState);

        final Bundle args = getArguments();

        radioGroup = (RadioGroup) inflater.inflate(R.layout.fragment_radio_group, container, false);

        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
            radioButton.setText(args.getStringArray(LABELS)[i]);
            radioButton.setEnabled(args.getBoolean(SELECTABLE));
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int radioButtonID = radioGroup.getCheckedRadioButtonId();
                    RadioButton radioButton = radioGroup.findViewById(radioButtonID);
                    int index = radioGroup.indexOfChild(radioButton);
                    try {
                        saveState(index);
                    } catch (InvalidStateException | JSONException | Regina.NullRequiredParameterException e) {
                        __.showLongToast(getContext(), "DebugMode : Une erreur s'est produite" + e);//todo prod mode
                    }
                }
            });
        }

        return radioGroup;
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
        return new UIAck(getActivity()) {
            @Override
            protected void onRes(Object res, JSONObject ctx) {
                try {
                    int selectedIndex = ((JSONArray) res).getJSONObject(0).optInt(getKey(), 0);
                    ((RadioButton) radioGroup.getChildAt(selectedIndex)).setChecked(true);
                } catch (JSONException e) {
                    fatalError(e); //SNO or means that DB is inconsistent if there is no profile found getJSONObject(0)
                }
            }
        };
    }


}