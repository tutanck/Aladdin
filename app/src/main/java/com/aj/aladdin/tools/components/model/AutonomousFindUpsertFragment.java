package com.aj.aladdin.tools.components.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joan on 17/09/2017.
 */

public abstract class AutonomousFindUpsertFragment extends AutonomousFindUpdateFragment {

    protected JSONObject saveStateOpt() throws JSONException {
        return jo().put("upsert",true);
    }

}