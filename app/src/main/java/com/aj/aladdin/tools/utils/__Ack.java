package com.aj.aladdin.tools.utils;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.aj.aladdin.tools.oths.utils.__;

import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Ack;

/**
 * Created by joan on 24/09/2017.
 */

public abstract class __Ack implements Ack {

    protected final AppCompatActivity compatActivity;
    protected boolean isDebugOn = true;


    protected abstract void onRes(Object res, JSONObject ctx);

    protected void onErr(
            JSONObject err,
            JSONObject ctx
    ) {
        __.showShortToast(compatActivity, "Une erreur s'est produite");
    }


    protected void onReginaFail(
    ) {
        __.showShortToast(compatActivity, "Une erreur s'est produite!");//note the exclamation mark : !
    }


    public __Ack(
            AppCompatActivity compatActivity
    ) {
        this.compatActivity = compatActivity;
    }

    public __Ack(
            AppCompatActivity compatActivity
            , boolean isDebugOn
    ) {
        this.compatActivity = compatActivity;
        this.isDebugOn = isDebugOn;
    }


    protected final void logObjectList(Object... objects) {
        ArrayList<String> strList = new ArrayList<>();
        for (Object obj : objects) strList.add("" + obj); //.toString() here could NPE
        Log.i("@logObjectList", strList.toString());
    }

}


