package com.aj.aladdin.tools.utils;

import android.support.v7.app.AppCompatActivity;

import org.json.JSONObject;

/**
 * Created by joan on 24/09/2017.
 */

public abstract class BAck extends __Ack {

    @Override
    public final void call(Object... args) {
        if (isDebugOn)
            logObjectList(args);

        if (args[0] != null && args[2]!=null)
            onErr((JSONObject) args[0], (JSONObject) args[2]);
        else if (args[1] != null && args[2]!=null)
            onRes(args[1], (JSONObject) args[2]);
        else
            onReginaFail();
    }


    public BAck(AppCompatActivity compatActivity) {
        super(compatActivity);
    }

    public BAck(AppCompatActivity compatActivity, boolean isDebugOn) {
        super(compatActivity, isDebugOn);
    }

}


