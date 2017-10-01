package com.aj.aladdin.tools.components.fragments.autonomous;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import com.aj.aladdin.R;
import com.aj.aladdin.tools.components.model.AutonomousFragment;
import com.aj.aladdin.tools.oths.db.IO;
import com.aj.aladdin.tools.oths.utils.__;
import com.aj.aladdin.tools.regina.Regina;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Ack;


public class AutoRatingBar extends AutonomousFragment {

    private static final String TO_ID = "TO_ID";

    private final String toIDKey = "toID";
    private final String ratingKey = "rating";

    private String toIDVal;


    private RatingBar ratingBar;

    public static AutoRatingBar newInstance(
            String coll
            , String toID
    ) {
        Bundle args = new Bundle();
        args.putString(TO_ID, toID);

        AutoRatingBar fragment = new AutoRatingBar();
        fragment.setArguments(args);
        fragment.init(IO.r, coll, false);
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
        toIDVal = args.getString(TO_ID);

        ratingBar = (RatingBar) inflater.inflate(R.layout.fragment_rating_bar, container, false);

        ratingBar.setIsIndicator(true);

        return ratingBar;
    }


    @Override
    protected void loadState() throws JSONException, Regina.NullRequiredParameterException {
        getRegina().socket.emit("getUserRating", __.jo().put("userID", toIDVal), loadStateMeta(), loadStateAck());
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
                                ratingBar.setRating(ratingDoc != null ? ratingDoc.getInt("reputation") : 0);
                            } catch (JSONException e) {
                                fatalError(e); //SNO : if a doc exist the reputation should exist too
                            }
                        }
                    }
                });
            }
        };
    }

    @Override
    protected JSONObject loadStateOpt() throws JSONException {
        return jo();
    }


    @Override
    protected String syncTag() {
        return null;
    }


    @Override
    protected void saveState(Object state) throws InvalidStateException, JSONException, Regina.NullRequiredParameterException {
    }

    @Override
    protected JSONObject saveStateMeta() throws JSONException {
        return null;
    }


}