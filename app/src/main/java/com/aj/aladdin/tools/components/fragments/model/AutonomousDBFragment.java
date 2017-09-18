package com.aj.aladdin.tools.components.fragments.model;

import android.app.Fragment;
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

public abstract class AutonomousDBFragment extends Fragment {

    public AutonomousDBFragment self = this;
    protected Regina.Amplitude defaultAmplitude = Regina.Amplitude.IO;

    //DB handler
    private Regina regina;

    //DB location
    private String coll;
    private String _id;
    private String key;
    private String collTag;
    private String docTag;
    private String locationTag;


    public void start(
            Regina regina
            , String coll
            , String _id
            , String key
    ) throws Regina.NullRequiredParameterException, JSONException {
        this.regina = regina;
        this.coll = coll;
        this._id = _id;
        this.key = key;

        this.collTag = "#" + coll;
        this.docTag = collTag + "/" + _id;
        this.locationTag = docTag + "/" + key;

        syncState();
    }


    protected void saveState(
            Object state
    ) throws InvalidStateException, JSONException, Regina.NullRequiredParameterException {
        if (!isStateValid(state))
            throw new InvalidStateException(state);
        regina.update(coll, id(), set(state), saveStateOpt(), saveStateMeta(), saveStateAck());
    }

    protected JSONObject saveStateOpt() {
        return jo();//todo meta see
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


    protected final void loadState() throws JSONException, Regina.NullRequiredParameterException {
        regina.find(coll, id(), loadStateOpt(), loadStateMeta(), loadStateAck());
    }

    protected JSONObject loadStateOpt() {
        return jo(); //todo projection
    }

    protected JSONObject loadStateMeta() {
        return jo();
    }

    protected abstract Ack loadStateAck();


    protected void syncState() throws Regina.NullRequiredParameterException, JSONException {
        loadState();

        regina.socket.on(locationTag, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    if (((JSONObject) args[1]).getInt("op") == 2)
                        self.loadState();
                } catch (JSONException e) {
                    e.printStackTrace(); //todo what? should never occur because the above loadState executed itself first
                } catch (Regina.NullRequiredParameterException e) {
                    throw new RuntimeException(e); //shame on the dev who use null required parameters ... shame on you
                }
            }
        });

        Log.i("@AutonomousDBFragment:"
                , self.getClass().getSimpleName() + " started following : '" + locationTag + "'");
    }


    protected final JSONObject id() throws JSONException {
        return jo().put("_id", _id);
    }

    protected final JSONObject set(Object val) throws JSONException {
        return jo().put(key, jo().put("$set", val));
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
            super(
                    self + " : InvalidStateException : "
                            + state + " doesn't respect this rules : [" + type + "/" + rule + "]"
            );
        }
    }


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
