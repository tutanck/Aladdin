package com.aj.aladdin.tools.components.model;

import android.util.Log;

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

public abstract class AutonomousDBFragment extends android.support.v4.app.Fragment {

    private boolean initialized = false;
    boolean synced = false;

    public AutonomousDBFragment self = this;
    protected Regina.Amplitude defaultAmplitude = Regina.Amplitude.IO;

    //DB handler
    private Regina regina;

    //DB location
    private String coll;
    private String _id;
    private String key;

    //DB locations tags
    private String collTag;
    private String docTag;
    private String locationTag;


    //destroy

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (!initialized) return;
        regina.socket.off(locationTag, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.i("@off", locationTag);
            }
        });
    }


    //init

    public void init(
            Regina regina
            , String coll
            , String _id
            , String key
    ) {
        this.regina = regina;
        this.coll = coll;
        this._id = _id;
        this.key = key;

        this.collTag = "#" + coll;
        this.docTag = collTag + "/" + _id;
        this.locationTag = docTag + "/" + key;

        this.initialized = true;
    }


    //save

    protected void saveState(
            Object state
    ) throws InvalidStateException, JSONException, Regina.NullRequiredParameterException {
        if (!initialized) fatalError(self + " : is not yet initialized");
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
                ArrayList<String> argsStr = new ArrayList<>();
                for (Object arg : args)
                    argsStr.add("" + arg); //toString() here could NPE
                Log.i("@saveStateAck", argsStr.toString());
            }
        };
    }


    //load

    protected final void loadState() throws JSONException, Regina.NullRequiredParameterException {
        if (!initialized) fatalError(self + " : is not yet initialized");
        regina.find(coll, id(), loadStateOpt(), loadStateMeta(), loadStateAck());
    }

    protected JSONObject loadStateOpt() throws JSONException {
        return jo(); //todo projection
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

        this.synced = true;

        Log.i("@AutonomousDBFragment:"
                , self.getClass().getSimpleName() + " started following : '" + locationTag + "'");
    }


    //utils

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


    //Data validation

    private Class type; //todo later
    private String rule; //todo later

    protected boolean isStateValid(Object state) {
        return true; //todo later : compile_check state
    }

    protected class InvalidStateException extends Exception {
        protected InvalidStateException(Object state) {
            super(self + " : InvalidStateException : "
                    + state + " doesn't respect this rules : [" + type + "/" + rule + "]");
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

    public Class getType() {
        return type;
    }

    public String getRule() {
        return rule;
    }
}
