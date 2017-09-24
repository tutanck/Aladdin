package com.aj.aladdin.tools.components.fragments.autonomous;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import com.aj.aladdin.R;
import com.aj.aladdin.tools.components.model.AutonomousQueryUpdateFragment;
import com.aj.aladdin.tools.oths.db.IO;
import com.aj.aladdin.tools.oths.utils.__;
import com.aj.aladdin.tools.regina.Regina;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Ack;


public class QUBIRatingBar extends AutonomousQueryUpdateFragment {

    private static final String FROM_ID = "FROM_ID";
    private static final String TO_ID = "TO_ID";

    private final String fromIDKey = "fromID";
    private final String toIDKey = "toID";
    private final String ratingKey = "rating";

    private String fromIDVal;
    private String toIDVal;


    private RatingBar ratingBar;

    public static QUBIRatingBar newInstance(
            String coll
            , String fromID
            , String toID
    ) {
        Bundle args = new Bundle();
        args.putString(FROM_ID, fromID);
        args.putString(TO_ID, toID);

        QUBIRatingBar fragment = new QUBIRatingBar();
        fragment.setArguments(args);
        fragment.defaultAmplitude = Regina.Amplitude.EMIT;
        fragment.init(IO.r, coll, true);
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
        fromIDVal = args.getString(FROM_ID);
        toIDVal = args.getString(TO_ID);

        ratingBar = (RatingBar) inflater.inflate(R.layout.fragment_rating_bar, container, false);

        ratingBar.setIsIndicator(false);

        ratingBar.setOnRatingBarChangeListener(
                new RatingBar.OnRatingBarChangeListener() {
                    public void onRatingChanged(
                            RatingBar ratingBar
                            , float rating
                            , boolean fromUser
                    ) {
                        try {
                            if (fromUser) saveState(rating);
                        } catch (InvalidStateException | JSONException | Regina.NullRequiredParameterException e) {
                            __.showLongToast(getContext(), "DebugMode : Une erreur s'est produite" + e);//todo prod mode
                        }
                    }
                }
        );
        return ratingBar;
    }


    @Override
    protected JSONObject query() throws JSONException {
        return jo().put(fromIDKey, fromIDVal).put(toIDKey, toIDVal);
    }

    @Override
    protected JSONObject update(Object state) throws JSONException {
        return jo().put(fromIDKey, fromIDVal).put(toIDKey, toIDVal).put(ratingKey, state);
    }

    @Override
    protected JSONObject saveStateOpt() throws JSONException {
        return jo().put("upsert", true);
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
    protected String syncTag() {
        return getCollTag() + "/" + toIDKey + ":" + toIDVal + "/" + fromIDKey + ":" + fromIDVal;
    }

    @Override
    protected JSONObject saveStateMeta() throws JSONException {
        JSONArray tags = jar().put(path(syncTag(), defaultAmplitude));
        Log.i("@saveStateMeta", tags.toString());
        return jo().put("tags", tags);
    }

    @Override
    protected JSONObject loadStateOpt() throws JSONException {
        return jo().put(ratingKey, 1).put("_id", 0);
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
                        else {
                            JSONObject ratingDoc = ((JSONArray) args[1]).optJSONObject(0);
                            try {
                                ratingBar.setRating(ratingDoc != null ? ratingDoc.getInt(ratingKey) : 0);
                            } catch (JSONException e) {
                                fatalError(e); //SNO : if a doc exist the rating should exist too
                            }
                        }
                    }
                });
            }
        };
    }
}