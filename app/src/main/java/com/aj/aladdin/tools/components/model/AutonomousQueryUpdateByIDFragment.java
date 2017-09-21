package com.aj.aladdin.tools.components.model;

import android.util.Log;

import com.aj.aladdin.tools.regina.Regina;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Ack;
import io.socket.emitter.Emitter;

/**
 * Created by joan on 17/09/2017.
 */

public abstract class AutonomousQueryUpdateByIDFragment extends AutonomousQueryUpdateFragment {

    //DB location
    private String _id;
    private String key;

    //DB paths tags
    private String docTag;
    private String locationTag;


    //init

    public final void init(
            Regina regina
            , String coll
            , String _id
            , String key
            , boolean sync
    ) {
        super.init(regina, coll, sync);

        this._id = _id;
        this.key = key;

        this.docTag = getCollTag() + "/" + _id;
        this.locationTag = docTag + "/" + key;

    }


    //Fragment destruction

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (isSynced())
            getRegina().socket.off(syncTag(), new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.i("@off", locationTag);
                }
            });
    }


    //IO parameters default handlers for save operation

    protected final JSONObject saveStateMeta() throws JSONException {
        final String defaultAmplitudeStr = defaultAmplitude.toString();

        //TODO CLEAN UP
        JSONObject collPath = path(getCollTag(), defaultAmplitude);
        //jo().put("val", getCollTag()).put("kind", defaultAmplitudeStr);
        JSONObject docPath = path(docTag, defaultAmplitude);
        //jo().put("val", docTag).put("kind", defaultAmplitudeStr);
        JSONObject locationPath = path(locationTag, defaultAmplitude);
        //jo().put("val", locationTag).put("kind", defaultAmplitudeStr);

        JSONArray tags = jar().put(collPath).put(docPath).put(locationPath);
        Log.i("@saveStateMeta", tags.toString());
        return jo().put("tags", tags);
    }


    //IO parameters default handlers for load operation

    protected final JSONObject loadStateOpt() throws JSONException {
        return key();
    }


    //concrete

    protected final JSONObject query() throws JSONException {
        return id();
    }

    protected final JSONObject update(Object state) throws JSONException {
        return set(state);
    }

    protected final String syncTag() {
        return locationTag;
    }


    //utils

    protected final JSONObject key() throws JSONException {
        return jo().put(getKey(), 1).put("_id", 0);
    }

    protected final JSONObject id() throws JSONException {
        return jo().put("_id", _id);
    }

    protected final JSONObject set(Object val) throws JSONException {
        return jo().put("$set", jo().put(key, val));
    }


    //accessors

    public final String get_id() {
        return _id;
    }

    public final String getKey() {
        return key;
    }

    public final String getDocTag() {
        return docTag;
    }

    public final String getLocationTag() {
        return locationTag;
    }

}
