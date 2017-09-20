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

public abstract class AutonomousQueryFragment extends android.support.v4.app.Fragment /*support compatibility*/ {

    //self ref
    private final AutonomousQueryFragment self = this;

    //DB Communication state
    private boolean isInitialized = false; //is Fragment ready to talk with DB

    //DB data synchronization mode
    private boolean sync; //load data once if false, continually sync state if true

    //DB synchronization state
    private boolean isSynced = false; //say if the fragment is now isSynced with the database

    //DB handler
    private Regina regina;

    //DB location
    private String coll;

    //DB paths tags
    private String collTag;

    //DB actions resounding
    protected Regina.Amplitude defaultAmplitude = Regina.Amplitude.IO;


    //init

    public final void init(
            Regina regina
            , String coll
            , boolean sync
    ) {
        this.regina = regina;
        this.coll = coll;
        this.sync = sync;

        this.collTag = "#" + coll;

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
        Log.i("@onViewCreated", self + " : sync=" + sync);
    }


    //IO

    //save
    protected final void saveState(
            Object state
    ) throws InvalidStateException, JSONException, Regina.NullRequiredParameterException {
        checkInit();
        checkState(state);
        regina.update(coll, query(), update(state), saveStateOpt(), saveStateMeta(), saveStateAck());
    }

    //load
    protected final void loadState() throws JSONException, Regina.NullRequiredParameterException {
        checkInit();
        regina.find(coll, query(), loadStateOpt(), loadStateMeta(), loadStateAck());
    }

    //sync
    protected final void syncState() throws Regina.NullRequiredParameterException, JSONException {
        loadState();

        regina.socket.on(syncTag(), new Emitter.Listener() {
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
                , self + " started following : '" + syncTag() + "'");
    }


    //IO parameters default handlers for save operation

    protected JSONObject saveStateOpt() throws JSONException {
        return jo();
    }

    protected Ack saveStateAck() {
        return new Ack() {
            @Override
            public void call(Object... args) {
                logObjectList(args);
            }
        };
    }


    //IO parameters default handlers for load operation

    protected JSONObject loadStateMeta() throws JSONException {
        return jo();
    }


    /*Discussion : Why this abstract have not default implementation?
    *
    * query() : obvious : define where to apply changes
    *
    * update(Object state) : obvious : define what change to apply
    *
    * syncState() : it's up to you to (and you should) define what to sync with
    *
    * saveStateMeta() : it's up to you to (and you should) define what others should sync with on data change
    *
    * loadStateOpt() : it's up to you to (and you should) define which specific parts (fields) of the found document represent the fragment's state
    *
    * loadStateAck() : it's up to you (and it's mandatory) to define what to do with the data loaded, like update the fragment's ui
    *
    * */

    //abstract

    protected abstract JSONObject query() throws JSONException;

    protected abstract JSONObject update(Object state) throws JSONException;

    protected abstract String syncTag();

    protected abstract JSONObject saveStateMeta() throws JSONException;

    protected abstract JSONObject loadStateOpt() throws JSONException;

    protected abstract Ack loadStateAck();


    //utils

    protected final void checkInit() {
        if (!isInitialized) fatalError(self + " : is not yet isInitialized");
    }

    protected final void checkState(Object state) throws InvalidStateException {
        if (!isStateValid(state)) throw new InvalidStateException(state);
    }

    protected final void logObjectList(Object... objects) {
        ArrayList<String> strList = new ArrayList<>();
        for (Object obj : objects) strList.add("" + obj); //.toString() here could NPE
        Log.i("@logObjectList", strList.toString());
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

    protected final void fatalError(Throwable throwable) {
        throw new RuntimeException(throwable);
    }

    protected final void fatalError(String message) {
        throw new RuntimeException(message);
    }


    //accessors

    protected final Regina getRegina() {
        return regina;
    }

    public final String getColl() {
        return coll;
    }

    public final String getCollTag() {
        return collTag;
    }

    public final AutonomousQueryFragment getSelf() {
        return self;
    }

    public final boolean getSyncMode() {
        return sync;
    }

    public final boolean isInitialized() {
        return isInitialized;
    }

    public boolean isSynced() {
        return isSynced;
    }
}
