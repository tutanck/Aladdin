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

public abstract class AutonomousFindUpdateByIDFragment extends AutonomousFindUpdateFragment {

    //DB location
    private String _id;
    private String key;

    //DB paths tags
    private String docTag;
    private String locationTag;


    //init

    public void init(
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
            getRegina().socket.off(locationTag, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.i("@off", locationTag);
                }
            });
    }


    //concrete

    protected JSONObject query() throws JSONException {
        return id();
    }

    protected JSONObject update(Object state) throws JSONException {
        return set(state);
    }

    protected String syncTag() {
        return locationTag;
    }

    //IO parameters default handlers for save operation

    protected JSONObject saveStateMeta() throws JSONException {
        final String defaultAmplitudeStr = defaultAmplitude.toString();

        JSONObject collPath = jo().put("val", getCollTag()).put("kind", defaultAmplitudeStr);
        JSONObject docPath = jo().put("val", docTag).put("kind", defaultAmplitudeStr);
        JSONObject locationPath = jo().put("val", locationTag).put("kind", defaultAmplitudeStr);

        JSONArray tags = jar().put(collPath).put(docPath).put(locationPath);
        Log.i("@saveStateMeta", tags.toString());
        return jo().put("tags", tags);
    }


    //IO parameters default handlers for load operation

    protected JSONObject loadStateOpt() throws JSONException {
        return key();
    }


    //abstract

    protected abstract Ack loadStateAck();


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

    public String get_id() {
        return _id;
    }

    public String getKey() {
        return key;
    }

    public String getDocTag() {
        return docTag;
    }

    public String getLocationTag() {
        return locationTag;
    }

}