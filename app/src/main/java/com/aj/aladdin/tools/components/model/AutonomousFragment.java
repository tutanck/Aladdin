package com.aj.aladdin.tools.components.model;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.aj.aladdin.tools.regina.Regina;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Ack;
import io.socket.emitter.Emitter;

/**
 * Created by joan on 17/09/2017.
 */

public abstract class AutonomousFragment extends android.support.v4.app.Fragment {

    //DB data synchronization mode
    private boolean sync; //load data once if false, continually sync state if true

    //self ref
    public final AutonomousFragment self = this;

    //DB synchronization state
    protected boolean isSynced = false; //say if the fragment is now isSynced with the database

    //DB actions resounding
    protected Regina.Amplitude defaultAmplitude = Regina.Amplitude.IO;

    //DB Communication state
    private boolean isInitialized = false; //is Fragment ready to talk with DB

    //DB handler
    private Regina regina;

    //DB location
    private String coll;
    private String _id;
    private String key;

    //DB paths tags
    private String collTag;
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
        this.regina = regina;
        this.coll = coll;
        this._id = _id;
        this.key = key;
        this.sync = sync;

        this.collTag = "#" + coll;
        this.docTag = collTag + "/" + _id;
        this.locationTag = docTag + "/" + key;

        this.isInitialized = true;
    }


    //Fragment construction

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            if (sync) syncState();
            else loadState();
        } catch (JSONException e) {
            fatalError(e); //SNO : Should Never Occurs
        } catch (Regina.NullRequiredParameterException e) {
            fatalError(e); //Shame on you who use null required parameters ... shame on you
        }
    }


    //Fragment destruction

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (!isInitialized) return;
        regina.socket.off(locationTag, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.i("@off", locationTag);
            }
        });
    }


    //save

    protected void saveState(
            Object state
    ) throws InvalidStateException, JSONException, Regina.NullRequiredParameterException {
        if (!isInitialized) fatalError(self + " : is not yet isInitialized");
        if (!isStateValid(state)) throw new InvalidStateException(state);
        regina.update(coll, id(), set(state), saveStateOpt(), saveStateMeta(), saveStateAck());
    }

    protected JSONObject saveStateOpt() throws JSONException {
        return jo();
    }

    protected JSONObject saveStateMeta() throws JSONException {
        final String defaultAmplitudeStr = defaultAmplitude.toString();

        JSONObject collPath = jo().put("val", collTag).put("kind", defaultAmplitudeStr);
        JSONObject docPath = jo().put("val", docTag).put("kind", defaultAmplitudeStr);
        JSONObject locationPath = jo().put("val", locationTag).put("kind", defaultAmplitudeStr);

        JSONArray tags = jar().put(collPath).put(docPath).put(locationPath);
        Log.i("@saveStateMeta", tags.toString());
        return jo().put("tags", tags);
    }

    protected Ack saveStateAck() {
        return new Ack() {
            @Override
            public void call(Object... args) {
                logObjectList(args);
            }
        };
    }


    //load

    protected final void loadState() throws JSONException, Regina.NullRequiredParameterException {
        if (!isInitialized) fatalError(self + " : is not yet isInitialized");
        regina.find(coll, id(), loadStateOpt(), loadStateMeta(), loadStateAck());
    }

    protected JSONObject loadStateOpt() throws JSONException {
        return key();
    }

    protected JSONObject loadStateMeta() throws JSONException {
        return jo();
    }

    protected abstract Ack loadStateAck();


    //sync

    protected void syncState() throws Regina.NullRequiredParameterException, JSONException {
        loadState();

        regina.socket.on(locationTag, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    if (((JSONObject) args[1]).getInt("op") == 2)
                        self.loadState();
                } catch (
                        JSONException /*should never occur because the above loadState executed itself first*/
                                | Regina.NullRequiredParameterException /*shame on the dev who use null required parameters ... shame on you*/
                                e
                        ) {
                    fatalError(e);
                }
            }
        });

        this.isSynced = true;

        Log.i("@syncState:"
                , self.getClass().getSimpleName() + " started following : '" + locationTag + "'");
    }


    //utils

    protected final void logObjectList(Object... objects){
        ArrayList<String> strList = new ArrayList<>();
        for (Object obj : objects)
            strList.add("" + obj); //toString() here could NPE
        Log.i("@logObjectList", strList.toString());
    }

    protected final JSONObject key() throws JSONException {
        return jo().put(getKey(),1).put("_id", 0);
    }

    protected final JSONObject id() throws JSONException {
        return jo().put("_id", _id);
    }

    protected final JSONObject set(Object val) throws JSONException {
        return jo().put("$set", jo().put(key, val));
    }

    protected final JSONObject jo() {
        return new JSONObject();
    }

    protected final JSONArray jar() {
        return new JSONArray();
    }


    //validation

    /**
     * isStateValid : Define if the fragment's state is valid before saving in Database.
     * This method must be overriden by its children
     *
     * @param state
     * @return
     */
    protected boolean isStateValid(Object state) {
        return true;
    }

    protected class InvalidStateException extends Exception {

        protected InvalidStateException(String message) {
            super(message);
        }

        protected InvalidStateException(Object state) {
            super(self + " : InvalidStateException : " + state);
        }

        protected InvalidStateException(Object state, String message) {
            super(self + " : InvalidStateException : " + state + "\n message : " + message);
        }
    }


    //fatal

    protected void fatalError(Throwable throwable) {
        throw new RuntimeException(throwable);
    }

    protected void fatalError(String message) {
        throw new RuntimeException(message);
    }


    //accessors

    public Regina getRegina() {
        return regina;
    }

    public String getColl() {
        return coll;
    }

    public String get_id() {
        return _id;
    }

    public String getKey() {
        return key;
    }

    public String getLocationTag() {
        return locationTag;
    }

    public String getCollTag() {
        return collTag;
    }

    public String getDocTag() {
        return docTag;
    }
}
