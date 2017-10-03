package com.aj.aladdin.db;

import com.aj.aladdin.db.itf.MongoColl;
import com.aj.aladdin.tools.regina.Regina;
import com.aj.aladdin.tools.regina.ack.__Ack;
import com.aj.aladdin.utils.__;

import org.json.JSONException;

/**
 * Created by joan on 02/10/2017.
 */

public class PROFILES implements MongoColl {

    private final static String coll = "PROFILES";

    public static void setField(String _id, String key, Object val, __Ack ack) {
        try {
            IO.r.update(coll, __.jo().put(_idKey, _id)
                    , __.jo().put("$set", __.jo().put(key, val))
                    , __.jo(), __.jo(), ack);
        } catch (Regina.NullRequiredParameterException | JSONException e) {
            __.fatal(e);
        }

    }


    public static void getProfile(String _id, __Ack ack) {
        try {
            IO.r.find(coll, __.jo().put(_idKey, _id), __.jo(), __.jo(), ack);
        } catch (Regina.NullRequiredParameterException | JSONException e) {
            __.fatal(e);
        }

    }

}
