package com.aj.aladdin.tools.components.services;

import com.aj.aladdin.tools.regina.Regina;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Ack;

/**
 * Created by joan on 17/09/2017.
 */

public abstract class AutonomousDBComponent {

    protected AutonomousDBComponent ac = this;

    //DB Handle
    protected final Regina regina;

    //DB location
    protected final String coll;

    private /*final*/ String _id = null;

    protected String get_id() {
        return _id;
    }


    //Data validation
    protected Object state;
    protected Class type;
    protected String rule;

    public AutonomousDBComponent(
            Regina regina
            , String coll
            , String _id
    ) {
        this.regina = regina;
        this.coll = coll;
        this._id = _id;
    }

    //initState
    public AutonomousDBComponent(
            Regina regina
            , String coll
            , Ack ack
    ) throws Regina.NullRequiredParameterException {
        this.regina = regina;
        this.coll = coll;
        regina.insert(coll, jo(), jo(), jo(),
                new Ack() {
                    @Override
                    public void call(Object... args) {
                        if (args[0] != null) //no error
                            try {
                                ac._id = ((JSONObject) args[1]).getString("_id");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                    }
                });
    }


    abstract void saveState();

    abstract void loadState();

    abstract void followState();

    abstract void syncState();

    abstract boolean isStateValid();


    protected class InitException extends DBException {
        protected InitException(JSONObject err) {
            super(ac + " : An InitException occurred at '" + regina + "/" + coll + "' due to '" + err + "'");
        }
    }

    protected class DBException extends Exception {
        protected DBException() {
            super(ac + " : An DBException occurred at '" + regina + "/'" + coll);
        }

        protected DBException(JSONObject err) {
            this(
                    ac + " : An DBException occurred at '"
                            + regina + "/'" + coll + " due to " + String.valueOf(err)
            );
        }

        protected DBException(String msg) {
            super(msg);
        }
    }


    private final JSONObject jo() {
        return new JSONObject();
    }

}
