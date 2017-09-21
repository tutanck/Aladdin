package com.aj.aladdin.tools.components.model;

import com.aj.aladdin.tools.regina.Regina;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by joan on 17/09/2017.
 */

public abstract class AutonomousQueryUpdateFragment extends AutonomousFragment {


    //IO

    //load
    protected final void loadState() throws JSONException, Regina.NullRequiredParameterException {
        checkInit();
        getRegina().find(getColl(), query(), loadStateOpt(), loadStateMeta(), loadStateAck());
    }

    //save
    protected final void saveState(
            Object state
    ) throws InvalidStateException, JSONException, Regina.NullRequiredParameterException {
        checkInit();
        checkState(state);
        getRegina().update(getColl(), query(), update(state), saveStateOpt(), saveStateMeta(), saveStateAck());
    }



    /*Discussion : Why this abstract have not default implementation?
    *
    * query() : obvious : define where to apply changes
    *
    * update(Object state) : obvious : define what change to apply
    *
    * */

    //abstract

    protected abstract JSONObject query() throws JSONException;

    protected abstract JSONObject update(Object state) throws JSONException;

}
