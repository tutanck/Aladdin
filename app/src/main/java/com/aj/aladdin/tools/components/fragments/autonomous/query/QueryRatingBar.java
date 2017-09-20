package com.aj.aladdin.tools.components.fragments.autonomous.query;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import com.aj.aladdin.R;
import com.aj.aladdin.tools.components.model.AutonomousFindUpsertFragment;
import com.aj.aladdin.tools.components.services.IO;
import com.aj.aladdin.tools.oths.utils.__;
import com.aj.aladdin.tools.regina.Regina;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Ack;


public class QueryRatingBar extends AutonomousFindUpsertFragment {

    private static final String SELECTABLE = "SELECTABLE";


    private RatingBar ratingBar;

    public static QueryRatingBar newInstance(
            String coll
            , String _id
            , String key
            , boolean selectable
    ) {
        Bundle args = new Bundle();
        args.putBoolean(SELECTABLE, selectable);
        QueryRatingBar fragment = new QueryRatingBar();
        fragment.setArguments(args);
        fragment.init(IO.r, coll, true);
        return fragment;
    }


    @Override
    public View onCreateView(
            LayoutInflater inflater
            , ViewGroup container
            , Bundle savedInstanceState
    ) {
        super.onCreateView(inflater,container,savedInstanceState);

        final Bundle args = getArguments();

        ratingBar = (RatingBar) inflater.inflate(R.layout.fragment_rating_bar, container, false);

        ratingBar.setIsIndicator(!args.getBoolean(SELECTABLE));

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
        return null;
    }

    @Override
    protected JSONObject update(Object state) throws JSONException {
        return null;
    }

    @Override
    protected String syncTag() {
        return null;
    }

    @Override
    protected JSONObject saveStateMeta() throws JSONException {
        return null;
    }

    @Override
    protected JSONObject loadStateOpt() throws JSONException {
        return null;
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
                       /* else try {
                            ratingBar.setRating(((JSONArray) args[1]).getJSONObject(0).optInt(getKey(), 0));

                        } catch (JSONException e) {
                            fatalError(e); //SNO or means that DB is inconsistent if there is no profile found getJSONObject(0)
                        }*/
                    }
                });
            }
        };
    }

}