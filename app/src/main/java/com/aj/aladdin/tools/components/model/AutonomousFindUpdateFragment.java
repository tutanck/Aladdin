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

public abstract class AutonomousFindUpdateFragment extends AutonomousFragment {

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



}
